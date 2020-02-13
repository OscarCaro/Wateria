package com.example.wateria;

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
import com.example.wateria.DataStructures.PlantList;
import com.example.wateria.Services.RemindLaterFromNotificationActionService;
import com.example.wateria.Services.WaterSinglePlantFromNotificationActionService;


public class NotificationClass {

    public static final Integer notificationId = 1;
    public static final String CHANNEL_ID = "channel";        // For notification
    static final String WATER_SINGLE_PLANT_SERVICE_PUTEXTRA_PLANT_NAME = "extra_plant_name";

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

    public static void pushNotification(Context context, PlantList plantList){
        // Refactor: don't assume all plants in plantList have daysRem>0
        Integer numOfPlants = plantList.getNumOfZeroDaysRemPlants();

        // General intent to open app on notification touch
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)       // it's app icon but without the bubbles
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
                //.addAction(R.drawable.icon_clock_reming_later, "Remind Later(fake)", remindLaterPendingIntent)

        if (numOfPlants == 1){
            setBuilderForSinglePlantNotification(plantList, builder, context);
        }
        else if (numOfPlants > 1){
            setBuilderForSeveralPlantsNotification(plantList, builder, context);
        }
        setRemindLaterActionInBuilder(builder, context);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());        //number is notificationId   IMPORTANT: save it to a variable, for later remove the notif.
        //notificationManager.cancel(notificationId);
    }

    public static void setRemindLaterActionInBuilder(NotificationCompat.Builder builder, Context context){
        // Intent for Remind Later action
        Intent remindLaterIntent = new Intent(context, RemindLaterFromNotificationActionService.class);
        remindLaterIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent remindLaterPendingIntent =
                PendingIntent.getService(context, 0, remindLaterIntent, 0);
        builder.addAction(R.drawable.icon_clock_reming_later, context.getResources().getString(R.string.notification_remind_later_text), remindLaterPendingIntent);
    }

    public static void setBuilderForSinglePlantNotification(PlantList plantList,
                                                            NotificationCompat.Builder builder, Context context){
        // This plantList is supposed to contain only 1 plant with daysRem = 0
        int idx = 0;
        for (int i = 0; i < plantList.getSize(); i++){
            if (plantList.getDaysRemaining(idx) <= 0){
                idx = i;
            }
        }
        String title = plantList.get(idx).getPlantName();
        builder.setContentTitle(title);

        String text = context.getResources().getString(R.string.notification_text_one_plant);
        builder.setContentText(text);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //Integer drawableId = getDrawableIdFromIconCode(plantList.get(0).getIconIdx());
            Drawable drawable = plantList.getPlantIcon(idx);
            builder.setLargeIcon(getBitmapFromVectorDrawable(drawable));
        }

        // Intent for water action
        Intent waterIntent = new Intent(context, WaterSinglePlantFromNotificationActionService.class);
        waterIntent.putExtra(WATER_SINGLE_PLANT_SERVICE_PUTEXTRA_PLANT_NAME, title);
        waterIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent waterPendingIntent =
                PendingIntent.getService(context, 0, waterIntent, 0);
        builder.addAction(R.drawable.icon_watering, context.getResources().getString(R.string.notification_water_action_text), waterPendingIntent);
    }

    public static void setBuilderForSeveralPlantsNotification(PlantList plantList,
                                                              NotificationCompat.Builder builder, Context context){
        String title = plantList.getNumOfZeroDaysRemPlants() + " " + context.getResources().getString(R.string.notification_title_several_plants);
        builder.setContentTitle(title);

        String text = "";

        for (int i = 0; i < plantList.getSize(); i++){
            if (plantList.getDaysRemaining(i) <= 0){
                if (i == 0){
                    text += plantList.get(i).getPlantName();
                }
                else {
                    text += ", " + plantList.get(i).getPlantName();
                }
            }
        }
        //text += " and " + plantList.get(plantList.size()-1).getPlantName();   Cannot be done with refactor implementation
        builder.setContentText(text);
    }

    public static Bitmap getBitmapFromVectorDrawable(Drawable drawable) {
        //Drawable drawable = ContextCompat.getDrawable(context, drawableId);
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
