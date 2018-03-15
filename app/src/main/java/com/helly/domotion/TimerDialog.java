package com.helly.domotion;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import com.helly.domotion.Misc.DateTimeFormat;
import com.helly.domotion.Misc.EditTextTimePicker;
import org.json.JSONArray;

// Created by helly on 3/4/18.

public class TimerDialog extends DialogFragment implements EditTextTimePicker.onUpdateListener, CheckBox.OnClickListener {
    protected String mMessage;
    protected int mId;
    protected JSONArray mContents;
    protected TimerDialogListener mListener;
    protected View mLayout;

    public interface TimerDialogListener {
        void onTimerDialogYesClick(DialogFragment dialog, int id, int time);
        void onTimerDialogNoClick(DialogFragment dialog, int id, int time);
    }

    public void setListener(Object implementer) {
        try {
            mListener = (TimerDialogListener) implementer;
        } catch (ClassCastException e) {
            throw new ClassCastException(implementer.toString() + " must implement TimerDialogListener");
        }
    }

    public void setContents(int id, String message, JSONArray contents) {
        mId = id;
        mMessage = message;
        mContents = contents;
    }

    public void showDialog(FragmentManager manager) {
        show(manager, "timer");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ViewGroup nullParent = null;
        mLayout = getActivity().getLayoutInflater().inflate(R.layout.dialog_timer, nullParent);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        setCancelable(false);
        builder.setTitle(R.string.dialog_timer);
        builder.setMessage(mMessage);
        builder.setView(mLayout);
        builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (mListener != null) {
                    mListener.onTimerDialogYesClick(TimerDialog.this, mId, getTime());
                }
            }
        });
        builder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (mListener != null) {
                    mListener.onTimerDialogNoClick(TimerDialog.this, mId, getTime());
                }
            }
        });
        setContents();
        return builder.create();
    }

    private int getTime() {
        int contents = -1;
        EditTextTimePicker time = mLayout.findViewById(R.id.timer_time);
        CheckBox active = mLayout.findViewById(R.id.timer_active);

        if (active.isChecked()) {
            contents = DateTimeFormat.Asc2Mod(time.getText().toString());
        }

        return contents;
    }

    private void setContents() {
        // Fill spinner
        if (mLayout != null) {
            TextView name = mLayout.findViewById(R.id.timer_name);
            name.setText(mContents.optString(0));
            TextView description = mLayout.findViewById(R.id.timer_description);
            description.setText(mContents.optString(1));
            EditTextTimePicker time = mLayout.findViewById(R.id.timer_time);
            time.setUpdateListener(this);
            CheckBox active = mLayout.findViewById(R.id.timer_active);
            active.setOnClickListener(this);
            if (mContents.optString(3).equals(getString(R.string.table_true))) {
                time.setText(mContents.optString(2));
                active.setChecked(true);
                active.setText(R.string.table_true);
                time.setEnabled(true);
            } else {
                time.setText(DateTimeFormat.Mod2Asc(0));
                active.setChecked(false);
                active.setText(R.string.table_false);
                time.setEnabled(false);
            }
        }
    }

    @Override
    public void onUpdate(View view, String value) {

    }

    @Override
    public void onClick(View view) {
        EditTextTimePicker time = mLayout.findViewById(R.id.timer_time);
        if (((CheckBox)view).isChecked()) {
            time.setText(mContents.optString(2));
            ((CheckBox)view).setText(R.string.table_true);
            time.setEnabled(true);
        } else {
            time.setText(DateTimeFormat.Mod2Asc(0));
            ((CheckBox)view).setText(R.string.table_false);
            time.setEnabled(false);
        }
    }
}
