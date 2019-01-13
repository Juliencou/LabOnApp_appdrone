package com.example.julien.appdrone;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ActivityControl extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
    }

    public void takeOfMethod(View view) {
        Button button = (Button)view;
        String a=button.getText().toString();
        if(a.equals("Take off")){
            button.setText("Land");

            //put here the code to take off
        }
        if(a.equals("Land")){
            button.setText("Take off");

            //put here the code to land
        }


    }

    public void changeControlMethod(View view) {
        Button button = (Button)view;
        String a=button.getText().toString();

        if (a.equals("Speed")){
            button.setText("Altitude");
        }
        if (a.equals("Altitude")){
            button.setText("Yaw");
        }
        if (a.equals("Yaw")){
            button.setText("Speed");
        }
        else{



        }

    }
}
