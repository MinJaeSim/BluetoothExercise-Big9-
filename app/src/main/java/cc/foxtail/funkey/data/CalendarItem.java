package cc.foxtail.funkey.data;

public class CalendarItem {
    private String date;
    private int star;
    private String userName;

    public CalendarItem(String date, int star, String userName) {
        this.date = date;
        this.star = star;
        this.userName = userName;
    }

    public CalendarItem() {
    }

    public String getDate() {
        return date;
    }

    public int getStar() {
        return star;
    }

    public String getUserName() {
        return userName;
    }
}
