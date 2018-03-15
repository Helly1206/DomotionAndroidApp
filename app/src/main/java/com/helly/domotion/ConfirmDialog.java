package com.helly.domotion;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;

// Created by helly on 3/4/18.

public class ConfirmDialog extends DialogFragment {
    protected String mMessage;
    protected int mId;
    protected JSONArray mContents;
    protected ConfirmDialogListener mListener;

    public interface ConfirmDialogListener {
        void onConfirmDialogYesClick(DialogFragment dialog, int id);
        void onConfirmDialogNoClick(DialogFragment dialog, int id);
    }

    public void setListener(Object implementer) {
        try {
            mListener = (ConfirmDialogListener) implementer;
        } catch (ClassCastException e) {
            throw new ClassCastException(implementer.toString() + " must implement ConfirmDialogListener");
        }
    }

    public void setContents(int id, String message, JSONArray contents) {
        mMessage = message;
        mId = id;
        mContents = contents;
    }

    public void showDialog(FragmentManager manager) {
        show(manager, "confirm");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        setCancelable(false);
        builder.setTitle(R.string.dialog_confirm);
        builder.setMessage(mMessage);
        builder.setView(buildContents());
        builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                if (mListener != null) {
                    mListener.onConfirmDialogYesClick(ConfirmDialog.this, mId);
                }
            }
        });
        builder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                if (mListener != null) {
                    mListener.onConfirmDialogNoClick(ConfirmDialog.this, mId);
                }
            }
        });
        return builder.create();
    }

    private View buildContents() {
        TableLayout tl = null;
        if (mContents != null) {
            if (mContents.length() > 0) {
                tl = new TableLayout(getActivity());
                TableLayout.LayoutParams tlp = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
                tl.setLayoutParams(tlp);
                tl.setGravity(Gravity.CENTER_HORIZONTAL);
                int startRow = 0;
                if (mContents.length() > 1) {
                    JSONArray columnNames = mContents.optJSONArray(startRow);
                    // Assume title row
                    TableRow tr = new TableRow(getActivity());
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);
                    tr.setLayoutParams(lp);
                    tr.setGravity(Gravity.CENTER_VERTICAL);
                    for (int i=0; i < columnNames.length(); i++) {
                        CreateTopText(tr, columnNames.optString(i), lp);
                    }
                    tl.addView(tr, lp);
                    startRow = 1;
                }
                boolean ThisRowEven = true;
                for (int row = startRow; row < mContents.length(); row++) {
                    JSONArray rowData = mContents.optJSONArray(row);
                    TableRow tr = new TableRow(getActivity());
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);
                    tr.setLayoutParams(lp);
                    //tr.setGravity(Gravity.CENTER_VERTICAL);
                    for (int i=0; i < rowData.length(); i++) {
                        CreateText(tr, rowData.optString(i), lp, ThisRowEven);
                    }
                    ThisRowEven ^= true;
                    tl.addView(tr, lp);
                }
            }
        }

        return tl;
    }

    private void CreateTopText(TableRow tr, String text, TableRow.LayoutParams lp) {
        if (tr != null) {
            TextView tv = new TextView(getActivity());
            tv.setText(text);
            tv.setTextColor(getActivity().getResources().getColor(R.color.tableWhite));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (getActivity().getResources().getDimension(R.dimen.icon_size)*0.3));
            tv.setHeight((int) (getActivity().getResources().getDimension(R.dimen.icon_size)*0.4));
            tv.setTypeface(null, Typeface.BOLD);
            tv.setLayoutParams(lp);
            tv.setBackgroundResource(R.drawable.table_head);
            tr.addView(tv);
        }
    }

    private void CreateText(TableRow tr, String text, TableRow.LayoutParams lp, boolean evenRow) {
        if (tr != null) {
            TextView tv = new TextView(getActivity());
            tv.setText(text);
            tv.setLayoutParams(lp);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (getActivity().getResources().getDimension(R.dimen.icon_size)*0.3));
            tv.setHeight((int) (getActivity().getResources().getDimension(R.dimen.icon_size)*0.4));
            if (evenRow) {
                tv.setBackgroundResource(R.drawable.table_even);
            } else {
                tv.setBackgroundResource(R.drawable.table_odd);
            }
            tr.addView(tv);
        }
    }
}
