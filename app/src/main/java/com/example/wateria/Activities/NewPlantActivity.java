package com.example.wateria.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;

import com.example.wateria.DataStructures.Plant;
import com.example.wateria.R;
import com.example.wateria.Utils.CommunicationKeys;

import org.threeten.bp.LocalDate;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;

public class NewPlantActivity extends AppCompatActivity {

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

        iconCode = 1;

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
                .setTitle(R.string.new_plant_exit_dialog_title)
                .setCancelable(true)
                .setPositiveButton(R.string.new_plant_exit_dialog_accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NewPlantActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.new_plant_exit_dialog_cancel, new DialogInterface.OnClickListener() {
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
            intent.putExtra(CommunicationKeys.NewPlant_Main_ExtraPlant, plant);
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

//        Dialog dialog = new Dialog(NewPlantActivity.this);
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

    public void iconClicked(View view){
        Integer selected;

        switch (view.getId()){
            case R.id.dialog_2_1:
                iconCode = 201;
                selected = R.drawable.ic_common_1;
                break;
            case R.id.dialog_2_2:
                iconCode = 202;
                selected = R.drawable.ic_common_2_snakeplant;
                break;
            case R.id.dialog_2_3:
                iconCode = 203;
                selected = R.drawable.ic_common_3_sansevieria;
                break;
            case R.id.dialog_2_4:
                iconCode = 204;
                selected = R.drawable.ic_common_4_hanging;
                break;
            case R.id.dialog_2_5:
                iconCode = 205;
                selected = R.drawable.ic_common_5_spiderplant;
                break;
            case R.id.dialog_2_6:
                iconCode = 206;
                selected = R.drawable.ic_common_6_ivy;
                break;
            case R.id.dialog_2_7:
                iconCode = 207;
                selected = R.drawable.ic_common_7_bamboo;
                break;
            case R.id.dialog_2_8:
                iconCode = 208;
                selected = R.drawable.ic_common_8_monstera;
                break;
            case R.id.dialog_2_9:
                iconCode = 209;
                selected = R.drawable.ic_common_9_monsteraleaf;
                break;
            case R.id.dialog_2_10:
                iconCode = 210;
                selected = R.drawable.ic_common_10;
                break;

            case R.id.dialog_3_1:
                iconCode = 301;
                selected = R.drawable.ic_flower_1_red;
                break;
            case R.id.dialog_3_2:
                iconCode = 302;
                selected = R.drawable.ic_flower_2_orange;
                break;
            case R.id.dialog_3_3:
                iconCode = 303;
                selected = R.drawable.ic_flower_3_yellow;
                break;
            case R.id.dialog_3_4:
                iconCode = 304;
                selected = R.drawable.ic_flower_4_two;
                break;
            case R.id.dialog_3_5:
                iconCode = 305;
                selected = R.drawable.ic_flower_5;
                break;
            case R.id.dialog_3_6:
                iconCode = 306;
                selected = R.drawable.ic_flower_6_rose;
                break;
            case R.id.dialog_1_1:
                iconCode = 101;
                selected = R.drawable.ic_cactus_1;
                break;
            case R.id.dialog_1_2:
                iconCode = 102;
                selected = R.drawable.ic_cactus_2;
                break;
            case R.id.dialog_1_3:
                iconCode = 103;
                selected = R.drawable.ic_cactus_3;
                break;
            case R.id.dialog_1_4:
                iconCode = 104;
                selected = R.drawable.ic_cactus_4;
                break;
            case R.id.dialog_1_5:
                iconCode = 105;
                selected = R.drawable.ic_cactus_5;
                break;
            case R.id.dialog_1_6:
                iconCode = 106;
                selected = R.drawable.ic_cactus_6;
                break;
            case R.id.dialog_1_7:
                iconCode = 107;
                selected = R.drawable.ic_cactus_7_concara;
                break;
            case R.id.dialog_5_1:
                iconCode = 501;
                selected = R.drawable.ic_tree_1_bush;
                break;
            case R.id.dialog_5_2:
                iconCode = 502;
                selected = R.drawable.ic_tree_2_dracaena;
                break;
            case R.id.dialog_5_3:
                iconCode = 503;
                selected = R.drawable.ic_tree_3_joshuatree_jade;
                break;
            case R.id.dialog_5_4:
                iconCode = 504;
                selected = R.drawable.ic_tree_4_palm;
                break;
            case R.id.dialog_5_5:
                iconCode = 505;
                selected = R.drawable.ic_tree_5_pine;
                break;
            case R.id.dialog_5_6:
                iconCode = 506;
                selected = R.drawable.ic_tree_6_bonsai;
                break;
            case R.id.dialog_4_1:
                iconCode = 401;
                selected = R.drawable.ic_propagation_1;
                break;
            case R.id.dialog_4_2:
                iconCode = 402;
                selected = R.drawable.ic_propagation_2;
                break;
            case R.id.dialog_4_3:
                iconCode = 403;
                selected = R.drawable.ic_propagation_3;
                break;
            case R.id.dialog_6_1:
                iconCode = 601;
                selected = R.drawable.ic_veggies_1_lettuce;
                break;
            case R.id.dialog_6_2:
                iconCode = 602;
                selected = R.drawable.ic_veggies_2_carrot;
                break;
            case R.id.dialog_6_3:
                iconCode = 603;
                selected = R.drawable.ic_veggies_3_onion;
                break;
            case R.id.dialog_6_4:
                iconCode = 604;
                selected = R.drawable.ic_veggies_4_onion2;
                break;
            case R.id.dialog_6_5:
                iconCode = 605;
                selected = R.drawable.ic_veggies_5_garlic;
                break;
            case R.id.dialog_6_6:
                iconCode = 606;
                selected = R.drawable.ic_veggies_6_general;
                break;
            case R.id.dialog_6_7:
                iconCode = 607;
                selected = R.drawable.ic_veggies_7_tomato;
                break;
            case R.id.dialog_6_8:
                iconCode = 608;
                selected = R.drawable.ic_veggies_8_eggplant;
                break;
            case R.id.dialog_6_9:
                iconCode = 609;
                selected = R.drawable.ic_veggies_9_greenpepper;
                break;
            case R.id.dialog_6_10:
                iconCode = 610;
                selected = R.drawable.ic_veggies_10_redpepper;
                break;
            case R.id.dialog_6_11:
                iconCode = 611;
                selected = R.drawable.ic_veggies_11_avocado;
                break;
            case R.id.dialog_6_12:
                iconCode = 612;
                selected = R.drawable.ic_veggies_12_strawberry;
                break;

            default:
                iconCode = 201;
                selected = R.drawable.ic_common_1;
        }

        iconImageView.setImageDrawable(getResources().getDrawable(selected));
        dialog.dismiss();
    }

}
