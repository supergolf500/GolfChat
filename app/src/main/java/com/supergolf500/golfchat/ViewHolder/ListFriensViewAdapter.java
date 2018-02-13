package com.supergolf500.golfchat.ViewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.supergolf500.golfchat.Entity.FriendMapping;
import com.supergolf500.golfchat.R;

import java.util.ArrayList;

/**
 * Created by supergolf500 on 10/11/2559.
 */

public class ListFriensViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context ctx;

    private ArrayList<FriendMapping> listFriends;

    public ListFriensViewAdapter(Context ctx, ArrayList<FriendMapping> listFriends_) {
        this.ctx = ctx;
        listFriends = listFriends_;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(ctx).inflate(R.layout.item_friend, parent, false);
        return new FriendItemViewHolder(v,ctx);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        FriendItemViewHolder v_holder = (FriendItemViewHolder) holder;
        v_holder.txtName.setText(listFriends.get(position).FriendID);
        v_holder.txtSName.setText(listFriends.get(position).EditName);
        v_holder.imageView1.setImageResource(R.drawable.image_people);

    }

    @Override
    public int getItemCount() {
        return listFriends.size();
    }
}
