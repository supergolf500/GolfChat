package com.supergolf500.golfchat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.supergolf500.golfchat.Entity.FriendMapping;
import com.supergolf500.golfchat.ViewHolder.ListFriensViewAdapter;

import java.util.ArrayList;

/**
 * Created by supergolf500 on 10/11/2559.
 */

public class Fragment_ListFriends extends Fragment {


    RecyclerView recyclerView1;
    ListFriensViewAdapter listAdt;
    Context ctx;

    private ArrayList<FriendMapping> listFriends = new ArrayList();;



    public void addlistFriends(FriendMapping friendData) {
        listFriends.add(friendData);
    }



    public Fragment_ListFriends(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_listfriends_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        listAdt = new ListFriensViewAdapter(ctx,listFriends);

        recyclerView1 = (RecyclerView)view.findViewById(R.id.recyclerView1);
        recyclerView1.setLayoutManager(new LinearLayoutManager(ctx));

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(500);
        recyclerView1.setItemAnimator(itemAnimator);


        recyclerView1.setAdapter(listAdt);



    }
}
