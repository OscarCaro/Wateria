package com.wateria.Utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class IconTagDecoder {

    // Explanation:     tag -> ID -> Drawable

    //                  tag:            got from onClick in dialog                  "res/drawable/ic_common_1.xml"
    //                  ID:             stored in Plant class                       R.drawable.ic_common_1
    //                  Drawable:       loaded on run time from a given ID          Actual image

    //  If a any point you need a way to get ID given a Drawable, there's no generic way, you would need a map

    public static int tagToId(Context context, String tag){
        // Input format example: tag = "res/drawable/ic_common_1.xml"
        // After trimming : "ic_common_1"
        // Return: R.drawable.ic_common_1 (is an int, the id)
        return context.getResources().getIdentifier(trimTag(tag),"drawable",context.getPackageName());
    }

    public static Drawable idToDrawable (Context context, int drawableId){
        return context.getResources().getDrawable(drawableId);
    }

    private static String trimTag(String longTag){
        // Input format example: longTag = "res/drawable/ic_common_1.xml"
        // Output format : "ic_common_1"
        int leftIdx = longTag.lastIndexOf('/');
        int rightIdx = longTag.lastIndexOf('.');
        return longTag.substring(leftIdx + 1, rightIdx);
    }

}
