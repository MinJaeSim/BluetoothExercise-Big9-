package cc.foxtail.funkey.alarm;

import android.support.annotation.NonNull;

public interface OnAlarmClickListener {
    void onClick(@NonNull Alarm alarm, String documentKey);
    void onLongClick(@NonNull String documentKey);
    void onStateChanged(@NonNull Alarm alarm, String documentKey, boolean isOn);
}
