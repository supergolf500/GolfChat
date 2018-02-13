package com.supergolf500.golfchat.ViewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.supergolf500.golfchat.MainActivity;
import com.supergolf500.golfchat.R;
import com.supergolf500.golfchat.SharedActivity;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by supergolf500 on 11/11/2559.
 */

public class FriendChatItemViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

    private Context ctx;

    public CircleImageView imageView1;
    //public ImageView imageView5;
    public TextView txtName;
    public TextView txtLastMessage;
    public TextView txtLastTime;
    public TextView txtCountMessage;

    private String TypeActivity; //----- ชนิดหน้าจอที่แสดง


    public FriendChatItemViewHolder(View itemView,Context ctx,String TypeActivity) {
        super(itemView);
        this.ctx = ctx;
        this.TypeActivity = TypeActivity;

        txtName = (TextView)itemView.findViewById(R.id.txtName);
        txtLastMessage= (TextView)itemView.findViewById(R.id.txtLastMessage);
        txtLastTime =  (TextView)itemView.findViewById(R.id.txtLastTime);
        txtCountMessage =  (TextView)itemView.findViewById(R.id.txtCountMessage);
        imageView1 =(CircleImageView)itemView.findViewById(R.id.imageView1);
        //imageView5 =(ImageView)itemView.findViewById(R.id.imageView5);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        //Toast.makeText(ctx,"Click Position : " + getAdapterPosition(),Toast.LENGTH_LONG).show();

        if(TypeActivity.equals("MainActivity")) {
            MainActivity Activity = (MainActivity) ctx;
            View imageView = view.findViewById(R.id.imageView1);
            Activity.OnChatNameClick(getAdapterPosition());
        }
        else if(TypeActivity.equals("SharedActivity"))
        {
            SharedActivity Activity = (SharedActivity) ctx;
            Activity.OnChatNameClick(getAdapterPosition());

        }
    }
}
