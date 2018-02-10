package cc.foxtail.funkey.data;


public class ExerciseTime {
    private String uid;
    private String userName;
    private String date;
    private long time;
    private int star;

    public ExerciseTime() {
    }

    public ExerciseTime(String uid, String userName, String date, long time, int star) {
        this.uid = uid;
        this.userName = userName;
        this.date = date;
        this.time = time;
        this.star = star;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }
}
