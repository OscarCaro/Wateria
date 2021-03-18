package com.wateria.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.wateria.DataStructures.Plant;
import com.wateria.DataStructures.PlantList;
import com.wateria.NumberPickers.BlueNumberPicker;
import com.wateria.NumberPickers.RedNumberPicker;
import com.wateria.R;
import com.wateria.Utils.CommunicationKeys;
import com.wateria.Utils.IconTagDecoder;

import org.threeten.bp.LocalDate;

public class EditPlantActivity extends AppCompatActivity {

    private PlantList plantList;
    private Plant plantToEdit;
    private int iconId;
    private Integer positionInPlantList;

    private TextView nameTextInputEditText;
    private TextInputLayout nameTextInputLayout;
    private ImageButton iconImageView;
    private BlueNumberPicker watFrequencyNumberPicker;
    private RedNumberPicker firstWatNumberPicker;
    private BottomSheetDialog dialog;

    private View dialogView;
    private Thread dialogInflatorThread;

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
        iconImageView = (ImageButton) findViewById(R.id.edit_options_plant_icon);
        watFrequencyNumberPicker = (BlueNumberPicker) findViewById(R.id.edit_plant_options_watering_frequency_numberpicker);
        firstWatNumberPicker = (RedNumberPicker) findViewById(R.id.edit_plant_options_first_watering_numberpicker);

        prepareLayout();

        dialogInflatorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                dialogView = getLayoutInflater().inflate(R.layout.dialog_select_icon_layout, null);
            }
        });
        dialogInflatorThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(dialogInflatorThread.isAlive()){
            dialogInflatorThread.interrupt();
        }
    }

    private void prepareLayout(){
        nameTextInputEditText.setText(plantToEdit.getPlantName());
        iconImageView.setImageDrawable(IconTagDecoder.idToDrawable(this, iconId));
        watFrequencyNumberPicker.setMinValue(1);
        watFrequencyNumberPicker.setMaxValue(40);
        watFrequencyNumberPicker.setValue(plantToEdit.getWateringFrequency());
        watFrequencyNumberPicker.setWrapSelectorWheel(false);
        firstWatNumberPicker.setMinValue(0);
        firstWatNumberPicker.setMaxValue(40);
        firstWatNumberPicker.setValue(plantToEdit.getDaysRemaining());
        firstWatNumberPicker.setWrapSelectorWheel(false);

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

    public void onDeleteButtonClicked(View view){
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
        try {
            dialogInflatorThread.join();
        } catch (InterruptedException e) {
            dialogInflatorThread.interrupt();
            dialogView = getLayoutInflater().inflate(R.layout.dialog_select_icon_layout, null);
        }

        if (dialog == null){
            dialog = new BottomSheetDialog(this);
            dialog.setContentView(dialogView);
        }
        dialog.show();
    }

    public void onDialogChoiceClicked(View view){

        String tag = (String) view.getTag();        //Example: "res/drawable/ic_common_1.xml"

        iconId = IconTagDecoder.tagToId(this, tag);
        iconImageView.setImageDrawable(IconTagDecoder.idToDrawable(this, iconId));

        dialog.dismiss();
    }
}
