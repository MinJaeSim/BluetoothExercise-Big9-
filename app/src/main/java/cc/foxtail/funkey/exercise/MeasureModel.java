package cc.foxtail.funkey.exercise;

import android.content.Context;

import com.unity3d.player.UnityPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import cc.foxtail.funkey.OnExerciseCorrectListener;
import cc.foxtail.funkey.R;
import cc.foxtail.funkey.util.ExerciseCountHelper;
import cc.foxtail.funkey.util.ExerciseCounter99;

import static cc.foxtail.funkey.exercise.UnityActivity.animationCount;

public class MeasureModel extends ExerciseModel {
    private float playTime = 3000;

    private TimerTask timerTask;
    private TimerTask timerTask2;
    private TimerTask timerTask3;
    private Timer timer;

    private int oneSecondChecker;

    private boolean isOneMinuteCheck = false;

    private String measureType;
    private int oneMinute;

    public MeasureModel(int stage, int time, Context context, String measureType) {
        super(stage, time, context);

        this.measureType = measureType;

        oneSecondChecker = 0;
        oneMinute = 60000;

        if (Objects.equals(measureType, "strengthEndurance")) {
            exerciseCounter = new ExerciseCounter99();
        }

        if (Objects.equals(measureType, "lungCapacity")) {
            oneMinute = 100000;
        }

        initTimer();

        if (Objects.equals(measureType, "core")) {
            exerciseCounter = new ExerciseCounter99();
            UnityPlayer.UnitySendMessage("Player", "DrawText", context.getResources().getString(R.string.core_exercise));
        }

        exerciseCounter.setExerciseUpListener(new OnExerciseCorrectListener() {
            @Override
            public void onExcellent() {
                exerciseCounter.setExerciseExcellentCount(exerciseCounter.getExerciseExcellentCount() + 1);
                System.out.println("excellent score : " + exerciseCounter.getExerciseExcellentCount());
                UnityPlayer.UnitySendMessage("Player", "StartEffect", "Excellent");
                exerciseCountHelper.initArmFlag();
                exerciseCountHelper.initLegFlag();
                exerciseCounter.init();
            }

            @Override
            public void onFail() {
                exerciseCounter.init();
            }
        });

        timerTask3 = new TimerTask() {
            @Override
            public void run() {
                oneSecondChecker++;
                UnityPlayer.UnitySendMessage("Player", "SetProgress", String.valueOf(oneSecondChecker));
            }
        };

    }

    private void initTimer() {
        timer = new Timer();

        UnityPlayer.UnitySendMessage("Player", "SetProgressMax", String.valueOf(3));
        if (Objects.equals(measureType, "core")) {
            animationCount = 3;
            int max = oneMinute / 1000 >= 100 ? 99 : oneMinute / 1000;
            UnityPlayer.UnitySendMessage("Player", "SetProgressMax", String.valueOf(max));

            isOneMinuteCheck = false;

            timerTask = new TimerTask() {
                @Override
                public void run() {
                    exerciseCounter.setExerciseLimitTime(999999999);
                    isOneMinuteCheck = true;
                    UnityPlayer.UnitySendMessage("Player", "DrawText", context.getResources().getString(R.string.core_exercise));
                    UnityPlayer.UnitySendMessage("Player", "PlayAnimation", String.valueOf(stage - 1));
                }
            };
            timerTask2 = timerTask;
        } else {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (animationCount == 3) {
                        playTime = time / 2f;
                        UnityPlayer.UnitySendMessage("Player", "SetAnimationSpeed", String.valueOf(2));
                        timer.schedule(timerTask2, 0, (int) (playTime));

                        timer.schedule(timerTask3, 0, 1000);

                        timerTask.cancel();
                        exerciseCounter.resetCount();
                        animationCount = 0;


                        isOneMinuteCheck = true;
                        exerciseCounter.setExerciseLimitTime(999999999);
                        UnityPlayer.UnitySendMessage("Player", "DrawText", context.getResources().getString(R.string.measure_exercise));
                        int max = oneMinute / 1000 >= 100 ? 99 : oneMinute / 1000;

                        UnityPlayer.UnitySendMessage("Player", "SetProgressMax", String.valueOf(max));
                        UnityPlayer.UnitySendMessage("Player", "SetProgress", "0");
                        System.out.println("1분 시작");
                        return;
                    }
                    UnityPlayer.UnitySendMessage("Player", "SetProgress", String.valueOf(animationCount));
                    exerciseCounter.setExerciseLimitTime(time - 650);
                    exerciseCounter.init();

                    UnityPlayer.UnitySendMessage("Player", "PlayAnimation", String.valueOf(stage - 1));
                    UnityPlayer.UnitySendMessage("Player", "DrawText", context.getResources().getString(R.string.slow_exercise));
                }
            };

            timerTask2 = new TimerTask() {
                @Override
                public void run() {
                    animationCount++;
                    exerciseCounter.setExerciseLimitTime(999999999);
                    UnityPlayer.UnitySendMessage("Player", "PlayAnimation", String.valueOf(stage - 1));
                    UnityPlayer.UnitySendMessage("Player", "DrawText", context.getResources().getString(R.string.measure_exercise));
                }
            };
        }
    }


    public void countExercise(String message) {
        if (exerciseCounter != null) {
            exerciseCounter.counting();
        }

        if (isOneMinuteCheck && oneSecondChecker >= 60) {
            isOneMinuteCheck = false;
            finishExercise();
        }
    }

    public void startExercise() {
        if (Objects.equals(measureType, "core")) {
            timer.schedule(timerTask, 0, 20000);
            timer.schedule(timerTask3, 3000, 1000);
        } else
            timer.schedule(timerTask, 3000, time);
    }
    @Override
    public void animationEnd() {

    }


    public void finishExercise() {
        timerTask2.cancel();
        timerTask3.cancel();
        timer.cancel();

        exerciseCounter.setExerciseLimitTime(999999999);

        int star = 3;
        int score;

        int[] scoreList = getScoreList(measureType);
        int count = Objects.equals(measureType, "core") ? oneSecondChecker : exerciseCounter.getExerciseExcellentCount();
        if (count >= scoreList[9]) {
            score = 20;
        } else if (count >= scoreList[8]) {
            score = 19;
        } else if (count >= scoreList[7]) {
            score = 18;
        } else if (count >= scoreList[6]) {
            score = 17;
        } else if (count >= scoreList[5]) {
            score = 16;
            star = 2;
        } else if (count >= scoreList[4]) {
            score = 15;
            star = 2;
        } else if (count >= scoreList[3]) {
            score = 14;
            star = 2;
        } else if (count >= scoreList[2]) {
            score = 13;
            star = 2;
        } else if (count >= scoreList[1]) {
            score = 12;
            star = 1;
        } else if (count >= scoreList[0]) {
            score = 11;
            star = 1;
        } else if (count >= 1) {
            score = 10;
            star = 1;
        } else {
            score = 0;
            star = 0;
        }

        if (finishListener != null)
            finishListener.finishExercise(star, score, exerciseCounter.getExerciseExcellentCount());
    }

    private int[] getScoreList(String measureType) {
        if (Objects.equals(measureType, "lungCapacity")) {
            return new int[]{25, 28, 31, 34, 37, 40, 43, 46, 49, 52};
        } else if (Objects.equals(measureType, "strengthDown") || Objects.equals(measureType, "strengthEndurance")) {
            return new int[]{18, 21, 24, 27, 30, 33, 36, 39, 42, 45};
        } else if (Objects.equals(measureType, "core")) {
            return new int[]{5, 9, 13, 17, 21, 25, 29, 33, 37, 41};
        } else {
            return new int[]{10, 13, 16, 19, 22, 25, 28, 31, 34, 37};
        }
    }
}

