package cc.foxtail.funkey.alarm;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import cc.foxtail.funkey.navigation.NavigationActivity;
import cc.foxtail.funkey.R;
import cc.foxtail.funkey.data.Child;

public class AlarmFragment extends Fragment implements AlarmContract.View, AlarmDialogFragment.OnAlarmDialogEventListener {

    private static final int REQUEST_NOTIFICATION_POLICY = 88;
    private AlarmPresenter alarmPresenter;
    private ProgressDialog progressDialog;
    private AlarmViewPageAdapter alarmViewPageAdapter;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        setHasOptionsMenu(true);

        alarmPresenter = new AlarmPresenter(getContext());
        alarmPresenter.setView(this);

        progressDialog = new ProgressDialog(getContext());
        showProgressDialog("잠시만 기다려 주세요");

        String uid = FirebaseAuth.getInstance().getUid();

        viewPager = view.findViewById(R.id.alarm_view_pager);

        Query query = FirebaseFirestore.getInstance().collection("Children").whereEqualTo("uid", uid);
        Task<QuerySnapshot> task = query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                List<DocumentSnapshot> data = documentSnapshots.getDocuments();
                List<Child> childList = new ArrayList<>();

                for (DocumentSnapshot d : data)
                    childList.add(d.toObject(Child.class));

                System.out.println(childList.size());

                alarmViewPageAdapter = new AlarmViewPageAdapter(getChildFragmentManager(),childList);
                alarmViewPageAdapter.setAlarmPresenter(alarmPresenter);
                viewPager.setAdapter(alarmViewPageAdapter);
                dismissProgressDialog();
            }
        });
        Tasks.whenAll(task);

        TabLayout tabLayout = view.findViewById(R.id.tab_dots);
        tabLayout.setupWithViewPager(viewPager);



        FloatingActionButton floatingActionButton = view.findViewById(R.id.add_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditAlarmDialog(null, "");
            }
        });

        checkPermission();


        return view;
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_NOTIFICATION_POLICY)) {
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, REQUEST_NOTIFICATION_POLICY);
                }
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_bluetooth).setVisible(false);
//        menu.findItem(R.id.menu_rank).setVisible(false);
        menu.findItem(R.id.menu_search).setVisible(false);
    }

    @Override
    public void onResume() {
        ((NavigationActivity) getActivity()).showToolbarTextView(getResources().getString(R.string.alarm_setting));
        ((NavigationActivity) getActivity()).setNavigationBackListener();
        super.onResume();
    }

    @Override
    public void showEditAlarmDialog(@Nullable Alarm alarm, final String documentKey) {
        AlarmDialogFragment dialogFragment = new AlarmDialogFragment();
        if (alarm != null)
            dialogFragment.setAlarm(alarm);
        dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        dialogFragment.setOnAlarmDialogEventListener(this);
        dialogFragment.show(getFragmentManager(), "alarm setting");
    }

    @Override
    public void showRemoveAlarmDialog(final String documentKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("알람을 삭제 하시겠습니까");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alarmPresenter.removeAlarm(documentKey);
                        alarmViewPageAdapter.notifyDataSetChanged();
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    @Override
    public void onConfirm(Alarm alarm) {
        alarmPresenter.addAlarm(alarm);
        alarmViewPageAdapter.notifyDataSetChanged();
    }

    public void showProgressDialog(String text) {
        progressDialog.setMessage(text);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }

}