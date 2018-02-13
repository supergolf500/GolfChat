package com.supergolf500.golfchat.Chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.supergolf500.golfchat.R;

/**
 * Created by supergolf500 on 14/11/2559.
 */

public class ItemMessageViewHolder_Status extends RecyclerView.ViewHolder  implements View.OnClickListener {

    private Context ctx;

    TextView txtMessage;
    LinearLayout layout_startus;

    public ItemMessageViewHolder_Status(View itemView, Context ctx) {
        super(itemView);

        this.ctx = ctx;
        txtMessage= (TextView)itemView.findViewById(R.id.txtMessage);
        layout_startus = (LinearLayout)itemView.findViewById(R.id.layout_startus);

        itemView.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        Toast.makeText(ctx,"Click Position : " + getAdapterPosition(),Toast.LENGTH_LONG).show();
    }
}
