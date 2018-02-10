package cc.foxtail.funkey.exercise;

import android.content.Context;

import com.unity3d.player.UnityPlayer;

import java.util.Timer;
import java.util.TimerTask;

import cc.foxtail.funkey.OnExerciseCorrectListener;
import cc.foxtail.funkey.R;

import static cc.foxtail.funkey.exercise.UnityActivity.animationCount;

public class BasicExerciseModel extends ExerciseModel {
    private float playTime = 3000;
    private static final int EXERCISE_COUNT = 10;

    private boolean canCounting;

    private TimerTask timerTask;
    private TimerTask timerTask2;
    private Timer timer;

    private boolean isSuccess;

    @Override
    public void animationEnd() {
        showEffect();
    }

    public BasicExerciseModel(int stage, int time, Context context) {
        super(stage, time, context);
        this.stage = stage;
        this.time = time;
        isSuccess = false;

        initTimer();

        exerciseCounter.setExerciseUpListener(new OnExerciseCorrectListener() {
            @Override
            public void onExcellent() {
                if (canCounting) {
                    isSuccess = true;
                }
            }


            @Override
            public void onFail() {
                if (canCounting) {
                    isSuccess = false;
                }
            }
        });

    }

    public void showEffect() {
        System.out.println("TEST EFFECT!!!!!!!!!");
        exerciseCounter.setExerciseLimitTime(100000);
        canCounting = false;
        UnityPlayer.UnitySendMessage("Player", "SetProgress", String.valueOf(exerciseCounter.getExerciseExcellentCount() + exerciseCounter.getExerciseBadCount() + 1));
        if (isSuccess) {
            UnityPlayer.UnitySendMessage("Player", "StartEffect", "Excellent");
            exerciseCounter.setExerciseExcellentCount(exerciseCounter.getExerciseExcellentCount() + 1);
        } else {
            UnityPlayer.UnitySendMessage("Player", "StartEffect", "Bad");
            exerciseCounter.setExerciseBadCount(exerciseCounter.getExerciseBadCount() + 1);
        }

        if (exerciseCounter.getExerciseExcellentCount() + exerciseCounter.getExerciseBadCount() >= EXERCISE_COUNT) {
            finishExercise();
        }
        exerciseCounter.init();

        isSuccess = false;
    }

    private void initTimer() {
        timer = new Timer();
        UnityPlayer.UnitySendMessage("Player", "SetProgressMax", String.valueOf(3));

        timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Run");
                if (animationCount == 3) {
                    playTime = time / 2f;
                    UnityPlayer.UnitySendMessage("Player", "SetAnimationSpeed", String.valueOf(2));
                    timer.schedule(timerTask2, 0, (int) (playTime));

                    timerTask.cancel();
                    exerciseCounter.resetCount();
                    animationCount = 0;
                    canCounting = true;
                    UnityPlayer.UnitySendMessage("Player", "SetProgressMax", String.valueOf(EXERCISE_COUNT));
                    UnityPlayer.UnitySendMessage("Player", "SetProgress", "0");
                    return;
                }
                isSuccess = false;
                exerciseCounter.setExerciseLimitTime(time - 500);
                exerciseCounter.init();
                canCounting = true;
                UnityPlayer.UnitySendMessage("Player", "PlayAnimation", String.valueOf(stage - 1));
                UnityPlayer.UnitySendMessage("Player", "DrawText", context.getResources().getString(R.string.slow_exercise));
            }
        };

        timerTask2 = new TimerTask() {
            @Override
            public void run() {
                animationCount++;
                isSuccess = false;
                exerciseCounter.setExerciseLimitTime(playTime - 500);
                UnityPlayer.UnitySendMessage("Player", "PlayAnimation", String.valueOf(stage - 1));

                exerciseCounter.init();
                canCounting = true;
                UnityPlayer.UnitySendMessage("Player", "DrawText", context.getResources().getString(R.string.actual_exercise));

            }
        };


    }

    public void countExercise(String message) {
        if (exerciseCounter != null) {
            exerciseCounter.counting();
        }
    }

    public void startExercise() {
        timer.schedule(timerTask, 3000, time);
    }

    public void finishExercise() {
        timerTask2.cancel();
        timer.cancel();
        exerciseCounter.setExerciseLimitTime(999999999);
        canCounting = false;

        int star = 0;

        if (exerciseCounter.getExerciseExcellentCount() > 7) {
            star = 3;
        } else if (exerciseCounter.getExerciseExcellentCount() > 4) {
            star = 2;
        } else if (exerciseCounter.getExerciseExcellentCount() > 0) {
            star = 1;
        }

        int score = exerciseCounter.getExerciseExcellentCount();
        if (finishListener != null)
            finishListener.finishExercise(star, score, EXERCISE_COUNT);
    }

}
