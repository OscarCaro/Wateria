package com.wateria.Notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.core.graphics.drawable.DrawableCompat;

import com.wateria.DataStructures.Plant;
import com.wateria.R;
import com.wateria.Services.WaterPlantService;
import com.wateria.Utils.CommunicationKeys;
import com.wateria.Utils.IconTagDecoder;

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
        Intent waterIntent = new Intent(context, WaterPlantService.class);
        waterIntent.putExtra(CommunicationKeys.NotificationClass_WaterSinglePlantService_PlantToWater, plant);
        waterIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent waterPendingIntent =
                PendingIntent.getService(context, 0, waterIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
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
