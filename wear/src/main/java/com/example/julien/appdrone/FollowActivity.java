package com.example.julien.appdrone;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import java.text.DecimalFormat;



public class FollowActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnSuccessListener<Location> {

    private FusedLocationProviderClient flpc;
    private LocationCallback lc;
    public String lati="";
    public String longit="";
    private Handler myHandler;
    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            getloc();
            myHandler.postDelayed(this,2000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        Button tkoff= findViewById(R.id.button6);
        Button follow = findViewById(R.id.button7);
        tkoff.setText("Take off");
        follow.setText("Follow");

        myHandler = new Handler();
        myHandler.postDelayed(myRunnable,2000);

        // Enables Always-on
        //setAmbientEnabled();
        GoogleApiClient gapi = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        gapi.connect();

    }
    @Override
    protected void onPause() {
        super.onPause();
        if(myHandler != null)
            myHandler.removeCallbacks(myRunnable);
    }

    public void landTakeOffMethod(View view) {
        Button button = (Button) view;
        String a=button.getText().toString();

        if (a.equals("Take off")){
            button.setText("Land");
            //code to take off here
        }
        if (a.equals("Land")){
            button.setText("Take off");
        }
    }

    public void followMethod(View view) {
        Button button = (Button) view;
        String a=button.getText().toString();

        if (a.equals("Follow")){
            button.setText("Stop Following");
            //Code to follow here
        }
        if (a.equals("Stop Following")){
            button.setText("Follow");
            //Code to  stop following here
        }

    }

    public void videoMethod(View view) {
        Button button = (Button) view;
        String a=button.getText().toString();

        if (a.equals("Start video")){
            button.setText("Stop video");
            //Code to start video here
        }
        if (a.equals("Stop video")){
            button.setText("Start video");
            //Code to  stop video here
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("CIO", "GAPI connected");
        // Get the location provider through the GoogleApi
        flpc = LocationServices.getFusedLocationProviderClient(this);
        // Check location availability
        @SuppressLint("MissingPermission") Task<LocationAvailability> locAvailable = flpc.getLocationAvailability();
        locAvailable.addOnSuccessListener(new OnSuccessListener<LocationAvailability>() {
            @Override
            public void onSuccess(LocationAvailability locationAvailability) {
                Log.i("CIO", "Location is available = " + locationAvailability.toString());
            }
        });
        // Ask for update of the location
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000).setFastestInterval(1000);
        lc = new LocationCallback();
        flpc.requestLocationUpdates(locationRequest, lc, null);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("CIO", "Error with GAPI connection");
    }


    // Should check if the permission is granted for this app
    @SuppressLint("MissingPermission")
    public void getlocation(View view) {
        Task<Location> loc = flpc.getLastLocation();
        loc.addOnSuccessListener(this);
    }

    @SuppressLint("MissingPermission")
    public void getloc() {
        Task<Location> loc = flpc.getLastLocation();
        loc.addOnSuccessListener(this);
    }


    // Callback for the location task
    @Override
    public void onSuccess(Location loc) {
        Log.i("CIO", "Task completed.");
        if (loc != null) {
            DecimalFormat df = new DecimalFormat("#.#######");
            String lon = df.format(loc.getLongitude());
            String lat = df.format(loc.getLatitude());
            Log.i("CIO", "Location: " + lon  + " , " + lat );
            longit=lon;
            lati=lat;
            Toast.makeText(this, lati.concat(",").concat(longit), Toast.LENGTH_SHORT).show();
        }
        else
        {
            Log.i("CIO", "No defined location ! Are you inside ? Have you authorized location on the smartwatch ?");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("CIO", "GAPI supsended");
    }



    @Override
    protected void onStop() {
        super.onStop();
        if (lc != null)
            flpc.removeLocationUpdates(lc);
    }

}
