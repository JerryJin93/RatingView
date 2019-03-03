package com.jerryjin.ratingview.library.widget.newer;

import android.graphics.Canvas;
import android.graphics.Shader;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * Author: Jerry
 * Generated at: 2019/2/22 10:07
 * WeChat: enGrave93
 * Description: The base view for customization.
 */
@SuppressWarnings("WeakerAccess")
public abstract class ShapeView {

    protected Canvas canvas;
    protected String mName;
    @ColorInt
    @ColorRes
    protected int mOverlayColor;
    protected int mOffset;
    private boolean mHasPadding;
    private int mWidth;
    private int mHeight;

    public ShapeView(int width, int height) {
        this();
        this.mWidth = width;
        this.mHeight = height;
    }

    private ShapeView() {
        mName = TextUtils.isEmpty(getViewName()) ? this.getClass().getSimpleName() : getViewName();
        mOverlayColor = getOverlayColor();
        mOffset = getItemOffset() * getItemCount();
        mHasPadding = (getPaddingLeft() != 0 || getPaddingTop() != 0 || getPaddingRight() != 0 || getPaddingBottom() != 0);
    }

    protected String getViewName() {
        return null;
    }

    public final void draw(Canvas canvas) {
        this.canvas = canvas;
        if (canvas != null) {
            draW(canvas);
        }
    }

    protected abstract void draW(Canvas canvas);

    protected abstract int getItemCount();

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int mWidth) {
        this.mWidth = mWidth;
        invalidate();
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int mHeight) {
        this.mHeight = mHeight;
        invalidate();
    }

    @ColorRes
    @ColorInt
    protected abstract int getOverlayColor();

    protected abstract int getItemOffset();

    protected int getOffset() {
        return mOffset;
    }

    protected Shader getOverlayShader() {
        return null;
    }

    protected final void invalidate() {
        draW(canvas);
    }

    protected boolean hasPadding() {
        return mHasPadding;
    }

    protected abstract int getPaddingLeft();

    protected abstract int getPaddingTop();

    protected abstract int getPaddingRight();

    protected abstract int getPaddingBottom();


    @NonNull

    @Override
    public String toString() {
        return "ShapeView{" +
                "canvas=" + canvas +
                ", mName='" + mName + '\'' +
                ", mOverlayColor=" + mOverlayColor +
                ", mOffset=" + mOffset +
                ", mHasPadding=" + mHasPadding +
                ", mWidth=" + mWidth +
                ", mHeight=" + mHeight +
                '}';
    }
}
