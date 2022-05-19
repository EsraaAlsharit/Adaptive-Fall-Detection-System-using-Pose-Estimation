package com.mosaza.falldetectionapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mosaza.falldetectionapp.R;

import static java.security.AccessController.getContext;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;

    private Button buttonBack;

    private boolean LOCATION_PERMISSION_GRANTED = false;
    private final int LOCATION_REQUEST_CODE = 1234;
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private final long MIN_TIME = 5000; //5 seconds
    private final long MIN_DISTANCE = 5; //5 meters
    private static final float DEFAULT_ZOOM = 13f;
    private LatLng position = new LatLng(24.774265,46.738586);

    private boolean hasOldLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        double lat = getIntent().getDoubleExtra(SignUpActivity.LAT_KEY, 0);
        double lon = getIntent().getDoubleExtra(SignUpActivity.LON_KEY, 0);
        if(lat > 0 && lon > 0){
            hasOldLocation = true;
            position = new LatLng(lat, lon);
        }

        buttonBack = findViewById(R.id.map_back_button);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnData();
            }
        });

        getLocationPermission();
        if(LOCATION_PERMISSION_GRANTED) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    private void getLocationPermission(){
        String[] Permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
                LOCATION_PERMISSION_GRANTED = true;
            }
        else
        {
            if (Build.VERSION.SDK_INT >= 23)
                requestPermissions(Permissions, LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LOCATION_PERMISSION_GRANTED = false;
        if (requestCode == LOCATION_REQUEST_CODE && grantResults.length > 0){
            for(int i = 0; i < grantResults.length; i++){
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    LOCATION_PERMISSION_GRANTED = false;
                    return;
                }
            }
            LOCATION_PERMISSION_GRANTED = true;
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerDragListener(this);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isEnabled) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this,
                    android.R.style.Theme_DeviceDefault_Light_Dialog));
            alertDialogBuilder.setTitle("GPS");
            alertDialogBuilder.setMessage("Open your GPS provider");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_map);
            alertDialogBuilder.setPositiveButton("Open", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // continue with discard
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialogBuilder.show();

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM));
            mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title("position")
                    .draggable(true));
        }
        else{
            mMap.clear();
            if(hasOldLocation){
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM));
                mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title("position")
                        .draggable(true));
            }else{
                getCurrentLocation();
            }
        }
    }

    private void getCurrentLocation(){
        Toast.makeText(this, "Getting location", Toast.LENGTH_LONG).show();

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                position = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM));
                mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title("position")
                        .draggable(true));
                mLocationManager.removeUpdates(mLocationListener);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {}
        };

        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
        }
        catch (SecurityException e){
            e.printStackTrace();
            Log.d("TAG", e.getMessage());
        }
        catch (Exception e){
            Log.d("TAG", e.getMessage());
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) { }

    @Override
    public void onMarkerDrag(Marker marker) { }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        position = marker.getPosition();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mLocationManager != null && mLocationListener != null)
            mLocationManager.removeUpdates(mLocationListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mLocationManager != null ){
            boolean isEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(isEnabled){
                mMap.clear();
                if(hasOldLocation){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM));
                    mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title("position")
                            .draggable(true));
                }else{
                    getCurrentLocation();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        returnData();
    }

    private void returnData(){
        Intent intent = new Intent();
        intent.putExtra(SignUpActivity.LAT_KEY, position.latitude);
        intent.putExtra(SignUpActivity.LON_KEY, position.longitude);
        setResult(RESULT_OK, intent);
        finish();
    }
}