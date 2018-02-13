package com.supergolf500.golfchat.Chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.supergolf500.golfchat.R;

/**
 * Created by supergolf500 on 29/11/2559.
 */

public class ItemMessageViewHolder_GPS_B extends RecyclerView.ViewHolder  implements View.OnClickListener,OnMapReadyCallback {

    private Context ctx;

    ImageView imgPhoto;
    TextView txtMessage;
    TextView txtStatus;
    TextView txtTime;
    LinearLayout layoutBody;


    MapView miniMap;
    public double latitude;
    public double longitude;
    GoogleMap gMap;



    public ItemMessageViewHolder_GPS_B(View itemView, Context ctx) {
        super(itemView);

        this.ctx = ctx;
        imgPhoto = (ImageView)itemView.findViewById(R.id.imgPhoto);
        txtMessage= (TextView)itemView.findViewById(R.id.txtMessage);
        txtStatus =(TextView)itemView.findViewById(R.id.txtStatus);
        txtTime =(TextView)itemView.findViewById(R.id.txtTime);
        layoutBody = (LinearLayout)itemView.findViewById(R.id.layoutBody);

        miniMap = (MapView)itemView.findViewById(R.id.miniMap);

        if (miniMap != null)
        {
            miniMap.onCreate(null);
            miniMap.onResume();
            miniMap.getMapAsync(this);
            miniMap.setClickable(false);

        }


        itemView.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        //Toast.makeText(ctx,"Click Position : " + getAdapterPosition(),Toast.LENGTH_LONG).show();
        ((ActivityChat)ctx).OnClickItemMap(getAdapterPosition());
    }

    public void SetMarker(GoogleMap googleMap)
    {

        gMap.clear();
        LatLng current_latlng = new LatLng(latitude, longitude);

        Marker marker = gMap.addMarker(new MarkerOptions()
                .position(current_latlng)
                .title(" ฉัน")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin))
        );

        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(current_latlng, 15);
        marker.showInfoWindow();
        gMap.moveCamera(yourLocation);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        MapsInitializer.initialize(ctx);
        gMap = googleMap;

        gMap.getUiSettings().setMapToolbarEnabled(false);  //--- Hide Icon navigate


        SetMarker(googleMap);


    }
}
