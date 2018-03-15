package com.helly.domotion.ZoomLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.RectF;
import android.os.Handler;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.helly.domotion.R;

// Created by helly on 2/25/18.

public class ZoomScrollbarsLayout extends FrameLayout {
    private final static String TAG = ZoomScrollbarsLayout.class.getSimpleName();
    private static final int SCROLLBAR_HIDE_DELAY_MILLIS = 1000;

    private boolean mDrawScrollbars;
    private boolean mFadeScrollbars;
    private float mSizeScrollbars;
    private Scrollbar mHorizontalScrollbar;
    private Scrollbar mVerticalScrollbar;
    private int mBackgroundColor;
    private int mIndicatorColor;
    private ZoomLayout mZoomLayout;

    private Handler mHandler = new Handler();

    public ZoomScrollbarsLayout(@NonNull Context context) {
        this(context, null);
    }

    public ZoomScrollbarsLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomScrollbarsLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ZoomScrollbarsLayout, defStyleAttr, 0);
        mDrawScrollbars = a.getBoolean(R.styleable.ZoomScrollbarsLayout_drawScrollbars, false);
        mFadeScrollbars = a.getBoolean(R.styleable.ZoomScrollbarsLayout_fadeScrollbars, false);
        mSizeScrollbars = a.getDimensionPixelSize(R.styleable.ZoomScrollbarsLayout_sizeScrollbars, 3);
        mBackgroundColor = a.getColor(R.styleable.ZoomScrollbarsLayout_sbBackgroundColor, Scrollbar.DEFAULT_BACKGROUND_COLOR);
        mIndicatorColor = a.getColor(R.styleable.ZoomScrollbarsLayout_sbIndicatorColor, Scrollbar.DEFAULT_INDICATOR_COLOR);
        a.recycle();
    }

    @Override
    public void addView(final View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() == 0) {
            super.addView(child, index, params);
            if (child instanceof ZoomLayout) {
                mZoomLayout = (ZoomLayout) child;
                addScrollbars();
            }
        } else {
            if ((child == mHorizontalScrollbar) || (child == mVerticalScrollbar)) {
                super.addView(child, index, params);
            } else {
                throw new RuntimeException(TAG + " accepts only a single child.");
            }
        }
    }

    private void addScrollbars() {
        if (mDrawScrollbars) {
            mHorizontalScrollbar = new Scrollbar(getContext(), mBackgroundColor, mIndicatorColor, false);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)mSizeScrollbars);
            lp.gravity = Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM;
            mHorizontalScrollbar.setLayoutParams(lp);
            addView(mHorizontalScrollbar);

            mVerticalScrollbar = new Scrollbar(getContext(), mBackgroundColor, mIndicatorColor, true);
            lp = new FrameLayout.LayoutParams((int)mSizeScrollbars,ViewGroup.LayoutParams.MATCH_PARENT);
            lp.gravity = Gravity.CENTER_VERTICAL|Gravity.END;
            mVerticalScrollbar.setLayoutParams(lp);
            addView(mVerticalScrollbar);

            if (!mFadeScrollbars) {
                mHorizontalScrollbar.show();
                mVerticalScrollbar.show();
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        handleTouchEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public void handleTouchEvent(MotionEvent ev) {
        if (mDrawScrollbars) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mHandler.removeCallbacks(HideScrollBar);
                    mHorizontalScrollbar.show();
                    mVerticalScrollbar.show();
                    break;
                case MotionEvent.ACTION_UP:
                    if (mFadeScrollbars) {
                        mHandler.postDelayed(HideScrollBar, SCROLLBAR_HIDE_DELAY_MILLIS);
                    }
                    break;
            }
            if (mZoomLayout != null) {
                float Zoom = mZoomLayout.getEngine().getRealZoom();
                RectF ChildRect = mZoomLayout.GetChildRect();
                SetHorizontalScrollbar(Zoom, ChildRect);
                SetVerticalScrollbar(Zoom, ChildRect);
            }
        }
    }

    private void SetHorizontalScrollbar(float Zoom, RectF ChildRect) {
        int realPan = -Math.round(mZoomLayout.getEngine().getPanX() * Zoom);
        int realChild = Math.round(ChildRect.width() * Zoom);
        int layout = mZoomLayout.getWidth();

        mHorizontalScrollbar.setScrollRangeAndViewportWidth(realChild, layout);
        mHorizontalScrollbar.setScrollPosition(realPan);
    }

    private void SetVerticalScrollbar(float Zoom, RectF ChildRect) {
        int realPan = -Math.round(mZoomLayout.getEngine().getPanY() * Zoom);
        int realChild = Math.round(ChildRect.height() * Zoom);
        int layout = mZoomLayout.getHeight();

        mVerticalScrollbar.setScrollRangeAndViewportWidth(realChild, layout);
        mVerticalScrollbar.setScrollPosition(realPan);
    }

    private Runnable HideScrollBar = new Runnable() {
        @Override
        public void run() {
            mHorizontalScrollbar.hide();
            mVerticalScrollbar.hide();
        }
    };
}
