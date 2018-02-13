package com.supergolf500.golfchat.Chat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.supergolf500.golfchat.R;

import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityMapChat extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    Context ctx;
    String Address;
    String Tag1;
    String Tag2;
    String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_chat);

        ctx = this;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Intent myIntent = getIntent();
        Address = myIntent.getStringExtra("Address");
        Tag1= myIntent.getStringExtra("Tag1");
        Tag2= myIntent.getStringExtra("Tag2");
        ID= myIntent.getStringExtra("ID");


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        mMap.getUiSettings().setRotateGesturesEnabled(false); //--- ไม่ให้ Rotage
        mMap.getUiSettings().setTiltGesturesEnabled(false);//--- ไม่ให้ ปรับองศามุมมองกล้อง


        if (mMap != null) {
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View V = getLayoutInflater().inflate(R.layout.marker_info, null);
                    TextView txtLatitude = (TextView) V.findViewById(R.id.txtLatitude);
                    TextView txtLongitude = (TextView) V.findViewById(R.id.txtLongitude);
                    TextView txtAddress = (TextView) V.findViewById(R.id.txtAddress);
                    CircleImageView imgPhoto = (CircleImageView) V.findViewById(R.id.imgPhoto);

                    LatLng LL = marker.getPosition();
                    txtLatitude.setText("Latitude : " + String.valueOf(LL.latitude));
                    txtLongitude.setText("Longitude : " + String.valueOf(LL.longitude));
                    //txtAddress.setText(Address);
                    txtAddress.setText(getCompleteAddressString(LL.latitude, LL.longitude));

//                    Picasso.with(ctx)
//                            .load("http://supergolf500.ddns.net:3000/image/" + ID + ".jpg")
//                            .placeholder(R.drawable.no_image)
//                            .error(R.drawable.no_image)
//                            .into(imgPhoto);


                    Glide.with(ctx)
                            .load("http://supergolf500.ddns.net:3000/image/" + ID + ".jpg")
                            .placeholder(R.drawable.no_image)
                            .override(100,100)
                            .centerCrop()
                            .animate(R.anim.zoom_in)
                            .into(imgPhoto);


                    return V;
                }
            });
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);



        double latitude = Double.parseDouble(Tag1);
        double longitude = Double.parseDouble(Tag2);

        SetMarker(latitude,longitude);


    }


    Marker marker;
    void SetMarker(double latitude, double longitude) {


        mMap.clear();
        LatLng current_latlng = new LatLng(latitude, longitude);




        marker = mMap.addMarker(new MarkerOptions()
                            .position(current_latlng)
                            .title("Supergolf")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin))

                    );


        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(current_latlng, 15);
        mMap.animateCamera(yourLocation,new GoogleMap.CancelableCallback()
        {

            @Override
            public void onFinish() {
                marker.showInfoWindow();

                Handler mainHandler = new Handler(ctx.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run()
                    {
                        marker.showInfoWindow();
                    }
                };
                mainHandler.postDelayed(myRunnable,1000);


            }

            @Override
            public void onCancel() {

            }
        });





    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    if(i==returnedAddress.getMaxAddressLineIndex()-1)
                    {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i));
                    }
                    else
                    {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    }
                }
                strAdd = strReturnedAddress.toString();

            } else {
                //Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }


}
