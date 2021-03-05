package com.wateria;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.threeten.bp.LocalDate;

import java.util.Calendar;

public class TipOfTheDay {

    private static final String sharedPrefTipIdx = "tip_idx";
    private static final String sharedPrefLastDay = "last_day";

    private static final int MAX_TIPS = 1;

    public static void showTip(Context context, ViewGroup viewGroup){
        final AlertDialog dialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme).create();

        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Depends on tipIdx
        View view = decideView(context, viewGroup);

        // Depends on time
        TextView unlockText = (TextView) view.findViewById(R.id.text_unlock);
        unlockText.setText(context.getResources().getString(R.string.tip_unlock_text, computeHours(), computeMins()));

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.setView(view);
        dialog.show();
    }

    private static View decideView(Context context, ViewGroup viewGroup){
        int tipIdx = decideTipIdx(context);

        ViewGroup rootView = (ConstraintLayout) viewGroup.findViewById(R.id.layout_dialog_container);
        switch(tipIdx % MAX_TIPS){
            case 0:
                return LayoutInflater.from(context).inflate( R.layout.tip_fertilizer_dialog, rootView);
            default:
                return LayoutInflater.from(context).inflate( R.layout.tip_fertilizer_dialog, rootView);
        }
    }

    private static int decideTipIdx(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        int tipIdx = prefs.getInt(sharedPrefTipIdx, 0);
        int today = LocalDate.now().getDayOfYear();
        int lastDay = prefs.getInt(sharedPrefLastDay, today);

        if(lastDay != today){   //Show a new tip every day
            tipIdx++;
        }

        prefs.edit().putInt(sharedPrefTipIdx, tipIdx).putInt(sharedPrefLastDay, today).apply();

        return tipIdx;
    }


    private static int computeMins(){
        return (60 - Calendar.getInstance().get(Calendar.MINUTE)) % 60;          // modulus to change case of 60-0=60  -> to 0
    }

    private static int computeHours(){
        return (24 - Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) % 24;    // modulus to change case of 24-0=24  -> to 0
    }

}
