package com.helly.domotion.Buttons;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.helly.domotion.R;

// Created by helly on 2/17/18.

public class ButtonAnalog extends LinearLayout implements ButtonInterface, View.OnClickListener {
    private EditText mEdit;

    public ButtonAnalog(Context context) {
        super(context);
        initializeViews(context);
    }

    public ButtonAnalog(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public ButtonAnalog(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.button_analog, this);
    }

    private void initializeComponents() {
        mEdit = this.findViewById(R.id.edit_analog);

        mEdit.setOnClickListener(this);
        mEdit.setFocusable(false);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initializeComponents();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initializeComponents();
    }

    @Override
    public void Set(String Value) {
        if (mEdit == null) {
            return;
        }
        mEdit.setText(Value);
    }

    @Override
    public String Get() {
        return mEdit.getText().toString();
    }

    @Override
    public ButtonType GetType() {
        return ButtonType.ANALOG;
    }

    @Override
    public void onClick(View view) {
        OpenAlertDialog();
    }

    private void OpenAlertDialog() {
        View layout;
        final EditText analog;
        ImageButton buttonUp;
        ImageButton buttonDown;
        final ViewGroup nullParent = null;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            layout = inflater.inflate(R.layout.dialog_analog, nullParent);
            analog = layout.findViewById(R.id.edit_dialog_analog);
            buttonUp = layout.findViewById(R.id.button_up);
            buttonDown = layout.findViewById(R.id.button_down);
        } else {
            layout = null;
            analog = null;
            buttonUp = null;
            buttonDown = null;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.dialog_analog);
        builder.setView(layout);

        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (analog != null) {
                    Set(analog.getText().toString());
                }
                callOnClick();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog analogDialog =  builder.create();
        if (analog != null) {
            analog.setText(Get());
            if (buttonUp != null) {
                buttonUp.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        float value = Float.valueOf(analog.getText().toString());
                        value += 0.01;
                        analog.setText(String.valueOf(value));
                    }
                });
            }
            if (buttonDown != null) {
                buttonDown.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        float value = Float.valueOf(analog.getText().toString());
                        value -= 0.01;
                        analog.setText(String.valueOf(value));
                    }
                });
            }
        }
        analogDialog.setCancelable(false);
        analogDialog.show();
    }
}
