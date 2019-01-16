package com.example.julien.appdrone;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.julien.appdrone.drone.BebopDrone;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.parrot.arsdk.arcommands.ARCOMMANDS_ARDRONE3_MEDIARECORDEVENT_PICTUREEVENTCHANGED_ERROR_ENUM;
import com.parrot.arsdk.arcommands.ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM;
import com.parrot.arsdk.arcontroller.ARCONTROLLER_DEVICE_STATE_ENUM;
import com.parrot.arsdk.arcontroller.ARControllerCodec;
import com.parrot.arsdk.arcontroller.ARFrame;
import com.parrot.arsdk.ardiscovery.ARDiscoveryDeviceService;


import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";

    // List of runtime permissions needed
    private static final String[] PERMISSIONS_NEEDED = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    // Code for permission request result handling
    private static final int REQUEST_CODE_PERMISSIONS_REQUEST = 1;

    private static final int GET_SERVICE_CODE = 1;
    private ARDiscoveryDeviceService service;
    private BebopDrone mBebopDrone;

    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // The BroadcastReceiver that tracks network connectivity changes.
    private NetworkReceiver receiver = new NetworkReceiver();

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Set<String> permissionsToRequest = new HashSet<>();
        for (String permission : PERMISSIONS_NEEDED) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    finish();
                    return;
                } else {
                    permissionsToRequest.add(permission);
                }
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                    REQUEST_CODE_PERMISSIONS_REQUEST);
        }


        updateUI();

        // Registers BroadcastReceiver to track network connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);

        String userEmail = getIntent().getStringExtra("googleUserEmail");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        TextView emailTextView = findViewById(R.id.userEmail);
        emailTextView.setText(userEmail);

        if (this.service != null) {
            mBebopDrone = new BebopDrone(this, service);
            mBebopDrone.addListener(mBebopListener);

            // after coming from DeviceListActivity and having selected drone, re-try download
            droneMediaDownload();
            updateUI();
        }

    }

    private void updateUI() {
        TextView mWifiValue = findViewById(R.id.wifiValue);
        TextView mDroneValue = findViewById(R.id.droneValue);
        TextView mGreeting = findViewById(R.id.greetingText);

        mGreeting.setText("connected as");

        if(wifiConnected){
            mWifiValue.setText("connected");
        }
        else{
            mWifiValue.setText("disconnected");
        }
        if(this.service != null){
            mDroneValue.setText("connected");
        }
        else{
            mDroneValue.setText("disconnected");
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dashboard, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_downloadDroneMedia:
                droneMediaDownload();
                return true;

            case R.id.action_sync:
                if(wifiConnected) {
                    firebaseUpload();
                }
                else{
                    Toast.makeText(this, "No wifi connection...", Toast.LENGTH_SHORT).show();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                this.service = data.getParcelableExtra("droneService");

            }

        }
    }

    private void droneMediaDownload() {
        if(this.service == null){
            Toast.makeText(this, "Add drone first...", Toast.LENGTH_SHORT).show();
            // discover drones
            Intent intent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(intent, GET_SERVICE_CODE);
        }
        else {
            if(mBebopDrone == null) {
                mBebopDrone = new BebopDrone(this, service);
                mBebopDrone.addListener(mBebopListener);
                updateUI();
            }
            // download from drone
            Toast.makeText(this, "Downloading...", Toast.LENGTH_SHORT).show();
            mBebopDrone.getLastFlightMedias();
        }
    }


    private void firebaseUpload() {
        // get list of available pics
        final ArrayList<String> filepaths = listImagesInFolder();

        // if any, upload
        if(!filepaths.isEmpty()) {
            for (int i = 0; i < filepaths.size(); i++) {

                String fileName = filepaths.get(i).substring(filepaths.get(i).lastIndexOf("/")+1);
                final StorageReference ref = storageReference.child("droneMedia/" + fileName);

                // check if image was already uploaded previously
                /*
                ref.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // file missing - upload it
                    }
                });
                */

                // upload
                ref.putFile(Uri.fromFile(new File(filepaths.get(i))))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            }
                        });


            }

        }
        else{
            Toast.makeText(this, "Nothing to upload.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregisters BroadcastReceiver when app is destroyed
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    @Override
    public void onStart () {
        super.onStart();

        updateConnectedFlags();
    }

    // Checks the network connection and sets the wifiConnected variables accordingly.
    public void updateConnectedFlags() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            //Toast.makeText(this, "GOT WIFI", Toast.LENGTH_SHORT).show();

        } else {
            wifiConnected = false;
            //Toast.makeText(this, "NO WIFI", Toast.LENGTH_SHORT).show();

        }
    }


    private ArrayList<String> listImagesInFolder() {

        ArrayList<String> filepaths = new ArrayList<String>();
        String folderName = Environment.getExternalStorageDirectory()+ File.separator + "droneMedia" + File.separator;

        File directory = new File(folderName);
        File[] files = directory.listFiles();

        if(files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getPath();
                filepaths.add(fileName);
            }
        }
        else{

        }

        return filepaths;
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

    public void signOut(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("forceLogout", true);
        startActivity(intent);
    }

    public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            updateConnectedFlags();
            updateUI();

            if(wifiConnected){
                firebaseUpload();
            }


        }

    }
}
