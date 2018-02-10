package cc.foxtail.funkey;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cc.foxtail.funkey.data.CalendarItem;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

    private List<CalendarItem> calendarItemList = new ArrayList<>();
    private String profileImageUrl;

    @Override
    public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new CalendarViewHolder(inflater.inflate(R.layout.list_item_calendar, parent, false));
    }

    @Override
    public void onBindViewHolder(CalendarViewHolder holder, int position) {
        holder.bindData(calendarItemList.get(position),profileImageUrl);
    }

    @Override
    public int getItemCount() {
        return calendarItemList.size();
    }

    public void setCalendarItemList(List<CalendarItem> calendarItemList) {
        this.calendarItemList = calendarItemList;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
