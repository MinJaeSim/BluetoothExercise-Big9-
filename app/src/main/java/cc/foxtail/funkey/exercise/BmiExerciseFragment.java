package cc.foxtail.funkey.exercise;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cc.foxtail.funkey.R;
import cc.foxtail.funkey.StageButtonAdapter;
import cc.foxtail.funkey.data.Child;
import cc.foxtail.funkey.data.ExerciseTime;
import cc.foxtail.funkey.data.JsonBmiObject;
import cc.foxtail.funkey.data.Stage;
import cc.foxtail.funkey.navigation.NavigationActivity;

import static android.app.Activity.RESULT_OK;
import static cc.foxtail.funkey.navigation.NavigationActivity.deviceAddress;

public class BmiExerciseFragment extends Fragment {

    private static final int BMI_MODEL = 3;
    private static final int PLAY_MODE_REQUEST_CODE = 700;
    private static final int OPEN_STAGE = 10;

    private StageButtonAdapter stageButtonAdapter;
    private float bmi;
    private String[] ageInfo;
    private Child child;
    private DocumentSnapshot currentChildData;
    private boolean allClear;
//    private int openStage = 5;
    private FirebaseFirestore firebaseFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();

        List<Stage> stageList = new ArrayList<>();
        for (int i = 0; i < OPEN_STAGE; i++) {
            String stageName = String.format(Locale.KOREA, "%dstep", i + 1);
            Stage stage = new Stage(stageName, (i + 1) + 30, 5000);

            stageList.add(stage);
        }
        currentChildData = ((NavigationActivity) getActivity()).getCurrentChildData();
        if (currentChildData != null)
            child = currentChildData.toObject(Child.class);

        List<Integer> scoreList = child.getStageScore();

        allClear = true;
        readList(scoreList);

        if (allClear) {
            System.out.println("All Clear");
            for (int i = 31; i <= 40; i++)
                scoreList.set(i, 0);

            child.setStageScore(scoreList);

            firebaseFirestore.collection("Children").document(currentChildData.getId()).set(child).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    stageButtonAdapter.notifyDataSetChanged();
                }
            });
        }


        float weight = child.getWeight();
        float height = child.getHeight();

        bmi = (float) (weight / Math.pow((height / 100), 2));
        bmi = (float) (Math.round((bmi * 10)) / 10.0);

        ageInfo = child.getAge().split("세");
        System.out.println("BMI : " + bmi);
        System.out.println("나이 : " + ageInfo[0]);

        RecyclerView recyclerView = view.findViewById(R.id.stage_button_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        stageButtonAdapter = new StageButtonAdapter(stageList, scoreList.subList(30, 41), new StageButtonClickListener() {
            @Override
            public void onClick(Stage stage) {
                startBmiUnityActivity(bmi, ageInfo[0], stage.getStageNumber(), stage.getStageName());
            }
        });

        recyclerView.setAdapter(stageButtonAdapter);


        return view;
    }

    private void readList(List<Integer> score) {
        for (int i = 31; i <= 30 + OPEN_STAGE; i++) {
            if (score.get(i) == 0) {
                allClear = false;
                System.out.println("Not All clear");
                break;
            }
        }
    }

    private String getBmiStatus(float bmi) {
        if (bmi > 35) {
            return "bmi_extremely_obese";
        } else if (bmi > 30) {
            return "bmi_obese";
        } else if (bmi > 25) {
            return "bmi_over_weight";
        } else if (bmi > 18.5) {
            return "bmi_normal";
        } else {
            return "bmi_under_weight";
        }
    }

    private void startBmiUnityActivity(float bmi, String age, int stageNumber, String bmiStageName) {
        Intent intent = new Intent(getContext(), UnityActivity.class);
        intent.putExtra("DEVICE_ADDRESS", deviceAddress);
        intent.putExtra("EXERCISE_MODEL_TYPE", BMI_MODEL);
        intent.putExtra("BMI_TYPE", getBmiStatus(bmi));
        intent.putExtra("STAGE", stageNumber);
        intent.putExtra("BMI_STAGE", bmiStageName);
        intent.putExtra("CHILD_AGE", age);
        String gender = ((NavigationActivity) getActivity()).getCurrentChild().getSex();
        intent.putExtra("GENDER", gender);
        startActivityForResult(intent, PLAY_MODE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLAY_MODE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                int stage = data.getIntExtra("STAGE", 0);
                final int star = data.getIntExtra("STAR", 0);
                final long time = data.getLongExtra("TIME", 0);

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");
                final String date = simpleDateFormat.format(calendar.getTime());

                final String key = child.getUserName() + date + child.getUid();
                firebaseFirestore.collection("Exercise_Time").document(key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ExerciseTime exerciseTime = new ExerciseTime(child.getUid(), child.getUserName(), date, time, star);
                        if (documentSnapshot.exists()) {
                            exerciseTime.setTime(documentSnapshot.toObject(ExerciseTime.class).getTime() + time);
                            exerciseTime.setStar(documentSnapshot.toObject(ExerciseTime.class).getStar() + star);
                        }

                        firebaseFirestore.collection("Exercise_Time").document(key).set(exerciseTime);
                    }
                });

                List<Integer> list = child.getStageScore();

                for (Integer integer : list)
                    System.out.println(integer);

                list.set(stage, star);
                child.setTotalStarCount(child.getTotalStarCount() + star);
                child.setStageScore(list);

                for (Integer integer : list)
                    System.out.println(integer);

                if (allClear) {
                    System.out.println("All Clear");
                    list = new ArrayList<>();
                    for (int i = 31; i <= 40; i++)
                        list.set(i, 0);

                    child.setStageScore(list);
                }

                DocumentSnapshot currentChildData = ((NavigationActivity) getActivity()).getCurrentChildData();

                firebaseFirestore.collection("Children").document(currentChildData.getId()).set(child).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        stageButtonAdapter.setScoreList(child.getStageScore().subList(30,40));
                        stageButtonAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }
}
