package com.example.julien.appdrone;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class activityfollow extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activityfollow);
        Button tkoff= findViewById(R.id.button6);
        Button follow = findViewById(R.id.button7);
        tkoff.setText("Take off");
        follow.setText("Follow");
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
}
