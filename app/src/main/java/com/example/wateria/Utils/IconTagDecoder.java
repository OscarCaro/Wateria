package com.example.wateria.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.example.wateria.R;

public class IconTagDecoder {

    public static Drawable tagToDrawable(Context context, String tag){
        // Input format example: tag = "ic_common_1"
        int drawableId = context.getResources().getIdentifier(tag,"drawable",context.getPackageName());
        return context.getResources().getDrawable(drawableId);
    }

    public static String trimTag(String longTag){
        // Input format example: longTag = "res/drawable/ic_common_1.xml"
        // Output format : "ic_common_1"
        int leftIdx = longTag.lastIndexOf('/');
        int rightIdx = longTag.lastIndexOf('.');
        return longTag.substring(leftIdx + 1, rightIdx);
    }

//    public static String drawableToTag(Context context, Drawable drawable){
////        // Input format example: tag = "ic_common_1"
////        int drawableId = context.getResources().getIdentifier(tag,"drawable",context.getPackageName());
////        return context.getResources().getDrawable(drawableId);
////        context.getResources().getIdentifier(drawable.toString(), "drawable", context.getPackageName
//        View v = (View) drawable;
//        int id = (View
//        Bitmap bm = new Bitmap();
//        bm.getGenerationId(R.drawable.)
//    }
}
