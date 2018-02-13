package com.supergolf500.golfchat.ViewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.supergolf500.golfchat.MainActivity;
import com.supergolf500.golfchat.R;


/**
 * Created by supergolf500 on 10/11/2559.
 */

public class FriendItemViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {


    private Context ctx;

    public ImageView imageView1;
    public TextView txtSName;
    public TextView txtName;
    public Button btnRemove;

    public FriendItemViewHolder(View itemView,Context ctx) {
        super(itemView);
        this.ctx = ctx;
        txtName = (TextView)itemView.findViewById(R.id.txtName);
        txtSName= (TextView)itemView.findViewById(R.id.txtSName);
        imageView1 =(ImageView)itemView.findViewById(R.id.imageView1);

        itemView.setOnClickListener(this);

    }



    @Override
    public void onClick(View view) {

        Toast.makeText(ctx,"Click Position : " + getAdapterPosition(),Toast.LENGTH_LONG).show();
//        MainActivity Activity = (MainActivity)ctx;


    }
}
