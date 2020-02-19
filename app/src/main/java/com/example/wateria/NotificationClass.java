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
import com.example.wateria.DataStructures.Plant;
import com.example.wateria.DataStructures.PlantList;
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

        if (zeroDaysRemList.size() == 1){
            setBuilderForSinglePlantNotification(zeroDaysRemList.get(0), builder, context);
        }
        else{
            setBuilderForSeveralPlantsNotification(zeroDaysRemList, builder, context);
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

    public static void setBuilderForSinglePlantNotification(Plant plant,
                                                            NotificationCompat.Builder builder, Context context){
        builder.setContentTitle(plant.getPlantName());

        String text = context.getResources().getString(R.string.notification_text_one_plant);
        builder.setContentText(text);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //Integer drawableId = getDrawableIdFromIconCode(plantList.get(0).getIconId());
            Drawable drawable = IconTagDecoder.idToDrawable(context, plant.getIconId());
            builder.setLargeIcon(getBitmapFromVectorDrawable(drawable));
        }

        // Intent for water action
        Intent waterIntent = new Intent(context, WaterSinglePlantFromNotificationActionService.class);
        waterIntent.putExtra(CommunicationKeys.NotificationClass_WaterSinglePlantService_PlantToWater, plant);
        waterIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent waterPendingIntent =
                PendingIntent.getService(context, 0, waterIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.icon_watering, context.getResources().getString(R.string.notification_water_action_text), waterPendingIntent);
    }

    public static void setBuilderForSeveralPlantsNotification(ArrayList<Plant> zeroDaysRemList,
                                                              NotificationCompat.Builder builder, Context context){

        // zeroDaysRemList contains only plants with daysRem = 0
        String title = zeroDaysRemList.size() + " " + context.getResources().getString(R.string.notification_title_several_plants);
        builder.setContentTitle(title);

        String text = "";

        for (int i = 0; i < zeroDaysRemList.size() - 1; i++){
            if (i == 0){
                text += zeroDaysRemList.get(i).getPlantName();
            }
            else {
                text += ", " + zeroDaysRemList.get(i).getPlantName();
            }

        }
        text += " and " + zeroDaysRemList.get(zeroDaysRemList.size() - 1).getPlantName();
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
