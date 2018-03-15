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
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.helly.domotion.Misc.DateTimeFormat;
import com.helly.domotion.Misc.EditTextDatePicker;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;

// Created by helly on 3/4/18.

public class HolidayDialog extends DialogFragment implements EditTextDatePicker.onUpdateListener {
    protected String mMessage;
    protected int mId;
    protected JSONArray mContents;
    protected HolidayDialogListener mListener;
    protected View mLayout;

    public interface HolidayDialogListener {
        void onHolidayDialogYesClick(DialogFragment dialog, int id, JSONArray contents);
        void onHolidayDialogNoClick(DialogFragment dialog, int id, JSONArray contents);
    }

    public void setListener(Object implementer) {
        try {
            mListener = (HolidayDialogListener) implementer;
        } catch (ClassCastException e) {
            throw new ClassCastException(implementer.toString() + " must implement HolidayDialogListener");
        }
    }

    public void setContents(int id, String message, JSONArray contents) {
        mMessage = message;
        mId = id;
        mContents = contents;
    }

    public void showDialog(FragmentManager manager) {
        show(manager, "holiday");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ViewGroup nullParent = null;
        mLayout = getActivity().getLayoutInflater().inflate(R.layout.dialog_holiday, nullParent);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        setCancelable(false);
        builder.setTitle(R.string.dialog_holiday);
        builder.setMessage(mMessage);
        builder.setView(mLayout);
        builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (mListener != null) {
                    mListener.onHolidayDialogYesClick(HolidayDialog.this, mId, getContents());
                }
            }
        });
        builder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                if (mListener != null) {
                    mListener.onHolidayDialogNoClick(HolidayDialog.this, mId, getContents());
                }
            }
        });
        setContents();
        return builder.create();
    }

    private JSONArray getContents() {
        JSONArray contents = new JSONArray();
        Spinner spinner = mLayout.findViewById(R.id.holiday_spinner);
        int val = 0;
        if (spinner.getSelectedItem().toString().equals(getString(R.string.table_trip))) {
            val = 1;
        }
        contents.put(val);
        EditTextDatePicker dateStart = mLayout.findViewById(R.id.holiday_start);
        contents.put(DateTimeFormat.Asc2Ord(dateStart.getText().toString()));
        EditTextDatePicker dateEnd = mLayout.findViewById(R.id.holiday_end);
        contents.put(DateTimeFormat.Asc2Ord(dateEnd.getText().toString()));

        return contents;
    }

    private void setContents() {
        // Fill spinner
        if (mLayout != null) {
            List<String> spinnerArray = new ArrayList<>();
            spinnerArray.add(getString(R.string.table_home));
            spinnerArray.add(getString(R.string.table_trip));
            ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner spinner = mLayout.findViewById(R.id.holiday_spinner);
            spinner.setAdapter(adapter);
            if (mContents != null) {
                if (mContents.length() > 2) {
                    String type = mContents.optString(0);
                    if (type.equals(getString(R.string.table_trip))) {
                        spinner.setSelection(1);
                    } else {
                        spinner.setSelection(0);
                    }
                    EditTextDatePicker dateStart = mLayout.findViewById(R.id.holiday_start);
                    dateStart.setUpdateListener(this);
                    dateStart.setText(mContents.optString(1));
                    EditTextDatePicker dateEnd = mLayout.findViewById(R.id.holiday_end);
                    dateEnd.setUpdateListener(this);
                    dateEnd.setText(mContents.optString(1));
                }
            }
        }
    }

    @Override
    public void onUpdate(View view, String value) {
        int ord = DateTimeFormat.Asc2Ord(value);
        int ordToday = DateTimeFormat.getTodayOrd();
        if (ordToday > ord) {
            ord = ordToday;
            ((EditTextDatePicker) view).setText(DateTimeFormat.Ord2Asc(ord));
        }
        if (mLayout != null) {
            if (view == mLayout.findViewById(R.id.holiday_start)) {
                EditTextDatePicker dateEnd = mLayout.findViewById(R.id.holiday_end);
                int ordEnd = DateTimeFormat.Asc2Ord(dateEnd.getText().toString());
                if (ord > ordEnd) {
                    dateEnd.setText(DateTimeFormat.Ord2Asc(ord));
                }
            } else { //holiday_end
                EditTextDatePicker dateStart = mLayout.findViewById(R.id.holiday_start);
                int ordStart = DateTimeFormat.Asc2Ord(dateStart.getText().toString());
                if (ord < ordStart) {
                    dateStart.setText(DateTimeFormat.Ord2Asc(ord));
                }
            }
        }
    }
}
