package com.supergolf500.golfchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.supergolf500.golfchat.Chat.ActivityChat;
import com.supergolf500.golfchat.DB.FriendMapping_DA;
import com.supergolf500.golfchat.Entity.FriendChatEntity;
import com.supergolf500.golfchat.ViewHolder.ListFriensChatViewAdapter;

import java.util.ArrayList;


public class SharedActivity extends AppCompatActivity {


    RecyclerView recyclerView1;
    ListFriensChatViewAdapter listAdt;
    Context ctx;
    TextView textView1;


    String SharedType;
    String SharedValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.shared_activity);
        ctx = this;

        textView1= (TextView)findViewById(R.id.textView1);


        FriendMapping_DA F_DA =  new FriendMapping_DA(this);
        F_DA.open();
        ArrayList<FriendChatEntity> listFC = F_DA.GetAllFriendChat(GetSharedPreferencesString("UserID"));
        F_DA.close();

        listAdt = new ListFriensChatViewAdapter(ctx,listFC,"SharedActivity");

        recyclerView1 = (RecyclerView)findViewById(R.id.recyclerView1);
        recyclerView1.setLayoutManager(new LinearLayoutManager(ctx));

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(500);
        recyclerView1.setItemAnimator(itemAnimator);
        recyclerView1.setAdapter(listAdt);





        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null)
        {
            if ("text/plain".equals(type))
            {
                handleSendText(intent); // Handle text being sent
            }
            else if (type.startsWith("image/"))
            {
                handleSendImage(intent); // Handle single image being sent
            }
        }
        else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null)
        {
            if (type.startsWith("image/"))
            {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        }
        else
        {
            // Handle other intents, such as being started from the home screen
        }

    }



    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
            //textView1.setText("Shared Text : " + sharedText);

            SharedType = "TXT";
            SharedValue = sharedText;

        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
            String path = getRealPathFromURI(imageUri);
            //textView1.setText("Shared SingleImage : " + path );

            SharedType = "IMG";
            SharedValue = path;
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
            //textView1.setText("Shared MultipleImages : Count = " + imageUris.size());
        }
    }


    public void OnChatNameClick(int Index)
    {

       // listAdt.listFriendChatEntity.get(Index);


        Intent intent = new Intent(this,  ActivityChat.class);
        intent.putExtra("FriendID", listAdt.listFriendChatEntity.get(Index).FriendID);
        intent.putExtra("EditName", listAdt.listFriendChatEntity.get(Index).EditName);

        intent.putExtra("SharedType", SharedType);
        intent.putExtra("SharedValue", SharedValue);



        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.setAction(Intent.ACTION_MAIN);

        startActivity(intent);
        finish();


    }


    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
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
