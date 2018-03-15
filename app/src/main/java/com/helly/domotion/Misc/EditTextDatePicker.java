package com.helly.domotion.Misc;

// Created by helly on 3/10/18.

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

public class EditTextDatePicker extends android.support.v7.widget.AppCompatEditText implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    protected onUpdateListener mListener;

    public interface onUpdateListener {
        void onUpdate(View view, String value);
    }

    public EditTextDatePicker(Context context) {
        super(context);
        setOnClickListener(this);
        setFocusable(false);
        setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
    }

    public EditTextDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
        setFocusable(false);
        setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
    }

    public EditTextDatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);
        setFocusable(false);
        setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
    }

    public void setUpdateListener(Object listener) {
        try {
            mListener = (onUpdateListener) listener;
        } catch (ClassCastException e) {
            throw new ClassCastException(listener.toString() + " must implement onUpdateListener");
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        setText(DateTimeFormat.Ord2Asc(DateTimeFormat.calcOrd(year, monthOfYear+1, dayOfMonth)));
        if (mListener != null) {
            mListener.onUpdate(this, getText().toString());
        }
    }

    @Override
    public void onClick(View v) {
        String value = getText().toString();
        int day = DateTimeFormat.getAscDay(value);
        int month = DateTimeFormat.getAscMonth(value);
        int year = DateTimeFormat.getAscYear(value);

        if ((day < 1) || (month < 1) || (year <1)) {
            value = DateTimeFormat.getTodayAsc();
            day = DateTimeFormat.getAscDay(value);
            month = DateTimeFormat.getAscMonth(value);
            year = DateTimeFormat.getAscYear(value);
        }
        DatePickerDialog dialog = new DatePickerDialog(getContext(), this, year, month, day);
        dialog.setCancelable(false);
        dialog.show();
    }

}
