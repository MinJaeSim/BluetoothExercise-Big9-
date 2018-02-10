package cc.foxtail.funkey.exercise;

import android.content.Context;

import com.unity3d.player.UnityPlayer;

import java.util.Timer;
import java.util.TimerTask;

import cc.foxtail.funkey.OnExerciseCorrectListener;
import cc.foxtail.funkey.R;
import cc.foxtail.funkey.data.JsonBmiObject;
import cc.foxtail.funkey.util.ExerciseCounter;

import static cc.foxtail.funkey.exercise.UnityActivity.animationCount;

public class BmiModel extends ExerciseModel {
    private int[] stages;
    private int[] times;

    private int walkingTime;
    private int restTime;
    private int combinationCount1;
    private int combinationCount2;

    private ExerciseCounter[] exerciseCounters;
    private final float playSpeed = 1.2f;
    private boolean canCounting;
    private int bmiExerciseCount1;
    private int bmiExerciseCount2;
    private int currentAnimation;
    private int currentAnimationTime;

    private boolean step1;
    private boolean step2;
    private boolean step3;
    private boolean step4;
    private boolean walking;
    private boolean rest;

    private int successCount;
    private boolean isSuccess;

    private final int limit = 750;

    public BmiModel(int[] stages, int[] times, int bmiExerciseCount1, int bmiExerciseCount2, int walkingTime, int restTime, int combinationCount1, int combinationCount2, Context context) {
        super(stages[0], times[0], context);

        this.stages = stages;
        this.times = times;
        this.bmiExerciseCount1 = bmiExerciseCount1;
        this.bmiExerciseCount2 = bmiExerciseCount2;
        this.walkingTime = walkingTime;
        this.restTime = restTime;
        this.combinationCount1 = combinationCount1;
        this.combinationCount2 = combinationCount2;

        step1 = true;
        step2 = false;
        step3 = false;
        step4 = false;
        walking = false;
        rest = false;

        successCount = 0;

        isSuccess = false;

        currentAnimation = stages[0] - 1;
        currentAnimationTime = times[0];
        exerciseCounters = new ExerciseCounter[stages.length];

        for (int i = 0; i < stages.length; i++) {
            int tempStage = stages[i];
            String motionCounterClass = "cc.foxtail.funkey.util.ExerciseCounter" + tempStage;
            try {
                Class clazz = Class.forName(motionCounterClass);
                exerciseCounters[i] = (ExerciseCounter) clazz.newInstance();
            } catch (ClassNotFoundException | IllegalAccessException | java.lang.InstantiationException e) {
                e.printStackTrace();
            }
        }

        for (ExerciseCounter exerciseCounter : exerciseCounters) {
            setExerciseCounterListener(exerciseCounter);
        }

        exerciseCounter = exerciseCounters[0];
        UnityPlayer.UnitySendMessage("Player", "SetProgressMax", String.valueOf(bmiExerciseCount1));
        UnityPlayer.UnitySendMessage("Player", "SetProgress", "0");

    }

    public void showEffect() {
        System.out.println("TEST EFFECT!!!!!!!!!");
        exerciseCounter.setExerciseLimitTime(100000);
        canCounting = false;
        if (currentAnimation == 28)
            return;
        if (isSuccess) {
            UnityPlayer.UnitySendMessage("Player", "StartEffect", "Excellent");
            exerciseCounter.setExerciseExcellentCount(exerciseCounter.getExerciseExcellentCount() + 1);
            successCount += 1;
        } else {
            UnityPlayer.UnitySendMessage("Player", "StartEffect", "Bad");
            exerciseCounter.setExerciseBadCount(exerciseCounter.getExerciseBadCount() + 1);
        }
    }


    @Override
    public void animationEnd() {
        showEffect();
        if (walking) {

            String text = String.format(context.getResources().getString(R.string.walking_on_spot), walkingTime / 1000);
            UnityPlayer.UnitySendMessage("Player", "DrawText", text);
            UnityPlayer.UnitySendMessage("Player", "SetProgressMax", String.valueOf(walkingTime / 1000));
            UnityPlayer.UnitySendMessage("Player", "SetProgress", String.valueOf(animationCount));

            currentAnimation = 28;
            playAnimation(false);


            exerciseCounter.setExerciseLimitTime(999999);

            if (animationCount == walkingTime / 1000) {
                walking = false;
                animationCount = 0;
                if (step2)
                    UnityPlayer.UnitySendMessage("Player", "SetProgressMax", String.valueOf(bmiExerciseCount2));
                if (step3)
                    UnityPlayer.UnitySendMessage("Player", "SetProgressMax", String.valueOf(combinationCount1 * 2));
                UnityPlayer.UnitySendMessage("Player", "SetProgress", "0");
                animationEnd();
            }
        } else if (rest) {
            String text = String.format(context.getResources().getString(R.string.walking_on_spot), restTime / 1000);
            UnityPlayer.UnitySendMessage("Player", "DrawText", text);
            UnityPlayer.UnitySendMessage("Player", "SetProgressMax", String.valueOf(restTime / 1000));
            UnityPlayer.UnitySendMessage("Player", "SetProgress", String.valueOf(animationCount));

            currentAnimation = 28;
            playAnimation(false);

            exerciseCounter.setExerciseLimitTime(999999);

            if (animationCount == restTime / 1000) {
                rest = false;
                animationCount = 0;
                UnityPlayer.UnitySendMessage("Player", "SetProgressMax", String.valueOf(combinationCount2 * 2));
                UnityPlayer.UnitySendMessage("Player", "SetProgress", "0");
                animationEnd();
            }
        } else if (step1) {
            System.out.println("STEP1 : " + animationCount);
            UnityPlayer.UnitySendMessage("Player", "SetProgress", String.valueOf(animationCount));
            if (animationCount == bmiExerciseCount1 - 1) {
                step1 = false;
                walking = true;
                step2 = true;
                animationCount = 0;
            }

            currentAnimation = stages[0] - 1;
            exerciseCounter = exerciseCounters[0];
            exerciseCounter.setCountHelper(exerciseCountHelper);

            currentAnimationTime = times[0];
            exerciseCounter.setExerciseLimitTime(currentAnimationTime / playSpeed - 500);
            playAnimation(false);

        } else if (step2) {
            UnityPlayer.UnitySendMessage("Player", "DrawText", context.getResources().getString(R.string.actual_exercise));
            System.out.println("STEP2 : " + animationCount);
            UnityPlayer.UnitySendMessage("Player", "SetProgress", String.valueOf(animationCount));
            if (animationCount == bmiExerciseCount2 - 1) {
                step2 = false;
                walking = true;
                step3 = true;
                animationCount = 0;
            }

            currentAnimation = stages[1] - 1;
            exerciseCounter = exerciseCounters[1];
            exerciseCounter.setCountHelper(exerciseCountHelper);

            currentAnimationTime = times[1];
            exerciseCounter.setExerciseLimitTime(currentAnimationTime / playSpeed - limit);
            playAnimation(false);


        } else if (step3) {
            UnityPlayer.UnitySendMessage("Player", "DrawText", context.getResources().getString(R.string.actual_exercise));
            System.out.println("STEP3 : " + animationCount);
            UnityPlayer.UnitySendMessage("Player", "SetProgress", String.valueOf(animationCount));
            if (animationCount == (combinationCount1 * 2) - 1) {
                step3 = false;
                rest = true;
                step4 = true;
                animationCount = 0;
            }
            if (animationCount % 2 == 0) {
                currentAnimation = stages[0] - 1;
                exerciseCounter = exerciseCounters[0];
                exerciseCounter.setCountHelper(exerciseCountHelper);

                currentAnimationTime = times[0];
                exerciseCounter.setExerciseLimitTime(currentAnimationTime / playSpeed - limit);
                playAnimation(false);
            } else {
                currentAnimation = stages[1] - 1;
                exerciseCounter = exerciseCounters[1];
                exerciseCounter.setCountHelper(exerciseCountHelper);

                currentAnimationTime = times[1];
                exerciseCounter.setExerciseLimitTime(currentAnimationTime / playSpeed - limit);
                playAnimation(false);
            }

        } else if (step4) {
            UnityPlayer.UnitySendMessage("Player", "DrawText", context.getResources().getString(R.string.actual_exercise));
            System.out.println("STEP4 : " + animationCount);
            UnityPlayer.UnitySendMessage("Player", "SetProgress", String.valueOf(animationCount));
            if (animationCount == (combinationCount2 * 2)) {
                step4 = false;
                finishExercise();
                return;
            }
            if (animationCount % 2 == 0) {
                currentAnimation = stages[0] - 1;
                exerciseCounter = exerciseCounters[0];
                exerciseCounter.setCountHelper(exerciseCountHelper);

                currentAnimationTime = times[0];
                exerciseCounter.setExerciseLimitTime((currentAnimationTime / (playSpeed * 1.5f)) - limit);
                playAnimation(true);
            } else {
                currentAnimation = stages[1] - 1;
                exerciseCounter = exerciseCounters[1];
                exerciseCounter.setCountHelper(exerciseCountHelper);

                currentAnimationTime = times[1];
                exerciseCounter.setExerciseLimitTime((currentAnimationTime / (playSpeed * 1.5f)) - limit);
                playAnimation(true);
            }

        }
    }


    public void countExercise(String message) {
        if (exerciseCounter != null) {
            exerciseCounter.counting();
        }
    }

    @Override
    public void startExercise() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        exerciseCounter.setCountHelper(exerciseCountHelper);
        currentAnimation = stages[0] - 1;
        currentAnimationTime = times[0];
        exerciseCounter.setExerciseLimitTime(currentAnimationTime / playSpeed - limit);
        playAnimation(false);
    }

    public void playAnimation(boolean fast) {
        System.out.println("PLAYING");
        isSuccess = false;
//        UnityPlayer.UnitySendMessage("Player", "DrawText", context.getResources().getString(R.string.actual_exercise));
        canCounting = true;
        exerciseCounter.init();
        if (fast)
            UnityPlayer.UnitySendMessage("Player", "SetAnimationSpeed", String.valueOf(playSpeed * 1.5));
        else
            UnityPlayer.UnitySendMessage("Player", "SetAnimationSpeed", String.valueOf(playSpeed));
        UnityPlayer.UnitySendMessage("Player", "PlayAnimation", String.valueOf(currentAnimation));
    }

    private void init() {
        System.out.println("INIT");
        UnityPlayer.UnitySendMessage("Player", "SetAnimationSpeed", String.valueOf(playSpeed));
    }

    public void finishExercise() {
        exerciseCounter.setExerciseLimitTime(999999999);
        canCounting = false;

        UnityPlayer.UnitySendMessage("Player", "CloseText", "");

        UnityPlayer.UnitySendMessage("Player", "OpenResultWindow", "");
        UnityPlayer.UnitySendMessage("Player", "SetResultWindowText", "Success: " + successCount);

        int star = 0;

        if (successCount > ((combinationCount1 * 2) + (combinationCount2 * 2) + (bmiExerciseCount1 + bmiExerciseCount2)) * 2 / 3) {
            star = 3;
        } else if (successCount > ((combinationCount1 * 2) + (combinationCount2 * 2) + (bmiExerciseCount1 + bmiExerciseCount2)) * 2 / 3) {
            star = 2;
        } else if (successCount > 0) {
            star = 1;
        }

        if (finishListener != null)
            finishListener.finishExercise(star, successCount, ((combinationCount1 * 2) + (combinationCount2 * 2) + (bmiExerciseCount1 + bmiExerciseCount2)));
    }


    private void setExerciseCounterListener(final ExerciseCounter counter) {
        counter.setExerciseUpListener(new OnExerciseCorrectListener() {
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
}