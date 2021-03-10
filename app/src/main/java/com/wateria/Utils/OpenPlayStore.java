package com.wateria.Utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.wateria.R;

public class OpenPlayStore {

    public static void open(Context context, String packageName){
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        try{
            context.startActivity(intent);
        }
        catch (ActivityNotFoundException e1){
            uri = Uri.parse("https://play.google.com/store/apps/details?id=" + packageName);
            intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            try{
                context.startActivity(intent);
            }
            catch (ActivityNotFoundException e2){
                Toast.makeText(context, context.getResources().getString(R.string.main_middle_play_store_error_toast), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
