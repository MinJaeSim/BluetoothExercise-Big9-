package cc.foxtail.funkey.data;

import java.io.Serializable;
import java.util.List;

public class Child implements Serializable {

    private String userName;
    private String sex;
    private long birthTimeStamp;
    private String age;
    private String nickName;
    private int weight;
    private int height;
    private String predictHeight;
    private String uid;
    private String profileImageUrl;
    private int totalStarCount;
    private List<Integer> stageScore;

    public Child(String userName, String sex, long birthTimeStamp,String age, String nickName, int weight, int height,String predictHeight, String uid, String profileImageUrl, List<Integer> stageScore) {
        this.userName = userName;
        this.sex = sex;
        this.birthTimeStamp = birthTimeStamp;
        this.age = age;
        this.nickName = nickName;
        this.weight = weight;
        this.height = height;
        this.predictHeight = predictHeight;
        this.uid = uid;
        this.profileImageUrl = profileImageUrl;
        this.stageScore = stageScore;
    }

    public Child() {
    }

    public String getUid() {
        return uid;
    }

    public String getUserName() {
        return userName;
    }

    public long getBirthTimeStamp() {
        return birthTimeStamp;
    }

    public int getWeight() {
        return weight;
    }

    public int getHeight() {
        return height;
    }

    public String getPredictHeight() {
        return predictHeight;
    }

    public String getSex() {
        return sex;
    }

    public String getAge() {
        return age;
    }

    public String getNickName() {
        return nickName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public List<Integer> getStageScore() {
            return stageScore;
    }

    public void setStageScore(List<Integer> stageScore) {
        this.stageScore = stageScore;
    }

    public int getTotalStarCount() {
        return totalStarCount;
    }

    public void setTotalStarCount(int totalStarCount) {
        this.totalStarCount = totalStarCount;
    }
}
