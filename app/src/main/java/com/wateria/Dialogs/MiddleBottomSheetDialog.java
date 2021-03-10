package com.wateria.Dialogs;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.wateria.R;
import com.wateria.Utils.OpenPlayStore;

public class MiddleBottomSheetDialog extends BottomSheetDialog {

    private static final String GoogleLensPackage = "com.google.ar.lens";

    public MiddleBottomSheetDialog(@NonNull final Context context) {
        super(context, R.style.CustomBottomSheetDialogTheme);

        View dialogView = LayoutInflater.from(context).inflate(
                R.layout.main_middle_dialog,
                (ConstraintLayout) findViewById(R.id.main_middle_dialog_container)
        );

        setContentView(dialogView);

        if(getWindow() != null){
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // OnClick Listeners:
        findViewById(R.id.main_middle_tip_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
                TipOfTheDay.showTip(context, viewGroup);
            }
        });

        findViewById(R.id.main_middle_rate_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenPlayStore.open(context, context.getPackageName());
                dismiss();
            }
        });

        findViewById(R.id.main_middle_lens_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Try opening Google Lens app
                PackageManager manager = context.getPackageManager();
                Intent lensIntent = manager.getLaunchIntentForPackage(GoogleLensPackage);
                if (lensIntent != null) {
                    lensIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    context.startActivity(lensIntent);
                }
                else{
                    // Show dialog explaining that they have to install the app
                    final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
                    GoogleLensDialog.showDialog(context, viewGroup);
                }
                dismiss();
            }
        });
    }




}
