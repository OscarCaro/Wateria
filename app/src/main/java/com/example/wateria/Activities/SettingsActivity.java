
package com.example.wateria.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.wateria.BootReceiver;
import com.example.wateria.DataStructures.PlantList;
import com.example.wateria.DataStructures.Settings;
import com.example.wateria.R;

import java.util.ArrayList;
import java.util.Calendar;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;

public class SettingsActivity extends AppCompatActivity {

    private Settings settings;

    private NotifStyle notifStyle;
    private NotifTiming notifTiming;
    private NotifPostpone notifPostpone;
    private Delete delete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_layout);

        settings = new Settings(this);

        notifStyle = new NotifStyle();
        notifTiming = new NotifTiming();
        notifPostpone = new NotifPostpone();
        delete = new Delete();
    }

    private class NotifStyle implements View.OnClickListener {

        private ConstraintLayout box;
        private TextView textView;
        private Settings.NotificationStyle style;
        private boolean unsavedChanges;
        private String[] stylesStringArray;

        NotifStyle(){
            style = settings.getNotifStyle();
            unsavedChanges = false;
            stylesStringArray = getResources().getStringArray(R.array.settings_notif_styles);

            box = findViewById(R.id.settings_notif_style_box);
            box.setOnClickListener(this);

            textView = findViewById(R.id.settings_notif_style_type);
            formatTextView();
        }

        public void onClick(View view){
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsActivity.this);
            mBuilder.setTitle("Choose a style");
            mBuilder.setSingleChoiceItems(stylesStringArray, style.ordinal(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    style = Settings.NotificationStyle.values()[i];
                    formatTextView();
                    unsavedChanges = true;
                    dialogInterface.dismiss();
                }
            });

            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        }

        void saveChanges(){
            if (unsavedChanges){
                settings.setNotifStyle(style);
                unsavedChanges = false;
            }
        }

        private void formatTextView(){
            textView.setText(stylesStringArray[style.ordinal()]);
        }
    }

    private class NotifTiming implements View.OnClickListener {

        private ConstraintLayout box;
        private TextView textView;
        private TimePickerDialog timePickerDialog;
        private boolean unsavedChanges;
        private int hour;
        private int minute;

        NotifTiming(){
            hour = settings.getNotifHour();
            minute = settings.getNotifMinute();
            unsavedChanges = false;

            box = findViewById(R.id.settings_notif_timing_box);
            box.setOnClickListener(this);

            textView = findViewById(R.id.settings_notif_timing_hour);
            formatTextView();
        }

        @Override
        public void onClick(View v) {
            if (timePickerDialog == null) {
                timePickerDialog = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int clickedHour, int clickedMinute) {
                        hour = clickedHour;
                        minute = clickedMinute;
                        unsavedChanges = true;
                        formatTextView();
                    }
                }, hour, minute, true);
            }
            timePickerDialog.show();
            Window window = timePickerDialog.getWindow();
            window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        }

        private void saveChanges(){
            if (unsavedChanges){
                settings.setNotifHour(hour);
                settings.setNotifMinute(minute);
                //Cancel alarm with previous timing and set the new one
                BootReceiver.cancelAlarm(SettingsActivity.this);
                BootReceiver.setAlarm(SettingsActivity.this);
            }
        }

        private void formatTextView(){
            String unpadded = "" + hour;
            String result = "00".substring(unpadded.length()) + unpadded;
            unpadded = "" + minute;
            result = result + ":" + "00".substring(unpadded.length()) + unpadded;
            textView.setText(result);
        }
    }

    private class NotifPostpone implements View.OnClickListener {

        private ConstraintLayout box;
        private TextView numberTextView;
        private TextView hourTextView;
        private boolean unsavedChanges;
        private int hours;

        NotifPostpone(){
            hours = settings.getNotifRepetInterval();
            unsavedChanges = false;

            box = findViewById(R.id.settings_notif_remind_box);
            box.setOnClickListener(this);

            numberTextView = findViewById(R.id.settings_notif_remind_num_hours);
            hourTextView = findViewById(R.id.settings_notif_remind_text_hours);
            formatTextView();
        }

        @Override
        public void onClick(View v) {
            //        Dialog dialog = new Dialog(this);
//        dialog.setContentView(R.layout.time_picker_dialog_layout);
//        dialog.show();
//        Window window = dialog.getWindow();
//        window.setBackgroundDrawableResource(android.R.color.transparent);
//        window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

            final MaterialNumberPicker numberPicker = new MaterialNumberPicker.Builder(SettingsActivity.this)
                    .minValue(1)
                    .maxValue(23)
                    .defaultValue(hours)
                    .backgroundColor(getResources().getColor(R.color.colorWhite))
                    .separatorColor(getResources().getColor(R.color.colorPrimaryFaded))
                    .textColor(getResources().getColor(R.color.colorPrimary))
                    .textSize(20)
                    .enableFocusability(false)
                    .wrapSelectorWheel(true)
                    .build();
            new AlertDialog.Builder(SettingsActivity.this)
                    .setView(numberPicker)
                    .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            hours = numberPicker.getValue();
                            formatTextView();
                            unsavedChanges = true;
                        }
                    })
                    .show();
        }

        private void saveChanges(){
            if (unsavedChanges){
                settings.setNotifRepetInterval(hours);
            }
        }

        private void formatTextView(){
            numberTextView.setText(String.valueOf(hours));
            hourTextView.setText(getResources().getQuantityString(R.plurals.hours, hours));
        }
    }

    private class Delete implements View.OnClickListener{

        private ConstraintLayout box;
        private boolean hasBeenClicked;

        Delete(){
            box = findViewById(R.id.settings_delete_box);
            box.setOnClickListener(this);
            hasBeenClicked = false;
        }

        @Override
        public void onClick(View v) {
            hasBeenClicked = true;
        }

        private void execute(){
            if(hasBeenClicked){
                //PlantList.deleteAll(SettingsActivity.this);
            }
        }
    }

    public void onAcceptButtonClicked(View view){

        notifStyle.saveChanges();
        notifTiming.saveChanges();
        notifPostpone.saveChanges();
        delete.execute();

        finish();
    }
}
