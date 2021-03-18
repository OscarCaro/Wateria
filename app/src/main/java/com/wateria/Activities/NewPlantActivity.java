package com.wateria.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.wateria.DataStructures.Plant;
import com.wateria.DataStructures.PlantList;
import com.wateria.NumberPickers.BlueNumberPicker;
import com.wateria.NumberPickers.RedNumberPicker;
import com.wateria.Animations.PulseAnim;
import com.wateria.R;
import com.wateria.Utils.CommunicationKeys;
import com.wateria.Utils.IconTagDecoder;
import com.wateria.Utils.MyFirebaseLogger;

import org.threeten.bp.LocalDate;

public class NewPlantActivity extends AppCompatActivity {

    private PlantList plantList;

    private TextView nameTextInputEditText;
    private TextInputLayout nameTextInputLayout;
    private ImageButton iconImageView;
    private BlueNumberPicker watFrequencyNumberPicker;
    private TextView firstWateringTextViewUpcoming;
    private RedNumberPicker firstWatNumberPicker;
    private TextView firstWatTextViewDays;
    private SwitchCompat firstWatSwitch;
    private BottomSheetDialog dialog;

    private View dialogView;
    private Thread dialogInflaterThread;

    private Integer iconId;
    private boolean showPulseAnim = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plant);

        plantList = PlantList.getInstance(this);

        iconId = IconTagDecoder.tagToId(this, (String) iconImageView.getTag());     // Default one: ic_common_1

        prepareUI();

        dialogInflaterThread = new Thread(new Runnable() {
            @Override
            public void run() {
                dialogView = getLayoutInflater().inflate(R.layout.dialog_select_icon_layout, null);
            }
        });
        dialogInflaterThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(dialogInflaterThread.isAlive()){
            dialogInflaterThread.interrupt();
        }
    }

    private void prepareUI(){
        nameTextInputEditText = findViewById(R.id.new_plant_options_name_textinputedittext);
        nameTextInputLayout = findViewById(R.id.new_plant_options_name_textinputlayout);
        iconImageView = findViewById(R.id.new_plant_options_plant_icon);
        watFrequencyNumberPicker = findViewById(R.id.new_plant_options_watering_frequency_numberpicker);
        firstWateringTextViewUpcoming = findViewById(R.id.new_plant_options_first_watering_text);
        firstWatNumberPicker = findViewById(R.id.new_plant_options_first_watering_numberpicker);
        firstWatTextViewDays =  findViewById(R.id.new_plant_options_first_watering_text_days);
        firstWatSwitch = findViewById(R.id.new_plant_options_first_watering_icon_switch);

        watFrequencyNumberPicker.setMinValue(1);
        watFrequencyNumberPicker.setMaxValue(40);
        watFrequencyNumberPicker.setValue(5);
        watFrequencyNumberPicker.setWrapSelectorWheel(false);

        firstWatNumberPicker.setEnabled(false);
        firstWatNumberPicker.setMinValue(0);
        firstWatNumberPicker.setMaxValue(40);
        firstWatNumberPicker.setValue(5);
        firstWatNumberPicker.setWrapSelectorWheel(false);

        firstWatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){ switchChangedOn(); }
                else { switchChangedOff(); }
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
        else if(showPulseAnim){
            new PulseAnim().show( (LottieAnimationView) findViewById(R.id.lottie));
        }
        else{
            String name = nameTextInputEditText.getText().toString();
            int wateringFreq =  watFrequencyNumberPicker.getValue();
            LocalDate nextWateringDate = computeNextWateringDate(wateringFreq);

            Plant plant = new Plant(name, iconId, wateringFreq, nextWateringDate, IconTagDecoder.idToDrawable(this, iconId));

            int pos = plantList.insertPlant(plant);

            MyFirebaseLogger.logNewPlant(this, plant);

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
        try {
            dialogInflaterThread.join();
        } catch (InterruptedException e) {
            dialogInflaterThread.interrupt();
            dialogView = getLayoutInflater().inflate(R.layout.dialog_select_icon_layout, null);
        }

        if (dialog == null){
            dialog = new BottomSheetDialog(this);
            dialog.setContentView(dialogView);
        }
        dialog.show();
        showPulseAnim = false;
    }

    public void switchChangedOn(){
        int red = getResources().getColor(R.color.colorRed);

        firstWateringTextViewUpcoming.setTextColor(red);
        firstWatNumberPicker.setValue(watFrequencyNumberPicker.getValue());
        firstWatNumberPicker.setVisibility(View.VISIBLE);
        firstWatTextViewDays.setVisibility(View.VISIBLE);
        firstWatNumberPicker.setEnabled(true);
    }

    public void switchChangedOff(){
        int grey = getResources().getColor(R.color.colorGrey);

        firstWateringTextViewUpcoming.setTextColor(grey);
        firstWatNumberPicker.setVisibility(View.INVISIBLE);
        firstWatTextViewDays.setVisibility(View.INVISIBLE);
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
