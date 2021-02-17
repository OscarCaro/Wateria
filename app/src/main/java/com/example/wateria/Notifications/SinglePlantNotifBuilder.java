package com.example.wateria.Notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.core.graphics.drawable.DrawableCompat;

import com.example.wateria.DataStructures.Plant;
import com.example.wateria.R;
import com.example.wateria.Services.WaterSinglePlantFromNotificationActionService;
import com.example.wateria.Utils.CommunicationKeys;
import com.example.wateria.Utils.IconTagDecoder;

import java.util.ArrayList;

public class SinglePlantNotifBuilder extends NotifBuilder {

    private Plant plant;

    public SinglePlantNotifBuilder(Plant plant, Context context){
        super(context);
        this.plant = plant;
    }

    @Override
    protected void addSpecificFeatures() {

        builder.setContentTitle(plant.getPlantName());

        String text = context.getResources().getString(R.string.notification_text_one_plant);
        builder.setContentText(text);
        builder.setLargeIcon(getBitmapFromVectorDrawable());

        addWateringAction();
    }

    private void addWateringAction(){
        // Intent for water action
        Intent waterIntent = new Intent(context, WaterSinglePlantFromNotificationActionService.class);
        waterIntent.putExtra(CommunicationKeys.NotificationClass_WaterSinglePlantService_PlantToWater, plant);
        waterIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent waterPendingIntent =
                PendingIntent.getService(context, 0, waterIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.icon_watering,
                context.getResources().getString(R.string.notification_water_action_text), waterPendingIntent);
    }

    private Bitmap getBitmapFromVectorDrawable() {
        Drawable drawable = IconTagDecoder.idToDrawable(context, plant.getIconId());

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
