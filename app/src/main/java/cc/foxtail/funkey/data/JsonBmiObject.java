package cc.foxtail.funkey.data;

public class JsonBmiObject {
    private final int[] stages;
    private final int[] playTimes;
    private final int bmiExerciseCount1;
    private final int bmiExerciseCount2;
    private final int walkingTime;
    private final int restTime;
    private final int combinationCount1;
    private final int combinationCount2;

    public JsonBmiObject(int[] stages, int[] playTimes, int bmiExerciseCount1, int bmiExerciseCount2, int walkingTime, int restTime, int combinationCount1, int combinationCount2) {
        this.stages = stages;
        this.playTimes = playTimes;
        this.bmiExerciseCount1 = bmiExerciseCount1;
        this.bmiExerciseCount2 = bmiExerciseCount2;
        this.walkingTime = walkingTime;
        this.restTime = restTime;
        this.combinationCount1 = combinationCount1;
        this.combinationCount2 = combinationCount2;
    }

    public int[] getStages() {
        return stages;
    }

    public int[] getPlayTimes() {
        return playTimes;
    }

    public int getBmiExerciseCount1() {
        return bmiExerciseCount1;
    }

    public int getBmiExerciseCount2() {
        return bmiExerciseCount2;
    }

    public int getWalkingTime() {
        return walkingTime;
    }

    public int getRestTime() {
        return restTime;
    }

    public int getCombinationCount1() {
        return combinationCount1;
    }

    public int getCombinationCount2() {
        return combinationCount2;
    }
}
