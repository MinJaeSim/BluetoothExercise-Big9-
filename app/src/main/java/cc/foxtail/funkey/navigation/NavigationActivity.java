package cc.foxtail.funkey.navigation;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import cc.foxtail.funkey.LoginActivity;
import cc.foxtail.funkey.MainFragment;
import cc.foxtail.funkey.OnChildChangeListener;
import cc.foxtail.funkey.R;
import cc.foxtail.funkey.alarm.AlarmFragment;
import cc.foxtail.funkey.childAdd.ChildManagementFragment;
import cc.foxtail.funkey.childInfo.ChildInfoFragment;
import cc.foxtail.funkey.data.Child;
import cc.foxtail.funkey.data.ChildPhysicalData;
import cc.foxtail.funkey.deviceConnect.DeviceConnectActivity;
import cc.foxtail.funkey.friend.FriendViewFragment;
import cc.foxtail.funkey.record.RecordFragment;
import de.hdodenhof.circleimageview.CircleImageView;


public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_ENABLE_BLUETOOTH = 100;
    private static final int REQUEST_LOCATION_ACCESS = 101;
    private static final int DEVICE_CONNECT_REQUEST_CODE = 444;

    private static final String TAG = "NAVIGATION_ACTIVITY";

    public static int soundId;
    public static SoundPool soundPool;
    private MediaPlayer mediaPlayer;
    private boolean measureFinish;

    private FragmentManager fragmentManager;
    private Spinner spinner;
    private NavigationView navigationView;
    private ArrayList<String> nameList;
    private ArrayList<Child> myChildrenList;
    private TextView toolbarTextView;
    private ActionBarDrawerToggle toggle;
    private int width;
    private OnChildChangeListener childDataView;
    private static Child currentChild;
    private List<DocumentSnapshot> childDatas;
    private List<DocumentSnapshot> childPhysicalDataList;
    private DocumentSnapshot currentChildPhysicalData;
    private static DocumentSnapshot currentChildData;
    public static String deviceAddress;
    private int currentPosition;

    private FirebaseFirestore firebaseFirestore;

    private View.OnClickListener navigationDrawerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            soundPool.play(soundId, 1, 1, 0, 0, 1);
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
        }
    };

    private View.OnClickListener navigationBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            soundPool.play(soundId, 1, 1, 0, 0, 1);
            fragmentManager.popBackStack();
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("ONRESUME");
        mediaPlayer.start();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mediaPlayer = MediaPlayer.create(this, R.raw.bgm_app_main);
        mediaPlayer.setVolume(0.6f, 0.6f);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();

        soundId = soundPool.load(getApplicationContext(), R.raw.button_click_sound, 1); // in 2nd param u have to pass your desire ringtone

        firebaseFirestore = FirebaseFirestore.getInstance();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        System.out.println("width : " + width);


        myChildrenList = new ArrayList<>();
        childDatas = new ArrayList<>();
        childPhysicalDataList = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbarTextView = toolbar.findViewById(R.id.toolbar_text_view);
        toolbarTextView.setVisibility(View.GONE);

        spinner = toolbar.findViewById(R.id.name_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                soundPool.play(soundId, 1, 1, 0, 0, 1);
                if (myChildrenList.size() > 0)
                    currentChild = myChildrenList.get(position);
                if (childDatas.size() > 0)
                    currentChildData = childDatas.get(position);
                currentPosition = position;
                System.out.println("TEST OnItemSelected");
                setUserData(currentChild);
                if (childDataView != null)
                    childDataView.onChange(currentChild);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.ic_menu);

        toggle.setToolbarNavigationClickListener(navigationDrawerListener);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.content_fragment, new MainFragment(), "MAIN_FRAGMENT").commit();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Button signOutButton = navigationView.getHeaderView(0).findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(NavigationActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
            }
        });

        loadUserChildren();


    }

    private void setUserData(Child child) {
        setHeaderInfo(child);
        loadCurrentChildPhysicalData();

        ((MainFragment) fragmentManager.findFragmentByTag("MAIN_FRAGMENT")).setUserName(child.getUserName());
        ((MainFragment) fragmentManager.findFragmentByTag("MAIN_FRAGMENT")).changeExerciseStartButtonClickListenerForUser();
    }


    private void setHeaderInfo(Child child) {
        View header = navigationView.getHeaderView(0);

        CircleImageView profileImageView = header.findViewById(R.id.child_profile_image_view);
        TextView nameTextView = header.findViewById(R.id.nav_header_profile_name_text_view);

        nameTextView.setText(child.getUserName());

        if (NavigationActivity.this.isFinishing())
            return;

        Glide.with(profileImageView)
                .load(child.getProfileImageUrl())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.child_image)
                )
                .into(profileImageView);
    }

    public void loadCurrentChildPhysicalData() {
        if (currentChild == null)
            return;

        System.out.println("TEST2");
        firebaseFirestore.collection("Child_Physical_Data")
                .whereEqualTo("uid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("userName", currentChild.getUserName())
                .orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {

                        measureFinish = true;
                        if (e != null) {
                            Log.d(TAG, "데이터 베이스 오류 존재");
                            Log.e(TAG, e.getMessage());
                            return;
                        }

                        if (value.isEmpty()) { //측정된 신체데이터가 없다 ==> 측정 모드로 진입
                            currentChildPhysicalData = null;
                            if (fragmentManager.findFragmentByTag("MAIN_FRAGMENT").isVisible())
                                ((MainFragment) fragmentManager.findFragmentByTag("MAIN_FRAGMENT")).setExerciseMeasureMode();
                        } else { //측정된 신체데이터가 있다
                            for (DocumentSnapshot document : value) {
                                currentChildPhysicalData = document;
                                childPhysicalDataList.add(document);
                                Log.d(TAG, "어린이 정보가 있다 그리고 그 정보의 이름은 : " + document.toObject(ChildPhysicalData.class).getUserName());

                                if (!(getCurrentChildPhysicalData().isMeasureFinish()))
                                    measureFinish = false;
                            }

                            if (fragmentManager.findFragmentByTag("MAIN_FRAGMENT").isVisible()) {
                                if (currentChildPhysicalData == null || !measureFinish) {
                                    Log.d(TAG, "어린이 정보가 없거나 측정이 끝나지 않았다");
                                    ((MainFragment) fragmentManager.findFragmentByTag("MAIN_FRAGMENT")).setExerciseMeasureMode();
                                } else
                                    ((MainFragment) fragmentManager.findFragmentByTag("MAIN_FRAGMENT")).setExercisePlayMode();

                                if ((System.currentTimeMillis() - getCurrentChildPhysicalData().getDate()) / 10000f > 14 * 8640f) {//마지막 측정에 2주가 지났다면
                                    final Dialog dialog = new Dialog(NavigationActivity.this);
                                    dialog.setContentView(R.layout.basic_dialog);

                                    TextView text = dialog.findViewById(R.id.basic_dialog_text_view);
                                    text.setText("마지막 신체능력을 측정한지 2주가 지났습니다. 새로운 신체능력 층적을 진행해야 합니다.");

                                    Button dialogButton = dialog.findViewById(R.id.basic_dialog_ok_button);
                                    dialogButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            soundPool.play(soundId, 1, 1, 0, 0, 1);
                                            dialog.dismiss();
                                        }
                                    });

                                    dialog.show();
                                    ((MainFragment) fragmentManager.findFragmentByTag("MAIN_FRAGMENT")).setExerciseMeasureMode();
                                }
                            }
                        }

                        if (fragmentManager.findFragmentByTag("MAIN_FRAGMENT").isVisible())
                            ((MainFragment) fragmentManager.findFragmentByTag("MAIN_FRAGMENT")).readCalendarData();


                        if (childDataView != null && childDataView instanceof ChildInfoFragment)
                            ((ChildInfoFragment) childDataView).setData();


                    }
                });
    }

    public void checkMeasureMode() {
        if (fragmentManager.findFragmentByTag("MAIN_FRAGMENT").isVisible()) {
            ((MainFragment) fragmentManager.findFragmentByTag("MAIN_FRAGMENT")).readCalendarData();
            if (currentChildPhysicalData == null || !measureFinish)
                ((MainFragment) fragmentManager.findFragmentByTag("MAIN_FRAGMENT")).setExerciseMeasureMode();
            else
                ((MainFragment) fragmentManager.findFragmentByTag("MAIN_FRAGMENT")).setExercisePlayMode();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    private void loadUserChildren() {
        nameList = new ArrayList<>();

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, nameList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(spinnerArrayAdapter);

        firebaseFirestore.collection("Children")
                .whereEqualTo("uid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {

                        System.out.println("TES CHILD CHANGE");
                        if (e != null) {
                            return;
                        }
                        nameList.clear();
                        myChildrenList.clear();
                        childDatas.clear();

                        for (DocumentSnapshot document : value) {
                            Child child = document.toObject(Child.class);
                            myChildrenList.add(child);
                            childDatas.add(document);
                            nameList.add(child.getUserName());
                            spinnerArrayAdapter.notifyDataSetChanged();
                        }

                        if (myChildrenList.size() > 0) {
                            currentChild = myChildrenList.get(currentPosition);
                            currentChildData = childDatas.get(currentPosition);
//                            setHeaderInfo(currentChild);
                        } else {
                            String text = "안녕하세요!\n앱을 사용하기 위해\n먼저 메뉴를 열고\n어린이를 등록해주세요!";

                            ((MainFragment) fragmentManager.findFragmentByTag("MAIN_FRAGMENT")).setDialogText(text);
                            ((MainFragment) fragmentManager.findFragmentByTag("MAIN_FRAGMENT")).changeExerciseStartButtonClickListenerForGuest();
                        }

                    }
                });


    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_ACCESS);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_bluetooth) {
            checkPermissions();
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                startActivityForResult(new Intent(getApplicationContext(), DeviceConnectActivity.class), DEVICE_CONNECT_REQUEST_CODE);
            }
        }

        if (requestCode == DEVICE_CONNECT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                deviceAddress = data.getStringExtra("ADDRESS");
                System.out.println("ACTIVITY ADDRESS : " + deviceAddress);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_user_add) {
            fragment = new ChildManagementFragment();
            fragmentManager.beginTransaction().replace(R.id.content_fragment, fragment).addToBackStack(null).commit();
        } else if (id == R.id.nav_user_info) {
            if (currentChild != null) {
                fragment = ChildInfoFragment.newInstance(currentChild);
                childDataView = (ChildInfoFragment) fragment;
            }
        } else if (id == R.id.nav_friend) {
            fragment = new FriendViewFragment();
        } else if (id == R.id.nav_record) {
            if (currentChild != null) {
                fragment = RecordFragment.newInstance(currentChild);
                childDataView = (RecordFragment) fragment;
            }
        } else if (id == R.id.nav_alarm) {
            showToolbarTextView(getResources().getString(R.string.alarm_setting));
            fragment = new AlarmFragment();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        soundPool.play(soundId, 1, 1, 0, 0, 1);
        if (currentChild != null)
            fragmentManager.beginTransaction().replace(R.id.content_fragment, fragment).addToBackStack(null).commit();
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.stop();
        super.onDestroy();
    }

    @Override
    protected void onUserLeaveHint() {
        mediaPlayer.pause();
        super.onUserLeaveHint();
    }

    public void showToolbarSpinner() {
        toolbarTextView.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);

    }

    public void showToolbarTextView(String title) {
        spinner.setVisibility(View.GONE);
        toolbarTextView.setVisibility(View.VISIBLE);
        toolbarTextView.setText(title);
    }

    public void setNavigationDrawerListener() {
        toggle.setHomeAsUpIndicator(R.drawable.ic_menu);
        toggle.setToolbarNavigationClickListener(navigationDrawerListener);
    }

    public void setNavigationBackListener() {
        toggle.setHomeAsUpIndicator(R.drawable.left_arrow);
        toggle.setToolbarNavigationClickListener(navigationBackListener);
    }

    public void openDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public Child getCurrentChild() {
        return currentChild;
    }

    public DocumentSnapshot getCurrentChildData() {
        return currentChildData;
    }


    public ChildPhysicalData getCurrentChildPhysicalData() {
        if (currentChildPhysicalData != null)
            return currentChildPhysicalData.toObject(ChildPhysicalData.class);
        else
            return null;
    }

    public DocumentSnapshot getChildPhysicalDocumentSnapShot() {
        return currentChildPhysicalData;
    }

    public void clearChildDataView() {
        childDataView = null;
    }
}