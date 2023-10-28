package com.victormugo.nsign_media.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.victormugo.nsign_media.R;
import com.victormugo.nsign_media.activities.Core;
import com.victormugo.nsign_media.activities.MainActivity;
import com.victormugo.nsign_media.api.models.VoMedia;
import com.victormugo.nsign_media.api.models.VoPlaylists;
import com.victormugo.nsign_media.api.models.VoResource;
import com.victormugo.nsign_media.bus.IntentServiceResult;
import com.victormugo.nsign_media.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

public class LoadMediaData extends Service {

    private Timer _timer = null;
    private TimerTask _timerTask = null;

    public VoMedia voMedia = null;

    public LoadMediaData() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground();

        // Cargar datos del fichero Assets en las clases correspondientes
        voMedia = Utils.loadJSONFromAsset(getBaseContext());

        // Ejecutar el timerTask
        Log.d(Core.TAG, "-------------------> voMedia.getPlaylists().get(0).getResources().get(0).getDuration(): " + voMedia.getPlaylists().get(0).getResources().get(0).getDuration());
        reScheduleTimer(voMedia.getPlaylists().get(0).getResources().get(0).getDuration());

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (_timer != null) _timer.cancel();

        stopForeground(true);
    }

    public void reScheduleTimer(long duration) {
        duration *= 1000;
        Log.d(Core.TAG, "-----------------> duration reScheduleTimer: " + duration);

        if (_timer != null) _timer.cancel();

        _timer = new Timer("timer",true);
        _timerTask = new MyTimerTask();
        _timer.schedule(_timerTask, duration, 2000L);
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            Log.d(Core.TAG, "-------------------> backGround Service is running .....");

            // Lectura de los objetos
            IntentServiceResult intentServiceResult = Utils.loadNextMediaFile(voMedia);

            if (intentServiceResult != null) {

                Log.d(Core.TAG, "-----------> resource: " + intentServiceResult.getResource().getName());
                Log.d(Core.TAG, "-----------> duration: " + intentServiceResult.getResource().getDuration());

                // Enviar a la actividad principal el recurso a mostrar
                //EventBus.getDefault().post(resource);
                EventBus.getDefault().post(intentServiceResult);

                // Reprogramar el timertask para el siguiente recurso
                reScheduleTimer(intentServiceResult.getResource().getDuration());

            } else {
                stopForeground(true);
            }
        }
    }

    public void startForeground() {

        final String CHANNEL_ID = "background service";

        try {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            int icon = R.drawable.ic_launcher_background;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_LOW);

                notificationChannel.setDescription("Descripcion");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableVibration(false);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            NotificationCompat.Builder builder = new NotificationCompat
                    .Builder(getApplicationContext(), CHANNEL_ID);

            Notification notification = builder
                    .setContentIntent(PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_IMMUTABLE))
                    .setSmallIcon(icon)
                    .setTicker(getApplicationContext().getString(R.string.async_notifications_message))
                    .setWhen(1000 * 60)
                    .setAutoCancel(true)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("")
                    .build();

            startForeground(1337, notification);

        } catch (Exception e) {
            e.getMessage();
        }
    }
}