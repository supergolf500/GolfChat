package com.supergolf500.golfchat;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener GpsListener;

    Context ctx;
    TextView txtLocation;
    Button btnSend;

    String DataAddress="";
    String DataLatitude="";
    String DataLongitude="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ctx = this;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        CheckPermissionGPS();

        txtLocation = (TextView) findViewById(R.id.txtLocation);

        btnSend = (Button)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(DataLatitude.equals("") || DataLongitude.equals(""))
                {
                    Toast.makeText(ctx,"ยังหาพิกัดไม่ได้",Toast.LENGTH_SHORT).show();
                }

                else
                {
                    Intent data = new Intent("com.supergolf500.golfchat.Chat.ActivityChat");
                    data.putExtra("DataAddress", DataAddress);
                    data.putExtra("DataLatitude", DataLatitude);
                    data.putExtra("DataLongitude", DataLongitude);
                    setResult(RESULT_OK, data);
                    finish();
                }

            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setRotateGesturesEnabled(false); //--- ไม่ให้ Rotage
        mMap.getUiSettings().setTiltGesturesEnabled(false);//--- ไม่ให้ ปรับองศามุมมองกล้อง


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }


        locationManager = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);


        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            Toast.makeText(ctx, "GPS " + String.valueOf(latitude) + "," + String.valueOf(longitude), Toast.LENGTH_SHORT).show();
            SetMarker(latitude, longitude);
        } else {
            long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
            long MIN_TIME_BW_UPDATES = 1000 * 5; // Sec

            GpsListener = new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    Toast.makeText(ctx, "GPS Update" + String.valueOf(latitude) + "," + String.valueOf(longitude), Toast.LENGTH_SHORT).show();
                    //edtMessage.setText(String.valueOf(latitude) + "," + String.valueOf(longitude));
                    SetMarker(latitude, longitude);


                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }

            };

            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, GpsListener);


        }


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
                    txtAddress.setText(getCompleteAddressString(LL.latitude, LL.longitude));

//                    Picasso.with(ctx)
//                            .load("http://supergolf500.ddns.net:3000/image/" + GetSharedPreferencesString("UserID") + ".jpg")
//                            .placeholder(R.drawable.no_image)
//                            .error(R.drawable.no_image)
//                            .into(imgPhoto);

                    Glide.with(ctx)
                            .load("http://supergolf500.ddns.net:3000/image/" + GetSharedPreferencesString("UserID") + ".jpg")
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
    }



    double latitude_Glo;
    double longitude_Glo;

    Marker marker;

    void SetMarker(double latitude, double longitude) {
        latitude_Glo = latitude;
        longitude_Glo = longitude;

        DataLatitude = String.valueOf(latitude);
        DataLongitude = String.valueOf(longitude);

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
                Handler handler = new Handler();
                final Runnable r = new Runnable() {
                    public void run() {

                        DataAddress = getCompleteAddressString(latitude_Glo, longitude_Glo);
                        txtLocation.setText("Latitude : " + String.valueOf(latitude_Glo) + "\nLongitude : " + String.valueOf(longitude_Glo) + "\n" + DataAddress);
                        marker.showInfoWindow();

                    }
                };
                handler.postDelayed(r, 1000);
            }

            @Override
            public void onCancel() {

            }
        });


        if (GpsListener != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(GpsListener);
            GpsListener = null;
        }




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

    @Override
    protected void onDestroy() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(GpsListener != null) {
            Toast.makeText(ctx,"onDestroy removeUpdates GpsListener",Toast.LENGTH_SHORT).show();
            locationManager.removeUpdates(GpsListener);
        }

        super.onDestroy();

    }
    //------------------------------------

    final private int REQUEST_CODE_ASK_PERMISSIONS_GPS = 123;

    private void CheckPermissionGPS()
    {

        int hasWriteGPSPermission = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasWriteGPSPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        else
        {
            return;
        }
        if (hasWriteGPSPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS_GPS);
            return;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS_GPS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //----- กรรณีกดให้สิทธิ


                } else {
                    // Permission Denied
                    Toast.makeText(ctx, "ไม่ให้สิทธิ GPS", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void showGPSDisabledAlertToUser()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS ไม่ได้ถูกเปิดอยู่")
                .setCancelable(false)
                .setNegativeButton("Goto Settings",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        })
                .setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    //------------------------------------------------------------------
    final String PreferencesName = "Config_GolfChat";

    public void SetSharedPreferencesString(String key,String value)
    {
        SharedPreferences sp = getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public String GetSharedPreferencesString(String key)
    {
        SharedPreferences sp = getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        String value = sp.getString(key, "");
        return value;
    }








}
