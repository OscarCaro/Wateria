package com.example.wateria.Activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.example.wateria.ClickListener;
import com.example.wateria.Utils.CommunicationKeys;
import com.example.wateria.DataStructures.Plant;
import com.example.wateria.DataStructures.PlantList;
import com.example.wateria.R;
import com.example.wateria.RecyclerViewAdapter;
import com.example.wateria.Utils.IconTagDecoder;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class MainActivity extends AppCompatActivity implements ClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private PlantList plantList;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidThreeTen.init(this);

        plantList = new PlantList(this);
        plantList.loadFromPrefs(true);

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
        intent.putExtra(CommunicationKeys.Main_EditPlant_ExtraPlantToEdit, plantToEdit);
        intent.putExtra(CommunicationKeys.Main_EditPlant_ExtraPlantPosition, position);
        startActivityForResult(intent, CommunicationKeys.Main_EditPlant_RequestCode);
    }

    @Override
    public void onWateringButtonClicked(int position){
        int newPos = plantList.waterPlant(position);
        if (newPos != position){
            mAdapter.notifyItemMoved(position, newPos); // Indicate possible change of position in list (after sorting)
        }
        mAdapter.notifyItemChanged(newPos);             // Indicate change in DaysRemaining field
    }

    public void startNewPlantActivity(View view){
        Intent intent = new Intent (this, NewPlantActivity.class);
        startActivityForResult(intent, CommunicationKeys.Main_NewPlant_RequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        // Check which request we're responding to
        if (requestCode == CommunicationKeys.Main_NewPlant_RequestCode) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Plant receivedPlant = data.getParcelableExtra(CommunicationKeys.NewPlant_Main_ExtraPlant);
                receivedPlant.setIcon(IconTagDecoder.idToDrawable(this, receivedPlant.getIconId()));
                int position = plantList.insertPlant(receivedPlant);
                mAdapter.notifyItemInserted(position);
            }
            else if (resultCode == RESULT_CANCELED) {

            }
        }
        else if(requestCode == CommunicationKeys.Main_EditPlant_RequestCode){
            if(resultCode == RESULT_OK){
                Plant returnedPlant = data.getParcelableExtra(CommunicationKeys.EditPlant_Main_ExtraPlantEdited);
                returnedPlant.setIcon(IconTagDecoder.idToDrawable(this, returnedPlant.getIconId()));
                Boolean daysRemChanged = data.getBooleanExtra(CommunicationKeys.EditPlant_Main_DaysRemChanged, false);
                Integer prevPos = data.getIntExtra(CommunicationKeys.EditPlant_Main_ExtraPlantEditedPosition, 0);

                int newPos = plantList.modifyPlant(returnedPlant, prevPos);
                if (newPos == prevPos){
                    mAdapter.notifyItemChanged(newPos);
                }
                else {
                    mAdapter.notifyItemMoved(prevPos, newPos);
                }

            }
            else if (resultCode == CommunicationKeys.EditPlant_Main_ResultDelete){
                // delete plant
                Integer positionInPlantList = data.getIntExtra(CommunicationKeys.EditPlant_Main_ExtraPlantEditedPosition, 0);
                plantList.removePlant(positionInPlantList);
                mAdapter.notifyItemRemoved(positionInPlantList);
            }
            else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

}
