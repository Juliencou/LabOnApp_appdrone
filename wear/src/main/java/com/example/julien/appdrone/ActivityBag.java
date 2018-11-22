package com.example.julien.appdrone;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ActivityBag extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bag);
        final ListView item_list = (ListView) findViewById(R.id._item_bag_list);
        final Button add_button = (Button) findViewById(R.id._add_bag_item_button);

        // Initializing a new String Array
        String[] items = new String[] {
                "Keys",
                "Phone"
        };
        final List<String> items_array = new ArrayList<String>(Arrays.asList(items));
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, items_array);

        item_list.setAdapter(arrayAdapter);

        add_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });
    }
}
