package com.supergolf500.golfchat.ViewHolder;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import com.supergolf500.golfchat.Entity.FriendChatEntity;
import com.supergolf500.golfchat.Entity.FriendMapping;
import com.supergolf500.golfchat.R;

import java.util.ArrayList;

/**
 * Created by supergolf500 on 11/11/2559.
 */

public class ListFriensChatViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx;

    public ArrayList<FriendChatEntity> listFriendChatEntity;

    private String TypeActivity;

    public ListFriensChatViewAdapter(Context ctx ,ArrayList<FriendChatEntity> listFriendChatEntity,String TypeActivity) {
        this.ctx = ctx;
        this.listFriendChatEntity = listFriendChatEntity;
        this.TypeActivity = TypeActivity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(ctx).inflate(R.layout.item_friend_chat, parent, false);
        return new FriendChatItemViewHolder(v,ctx,TypeActivity);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        FriendChatItemViewHolder v_holder = (FriendChatItemViewHolder) holder;
        v_holder.txtName.setText("[" + listFriendChatEntity.get(position).FriendID + "] " + listFriendChatEntity.get(position).EditName);
        v_holder.txtLastMessage.setText(listFriendChatEntity.get(position).LastMessage);
        if(listFriendChatEntity.get(position).LastTimeMessage != null)
        {
            v_holder.txtLastTime.setText("Time " + listFriendChatEntity.get(position).LastTimeMessage.getHours() + ":" + listFriendChatEntity.get(position).LastTimeMessage.getMinutes());
        }

        v_holder.txtCountMessage.setText(listFriendChatEntity.get(position).CountMessage+"");


        //v_holder.imageView1.setImageResource(R.drawable.image_people);

        Glide.with(ctx)
                .load("http://supergolf500.ddns.net:3000/image/" + listFriendChatEntity.get(position).FriendID + ".jpg")
                .placeholder(R.drawable.no_image)
                .override(100,100)
                .centerCrop()
                .animate(R.anim.zoom_in)
                .into(v_holder.imageView1);





//        Picasso.with(ctx)
//                .load("http://supergolf500.ddns.net:3000/image/" + listFriendChatEntity.get(position).FriendID + ".jpg")
//                .placeholder(R.drawable.no_image)
//                .error(R.drawable.no_image)
//                .into(v_holder.imageView1);



    }

    @Override
    public int getItemCount() {
        return listFriendChatEntity.size();
    }
}
