package com.supergolf500.golfchat.Chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.supergolf500.golfchat.R;

/**
 * Created by supergolf500 on 14/11/2559.
 */

public class ItemMessageViewHolder_img_A extends RecyclerView.ViewHolder  implements View.OnClickListener {

    private Context ctx;

    ImageView imgPhoto;
    ImageView imgView1;
    TextView txtStatus;
    TextView txtTime;

    public ItemMessageViewHolder_img_A(View itemView, Context ctx) {
        super(itemView);

        this.ctx = ctx;
        imgPhoto = (ImageView)itemView.findViewById(R.id.imgPhoto);
        imgView1= (ImageView)itemView.findViewById(R.id.imgView1);
        txtStatus =(TextView)itemView.findViewById(R.id.txtStatus);
        txtTime =(TextView)itemView.findViewById(R.id.txtTime);

        itemView.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
       // Toast.makeText(ctx,"Click Position : " + getAdapterPosition(),Toast.LENGTH_LONG).show();
        ((ActivityChat)ctx).OnClickItemImage(getAdapterPosition());

    }
}
