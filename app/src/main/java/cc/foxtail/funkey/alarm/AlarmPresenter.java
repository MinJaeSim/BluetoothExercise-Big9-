package cc.foxtail.funkey.alarm;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

public class AlarmPresenter implements AlarmContract.Presenter, OnAlarmClickListener {

    private Context context;
    private AlarmContract.View view;
    private InnerAlarmAdapter adapter;
    private static Calendar calendar;
    private static AlarmManager alarmManager;

    private FirebaseFirestore firebaseFirestore;


    public AlarmPresenter(Context context) {
        this.context = context;
        firebaseFirestore = FirebaseFirestore.getInstance();
        calendar = Calendar.getInstance();
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public void setView(AlarmContract.View view) {
        this.view = view;
    }

    @Override
    public void setAdapter(InnerAlarmAdapter alarmAdapter) {
        this.adapter = alarmAdapter;
        this.adapter.setOnAlarmClickListener(this);
    }

    @Override
    public void alarmOnOff(Alarm alarm) {
        Intent alarmIntent = new Intent("android.intent.action.ALARM_START");
        PendingIntent pendingIntent;

        if (!alarm.isOn()) {
            pendingIntent = PendingIntent.getBroadcast(context, alarm.getAlarmId(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
        } else {

            boolean isRepeat = false;
            boolean[] week = new boolean[alarm.getDay().size()];
            int size = 0;
            for (boolean b : alarm.getDay()) {
                if (b)
                    isRepeat = true;
                week[size++] = b;
            }

            String[] time = alarm.getTime().split(":");
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
            calendar.set(Calendar.SECOND, 0);

            int diff = (int) (Calendar.getInstance().getTimeInMillis() - calendar.getTimeInMillis());

            if (isRepeat) {
                alarmIntent.putExtra("repeat", true);
                alarmIntent.putExtra("day", week);

                pendingIntent = PendingIntent.getBroadcast(context, alarm.getAlarmId(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                if (diff >= 0)
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + (24 * 60 * 60 * 1000), AlarmManager.INTERVAL_DAY, pendingIntent);
                else
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            } else {
                alarmIntent.putExtra("repeat", false);
                pendingIntent = PendingIntent.getBroadcast(context, alarm.getAlarmId(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (diff >= 0)
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + (24 * 60 * 60 * 1000), pendingIntent);
                else
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }

    @Override
    public void onClick(@NonNull Alarm alarm, String documentKey) {
        view.showEditAlarmDialog(alarm, documentKey);
    }

    @Override
    public void onLongClick(@NonNull String documentKey) {
        view.showRemoveAlarmDialog(documentKey);
    }

    @Override
    public void onStateChanged(@NonNull Alarm alarm, String documentKey, boolean isOn) {
        alarm.setOn(isOn);

        alarmOnOff(alarm);
        updateAlarm(alarm, documentKey);
    }

    @Override
    public void addAlarm(final Alarm alarm) {
        Query query = firebaseFirestore.collection("Alarm").whereEqualTo("alarmId", alarm.getAlarmId());
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.isEmpty()) {
                    firebaseFirestore.collection("Alarm").add(alarm);
                    alarmOnOff(alarm);
                } else {
                    String documentKey = documentSnapshots.getDocuments().get(0).getId();
                    updateAlarm(alarm, documentKey);
                }
            }
        });
    }

    @Override
    public void setAllAlarmOnOff(boolean isOn) {
        for (DocumentSnapshot d : adapter.getmSnapshots()) {
            Alarm alarm = d.toObject(Alarm.class);
            alarm.setOn(isOn);
            updateAlarm(alarm, d.getId());
        }
    }

    @Override
    public void updateAlarm(Alarm alarm, String documentKey) {
        alarm.setDay(alarm.getDay());
        alarm.setGender(alarm.getGender());
        alarm.setOn(alarm.isOn());
        alarm.setTime(alarm.getTime());
        alarm.setUserName(alarm.getUserName());

        firebaseFirestore.collection("Alarm").document(documentKey).set(alarm);
        alarmOnOff(alarm);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void removeAlarm(String documentKey) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Alarm").document(documentKey).delete();
    }
}
