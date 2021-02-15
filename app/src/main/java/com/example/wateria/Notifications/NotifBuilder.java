package com.example.wateria.Notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.wateria.Activities.MainActivity;
import com.example.wateria.DataStructures.Plant;
import com.example.wateria.R;
import com.example.wateria.Services.RemindLaterFromNotificationActionService;

import java.util.ArrayList;

public abstract class NotifBuilder {

    protected Context context;
    protected NotificationCompat.Builder builder;
    protected String CHANNEL_ID;

    public NotifBuilder(Context context, String CHANNEL_ID){
        this.context = context;
        this.CHANNEL_ID = CHANNEL_ID;
    }

    public NotificationCompat.Builder getBuilder(){

        builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)       // it's app icon but without the bubbles
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        addOnTouchIntent();
        addRemindLaterAction();

        addSpecificFeatures();

        return builder;
    }

    protected abstract void addSpecificFeatures();

    private void addOnTouchIntent(){
        // General intent to open app on notification touch
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
    }

    private void addRemindLaterAction(){
        // Intent for Remind Later action
        Intent intent = new Intent(context, RemindLaterFromNotificationActionService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        builder.addAction(R.drawable.icon_clock_remind_later_white,
                context.getResources().getString(R.string.notification_remind_later_text), pendingIntent);
    }

}
