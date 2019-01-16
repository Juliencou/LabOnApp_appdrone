package com.example.julien.appdrone;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.ImageButton;

import com.example.julien.appdrone.drone.BebopDrone;
import com.example.julien.appdrone.utils.Constant;
import com.parrot.arsdk.arcommands.ARCOMMANDS_ARDRONE3_MEDIARECORDEVENT_PICTUREEVENTCHANGED_ERROR_ENUM;
import com.parrot.arsdk.arcommands.ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM;
import com.parrot.arsdk.arcontroller.ARCONTROLLER_DEVICE_STATE_ENUM;
import com.parrot.arsdk.arcontroller.ARControllerCodec;
import com.parrot.arsdk.arcontroller.ARFrame;
import com.parrot.arsdk.ardiscovery.ARDiscoveryDeviceService;


public class ControlActivity extends WearableActivity {
    private static final String TAG = "ControlActivity";
    private BebopDrone mBebopDrone;

    private ImageButton mButtonTakeOffLand;
    private ImageButton mButtonEmergency;
    private ImageButton mButtonCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        Intent intent = getIntent();
        ARDiscoveryDeviceService service = intent.getParcelableExtra(Constant.DRONE_SERVICE);
        mBebopDrone = new BebopDrone(this, service);
        mBebopDrone.addListener(mBebopListener);

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
    public void onBackPressed() {
        if (mBebopDrone != null) {
            if (!mBebopDrone.disconnect()) {
                finish();
            }
        }
    }

    private void initActivity() {
        // Land-Takeoff button
        mButtonTakeOffLand = findViewById(R.id.takeOffLandButton);
        mButtonTakeOffLand.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (mBebopDrone.getFlyingState()) {
                    case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_LANDED:
                        mBebopDrone.takeOff();
                        break;
                    case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_FLYING:
                    case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_HOVERING:
                        mBebopDrone.land();
                        break;
                    default:
                }
            }
        });

        // Emergency button
        mButtonEmergency = findViewById(R.id.emergencyButton);
        mButtonEmergency.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mBebopDrone.emergency();
            }
        });

        // Take picture
        mButtonCamera =  findViewById(R.id.takePictureButton);
        mButtonCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mBebopDrone.takePicture();
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
                    mButtonTakeOffLand.setImageDrawable(getResources().getDrawable(R.drawable.takeoff, getApplicationContext().getTheme()));
                    mButtonTakeOffLand.setEnabled(true);
                    break;
                case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_FLYING:
                case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_HOVERING:
                    mButtonTakeOffLand.setImageDrawable(getResources().getDrawable(R.drawable.land, getApplicationContext().getTheme()));
                    mButtonTakeOffLand.setEnabled(true);
                    break;
                default:
                    mButtonTakeOffLand.setEnabled(false);
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
