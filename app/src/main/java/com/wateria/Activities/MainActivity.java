package com.wateria.Activities;

import android.app.job.JobScheduler;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.wateria.JobSchedulers.NotificationJobService;
import com.wateria.Utils.CommunicationKeys;
import com.wateria.DataStructures.PlantList;
import com.wateria.R;
import com.wateria.RecyclerViewAdapter;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private static final String sharedPrefFirstTimeKey = "first_time";

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
        checkOnboardingDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Check if there's a Job for notification scheduled:
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        if(jobScheduler.getAllPendingJobs().isEmpty()
                || jobScheduler.getAllPendingJobs().get(0).getId() != NotificationJobService.NOTIF_JOB_ID){
            // There isn't -> schedule it
            NotificationJobService.scheduleNextJob(this);
        }
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
        Intent intent = new Intent (this, NewPlantActivity.class);
        startActivityForResult(intent, CommunicationKeys.Main_NewPlant_RequestCode);
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

    public void checkOnboardingDialog(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.contains(sharedPrefFirstTimeKey)){
            showOnboardingDialog();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(sharedPrefFirstTimeKey, false).apply();
        }
    }

    private void showOnboardingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);

        View view = LayoutInflater.from(this).inflate(
                R.layout.onboarding_dialog_1,
                (ConstraintLayout) findViewById(R.id.layout_dialog_container)
        );
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                alertDialog.dismiss();
                showOnboardingDialog2();
            }
        });

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        alertDialog.show();
    }

    private void showOnboardingDialog2(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(this).inflate(
                R.layout.onboarding_dialog_2,
                (ConstraintLayout) findViewById(R.id.layout_dialog_container)
        );

        TextView textView = (TextView) view.findViewById(R.id.plant_days);
        textView.setText(getResources().getQuantityString(R.plurals.days, 3));

        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        alertDialog.show();
    }
}
