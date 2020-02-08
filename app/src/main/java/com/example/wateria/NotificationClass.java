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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

import com.example.wateria.DataStructures.Plant;

import java.util.ArrayList;


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

    public static void pushNotification(Context context, ArrayList<Plant> plantList){

        Integer numOfPlants = plantList.size();
        String title = "";
        String text = "";

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
            setBuilderForSinglePlantNotification(plantList, title, text, builder, context);
        }
        else if (numOfPlants > 1){
            setBuilderForSeveralPlantsNotification(plantList, title, text, builder, context);
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

    public static void setBuilderForSinglePlantNotification(ArrayList<Plant> plantList, String title, String text,
                                                     NotificationCompat.Builder builder, Context context){
        title = plantList.get(0).getPlantName();
        builder.setContentTitle(title);

        text = context.getResources().getString(R.string.notification_text_one_plant);
        builder.setContentText(text);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //Integer drawableId = getDrawableIdFromIconCode(plantList.get(0).getIconIdx());
            Drawable drawable = getDrawablefromIconCode(plantList.get(0).getIconIdx(), context);
            builder.setLargeIcon(getBitmapFromVectorDrawable(context, drawable));
        }

        // Intent for water action
        Intent waterIntent = new Intent(context, WaterSinglePlantFromNotificationActionService.class);
        waterIntent.putExtra(WATER_SINGLE_PLANT_SERVICE_PUTEXTRA_PLANT_NAME, title);
        waterIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent waterPendingIntent =
                PendingIntent.getService(context, 0, waterIntent, 0);
        builder.addAction(R.drawable.icon_watering, context.getResources().getString(R.string.notification_water_action_text), waterPendingIntent);
    }

    public static void setBuilderForSeveralPlantsNotification(ArrayList<Plant> plantList, String title, String text,
                                                              NotificationCompat.Builder builder, Context context){
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
