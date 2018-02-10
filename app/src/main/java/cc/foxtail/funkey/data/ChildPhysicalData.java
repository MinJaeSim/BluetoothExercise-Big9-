package cc.foxtail.funkey.data;

public class ChildPhysicalData {
    private long date;
    private String uid;
    private String userName;
    private int core;
    private int lungCapacity;
    private int strengthDown;
    private int strengthUp;
    private int strengthEndurance;
    private boolean measureFinish;

    public ChildPhysicalData() {
    }

    public long getDate() {
        return date;
    }

    public String getUid() {
        return uid;
    }

    public String getUserName() {
        return userName;
    }

    public int getCore() {
        return core;
    }

    public int getLungCapacity() {
        return lungCapacity;
    }

    public int getStrengthDown() {
        return strengthDown;
    }

    public int getStrengthUp() {
        return strengthUp;
    }

    public int getStrengthEndurance() {
        return strengthEndurance;
    }

    public void setCore(int core) {
        this.core = core;
    }

    public void setLungCapacity(int lungCapacity) {
        this.lungCapacity = lungCapacity;
    }

    public void setStrengthDown(int strengthDown) {
        this.strengthDown = strengthDown;
    }

    public void setStrengthUp(int strengthUp) {
        this.strengthUp = strengthUp;
    }

    public void setStrengthEndurance(int strengthEndurance) {
        this.strengthEndurance = strengthEndurance;
    }

    public boolean isMeasureFinish() {
        return measureFinish;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void checkTestFinish() {
        if (strengthDown != 0 && strengthUp != 0 && strengthEndurance != 0
                && lungCapacity != 0 && core != 0) {
            measureFinish = true;
        } else
            measureFinish = false;
    }
}
