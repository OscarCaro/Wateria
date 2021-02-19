package com.wateria.Activities;

import android.content.Intent;

import com.wateria.DataStructures.PlantList;
import com.wateria.Utils.IconTagDecoder;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wateria.DataStructures.Plant;
import com.wateria.R;
import com.wateria.Utils.CommunicationKeys;

import org.threeten.bp.LocalDate;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;

public class NewPlantActivity extends AppCompatActivity {

    private PlantList plantList;

    private TextView nameTextInputEditText;
    private TextInputLayout nameTextInputLayout;
    private ImageView iconImageView;
    private MaterialNumberPicker watFrequencyNumberPicker;
    private TextView firstWateringTextViewUpcoming;
    private MaterialNumberPicker firstWatNumberPicker;
    private TextView firstWatTextViewDays;
    private SwitchCompat firstWatSwitch;
    private BottomSheetDialog dialog;

    private Integer iconId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plant);

        plantList = PlantList.getInstance(this);

        nameTextInputEditText = (TextView) findViewById(R.id.new_plant_options_name_textinputedittext);
        nameTextInputLayout = (TextInputLayout) findViewById(R.id.new_plant_options_name_textinputlayout);
        iconImageView = (ImageView) findViewById(R.id.new_plant_options_plant_icon_selected_icon);
        watFrequencyNumberPicker = (MaterialNumberPicker) findViewById(R.id.new_plant_options_watering_frequency_numberpicker);
        firstWateringTextViewUpcoming = (TextView) findViewById(R.id.new_plant_options_first_watering_text);
        firstWatNumberPicker = (MaterialNumberPicker) findViewById(R.id.new_plant_options_first_watering_numberpicker);
        firstWatTextViewDays = (TextView) findViewById(R.id.new_plant_options_first_watering_text_days);
        firstWatSwitch = (SwitchCompat) findViewById(R.id.new_plant_options_first_watering_icon_switch);

        iconId = IconTagDecoder.tagToId(this, (String) iconImageView.getTag());     // Default one: ic_common_1

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
    }

    public void onAcceptButtonClicked(View view){

        if (nameTextInputEditText.getText().length() <= 0){
            // Empty name
            nameTextInputLayout.setError(getResources().getString(R.string.new_plant_options_name_error));
        }
        else if (plantList.exists(nameTextInputEditText.getText().toString())){
            // Name has changed, and new name is already used by an existing plant
            nameTextInputLayout.setError(getResources().getString(R.string.new_plant_options_name_error_already_exists));
        }
        else{
            String name = nameTextInputEditText.getText().toString();
            int wateringFreq =  watFrequencyNumberPicker.getValue();
            LocalDate nextWateringDate = computeNextWateringDate(wateringFreq);

            Plant plant = new Plant(name, iconId, wateringFreq, nextWateringDate, IconTagDecoder.idToDrawable(this, iconId));

            int pos = plantList.insertPlant(plant);

            Intent intent = new Intent();
            intent.putExtra(CommunicationKeys.NewPlant_Main_PlantPos, pos);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void onCancelButtonClicked(View view){
        finish();
    }

    public void onIconClicked(View view){

        if (dialog == null){
            View myView = getLayoutInflater().inflate(R.layout.dialog_select_icon_layout, null);

            dialog = new BottomSheetDialog(this);
            dialog.setContentView(myView);
        }

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

    public void onDialogChoiceClicked(View view){

        String tag = (String) view.getTag();        //Example: "res/drawable/ic_common_1.xml"

        iconId = IconTagDecoder.tagToId(this, tag);
        iconImageView.setImageDrawable(IconTagDecoder.idToDrawable(this, iconId));

        dialog.dismiss();
    }

    private LocalDate computeNextWateringDate(Integer wateringFreq){
        LocalDate date = LocalDate.now();
        if (firstWatSwitch.isChecked()){
            date = date.plusDays(firstWatNumberPicker.getValue());
        } else {
            date = date.plusDays(wateringFreq);
        }
        return date;
    }

}
