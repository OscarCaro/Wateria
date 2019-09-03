package com.example.userinterface;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.threetenabp.AndroidThreeTen;


import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class MainActivity extends AppCompatActivity implements ClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Plant> plantList = new ArrayList<Plant>();

    private Resources res;
    private SharedPreferences prefs;

    private String sharedPrefPlantListKey;
    private String sharedPrefFirstRunKey;

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

        res = getResources();
        //prefs = getPreferences(MODE_PRIVATE);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        sharedPrefPlantListKey = res.getString(R.string.shared_prefs_plantlist_key);
        sharedPrefFirstRunKey = res.getString(R.string.shared_prefs_firstrun_key);

        AndroidThreeTen.init(this);

        if (prefs.getBoolean(sharedPrefFirstRunKey, true)) {
            // Initialise the sharePrefs with empty plantList to avoid error
            saveArrayList(plantList);

            prefs.edit().putBoolean(sharedPrefFirstRunKey, false).apply();
        }

        plantList = getArrayList();

        setDaysRemainingList(plantList);
        Collections.sort(plantList);

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

        saveArrayList(plantList);
    }

    @Override
    public void onRowClicked(int position){
//        Toast toast1 = Toast.makeText(getApplicationContext(), "Row", Toast.LENGTH_SHORT);
//        toast1.show();
        Plant plantToEdit = plantList.get(position);
        Intent intent = new Intent(this, EditPlantActivity.class);
        intent.putExtra(EDIT_PLANT_ACTIVITY_INTENT_PUTEXTRA_PLANT_TO_EDIT_KEY, plantToEdit);
        intent.putExtra(EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_TO_EDIT_POSITION, position);
        startActivityForResult(intent, EDIT_PLANT_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onWateringButtonClicked(int position){
        Toast toast1 = Toast.makeText(getApplicationContext(), "Watering", Toast.LENGTH_SHORT);
        toast1.show();
        waterPlant(mAdapter, plantList, position);
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
                insertPlant(mAdapter, plantList, receivedPlant);
            }
            else if (resultCode == RESULT_CANCELED) {

            }
        }
        else if(requestCode == EDIT_PLANT_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Plant returnedPlant = data.getParcelableExtra(EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_RETURNED);
                Boolean daysRemChanged = data.getBooleanExtra(EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_RETURNED_DAYS_REMAINING_CHANGED, false);
                Integer positionInPlantList = data.getIntExtra(EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_RETURNED_POSITION, 0);

                modifyPlant(mAdapter, plantList, returnedPlant, positionInPlantList, daysRemChanged);

            }
            else if (resultCode == RESULT_DELETE){
                // delete plant
                Integer positionInPlantList = data.getIntExtra(EDIT_TEXT_ACTIVITY_INTENT_PUTEXTRA_PLANT_RETURNED_POSITION, 0);
                removePlant(mAdapter, plantList, positionInPlantList);
            }
            else if (resultCode == RESULT_CANCELED) {

            }
        }
    }


    public ArrayList<Plant> getArrayList(){
        Gson gson = new Gson();
        String json = prefs.getString(sharedPrefPlantListKey, null);
        Type type = new TypeToken<ArrayList<Plant>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveArrayList(ArrayList<Plant> list){
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(sharedPrefPlantListKey, json);
        editor.apply();
    }

    public void setDaysRemainingList (ArrayList<Plant> plantList){
        LocalDate todayDate = LocalDate.now();
        Plant currentPlant;
        int currentDaysRemaining;

        for (int i = 0; i < plantList.size(); i++){
            currentPlant = plantList.get(i);
            currentDaysRemaining = ((int) todayDate.until(currentPlant.getNextWateringDate(), ChronoUnit.DAYS));
            if (currentDaysRemaining < 0){
                currentDaysRemaining = 0;
            }
            currentPlant.setDaysRemaining(currentDaysRemaining);
        }
    }

    public void removePlant(RecyclerView.Adapter mAdapter, ArrayList<Plant> plantList, int position) {
        plantList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    public void insertPlant ( RecyclerView.Adapter mAdapter,ArrayList<Plant> plantList, Plant plant){
        // Compute daysRemaining of new:
        LocalDate todayDate = LocalDate.now();
        plant.setDaysRemaining(((int) todayDate.until(plant.getNextWateringDate(), ChronoUnit.DAYS)));
        // Insert to the list:
        plantList.add(plant);
        // Sort the list:
        Collections.sort(plantList);
        // Adapter:
        mAdapter.notifyItemInserted(plantList.indexOf(plant));
    }

    public void modifyPlant ( RecyclerView.Adapter mAdapter,ArrayList<Plant> plantList, Plant plant,
                              Integer positionInPlantList, Boolean daysRemChanged){
        // Compute daysRemaining of new:
        LocalDate todayDate = LocalDate.now();
        plant.setDaysRemaining(((int) todayDate.until(plant.getNextWateringDate(), ChronoUnit.DAYS)));
        if(daysRemChanged){
            plantList.set(positionInPlantList, plant);
            Collections.sort(plantList);
            mAdapter.notifyDataSetChanged();
        } else {
            // Insert to the list:
            plantList.set(positionInPlantList, plant);
            // Sort not necesary
            // Adapter:
            mAdapter.notifyItemChanged(positionInPlantList);
        }

    }

    public void waterPlant (RecyclerView.Adapter mAdapter, ArrayList<Plant> plantList, int position){
        Plant currentPlant = plantList.get(position);
        LocalDate date = LocalDate.now();
        date = date.plusDays(currentPlant.getWateringFrequency());
        currentPlant.setNextWateringDate(date);
        currentPlant.setDaysRemaining(currentPlant.getWateringFrequency());
        plantList.set(position, currentPlant);

        Collections.sort(plantList);

        mAdapter.notifyDataSetChanged();
    }

//    private void initList(ArrayList<Plant> plantList){
//
//        String[] names_array = res.getStringArray(R.array.plantNames);
//
//        plantList.add(new Plant(names_array[0], 203, 6, LocalDate.of(2019,7,22)));
//
//        plantList.add(new Plant(names_array[1], 206, 5, LocalDate.of(2019,7,20)));
//
//        plantList.add(new Plant(names_array[2], 502, 12, LocalDate.of(2019,7,25)));
//
//        plantList.add(new Plant(names_array[3], 504, 3, LocalDate.of(2019,7,26)));
//
//        plantList.add(new Plant(names_array[4], 201, 8, LocalDate.of(2019,7,25)));
//
//        plantList.add(new Plant(names_array[5], 101, 4, LocalDate.of(2019,7,23)));
//
//        plantList.add(new Plant(names_array[6], 208, 3, LocalDate.of(2019,7,18)));
//
//        plantList.add(new Plant(names_array[7], 202, 9, LocalDate.of(2019,7,31)));
//
//        plantList.add(new Plant(names_array[8], 210, 6, LocalDate.of(2019,7,20)));
//    }

}
