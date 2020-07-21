package com.example.wateria.Notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.wateria.Activities.MainActivity;
import com.example.wateria.DataStructures.Plant;
import com.example.wateria.R;
import com.example.wateria.Services.RemindLaterFromNotificationActionService;
import com.example.wateria.Services.WaterSinglePlantFromNotificationActionService;
import com.example.wateria.Utils.CommunicationKeys;
import com.example.wateria.Utils.IconTagDecoder;

import java.util.ArrayList;


public class NotificationClass {

    public static final Integer notificationId = 1;
    public static final String CHANNEL_ID = "channel";        // For notification

    public static void createNotificationChannel(Context mainContext) {
        // SHOULD BE CALLED ONSTART (doesn't mind if called repeatedly)

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Wateria";              //getString(R.string.channel_name);
            String description = "Watering reminder notifications";       //getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = mainContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void pushNotification(Context context, ArrayList<Plant> zeroDaysRemList){
        // All plant in zeroDaysRemList need to be watered (daysRem = 0)

        NotifBuilder myBuilder;

        if (zeroDaysRemList.size() == 1){
            myBuilder = new SinglePlantNotifBuilder(zeroDaysRemList.get(0), context, CHANNEL_ID);
        }
        else{
            myBuilder = new MultiplePlantsNotifBuilder(zeroDaysRemList, context, CHANNEL_ID);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, myBuilder.getBuilder().build());        //number is notificationId   IMPORTANT: save it to a variable, for later remove the notif.
        //notificationManager.cancel(notificationId);
    }

}