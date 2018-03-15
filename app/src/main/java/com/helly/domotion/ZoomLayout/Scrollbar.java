package com.helly.domotion.ZoomLayout;

// Created by helly on 2/25/18.

/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.helly.domotion.R;

public class Scrollbar extends View {
    public static final int DEFAULT_BACKGROUND_COLOR = 0x80000000;
    public static final int DEFAULT_INDICATOR_COLOR = 0xff000000;

    private boolean mHidden = true;

    private final int mAnimationDuration;
    private float mIndicatorWidth;
    private Paint mBackgroundPaint;
    private Paint mIndicatorPaint;

    private Path mTempPath = new Path();
    private RectF mTempRectF = new RectF();

    private int mWidth;
    private int mHeight;

    private int mScrollRange;
    private int mViewportWidth;
    private float mPosition;
    private boolean mVertical;

    public Scrollbar(Context context) {
        this(context, null, 0);
    }

    public Scrollbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Scrollbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Scrollbar);
        int BackgroundColor = a.getColor(R.styleable.Scrollbar_backgroundColor, DEFAULT_BACKGROUND_COLOR);
        int IndicatorColor = a.getColor(R.styleable.Scrollbar_indicatorColor, DEFAULT_INDICATOR_COLOR);
        mVertical = a.getBoolean(R.styleable.Scrollbar_vertical, false);
        a.recycle();

        InitScrollbar(BackgroundColor, IndicatorColor);
    }

    public Scrollbar(Context context, int BackgroundColor, int IndicatorColor, boolean Vertical) {
        super(context, null ,0);

        mAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mVertical = Vertical;

        InitScrollbar(BackgroundColor, IndicatorColor);
    }

    private void InitScrollbar(int BackgroundColor, int IndicatorColor) {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(BackgroundColor);
        mBackgroundPaint.setAntiAlias(true);

        mIndicatorPaint = new Paint();
        mIndicatorPaint.setColor(IndicatorColor);
        mIndicatorPaint.setAntiAlias(true);

        setAlpha(0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mScrollRange <= mViewportWidth) {
            return;
        }

        mTempRectF.top = 0;
        mTempRectF.bottom = mHeight;
        mTempRectF.left = 0;
        mTempRectF.right = mWidth;

        drawPill(canvas, mTempRectF, mBackgroundPaint);

        if (mVertical) {
            // vertical
            mTempRectF.left = 0;
            mTempRectF.right = mWidth;
            mTempRectF.top = mPosition * 1f / (mScrollRange - mViewportWidth)
                    * mHeight * (1 - mIndicatorWidth);
            mTempRectF.bottom = mTempRectF.top + mIndicatorWidth * mHeight;
        } else {
            // horizontal
            mTempRectF.top = 0;
            mTempRectF.bottom = mHeight;
            mTempRectF.left = mPosition * 1f / (mScrollRange - mViewportWidth)
                    * mWidth * (1 - mIndicatorWidth);
            mTempRectF.right = mTempRectF.left + mIndicatorWidth * mWidth;
        }

        drawPill(canvas, mTempRectF, mIndicatorPaint);
    }

    private void drawPill(Canvas canvas, RectF rectF, Paint paint) {
        float radius;
        float temp;

        mTempPath.reset();
        if (mVertical) {
            // vertical
            radius = rectF.width() / 2;
            mTempPath.moveTo(rectF.left, rectF.bottom - radius);
            mTempPath.lineTo(rectF.left, rectF.top + radius);

            temp = rectF.bottom;
            rectF.bottom = rectF.top + 2 * radius;
            mTempPath.arcTo(rectF, 180, 180);
            rectF.bottom = temp;

            mTempPath.lineTo(rectF.right, rectF.bottom - radius);

            temp = rectF.top;
            rectF.top = rectF.bottom - 2 * radius;
            mTempPath.arcTo(rectF, 0, 180);
            rectF.top = temp;
        } else {
            // horizontal
            radius = rectF.height() / 2;
            mTempPath.moveTo(rectF.left + radius, rectF.top);
            mTempPath.lineTo(rectF.right - radius, rectF.top);

            temp = rectF.left;
            rectF.left = rectF.right - 2 * radius;
            mTempPath.arcTo(rectF, 270, 180);
            rectF.left = temp;

            mTempPath.lineTo(rectF.left + radius, rectF.bottom);

            temp = rectF.right;
            rectF.right = rectF.left + 2 * radius;
            mTempPath.arcTo(rectF, 90, 180);
            rectF.right = temp;
        }

        mTempPath.close();
        canvas.drawPath(mTempPath, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                View.resolveSize(0, widthMeasureSpec),
                View.resolveSize(0, heightMeasureSpec));
    }

    public void setScrollPosition(int position) {
        mPosition = constrain(0, mScrollRange, position);
        postInvalidateOnAnimation();
    }

    public void setScrollRangeAndViewportWidth(int scrollRange, int viewportWidth) {
        mScrollRange = scrollRange;
        mViewportWidth = viewportWidth;
        mIndicatorWidth = 0.1f;
        if (mScrollRange > 0) {
            mIndicatorWidth = constrain(mIndicatorWidth, 1f,
                    mViewportWidth * 1f / mScrollRange);
        }
        postInvalidateOnAnimation();
    }

    private float constrain(float min, float max, float v) {
        return Math.max(min, Math.min(max, v));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mVertical) {
            mHeight = h-w;
            mWidth = w;
        } else {
            mHeight = h;
            mWidth = w-h;
        }

    }

    public void show() {
        if (!mHidden) {
            return;
        }

        mHidden = false;
        animate().cancel();
        animate().alpha(1f).setDuration(mAnimationDuration);
    }

    public void hide() {
        if (mHidden) {
            return;
        }

        mHidden = true;
        animate().cancel();
        animate().alpha(0f).setDuration(mAnimationDuration);
    }
}
