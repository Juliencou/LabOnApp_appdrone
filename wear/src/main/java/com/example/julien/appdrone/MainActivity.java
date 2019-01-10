package com.example.julien.appdrone;

import android.support.wearable.activity.WearableActivity;
import android.view.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends WearableActivity implements View.OnClickListener {
    String[] items = new String[] {
    };
    private TextView mTextView;
    ArrayList<String> bag_array = new ArrayList<String>(Arrays.asList(items));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);

        Button boutonfollow = findViewById(R.id.button);
        boutonfollow.setOnClickListener(this);
        Button boutoncontrol = findViewById(R.id.button2);
        boutoncontrol.setOnClickListener(this);
        Button boutonbag = findViewById(R.id.button3);
        boutonbag.setOnClickListener(this);
        // Enables Always-on
        setAmbientEnabled();
    }
    static final int GET_BAG_ARRAY = 1;

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
        else if(view.getId() == R.id.button3){

            Intent intent3 = new Intent(this, ActivityBag.class);
            intent3.putExtra("array_list", bag_array);
            startActivityForResult(intent3,GET_BAG_ARRAY);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent)
    {
        if(requestCode == GET_BAG_ARRAY)
        {
            if(resultCode == RESULT_OK)
            {
                bag_array = resultIntent.getStringArrayListExtra("mydata");
            }
        }

    }
}
