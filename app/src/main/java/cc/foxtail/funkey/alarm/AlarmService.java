package cc.foxtail.funkey.alarm;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import cc.foxtail.funkey.R;

public class AlarmService extends Service {

    public AlarmService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Alarm Service");

        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamVolume(AudioManager.STREAM_ALARM), 0);

        MediaPlayer soundPlay = MediaPlayer.create(getApplicationContext(), R.raw.alarm);
        soundPlay.start();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
