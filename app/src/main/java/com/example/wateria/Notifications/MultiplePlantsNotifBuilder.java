package com.example.wateria.Notifications;

import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.example.wateria.DataStructures.Plant;
import com.example.wateria.R;

import java.util.ArrayList;

public class MultiplePlantsNotifBuilder extends NotifBuilder {

    private ArrayList<Plant> plantList;

    public MultiplePlantsNotifBuilder(ArrayList<Plant> plantList, Context context, String CHANNEL_ID){
        super(context, CHANNEL_ID);
        this.plantList = plantList;
    }

    @Override
    protected void addSpecificFeatures() {
        String title = plantList.size() + " " + context.getResources().getString(R.string.notification_title_several_plants);
        builder.setContentTitle(title);

        String text = "";

        for (int i = 0; i < plantList.size() - 1; i++){
            if (i == 0){
                text += plantList.get(i).getPlantName();
            }
            else {
                text += ", " + plantList.get(i).getPlantName();
            }

        }
        text += " and " + plantList.get(plantList.size() - 1).getPlantName();
        builder.setContentText(text);
        builder.setStyle( new NotificationCompat.BigTextStyle().bigText(text));
    }
}
