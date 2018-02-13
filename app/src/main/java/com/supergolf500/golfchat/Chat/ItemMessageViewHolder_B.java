package com.supergolf500.golfchat.Chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.supergolf500.golfchat.R;

/**
 * Created by supergolf500 on 14/11/2559.
 */

public class ItemMessageViewHolder_B extends RecyclerView.ViewHolder  implements View.OnClickListener {

    private Context ctx;

    TextView txtMessage;
    TextView txtStatus;
    TextView txtTime;

    public ItemMessageViewHolder_B(View itemView, Context ctx) {
        super(itemView);

        this.ctx = ctx;
        txtMessage= (TextView)itemView.findViewById(R.id.txtMessage);
        txtStatus =(TextView)itemView.findViewById(R.id.txtStatus);
        txtTime =(TextView)itemView.findViewById(R.id.txtTime);

        itemView.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        //Toast.makeText(ctx,"Click Position : " + getAdapterPosition(),Toast.LENGTH_LONG).show();

        if(Patterns.WEB_URL.matcher(txtMessage.getText()).matches())
        {
            String url = txtMessage.getText().toString();
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            ctx.startActivity(i);
        }

    }
}
