package com.wateria.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.wateria.R;

public class SpeciesDialog extends DialogFragment {
//    public SpeciesDialog(Context context, ViewGroup viewGroup) {
//        super(context, R.style.ThemeOverlay_AppCompat_Dialog);
//        // TODO: If crash: create new style for Dialog instead of AlertDialog
//        View view = LayoutInflater.from(context).inflate(
//                R.layout.species_dialog,
//                (ConstraintLayout) viewGroup.findViewById(R.id.layout_dialog_container)
//        );
//        setContentView(view);
//        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        //getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
//    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.species_dialog, null);

        Dialog dialog = new Dialog(getContext(), R.style.ThemeOverlay_AppCompat_Dialog);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        return dialog;
    }
}
