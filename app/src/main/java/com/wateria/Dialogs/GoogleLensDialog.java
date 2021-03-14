package com.wateria.Dialogs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.wateria.R;
import com.wateria.Utils.OpenPlayStore;

public class GoogleLensDialog {

    private static final String GoogleLensPackage = "com.google.ar.lens";

    public static void showDialog(final Context context, ViewGroup viewGroup){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);

        View view = LayoutInflater.from(context).inflate(
                R.layout.google_lens_install_dialog,
                (ConstraintLayout) viewGroup.findViewById(R.id.layout_dialog_container)
        );
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenPlayStore.open(context, GoogleLensPackage);
                alertDialog.cancel();
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        alertDialog.show();
    }

}
