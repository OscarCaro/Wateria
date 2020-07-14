package com.example.wateria.Activities;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.example.wateria.ClickListener;
import com.example.wateria.Services.CheckPlantlistForNotificationService;
import com.example.wateria.Services.WaterSinglePlantFromNotificationActionService;
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
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidThreeTen.init(this);

        plantList = PlantList.getInstance(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewAdapter(this, plantList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //plantList.loadFromPrefs(true);

        mAdapter.notifyDataSetChanged();        // TODO: needed?
    }


    @Override
    public void onRowClicked(int position){
        Plant plantToEdit = plantList.get(position);
        Intent intent = new Intent(this, EditPlantActivity.class);
        //intent.putExtra(CommunicationKeys.Main_EditPlant_ExtraPlantToEdit, plantToEdit);
        intent.putExtra(CommunicationKeys.Main_EditPlant_ExtraPlantPosition, position);
        startActivityForResult(intent, CommunicationKeys.Main_EditPlant_RequestCode);
    }

    @Override
    public void onWateringButtonClicked(int position){
        int newPos = plantList.waterPlant(position);
        if (newPos != position){
            mRecyclerView.smoothScrollToPosition(newPos);
            mAdapter.notifyItemMoved(position, newPos); // Indicate possible change of position in list (after sorting)
        }
        mAdapter.notifyItemChanged(newPos);             // Indicate change in DaysRemaining field
    }

    public void startNewPlantActivity(View view){
        Intent intent = new Intent (this, NewPlantActivity.class);
        startActivityForResult(intent, CommunicationKeys.Main_NewPlant_RequestCode);
    }

    public void onSettingsButtonClicked(View view){
        Intent intent = new Intent (this, SettingsActivity.class);
        startActivity(intent);
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
                //plantList.saveToPrefs();
//                mRecyclerView.smoothScrollToPosition(position);
//                mAdapter.notifyItemInserted(position);
            }
            else if (resultCode == RESULT_CANCELED) {

            }
        }
        else if(requestCode == CommunicationKeys.Main_EditPlant_RequestCode){
            if(resultCode == RESULT_OK){
                Integer newPos = data.getIntExtra(CommunicationKeys.EditPlant_Main_ExtraPlantEditedPosition, 0);

                mAdapter.notifyDataSetChanged();
//                mRecyclerView.smoothScrollToPosition(newPos);
//                if (newPos != prevPos){
//                    mAdapter.notifyItemMoved(prevPos, newPos);
//                }
//                mAdapter.notifyItemChanged(newPos);
            }
            else if (resultCode == CommunicationKeys.EditPlant_Main_ResultDelete){
                // delete plant
                int position = data.getIntExtra(CommunicationKeys.EditPlant_Main_ExtraPlantEditedPosition, 0);
                //plantList.saveToPrefs();
                mRecyclerView.smoothScrollToPosition(position);
                mAdapter.notifyItemRemoved(position);
            }
            else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

//    private void runLayoutAnimation(final RecyclerView recyclerView) {
//        final Context context = recyclerView.getContext();
//        final LayoutAnimationController controller =
//                AnimationUtils.loadLayoutAnimation(context, R.anim.recycler_view_layout_anim);
//
//        recyclerView.setLayoutAnimation(controller);
//        recyclerView.getAdapter().notifyDataSetChanged();
//        recyclerView.scheduleLayoutAnimation();
//    }
}
