package com.victormugo.nsign_media.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class Core {

    public static final String TAG  = "nsign_media";

    public static final String image = "png";
    public static final String video = "mp4";
    public static final String URL_FILE  = "https://media.nsign.tv/media/";
    public static final String FILE_NAME = "NSIGN_Prueba_Android.rar";
    public static final String JSON_FILE = "events.json";

    public static void activateService(Context context, Class<?> className) {
        boolean isRunning = isMyServiceRunning(className, context);
        if (!isRunning) {
            Intent serviceIntent = new Intent(context, className);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
    }

    public static void finishService(Context context, Class<?> className) {
        boolean isRunning = isMyServiceRunning(className, context);
        if (isRunning) {
            context.stopService(new Intent(context, className));
        }
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {

            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }
}
