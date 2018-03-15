package com.helly.domotion.Buttons;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.helly.domotion.R;

// Created by helly on 2/17/18.

public class ButtonBlindsUpDown extends LinearLayout implements ButtonInterface, View.OnClickListener {
    private ImageButton mUpButton;
    private ImageButton mDownButton;
    private ImageButton mStateButton;

    public ButtonBlindsUpDown(Context context) {
        super(context);
        initializeViews(context);
    }

    public ButtonBlindsUpDown(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public ButtonBlindsUpDown(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.button_blindsupdown, this);
    }

    private void initializeComponents() {
        mUpButton = this.findViewById(R.id.button_blinds_up);
        mDownButton = this.findViewById(R.id.button_blinds_down);
        mStateButton = this.findViewById(R.id.button_blinds_up_down);
        mUpButton.setSelected(false);
        mDownButton.setSelected(false);
        mStateButton.setSelected(false);

        mUpButton.setOnClickListener(this);
        mDownButton.setOnClickListener(this);
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
        // false = up, true = down
        if ((mUpButton != null) && (mDownButton != null) && (mStateButton != null)) {
            mUpButton.setSelected(false);
            mDownButton.setSelected(false);
            mStateButton.setSelected(boolVal);
        }
    }

    @Override
    public String Get() {
        return mStateButton.isSelected()?"1":"0";
    }

    @Override
    public ButtonType GetType() {
        return ButtonType.BLINDS;
    }

    @Override
    public void onClick(View view) {
        if (view == mUpButton) {
            mStateButton.setSelected(false);
        } else if (view == mDownButton) {
            mStateButton.setSelected(true);
        } else if (view == mStateButton) {
            mStateButton.setSelected(!mStateButton.isSelected());
        }
        super.callOnClick();
    }
}
