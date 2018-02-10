package cc.foxtail.funkey;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Objects;

import cc.foxtail.funkey.childAdd.ChildAddFragment;
import cc.foxtail.funkey.data.CalendarItem;

import static cc.foxtail.funkey.navigation.NavigationActivity.soundId;
import static cc.foxtail.funkey.navigation.NavigationActivity.soundPool;


public class CalendarViewHolder extends RecyclerView.ViewHolder {

    private TextView monthTextView;
    private TextView dayTextView;
    private int starCount;
    private String profileImageUrl;
    private String userName;
    private String title;

    public CalendarViewHolder(View itemView) {
        super(itemView);

        monthTextView = itemView.findViewById(R.id.calendar_month_text_view);
        dayTextView = itemView.findViewById(R.id.calendar_day_text_view);

        final Context context = itemView.getContext();
        final FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(soundId, 1, 1, 0, 0, 1);
                fm.beginTransaction()
                        .replace(R.id.content_fragment, StarFragment.newInstance(title,userName,profileImageUrl,starCount))
                        .addToBackStack(null).commit();
            }
        });

    }

    public void bindData(CalendarItem calendarItem, String profileImageUrl) {
        this.starCount = calendarItem.getStar();
        this.profileImageUrl = profileImageUrl;
        this.userName = calendarItem.getUserName();
        String date = calendarItem.getDate();
        String month = date.substring(2, 4);
        String day = date.substring(4, 6);
        title = month + "월 " + day + "일의 기록";

        if(Objects.equals(month, "01"))
            month = "JAN";
        else if (Objects.equals(month, "02"))
            month = "FEB";
        else if (Objects.equals(month, "03"))
            month = "MAR";
        else if (Objects.equals(month, "04"))
            month = "API";
        else if (Objects.equals(month, "05"))
            month = "MAY";
        else if (Objects.equals(month, "06"))
            month = "JUN";
        else if (Objects.equals(month, "07"))
            month = "JUL";
        else if (Objects.equals(month, "08"))
            month = "AUG";
        else if (Objects.equals(month, "09"))
            month = "SEP";
        else if (Objects.equals(month, "10"))
            month = "OCT";
        else if (Objects.equals(month, "11"))
            month = "NOV";
        else if (Objects.equals(month, "12"))
            month = "DEC";
        else
            month = "NOV";

        monthTextView.setText(month);
        dayTextView.setText(day);

    }
}
