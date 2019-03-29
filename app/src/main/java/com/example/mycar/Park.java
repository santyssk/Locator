package com.example.mycar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

public class Park extends AppCompatActivity {

    String park_time_selected,remind_time_selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park);

        final TextView parked_address,park_time,remind_time;
        final Button park,cancel,replace;
        final Spinner park_time_value,remind_time_value;

        parked_address = findViewById(R.id.parkedaddress);
        park_time = findViewById(R.id.parktext);
        remind_time = findViewById(R.id.remindtext);
        park = findViewById(R.id.park);
        cancel = findViewById(R.id.cancel);
        replace = findViewById(R.id.replace);
        park_time_value = findViewById(R.id.parktime);
        remind_time_value = findViewById(R.id.remindtime);

        final SharedPreferences sharedPreferences = getSharedPreferences("Preference", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("location_address",null);
        LocationDetail locationDetail = gson.fromJson(json,LocationDetail.class);
        String parked_address_value = "";
        if(locationDetail!=null)
            parked_address_value = locationDetail.address;

        park_time_selected = sharedPreferences.getString("park_time_preference","None");
        park_time_value.setSelection(((ArrayAdapter<String>)park_time_value.getAdapter()).getPosition(park_time_selected));
        remind_time_selected = sharedPreferences.getString("remind_time_preference","None");
        remind_time_value.setSelection(((ArrayAdapter<String>)remind_time_value.getAdapter()).getPosition(remind_time_selected));
        if(parked_address_value!="") {
            parked_address.setText("Already parked at\n"+parked_address_value+"\nReplace?");
            park.setVisibility(View.INVISIBLE);
            park_time.setVisibility(View.INVISIBLE);
            remind_time.setVisibility(View.INVISIBLE);
            park_time_value.setVisibility(View.INVISIBLE);
            remind_time_value.setVisibility(View.INVISIBLE);
        }
        else{
             parked_address.setVisibility(View.INVISIBLE);
             cancel.setVisibility(View.INVISIBLE);
             replace.setVisibility(View.INVISIBLE);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        replace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  parked_address.setVisibility(View.INVISIBLE);
                  cancel.setVisibility(View.INVISIBLE);
                  replace.setVisibility(View.INVISIBLE);
                  park.setVisibility(View.VISIBLE);
                  park_time.setVisibility(View.VISIBLE);
                  remind_time.setVisibility(View.VISIBLE);
                  park_time_value.setVisibility(View.VISIBLE);
                  remind_time_value.setVisibility(View.VISIBLE);
            }
        });

        park.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                park_time_selected = park_time_value.getSelectedItem().toString();
                remind_time_selected = remind_time_value.getSelectedItem().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("park_time_preference",park_time_selected);
                editor.putString("remind_time_preference",remind_time_selected);
                long time = 0;
                switch(park_time_selected){
                    case "30 minutes" : time = 1800000;
                        break;
                    case "60 minutes" : time = 3600000;
                        break;
                    case "90 minutes" : time = 5400000;
                        break;
                    case "2 hours" : time = 7200000;
                        break;
                    case "4 hours" : time = 14400000;
                        break;
                }
                if(park_time_selected.equalsIgnoreCase("None")==false)
                    editor.putLong("time_left",System.currentTimeMillis()+time);
                else
                    editor.putLong("time_left",0);
                time = 0;
                switch(remind_time_selected){
                    case "5 minutes" : time = 300000;
                        break;
                    case "10 minutes" : time = 600000;
                        break;
                    case "15 minutes" : time = 900000;
                        break;
                }
                if(park_time_selected.equalsIgnoreCase("None")==false)
                    editor.putLong("remind_left",time);
                else
                    editor.putLong("remind_left",0);
                editor.commit();
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}
