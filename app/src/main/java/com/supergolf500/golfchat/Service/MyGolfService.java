package com.supergolf500.golfchat.Service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.supergolf500.golfchat.Chat.ActivityChat;
import com.supergolf500.golfchat.DB.FriendMapping_DA;
import com.supergolf500.golfchat.DB.Message_DA;
import com.supergolf500.golfchat.Entity.FriendChatEntity;
import com.supergolf500.golfchat.Entity.MessageEntity;
import com.supergolf500.golfchat.MainActivity;
import com.supergolf500.golfchat.R;
import com.supergolf500.golfchat.WebAccess.WebMessage;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by supergolf500 on 15/11/2559.
 */

public class MyGolfService extends Service {
    Context mCtx;
    public Context activityChatCtx;
    private SocketThead thread1;
    private final IBinder mBinder = new LocalGolfService();

    String UserID="";

    LocationManager locationManager;
    LocationListener GpsListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalGolfService extends Binder {
        public MyGolfService getService() {
            return MyGolfService.this;


        }
    }

    //
    public void handleUncaughtException(Thread thread, Throwable e) {
        System.exit(1); // kill off the crashed app
    }


    @Override
    public void onCreate() {

        //Toast.makeText(this,"MyGolfService onCreate...",Toast.LENGTH_SHORT).show();

        thread1 = new SocketThead(this);
        mCtx = this;

        UserID = GetSharedPreferencesString("UserID");

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                handleUncaughtException(thread, e);
            }
        });

    }


    ArrayList<String> listFriendGetMyGPS=new ArrayList<>();
    String SS;
    Boolean isGpsScanning=false;
    double latitude_;
    double longitude_;

    void CheckLocation() {

        isGpsScanning = true;
        locationManager = (LocationManager) mCtx.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            SS = "GPS " + String.valueOf(latitude) + "," + String.valueOf(longitude);
            Handler mainHandler = new Handler(mCtx.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mCtx, SS, Toast.LENGTH_SHORT).show();
                }
            };
            mainHandler.post(myRunnable);

            BotSendLocation(latitude,longitude);

        } else {
            long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
            long MIN_TIME_BW_UPDATES = 1000 * 5; // Sec

            GpsListener = new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    SS = "GPS Update" + String.valueOf(latitude) + "," + String.valueOf(longitude);
                    Handler mainHandler = new Handler(mCtx.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mCtx, SS, Toast.LENGTH_SHORT).show();
                        }
                    };
                    mainHandler.post(myRunnable);

                    BotSendLocation(latitude,longitude);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }

            };

            try {

                Handler mainHandler = new Handler(mCtx.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                5000,
                                10, GpsListener);

                    }
                };
                mainHandler.post(myRunnable);

            }
            catch (Exception e)
            {
                String dd = e.getMessage();
            }
        }

    }


    void BotSendLocation(double latitude,double longitude)
    {
        latitude_ =latitude;
        longitude_= longitude;

        if (GpsListener != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(GpsListener);
            GpsListener = null;

            Handler mainHandler = new Handler(mCtx.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mCtx, "Close GpsListener ", Toast.LENGTH_SHORT).show();
                }
            };
            mainHandler.post(myRunnable);
        }

        //-----  Web Send

        Handler mainHandler = new Handler(mCtx.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run()
            {
                for(int i=0;i<listFriendGetMyGPS.size();i++)
                {
                    try {
                        if (activityChatCtx != null) {
                            new WebMessage(activityChatCtx).sendMessage(UserID, listFriendGetMyGPS.get(i), "AUTO_GPS", "ส่งอัตโนมัติ", String.valueOf(latitude_), String.valueOf(longitude_),true);

                        }
                        else
                        {
                            new WebMessage(mCtx).sendMessage(UserID, listFriendGetMyGPS.get(i), "AUTO_GPS", "ส่งอัตโนมัติ", String.valueOf(latitude_), String.valueOf(longitude_),false);
                        }
                    }
                    catch (Exception e)
                    {
                        String dd = e.getMessage();
                    }

                }
                listFriendGetMyGPS.clear();
                isGpsScanning=false;
            }
        };
        mainHandler.post(myRunnable);

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        Toast.makeText(this,"MyGolfService Started...",Toast.LENGTH_SHORT).show();
        Log.i("MyGolfService", "MyGolfService StartUp");

        if(!thread1.isAlive())
        {
            thread1.start();
        }
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Toast.makeText(this,"MyGolfService Destroy...",Toast.LENGTH_SHORT).show();
        super.onDestroy();

    }


    @Override
    @Deprecated
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        System.out.println("Service started");
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                System.out.println("Service is running");
            }
        }, 5000);
    }


    //---------------------------------------------------------------------------------------------


    final String PreferencesName = "Config_GolfChat";

    public void SetSharedPreferencesString(String key,String value)
    {
        SharedPreferences sp = getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public String GetSharedPreferencesString(String key)
    {
        SharedPreferences sp = getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        String value = sp.getString(key, "");
        return value;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    private class SocketThead extends Thread
    {

        Context ctx;
//        String UserID="";

        boolean isConnected=false;

        private Socket socket;
        public SocketThead(Context context_)
        {
            ctx = context_;
            try{
                socket = IO.socket("http://supergolf500.ddns.net:3000");
            }catch(URISyntaxException e){
                throw new RuntimeException(e);
            }

//            UserID = GetSharedPreferencesString("UserID");
            isLoop=true;
            loopPing();
        }



        boolean isLoop=false;
        Thread loopThread;

        void loopPing()
        {
            loopThread =  new Thread(new Runnable() {
                public void run() {
                    // TODO Auto-generated method stub
                    while (isLoop) {
                        try
                        {
                            Thread.sleep(10000);
                            if(isConnected)
                            {
                                Log.i("Ping", "Ping :" + UserID);
                                socket.emit("ping", "Ping :" + UserID, new Ack() {
                                    @Override
                                    public void call(Object... args) {

                                        Log.i("Ping", "recieved ping callback");
                                    }
                                });
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            loopThread.start();
        }

        private synchronized void stopThread()
        {
            isLoop=false;
            if (loopThread != null)
            {
                loopThread = null;
            }
        }



        @Override
        public void run() {

            if(!UserID.equals("")) {

                socket.connect();

                socket.on(UserID, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        String message = args[0].toString();
                        Log.i("MyService", "Message : " + message);
//                    GolfValue = message;
//                    CallApp(message);

                        Gson gson = new GsonBuilder()
                                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                .create();

                        //MessageEntity ddd = gson.fromJson(args[0].toString(), MessageEntity.class);
                        Type listType = new TypeToken<ArrayList<MessageEntity>>() {
                        }.getType();
                        ArrayList<MessageEntity> LB = gson.fromJson(message, listType);

                        ArrayList<MessageEntity> LB_NewMessage = new ArrayList<MessageEntity>();

                        Message_DA M_DA = new Message_DA(ctx);
                        M_DA.open();
                        for(int i=0;i<LB.size();i++) {
                            if(M_DA.CheckExistMessage(LB.get(i).RunNo))
                            {
                                //--- มีใน DB แล้ว

                            }
                            else
                            {
                                //----- ยังไม่มีใน DB
                                M_DA.InsertMessage(LB.get(i));
                                LB_NewMessage.add(LB.get(i));

                                //----- เก็บข้อมูล Id ผู้ที่ขอตำแหน่ง GPS
                                if(LB.get(i).value.equals("GETGPS") && !LB.get(i).MessageFrom.equals(UserID))
                                {
                                    Boolean same=false;
                                    for(int j=0;j<listFriendGetMyGPS.size();j++)
                                    {
                                        if(LB.get(i).MessageFrom.equals(listFriendGetMyGPS.get(j)))
                                        {
                                            same = true;
                                            break;
                                        }
                                    }
                                    if(same==false) {
                                        listFriendGetMyGPS.add(LB.get(i).MessageFrom);
                                    }
                                }
                            }

                        }
                        M_DA.close();

                        if(isGpsScanning==false && listFriendGetMyGPS.size()>0)
                        {
                            CheckLocation();
                        }

                        String FriendID = GetSharedPreferencesString("FocusFriendID");


                        if(mOnGolfReceiveMessage != null)
                        {
                            if(LB_NewMessage.size()>0)
                            {
                                if(LB_NewMessage.get(LB_NewMessage.size() - 1).MessageFrom.equals(FriendID))
                                {
                                    mOnGolfReceiveMessage.onGolfReceiveMessageHandler(LB);
                                }
                                else
                                {
                                    ProcessDataForNotification(LB_NewMessage);
                                    //ShowNotification(LB_NewMessage.get(LB_NewMessage.size() - 1).RunNo, LB_NewMessage.get(LB_NewMessage.size() - 1).MessageFrom, LB_NewMessage.get(LB_NewMessage.size() - 1).value);
                                }
                            }

                        }
                        else {

                            if(LB_NewMessage.size()>0) {
                                ProcessDataForNotification(LB_NewMessage);
                                //ShowNotification(LB_NewMessage.get(LB_NewMessage.size() - 1).RunNo, LB_NewMessage.get(LB_NewMessage.size() - 1).MessageFrom, LB_NewMessage.get(LB_NewMessage.size() - 1).value);
                            }
                        }




                    }
                });
                socket.on(UserID + "_Read", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {  //---- ไว้รับข้อมูลเวลาคนอ่าน

                        String message = args[0].toString();

                        Gson gson = new Gson();
                        //MessageEntity ddd = gson.fromJson(args[0].toString(), MessageEntity.class);

                        Type listType = new TypeToken<ArrayList<Integer>>() {}.getType();
                        ArrayList<Integer> LB = gson.fromJson(message, listType);


                        if(LB.size()>0)
                        {
                            Message_DA M_DA = new Message_DA(ctx);
                            M_DA.open();
                            M_DA.UpdateReadTime(LB);
                            M_DA.close();

                            if(mOnGolfSaveReadMessage != null) {
                                mOnGolfSaveReadMessage.onGolfSaveReadMessageHandler(LB);
                            }

                        }
                    }
                });

                socket.on(com.github.nkzawa.socketio.client.Socket.EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {

                        Log.i("MyGolfService", "Message : " + "Socket.IO Connected ^_^");

                        isConnected = true;

                        Handler mainHandler = new Handler(ctx.getMainLooper());
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run()
                            {
                                Toast.makeText(ctx,"Socket.IO Connected ^_^",Toast.LENGTH_SHORT).show();

                                WebMessage Web = new WebMessage(ctx);
                                Web.GetUnReadMessage(UserID);
                            }
                        };
                        mainHandler.post(myRunnable);





                    }
                });


                socket.on(com.github.nkzawa.socketio.client.Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {

                        Log.i("MyGolfService", "Message : " + "Socket.IO DisConnect T_T");

                        isConnected = false;

                        Handler mainHandler = new Handler(ctx.getMainLooper());
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run()
                            {
                                Toast.makeText(ctx,"Socket.IO DisConnect T_T",Toast.LENGTH_SHORT).show();
                            }
                        };
                        mainHandler.post(myRunnable);

                    }
                });
            }


        }

    }

    void ProcessDataForNotification(ArrayList<MessageEntity> LB_NewMessage )
    {


        ArrayList<String> ListFriend=new ArrayList<>();

        for(int i=0;i<LB_NewMessage.size();i++)
        {
            //---- ถ้าขอพิกัดไม่ต้องแจ้งเตือน
            if(LB_NewMessage.get(i).MessageType.equals("AUTO_TXT") && LB_NewMessage.get(i).value.equals("GETGPS"))
            {
                LB_NewMessage.remove(i);
                i--;
            }

        }


        for(int i=0;i<LB_NewMessage.size();i++)   //----- เพื่อแยกว่า Message มาจากกี่คน  ใครบ้าง
        {
            boolean same=false;
            for(int j=0;j<ListFriend.size();j++)
            {

                if(LB_NewMessage.get(i).MessageFrom.equals(ListFriend.get(j)))
                {
                   same = true;
                    break;
                }
            }

            if(!same)ListFriend.add(LB_NewMessage.get(i).MessageFrom);

        }


        for(int i=0;i<ListFriend.size();i++)
        {
            int count = 0;
            String lastMessage = "";

            for(int j=0;j<LB_NewMessage.size();j++)
            {
                if(ListFriend.get(i).equals(LB_NewMessage.get(j).MessageFrom))
                {
                    count += 1;
                    lastMessage = LB_NewMessage.get(j).value;
                }

                ShowNotification(ListFriend.get(i),lastMessage);
            }



        }


    }



    void ShowNotification(String FriendID,String message )
    {


        Bitmap bitmap;
        try
        {
            bitmap = getCircleBitmap(getBitmapFromURL("http://supergolf500.ddns.net:3000/image/" + FriendID + ".jpg"));
        }catch (Exception e)
        {
            bitmap= null;
        }

        FriendMapping_DA F_DA = new FriendMapping_DA(mCtx);
        F_DA.open();
        FriendChatEntity FriendResult =  F_DA.GetFriendMapping(FriendID);
        F_DA.close();



        NotificationCompat.Builder mNotifi = new NotificationCompat.Builder(mCtx);

        mNotifi.setSmallIcon(R.drawable.mini_icon);
        if(bitmap != null)
        {
            mNotifi.setLargeIcon(bitmap);
        }

        //mNotifi.setStyle(new NotificationCompat.BigTextStyle().bigText("xxxxxxxxxxxxx 1"));

        Message_DA M_DA = new Message_DA(mCtx);
        M_DA.open();
        int count = M_DA.GetCountMessageUnread(FriendID);
        M_DA.close();

        mNotifi.setContentTitle(FriendResult.EditName + " : " + count + " ข้อความ");
        mNotifi.setContentText(message);
        mNotifi.setWhen(System.currentTimeMillis());
        mNotifi.setAutoCancel(true);
        mNotifi.setLights(Color.YELLOW, 500, 500);
        long[] pattern = {200,500};
        mNotifi.setVibrate(pattern);

        //Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri alarmSound=Uri.parse("android.resource://"+getPackageName()+"/raw/notification_sound");
        mNotifi.setSound(alarmSound);

        Intent resultIntent = new Intent(this, ActivityChat.class);

        resultIntent.putExtra("FriendID", FriendID);
        resultIntent.putExtra("EditName", new Date());
        resultIntent.putExtra("Reload", true);

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resultIntent.setAction(Intent.ACTION_MAIN);


        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx,FriendResult.FriendKey, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);//FLAG_CANCEL_CURRENT,FLAG_UPDATE_CURRENT

        mNotifi.setContentIntent(pendingIntent);

        NotificationManager NotiManag =(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        NotiManag.notify(FriendResult.FriendKey,mNotifi.build());
    }


    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }





    //--------------- Handler ----------------------------------------------------------------------

    public interface OnGolfReceiveMessage
    {
        void onGolfReceiveMessageHandler( ArrayList<MessageEntity> listMessage);
    }

    public OnGolfReceiveMessage mOnGolfReceiveMessage;

    public void setGolfReceiveMessageHandlerListener(OnGolfReceiveMessage l)
    {
        mOnGolfReceiveMessage=l;
    }
    //--------------- Handler ----------------------------------------------------------------------

    public interface OnGolfSaveReadMessage
    {
        void onGolfSaveReadMessageHandler( ArrayList<Integer> listRunNo);
    }

    public OnGolfSaveReadMessage mOnGolfSaveReadMessage;

    public void setGolfSaveReadMessageHandlerListener(OnGolfSaveReadMessage l)
    {
        mOnGolfSaveReadMessage=l;
    }






}
