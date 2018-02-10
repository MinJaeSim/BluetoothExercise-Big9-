package cc.foxtail.funkey.util;


public enum Exercise {
    EXERCISE1, EXERCISE2;

    private int count;
    private ExerciseCountHelper exerciseCountHelper;

    Exercise() {
        count = 0;
    }

    public void setExerciseCountHelper(ExerciseCountHelper exerciseCountHelper) {
        this.exerciseCountHelper = exerciseCountHelper;
    }

}
