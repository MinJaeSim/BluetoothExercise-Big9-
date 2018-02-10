package cc.foxtail.funkey.exercise;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import cc.foxtail.funkey.R;
import cc.foxtail.funkey.data.Child;
import cc.foxtail.funkey.data.ChildPhysicalData;
import cc.foxtail.funkey.data.ExerciseTime;
import cc.foxtail.funkey.deviceConnect.DeviceConnectActivity;
import cc.foxtail.funkey.navigation.NavigationActivity;

import static android.app.Activity.RESULT_OK;
import static cc.foxtail.funkey.navigation.NavigationActivity.deviceAddress;

public class ExerciseMeasureFragment extends Fragment {

    private static final int MEASURE_MODE_REQUEST_CODE = 800;
    private static final int DEVICE_CONNECT_REQUEST_CODE = 444;
    private static final int MEASURE_MODEL = 2;
    private ChildPhysicalData childPhysicalData;

    private FirebaseFirestore firebaseFirestore;
    private DocumentSnapshot childData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_ability, container, false);

        deviceAddress = getArguments().getString("DEVICE_ADDRESS");
        childData = ((NavigationActivity) getActivity()).getCurrentChildData();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (deviceAddress == null) {
            System.out.println("address is null");
            Intent i = new Intent(getContext(), DeviceConnectActivity.class);
            startActivityForResult(i, DEVICE_CONNECT_REQUEST_CODE);
        }

        ConstraintLayout ability1 = view.findViewById(R.id.ability1);
        TextView ability1TextView = ability1.findViewById(R.id.stage_text_view);
        ability1TextView.setText(getResources().getString(R.string.lower_body_strength));

        ability1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUnityActivity(4, "strengthDown", 6500);
            }
        });

        ConstraintLayout ability2 = view.findViewById(R.id.ability2);
        TextView ability2TextView = ability2.findViewById(R.id.stage_text_view);
        ability2TextView.setText(getResources().getString(R.string.upper_body_strength));

        ability2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUnityActivity(1, "strengthUp", 5000);
            }
        });

        ConstraintLayout ability3 = view.findViewById(R.id.ability3);
        TextView ability3TextView = ability3.findViewById(R.id.stage_text_view);
        ability3TextView.setText(getResources().getString(R.string.muscle_endurance));

        ability3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUnityActivity(9, "strengthEndurance", 14000);
            }
        });

        ConstraintLayout ability4 = view.findViewById(R.id.ability4);
        TextView ability4TextView = ability4.findViewById(R.id.stage_text_view);
        ability4TextView.setText(getResources().getString(R.string.core));

        ability4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUnityActivity(28, "core", 20000);
            }
        });

        ConstraintLayout ability5 = view.findViewById(R.id.ability5);
        TextView ability5TextView = ability5.findViewById(R.id.stage_text_view);
        ability5TextView.setText(getResources().getString(R.string.cardiopulmonary_endurance));

        ability5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUnityActivity(17, "lungCapacity", 11000);
            }
        });

        Button measureSkipButton = view.findViewById(R.id.measure_skip_button);
        measureSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                DocumentSnapshot documentSnapshot = ((NavigationActivity) getActivity()).getChildPhysicalDocumentSnapShot();

                boolean update = false;

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    update = true;
                    childPhysicalData = documentSnapshot.toObject(ChildPhysicalData.class);
                    childPhysicalData.checkTestFinish();

                    if (childPhysicalData.isMeasureFinish()) { //여기서부터
                        update = false;
                        createDummyData();
                    }
                } else {
                    createDummyData();
                }

                if (update) {
                    firebaseFirestore.collection("Child_Physical_Data").document(documentSnapshot.getId())
                            .set(childPhysicalData);
                } else
                    firebaseFirestore.collection("Child_Physical_Data").add(childPhysicalData);

                FragmentManager fragmentManager = getFragmentManager();
                Fragment exerciseChoiceFragment = new ExerciseChoiceFragment();
                fragmentManager.beginTransaction().replace(R.id.content_fragment, exerciseChoiceFragment).commit();
            }
        });


        return view;
    }

    private void startUnityActivity(int stage, String measureType, int time) {
        Intent intent = new Intent(getContext(), UnityActivity.class);
        intent.putExtra("DEVICE_ADDRESS", deviceAddress);
        intent.putExtra("EXERCISE_MODEL_TYPE", MEASURE_MODEL);
        intent.putExtra("STAGE", stage);
        intent.putExtra("TIME", time);
        intent.putExtra("MEASURE_TYPE", measureType);
        String gender = ((NavigationActivity) getActivity()).getCurrentChild().getSex();
        intent.putExtra("GENDER", gender);
        startActivityForResult(intent, MEASURE_MODE_REQUEST_CODE);  //여기수정
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

        if (requestCode == MEASURE_MODE_REQUEST_CODE) {
            DocumentSnapshot documentSnapshot = ((NavigationActivity) getActivity()).getChildPhysicalDocumentSnapShot();

            boolean update = false;

            if (documentSnapshot != null && documentSnapshot.exists()) {//데이터가 존재한다면 기존 데이터를 가져온다
                update = true;
                childPhysicalData = documentSnapshot.toObject(ChildPhysicalData.class);

                //데이터가 존재하는데 측정이 끝나있다면 이것은 업데이트가 아니라 재측정이다 라고 판단
                if (childPhysicalData.isMeasureFinish()) { //여기서부터
                    update = false;
                    childPhysicalData = new ChildPhysicalData();
                    String name = ((NavigationActivity) getActivity()).getCurrentChild().getUserName();
                    String uid = ((NavigationActivity) getActivity()).getCurrentChild().getUid();
                    childPhysicalData.setUserName(name);
                    childPhysicalData.setUid(uid); //여기까지 2주에한번 측정하려고 추가된 코드
                }
            } else {
                childPhysicalData = new ChildPhysicalData();
                String name = ((NavigationActivity) getActivity()).getCurrentChild().getUserName();
                String uid = ((NavigationActivity) getActivity()).getCurrentChild().getUid();
                childPhysicalData.setUserName(name);
                childPhysicalData.setUid(uid);
            }

            if (resultCode == RESULT_OK) {
                int score = data.getIntExtra("SCORE", 0);
                final int star = data.getIntExtra("STAR", 0);
                final long time = data.getLongExtra("TIME", 0);

                String measureType = data.getStringExtra("MEASURE_TYPE");
                switch (measureType) {
                    case "core":
                        childPhysicalData.setCore(score);
                        break;
                    case "strengthDown":
                        childPhysicalData.setStrengthDown(score);
                        break;
                    case "strengthUp":
                        childPhysicalData.setStrengthUp(score);
                        break;
                    case "lungCapacity":
                        childPhysicalData.setLungCapacity(score);
                        break;
                    case "strengthEndurance":
                        childPhysicalData.setStrengthEndurance(score);
                        break;

                }

                childPhysicalData.checkTestFinish();

                firebaseFirestore = FirebaseFirestore.getInstance();
                childPhysicalData.setDate(System.currentTimeMillis());

                if (update) {
                    firebaseFirestore.collection("Child_Physical_Data").document(documentSnapshot.getId())
                            .set(childPhysicalData);
                } else
                    firebaseFirestore.collection("Child_Physical_Data").add(childPhysicalData);

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd", Locale.KOREA);
                final String date = simpleDateFormat.format(calendar.getTime());
                final String key = childPhysicalData.getUserName() + date + childPhysicalData.getUid();

                firebaseFirestore.collection("Exercise_Time").document(key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ExerciseTime exerciseTime = new ExerciseTime(childPhysicalData.getUid(), childPhysicalData.getUserName(), date, time, star);
                        if (documentSnapshot.exists()) {
                            exerciseTime.setTime(documentSnapshot.toObject(ExerciseTime.class).getTime() + time);
                            exerciseTime.setStar(documentSnapshot.toObject(ExerciseTime.class).getStar() + star);
                        }

                        firebaseFirestore.collection("Exercise_Time").document(key).set(exerciseTime);
                    }
                });

                Child child = ((NavigationActivity) getActivity()).getCurrentChild();
                child.setTotalStarCount(child.getTotalStarCount() + star);

                firebaseFirestore.collection("Children").document(childData.getId()).set(child);
            }
        }
    }

    @Override
    public void onResume() {
        ((NavigationActivity) getActivity()).showToolbarTextView("운동하기");
        super.onResume();
    }

    private void createDummyData() {
        childPhysicalData = new ChildPhysicalData();
        String name = ((NavigationActivity) getActivity()).getCurrentChild().getUserName();
        String uid = ((NavigationActivity) getActivity()).getCurrentChild().getUid();
        childPhysicalData.setUserName(name);
        childPhysicalData.setUid(uid);
        childPhysicalData.setCore(10);
        childPhysicalData.setStrengthDown(10);
        childPhysicalData.setStrengthUp(10);
        childPhysicalData.setLungCapacity(10);
        childPhysicalData.setStrengthEndurance(10);
        childPhysicalData.checkTestFinish();
        childPhysicalData.setDate(System.currentTimeMillis());
    }
}
