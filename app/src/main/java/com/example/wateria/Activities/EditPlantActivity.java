package com.example.wateria.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.example.wateria.Utils.IconTagDecoder;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wateria.DataStructures.Plant;
import com.example.wateria.R;
import com.example.wateria.Utils.CommunicationKeys;

import org.threeten.bp.LocalDate;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;

public class EditPlantActivity extends AppCompatActivity {

    Plant plantToEdit;

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

    private Integer positionInPlantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plant);

        Intent intent = getIntent();
        plantToEdit = intent.getParcelableExtra(CommunicationKeys.Main_EditPlant_ExtraPlantToEdit);
        positionInPlantList = intent.getIntExtra(CommunicationKeys.Main_EditPlant_ExtraPlantPosition, 0);

        //Set daysRemaining
        plantToEdit.computeDaysRemaining();

        nameTextInputEditText = (TextView) findViewById(R.id.new_plant_options_name_textinputedittext);
        nameTextInputLayout = (TextInputLayout) findViewById(R.id.new_plant_options_name_textinputlayout);
        iconImageView = (ImageView) findViewById(R.id.new_plant_options_plant_icon_selected_icon);
        watFrequencyNumberPicker = (MaterialNumberPicker) findViewById(R.id.new_plant_options_watering_frequency_numberpicker);
        firstWateringTextViewUpcoming = (TextView) findViewById(R.id.new_plant_options_first_watering_text);
        firstWatNumberPicker = (MaterialNumberPicker) findViewById(R.id.new_plant_options_first_watering_numberpicker);
        firstWatTextViewDays = (TextView) findViewById(R.id.new_plant_options_first_watering_text_days);
        firstWatSwitch = (SwitchCompat) findViewById(R.id.new_plant_options_first_watering_icon_switch);
        firstWatIcon = (ImageView) findViewById(R.id.new_plant_options_first_watering_icon);
        saveButtonText = (TextView) findViewById(R.id.new_plant_acceptbutton_text);
        deleteButtonText = (TextView) findViewById(R.id.new_plant_cancelbutton_text);
        activityTitleTextView = (TextView) findViewById(R.id.new_plant_logo);
        homeButton = (ImageButton) findViewById(R.id.new_plant_button_middle_home);

        prepareLayout();

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

    public void prepareLayout(){
        firstWatSwitch.setVisibility(View.INVISIBLE);
        firstWatSwitch.setEnabled(false);
        firstWatIcon.setVisibility(View.VISIBLE);

        int red = getResources().getColor(R.color.colorRed);
        firstWateringTextViewUpcoming.setTextColor(red);
        firstWatNumberPicker.setTextColor(red);
        firstWatTextViewDays.setTextColor(red);
        firstWatNumberPicker.setEnabled(true);

        nameTextInputEditText.setText(plantToEdit.getPlantName());
        // iconImageView.setImageDrawable(getDrawablefromIconCode(plantToEdit.getIconIdx()));           // EEEEEEEEEEEEEEEEEEEE
        watFrequencyNumberPicker.setValue(plantToEdit.getWateringFrequency());
        firstWatNumberPicker.setValue(plantToEdit.getDaysRemaining());
        saveButtonText.setText(R.string.edit_plant_save_button_text);
        deleteButtonText.setText(R.string.edit_plant_delete_button_text);
        activityTitleTextView.setText(R.string.editPlantActivityTitle);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeButtonClicked(v);
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

    public void acceptButtonClicked(View view){

        if (nameTextInputEditText.getText().length() > 0){
            Boolean daysRemChanged = false;
            plantToEdit.setPlantName(nameTextInputEditText.getText().toString());
            plantToEdit.setWateringFrequency(watFrequencyNumberPicker.getValue());
            plantToEdit.setNextWateringDate(computeNextWateringDate());

            if(plantToEdit.getDaysRemaining() != firstWatNumberPicker.getValue()){
                plantToEdit.setDaysRemaining(firstWatNumberPicker.getValue());
                daysRemChanged = true;
            }
            // IconCode assumed to be set

            Intent intent = new Intent();
            intent.putExtra(CommunicationKeys.EditPlant_Main_ExtraPlantEdited, plantToEdit);
            intent.putExtra(CommunicationKeys.EditPlant_Main_DaysRemChanged, daysRemChanged);
            intent.putExtra(CommunicationKeys.EditPlant_Main_ExtraPlantEditedPosition, positionInPlantList);
            setResult(RESULT_OK, intent);
            finish();

        } else{
            nameTextInputLayout.setError(getResources().getString(R.string.new_plant_options_name_error));
        }
    }

    public void cancelButtonCliked(View view){
        //onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.edit_plant_delete_dialog_text)
                .setTitle(R.string.edit_plant_delete_dialog_title)
                .setCancelable(true)
                .setPositiveButton(R.string.edit_plant_delete_dialog_accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.putExtra(CommunicationKeys.EditPlant_Main_ExtraPlantEditedPosition, positionInPlantList);
                        setResult(CommunicationKeys.EditPlant_Main_ResultDelete, intent);
                        finish();

                        EditPlantActivity.this.finish();
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

    public void iconClickedDisplayDialog(View view){
        if (dialog == null){
            View myView = getLayoutInflater().inflate(R.layout.dialog_select_icon_layout, null);

            dialog = new BottomSheetDialog(this);
            dialog.setContentView(myView);
        }

        dialog.show();
    }

    public void homeButtonClicked(View view){
        onBackPressed();
    }

    public LocalDate computeNextWateringDate(){
        LocalDate date = LocalDate.now();
        date = date.plusDays(firstWatNumberPicker.getValue());
        return date;
    }

    public void iconClicked(View view){

        String tag = (String) view.getTag();        //Example: "res/drawable/ic_common_1.xml"
        iconImageView.setImageDrawable(IconTagDecoder.tagToDrawable(this, IconTagDecoder.trimTag(tag)));

        dialog.dismiss();
    }

}
