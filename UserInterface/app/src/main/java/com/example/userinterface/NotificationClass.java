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


import java.util.ArrayList;

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

    public static void pushNotificationFirstAttempt(Context context, ArrayList<Plant> plantList){
        // DON'T FORGET TO REMOVE ONCLICK EN LAYOUT_MAIN BOTÓN SETTINGS                                <----------- IMPORTANT

        Integer numOfPlants = plantList.size();
        String title = "";
        String text = "";

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Intent snoozeIntent = new Intent(context, MainActivity.class);      // Change to broadcastReceiver.class
        //snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
        PendingIntent snoozePendingIntent =
                PendingIntent.getActivity(context, 0, snoozeIntent, 0);

        Intent remindLaterIntent = new Intent(context, MainActivity.class);      // Change to broadcastReceiver.class
        //snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
        PendingIntent remindLaterPendingIntent =
                PendingIntent.getActivity(context, 0, remindLaterIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)       // it's app icon but without the bubbles
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                //.setContentTitle("Haworthia Fasciata")
                //.setContentText("This plant needs to be watered")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.icon_clock, "Remind Later(fake)", remindLaterPendingIntent)
                .addAction(R.drawable.icon_watering, "Water(fake)", snoozePendingIntent);

        if (numOfPlants == 1){
            title = plantList.get(0).getPlantName();
            builder.setContentTitle(title);

            text = context.getResources().getString(R.string.notification_text_one_plant);
            builder.setContentText(text);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //Integer drawableId = getDrawableIdFromIconCode(plantList.get(0).getImageCode());
                Drawable drawable = getDrawablefromIconCode(plantList.get(0).getImageCode(), context);
                builder.setLargeIcon(getBitmapFromVectorDrawable(context, drawable));
            }
        }
        else if (numOfPlants > 1){
            title = plantList.size() + " " + context.getResources().getString(R.string.notification_title_several_plants);
            builder.setContentTitle(title);

            for (int i = 0; i < plantList.size()-1; i++){
                if (i == 0){
                    text += plantList.get(i).getPlantName();
                }
                else {
                    text += ", " + plantList.get(i).getPlantName();
                }
            }
            text += " and " + plantList.get(plantList.size()-1).getPlantName();
            builder.setContentText(text);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());        //number is notificationId   IMPORTANT: save it to a variable, for later remove the notif.
        //notificationManager.cancel(notificationId);
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, Drawable drawable) {
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

    public static Drawable getDrawablefromIconCode(Integer iconCode, Context context){          // Maybe problem context vs ContextCompat
        Drawable drawable;

        switch (iconCode) {
            case 101:
                drawable = context.getResources().getDrawable(R.drawable.ic_cactus_1);
                break;
            case 102:
                drawable = context.getResources().getDrawable(R.drawable.ic_cactus_2);
                break;
            case 103:
                drawable = context.getResources().getDrawable(R.drawable.ic_cactus_3);
                break;
            case 104:
                drawable = context.getResources().getDrawable(R.drawable.ic_cactus_4);
                break;
            case 105:
                drawable = context.getResources().getDrawable(R.drawable.ic_cactus_5);
                break;
            case 106:
                drawable = context.getResources().getDrawable(R.drawable.ic_cactus_6);
                break;
            case 107:
                drawable = context.getResources().getDrawable(R.drawable.ic_cactus_7_concara);
                break;
            case 201:
                drawable = context.getResources().getDrawable(R.drawable.ic_common_1);
                break;
            case 202:
                drawable = context.getResources().getDrawable(R.drawable.ic_common_2_snakeplant);
                break;
            case 203:
                drawable = context.getResources().getDrawable(R.drawable.ic_common_3_sansevieria);
                break;
            case 204:
                drawable = context.getResources().getDrawable(R.drawable.ic_common_4_hanging);
                break;
            case 205:
                drawable = context.getResources().getDrawable(R.drawable.ic_common_5_spiderplant);
                break;
            case 206:
                drawable = context.getResources().getDrawable(R.drawable.ic_common_6_ivy);
                break;
            case 207:
                drawable = context.getResources().getDrawable(R.drawable.ic_common_7_bamboo);
                break;
            case 208:
                drawable = context.getResources().getDrawable(R.drawable.ic_common_8_monstera);
                break;
            case 209:
                drawable = context.getResources().getDrawable(R.drawable.ic_common_9_monsteraleaf);
                break;
            case 210:
                drawable = context.getResources().getDrawable(R.drawable.ic_common_10);
                break;
            case 301:
                drawable = context.getResources().getDrawable(R.drawable.ic_flower_1_red);
                break;
            case 302:
                drawable = context.getResources().getDrawable(R.drawable.ic_flower_2_orange);
                break;
            case 303:
                drawable = context.getResources().getDrawable(R.drawable.ic_flower_3_yellow);
                break;
            case 304:
                drawable = context.getResources().getDrawable(R.drawable.ic_flower_4_two);
                break;
            case 305:
                drawable = context.getResources().getDrawable(R.drawable.ic_flower_5);
                break;
            case 306:
                drawable = context.getResources().getDrawable(R.drawable.ic_flower_6_rose);
                break;
            case 401:
                drawable = context.getResources().getDrawable(R.drawable.ic_propagation_1);
                break;
            case 402:
                drawable = context.getResources().getDrawable(R.drawable.ic_propagation_2);
                break;
            case 403:
                drawable = context.getResources().getDrawable(R.drawable.ic_propagation_3);
                break;
            case 501:
                drawable = context.getResources().getDrawable(R.drawable.ic_tree_1_bush);
                break;
            case 502:
                drawable = context.getResources().getDrawable(R.drawable.ic_tree_2_dracaena);
                break;
            case 503:
                drawable = context.getResources().getDrawable(R.drawable.ic_tree_3_joshuatree_jade);
                break;
            case 504:
                drawable = context.getResources().getDrawable(R.drawable.ic_tree_4_palm);
                break;
            case 505:
                drawable = context.getResources().getDrawable(R.drawable.ic_tree_5_pine);
                break;
            case 506:
                drawable = context.getResources().getDrawable(R.drawable.ic_tree_6_bonsai);
                break;
            case 601:
                drawable = context.getResources().getDrawable(R.drawable.ic_veggies_1_lettuce);
                break;
            case 602:
                drawable = context.getResources().getDrawable(R.drawable.ic_veggies_2_carrot);
                break;
            case 603:
                drawable = context.getResources().getDrawable(R.drawable.ic_veggies_3_onion);
                break;
            case 604:
                drawable = context.getResources().getDrawable(R.drawable.ic_veggies_4_onion2);
                break;
            case 605:
                drawable = context.getResources().getDrawable(R.drawable.ic_veggies_5_garlic);
                break;
            case 606:
                drawable = context.getResources().getDrawable(R.drawable.ic_veggies_6_general);
                break;
            case 607:
                drawable = context.getResources().getDrawable(R.drawable.ic_veggies_7_tomato);
                break;
            case 608:
                drawable = context.getResources().getDrawable(R.drawable.ic_veggies_8_eggplant);
                break;
            case 609:
                drawable = context.getResources().getDrawable(R.drawable.ic_veggies_9_greenpepper);
                break;
            case 610:
                drawable = context.getResources().getDrawable(R.drawable.ic_veggies_10_redpepper);
                break;
            case 611:
                drawable = context.getResources().getDrawable(R.drawable.ic_veggies_11_avocado);
                break;
            case 612:
                drawable = context.getResources().getDrawable(R.drawable.ic_veggies_12_strawberry);
                break;
            default:
                drawable = context.getResources().getDrawable(R.drawable.ic_common_10);
                break;
        }
        return drawable;
    }

}
