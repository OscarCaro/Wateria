package com.wateria.Notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.wateria.Activities.MainActivity;
import com.wateria.R;
import com.wateria.Services.RemindLaterService;

public abstract class NotifBuilder {

    protected Context context;
    protected NotificationCompat.Builder builder;

    public NotifBuilder(Context context){
        this.context = context;
    }

    public NotificationCompat.Builder getBuilder(){

        builder = new NotificationCompat.Builder(context, NotificationClass.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
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
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);
    }

    private void addRemindLaterAction(){
        // Intent for Remind Later action
        Intent intent = new Intent(context, RemindLaterService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.addAction(R.drawable.icon_clock_remind_later_white,
                context.getResources().getString(R.string.notification_remind_later_text), pendingIntent);
    }

}
