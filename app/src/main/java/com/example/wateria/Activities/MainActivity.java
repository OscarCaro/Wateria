package com.example.wateria.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.wateria.ClickListener;
import com.example.wateria.DataStructures.Plant;
import com.example.wateria.DataStructures.PlantList;
import com.example.wateria.EditPlantActivity;
import com.example.wateria.NewPlantActivity;
import com.example.wateria.R;
import com.example.wateria.RecyclerViewAdapter;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class MainActivity extends AppCompatActivity implements ClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private PlantList plantList;

    private SharedPreferences prefs;

    private final String sharedPrefFirstRunKey = "firstrunkey";

    static final int NEW_PLANT_ACTIVITY_REQUEST_CODE = 1;  // The request code
    static final String NEW_PLANT_ACTIVITY_INTENT_PUTEXTRA_PLANT_KEY = "extra_plant";  //The key for the intent.putExtra in newPlant Act

    static final int EDIT_PLANT_ACTIVITY_REQUEST_CODE = 2;  // The request code
    static final String EDIT_PLANT_ACTIVITY_INTENT_PUTEXTRA_PLANT_TO_EDIT_KEY = "extra_plant_to_edit";
    static final String EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_RETURNED = "extra_plant_returned";
    static final String EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_RETURNED_DAYS_REMAINING_CHANGED = "extra_plant_returned_days_remaining_changed";
    static final String EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_TO_EDIT_POSITION = "extra_plant_to_edit_position";
    static final String EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_RETURNED_POSITION = "extra_plant_returned_position";
    static final Integer RESULT_DELETE = 3;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        AndroidThreeTen.init(this);

        plantList = new PlantList(this);

        if (prefs.getBoolean(sharedPrefFirstRunKey, true)) {
            prefs.edit().putBoolean(sharedPrefFirstRunKey, false).apply();
        }
        else {
            plantList.loadFromPrefs();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewAdapter(this, plantList);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onPause() {
        super.onPause();

        plantList.saveToPrefs();
    }

    @Override
    public void onRowClicked(int position){
        Plant plantToEdit = plantList.get(position);
        Intent intent = new Intent(this, EditPlantActivity.class);
        intent.putExtra(EDIT_PLANT_ACTIVITY_INTENT_PUTEXTRA_PLANT_TO_EDIT_KEY, plantToEdit);
        intent.putExtra(EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_TO_EDIT_POSITION, position);
        startActivityForResult(intent, EDIT_PLANT_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onWateringButtonClicked(int position){
        int newPos = plantList.waterPlant(position);
        mAdapter.notifyItemMoved(position, newPos);
    }

    public void startNewPlantActivity(View view){
        Intent intent = new Intent (this, NewPlantActivity.class);
        startActivityForResult(intent, NEW_PLANT_ACTIVITY_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == NEW_PLANT_ACTIVITY_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Plant receivedPlant = data.getParcelableExtra(NEW_PLANT_ACTIVITY_INTENT_PUTEXTRA_PLANT_KEY);
                int position = plantList.insertPlant(receivedPlant);
                mAdapter.notifyItemInserted(position);
            }
            else if (resultCode == RESULT_CANCELED) {

            }
        }
        else if(requestCode == EDIT_PLANT_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Plant returnedPlant = data.getParcelableExtra(EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_RETURNED);
                Boolean daysRemChanged = data.getBooleanExtra(EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_RETURNED_DAYS_REMAINING_CHANGED, false);
                Integer prevPos = data.getIntExtra(EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_RETURNED_POSITION, 0);

                int newPos = plantList.modifyPlant(returnedPlant, prevPos);
                if (newPos == prevPos){
                    mAdapter.notifyItemChanged(newPos);
                }
                else {
                    mAdapter.notifyItemMoved(prevPos, newPos);
                }

            }
            else if (resultCode == RESULT_DELETE){
                // delete plant
                Integer positionInPlantList = data.getIntExtra(EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_RETURNED_POSITION, 0);
                plantList.removePlant(positionInPlantList);
                mAdapter.notifyItemRemoved(positionInPlantList);
            }
            else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

}
