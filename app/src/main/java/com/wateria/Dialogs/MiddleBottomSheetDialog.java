package com.wateria.Dialogs;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.wateria.R;

public class MiddleBottomSheetDialog extends BottomSheetDialog {

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
                Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                try{
                    context.startActivity(intent);
                }
                catch (ActivityNotFoundException e1){
                    uri = Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName());
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                    try{
                        context.startActivity(intent);
                    }
                    catch (ActivityNotFoundException e2){
                        Toast.makeText(context, context.getResources().getString(R.string.main_middle_rate_error_toast), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


}
