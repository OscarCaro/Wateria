package com.example.userinterface;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;

public class EditPlantActivity extends AppCompatActivity {

    Plant plantToEdit;

    static final String EDIT_PLANT_ACTIVITY_INTENT_PUTEXTRA_PLANT_TO_EDIT_KEY = "extra_plant_to_edit";
    static final String EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_RETURNED = "extra_plant_returned";
    static final String EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_RETURNED_DAYS_REMAINING_CHANGED = "extra_plant_returned_days_remaining_changed";
    static final String EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_TO_EDIT_POSITION = "extra_plant_to_edit_position";
    static final String EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_RETURNED_POSITION = "extra_plant_returned_position";
    static final Integer RESULT_DELETE = 3;

    private TextView nameTextInputEditText;
    private TextInputLayout nameTextInputLayout;
    private ImageView iconImageView;
    private MaterialNumberPicker watFrequencyNumberPicker;
    private TextView firstWateringTextViewUpcoming;
    private MaterialNumberPicker firstWatNumberPicker;
    private TextView firstWatTextViewDays;
    private SwitchCompat firstWatSwitch;
    private ImageView firstWatIcon;
    private Dialog dialog;
    private TextView saveButtonText;
    private TextView deleteButtonText;
    private TextView activityTitleTextView;
    private ImageButton homeButton;

    private Integer positionInPlantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plant);

        Intent intent = getIntent();
        plantToEdit = intent.getParcelableExtra(EDIT_PLANT_ACTIVITY_INTENT_PUTEXTRA_PLANT_TO_EDIT_KEY);
        positionInPlantList = intent.getIntExtra(EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_TO_EDIT_POSITION, 0);

        //Set daysRemaining
        LocalDate todayDate = LocalDate.now();
        plantToEdit.setDaysRemaining((int) todayDate.until(plantToEdit.getNextWateringDate(), ChronoUnit.DAYS));
        if (plantToEdit.getDaysRemaining() < 0){
            plantToEdit.setDaysRemaining(0);
        }

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

        dialog = new Dialog(EditPlantActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_icon_layout);
        dialog.setTitle(R.string.dialog_title);
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
        iconImageView.setImageDrawable(getDrawablefromIconCode(plantToEdit.getImageCode()));
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
            intent.putExtra(EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_RETURNED, plantToEdit);
            intent.putExtra(EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_RETURNED_DAYS_REMAINING_CHANGED, daysRemChanged);
            intent.putExtra(EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_RETURNED_POSITION, positionInPlantList);
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
                .setCancelable(true)
                .setPositiveButton(R.string.edit_plant_delete_dialog_accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.putExtra(EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_RETURNED_POSITION, positionInPlantList);
                        setResult(RESULT_DELETE, intent);
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
        Integer selected;

        switch (view.getId()){
            case R.id.dialog_2_1:
                plantToEdit.setImageCode(201);
                selected = R.drawable.ic_common_1;
                break;
            case R.id.dialog_2_2:
                plantToEdit.setImageCode(202);
                selected = R.drawable.ic_common_2_snakeplant;
                break;
            case R.id.dialog_2_3:
                plantToEdit.setImageCode(203);
                selected = R.drawable.ic_common_3_sansevieria;
                break;
            case R.id.dialog_2_4:
                plantToEdit.setImageCode(204);
                selected = R.drawable.ic_common_4_hanging;
                break;
            case R.id.dialog_2_5:
                plantToEdit.setImageCode(205);
                selected = R.drawable.ic_common_5_spiderplant;
                break;
            case R.id.dialog_2_6:
                plantToEdit.setImageCode(206);
                selected = R.drawable.ic_common_6_ivy;
                break;
            case R.id.dialog_2_7:
                plantToEdit.setImageCode(207);
                selected = R.drawable.ic_common_7_bamboo;
                break;
            case R.id.dialog_2_8:
                plantToEdit.setImageCode(208);
                selected = R.drawable.ic_common_8_monstera;
                break;
            case R.id.dialog_2_9:
                plantToEdit.setImageCode(209);
                selected = R.drawable.ic_common_9_monsteraleaf;
                break;
            case R.id.dialog_2_10:
                plantToEdit.setImageCode(210);
                selected = R.drawable.ic_common_10;
                break;

            case R.id.dialog_3_1:
                plantToEdit.setImageCode(301);
                selected = R.drawable.ic_flower_1_red;
                break;
            case R.id.dialog_3_2:
                plantToEdit.setImageCode(302);
                selected = R.drawable.ic_flower_2_orange;
                break;
            case R.id.dialog_3_3:
                plantToEdit.setImageCode(303);
                selected = R.drawable.ic_flower_3_yellow;
                break;
            case R.id.dialog_3_4:
                plantToEdit.setImageCode(304);
                selected = R.drawable.ic_flower_4_two;
                break;
            case R.id.dialog_3_5:
                plantToEdit.setImageCode(305);
                selected = R.drawable.ic_flower_5;
                break;
            case R.id.dialog_3_6:
                plantToEdit.setImageCode(306);
                selected = R.drawable.ic_flower_6_rose;
                break;
            case R.id.dialog_1_1:
                plantToEdit.setImageCode(101);
                selected = R.drawable.ic_cactus_1;
                break;
            case R.id.dialog_1_2:
                plantToEdit.setImageCode(102);
                selected = R.drawable.ic_cactus_2;
                break;
            case R.id.dialog_1_3:
                plantToEdit.setImageCode(103);
                selected = R.drawable.ic_cactus_3;
                break;
            case R.id.dialog_1_4:
                plantToEdit.setImageCode(104);
                selected = R.drawable.ic_cactus_4;
                break;
            case R.id.dialog_1_5:
                plantToEdit.setImageCode(105);
                selected = R.drawable.ic_cactus_5;
                break;
            case R.id.dialog_1_6:
                plantToEdit.setImageCode(106);
                selected = R.drawable.ic_cactus_6;
                break;
            case R.id.dialog_1_7:
                plantToEdit.setImageCode(107);
                selected = R.drawable.ic_cactus_7_concara;
                break;
            case R.id.dialog_5_1:
                plantToEdit.setImageCode(501);
                selected = R.drawable.ic_tree_1_bush;
                break;
            case R.id.dialog_5_2:
                plantToEdit.setImageCode(502);
                selected = R.drawable.ic_tree_2_dracaena;
                break;
            case R.id.dialog_5_3:
                plantToEdit.setImageCode(503);
                selected = R.drawable.ic_tree_3_joshuatree_jade;
                break;
            case R.id.dialog_5_4:
                plantToEdit.setImageCode(504);
                selected = R.drawable.ic_tree_4_palm;
                break;
            case R.id.dialog_5_5:
                plantToEdit.setImageCode(505);
                selected = R.drawable.ic_tree_5_pine;
                break;
            case R.id.dialog_5_6:
                plantToEdit.setImageCode(506);
                selected = R.drawable.ic_tree_6_bonsai;
                break;
            case R.id.dialog_4_1:
                plantToEdit.setImageCode(401);
                selected = R.drawable.ic_propagation_1;
                break;
            case R.id.dialog_4_2:
                plantToEdit.setImageCode(402);
                selected = R.drawable.ic_propagation_2;
                break;
            case R.id.dialog_4_3:
                plantToEdit.setImageCode(403);
                selected = R.drawable.ic_propagation_3;
                break;
            case R.id.dialog_6_1:
                plantToEdit.setImageCode(601);
                selected = R.drawable.ic_veggies_1_lettuce;
                break;
            case R.id.dialog_6_2:
                plantToEdit.setImageCode(602);
                selected = R.drawable.ic_veggies_2_carrot;
                break;
            case R.id.dialog_6_3:
                plantToEdit.setImageCode(603);
                selected = R.drawable.ic_veggies_3_onion;
                break;
            case R.id.dialog_6_4:
                plantToEdit.setImageCode(604);
                selected = R.drawable.ic_veggies_4_onion2;
                break;
            case R.id.dialog_6_5:
                plantToEdit.setImageCode(605);
                selected = R.drawable.ic_veggies_5_garlic;
                break;
            case R.id.dialog_6_6:
                plantToEdit.setImageCode(606);
                selected = R.drawable.ic_veggies_6_general;
                break;
            case R.id.dialog_6_7:
                plantToEdit.setImageCode(607);
                selected = R.drawable.ic_veggies_7_tomato;
                break;
            case R.id.dialog_6_8:
                plantToEdit.setImageCode(608);
                selected = R.drawable.ic_veggies_8_eggplant;
                break;
            case R.id.dialog_6_9:
                plantToEdit.setImageCode(609);
                selected = R.drawable.ic_veggies_9_greenpepper;
                break;
            case R.id.dialog_6_10:
                plantToEdit.setImageCode(610);
                selected = R.drawable.ic_veggies_10_redpepper;
                break;
            case R.id.dialog_6_11:
                plantToEdit.setImageCode(611);
                selected = R.drawable.ic_veggies_11_avocado;
                break;
            case R.id.dialog_6_12:
                plantToEdit.setImageCode(612);
                selected = R.drawable.ic_veggies_12_strawberry;
                break;

            default:
                plantToEdit.setImageCode(201);
                selected = R.drawable.ic_common_1;
        }

        iconImageView.setImageDrawable(getResources().getDrawable(selected));
        dialog.dismiss();
    }

    public Drawable getDrawablefromIconCode(Integer iconCode){
        Drawable drawable;

        switch (iconCode) {
            case 101:
                drawable = (getResources().getDrawable(R.drawable.ic_cactus_1));
                break;
            case 102:
                drawable = getResources().getDrawable(R.drawable.ic_cactus_2);
                break;
            case 103:
                drawable = getResources().getDrawable(R.drawable.ic_cactus_3);
                break;
            case 104:
                drawable = getResources().getDrawable(R.drawable.ic_cactus_4);
                break;
            case 105:
                drawable = getResources().getDrawable(R.drawable.ic_cactus_5);
                break;
            case 106:
                drawable = getResources().getDrawable(R.drawable.ic_cactus_6);
                break;
            case 107:
                drawable = getResources().getDrawable(R.drawable.ic_cactus_7_concara);
                break;
            case 201:
                drawable = getResources().getDrawable(R.drawable.ic_common_1);
                break;
            case 202:
                drawable = getResources().getDrawable(R.drawable.ic_common_2_snakeplant);
                break;
            case 203:
                drawable = getResources().getDrawable(R.drawable.ic_common_3_sansevieria);
                break;
            case 204:
                drawable = getResources().getDrawable(R.drawable.ic_common_4_hanging);
                break;
            case 205:
                drawable = getResources().getDrawable(R.drawable.ic_common_5_spiderplant);
                break;
            case 206:
                drawable = getResources().getDrawable(R.drawable.ic_common_6_ivy);
                break;
            case 207:
                drawable = getResources().getDrawable(R.drawable.ic_common_7_bamboo);
                break;
            case 208:
                drawable = getResources().getDrawable(R.drawable.ic_common_8_monstera);
                break;
            case 209:
                drawable = getResources().getDrawable(R.drawable.ic_common_9_monsteraleaf);
                break;
            case 210:
                drawable = getResources().getDrawable(R.drawable.ic_common_10);
                break;
            case 301:
                drawable = getResources().getDrawable(R.drawable.ic_flower_1_red);
                break;
            case 302:
                drawable = getResources().getDrawable(R.drawable.ic_flower_2_orange);
                break;
            case 303:
                drawable = getResources().getDrawable(R.drawable.ic_flower_3_yellow);
                break;
            case 304:
                drawable = getResources().getDrawable(R.drawable.ic_flower_4_two);
                break;
            case 305:
                drawable = getResources().getDrawable(R.drawable.ic_flower_5);
                break;
            case 306:
                drawable = getResources().getDrawable(R.drawable.ic_flower_6_rose);
                break;
            case 401:
                drawable = getResources().getDrawable(R.drawable.ic_propagation_1);
                break;
            case 402:
                drawable = getResources().getDrawable(R.drawable.ic_propagation_2);
                break;
            case 403:
                drawable = getResources().getDrawable(R.drawable.ic_propagation_3);
                break;
            case 501:
                drawable = getResources().getDrawable(R.drawable.ic_tree_1_bush);
                break;
            case 502:
                drawable = getResources().getDrawable(R.drawable.ic_tree_2_dracaena);
                break;
            case 503:
                drawable = getResources().getDrawable(R.drawable.ic_tree_3_joshuatree_jade);
                break;
            case 504:
                drawable = getResources().getDrawable(R.drawable.ic_tree_4_palm);
                break;
            case 505:
                drawable = getResources().getDrawable(R.drawable.ic_tree_5_pine);
                break;
            case 506:
                drawable = getResources().getDrawable(R.drawable.ic_tree_6_bonsai);
                break;
            case 601:
                drawable = getResources().getDrawable(R.drawable.ic_veggies_1_lettuce);
                break;
            case 602:
                drawable = getResources().getDrawable(R.drawable.ic_veggies_2_carrot);
                break;
            case 603:
                drawable = getResources().getDrawable(R.drawable.ic_veggies_3_onion);
                break;
            case 604:
                drawable = getResources().getDrawable(R.drawable.ic_veggies_4_onion2);
                break;
            case 605:
                drawable = getResources().getDrawable(R.drawable.ic_veggies_5_garlic);
                break;
            case 606:
                drawable = getResources().getDrawable(R.drawable.ic_veggies_6_general);
                break;
            case 607:
                drawable = getResources().getDrawable(R.drawable.ic_veggies_7_tomato);
                break;
            case 608:
                drawable = getResources().getDrawable(R.drawable.ic_veggies_8_eggplant);
                break;
            case 609:
                drawable = getResources().getDrawable(R.drawable.ic_veggies_9_greenpepper);
                break;
            case 610:
                drawable = getResources().getDrawable(R.drawable.ic_veggies_10_redpepper);
                break;
            case 611:
                drawable = getResources().getDrawable(R.drawable.ic_veggies_11_avocado);
                break;
            case 612:
                drawable = getResources().getDrawable(R.drawable.ic_veggies_12_strawberry);
                break;
            default:
                drawable = getResources().getDrawable(R.drawable.ic_common_10);
                break;
        }
        return drawable;
    }
}
