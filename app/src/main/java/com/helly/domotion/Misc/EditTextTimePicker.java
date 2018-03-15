package com.helly.domotion.Misc;

// Created by helly on 3/10/18.

import android.app.TimePickerDialog;
import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

public class EditTextTimePicker extends android.support.v7.widget.AppCompatEditText implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {
    protected onUpdateListener mListener;

    public interface onUpdateListener {
        void onUpdate(View view, String value);
    }

    public EditTextTimePicker(Context context) {
        super(context);
        setOnClickListener(this);
        setFocusable(false);
        setInputType(InputType.TYPE_DATETIME_VARIATION_TIME);
    }

    public EditTextTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
        setFocusable(false);
        setInputType(InputType.TYPE_DATETIME_VARIATION_TIME);
    }

    public EditTextTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);
        setFocusable(false);
        setInputType(InputType.TYPE_DATETIME_VARIATION_TIME);
    }

    public void setUpdateListener(Object listener) {
        try {
            mListener = (onUpdateListener) listener;
        } catch (ClassCastException e) {
            throw new ClassCastException(listener.toString() + " must implement onUpdateListener");
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        setText(DateTimeFormat.Mod2Asc((DateTimeFormat.calcMod(hourOfDay, minute))));
        if (mListener != null) {
            mListener.onUpdate(this, getText().toString());
        }
    }

    @Override
    public void onClick(View v) {
        String value = getText().toString();
        int Mod = DateTimeFormat.Asc2Mod(value);

        int hour = DateTimeFormat.getModHour(Mod);
        int minute = DateTimeFormat.getModMinute(Mod);

        TimePickerDialog dialog = new TimePickerDialog(getContext(), this, hour, minute, android.text.format.DateFormat.is24HourFormat(getContext()));
        dialog.setCancelable(false);
        dialog.show();
    }

}
