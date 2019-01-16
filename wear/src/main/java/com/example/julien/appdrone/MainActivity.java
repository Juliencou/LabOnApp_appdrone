package com.example.julien.appdrone;

import android.media.Image;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.julien.appdrone.drone.BebopDrone;
import com.example.julien.appdrone.utils.Constant;
import com.parrot.arsdk.ardiscovery.ARDiscoveryDeviceService;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends WearableActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int FOLLOW = 2;
    private static final int CONTROL_CODE = 3;

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

        ImageButton button_bag = findViewById(R.id.bagButton);
        button_bag.setOnClickListener(this);
        ImageButton button_follow = findViewById(R.id.followButton);
        button_follow.setOnClickListener(this);
        ImageButton button_control = findViewById(R.id.controlButton);
        button_control.setOnClickListener(this);


        Intent intent = getIntent();
        service = intent.getParcelableExtra(Constant.DRONE_SERVICE);
        Toast.makeText(this, service.toString(), Toast.LENGTH_SHORT).show();
        //mBebopDrone = new BebopDrone(this, service);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onClick(View view) {
        Intent intent = null;

        if (view.getId()==R.id.followButton){
            intent = new Intent(this, FollowActivity.class);
            intent.putExtra(Constant.DRONE_SERVICE, service);
            startActivityForResult(intent, FOLLOW);

        }
        else if (view.getId()==R.id.controlButton){
            intent = new Intent(this, ControlActivity.class);
            intent.putExtra(Constant.DRONE_SERVICE, service);
            startActivityForResult(intent, CONTROL_CODE);
        }
        else if(view.getId() == R.id.bagButton){
            intent = new Intent(this, BagActivity.class);
            intent.putExtra(Constant.BAG_TAG, bag_array);
            intent.putExtra(Constant.DRONE_SERVICE, service);
            startActivityForResult(intent,GET_BAG_ARRAY);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent)
    {
        if(requestCode == GET_BAG_ARRAY && resultCode == RESULT_OK)
        {
            bag_array = resultIntent.getStringArrayListExtra(Constant.BAG_TAG);
            service= resultIntent.getParcelableExtra(Constant.DRONE_SERVICE);
            Toast.makeText(this, service.toString(), Toast.LENGTH_SHORT).show();
        }
        if(requestCode == FOLLOW && resultCode == RESULT_OK)
        {
            service = resultIntent.getParcelableExtra(Constant.DRONE_SERVICE);
            Toast.makeText(this, service.toString(), Toast.LENGTH_SHORT).show();

        }
        if(requestCode == CONTROL_CODE && resultCode == RESULT_OK)
        {
            service= resultIntent.getParcelableExtra(Constant.DRONE_SERVICE);
            Toast.makeText(this, service.toString(), Toast.LENGTH_SHORT).show();
        }

    }
}
