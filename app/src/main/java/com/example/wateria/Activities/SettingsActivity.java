
package com.example.wateria.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.wateria.BootReceiver;
import com.example.wateria.DataStructures.PlantList;
import com.example.wateria.DataStructures.Settings;
import com.example.wateria.R;
import com.example.wateria.Utils.CommunicationKeys;

import java.util.ArrayList;
import java.util.Calendar;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;

public class SettingsActivity extends AppCompatActivity {

    private Settings settings;

    private TextView notifStyleTextView;
    private String[] stylesStringArray;

    private TextView notifTimingTextView;
    private TimePickerDialog notifTimingTimePickerDialog;

    private NotifPostpone notifPostpone;
    private Delete delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_layout);

        settings = new Settings(this);


        notifPostpone = new NotifPostpone();
        delete = new Delete();

        // Prepare Notification Style UI
        stylesStringArray = getResources().getStringArray(R.array.settings_notif_styles);
        notifStyleTextView = findViewById(R.id.settings_notif_style_type);
        notifStyleTextView.setText(stylesStringArray[settings.getNotifStyle().ordinal()]);
        //Prepare Notification Timing UI
        notifTimingTextView = findViewById(R.id.settings_notif_timing_hour);
        formatNotifTimingTextView();
    }

    private void formatNotifTimingTextView(){
        String unpadded = "" + settings.getNotifHour();
        String result = "00".substring(unpadded.length()) + unpadded;
        unpadded = "" + settings.getNotifMinute();
        result = result + ":" + "00".substring(unpadded.length()) + unpadded;
        notifTimingTextView.setText(result);
    }

    public void onNotifTimingBoxClick(View v){
        if (notifTimingTimePickerDialog == null) {
            notifTimingTimePickerDialog = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int clickedHour, int clickedMinute) {
                    settings.setNotifHour(clickedHour);
                    settings.setNotifMinute(clickedMinute);
                    //Cancel alarm with previous timing and set the new one
                    BootReceiver.cancelAlarm(SettingsActivity.this);
                    BootReceiver.setAlarm(SettingsActivity.this);

                    formatNotifTimingTextView();
                }
            }, settings.getNotifHour(), settings.getNotifMinute(), true);
        }
        notifTimingTimePickerDialog.show();
        Window window = notifTimingTimePickerDialog.getWindow();
        window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
    }


    public void onNotifStyleBoxClick(View v){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsActivity.this);
        mBuilder.setTitle("Choose a style");
        mBuilder.setSingleChoiceItems(stylesStringArray, settings.getNotifStyle().ordinal(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Store new value
                settings.setNotifStyle(Settings.NotificationStyle.values()[i]);
                // Update visual text
                notifStyleTextView.setText(stylesStringArray[i]);

                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public void onAboutBoxClick(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(this).inflate(
                R.layout.settings_about_dialog,
                (ConstraintLayout) findViewById(R.id.layout_dialog_container)
        );
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.about_accept_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        alertDialog.show();
    }

    public void onLicensesBoxClick(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(this).inflate(
                R.layout.settings_licenses_dialog,
                (ConstraintLayout) findViewById(R.id.layout_dialog_container)
        );

        //Make the links clickable
        TextView textView = (TextView) view.findViewById(R.id.settings_license_text);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.license_accept_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        alertDialog.show();
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

            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            builder.setMessage(R.string.settings_delete_all_warning_text)
                    .setTitle(R.string.settings_delete_all_warning_title)
                    .setCancelable(true)
                    .setPositiveButton(R.string.settings_delete_all_warning_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            hasBeenClicked = true;
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton(R.string.settings_delete_all_warning_no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        private void execute(){
            if(hasBeenClicked){
                PlantList plantList = PlantList.getInstance(SettingsActivity.this);
                plantList.deleteAll(SettingsActivity.this);
                setResult(CommunicationKeys.Settings_Main_ResultDeleteAll);
            }
        }
    }

    public void onAcceptButtonClicked(View view){

        //notifStyle.saveChanges();
        //notifTiming.saveChanges();
        notifPostpone.saveChanges();
        delete.execute();

        finish();       //Carry setResult(ResultDeleteAll) flag if applicable
    }
}
