package cc.foxtail.funkey.data;


public class Stage {
    private String stageName;
    private int stageNumber;
    private int stagePlayTime;

    public String getStageName() {
        return stageName;
    }

    public int getStageNumber() {
        return stageNumber;
    }

    public int getStagePlayTime() {
        return stagePlayTime;
    }

    public Stage(String stageName, int stageNumber, int stagePlayTime) {
        this.stageName = stageName;
        this.stageNumber = stageNumber;
        this.stagePlayTime = stagePlayTime;
    }
}
