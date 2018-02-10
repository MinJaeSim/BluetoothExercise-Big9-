package cc.foxtail.funkey.data;


import java.util.List;

public class User {
    private String userName;
    private List<String> friendList;
    private String uid;

    public User(String userName, List<String> friendList, String uid) {
        this.userName = userName;
        this.friendList = friendList;
        this.uid = uid;
    }

    public User() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<String> friendList) {
        this.friendList = friendList;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
