package com.example.julien.appdrone;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.widget.AdapterView.OnItemSelectedListener;


public class ActivityBag extends Activity
{
    // Initializing a new String Array
    String[] items = new String[] {
            "Keys",
            "Phone"
    };
    final List<String> items_array = new ArrayList<String>(Arrays.asList(items));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bag);

        final Spinner spinner = (Spinner) findViewById(R.id.add_bag_item_spinner);

        final ListView item_list = (ListView) findViewById(R.id._item_bag_list);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, items_array);

        item_list.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                String item_selected = parent.getItemAtPosition(pos).toString();
                items_array.add(item_selected);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }

        });

    }
}
