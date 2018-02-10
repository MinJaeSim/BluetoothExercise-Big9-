package cc.foxtail.funkey.alarm;


import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import cc.foxtail.funkey.FireStoreAdapter;
import cc.foxtail.funkey.R;
import cc.foxtail.funkey.data.Child;

public class AlarmAdapter extends FireStoreAdapter<AlarmAdapter.AlarmViewHolder> {

    private InnerAlarmAdapter innerAlarmAdapter;
    private AlarmContract.Presenter presenter;

    public AlarmAdapter(Query query) {
        super(query);
    }

    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_item_alarm_holder, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AlarmViewHolder holder, final int position) {
        final Child child = getSnapshot(position).toObject(Child.class);

        holder.userName.setText(child.getUserName() + "'s Alarm");
        Query query = FirebaseFirestore.getInstance().collection("Alarm").whereEqualTo("userName", child.getUserName());
        innerAlarmAdapter = new InnerAlarmAdapter(query);
        holder.setAdapter(innerAlarmAdapter);
        presenter.setAdapter(innerAlarmAdapter);
    }

    public void setPresenter(AlarmPresenter presenter) {
        this.presenter = presenter;
    }


    static class AlarmViewHolder extends RecyclerView.ViewHolder {

        private TextView userName;
        private RecyclerView innerRecyclerView;

        public AlarmViewHolder(View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.calendar_day_text_view);

            innerRecyclerView = itemView.findViewById(R.id.alarm_inner_recycler_view);
            LinearLayoutManager alarmLayoutManager = new LinearLayoutManager(itemView.getContext());
            alarmLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(innerRecyclerView.getContext(), alarmLayoutManager.getOrientation());
            innerRecyclerView.addItemDecoration(dividerItemDecoration);

            innerRecyclerView.setLayoutManager(alarmLayoutManager);
        }

        public void setAdapter(InnerAlarmAdapter adapter) {
            innerRecyclerView.setAdapter(adapter);
        }
    }
}
