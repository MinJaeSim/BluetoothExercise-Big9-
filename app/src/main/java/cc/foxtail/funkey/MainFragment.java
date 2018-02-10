package cc.foxtail.funkey;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cc.foxtail.funkey.data.CalendarItem;
import cc.foxtail.funkey.exercise.ExerciseChoiceFragment;
import cc.foxtail.funkey.exercise.ExerciseMeasureFragment;
import cc.foxtail.funkey.navigation.NavigationActivity;

import static cc.foxtail.funkey.navigation.NavigationActivity.soundId;
import static cc.foxtail.funkey.navigation.NavigationActivity.soundPool;

public class MainFragment extends Fragment {

    private String address;
    private CalendarAdapter calendarAdapter;
    private TextView dialogTextView;
    private Button exerciseStartButton;
    private Fragment exerciseFragment;
    private String userName;
    private boolean measureMode;
    private List<CalendarItem> calendarItemList;

    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        calendarItemList = new ArrayList<>();

        exerciseStartButton = view.findViewById(R.id.main_exercise_start_button);
        RecyclerView calendarRecyclerView = view.findViewById(R.id.calendar_recycler_view);
        dialogTextView = view.findViewById(R.id.welcome_text_view);
        dialogTextView.setMovementMethod(new ScrollingMovementMethod());


        changeExerciseStartButtonClickListenerForUser();

        if (measureMode)
            changeMeasureModeInterface();
        else
            changePlayModeInterface();


        if (userName == null) {
            String text = "안녕하세요!\n앱을 사용하기 위해\n먼저 메뉴를 열고\n어린이를 등록해주세요!";
            setDialogText(text);
            changeExerciseStartButtonClickListenerForGuest();
        } else {
            changeExerciseStartButtonClickListenerForUser();
            Resources res = getResources();
            String text = String.format(res.getString(R.string.welcome_messages), userName);
            setDialogText(text);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        calendarRecyclerView.setLayoutManager(linearLayoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendarAdapter = new CalendarAdapter();
    }

    @Override
    public void onResume() {
        System.out.println("Main Resume");
        ((NavigationActivity) getActivity()).showToolbarSpinner();
        ((NavigationActivity) getActivity()).setNavigationDrawerListener();
        ((NavigationActivity) getActivity()).checkMeasureMode();

        super.onResume();
    }

    public void setDialogText(String text) {
        dialogTextView.setText(text);
    }

    public void setUserName(String userName) {
        this.userName = userName;
        Resources res = getResources();
        String text = String.format(res.getString(R.string.welcome_messages), userName);
        setDialogText(text);
    }

    public void setExerciseMeasureMode() {
        measureMode = true;
        changeMeasureModeInterface();
    }

    public void setExercisePlayMode() {
        measureMode = false;
        changePlayModeInterface();
    }

    public void changeExerciseStartButtonClickListenerForGuest() {
        exerciseStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(soundId, 1, 1, 0, 0, 1);
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.basic_dialog);

                TextView text = dialog.findViewById(R.id.basic_dialog_text_view);
                text.setText("메뉴를 열고 어린이를 등록해 주세요!");

                Button dialogButton = dialog.findViewById(R.id.basic_dialog_ok_button);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        soundPool.play(soundId, 1, 1, 0, 0, 1);
                        dialog.dismiss();
                        ((NavigationActivity) getActivity()).openDrawer();
                    }
                });

                dialog.show();
            }
        });
    }

    public void changeExerciseStartButtonClickListenerForUser() {
        final FragmentManager fragmentManager = getFragmentManager();
        exerciseStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(soundId, 1, 1, 0, 0, 1);

                address = ((NavigationActivity) getActivity()).getDeviceAddress();
                System.out.println("ADDRESS " + address);

                Bundle args = new Bundle();
                if (address != null) {
                    args.putString("DEVICE_ADDRESS", address);
                }
                exerciseFragment.setArguments(args);

                fragmentManager.beginTransaction().replace(R.id.content_fragment, exerciseFragment).addToBackStack(null).commit();

                ((NavigationActivity) getActivity()).setNavigationBackListener();
            }
        });
    }

    private void changeMeasureModeInterface() {
        exerciseStartButton.setText(getResources().getString(R.string.exercise_measure_start));
        exerciseFragment = new ExerciseMeasureFragment();
    }

    private void changePlayModeInterface() {
        exerciseStartButton.setText(getResources().getString(R.string.exercise_start));
        exerciseFragment = new ExerciseChoiceFragment();
    }


    public void readCalendarData() {
        calendarItemList.clear();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("정보를 불러오는 중..");

        if (!progressDialog.isShowing())
            progressDialog.show();

        final int starCount = ((NavigationActivity) getActivity()).getCurrentChild().getTotalStarCount();
        System.out.println("TEST : " + ((NavigationActivity) getActivity()).getCurrentChild().getUserName());
        Query firstQuery = FirebaseFirestore.getInstance().collection("Exercise_Time").whereEqualTo("uid", ((NavigationActivity) getActivity()).getCurrentChild().getUid())
                .whereEqualTo("userName", ((NavigationActivity) getActivity()).getCurrentChild().getUserName());

        Task<QuerySnapshot> firstTask = firstQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.isEmpty())
                    return;

                for (DocumentSnapshot d : documentSnapshots) {
                    CalendarItem calendarItem = d.toObject(CalendarItem.class);
                    calendarItemList.add(calendarItem);
                }
            }
        });


        Task<Void> t = Tasks.whenAll(firstTask);
        t.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (calendarItemList.size() == 0) {
                    calendarItemList.add(new CalendarItem("171218", 0, "User"));
                    calendarItemList.add(new CalendarItem("171219", 0, "User"));
                    calendarItemList.add(new CalendarItem("171220", 0, "User"));
                    calendarItemList.add(new CalendarItem("171221", 0, "User"));
                }
                String profileImageUrl = ((NavigationActivity) getActivity()).getCurrentChild().getProfileImageUrl();
                calendarAdapter.setProfileImageUrl(profileImageUrl);
                calendarAdapter.setCalendarItemList(calendarItemList);
                calendarAdapter.notifyDataSetChanged();

                if (((NavigationActivity) getActivity()).getCurrentChildPhysicalData() != null) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd", Locale.KOREA);
                    final String date = simpleDateFormat.format(System.currentTimeMillis() - ((NavigationActivity) getActivity()).getCurrentChildPhysicalData().getDate());
                    final String restDate = String.valueOf(14 - Integer.parseInt(date));

                    dialogTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            String text = String.format(getString(R.string.welcome_messages), userName);
                            text += String.format(getResources().getString(R.string.welcome_description), starCount, restDate);
                            dialogTextView.setText(text);
                        }
                    });
                }

                progressDialog.dismiss();
            }
        });

        t.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("CAL FAIL : " + e.getMessage());
                progressDialog.dismiss();
            }
        });


    }
}
