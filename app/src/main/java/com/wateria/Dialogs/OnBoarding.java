package com.wateria.Dialogs;

import android.content.Context;
import android.content.DialogInterface;
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

import com.wateria.R;

public class OnBoarding {

    private static final String sharedPrefFirstTimeKey = "first_time";

    public static void checkOnboardingDialog(Context context, ViewGroup viewGroup){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if(!prefs.contains(sharedPrefFirstTimeKey)){
            showOnboardingDialog1(context, viewGroup);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(sharedPrefFirstTimeKey, false).apply();
        }
    }

    private static void showOnboardingDialog1(final Context context, final ViewGroup viewGroup){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);

        View view = LayoutInflater.from(context).inflate(
                R.layout.onboarding_dialog_1,
                (ConstraintLayout) viewGroup.findViewById(R.id.layout_dialog_container)
        );
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                alertDialog.dismiss();
                showOnboardingDialog2(context, viewGroup);
            }
        });

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        alertDialog.show();
    }

    private static void showOnboardingDialog2(final Context context, final ViewGroup viewGroup){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(context).inflate(
                R.layout.onboarding_dialog_2,
                (ConstraintLayout) viewGroup.findViewById(R.id.layout_dialog_container)
        );

        TextView textView = (TextView) view.findViewById(R.id.plant_days);
        textView.setText(context.getResources().getQuantityString(R.plurals.days, 3));

        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                alertDialog.dismiss();
                showOnboardingDialog3(context, viewGroup);
            }
        });

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        alertDialog.show();
    }

    private static void showOnboardingDialog3(final Context context, final ViewGroup viewGroup){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(context).inflate(
                R.layout.onboarding_dialog_3,
                (ConstraintLayout) viewGroup.findViewById(R.id.layout_dialog_container)
        );

        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        alertDialog.show();
    }
}
