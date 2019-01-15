package com.example.julien.appdrone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import android.support.v4.app.FragmentManager;

import android.content.Intent;

import com.example.julien.appdrone.utils.Constant;

import java.util.ArrayList;

public class BagActivity extends FragmentActivity
{
    private static final String TAG = "BagActivity";

    int first_time = 1;
    FragmentManager fm = getSupportFragmentManager();
    String[] spinner_item_array = {
        "Keys",
        "Phone",
        "Wallet",
        "Water",
        "Earphones",
        "Battery"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bag);

        Bundle bundle = getIntent().getExtras();
        final ArrayList<String> items_array = bundle.getStringArrayList(Constant.BAG_TAG);


        final Spinner spinner = findViewById(R.id.add_bag_item_spinner);

        final spinnerAdapter adapter = new spinnerAdapter(BagActivity.this, android.R.layout.simple_list_item_1);
        adapter.addAll(spinner_item_array);
        adapter.add("Add item");
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount());

        final ListView item_list = findViewById(R.id._item_bag_list);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, items_array);

        item_list.setAdapter(arrayAdapter);

        Intent intent = new Intent();
        intent.putExtra(Constant.BAG_TAG, items_array);

        setResult(RESULT_OK, intent);

        if(items_array.isEmpty())
        {
            items_array.add("Empty bag");
            arrayAdapter.notifyDataSetChanged();
        }

        //After user clicks on the spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                if(spinner.getSelectedItem() == "This is Hint Text")
                {
                    //Do nothing.
                }
                else {
                    if (first_time == 0) {
                        if (items_array.contains("Empty bag")) {
                            items_array.remove("Empty bag");
                            arrayAdapter.notifyDataSetChanged();
                        }
                        String item_selected = parent.getItemAtPosition(pos).toString();

                        if (items_array.contains(item_selected)) {
                            //Do noting
                        } else {
                            items_array.add(item_selected);
                            Toast.makeText
                                    (getApplicationContext(), "New item added", Toast.LENGTH_SHORT)
                                    .show();
                            arrayAdapter.notifyDataSetChanged();
                        }

                    }
                    first_time = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }

        });

        //After user clicks on one of the item in the list
        item_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                if(!items_array.contains("Empty bag"))
                {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BagActivity.this);
                    alertDialogBuilder.setMessage("Delete selected item ?");
                    alertDialogBuilder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    items_array.remove(position);
                                    arrayAdapter.notifyDataSetChanged();
                                    /*if(items_array.isEmpty())
                                    {
                                        items_array.add("Empty bag");

                                        arrayAdapter.notifyDataSetChanged();
                                    }*/
                                }
                            });

                    alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

            }
        });

    }
}
