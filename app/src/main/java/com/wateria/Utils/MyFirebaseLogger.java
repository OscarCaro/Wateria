package com.wateria.Utils;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.wateria.DataStructures.Plant;
import com.wateria.R;

import org.threeten.bp.LocalDate;

public class MyFirebaseLogger {

    private static final String new_plant_event = "new_plant_event";
    private static final String new_plant_param_name = "new_plant_name";
    private static final String new_plant_param_icon_changed = "new_plant_icon_changed";
    private static final String new_plant_param_first_wat_used = "new_plant_first_wat_used";

    private static final String tip_event = "tip_event";
    private static final String tip_param_idx = "tip_idx";
    private static final String tip_param_time_diff = "tip_time_diff";

    private static final String tip_like_event = "tip_like_event";

    private static final String tip__dislike_event = "tip__dislike_event";

    public static void logNewPlant(Context context, final Plant plant){
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(new_plant_param_name, plant.getPlantName());
        bundle.putBoolean(new_plant_param_icon_changed, plant.getIconId() != R.drawable.ic_common_1);
        bundle.putBoolean(new_plant_param_first_wat_used, plant.getNextWateringDate().getDayOfYear() != LocalDate.now().plusDays(plant.getWateringFrequency()).getDayOfYear());
        firebaseAnalytics.setDefaultEventParameters(bundle);
        firebaseAnalytics.logEvent(new_plant_event, bundle);
    }

    public static void logTip(Context context, int tipIdx, int timeDiff){
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putInt(tip_param_idx, tipIdx);
        bundle.putInt(tip_param_time_diff, timeDiff);
        firebaseAnalytics.logEvent(tip_event, bundle);
    }

    public static void logTipLike(Context context){
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        firebaseAnalytics.logEvent(tip_like_event, bundle);
    }

    public static void logTipDislike(Context context){
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        firebaseAnalytics.logEvent(tip__dislike_event, bundle);
    }

}
