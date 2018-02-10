package cc.foxtail.funkey.alarm;

public interface AlarmContract {
    interface View {
        void showEditAlarmDialog(Alarm alarm, String documentKey);

        void showRemoveAlarmDialog(String documentKey);
    }

    interface Presenter {
        void setView(View view);

        void setAdapter(InnerAlarmAdapter alarmAdapter);

        void addAlarm(Alarm alarm);

        void updateAlarm(Alarm alarm, String documentKey);

        void alarmOnOff(Alarm alarm);

        void removeAlarm(String documentKey);

        void setAllAlarmOnOff(boolean isOn);
    }
}
