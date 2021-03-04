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

    private static TipOfTheDay instance;

    private static final String sharedPrefTipIdx = "tip_idx";
    private static final String sharedPrefLastDay = "last_day";

    private static final int MAX_TIPS = 1;

    private final Context context;
    private final ViewGroup viewGroup;
    private final SharedPreferences prefs;
    private final AlertDialog dialog;             // Reused every time, just changing its view

    private TipOfTheDay(Context context, ViewGroup viewGroup){
        this.context = context;
        this.viewGroup = viewGroup;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        dialog = builder.create();

        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public static TipOfTheDay getInstance(Context context, ViewGroup viewGroup){
        if (instance == null){
            instance = new TipOfTheDay(context, viewGroup);
        }
        return instance;
    }

    public void showTip(){
        int tipIdx = prefs.getInt(sharedPrefTipIdx, 0);
        int today = LocalDate.now().getDayOfYear();
        int lastDay = prefs.getInt(sharedPrefLastDay, today);

        if(lastDay != today){   //Show a new tip every day
            tipIdx++;
        }

        // Depends on tipIdx
        View view = decideView(tipIdx);

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

        prefs.edit().putInt(sharedPrefTipIdx, tipIdx).putInt(sharedPrefLastDay, today).apply();
    }

    private View decideView(int tipIdx){
        View view;
        ViewGroup rootView = (ConstraintLayout) viewGroup.findViewById(R.id.layout_dialog_container);
        switch(tipIdx % MAX_TIPS){
            case 0:
                view = LayoutInflater.from(context).inflate( R.layout.tip_fertilizer_dialog, rootView);
                break;
            default:
                view = LayoutInflater.from(context).inflate( R.layout.tip_fertilizer_dialog, rootView);
        }
        return view;
    }


    private int computeMins(){
        return 60 - Calendar.getInstance().get(Calendar.MINUTE);
    }

    private int computeHours(){
        return 24 - Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

}
