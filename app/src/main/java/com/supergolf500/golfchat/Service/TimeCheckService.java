package com.supergolf500.golfchat.Service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by supergolf500 on 22/11/2559.
 */

public class TimeCheckService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Toast.makeText(getBaseContext(),"TimeCheckService Time",Toast.LENGTH_SHORT).show();

        return super.onStartCommand(intent, flags, startId);
        //return START_NOT_STICKY;
    }

    //
//
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//
//        if (isServiceRunning(getBaseContext(), MyGolfService.class) == false) {
//
//            Toast.makeText(getBaseContext(),"TimeCheckService Check = False",Toast.LENGTH_LONG).show();
////            Intent MyServiceIntent = new Intent(getBaseContext(),MyGolfService.class);
////            startService(MyServiceIntent);
//        }
//        else
//        {
//            Toast.makeText(getBaseContext(),"TimeCheckService Check = True",Toast.LENGTH_LONG).show();
//        }
//
//        return START_NOT_STICKY;
//    }


    private boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : am
                .getRunningServices(Integer.MAX_VALUE)) {
            String className1 = serviceInfo.service.getClassName();
            String className2 = serviceClass.getName();
            if (className1.equals(className2)) {
                return true;
            }
        }
        return false;
    }

}
