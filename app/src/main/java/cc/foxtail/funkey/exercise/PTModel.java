package cc.foxtail.funkey.exercise;

import android.content.Context;

import com.unity3d.player.UnityPlayer;

import java.util.Timer;
import java.util.TimerTask;

import cc.foxtail.funkey.OnExerciseCorrectListener;
import cc.foxtail.funkey.R;
import cc.foxtail.funkey.util.ExerciseCounter;

import static cc.foxtail.funkey.exercise.UnityActivity.animationCount;

public class PTModel extends ExerciseModel {


    private int[] stages;
    private int[] times;
    private int walkingTime1;
    private int walkingTime2;
    private ExerciseCounter[] exerciseCounters;

    private boolean canCounting;
    private int currentAnimation;
    private int currentAnimationTime;

    private boolean step1;
    private boolean step2;
    private boolean step3;
    private boolean step4;
    private boolean walking1;
    private boolean walking2;

    private final float playSpeed = 1.2f;
    private int maxCount;
    private final int limit = 800;

    private int successCount;
    private boolean isSuccess;

    public PTModel(int[] stages, int[] times, int walkingTime1, int walkingTime2, Context context) {
        super(stages[0], times[0], context);
        this.stages = stages;
        this.times = times;
        this.walkingTime1 = walkingTime1;
        this.walkingTime2 = walkingTime2;
        successCount = 0;

        step1 = false;
        step2 = true;
        step3 = false;
        step4 = false;
        walking1 = false;
        walking2 = false;

        maxCount = 6 + 6 + 9 + 12;

        isSuccess = false;

        currentAnimation = stages[0] - 1;
        currentAnimationTime = times[0];
        exerciseCounters = new ExerciseCounter[stages.length];

        UnityPlayer.UnitySendMessage("Player", "DrawText", context.getResources().getString(R.string.actual_exercise));

        UnityPlayer.UnitySendMessage("Player", "SetProgressMax", String.valueOf(6));
        UnityPlayer.UnitySendMessage("Player", "SetProgress", String.valueOf(0));

        for (int i = 0; i < stages.length; i++) {
            int tempStage = stages[i];
            System.out.println("TEST " + tempStage);
            String motionCounterClass = "cc.foxtail.funkey.util.ExerciseCounter" + tempStage;
            try {
                Class clazz = Class.forName(motionCounterClass);
                exerciseCounters[i] = (ExerciseCounter) clazz.newInstance();
                System.out.println(exerciseCounters[i].toString());
            } catch (ClassNotFoundException | IllegalAccessException | java.lang.InstantiationException e) {
                e.printStackTrace();
            }
        }

        for (ExerciseCounter exerciseCounter : exerciseCounters) {
            setExerciseCounterListener(exerciseCounter);
        }
        exerciseCounter = exerciseCounters[0];
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

    public void animationEnd() {
        showEffect();
        if (walking1) {
            System.out.println("STEP WALKING1 : " + ((walkingTime1 / 1000) - 1));
            UnityPlayer.UnitySendMessage("Player", "SetProgressMax", String.valueOf(walkingTime1 / 1000));

            String text = String.format(context.getResources().getString(R.string.walking_on_spot), walkingTime1 / 1000);
            UnityPlayer.UnitySendMessage("Player", "DrawText", text);
            if (animationCount == walkingTime1 / 1000 - 1) {
                System.out.println("STEP WALKING1 FINISH");
                walking1 = false;
            }
            UnityPlayer.UnitySendMessage("Player", "SetProgress", String.valueOf(animationCount));
            currentAnimation = 28;
            exerciseCounter.setExerciseLimitTime(999999999);
            if (!walking1)
                animationCount = -1;
            playAnimation(false);
        } else if (walking2) {

            System.out.println("STEP WALKING2 : " + ((walkingTime2 / 1000) - 1));
            UnityPlayer.UnitySendMessage("Player", "SetProgressMax", String.valueOf(walkingTime2 / 1000));

            String text = String.format(context.getResources().getString(R.string.walking_on_spot), walkingTime2 / 1000);
            UnityPlayer.UnitySendMessage("Player", "DrawText", text);
            if (animationCount == walkingTime2 / 1000 - 1) {
                System.out.println("STEP WALKING1 FINISH");
                walking2 = false;
            }
            UnityPlayer.UnitySendMessage("Player", "SetProgress", String.valueOf(animationCount));
            currentAnimation = 28;
            exerciseCounter.setExerciseLimitTime(999999999);
            if (!walking2)
                animationCount = -1;
            playAnimation(false);
        } else if (step1) {
            System.out.println("STEP1 : " + animationCount);

            UnityPlayer.UnitySendMessage("Player", "SetProgressMax", String.valueOf(6));
            UnityPlayer.UnitySendMessage("Player", "SetProgress", String.valueOf(animationCount));
            if (animationCount == 5) {
                System.out.println("STEP1 Finish");
                step1 = false;
                step2 = true;
            }

            if (animationCount % 3 == 0) {
                currentAnimation = stages[0] - 1;
                currentAnimationTime = times[0];
                exerciseCounter = exerciseCounters[0];
            } else if (animationCount % 3 == 1) {
                currentAnimation = stages[1] - 1;
                currentAnimationTime = times[1];
                exerciseCounter = exerciseCounters[1];
            } else if (animationCount % 3 == 2) {
                currentAnimation = stages[2] - 1;
                currentAnimationTime = times[2];
                exerciseCounter = exerciseCounters[2];
            }
            exerciseCounter.setCountHelper(exerciseCountHelper);
            exerciseCounter.setExerciseLimitTime(currentAnimationTime / playSpeed - limit);
            if (step2)
                animationCount = -1;
            playAnimation(false);

        } else if (step2) {
            System.out.println("STEP2 : " + animationCount);

            UnityPlayer.UnitySendMessage("Player", "SetProgressMax", String.valueOf(6));
            UnityPlayer.UnitySendMessage("Player", "SetProgress", String.valueOf(animationCount));
            if (animationCount == 5) {
                System.out.println("STEP2 Finish");
                step2 = false;
                walking1 = true;
                step3 = true;
            }

            if (animationCount < 2) {
                currentAnimation = stages[0] - 1;
                currentAnimationTime = times[0];
                exerciseCounter = exerciseCounters[0];
            } else if (animationCount < 4) {
                currentAnimation = stages[1] - 1;
                currentAnimationTime = times[1];
                exerciseCounter = exerciseCounters[1];
            } else if (animationCount < 6) {
                currentAnimation = stages[2] - 1;
                currentAnimationTime = times[2];
                exerciseCounter = exerciseCounters[2];
            }
            exerciseCounter.setCountHelper(exerciseCountHelper);
            exerciseCounter.setExerciseLimitTime(currentAnimationTime / playSpeed - limit);
            if (step3)
                animationCount = -1;
            playAnimation(false);
        } else if (step3) {
            System.out.println("STEP3 : " + animationCount);
            UnityPlayer.UnitySendMessage("Player", "SetProgressMax", String.valueOf(9));
            UnityPlayer.UnitySendMessage("Player", "SetProgress", String.valueOf(animationCount));
            if (animationCount == 8) {
                step3 = false;
                walking2 = true;
                step4 = true;
            }

            if (animationCount < 3) {
                currentAnimation = stages[0] - 1;
                currentAnimationTime = times[0];
                exerciseCounter = exerciseCounters[0];
            } else if (animationCount < 6) {
                currentAnimation = stages[1] - 1;
                currentAnimationTime = times[1];
                exerciseCounter = exerciseCounters[1];
            } else if (animationCount < 9) {
                currentAnimation = stages[2] - 1;
                currentAnimationTime = times[2];
                exerciseCounter = exerciseCounters[2];
            }
            exerciseCounter.setCountHelper(exerciseCountHelper);
            exerciseCounter.setExerciseLimitTime(currentAnimationTime / playSpeed - limit);
            if (step4)
                animationCount = -1;
            playAnimation(false);

        } else if (step4) {
            System.out.println("STEP4 : " + animationCount);
            UnityPlayer.UnitySendMessage("Player", "SetProgressMax", String.valueOf(12));
            UnityPlayer.UnitySendMessage("Player", "SetProgress", String.valueOf(animationCount));
            if (animationCount == 12) {
                step4 = false;
                UnityPlayer.UnitySendMessage("Player", "SetProgress", String.valueOf(12));
                finishExercise();
                return;
            }

            if (animationCount < 4) {
                currentAnimation = stages[0] - 1;
                currentAnimationTime = times[0];
                exerciseCounter = exerciseCounters[0];
            } else if (animationCount < 8) {
                currentAnimation = stages[1] - 1;
                currentAnimationTime = times[1];
                exerciseCounter = exerciseCounters[1];
            } else if (animationCount < 12) {
                currentAnimation = stages[2] - 1;
                currentAnimationTime = times[2];
                exerciseCounter = exerciseCounters[2];
            }
            exerciseCounter.setCountHelper(exerciseCountHelper);
            exerciseCounter.setExerciseLimitTime(currentAnimationTime / playSpeed - limit);
            playAnimation(false);

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

    private void init() {
        System.out.println("INIT");
        UnityPlayer.UnitySendMessage("Player", "SetAnimationSpeed", String.valueOf(playSpeed));
    }

    public void playAnimation(boolean fast) {
        System.out.println("PLAYING");
        isSuccess = false;

        canCounting = true;
        exerciseCounter.init();
        if (fast) {
            UnityPlayer.UnitySendMessage("Player", "SetAnimationSpeed", String.valueOf(playSpeed * 1.5));
            UnityPlayer.UnitySendMessage("Player", "DrawText", context.getResources().getString(R.string.actual_exercise));
        } else if ((walking1 || walking2) && currentAnimation == 28) {
            UnityPlayer.UnitySendMessage("Player", "SetAnimationSpeed", String.valueOf(1));
        } else {
            UnityPlayer.UnitySendMessage("Player", "SetAnimationSpeed", String.valueOf(playSpeed));
            UnityPlayer.UnitySendMessage("Player", "DrawText", context.getResources().getString(R.string.actual_exercise));
        }

        UnityPlayer.UnitySendMessage("Player", "PlayAnimation", String.valueOf(currentAnimation));
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

    public void finishExercise() {
        exerciseCounter.setExerciseLimitTime(999999999);
        canCounting = false;

        UnityPlayer.UnitySendMessage("Player", "CloseText", "");

        int star = 0;

        if (successCount > maxCount * 2 / 3) {
            star = 3;
        } else if (successCount > maxCount / 3) {
            star = 2;
        } else if (successCount >= 1) {
            star = 1;
        }

        if (finishListener != null)
            finishListener.finishExercise(star, successCount, maxCount);
    }
}
