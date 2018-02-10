package cc.foxtail.funkey.alarm;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Alarm Receiver");

        Bundle extra = intent.getExtras();
        if (extra != null) {
            boolean isRepeat = extra.getBoolean("repeat");
            if (isRepeat) {
                boolean[] week = extra.getBooleanArray("day");
                Calendar cal = Calendar.getInstance();
                if ((week != null && week[cal.get(Calendar.DAY_OF_WEEK)])) {
                    Intent alarmIntent = new Intent(context, AlarmService.class);
                    context.startService(alarmIntent);
                }
            } else {
                Intent alarmIntent = new Intent(context, AlarmService.class);
                context.startService(alarmIntent);
            }
        }
    }
}
