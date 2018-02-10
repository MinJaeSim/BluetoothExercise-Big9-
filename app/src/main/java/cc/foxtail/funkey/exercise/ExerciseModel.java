package cc.foxtail.funkey.exercise;

import android.content.Context;

import cc.foxtail.funkey.util.ExerciseCountHelper;
import cc.foxtail.funkey.util.ExerciseCounter;

public class ExerciseModel {
    protected ExerciseCounter exerciseCounter;
    protected ExerciseFinishListener finishListener;
    protected ExerciseCountHelper exerciseCountHelper;
    protected int stage;
    protected int time;
    protected Context context;

    public ExerciseModel(int stage, int time, Context context) {
        this.stage = stage;
        this.time = time;
        this.context = context;

        String motionCounterClass = "cc.foxtail.funkey.util.ExerciseCounter" + stage;
        try {
            Class clazz = Class.forName(motionCounterClass);
            exerciseCounter = (ExerciseCounter) clazz.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | java.lang.InstantiationException e) {
            e.printStackTrace();
        }
    }



    public void countExercise(String message) {}

    public void setFinishListener(ExerciseFinishListener listener) {
        this.finishListener = listener;
    }

    public void setExerciseCountHelper(ExerciseCountHelper helper) {
        exerciseCountHelper = helper;
        exerciseCounter.setCountHelper(helper);
    }

    public void startExercise() {
    }

    public void animationEnd(){
        System.out.println("TEST End1");
    }

}
