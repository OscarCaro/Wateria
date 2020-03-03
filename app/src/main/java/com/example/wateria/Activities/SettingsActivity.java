
package com.example.wateria.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.wateria.BootReceiver;
import com.example.wateria.DataStructures.Settings;
import com.example.wateria.R;

import java.util.ArrayList;
import java.util.Calendar;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;

public class SettingsActivity extends AppCompatActivity {

    private Changes changes;

    private NotifStyle notifStyle;

    private Settings settings;
    private int notifHour;
    private int notifMinute;
    private int notifPostpone;

    private TimePickerDialog notTimingDialog;
    private TextView notTimingText;
    private TextView notPostponeTextViewNumber;
    private TextView notPostponeTextViewHours;

    private class Changes{
        boolean notifStyleChanged;
        boolean notifTimingChanged;
        boolean notifPostponeChanged;
        boolean deletePressed;
        private Changes(){
            notifStyleChanged = false;
            notifPostponeChanged = false;
            notifTimingChanged = false;
            deletePressed = false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_layout);

        changes = new Changes();

        settings = new Settings(this);
        notifHour = settings.getNotifHour();
        notifMinute = settings.getNotifMinute();
        notifPostpone = settings.getNotifRepetInterval();

        notTimingText = findViewById(R.id.settings_notif_timing_hour);
        formatTimeTextView();
        notPostponeTextViewNumber = findViewById(R.id.settings_notif_remind_num_hours);
        notPostponeTextViewHours = findViewById(R.id.settings_notif_remind_text_hours);
        formatPostponeTextView();

        notifStyle = new NotifStyle();
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

    public void onAcceptButtonClicked(View view){

        notifStyle.saveChanges();


        if (changes.notifTimingChanged){
            settings.setNotifHour(notifHour);
            settings.setNotifMinute(notifMinute);
            //Cancel alarm with previous timing and set the new one
            BootReceiver.cancelAlarm(this);
            BootReceiver.setAlarm(this);
        }
        if (changes.notifPostponeChanged){
            settings.setNotifRepetInterval(notifPostpone);
        }
        if(changes.deletePressed){

        }
        finish();
    }

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
                    changes.notifTimingChanged = true;
                }
            }, storedHour, storedMinute, true);
        }
        notTimingDialog.show();
        Window window = notTimingDialog.getWindow();
        window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
    }

    public void onNotRemindClick(View view){

//        Dialog dialog = new Dialog(this);
//        dialog.setContentView(R.layout.time_picker_dialog_layout);
//        dialog.show();
//        Window window = dialog.getWindow();
//        window.setBackgroundDrawableResource(android.R.color.transparent);
//        window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);


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
                        changes.notifPostponeChanged = true;
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
