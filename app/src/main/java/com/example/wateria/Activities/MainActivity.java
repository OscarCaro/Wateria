package com.example.wateria.Activities;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wateria.DataStructures.Plant;
import com.example.wateria.Notifications.NotificationClass;
import com.example.wateria.Utils.CommunicationKeys;
import com.example.wateria.DataStructures.PlantList;
import com.example.wateria.R;
import com.example.wateria.RecyclerViewAdapter;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView noPlantsMessageTextView;

    private PlantList plantList;

    @Override
    public void onCreate(Bundle savedInstanceState){
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidThreeTen.init(this);

        plantList = PlantList.getInstance(this);

        noPlantsMessageTextView = (TextView) findViewById(R.id.noPlantsText);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewAdapter(this, plantList);
        mRecyclerView.setAdapter(mAdapter);

        checkNoPlantsMessage();
    }

    public void onRowClicked(int position){
        Intent intent = new Intent(this, EditPlantActivity.class);
        intent.putExtra(CommunicationKeys.Main_EditPlant_ExtraPlantPosition, position);
        startActivityForResult(intent, CommunicationKeys.Main_EditPlant_RequestCode);
    }

    public void onWateringButtonClicked(int position){
        int newPos = plantList.waterPlant(position);
        if (newPos != position){
            mRecyclerView.smoothScrollToPosition(newPos);
            mAdapter.notifyItemMoved(position, newPos); // Indicate possible change of position in list (after sorting)
        }
        mAdapter.notifyItemChanged(newPos);             // Indicate change in DaysRemaining field

        checkNoPlantsMessage();
    }

    public void onNewPlantButtonClicked(View view){
//        Intent intent = new Intent (this, NewPlantActivity.class);
//        startActivityForResult(intent, CommunicationKeys.Main_NewPlant_RequestCode);
        Context context = getApplicationContext();

        // Get a sublist filled with the plants that need to be watered (0 days remaining)
        ArrayList<Plant> zeroDaysList = PlantList.getInstance(this).get0daysRemSublist();

        if (zeroDaysList.size() > 0) {        // There are plants that need to be watered today
            //Compute notifications
            NotificationClass.createNotificationChannel(context);
            NotificationClass.pushNotification(context, zeroDaysList);
        }

    }

    public void onSettingsButtonClicked(View view){
        Intent intent = new Intent (this, SettingsActivity.class);
        startActivityForResult(intent, CommunicationKeys.Main_Settings_RequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);

        // Check which request we're responding to
        if (requestCode == CommunicationKeys.Main_NewPlant_RequestCode) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                int pos = data.getIntExtra(CommunicationKeys.NewPlant_Main_PlantPos, 0);
                mRecyclerView.smoothScrollToPosition(pos);
                mAdapter.notifyItemInserted(pos);
            }
        }
        else if(requestCode == CommunicationKeys.Main_EditPlant_RequestCode){
            if(resultCode == RESULT_OK){
                int prevPos = data.getIntExtra(CommunicationKeys.EditPlant_Main_PlantPrevPosition, 0);
                int newPos = data.getIntExtra(CommunicationKeys.EditPlant_Main_PlantNewPosition, 0);
                //mRecyclerView.smoothScrollToPosition(newPos);
                if (newPos != prevPos){
                    mAdapter.notifyItemMoved(prevPos, newPos);
                }
                mAdapter.notifyItemChanged(newPos);
            }
            else if (resultCode == CommunicationKeys.EditPlant_Main_ResultDelete){
                int prevPos = data.getIntExtra(CommunicationKeys.EditPlant_Main_PlantPrevPosition, 0);
                mRecyclerView.smoothScrollToPosition(prevPos);
                mAdapter.notifyItemRemoved(prevPos);
            }
        }
        else if(requestCode == CommunicationKeys.Main_Settings_RequestCode){
            if(resultCode == CommunicationKeys.Settings_Main_ResultDeleteAll){
                mAdapter.notifyDataSetChanged();
            }
        }

        checkNoPlantsMessage();
    }

    private void checkNoPlantsMessage(){
        if(plantList.getSize() <= 0){
            noPlantsMessageTextView.setVisibility(View.VISIBLE);
        }
        else{
            noPlantsMessageTextView.setVisibility(View.INVISIBLE);
        }
    }

}
