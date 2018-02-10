package cc.foxtail.funkey.util;


import cc.foxtail.funkey.OnExerciseCorrectListener;

public abstract class ExerciseCounter {
    protected ExerciseCountHelper countHelper;
    protected OnExerciseCorrectListener onExerciseCorrectListener;
    protected boolean canCounting;
    protected long startTime;
    protected float exerciseLimitTime;

    protected int exerciseExcellentCount;
    protected int exerciseBadCount;

    public ExerciseCounter() {
        exerciseExcellentCount = 0;
        exerciseBadCount = 0;
        canCounting = true;
        startTime = System.currentTimeMillis();
        exerciseLimitTime = 100000;
    }

    public void setExerciseLimitTime(float exerciseLimitTime) {
        this.exerciseLimitTime = exerciseLimitTime;
    }

    public void init() {
        startTime = System.currentTimeMillis();
    }

    abstract public void counting();

    public void setExerciseUpListener(OnExerciseCorrectListener onExerciseCorrectListener) {
        this.onExerciseCorrectListener = onExerciseCorrectListener;
    }

    public void setCountHelper(ExerciseCountHelper countHelper) {
        this.countHelper = countHelper;
    }

    public int getExerciseExcellentCount() {
        return exerciseExcellentCount;
    }

    public void setExerciseExcellentCount(int exerciseExcellentCount) {
        this.exerciseExcellentCount = exerciseExcellentCount;
    }

    public int getExerciseBadCount() {
        return exerciseBadCount;
    }

    public void setExerciseBadCount(int exerciseBadCount) {
        this.exerciseBadCount = exerciseBadCount;
    }

    public void resetCount() {
        System.out.println("reset count");
        exerciseExcellentCount = 0;
        exerciseBadCount = 0;
    }
}
