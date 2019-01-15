package com.example.julien.appdrone;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.wearable.activity.WearableActivity;
import android.view.View;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.julien.appdrone.drone.BebopDrone;
import com.example.julien.appdrone.utils.Constant;
import com.parrot.arsdk.ardiscovery.ARDiscoveryDeviceService;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends WearableActivity {
    private static final String TAG = "MainActivity";

    String[] items = new String[] {
    };
    private TextView mTextView;
    ArrayList<String> bag_array = new ArrayList<String>(Arrays.asList(items));

    private BebopDrone mBebopDrone;

    private ARDiscoveryDeviceService service;

    static final int GET_BAG_ARRAY = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Enables Always-on
        setAmbientEnabled();

        //mTextView = findViewById(R.id.text);

        //Intent intent = getIntent();
        //this.service = intent.getParcelableExtra(Constant.DRONE_SERVICE);
        //mBebopDrone = new BebopDrone(this, service);
    }


    @Override
    protected void onStart() {
      super.onStart();
    }


    @Override
    public void onClick(View view) {
        Intent intent = null;

        if (view.getId()==R.id.followButton){
            intent = new Intent(MainActivity.this, FollowActivity.class);
            MainActivity.this.startActivity(intent);

        }
        else if (view.getId()==R.id.controlButton){
            intent = new Intent(this, ControlActivity.class);
            intent.putExtra(Constant.DRONE_SERVICE, service);
            startActivity(intent);
        }
        else if(view.getId() == R.id.bagButton){
            intent = new Intent(this, BagActivity.class);
            intent.putExtra(Constant.BAG_TAG, bag_array);
            startActivityForResult(intent,GET_BAG_ARRAY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent)
    {
        if(requestCode == GET_BAG_ARRAY && requestCode == RESULT_OK)
        {
            bag_array = resultIntent.getStringArrayListExtra(Constant.BAG_TAG);
        }

    }


}
