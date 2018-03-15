package com.helly.domotion.Buttons;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.helly.domotion.R;

// Created by helly on 2/17/18.

public class ButtonDelete extends LinearLayout implements ButtonInterface, View.OnClickListener {
    private ImageButton mButton;

    public ButtonDelete(Context context) {
        super(context);
        initializeViews(context);
    }

    public ButtonDelete(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public ButtonDelete(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.button_delete, this);
    }

    private void initializeComponents() {
        mButton = this.findViewById(R.id.button_delete);
        mButton.setOnClickListener(this);
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

    }

    @Override
    public String Get() {
        return "1";
    }

    @Override
    public ButtonType GetType() {
        return ButtonType.DELETE;
    }

    @Override
    public void onClick(View view) {
        super.callOnClick();
    }
}
