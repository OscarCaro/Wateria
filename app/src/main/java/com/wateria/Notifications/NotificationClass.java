package com.wateria.Notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

import com.wateria.DataStructures.Plant;
import com.wateria.R;

import java.util.ArrayList;


public class NotificationClass {

    public static final Integer notificationId = 1;
    public static final String CHANNEL_ID = "channel";        // For notification

    public static void createNotificationChannel(Context mainContext) {
        // SHOULD BE CALLED ONSTART (doesn't mind if called repeatedly)

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = mainContext.getResources().getString(R.string.notif_channel_name);
            String description = mainContext.getResources().getString(R.string.notif_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = mainContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void pushNotification(Context context, ArrayList<Plant> zeroDaysRemList){
        NotifBuilder myBuilder;

        if (zeroDaysRemList.size() <= 1){
            myBuilder = new SinglePlantNotifBuilder(zeroDaysRemList.get(0), context);
        }
        else{
            myBuilder = new MultiplePlantsNotifBuilder(zeroDaysRemList, context);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, myBuilder.getBuilder().build());
    }

}
