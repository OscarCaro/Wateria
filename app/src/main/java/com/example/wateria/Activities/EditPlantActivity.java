package com.example.wateria.Activities;

import android.content.DialogInterface;
import android.content.Intent;

import com.example.wateria.DataStructures.PlantList;
import com.example.wateria.Utils.IconTagDecoder;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.icu.util.LocaleData;
import android.os.Bundle;
import androidx.appcompat.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wateria.DataStructures.Plant;
import com.example.wateria.R;
import com.example.wateria.Utils.CommunicationKeys;

import org.threeten.bp.LocalDate;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;

public class EditPlantActivity extends AppCompatActivity {

    private PlantList plantList;
    private Plant plantToEdit;
    private int iconId;
    private Integer positionInPlantList;

    private TextView nameTextInputEditText;
    private TextInputLayout nameTextInputLayout;
    private ImageView iconImageView;
    private MaterialNumberPicker watFrequencyNumberPicker;
    private TextView firstWateringTextViewUpcoming;
    private MaterialNumberPicker firstWatNumberPicker;
    private TextView firstWatTextViewDays;
    private SwitchCompat firstWatSwitch;
    private ImageView firstWatIcon;
    private TextView saveButtonText;
    private TextView deleteButtonText;
    private TextView activityTitleTextView;
    private ImageButton homeButton;
    private BottomSheetDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plant);

        Intent intent = getIntent();
        positionInPlantList = intent.getIntExtra(CommunicationKeys.Main_EditPlant_ExtraPlantPosition, 0);
        plantList = PlantList.getInstance(this);
        plantToEdit = plantList.get(positionInPlantList);

        //Set daysRemaining
        plantToEdit.computeDaysRemaining();

        iconId = plantToEdit.getIconId();

        nameTextInputEditText = (TextView) findViewById(R.id.edit_plant_options_name_textinputedittext);
        nameTextInputLayout = (TextInputLayout) findViewById(R.id.edit_plant_options_name_textinputlayout);
        iconImageView = (ImageView) findViewById(R.id.edit_plant_options_plant_icon_selected_icon);
        watFrequencyNumberPicker = (MaterialNumberPicker) findViewById(R.id.edit_plant_options_watering_frequency_numberpicker);
        firstWateringTextViewUpcoming = (TextView) findViewById(R.id.edit_plant_options_first_watering_text);
        firstWatNumberPicker = (MaterialNumberPicker) findViewById(R.id.edit_plant_options_first_watering_numberpicker);
        firstWatTextViewDays = (TextView) findViewById(R.id.edit_plant_options_first_watering_text_days);
        firstWatSwitch = (SwitchCompat) findViewById(R.id.edit_plant_options_first_watering_icon_switch);
        firstWatIcon = (ImageView) findViewById(R.id.edit_plant_options_first_watering_icon);
        saveButtonText = (TextView) findViewById(R.id.edit_plant_acceptbutton_text);
        deleteButtonText = (TextView) findViewById(R.id.edit_plant_cancelbutton_text);
        activityTitleTextView = (TextView) findViewById(R.id.edit_plant_logo);
        homeButton = (ImageButton) findViewById(R.id.edit_plant_button_middle_home);

        prepareLayout();

        nameTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {  }
            @Override
            public void afterTextChanged(Editable editable) {
                if (nameTextInputEditText.getText().length() > 0){
                    nameTextInputLayout.setError(null);
                }
            }
        });
    }

    private void prepareLayout(){
        firstWatSwitch.setVisibility(View.INVISIBLE);
        firstWatSwitch.setEnabled(false);
        firstWatIcon.setVisibility(View.VISIBLE);

        int red = getResources().getColor(R.color.colorRed);
        firstWateringTextViewUpcoming.setTextColor(red);
        firstWatNumberPicker.setTextColor(red);
        firstWatTextViewDays.setTextColor(red);
        firstWatNumberPicker.setEnabled(true);

        nameTextInputEditText.setText(plantToEdit.getPlantName());
        iconImageView.setImageDrawable(IconTagDecoder.idToDrawable(this, iconId));
        watFrequencyNumberPicker.setValue(plantToEdit.getWateringFrequency());
        firstWatNumberPicker.setValue(plantToEdit.getDaysRemaining());
        saveButtonText.setText(R.string.edit_plant_save_button_text);
        deleteButtonText.setText(R.string.edit_plant_delete_button_text);
        activityTitleTextView.setText(R.string.editPlantActivityTitle);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHomeButtonClicked(v);
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.edit_plant_exit_dialog_text)
                .setTitle(R.string.edit_plant_exit_dialog_title)
                .setCancelable(true)
                .setPositiveButton(R.string.new_plant_exit_dialog_accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditPlantActivity.this.finish();
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

    public void onAcceptButtonClicked(View view){

        if (nameTextInputEditText.getText().length() <= 0){
            // Empty name
            nameTextInputLayout.setError(getResources().getString(R.string.edit_plant_options_name_error));
        }
        else if (!nameTextInputEditText.getText().toString().equals(plantToEdit.getPlantName())
                && plantList.exists(nameTextInputEditText.getText().toString())){
            // Name has changed, and new name is already used by an existing plant
            nameTextInputLayout.setError(getResources().getString(R.string.new_plant_options_name_error_already_exists));
        }
        else{
            plantToEdit.setPlantName(nameTextInputEditText.getText().toString());
            plantToEdit.setWateringFrequency(watFrequencyNumberPicker.getValue());
            plantToEdit.setNextWateringDate(LocalDate.now().plusDays(firstWatNumberPicker.getValue()));
            plantToEdit.setIconId(iconId);
            plantToEdit.setDaysRemaining(firstWatNumberPicker.getValue());
            plantToEdit.setIcon(IconTagDecoder.idToDrawable(this, iconId));

            int newPos = plantList.modifyPlant(plantToEdit, positionInPlantList);

            Intent intent = new Intent();
            intent.putExtra(CommunicationKeys.EditPlant_Main_PlantPrevPosition, positionInPlantList);
            intent.putExtra(CommunicationKeys.EditPlant_Main_PlantNewPosition, newPos);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void onCancelButtonClicked(View view){
        //onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.edit_plant_delete_dialog_text)
                .setTitle(R.string.edit_plant_delete_dialog_title)
                .setCancelable(true)
                .setPositiveButton(R.string.edit_plant_delete_dialog_accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        plantList.removePlant(positionInPlantList);
                        Intent intent = new Intent();
                        intent.putExtra(CommunicationKeys.EditPlant_Main_PlantPrevPosition, positionInPlantList);
                        setResult(CommunicationKeys.EditPlant_Main_ResultDelete, intent);
                        finish();

                        //EditPlantActivity.this.finish();      TODO:needed?
                    }
                })
                .setNegativeButton(R.string.edit_plant_delete_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void onIconClicked(View view){
        if (dialog == null){
            View myView = getLayoutInflater().inflate(R.layout.dialog_select_icon_layout, null);

            dialog = new BottomSheetDialog(this);
            dialog.setContentView(myView);
        }

        dialog.show();
    }

    public void onHomeButtonClicked(View view){
        onBackPressed();
    }

    public void onDialogChoiceClicked(View view){

        String tag = (String) view.getTag();        //Example: "res/drawable/ic_common_1.xml"

        iconId = IconTagDecoder.tagToId(this, tag);
        iconImageView.setImageDrawable(IconTagDecoder.idToDrawable(this, iconId));

        dialog.dismiss();
    }
}
