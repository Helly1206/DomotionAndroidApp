package com.helly.domotion.Buttons;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import com.helly.domotion.R;

// Created by helly on 2/17/18.

public class ButtonDeviceOnOff extends LinearLayout implements ButtonInterface, View.OnClickListener {
    private Switch mSwitch;
    private ImageButton mStateButton;

    public ButtonDeviceOnOff(Context context) {
        super(context);
        initializeViews(context);
    }

    public ButtonDeviceOnOff(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public ButtonDeviceOnOff(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.button_deviceonoff, this);
    }

    private void initializeComponents() {
        mSwitch = this.findViewById(R.id.switch_device_on_off);
        mStateButton = this.findViewById(R.id.button_device_on_off);
        mSwitch.setChecked(false);
        mStateButton.setSelected(false);

        mSwitch.setOnClickListener(this);
        mStateButton.setOnClickListener(this);
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
        boolean boolVal = (Double.valueOf(Value) > 0);
        if ((mSwitch != null) && (mStateButton != null)) {
            mSwitch.setChecked(boolVal);
            mStateButton.setSelected(boolVal);
        }
    }

    @Override
    public String Get() {
        return mStateButton.isSelected()?"1":"0";
    }

    @Override
    public ButtonType GetType() {
        return ButtonType.SWITCH;
    }

    @Override
    public void onClick(View view) {
        if (view == mSwitch) {
            mStateButton.setSelected(mSwitch.isChecked());
        } else if (view == mStateButton) {
            mSwitch.setChecked(!mStateButton.isSelected());
            mStateButton.setSelected(mSwitch.isChecked());
        }
        super.callOnClick();
    }
}
