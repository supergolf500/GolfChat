package com.supergolf500.golfchat;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.supergolf500.golfchat.Chat.ActivityChat;
import com.supergolf500.golfchat.DB.FriendMapping_DA;
import com.supergolf500.golfchat.Entity.FriendChatEntity;
import com.supergolf500.golfchat.Entity.FriendMapping;
import com.supergolf500.golfchat.Entity.MessageEntity;
import com.supergolf500.golfchat.Service.MyGolfService;
import com.supergolf500.golfchat.Service.MyReceiver;
import com.supergolf500.golfchat.Service.TimeCheckService;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private Socket socket;
    Context ctx;
    Toolbar toolbar;
    FloatingActionButton btnAddFriend;


    private int[] tabIcons = {
            R.drawable.tabicon1,
            R.drawable.tabicon2,
            R.drawable.tabicon3,
            R.drawable.tabicon4};


    Fragment_ListFriends fragment_list_friends;
    Fragment_ListChat fragment_list_chat;

    static boolean flagScreenStatup = true;

    public void OnChatNameClick(int Index)
    {

//        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
//                this,
//                imageView,
//                "transitionBtnSend");

        Intent intent = new Intent(this,  ActivityChat.class);
        intent.putExtra("FriendID", fragment_list_chat.listFriendChatEntity.get(Index).FriendID);
        intent.putExtra("EditName", fragment_list_chat.listFriendChatEntity.get(Index).EditName);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        flagScreenStatup = false;
        super.onConfigurationChanged(newConfig);
    }


    public void SetAlarm(Context context)
    {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        Intent i = new Intent(context, TimeCheckService.class);
//        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        Intent i = new Intent(context, MyReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

        am.cancel(pi);

       // am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 5000, pi); // Millisec * Second * Minute
        am.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), 60000, pi);


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = this;
        SetAlarm(ctx);

        if(!flagScreenStatup && toolbar!=null)return;  //---- เพื่อให้สามารถ หมุนจอตะแคงได้

        btnAddFriend = (FloatingActionButton)findViewById(R.id.btnAddFriend);
        btnAddFriend.setTag(0);
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ctx,"FloatingActionButton Add Click",Toast.LENGTH_LONG).show();
            }
        });

        toolbar = (Toolbar) findViewById(R.id.id_toolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Friends");

        ViewPager viewPager = (ViewPager) findViewById(R.id.id_viewpager);

        PagerAdapterMain PageAdapter = new PagerAdapterMain(getSupportFragmentManager());


        fragment_list_friends= new Fragment_ListFriends(ctx);
        for(int i=0;i<30;i++)
        {
            FriendMapping FM=new FriendMapping();
            FM.FriendID="F-" + i;
            FM.EditName = "Akkaraphong";
            fragment_list_friends.addlistFriends(FM);
        }


        fragment_list_chat= new Fragment_ListChat(ctx);
//        for(int i=0;i<10;i++)
//        {
//            FriendChatEntity FC=new FriendChatEntity();
//            FC.FriendID="F-" + i;
//            FC.EditName = "Akkaraphong";
//            FC.LastMessage = "Message xxxxx";
//            FC.LastTimeMessage = new Date();
//            FC.CountMessage = 99;
//            fragment_list_chat.addlistFriendChatEndtity(FC);
//        }
        FriendMapping_DA F_DA =  new FriendMapping_DA(this);
        F_DA.open();
        ArrayList<FriendChatEntity> listFC = F_DA.GetAllFriendChat(GetSharedPreferencesString("UserID"));
        F_DA.close();


        fragment_list_chat.setlistFriendChatEndtity(listFC);


        PageAdapter.addFragment(fragment_list_friends, "Friends");
        PageAdapter.addFragment(fragment_list_chat, "Chat");
        PageAdapter.addFragment(new Fragment_ListFriends(ctx), "Profile");
        PageAdapter.addFragment(new Fragment_ListFriends(ctx), "Setting");
        viewPager.setAdapter(PageAdapter);
        viewPager.setCurrentItem(1);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.id_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);



        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position)
                {
                    case 0:
                        getSupportActionBar().setTitle("Friends");
                        btnAddFriend.setTag(0);
                        btnAddFriend.show();
                        break;
                    case 1:
                        getSupportActionBar().setTitle("Chat");
                        btnAddFriend.setTag(1);
                        btnAddFriend.hide();
                        break;
                    case 2:
                        getSupportActionBar().setTitle("Profile");
                        btnAddFriend.setTag(2);
                        btnAddFriend.hide();
                        break;
                    case 3:
                        getSupportActionBar().setTitle("Setting");
                        btnAddFriend.setTag(3);
                        btnAddFriend.hide();
                        break;
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        //------------------------------------------------------***************************************************
//
//        Message_DA M_DA = new Message_DA(this);
//        M_DA.open();
//        M_DA.InsertMessage("U03","U04",new Date(),new Date(),"TXT","VVVV");
//        ArrayList<MessageEntity> result = M_DA.GetAllMessage();
//        M_DA.close();



////        F_DA.RemoveAll();
//        FriendMapping_DA F_DA =  new FriendMapping_DA(this);
//        F_DA.open();
//
//        FriendChatEntity f1 = new FriendChatEntity();
//        f1.UserID="U09";
//        f1.FriendID = "U1";
//        f1.EditName = "สมศักดิ์";
//
//        FriendChatEntity f2 = new FriendChatEntity();
//        f2.UserID="U09";
//        f2.FriendID = "U2";
//        f2.EditName = "รินดา";
//
//        FriendChatEntity f3 = new FriendChatEntity();
//        f3.UserID="U09";
//        f3.FriendID = "U3";
//        f3.EditName = "ทักษิณ";
//
//        FriendChatEntity f4 = new FriendChatEntity();
//        f4.UserID="U09";
//        f4.FriendID = "U4";
//        f4.EditName = "สุเทพ";
//
//        F_DA.InsertFriendMapping(f1);
//        F_DA.InsertFriendMapping(f2);
//        F_DA.InsertFriendMapping(f3);
//        F_DA.InsertFriendMapping(f4);

//        ArrayList<FriendChatEntity> result = F_DA.GetAllFriendChat();
//        F_DA.close();

        //+++++++++++++++++++++++++++++++++++++++++++++

//        try{
//            socket = IO.socket("http://supergolf500.ddns.net:3000");
//        }catch(URISyntaxException e){
//            throw new RuntimeException(e);
//        }
//
//        socket.connect();
//
//        socket.on("U09", new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//
//               // Gson gson = new Gson();
//                Gson gson = new GsonBuilder()
//                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//                        .create();
//
//                //MessageEntity ddd = gson.fromJson(args[0].toString(), MessageEntity.class);
//                Type listType = new TypeToken<ArrayList<MessageEntity>>() {}.getType();
//                List<MessageEntity> LB = gson.fromJson(args[0].toString(),  listType);
//
//
//                String message =args[0].toString();
//                Log.i("MyService", "Message : " + message);
//
//            }
//        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        menu.add(0,0,0, "Start Service");
        menu.add(0,1,1, "Stop Service");
        menu.add(0,2,2, "Set UserID");
        return  true;

    }


    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intentService;

        switch (item.getItemId())
        {
            case 0:
                if (isServiceRunning(ctx, MyGolfService.class) == false)
                {
                    intentService = new Intent(this, MyGolfService.class);
                    //  bindService(intentService, serviceConnection, Context.BIND_AUTO_CREATE);
                    startService(intentService);
                }
                else
                {
                    Toast.makeText(ctx,"Service ทำงานอยู่แล้ว",Toast.LENGTH_SHORT).show();
                }
                return true;

            case 1:

                if (isServiceRunning(ctx, MyGolfService.class) == false)
                {
                    Toast.makeText(ctx,"Service ปิดอยู่แล้ว",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    intentService = new Intent(this,MyGolfService.class);
                    stopService(intentService);
                }

                return true;
            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Insert UserID");
                final EditText input = new EditText(this);


                input.setText(GetSharedPreferencesString("UserID"));
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SetSharedPreferencesString("UserID",input.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }




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

    @Override
    protected void onResume() {
        super.onResume();


//        MyReceiver re = new MyReceiver();
//        re.SetAlarm(ctx);


        if (isServiceRunning(ctx, MyGolfService.class) == false)
        {
            Intent intentService = new Intent(this, MyGolfService.class);
            //  bindService(intentService, serviceConnection, Context.BIND_AUTO_CREATE);
            startService(intentService);
        }

    }

    //------------------------------------------------------------------
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





}
