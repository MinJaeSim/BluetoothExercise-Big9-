package cc.foxtail.funkey.data;


public class ChildScoreData {
    private String userName;
    private String uid;
    private long date;
    private int score;
    private int stamina;
    private int flexibility;
    private int balance;
    private int strength;
    private int quick;

    public ChildScoreData() {

    }

    public ChildScoreData(String userName, String uid, long date, int score, int stamina, int flexibility, int balance, int strength, int quick) {
        this.userName = userName;
        this.uid = uid;
        this.date = date;
        this.score = score;
        this.stamina = stamina;
        this.flexibility = flexibility;
        this.balance = balance;
        this.strength = strength;
        this.quick = quick;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public int getFlexibility() {
        return flexibility;
    }

    public void setFlexibility(int flexibility) {
        this.flexibility = flexibility;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getQuick() {
        return quick;
    }

    public void setQuick(int quick) {
        this.quick = quick;
    }
}
