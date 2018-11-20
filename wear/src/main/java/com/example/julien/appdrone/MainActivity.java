package com.example.julien.appdrone;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends WearableActivity implements View.OnClickListener {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);

        Button boutonfollow = findViewById(R.id.button);
        boutonfollow.setOnClickListener(this);
        Button boutoncontrol = findViewById(R.id.button2);
        boutoncontrol.setOnClickListener(this);
        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    public void onClick(View view) {

        if (view.getId()==R.id.button){
            Intent intent=new Intent(MainActivity.this, activityfollow.class);
            MainActivity.this.startActivity(intent);

        }
        else if (view.getId()==R.id.button2){
            Intent intent2=new Intent(this, ActivityControl.class);
            startActivity(intent2);


        }


    }
}
