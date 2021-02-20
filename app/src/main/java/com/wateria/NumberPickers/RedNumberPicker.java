package com.wateria.NumberPickers;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import androidx.appcompat.view.ContextThemeWrapper;

import com.wateria.R;

public class RedNumberPicker extends NumberPicker {
    public RedNumberPicker(Context context) {
        this(context, null);
    }

    public RedNumberPicker(Context context, AttributeSet attrs) {
        // wrap the current context in the style we defined before
        super(new ContextThemeWrapper(context, R.style.NumberPickerRedStyle), attrs);
    }
}
