package com.example.userinterface;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.threeten.bp.LocalDate;
import org.w3c.dom.Text;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;

public class NewPlantActivity extends AppCompatActivity {

    static final int NEW_PLANT_ACTIVITY_REQUEST_CODE = 1;  // The request code
    static final String NEW_PLANT_ACTIVITY_INTENT_PUTEXTRA_PLANT_KEY = "extra_plant";  //The key for the intent.putExtra in newPlant Act

    private TextView nameTextInputEditText;
    private TextInputLayout nameTextInputLayout;
    private ImageView iconImageView;
    private MaterialNumberPicker watFrequencyNumberPicker;
    private TextView firstWateringTextViewUpcoming;
    private MaterialNumberPicker firstWatNumberPicker;
    private TextView firstWatTextViewDays;
    private SwitchCompat firstWatSwitch;
    private Dialog dialog;

    private Integer iconCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plant);

        iconCode = 201;

        nameTextInputEditText = (TextView) findViewById(R.id.new_plant_options_name_textinputedittext);
        nameTextInputLayout = (TextInputLayout) findViewById(R.id.new_plant_options_name_textinputlayout);
        iconImageView = (ImageView) findViewById(R.id.new_plant_options_plant_icon_selected_icon);
        watFrequencyNumberPicker = (MaterialNumberPicker) findViewById(R.id.new_plant_options_watering_frequency_numberpicker);
        firstWateringTextViewUpcoming = (TextView) findViewById(R.id.new_plant_options_first_watering_text);
        firstWatNumberPicker = (MaterialNumberPicker) findViewById(R.id.new_plant_options_first_watering_numberpicker);
        firstWatTextViewDays = (TextView) findViewById(R.id.new_plant_options_first_watering_text_days);
        firstWatSwitch = (SwitchCompat) findViewById(R.id.new_plant_options_first_watering_icon_switch);

        firstWatNumberPicker.setEnabled(false);

        firstWatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked){
                    switchChangedOn();
                }
                else {
                    switchChangedOff();
                }
            }
        });

        nameTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (nameTextInputEditText.getText().length() > 0){
                    nameTextInputLayout.setError(null);
                }
            }
        });

        dialog = new Dialog(NewPlantActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_icon_layout);
        dialog.setTitle(R.string.dialog_title);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.new_plant_exit_dialog_text)
                .setCancelable(true)
                .setNegativeButton(R.string.new_plant_exit_dialog_accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NewPlantActivity.this.finish();
                    }
                })
                .setPositiveButton(R.string.new_plant_exit_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void acceptButtonClicked(View view){

        if (nameTextInputEditText.getText().length() > 0){
            String name = nameTextInputEditText.getText().toString();
            Integer wateringFreq =  watFrequencyNumberPicker.getValue();
            LocalDate nextWateringDate = computeNextWateringDate(wateringFreq);
            // IconCode assumed to be correct (if not set, it is the default 201)

            Plant plant = new Plant(name, iconCode, wateringFreq, nextWateringDate);
            Intent intent = new Intent();
            intent.putExtra(NEW_PLANT_ACTIVITY_INTENT_PUTEXTRA_PLANT_KEY, plant);
            setResult(RESULT_OK, intent);
            finish();

        } else{
            nameTextInputLayout.setError(getResources().getString(R.string.new_plant_options_name_error));
            //nameTextInputEditText.getBackground().setColorFilter(getResources().getColor(R.color.colorBlue), PorterDuff.Mode.SRC_ATOP);
            // display: Fill Name Data
        }
    }

    public void cancelButtonCliked(View view){
        onBackPressed();
    }

    public LocalDate computeNextWateringDate(Integer wateringFreq){
        LocalDate date = LocalDate.now();
        if (firstWatSwitch.isChecked()){
            date = date.plusDays(firstWatNumberPicker.getValue());
        } else {
            date = date.plusDays(wateringFreq);
        }
        return date;
    }

    public void iconClickedDisplayDialog(View view){

        //Dialog dialog = new Dialog(NewPlantActivity.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_select_icon_layout);
//        dialog.setTitle(R.string.dialog_title);
        dialog.show();
    }

    public void switchChangedOn(){
        int red = getResources().getColor(R.color.colorRed);

        firstWateringTextViewUpcoming.setTextColor(red);
        firstWatNumberPicker.setTextColor(red);
        firstWatTextViewDays.setTextColor(red);
        firstWatNumberPicker.setEnabled(true);
    }

    public void switchChangedOff(){
        int grey = getResources().getColor(R.color.colorGrey);

        firstWateringTextViewUpcoming.setTextColor(grey);
        firstWatNumberPicker.setTextColor(grey);
        firstWatTextViewDays.setTextColor(grey);
        firstWatNumberPicker.setEnabled(false);
    }

}
