package cc.foxtail.funkey.exercise;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cc.foxtail.funkey.R;
import cc.foxtail.funkey.StageButtonAdapter;
import cc.foxtail.funkey.data.Child;
import cc.foxtail.funkey.data.ChildPhysicalData;
import cc.foxtail.funkey.data.ExerciseTime;
import cc.foxtail.funkey.data.Stage;
import cc.foxtail.funkey.deviceConnect.DeviceConnectActivity;
import cc.foxtail.funkey.navigation.NavigationActivity;

import static android.app.Activity.RESULT_OK;
import static cc.foxtail.funkey.navigation.NavigationActivity.deviceAddress;

public class ExerciseFragment extends Fragment {

    private static final int DEVICE_CONNECT_REQUEST_CODE = 444;
    private static final int PLAY_MODE_REQUEST_CODE = 700;
    private static final int BASIC_MODEL = 1;
    private StageButtonAdapter stageButtonAdapter;
    private FirebaseFirestore firebaseFirestore;

    private DocumentSnapshot currentChildData;
    private Child child;

    private String childName;
    private String childUid;
    private ArrayList<Integer> childStageScoreList;

    private boolean allClear;
    //    private int index;
    private List<Stage> stageList;

    private final int[] stageAnimationPlayTime = {
            5000, 9000, 14000, 6500, 4500, 9000, 9000, 8000, 14000, 10000, 8500, 8500, 8800, 8500, 9500, 5500, 11000, 15000, 18000, 18000, 7500, 8500, 19000, 12000, 11000, 9000, 9000
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("NAME", child.getUserName());
        outState.putString("UID", child.getUid());
        outState.putIntegerArrayList("SCORE", childStageScoreList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            childName = savedInstanceState.getString("NAME");
            childUid = savedInstanceState.getString("UID");
            childStageScoreList = savedInstanceState.getIntegerArrayList("SCORE");
        }

        final View view = inflater.inflate(R.layout.fragment_exercise, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (deviceAddress == null) {
            System.out.println("address is null");
            Intent i = new Intent(getContext(), DeviceConnectActivity.class);
            startActivityForResult(i, DEVICE_CONNECT_REQUEST_CODE);
        }

        stageList = new ArrayList<>();
        for (int i = 0; i < 27; i++) {
            String stageName = String.format(Locale.KOREA, "Stage #%02d", i + 1);
            Stage stage = new Stage(stageName, i + 1, stageAnimationPlayTime[i]);

            stageList.add(stage);
        }

        currentChildData = ((NavigationActivity) getActivity()).getCurrentChildData();
        if (currentChildData != null)
            child = currentChildData.toObject(Child.class);
        List<Integer> scoreList = child.getStageScore();

        allClear = true;
//        index = 27;

        readList(scoreList);

        if (allClear) {
            System.out.println("All Clear");
            scoreList = new ArrayList<>();
            for (int i = 0; i < 30; i++)
                scoreList.add(0);

            child.setStageScore(scoreList);

            firebaseFirestore.collection("Children").document(currentChildData.getId()).set(child).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    stageButtonAdapter.notifyDataSetChanged();
                }
            });
        }


        RecyclerView recyclerView = view.findViewById(R.id.stage_button_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        stageButtonAdapter = new StageButtonAdapter(stageList, scoreList, new StageButtonClickListener() {
            @Override
            public void onClick(Stage stage) {
                startUnityActivity(stage);
            }
        });

        recyclerView.setAdapter(stageButtonAdapter);


        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Score")
                .whereEqualTo("uid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                    }
                });

        return view;
    }

    @Override
    public void onResume() {
        ((NavigationActivity) getActivity()).showToolbarTextView(getResources().getString(R.string.basic_course));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DEVICE_CONNECT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                deviceAddress = data.getStringExtra("ADDRESS");
            } else {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack();
            }
        }

        if (requestCode == PLAY_MODE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                final int stage = data.getIntExtra("STAGE", 0);
                final int star = data.getIntExtra("STAR", 0);
                final long time = data.getLongExtra("TIME", 0);

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");
                final String date = simpleDateFormat.format(calendar.getTime());

                Query query = firebaseFirestore.collection("Child_Physical_Data")
                        .whereEqualTo("uid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .whereEqualTo("userName", childName);
                Task<QuerySnapshot> task = query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        for (DocumentSnapshot document : documentSnapshots) {
                            Child c = document.toObject(Child.class);
                            if (Objects.equals(c.getUserName(), childName))
                                child = c;
                        }
                    }
                });

                Task<Void> t = Tasks.whenAll(task);
                t.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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
                        list.set(stage, star);

                        child.setTotalStarCount(child.getTotalStarCount() + star);
                        child.setStageScore(list);

                        readList(list);

                        if (allClear) {
                            System.out.println("All Clear");
                            list = new ArrayList<>();
                            for (int i = 0; i < 30; i++)
                                list.set(i,0);

                            child.setStageScore(list);
                        }

                        firebaseFirestore.collection("Children").document(currentChildData.getId()).set(child).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                stageButtonAdapter.setScoreList(child.getStageScore());
//                        stageButtonAdapter.setStageList(stageList.subList(0, index));
                                stageButtonAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        }
    }

    private void readList(List<Integer> score) {
        for (int i = 1; i <= 27; i++) {
            if (score.get(i) == 0) {
                allClear = false;
                System.out.println("Not All clear");
//                index = i + 2 > 27 ? 27 : i + 2;
                break;
            }
        }
    }


    private void startUnityActivity(Stage stage) {
        Intent intent = new Intent(getContext(), UnityActivity.class);
        intent.putExtra("DEVICE_ADDRESS", deviceAddress);
        intent.putExtra("EXERCISE_MODEL_TYPE", BASIC_MODEL);
        intent.putExtra("STAGE", stage.getStageNumber());
        intent.putExtra("TIME", stage.getStagePlayTime());
        String gender = ((NavigationActivity) getActivity()).getCurrentChild().getSex();
        intent.putExtra("GENDER", gender);
        startActivityForResult(intent, PLAY_MODE_REQUEST_CODE);
    }


}
