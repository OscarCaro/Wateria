
package com.example.wateria.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.wateria.DataStructures.Settings;
import com.example.wateria.R;

import java.util.Calendar;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;

public class SettingsActivity extends AppCompatActivity {

    private Settings settings;
    private int notifHour;
    private int notifMinute;
    private int notifPostpone;

    private TimePickerDialog notTimingDialog;
    private TextView notTimingText;
    private TextView notPostponeTextViewNumber;
    private TextView notPostponeTextViewHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_layout);

        settings = new Settings(this);
        notifHour = settings.getNotifHour();
        notifMinute = settings.getNotifMinute();
        notifPostpone = settings.getNotifRepetInterval();

        notTimingText = findViewById(R.id.settings_notif_timing_hour);
        formatTimeTextView();
        notPostponeTextViewNumber = findViewById(R.id.settings_notif_remind_num_hours);
        notPostponeTextViewHours = findViewById(R.id.settings_notif_remind_text_hours);
        formatPostponeTextView();
    }

    //TODO: On accept button clicked: store times in sharedPrefs and trigger service to set alarm

    public void onNotStyleBoxClick(View view){

    }

    public void onNotTimingBoxClick(View view){
        int storedHour = settings.getNotifHour();
        int storedMinute = settings.getNotifMinute();

        if (notTimingDialog == null) {
            notTimingDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    notifHour = hourOfDay;
                    notifMinute = minute;
                    formatTimeTextView();
                }
            }, storedHour, storedMinute, true);
        }
        notTimingDialog.show();
        Window window = notTimingDialog.getWindow();
        window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
    }

    public void onNotRemindClick(View view){
        final MaterialNumberPicker numberPicker = new MaterialNumberPicker.Builder(this)
                .minValue(1)
                .maxValue(23)
                .defaultValue(1)
                .backgroundColor(getResources().getColor(R.color.colorWhite))
                .separatorColor(getResources().getColor(R.color.colorPrimaryFaded))
                .textColor(getResources().getColor(R.color.colorPrimary))
                .textSize(20)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .build();
        new AlertDialog.Builder(this)
                .setView(numberPicker)
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notifPostpone = numberPicker.getValue();
                        formatPostponeTextView();
                    }
                })
                .show();
    }

    public void onDeleteBoxClick(View view){

    }
    public void onLicensesBoxClick(View view){

    }
    public void onAboutBoxClick(View view){

    }

    private void formatTimeTextView(){
        String hourStr = String.format("%02d", notifHour);
        String minutesStr = String.format("%02d", notifMinute);
        String result = hourStr + ":" + minutesStr;
        notTimingText.setText(result);
    }

    private void formatPostponeTextView(){
        notPostponeTextViewNumber.setText(String.valueOf(notifPostpone));
        notPostponeTextViewHours.setText(getResources().getQuantityString(R.plurals.hours, notifPostpone));
    }
}
