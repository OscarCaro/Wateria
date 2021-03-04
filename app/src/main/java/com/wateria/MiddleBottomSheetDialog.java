package com.wateria;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;

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
                TipOfTheDay.getInstance(context, viewGroup).showTip();
            }
        });

        findViewById(R.id.main_middle_rate_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


}
