package com.supergolf500.golfchat.Chat;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.GoogleMap;


import com.supergolf500.golfchat.Entity.MessageEntity;
import com.supergolf500.golfchat.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by supergolf500 on 14/11/2559.
 */

public class ListItemMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    private Context ctx;
    public ArrayList<MessageEntity> listMessages;

    public String SendStatus="";


    String FriendID;
    String UserID;
    String FriendName;


    public ListItemMessageAdapter(Context ctx, ArrayList<MessageEntity> listMessages_,String FriendID,String UserID,String FriendName) {
        this.ctx = ctx;
        this.listMessages = listMessages_;
        this.FriendID=FriendID;
        this.UserID = UserID;
        this.FriendName = FriendName;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(ctx).inflate(R.layout.item_chat_1, parent, false);
//        return new ItemMessageViewHolder_A(v,ctx);


        View v = null;
        if(viewType==-1)
        {
            v = LayoutInflater.from(ctx).inflate(R.layout.item_chat_status, parent, false);
            return new ItemMessageViewHolder_Status(v,ctx);

        }
        else if(viewType==0)
        {
            v = LayoutInflater.from(ctx).inflate(R.layout.item_chat_1, parent, false);
            return new ItemMessageViewHolder_A(v,ctx);

        }
        else if(viewType==1)
        {
            v = LayoutInflater.from(ctx).inflate(R.layout.item_chat_2, parent, false);
            return new ItemMessageViewHolder_B(v,ctx);
        }
        else if(viewType==2)
        {
            v = LayoutInflater.from(ctx).inflate(R.layout.item_chat_gps_1,parent, false);
            return new ItemMessageViewHolder_GPS_A(v,ctx,FriendName);
        }
        else if(viewType==3)
        {
            v = LayoutInflater.from(ctx).inflate(R.layout.item_chat_gps_2, parent, false);
            return new ItemMessageViewHolder_GPS_B(v,ctx);
        }
        else if(viewType==4)
        {
            v = LayoutInflater.from(ctx).inflate(R.layout.item_chat_img_1, parent, false);
            return new ItemMessageViewHolder_img_A(v,ctx);
        }
        else if(viewType==5)
        {
            v = LayoutInflater.from(ctx).inflate(R.layout.item_chat_img_2, parent, false);
            return new ItemMessageViewHolder_img_B(v,ctx);
        }

        return new ItemMessageViewHolder_A(v,ctx);

    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        int  viewType = getItemViewType(position);

        if(viewType==-1) //-----STATUS
        {

            ItemMessageViewHolder_Status v_holder = (ItemMessageViewHolder_Status) holder;
            if (SendStatus.equals("")) {
                v_holder.txtMessage.setText("");
                v_holder.layout_startus.setVisibility(View.GONE);
            } else {
                v_holder.txtMessage.setText(SendStatus);
                v_holder.layout_startus.setVisibility(View.VISIBLE);
            }


        }
        else if(viewType==0) //-----ข้อความฝั่งคู่สนมนา
        {
            ItemMessageViewHolder_A v_holder = (ItemMessageViewHolder_A) holder;

            v_holder.txtStatus.setText("");
            if (listMessages.get(position).SendTime != null) {

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                v_holder.txtTime.setText(new SimpleDateFormat("h:mm aa").format(listMessages.get(position).SendTime));
                //v_holder.txtTime.setText(listMessages.get(position).SendTime.getHours() + ":" + listMessages.get(position).SendTime.getMinutes());
            } else {
                v_holder.txtTime.setText("");
            }

            if(listMessages.get(position).MessageType.equals("TXT"))
            {
                //---- กรอบสีปกติ
                v_holder.txtMessage.setBackgroundResource(R.drawable.chat_bg1);
                v_holder.txtMessage.setText(listMessages.get(position).value);
            }
            else
            {
                //---- กรอบสี Bot
                v_holder.txtMessage.setBackgroundResource(R.drawable.chat_bg_bot1);

                if(listMessages.get(position).value.equals("GETGPS"))
                {
                    v_holder.txtMessage.setText("ขอข้อมูลพิกัด");
                }
                else
                {
                    v_holder.txtMessage.setText(listMessages.get(position).value);
                }

            }

            Glide.with(ctx)
                    .load("http://supergolf500.ddns.net:3000/image/" + FriendID + ".jpg")
                    .placeholder(R.drawable.no_image)
                    .animate(R.anim.zoom_in)
                    .into(v_holder.imgPhoto);

        }
        else if(viewType==1) //-----ข้อความฝั่งเรา
        {
            ItemMessageViewHolder_B v_holder = (ItemMessageViewHolder_B) holder;

            if (listMessages.get(position).ReadTime != null) {
                v_holder.txtStatus.setText("Read");
            } else {
                v_holder.txtStatus.setText(" ");
            }

            if(listMessages.get(position).MessageType.equals("TXT"))
            {
                //---- กรอบสีปกติ
                v_holder.txtMessage.setBackgroundResource(R.drawable.chat_bg2);
                v_holder.txtMessage.setText(listMessages.get(position).value);
            }
            else
            {
                //---- กรอบสี Bot
                v_holder.txtMessage.setBackgroundResource(R.drawable.chat_bg_bot2);
                if(listMessages.get(position).value.equals("GETGPS"))
                {
                    v_holder.txtMessage.setText("ขอข้อมูลพิกัด");
                }
                else
                {
                    v_holder.txtMessage.setText(listMessages.get(position).value);
                }
            }


            if (listMessages.get(position).SendTime != null) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                v_holder.txtTime.setText(new SimpleDateFormat("h:mm aa").format(listMessages.get(position).SendTime));
                //v_holder.txtTime.setText(listMessages.get(position).SendTime.getHours() + ":" + listMessages.get(position).SendTime.getMinutes());
            } else {
                v_holder.txtTime.setText("");
            }
        }
        else if(viewType==2) //-----GPS  ข้อความฝั่งคู่สนมนา
        {
            ItemMessageViewHolder_GPS_A v_holder = (ItemMessageViewHolder_GPS_A) holder;
            v_holder.latitude = Double.parseDouble(listMessages.get(position).Tag1);
            v_holder.longitude = Double.parseDouble(listMessages.get(position).Tag2);

            if(listMessages.get(position).MessageType.equals("GPS"))
            {
                //---- กรอบสีปกติ
                v_holder.layoutBody.setBackgroundResource(R.drawable.chat_bg1);
            }
            else
            {
                //---- กรอบสี Bot
                v_holder.layoutBody.setBackgroundResource(R.drawable.chat_bg_bot1);
            }



            GoogleMap gMap = v_holder.gMap;
            if(gMap != null)
            {
                v_holder.SetMarker(gMap);
            }

            v_holder.txtMessage.setText(listMessages.get(position).value);
            v_holder.txtStatus.setText(" ");

            if (listMessages.get(position).SendTime != null) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                v_holder.txtTime.setText(new SimpleDateFormat("h:mm aa").format(listMessages.get(position).SendTime));
                //v_holder.txtTime.setText(listMessages.get(position).SendTime.getHours() + ":" + listMessages.get(position).SendTime.getMinutes());
            } else {
                v_holder.txtTime.setText("");
            }

            Glide.with(ctx)
                    .load("http://supergolf500.ddns.net:3000/image/" + FriendID + ".jpg")
                    .placeholder(R.drawable.no_image)
                    .animate(R.anim.zoom_in)
                    .into(v_holder.imgPhoto);

        }
        else if(viewType==3) //-----GPS  ้อความฝั่งเรา
        {
            ItemMessageViewHolder_GPS_B v_holder = (ItemMessageViewHolder_GPS_B) holder;
            v_holder.latitude = Double.parseDouble(listMessages.get(position).Tag1);
            v_holder.longitude = Double.parseDouble(listMessages.get(position).Tag2);


            if(listMessages.get(position).MessageType.equals("GPS"))
            {
                //---- กรอบสีปกติ
                v_holder.layoutBody.setBackgroundResource(R.drawable.chat_bg2);

            }
            else
            {
                //---- กรอบสี Bot
                v_holder.layoutBody.setBackgroundResource(R.drawable.chat_bg_bot2);
            }


            GoogleMap gMap = v_holder.gMap;
            if(gMap != null)
            {
                v_holder.SetMarker(gMap);
            }


            v_holder.txtMessage.setText(listMessages.get(position).value);
            if (listMessages.get(position).ReadTime != null) {
                v_holder.txtStatus.setText("Read");
            } else {
                v_holder.txtStatus.setText(" ");
            }

            if (listMessages.get(position).SendTime != null) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                v_holder.txtTime.setText(new SimpleDateFormat("h:mm aa").format(listMessages.get(position).SendTime));
                //v_holder.txtTime.setText(listMessages.get(position).SendTime.getHours() + ":" + listMessages.get(position).SendTime.getMinutes());
            } else {
                v_holder.txtTime.setText("");
            }
        }
        else if(viewType==4) //----- Image ฝั่งเค้า
        {
            ItemMessageViewHolder_img_A v_holder = (ItemMessageViewHolder_img_A) holder;
//            v_holder.imgView1=...

            v_holder.txtStatus.setText(" ");

            if (listMessages.get(position).SendTime != null) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                v_holder.txtTime.setText(new SimpleDateFormat("h:mm aa").format(listMessages.get(position).SendTime));
                //v_holder.txtTime.setText(listMessages.get(position).SendTime.getHours() + ":" + listMessages.get(position).SendTime.getMinutes());
            } else {
                v_holder.txtTime.setText("");
            }


            Glide.with(ctx)
                    .load("http://supergolf500.ddns.net:3000/Upload/thumbnail/" + listMessages.get(position).RunNo + ".jpg")
                    .listener(new RequestListener<String, GlideDrawable>() {

                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {

                            Log.i("Glide","GlidError Index : " + position + " : " + e.getMessage());

                            return false;
                        }



                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            Log.i("Glide","GlidReady");
                            return false;
                        }
                    })

                    .placeholder(R.drawable.ic_camera)
                    .override(200,200)
                    .centerCrop()
                    .into(v_holder.imgView1);


            Glide.with(ctx)
                    .load("http://supergolf500.ddns.net:3000/image/" + FriendID + ".jpg")
                    .placeholder(R.drawable.no_image)
                    .animate(R.anim.zoom_in)
                    .into(v_holder.imgPhoto);


        }
        else if(viewType==5) //----- Image ฝั่งเเรา
        {
            ItemMessageViewHolder_img_B v_holder = (ItemMessageViewHolder_img_B) holder;
//            v_holder.imgView1=...

            if (listMessages.get(position).ReadTime != null) {
                v_holder.txtStatus.setText("Read");
            } else {
                v_holder.txtStatus.setText(" ");
            }

            if (listMessages.get(position).SendTime != null) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                v_holder.txtTime.setText(new SimpleDateFormat("h:mm aa").format(listMessages.get(position).SendTime));
                //v_holder.txtTime.setText(listMessages.get(position).SendTime.getHours() + ":" + listMessages.get(position).SendTime.getMinutes());
            } else {
                v_holder.txtTime.setText("");
            }

            Glide.with(ctx)
                    .load("http://supergolf500.ddns.net:3000/Upload/thumbnail/" + listMessages.get(position).RunNo + ".jpg")
                    .placeholder(R.drawable.ic_camera)
                    .override(200,200)
                    .centerCrop()
                    .into(v_holder.imgView1);




        }


    }



    @Override
    public int getItemViewType(int position) {
        int viewType = 0; //Default is 0

        if(position==getItemCount()-1)
        {
            viewType = -1;
        }
        else if(listMessages.get(position).MessageType.equals("TXT") || listMessages.get(position).MessageType.equals("AUTO_TXT"))
        {
            if (listMessages.get(position).MessageFrom.equals(UserID))
                viewType = 1;
            else
                viewType = 0;
        }
        else if(listMessages.get(position).MessageType.equals("GPS") || listMessages.get(position).MessageType.equals("AUTO_GPS"))
        {
            if (listMessages.get(position).MessageFrom.equals(UserID))
                viewType = 3;
            else
                viewType = 2;
        }
        else if(listMessages.get(position).MessageType.equals("IMG"))
        {
            if (listMessages.get(position).MessageFrom.equals(UserID))
                viewType = 5;
            else
                viewType = 4;
        }

        return viewType;
    }

    @Override
    public int getItemCount() {
        return listMessages.size()+1;
    }




    public void AddMessageItems(MessageEntity mess)
    {

        listMessages.add(mess);
        //notifyDataSetChanged();

        //notifyItemInserted(listMessages.size()-1);



        int DelayTime;

        //---- ถ้าเป็น รูปภาพให้ถ่วงเวลาไว้หน่อย  เดี๋ยว Server Save thumbnail ไม่ทัน
        if(mess.MessageType.equals("IMG")) {
            DelayTime = 0;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else
        {
            DelayTime = 0;
        }

        Handler mainHandler = new Handler(ctx.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {

                notifyItemInserted(listMessages.size() - 1);

            }
        };
        mainHandler.postDelayed(myRunnable, DelayTime);





    }



}
