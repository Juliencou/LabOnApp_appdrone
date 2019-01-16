package com.example.julien.appdrone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.example.julien.appdrone.drone.BebopDrone;
import com.example.julien.appdrone.utils.Constant;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parrot.arsdk.arcommands.ARCOMMANDS_ARDRONE3_MEDIARECORDEVENT_PICTUREEVENTCHANGED_ERROR_ENUM;
import com.parrot.arsdk.arcommands.ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM;
import com.parrot.arsdk.arcontroller.ARCONTROLLER_DEVICE_STATE_ENUM;
import com.parrot.arsdk.arcontroller.ARControllerCodec;
import com.parrot.arsdk.arcontroller.ARFrame;
import com.parrot.arsdk.ardiscovery.ARDiscoveryDeviceService;
import java.text.DecimalFormat;




public class FollowActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnSuccessListener<Location> {

    private BebopDrone mBebopDrone;
    private Button follow;
    private Button pic_button;
    private double latitude;
    private double longitude;
    private double altitude;
    private boolean follow_state;
    private ARDiscoveryDeviceService service;
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
        follow_state = false;
        Intent intent = getIntent();
        service = intent.getParcelableExtra(Constant.DRONE_SERVICE);
        mBebopDrone = new BebopDrone(this, service);
        mBebopDrone.addListener(mBebopListener);
        myHandler = new Handler();
        myHandler.postDelayed(myRunnable,2000);
        follow = findViewById(R.id.button_follow);

        // Enables Always-on
        //setAmbientEnabled();
        GoogleApiClient gapi = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        gapi.connect();

        initActivity();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // show a loading view while the bebop drone is connecting
        if ((mBebopDrone != null) && !(ARCONTROLLER_DEVICE_STATE_ENUM.ARCONTROLLER_DEVICE_STATE_RUNNING.equals(mBebopDrone.getConnectionState()))) {
            // if the connection to the Bebop fails, finish the activity
            if (!mBebopDrone.connect()) {
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(myHandler != null)
            myHandler.removeCallbacks(myRunnable);
    }

    public void picMethod(View view){
        mBebopDrone.takePicture();
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
            longitude = loc.getLongitude();
            latitude = loc.getLatitude();
            altitude = loc.getAltitude();
            Log.i("CIO", "Location: " + lon  + " , " + lat );
            longit=lon;
            lati=lat;
            //Toast.makeText(this, lati.concat(",").concat(longit), Toast.LENGTH_SHORT).show();
            if(follow_state)
            {
                switch (mBebopDrone.getFlyingState()) {
                    case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_LANDED:
                        break;
                    case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_FLYING:
                        mBebopDrone.moveToLocation(latitude,longitude,altitude + 2);
                        break;
                    case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_HOVERING:
                        mBebopDrone.moveToLocation(latitude,longitude,altitude + 2);
                        break;
                    default:
                }
            }
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


    @Override
    public void onBackPressed() {
        if (mBebopDrone != null) {
            if (!mBebopDrone.disconnect()) {
                finish();
            }
        }
    }

    private void initActivity() {
        // Follow button
        follow = findViewById(R.id.button_follow);
        follow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if(!follow_state)
                {
                    switch (mBebopDrone.getFlyingState()) {
                        case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_LANDED:
                            mBebopDrone.takeOff();
                            break;
                        case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_FLYING:
                            break;
                        case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_HOVERING:
                            break;
                        default:
                    }
                }
                else
                {
                    switch (mBebopDrone.getFlyingState()) {
                        case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_LANDED:
                            break;
                        case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_FLYING:

                            break;
                        case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_HOVERING:
                            mBebopDrone.land();
                            break;
                        default:
                    }
                }
            }
        });

    }

    private final BebopDrone.Listener mBebopListener = new BebopDrone.Listener() {
        @Override
        public void onDroneConnectionChanged(ARCONTROLLER_DEVICE_STATE_ENUM state) {
            switch (state) {
                case ARCONTROLLER_DEVICE_STATE_RUNNING:
                    break;

                case ARCONTROLLER_DEVICE_STATE_STOPPED:
                    // if the deviceController is stopped, go back to the previous activity
                    finish();
                    break;

                default:
                    break;
            }
        }

        @Override
        public void onPilotingStateChanged(ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM state) {
            switch (state) {
                case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_LANDED:
                    break;
                case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_FLYING:
                    follow.setText("Stop Follow");
                    follow_state = true;
                    break;
                case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_HOVERING:
                    follow.setText("Stop Follow");
                    follow_state = true;
                    break;
                default:
            }
        }
        @Override
        public void onDownloadProgressed(String mediaName, int progress) {
        }

        @Override
        public void onFrameReceived(ARFrame frame) {
        }

        @Override
        public void onMatchingMediasFound(int nbMedias) {
        }

        @Override
        public void onBatteryChargeChanged(int batteryPercentage) {
        }

        @Override
        public void onPictureTaken(ARCOMMANDS_ARDRONE3_MEDIARECORDEVENT_PICTUREEVENTCHANGED_ERROR_ENUM error) {
        }

        @Override
        public void configureDecoder(ARControllerCodec codec) {
        }

        @Override
        public void onDownloadComplete(String mediaName) {
        }
    };
}
