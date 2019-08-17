package com.example.userinterface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;

public class NewPlantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plant);

        final TextView firstWateringTextViewUpcoming = (TextView) findViewById(R.id.new_plant_options_first_watering_text);
        final MaterialNumberPicker firstWatNumberPicker = (MaterialNumberPicker) findViewById(R.id.new_plant_options_first_watering_numberpicker);
        final TextView firstWatTextViewDays = (TextView) findViewById(R.id.new_plant_options_first_watering_text_days);
        SwitchCompat firstWatSwitch = (SwitchCompat) findViewById(R.id.new_plant_options_first_watering_icon_switch);

        firstWatNumberPicker.setEnabled(false);
        firstWatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked){
                    switchChangedOn(compoundButton, firstWateringTextViewUpcoming, firstWatNumberPicker, firstWatTextViewDays);
                }
                else {
                    switchChangedOff(compoundButton, firstWateringTextViewUpcoming, firstWatNumberPicker, firstWatTextViewDays);
                }
            }
        });
    }

    public void switchChangedOn(CompoundButton switchFirstWatering, TextView firstWateringTextViewUpcoming,
                                MaterialNumberPicker firstWatNumberPicker, TextView firstWatTextViewDays ){
        int red = getResources().getColor(R.color.colorRed);

        firstWateringTextViewUpcoming.setTextColor(red);
        firstWatNumberPicker.setTextColor(red);
        firstWatTextViewDays.setTextColor(red);
        firstWatNumberPicker.setEnabled(true);
    }

    public void switchChangedOff(CompoundButton switchFirstWatering, TextView firstWateringTextViewUpcoming,
                                 MaterialNumberPicker firstWatNumberPicker, TextView firstWatTextViewDays ){
        int grey = getResources().getColor(R.color.colorGrey);

        firstWateringTextViewUpcoming.setTextColor(grey);
        firstWatNumberPicker.setTextColor(grey);
        firstWatTextViewDays.setTextColor(grey);
        firstWatNumberPicker.setEnabled(false);
    }

}
