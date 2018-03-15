package com.helly.domotion.Buttons;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.helly.domotion.R;

// Created by helly on 2/17/18.

public class ButtonControlOnOff extends LinearLayout implements ButtonInterface, View.OnClickListener {
    private ImageButton mOnButton;
    private ImageButton mOffButton;
    private ImageButton mStateButton;

    public ButtonControlOnOff(Context context) {
        super(context);
        initializeViews(context);
    }

    public ButtonControlOnOff(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public ButtonControlOnOff(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.button_controlonoff, this);
    }

    private void initializeComponents() {
        mOnButton = this.findViewById(R.id.button_control_on);
        mOffButton = this.findViewById(R.id.button_control_off);
        mStateButton = this.findViewById(R.id.button_control_on_off);
        mOnButton.setSelected(false);
        mOffButton.setSelected(false);
        mStateButton.setSelected(false);

        mOnButton.setOnClickListener(this);
        mOffButton.setOnClickListener(this);
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
        if ((mOnButton != null) && (mOffButton != null) && (mStateButton != null)) {
            mOnButton.setSelected(false);
            mOffButton.setSelected(false);
            mStateButton.setSelected(boolVal);
        }
    }

    @Override
    public String Get() {
        return mStateButton.isSelected()?"1":"0";
    }

    @Override
    public ButtonType GetType() {
        return ButtonType.CONTROL;
    }

    @Override
    public void onClick(View view) {
        if (view == mOnButton) {
            mStateButton.setSelected(true);
        } else if (view == mOffButton) {
            mStateButton.setSelected(false);
        } else if (view == mStateButton) {
            mStateButton.setSelected(!mStateButton.isSelected());
        }
        super.callOnClick();
    }
}
