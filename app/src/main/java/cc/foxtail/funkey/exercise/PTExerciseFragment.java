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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cc.foxtail.funkey.R;
import cc.foxtail.funkey.StageButtonAdapter;
import cc.foxtail.funkey.data.Child;
import cc.foxtail.funkey.data.ChildPhysicalData;
import cc.foxtail.funkey.data.ExerciseTime;
import cc.foxtail.funkey.data.Stage;
import cc.foxtail.funkey.navigation.NavigationActivity;

import static android.app.Activity.RESULT_OK;
import static cc.foxtail.funkey.navigation.NavigationActivity.deviceAddress;

public class PTExerciseFragment extends Fragment {
    private static final int PT_MODEL = 4;
    private static final int PLAY_MODE_REQUEST_CODE = 700;
    private static final int OPEN_STAGE = 5;

    private StageButtonAdapter stageButtonAdapter;
    private String[] ageInfo;

    private Child child;
    private DocumentSnapshot currentChildData;
    private FirebaseFirestore firebaseFirestore;
    private boolean allClear;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();

        List<Stage> stageList = new ArrayList<>();
        for (int i = 0; i < OPEN_STAGE; i++) {
            String stageName = String.format(Locale.KOREA, "%dstep", i + 1);
            Stage stage = new Stage(stageName, (i + 1) + 40, 5000);

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
            for (int i = 41; i <= 50; i++)
                scoreList.set(i, 0);

            child.setStageScore(scoreList);

            firebaseFirestore.collection("Children").document(currentChildData.getId()).set(child).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    stageButtonAdapter.notifyDataSetChanged();
                }
            });
        }

        ChildPhysicalData childPhysicalData = ((NavigationActivity) getActivity()).getCurrentChildPhysicalData();

        ageInfo = child.getAge().split("세");

        HashMap<String, Integer> scoreHashMap = new HashMap<>();
        scoreHashMap.put("StrengthUp", childPhysicalData.getStrengthUp());
        scoreHashMap.put("StrengthDown", childPhysicalData.getStrengthDown());
        scoreHashMap.put("Core", childPhysicalData.getCore());
        scoreHashMap.put("LungCapacity", childPhysicalData.getLungCapacity());
        scoreHashMap.put("StrengthEndurance", childPhysicalData.getStrengthEndurance());

        System.out.println("StrengthUp"+ childPhysicalData.getStrengthUp());
        System.out.println("StrengthDown"+ childPhysicalData.getStrengthDown());
        System.out.println("Core"+ childPhysicalData.getCore());
        System.out.println("LungCapacity"+ childPhysicalData.getLungCapacity());
        System.out.println("StrengthEndurance"+ childPhysicalData.getStrengthEndurance());

        Iterator<String> it = sortByValue(scoreHashMap).iterator();

        final String exerciseName1 = it.next();
        System.out.println(exerciseName1 + " = " + scoreHashMap.get(exerciseName1));


        final String exerciseName2 = it.next();
        System.out.println(exerciseName2 + " = " + scoreHashMap.get(exerciseName2));

        RecyclerView recyclerView = view.findViewById(R.id.stage_button_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        stageButtonAdapter = new StageButtonAdapter(stageList, scoreList.subList(40, 51), new StageButtonClickListener() {
            @Override
            public void onClick(Stage stage) {
                startPTUnityActivity(getPTFileName(exerciseName1, exerciseName2), ageInfo[0],stage.getStageNumber(), stage.getStageName());
            }
        });

        recyclerView.setAdapter(stageButtonAdapter);


        return view;
    }

    private void readList(List<Integer> score) {
        for (int i = 40; i <= 40 + OPEN_STAGE; i++) {
            if (score.get(i) == 0) {
                allClear = false;
                System.out.println("Not All clear");
                break;
            }
        }
    }


    public String getPTFileName(String exerciseName1, String exerciseName2) {
        if ((Objects.equals(exerciseName1, "StrengthUp") && Objects.equals(exerciseName2, "StrengthDown"))
                || (Objects.equals(exerciseName1, "StrengthDown") && Objects.equals(exerciseName2, "StrengthUp")))
            return "PT_up_down.json";

        else if ((Objects.equals(exerciseName1, "StrengthUp") && Objects.equals(exerciseName2, "Core"))
                || (Objects.equals(exerciseName1, "Core") && Objects.equals(exerciseName2, "StrengthUp")))
            return "PT_up_core.json";

        else if ((Objects.equals(exerciseName1, "StrengthUp") && Objects.equals(exerciseName2, "LungCapacity"))
                || (Objects.equals(exerciseName1, "LungCapacity") && Objects.equals(exerciseName2, "StrengthUp")))
            return "PT_up_lung.json";

        else if ((Objects.equals(exerciseName1, "StrengthUp") && Objects.equals(exerciseName2, "StrengthEndurance"))
                || (Objects.equals(exerciseName1, "StrengthEndurance") && Objects.equals(exerciseName2, "StrengthUp")))
            return "PT_up_endurance.json";

        else if ((Objects.equals(exerciseName1, "StrengthDown") && Objects.equals(exerciseName2, "Core"))
                || (Objects.equals(exerciseName1, "Core") && Objects.equals(exerciseName2, "StrengthDown")))
            return "PT_core_down.json";

        else if ((Objects.equals(exerciseName1, "StrengthDown") && Objects.equals(exerciseName2, "LungCapacity"))
                || (Objects.equals(exerciseName1, "LungCapacity") && Objects.equals(exerciseName2, "StrengthDown")))
            return "PT_down_lung.json";

        else if ((Objects.equals(exerciseName1, "StrengthDown") && Objects.equals(exerciseName2, "StrengthEndurance"))
                || (Objects.equals(exerciseName1, "StrengthEndurance") && Objects.equals(exerciseName2, "StrengthDown")))
            return "PT_down_endurance.json";

        else if ((Objects.equals(exerciseName1, "Core") && Objects.equals(exerciseName2, "LungCapacity"))
                || (Objects.equals(exerciseName1, "LungCapacity") && Objects.equals(exerciseName2, "Core")))
            return "PT_core_lung.json";

        else if ((Objects.equals(exerciseName1, "Core") && Objects.equals(exerciseName2, "StrengthEndurance"))
                || (Objects.equals(exerciseName1, "StrengthEndurance") && Objects.equals(exerciseName2, "Core")))
            return "PT_core_endurance.json";

        else if ((Objects.equals(exerciseName1, "LungCapacity") && Objects.equals(exerciseName2, "StrengthEndurance"))
                || (Objects.equals(exerciseName1, "StrengthEndurance") && Objects.equals(exerciseName2, "LungCapacity")))
            return "PT_lung_endurance.json";

        else return "";

    }


    public List<String> sortByValue(final HashMap<String, Integer> map) {
        List<String> list = new ArrayList<>();
        list.addAll(map.keySet());
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int v1 = map.get(o1);
                int v2 = map.get(o2);
                if (v1 > v2)
                    return 1;
                else if (v1 < v2)
                    return -1;
                else return 0;

            }
        });
        return list;
    }

    private void startPTUnityActivity(String PT_Type, String age, int stageNumber, String stageName) {
        System.out.println(PT_Type);
        Intent intent = new Intent(getContext(), UnityActivity.class);
        intent.putExtra("DEVICE_ADDRESS", deviceAddress);
        intent.putExtra("EXERCISE_MODEL_TYPE", PT_MODEL);
        intent.putExtra("PT_TYPE", PT_Type);
        intent.putExtra("STAGE", stageNumber);
        intent.putExtra("PT_STAGE", stageName);
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
                final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
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
                    for (int i = 41; i <= 50; i++)
                        list.set(i, 0);

                    child.setStageScore(list);
                }

                DocumentSnapshot currentChildData = ((NavigationActivity) getActivity()).getCurrentChildData();

                firebaseFirestore.collection("Children").document(currentChildData.getId()).set(child).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        stageButtonAdapter.setScoreList(child.getStageScore().subList(40, 50));
                        stageButtonAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

}
