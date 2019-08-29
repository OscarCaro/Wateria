package com.example.userinterface;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;


import static com.example.userinterface.MainActivity.CHANNEL_ID;

public class NotificationClass {

    public static Integer notificationId = 1;

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
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = mainContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void pushNotificationFirstAttempt(Context mainContext){
        // DON'T FORGET TO REMOVE ONCLICK EN LAYOUT_MAIN BOTÓN SETTINGS                                <----------- IMPORTANT

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(mainContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mainContext, 0, intent, 0);

        Intent snoozeIntent = new Intent(mainContext, MainActivity.class);      // Change to broadcastReceiver.class
        //snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
        PendingIntent snoozePendingIntent =
                PendingIntent.getActivity(mainContext, 0, snoozeIntent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(mainContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)       // it's app icon but without the bubbles
                .setColor(mainContext.getResources().getColor(R.color.colorPrimary))
                .setContentTitle("Haworthia Fasciata")
                .setContentText("This plant needs to be watered")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.icon_pencil_edit_green, "Water(fake)", snoozePendingIntent);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setLargeIcon(getBitmapFromVectorDrawable(mainContext, R.drawable.ic_cactus_1));
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mainContext);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());        //number is notificationId   IMPORTANT: save it to a variable, for later remove the notif.
        //notificationManager.cancel(notificationId);
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
