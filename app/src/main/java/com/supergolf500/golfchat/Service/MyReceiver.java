package com.supergolf500.golfchat.Service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by supergolf500 on 15/11/2559.
 */

public class MyReceiver extends BroadcastReceiver {

    private static Intent MyServiceIntent = null;

    boolean IsSetAlarm = false;



    @Override
    public void onReceive(Context context, Intent intent) {


        Toast.makeText(context,"MyReceiver....",Toast.LENGTH_SHORT).show();

        //if(IsSetAlarm==false)
        //{
        //    SetAlarm(context);
        //}

        Log.d("Service Controller", "onReceiver");
        if (isServiceRunning(context, MyGolfService.class) == false) {

            MyServiceIntent = new Intent(context,MyGolfService.class);
            context.startService(MyServiceIntent);
        }
    }



    public void SetAlarm(Context context)
    {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, TimeCheckService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

        //am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 5000, pi); // Millisec * Second * Minute
        am.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), 60000, pi);

        IsSetAlarm=true;


    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, MyReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        IsSetAlarm=false;
    }







    // this method is very important
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

    public static Intent getServiceIntent() {
        return MyServiceIntent;
    }

}
