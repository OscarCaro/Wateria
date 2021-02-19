package com.wateria.NumberPickers;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import androidx.appcompat.view.ContextThemeWrapper;

import com.wateria.R;

public class GreenNumberPicker extends NumberPicker {
    public GreenNumberPicker(Context context) {
        this(context, null);
    }

    public GreenNumberPicker(Context context, AttributeSet attrs) {
        // wrap the current context in the style we defined before
        super(new ContextThemeWrapper(context, R.style.NumberPickerGreenStyle), attrs);
    }
}
