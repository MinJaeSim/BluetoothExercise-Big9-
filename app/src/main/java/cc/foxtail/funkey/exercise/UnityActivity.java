package cc.foxtail.funkey.exercise;


import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.unity3d.player.UnityPlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Random;

import cc.foxtail.funkey.R;
import cc.foxtail.funkey.bluetooth.BluetoothContract;
import cc.foxtail.funkey.bluetooth.BluetoothPresenter;
import cc.foxtail.funkey.data.JsonBmiObject;
import cc.foxtail.funkey.util.ExerciseCounter;

import static cc.foxtail.funkey.data.ProtocolConstants.DISCONNECT;
import static cc.foxtail.funkey.data.ProtocolConstants.START_ARM_EXERCISE;
import static cc.foxtail.funkey.data.ProtocolConstants.START_ARM_LEG_EXERCISE;
import static cc.foxtail.funkey.data.ProtocolConstants.STOP_EXERCISE;

public class UnityActivity extends AppCompatActivity implements BluetoothContract.View {


    protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code
    public BluetoothPresenter bluetoothPresenter;
    private Button stopExerciseButton;
    private Button endExerciseButton;
    private int stage;
    private int time;
    private static long exerciseTime;
    private String measureType;
    private int exerciseModelType;
    public static int animationCount;
    public static ExerciseModel exerciseModel;
    private String gender;

    private static final int BASIC_MODEL = 1;
    private static final int MEASURE_MODEL = 2;
    private static final int BMI_MODEL = 3;
    private static final int PT_MODEL = 4;

    public int soundIdSuccess;
    public int soundIdFail;
    public SoundPool soundPool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unity);

        System.out.println("Unity Activity on Create");
        stopExerciseButton = findViewById(R.id.stop_exercise);
        stopExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        endExerciseButton = findViewById(R.id.end_exercise);

        initUnitySettings();

        FrameLayout frameLayout = findViewById(R.id.frame_layout_for_unity_player);
        frameLayout.addView(mUnityPlayer.getView());
        mUnityPlayer.requestFocus();

        final Button startExerciseButton = findViewById(R.id.start_exercise);

        startExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothPresenter.sendProtocol(START_ARM_LEG_EXERCISE);
                UnityPlayer.UnitySendMessage("Player", "DrawText", getResources().getString(R.string.ready_exercise));
                startExerciseButton.setVisibility(View.GONE);
                UnityPlayer.UnitySendMessage("Player", "StartCounter", "");
                bluetoothPresenter.startExercise();

                if (exerciseModelType == MEASURE_MODEL)
                    endExerciseButton.post(new Runnable() {
                        @Override
                        public void run() {
                            endExerciseButton.setVisibility(View.VISIBLE);
                        }
                    });
            }
        });


        UnityPlayer.UnitySendMessage("Player", "DrawText", getResources().getString(R.string.start_exercise));
    }

    private void initUnitySettings() {
        mUnityPlayer = new UnityPlayer(this);
        bluetoothPresenter = new BluetoothPresenter();
        bluetoothPresenter.setView(this);

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();

        soundIdSuccess = soundPool.load(getApplicationContext(), R.raw.result_success, 1);
        soundIdFail = soundPool.load(getApplicationContext(), R.raw.result_fail, 1);

        exerciseModelType = getIntent().getIntExtra("EXERCISE_MODEL_TYPE", BASIC_MODEL);
        String address = getIntent().getStringExtra("DEVICE_ADDRESS");

        stage = getIntent().getIntExtra("STAGE", 0);
        time = getIntent().getIntExtra("TIME", 8000);

        animationCount = 0;

        measureType = getIntent().getStringExtra("MEASURE_TYPE");

        System.out.println("stage : " + stage);

        getWindow().setFormat(PixelFormat.RGBX_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        UnityPlayer.UnitySendMessage("Player", "SetAnimationSpeed", "1");
        gender = Objects.equals(getIntent().getStringExtra("GENDER"), "남") ? "Man" : "Woman";

        if (new Random().nextInt(100) <= 5)
            gender = "Gom";
        UnityPlayer.UnitySendMessage("Player", "SetSex", gender);


        if (exerciseModelType == BASIC_MODEL) {
            exerciseModel = new BasicExerciseModel(stage, time, this);
            bluetoothPresenter.setModel(exerciseModel);
        } else if (exerciseModelType == MEASURE_MODEL) {
            final MeasureModel measureModel = new MeasureModel(stage, time, this, measureType);
            bluetoothPresenter.setModel(measureModel);

            endExerciseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    endExerciseButton.setVisibility(View.VISIBLE);
                    measureModel.finishExercise();
                    endExerciseButton.setVisibility(View.GONE);
                }
            });
        } else if (exerciseModelType == BMI_MODEL) {
            String bmiFileName = getIntent().getStringExtra("BMI_TYPE") + ".json";
            String age = getIntent().getStringExtra("CHILD_AGE");
            String bmiStage = getIntent().getStringExtra("BMI_STAGE");

            if (Integer.valueOf(age) < 5)
                age = "5세";
            else if (Integer.valueOf(age) > 9)
                age = "9세";
            else
                age = age + "세";

            try {
                JSONObject obj = new JSONObject(loadJSONFromAsset(bmiFileName));
                JSONObject bmi_info = obj.getJSONObject(age); //나이 읽기

                JSONObject bmi_stages = bmi_info.getJSONObject(bmiStage);//스텝 읽기
                JSONArray JSONStages = bmi_stages.getJSONArray("stages");
                JSONArray JSONTimes = bmi_stages.getJSONArray("playTimes");
                int bmiExerciseCount1 = bmi_stages.getInt("bmiExerciseCount1");
                int bmiExerciseCount2 = bmi_stages.getInt("bmiExerciseCount2");
                int walkingTime = bmi_stages.getInt("walkingTime");
                int restTime = bmi_stages.getInt("restTime");
                int combinationCount1 = bmi_stages.getInt("combinationCount1");
                int combinationCount2 = bmi_stages.getInt("combinationCount2");

                int[] stages = new int[JSONStages.length()];
                int[] times = new int[JSONTimes.length()];

                for (int i = 0; i < JSONStages.length(); i++)
                    stages[i] = JSONStages.getInt(i);

                for (int j = 0; j < JSONTimes.length(); j++)
                    times[j] = JSONTimes.getInt(j);

                exerciseModel = new BmiModel(stages, times, bmiExerciseCount1, bmiExerciseCount2, walkingTime, restTime, combinationCount1, combinationCount2, this);
                bluetoothPresenter.setModel(exerciseModel);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (exerciseModelType == PT_MODEL) {
            String ptFileName = getIntent().getStringExtra("PT_TYPE");
            String age = getIntent().getStringExtra("CHILD_AGE");
            String ptStage = getIntent().getStringExtra("PT_STAGE");

            System.out.println("TEST " + ptFileName + " stage " + ptStage);

            if (Integer.valueOf(age) < 5)
                age = "5세";
            else if (Integer.valueOf(age) > 9)
                age = "9세";
            else
                age = age + "세";

            try {
                JSONObject obj = new JSONObject(loadJSONFromAsset(ptFileName));
                JSONObject pt_info = obj.getJSONObject(age); //나이 읽기

                JSONObject pt_stages = pt_info.getJSONObject(ptStage);//스텝 읽기
                JSONArray JSONStages = pt_stages.getJSONArray("stages");
                JSONArray JSONTimes = pt_stages.getJSONArray("playTimes");
                int walkingTime1 = pt_stages.getInt("walkingTime1");
                int walkingTime2 = pt_stages.getInt("walkingTime2");

                int[] stages = new int[JSONStages.length()];
                int[] times = new int[JSONTimes.length()];

                for (int i = 0; i < JSONStages.length(); i++)
                    stages[i] = JSONStages.getInt(i);

                for (int j = 0; j < JSONTimes.length(); j++)
                    times[j] = JSONTimes.getInt(j);

                exerciseModel = new PTModel(stages, times, walkingTime1, walkingTime2, this);
                bluetoothPresenter.setModel(exerciseModel);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        bluetoothPresenter.connectBlueToothDevice(address);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    public void checkAnimationEnd() {
        animationCount++;

        exerciseModel.animationEnd();
    }

    public void countDownFinish() {
        System.out.println("카운트다운 끝");
        exerciseTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        bluetoothPresenter.sendProtocol(STOP_EXERCISE);
        bluetoothPresenter.sendProtocol(DISCONNECT);
        bluetoothPresenter.disconnect();
        mUnityPlayer.quit();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUnityPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUnityPlayer.resume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL) {
            mUnityPlayer.lowMemory();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    @Override
    public void printLog(String message) {
//        if (exerciseCounter != null) {
//            exerciseCounter.counting();
//        }

//        System.out.println("TEST LOG : " + message);

    }

    @Override
    public void showResultWindow(int star, int score, int maxCount) {
        if (endExerciseButton.getVisibility() == View.VISIBLE)
            endExerciseButton.post(new Runnable() {
                @Override
                public void run() {
                    endExerciseButton.setVisibility(View.GONE);
                }
            });

        if (stopExerciseButton.getVisibility() == View.GONE)
            stopExerciseButton.post(new Runnable() {
                @Override
                public void run() {
                    stopExerciseButton.setVisibility(View.VISIBLE);
                }
            });

        UnityPlayer.UnitySendMessage("Player", "CloseText", "");


        if (exerciseModelType == MEASURE_MODEL) {
            UnityPlayer.UnitySendMessage("Player", "SetGoodScore", String.valueOf(100));
            UnityPlayer.UnitySendMessage("Player", "SetBadScore", String.valueOf(0));

            UnityPlayer.UnitySendMessage("Player", "SetSuccessText", String.valueOf(maxCount));
            UnityPlayer.UnitySendMessage("Player", "SetFailedText", String.valueOf(0));
        } else {
            float goodScore = (score / (maxCount * 1f)) * 100;

            UnityPlayer.UnitySendMessage("Player", "SetGoodScore", String.valueOf(Math.round(goodScore)));
            UnityPlayer.UnitySendMessage("Player", "SetBadScore", String.valueOf(100 - Math.round(goodScore)));

            UnityPlayer.UnitySendMessage("Player", "SetSuccessText", String.valueOf(score));
            UnityPlayer.UnitySendMessage("Player", "SetFailedText", String.valueOf(maxCount - score));
        }

        if (star >= 2) {
            soundPool.play(soundIdSuccess, 1, 1, 0, 0, 1);
        } else
            soundPool.play(soundIdFail, 1, 1, 0, 0, 1);

        UnityPlayer.UnitySendMessage("Player", "OpenResultView", "");
        UnityPlayer.UnitySendMessage("Player", "SetScore", String.valueOf(star * 33));

        Intent intent = new Intent();

        if (Objects.equals(gender, "Gom"))
            star *= 2;

        intent.putExtra("STAR", star);
        intent.putExtra("STAGE", stage);
        intent.putExtra("MEASURE_TYPE", measureType);
        intent.putExtra("SCORE", score);

        exerciseTime = System.currentTimeMillis() - exerciseTime;

        intent.putExtra("TIME", exerciseTime);

        setResult(RESULT_OK, intent);
    }

    @Override
    public void showDialog(final String message) {

        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final Dialog dialog = new Dialog(UnityActivity.this);
                dialog.setContentView(R.layout.basic_dialog);

                TextView text = dialog.findViewById(R.id.basic_dialog_text_view);
                text.setText(message);

                Button dialogButton = dialog.findViewById(R.id.basic_dialog_ok_button);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        finish();
                    }
                });

                dialog.show();
            }
        }, 0);
    }

    public String loadJSONFromAsset(String fileName) {
        String json;
        try {
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}