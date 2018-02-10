package cc.foxtail.funkey.alarm;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.firestore.Query;

import cc.foxtail.funkey.FireStoreAdapter;
import cc.foxtail.funkey.R;

public class InnerAlarmAdapter extends FireStoreAdapter<InnerAlarmAdapter.InnerAlarmViewHolder> {

    private OnAlarmClickListener onAlarmClickListener;

    public void setOnAlarmClickListener(OnAlarmClickListener onAlarmClickListener) {
        this.onAlarmClickListener = onAlarmClickListener;
    }

    public InnerAlarmAdapter(Query query) {
        super(query);
    }

    @Override
    public InnerAlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_item_alarm, parent, false);
        return new InnerAlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InnerAlarmViewHolder holder, int position) {
        final Alarm alarm = getSnapshot(position).toObject(Alarm.class);
        final String documentKey = getSnapshot(position).getId();
        holder.timeTextView.setText(alarm.getTime());

//        holder.alarmTime.setText(alarm.getTime());
//        StringBuilder alarmDayString = new StringBuilder();

//        holder.alarmDay.setText(alarmDayString.toString());
//        holder.alarmSwitch.setChecked(alarm.isOn());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onAlarmClickListener != null) {
                    onAlarmClickListener.onClick(alarm, documentKey);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (onAlarmClickListener != null) {
                    onAlarmClickListener.onLongClick(documentKey);
                }
                return true;
            }
        });

        for (int i = 1; i < alarm.getDay().size(); i++) {
            holder.days[i - 1].setChecked(alarm.getDay().get(i));
        }
    }


    static class InnerAlarmViewHolder extends RecyclerView.ViewHolder {

        private TextView timeTextView;
        private CheckBox sun = itemView.findViewById(R.id.sunday_button);
        private CheckBox mon = itemView.findViewById(R.id.monday_button);
        private CheckBox tue = itemView.findViewById(R.id.tuesday_button);
        private CheckBox wed = itemView.findViewById(R.id.wednesday_button);
        private CheckBox thu = itemView.findViewById(R.id.thursday_button);
        private CheckBox fri = itemView.findViewById(R.id.friday_button);
        private CheckBox sat = itemView.findViewById(R.id.saturday_button);
        private CheckBox[] days = {sun, mon, tue, wed, thu, fri, sat};

        public InnerAlarmViewHolder(View itemView) {
            super(itemView);

            timeTextView = itemView.findViewById(R.id.time_text_view);
            sun = itemView.findViewById(R.id.sunday_button);
            sun.setClickable(false);
            mon = itemView.findViewById(R.id.monday_button);
            mon.setClickable(false);
            tue = itemView.findViewById(R.id.tuesday_button);
            tue.setClickable(false);
            wed = itemView.findViewById(R.id.wednesday_button);
            wed.setClickable(false);
            thu = itemView.findViewById(R.id.thursday_button);
            thu.setClickable(false);
            fri = itemView.findViewById(R.id.friday_button);
            fri.setClickable(false);
            sat = itemView.findViewById(R.id.saturday_button);
            sat.setClickable(false);
        }
    }
}
