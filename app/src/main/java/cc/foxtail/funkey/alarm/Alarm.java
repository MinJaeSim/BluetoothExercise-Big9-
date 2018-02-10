package cc.foxtail.funkey.alarm;


import java.util.List;

public class Alarm {
    private String userName;
    private String gender;
    private String time;
    private List<Boolean> day;
    private boolean isOn;
    private int alarmId;
    private String uid;

    public Alarm() {

    }

    public Alarm(String userName, String gender, String time, List<Boolean> day, boolean isOn, int alarmId, String uid) {
        this.userName = userName;
        this.gender = gender;
        this.time = time;
        this.day = day;
        this.isOn = isOn;
        this.alarmId = alarmId;
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<Boolean> getDay() {
        return day;
    }

    public void setDay(List<Boolean> day) {
        this.day = day;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
