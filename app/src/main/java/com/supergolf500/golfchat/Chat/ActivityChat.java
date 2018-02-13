package com.supergolf500.golfchat.Chat;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.supergolf500.golfchat.DB.FriendMapping_DA;
import com.supergolf500.golfchat.DB.Message_DA;
import com.supergolf500.golfchat.Entity.FriendChatEntity;
import com.supergolf500.golfchat.Entity.MessageEntity;
import com.supergolf500.golfchat.MainActivity;
import com.supergolf500.golfchat.MapsActivity;
import com.supergolf500.golfchat.R;
import com.supergolf500.golfchat.Service.MyGolfService;
import com.supergolf500.golfchat.Utility.ImageConverter;
import com.supergolf500.golfchat.WebAccess.WebMessage;


import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
public class ActivityChat extends AppCompatActivity {




    Context ctx;
    EditText edtMessage;
    ImageView btnSend, btnOption;
    String FriendID;
    FriendChatEntity FriendResult;

    String UserID;
    WebMessage webMessage;

    ListItemMessageAdapter listAdt;
    RecyclerView recyclerViewChat;

    MyGolfService myGolfService;
    boolean isBind = false;
    int MaxRunNo = 0;
    static final int CAMERA_REQUEST = 4; // Shoot CAMERA
    static final int GALLERY_REQUEST = 3;  // The request code GALLERY
    static final int MAPDATA_REQUEST = 2;  // The request code MAP
    static final int SPEAK_TO_TEXT_REQUEST = 1;  // The request code SPEAK

    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.supergolf500.golfchat.fileprovider";

    ProgressDialog PD;

    Uri uri_camera;

    Bundle bundle;


    public void handleUncaughtException (Thread thread, Throwable e)
    {
        System.exit(1); // kill off the crashed app
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException (Thread thread, Throwable e)
            {
                handleUncaughtException (thread, e);
            }
        });

        ctx = this;


        //PD = ProgressDialog.show(ctx, "dialog title", "dialog message", true);

//        Button button2 = (Button)findViewById(R.id.button2);
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//               int g= Integer.parseInt("dddd");   //---- Create Error
//
//            }
//        });


        webMessage = new WebMessage(ctx);
        UserID = GetSharedPreferencesString("UserID");


        final BottomSheetDialog BTSheet_Dialog = new BottomSheetDialog(ctx);
        View BTSheet_View = getLayoutInflater().inflate(R.layout.buttom_sheet, null);
        BTSheet_Dialog.setContentView(BTSheet_View);


        btnOption = (ImageView) findViewById(R.id.btnOption);
        btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //---- Hide Keyboard--------------------------------------------
                InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), SPEAK_TO_TEXT_REQUEST);
                BTSheet_Dialog.show();
            }
        });

        BTSheet_View.findViewById(R.id.imgGps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BTSheet_Dialog.hide();
                Intent intent = new Intent(ctx,  MapsActivity.class);
                //intent.putExtra("Para",999);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //startActivity(intent);
                startActivityForResult(intent,MAPDATA_REQUEST);

            }
        });

        BTSheet_View.findViewById(R.id.imgFolder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BTSheet_Dialog.hide();

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST);


            }
        });

        BTSheet_View.findViewById(R.id.imgFriendGps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BTSheet_Dialog.hide();

                //---- Send GetFriend GPS
                webMessage.sendMessage(UserID,FriendID,"AUTO_TXT","GETGPS","","",true);


            }
        });

        BTSheet_View.findViewById(R.id.imgCemera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BTSheet_Dialog.hide();

                try {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    String timeStamp =
//                            new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//                    String imageCameraFileName = "IMG_" + timeStamp + ".jpg";


                    File f3=new File(Environment.getExternalStorageDirectory()+"/DCIM/Camera2/");
                    if(!f3.exists()){
                        f3.mkdirs();
                    }

                    File fff = new File(Environment.getExternalStorageDirectory(), "DCIM/Camera2/" + "Captured.jpg");
                    fff.delete();

                    File f = new File(Environment.getExternalStorageDirectory(), "DCIM/Camera2/" + "Captured.jpg");
                    uri_camera = Uri.fromFile(f);

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri_camera);
                    startActivityForResult(intent, CAMERA_REQUEST);

                }
                catch (Exception e)
                {
                    Toast.makeText(ctx,e.getMessage(),Toast.LENGTH_SHORT).show();
                }



            }
        });




        //-----------ทำเพื่อไม่ให้ Slide ลงมาแล้ว BottomSheetDialog หายไปเลย   เป็นคำสั่ง ห้าม Slide
        final BottomSheetBehavior behavior = BottomSheetBehavior.from((View) BTSheet_View.getParent());
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });





        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ctx,  MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSend = (ImageView)findViewById(R.id.btnSend);
        btnSend.setTag(0);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(btnSend.getTag().toString().equals("0"))
                {
                    Toast.makeText(ctx,"microphone Click",Toast.LENGTH_LONG).show();

                    //onSearchRequested();
                    Intent voiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
                    try {
                        startActivityForResult(voiceIntent, SPEAK_TO_TEXT_REQUEST);
                    } catch (ActivityNotFoundException ex) {
                        //DebugLog.e(TAG, "Not found excpetion onKeyDown: " + ex);
                    }
                }
                else
                {
                    //Toast.makeText(ctx,"Send Click",Toast.LENGTH_LONG).show();
                    webMessage.sendMessage(UserID,FriendID,"TXT",edtMessage.getText().toString(),"","",true);
                    edtMessage.setText("");

                }

            }
        });


        edtMessage = (EditText)findViewById(R.id.edtMessage);
        edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(TextUtils.isEmpty(edtMessage.getText()))
                {
                    btnSend.setImageResource(R.drawable.microphone);
                    btnSend.setTag(0);
                }
                else
                {
                    btnSend.setImageResource(R.drawable.send_icon);
                    btnSend.setTag(1);
                }
            }
        });

        edtMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(isMaxScrollReached(recyclerViewChat)) {

                    Handler handler = new Handler();
                    final Runnable r = new Runnable() {
                        public void run() {

                            recyclerViewChat.scrollToPosition(recyclerViewChat.getAdapter().getItemCount() - 1);
                        }
                    };
                    handler.postDelayed(r, 200);

                }
                return false;
            }
        });

        edtMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {

                    if(!btnSend.getTag().toString().equals("0"))
                    {
                        webMessage.sendMessage(UserID,FriendID,"TXT",edtMessage.getText().toString(),"","",true);
                        edtMessage.setText("");
                    }

                }
                return true;
            }
        });




        bundle = getIntent().getExtras();
        if (bundle != null) {
            FriendID = bundle.getString("FriendID");
            //String EditName = bundle.getString("EditName");


            SetSharedPreferencesString("FocusFriendID",FriendID);

            FriendMapping_DA F_DA = new FriendMapping_DA(ctx);
            F_DA.open();
            FriendResult =  F_DA.GetFriendMapping(FriendID);
            F_DA.close();

            getSupportActionBar().setTitle(FriendResult.EditName);
            LoadMessageData();

            if(bundle.getString("SharedType") != null)

            {
                Handler mainHandler = new Handler(ctx.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run()
                    {
                        if (bundle.getString("SharedType").equals("TXT")) {
                            String SharedValue = bundle.getString("SharedValue");
                            webMessage.sendMessage(UserID, FriendID, "TXT", SharedValue, "", "", true);
                        } else if (bundle.getString("SharedType").equals("IMG")) {
                            String selectedImagePath = bundle.getString("SharedValue");
                            SendImageFile(selectedImagePath);
                        }
                        bundle.remove("SharedType");
                        bundle.remove("SharedValue");
                    }
                };
                mainHandler.postDelayed(myRunnable,1000);

            }

        }


    }



    public void OnClickItemMap(int position)
    {


        MessageEntity result = listAdt.listMessages.get(position);


        Intent intent = new Intent(ctx,  ActivityMapChat.class);
        intent.putExtra("Address",result.value);
        intent.putExtra("Tag1",result.Tag1);
        intent.putExtra("Tag2",result.Tag2);
        intent.putExtra("ID",result.MessageFrom);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    public void OnClickItemImage(int position)
    {


        MessageEntity result = listAdt.listMessages.get(position);

        Intent intent = new Intent(ctx,  ActivityPhotoPreview.class);
        intent.putExtra("RunNo",result.RunNo);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }






    void LoadMessageData()
    {

        Message_DA M_DA = new Message_DA(this);
        M_DA.open();
        ArrayList<MessageEntity> listMessages = M_DA.GetAllMessage(FriendID);
        M_DA.close();

        if(listMessages.size()>0)
        {
            if (listMessages.get(listMessages.size() - 1).RunNo > MaxRunNo) {
                MaxRunNo = listMessages.get(listMessages.size() - 1).RunNo;
            }
        }

        listAdt = new ListItemMessageAdapter(ctx,listMessages,FriendID,UserID,FriendResult.EditName);

        recyclerViewChat = (RecyclerView)findViewById(R.id.recyclerViewChat);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(ctx));


        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(500);
        recyclerViewChat.setItemAnimator(itemAnimator);


        recyclerViewChat.setAdapter(listAdt);

        //recyclerViewChat.smoothScrollToPosition(recyclerViewChat.getAdapter().getItemCount()+1); //---scoll ลงล่างสุด
        recyclerViewChat.scrollToPosition(recyclerViewChat.getAdapter().getItemCount()-1);//---scoll ลงล่างสุด


        SaveReadMessage(listMessages);

    }

    private void SaveReadMessage(ArrayList<MessageEntity> listMessages)
    {

        ArrayList<Integer> listOfRunNo = new ArrayList<>();

        for(int i=0;i<listMessages.size();i++)
        {
            if(listMessages.get(i).ReadTime==null && listMessages.get(i).MessageFrom.equals(FriendID))
            {
                listOfRunNo.add(listMessages.get(i).RunNo);
            }
        }

        Message_DA DA = new Message_DA(ctx);
        DA.open();
        DA.UpdateReadTime(listOfRunNo);
        DA.close();

        if(listOfRunNo.size()>0)
        {
            webMessage.saveReadMessage(listOfRunNo,FriendID);
        }
    }

    private void CheckMyUnReadMessage(ArrayList<MessageEntity> listMessages)
    {

        ArrayList<Integer> listRunNoMyUnRead = new ArrayList<>();

        for(int i=0;i<listMessages.size();i++)
        {
            listRunNoMyUnRead.add(listMessages.get(i).RunNo);
        }


        if(listRunNoMyUnRead.size()>0)
        {
            webMessage.CheckMyUnReadMessage(listRunNoMyUnRead,UserID);
        }
    }




    public void AddMessage(ArrayList<MessageEntity> listMessages)
    {
//
        for(int i=0;i<listMessages.size();i++)
        {
            if(listMessages.get(i).RunNo > MaxRunNo) {
                listAdt.AddMessageItems(listMessages.get(i));

            }
        }



//        if(listMessages.size()>0) {
//            listAdt.AddMessageItems(listMessages.get(listMessages.size() - 1));
//        }


        if(listMessages.get(listMessages.size()-1).MessageFrom.equals(UserID))
        {
            recyclerViewChat.smoothScrollToPosition(recyclerViewChat.getAdapter().getItemCount() + 1); //---scoll ลงล่างสุด
        }
        else if(isMaxScrollReached(recyclerViewChat))
        {
            recyclerViewChat.smoothScrollToPosition(recyclerViewChat.getAdapter().getItemCount() + 1); //---scoll ลงล่างสุด
        }

        if(listMessages.size()>0)
        {
            if (listMessages.get(listMessages.size() - 1).RunNo > MaxRunNo) {
                MaxRunNo = listMessages.get(listMessages.size() - 1).RunNo;
            }
        }



    }


    long progressPercentage;
    boolean isSendLoad = false;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case SPEAK_TO_TEXT_REQUEST: //--------  เข้ามาเมื่อ รับฟังเสียงพูดเสร็จ
            {
                // if user speech any word and stop speak and data not null
                if (resultCode == RESULT_OK && null != data)
                {

                    // voice data stored in arraylist as a text so we get this text
                    // and set in textfield
                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    edtMessage.setText(text.get(0));


                }
                break;
            }

            case MAPDATA_REQUEST : //--------  เข้ามาเมื่อ เลือก Map เสร็จ
            {
                if (resultCode == RESULT_OK)
                {
                    String value = data.getStringExtra("DataAddress");
                    String Tag1 = data.getStringExtra("DataLatitude");
                    String Tag2 = data.getStringExtra("DataLongitude");
                    //edtMessage.setText(DataAddress);

                    webMessage.sendMessage(UserID,FriendID,"GPS",value,Tag1,Tag2,true);

                }
                break;
            }
            case GALLERY_REQUEST : //--------  เข้ามาเมื่อ เลือก GALLERY เสร็จ
            {
                if (resultCode == RESULT_OK)
                {

                    Uri selectedImageUri = data.getData();
                    String selectedImagePath = getRealPathFromURI(selectedImageUri);

                    SendImageFile(selectedImagePath);

                }
                break;
            }
            case CAMERA_REQUEST : //--------  เข้ามาเมื่อ SHOOT PHOTO เสร็จ
            {
                if (resultCode == RESULT_OK)
                {

                    getContentResolver().notifyChange(uri_camera, null);
                    String selectedImagePath = uri_camera.getPath();

                    SendImageFile(selectedImagePath);


                }
                break;
            }


        }
    }



    private void SendImageFile(String selectedImagePath)
    {

        FileOutputStream out=null;

        try
        {
            ImageConverter ImgTool = new ImageConverter();

            Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);

            ExifInterface exif = new ExifInterface(selectedImagePath);  //---- หาว่ารูปต้องหมุนกี่ องศา
            int rotation = ImgTool.getDegreesRotate(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));

            Bitmap bitmap_Resize = ImgTool.ResizeDown(bitmap,2000,rotation,true);  //---- 1000 คือ ขนาดด้านที่ยาวที่สุด

            selectedImagePath = ctx.getCacheDir()+ "/Resize.jpg";

            out = new FileOutputStream(selectedImagePath);
            bitmap_Resize.compress(Bitmap.CompressFormat.JPEG, 50, out);



        } catch (IOException e) {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        WebMessage web1 = new WebMessage(ctx);

        isSendLoad=true;
        recyclerViewChat.setItemAnimator(null);


        web1.setProcessSendMessageHandlerListener(new WebMessage.OnProcessSendMessage() {
            @Override
            public void onProcessSendMessageHandler(long progressPercentage_) {


                if(progressPercentage < progressPercentage_)
                {
                    progressPercentage = progressPercentage_;

                    Log.i("Send", String.valueOf(progressPercentage) + " %            ");

                    Handler mainHandler = new Handler(ctx.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {

                            //Log.i("Send", String.valueOf(progressPercentage) + " %            ");

                            listAdt.SendStatus = "Send " + progressPercentage + " %";
                            listAdt.notifyItemChanged(recyclerViewChat.getAdapter().getItemCount() - 1);
                            recyclerViewChat.scrollToPosition(recyclerViewChat.getAdapter().getItemCount()-1);//---scoll ลงล่างสุด

                        }
                    };
                    mainHandler.post(myRunnable);
                }

            }
        });
        web1.setProcessCompleteHandlerListener(new WebMessage.OnProcessComplete() {
            @Override
            public void onProcessCompleteHandler() {

                progressPercentage=0;
                isSendLoad=false;

                RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
                itemAnimator.setAddDuration(500);
                itemAnimator.setRemoveDuration(500);
                recyclerViewChat.setItemAnimator(itemAnimator);

                Handler mainHandler = new Handler(ctx.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run()
                    {
                        //recyclerViewChat.setAnimation(null);
                        listAdt.SendStatus = "";
                        listAdt.notifyItemChanged(recyclerViewChat.getAdapter().getItemCount()-1);

                    }
                };
                mainHandler.postDelayed(myRunnable,1500);
            }
        });


        web1.sendMessage(UserID,FriendID,"IMG",selectedImagePath,"","",true);

//
//                    Handler mainHandler = new Handler(ctx.getMainLooper());
//                    Runnable myRunnable = new Runnable() {
//                        @Override
//                        public void run() {
//                            //recyclerViewChat.smoothScrollToPosition(recyclerViewChat.getAdapter().getItemCount() + 1); //---scoll ลงล่างสุด
//                            recyclerViewChat.scrollToPosition(recyclerViewChat.getAdapter().getItemCount()-1);//---scoll ลงล่างสุด
//                        } };
//                    mainHandler.postDelayed(myRunnable,200);
    }



    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    static private boolean isMaxScrollReached(RecyclerView recyclerView) {
        int maxScroll = recyclerView.computeVerticalScrollRange();
        int currentScroll = recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent();
        return currentScroll >= maxScroll;
    }


    ArrayList<Integer> listIndexUpdateRead;

    public void UpdateReadTimeFromSocket(ArrayList<Integer> listRunNo)
    {

        listIndexUpdateRead = new ArrayList<>();





        for(int i=listAdt.listMessages.size()-1;i>=0;i--)
        {
            for (int j = 0; j < listRunNo.size(); j++)
            {
                //SS += listRunNo.get(i) +",";

                if(listAdt.listMessages.get(i).RunNo==listRunNo.get(j))
                {
                    listAdt.listMessages.get(i).ReadTime = new Date();
                    listIndexUpdateRead.add(i);
                    break;
                }

                if(listIndexUpdateRead.size()==listRunNo.size())
                {
                    break;
                }

            }
        }


        Handler mainHandler = new Handler(ctx.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run()
            {

                for(int i=0;i<listIndexUpdateRead.size();i++)
                {
                    listAdt.notifyItemChanged(listIndexUpdateRead.get(i));
                    Log.i("UpdateReadTime","Index : "+ listIndexUpdateRead.get(i));
                }


            }
        };
        mainHandler.post(myRunnable);



    }


    ArrayList<MessageEntity> buflistMessages;

    public ServiceConnection Mconnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            //Toast.makeText(ctx,"isBind = True",Toast.LENGTH_LONG).show();
            MyGolfService.LocalGolfService localGolfService = (MyGolfService.LocalGolfService)iBinder;
            myGolfService = localGolfService.getService();

            myGolfService.activityChatCtx = ctx;

            myGolfService.setGolfReceiveMessageHandlerListener(new MyGolfService.OnGolfReceiveMessage() {
                @Override
                public void onGolfReceiveMessageHandler(ArrayList<MessageEntity> listMessagesAll) {

                    buflistMessages = new ArrayList<>();
                    for(int i=0;i<listMessagesAll.size();i++)
                    {
                        if(listMessagesAll.get(i).MessageFrom.equals(FriendID))
                        {
                            buflistMessages.add(listMessagesAll.get(i));
                        }
                    }
                    if(buflistMessages.size()>0)
                    {
                        if (buflistMessages.get(buflistMessages.size() - 1).MessageFrom.equals(FriendID))
                        {
                            AddMessage(buflistMessages);

                            //buflistMessages = listMessages;

                            Handler mainHandler = new Handler(ctx.getMainLooper());
                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run()
                                {
                                    SaveReadMessage(buflistMessages);
                                }
                            };
                            mainHandler.post(myRunnable);


                        }

                    }

                }
            });

            myGolfService.setGolfSaveReadMessageHandlerListener(new MyGolfService.OnGolfSaveReadMessage() {
                @Override
                public void onGolfSaveReadMessageHandler(ArrayList<Integer> listRunNo) {

                    UpdateReadTimeFromSocket(listRunNo);

                }
            });





            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Toast.makeText(ctx,"isBind = False",Toast.LENGTH_LONG).show();
            myGolfService = null;
            isBind = false;

        }
    };



    @Override
    public void onBackPressed() {

        Intent intent = new Intent(ctx,  MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();



//        Toast.makeText(ctx,"onResume",Toast.LENGTH_SHORT).show();

        try {

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(FriendResult.FriendKey);


            Handler mainHandler = new Handler(ctx.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {

                    if (!isSendLoad) {
                        Message_DA M_DA = new Message_DA(ctx);
                        M_DA.open();
                        ArrayList<MessageEntity> listMessages = M_DA.GetNewMessage(FriendID, MaxRunNo);
                        ArrayList<MessageEntity> listMyMessagesNotRead = CheckUnReadInAdt();// M_DA.GetMyMessageNotRead(UserID,FriendID);
                        M_DA.close();

                        if (listMessages.size() > 0) {
                            for (int i = 0; i < listMessages.size(); i++) {
                                listAdt.AddMessageItems(listMessages.get(i));
                            }

                            SaveReadMessage(listMessages);
                            recyclerViewChat.scrollToPosition(recyclerViewChat.getAdapter().getItemCount() - 1);//---scoll ลงล่างสุด

                            Toast.makeText(ctx, "GetNewMessage = " + listMessages.size(), Toast.LENGTH_LONG).show();

                            if (listMessages.size() > 0) {
                                if (listMessages.get(listMessages.size() - 1).RunNo > MaxRunNo) {
                                    MaxRunNo = listMessages.get(listMessages.size() - 1).RunNo;
                                }
                            }
                        }
                        CheckMyUnReadMessage(listMyMessagesNotRead);
                    }


                    Intent intentService = new Intent(ctx, MyGolfService.class);
                    bindService(intentService, Mconnect, ctx.BIND_AUTO_CREATE);


                }
            };
            mainHandler.postDelayed(myRunnable, 500);

        }
        catch (Exception e)
        {
            Toast.makeText(ctx,"Error Resume : " + e.getMessage(),Toast.LENGTH_LONG).show();
        }



    }



    ArrayList<MessageEntity>  CheckUnReadInAdt()
    {
        ArrayList<MessageEntity> buff= new ArrayList<>();

        for(int i=0;i<listAdt.listMessages.size();i++)
        {
            if(listAdt.listMessages.get(i).ReadTime == null)
            {
                buff.add(listAdt.listMessages.get(i));
            }
        }
        return buff;
    }



    @Override
    protected void onStop() {
        super.onStop();

        edtMessage.clearFocus();
        //Toast.makeText(ctx,"onStop",Toast.LENGTH_SHORT).show();
        if(isBind)
        {
            myGolfService.activityChatCtx = null;
            myGolfService.mOnGolfReceiveMessage = null;
            myGolfService.mOnGolfSaveReadMessage = null;
            unbindService(Mconnect);
            isBind = false;
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
