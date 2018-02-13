package com.supergolf500.golfchat.WebAccess;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.supergolf500.golfchat.Chat.ActivityChat;
import com.supergolf500.golfchat.DB.Message_DA;
import com.supergolf500.golfchat.Entity.MessageEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by supergolf500 on 15/11/2559.
 */

public class WebMessage {

    //private static String WEB_URL = "http://supergolf500.ddns.net:81/Handler/Login.ashx";
    private static String WEB_URL = "http://supergolf500.ddns.net:3000/";

    Context ctx;

    long progressLoad=0;


    public WebMessage(Context ctx) {
        this.ctx = ctx;
    }

    public void sendMessage(String MessageFrom, String MessageTo, String MessageType, String value, String Tag1, String Tag2, final boolean isLoadDisplay)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(10000);
        client.setMaxRetriesAndTimeout(0, 10000);
        RequestParams params = new RequestParams();
        params.put("MessageFrom", MessageFrom);
        params.put("MessageTo",MessageTo);
        params.put("MessageType", MessageType);
        if(MessageType.equals("IMG"))
        {
//            File imagefile = new File(value);
//            FileInputStream fis = null;
//            try{
//                fis = new FileInputStream(imagefile);
//            }catch(FileNotFoundException e){
//                e.printStackTrace();
//            }
//            Bitmap bm = BitmapFactory.decodeStream(fis);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
//            byte[] b = baos.toByteArray();

            try {
                params.put("value",new File(value));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }
        else
        {
            params.put("value", value);
        }
        params.put("Tag1", Tag1);
        params.put("Tag2", Tag2);





        client.post(WEB_URL + "SendMessage/",params, new AsyncHttpResponseHandler() {

//            ProgressDialog pd;


            @Override
            public void onStart() {
//                pd = ProgressDialog.show(ctx, "Please wait ...", "Sending...", true);
//                pd.setCancelable(true);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                long progressPercentage = (long)100*bytesWritten/totalSize;
                //Log.i("Upload",String.valueOf(progressPercentage)+ " %            ");

                //progressLoad=0;
                //if(  progressPercentage > progressLoad)
                if(mOnProcessSendMessage != null) mOnProcessSendMessage.onProcessSendMessageHandler(progressPercentage);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {

                if(mOnProcessComplete != null) mOnProcessComplete.onProcessCompleteHandler();

//                pd.dismiss();
                String result = null;

                try {
                    result = new String(response, "UTF-8");

                    Log.i("HTTP", result);
                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                            .create();

                    //packresult = gson.fromJson(result, MessageEntity.class);
                    Type listType = new TypeToken<ArrayList<MessageEntity>>() {}.getType();
                    ArrayList<MessageEntity> LB = gson.fromJson(result,  listType);
                    //Toast.makeText(ctx,"MessageEntity : " +  LB.size(), Toast.LENGTH_LONG).show();

                    Message_DA M_DA = new Message_DA(ctx);
                    M_DA.open();
                    for(int i=0;i<LB.size();i++) {
                        M_DA.InsertMessage(LB.get(i));
                    }
                    M_DA.close();


                    if(isLoadDisplay) {  //---- ถ้าอยู่หน้าจอ Chat จะให้โหลดไปแสดงที่หน้าจอ  กันไว้กรณี Bot ส่ง
                        ((ActivityChat) ctx).AddMessage(LB);
                    }
//
//                    Handler mainHandler = new Handler(ctx.getMainLooper());
//                    Runnable myRunnable = new Runnable() {
//                        @Override
//                        public void run() {
//                            ((ActivityChat)ctx).AddMessage(LB_Glo);
//                        } };
//                    mainHandler.postDelayed(myRunnable,1000);





                }
                catch (Exception e)
                {
                    String message = e.getMessage();
                    Toast.makeText(ctx,"ERROR : " +  message + "nResult : " + result, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
//                pd.dismiss();
                mOnProcessComplete.onProcessCompleteHandler();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

        });
    }


    public void saveReadMessage(ArrayList<Integer> listOfRunNo, String MessageFrom)
    {


        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(4000);
        client.setMaxRetriesAndTimeout(0, 4000);
        RequestParams params = new RequestParams();



        for(int i = 0;i<listOfRunNo.size();i++)
        {
            params.put("RunNo_"+ i, listOfRunNo.get(i));
        }
        params.put("RunNoCount", listOfRunNo.size());
        //params.put("listOfRunNo", listOfRunNo);
        params.put("MessageFrom", MessageFrom);




        client.get(WEB_URL + "SaveReadMessage/",params, new AsyncHttpResponseHandler() {

//            ProgressDialog pd;


            @Override
            public void onStart() {
//                pd = ProgressDialog.show(ctx, "Please wait ...", "Sending...", true);
//                pd.setCancelable(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {

//                pd.dismiss();
                String result = null;

                try {
                    result = new String(response, "UTF-8");


//                    Toast toast= Toast.makeText(ctx,"ผลลัพท์ saveReadMessage : " + result, Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
//                    toast.show();


                }
                catch (Exception e)
                {
                    String message = e.getMessage();
                    Toast.makeText(ctx,"ERROR : " +  message + "nResult : " + result, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
//                pd.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }



    public void GetUnReadMessage(String UserID)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(4000);
        client.setMaxRetriesAndTimeout(0, 4000);
        RequestParams params = new RequestParams();
        params.put("UserID", UserID);

        client.get(WEB_URL + "GetUnReadMessage/",params, new AsyncHttpResponseHandler() {

//            ProgressDialog pd;


            @Override
            public void onStart() {
//                pd = ProgressDialog.show(ctx, "Please wait ...", "Sending...", true);
//                pd.setCancelable(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {

//                pd.dismiss();
                String result = null;

                try {
                    result = new String(response, "UTF-8");

                }
                catch (Exception e)
                {
                    String message = e.getMessage();
                    Toast.makeText(ctx,"ERROR : " +  message + "nResult : " + result, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
//                pd.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }



    public void CheckMyUnReadMessage(ArrayList<Integer> listOfRunNo,String UserID)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(4000);
        client.setMaxRetriesAndTimeout(0, 4000);
        RequestParams params = new RequestParams();
        params.put("listOfRunNo", listOfRunNo);
        params.put("UserID", UserID);

        client.get(WEB_URL + "CheckMyUnReadMessage/",params, new AsyncHttpResponseHandler() {

//            ProgressDialog pd;


            @Override
            public void onStart() {
//                pd = ProgressDialog.show(ctx, "Please wait ...", "Sending...", true);
//                pd.setCancelable(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {

//                pd.dismiss();
                String result = null;

                try {
                    result = new String(response, "UTF-8");
//
//                    Gson gson = new Gson();
//
//                    Type listType = new TypeToken<ArrayList<Integer>>() {}.getType();
//                    ArrayList<Integer> LB = gson.fromJson(result,  listType);//
//                    ((ActivityChat)ctx).UpdateReadTimeFromSocket(LB);

                }
                catch (Exception e)
                {
                    String message = e.getMessage();
                    Toast.makeText(ctx,"ERROR : " +  message + "nResult : " + result, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
//                pd.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }




    //--------------- Handler ----------------------------------------------------------------------

    public interface OnProcessSendMessage
    {
        void onProcessSendMessageHandler( long progressPercentage);
    }

    public OnProcessSendMessage mOnProcessSendMessage;

    public void setProcessSendMessageHandlerListener(OnProcessSendMessage l)
    {
        mOnProcessSendMessage=l;
    }

    //--------------- Handler ----------------------------------------------------------------------


    public interface OnProcessComplete
    {
        void onProcessCompleteHandler( );
    }

    public OnProcessComplete mOnProcessComplete;

    public void setProcessCompleteHandlerListener(OnProcessComplete l)
    {
        mOnProcessComplete=l;
    }



}
