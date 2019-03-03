package com.jerryjin.ratingview.library;

import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

/**
 * Author: Jerry
 * Generated at: 2019/2/26 10:50
 * WeChat: enGrave93
 * Description:
 */
@SuppressWarnings("WeakerAccess")
public class CustomShapeDepiction {
    @DrawableRes
    private int drawableId;
    private int width;
    private int height;
    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;

    public CustomShapeDepiction(int drawableId, int width, int height, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        this.drawableId = drawableId;
        this.width = width;
        this.height = height;
        this.paddingLeft = paddingLeft;
        this.paddingTop = paddingTop;
        this.paddingRight = paddingRight;
        this.paddingBottom = paddingBottom;
    }

    @DrawableRes
    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(@DrawableRes int drawableId) {
        this.drawableId = drawableId;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    public int getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }

    public int getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public void refresh(Bitmap bitmap) {
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
    }

    @NonNull
    @Override
    public String toString() {
        return "CustomShapeDepiction{" +
                "drawableId=" + drawableId +
                ", width=" + width +
                ", height=" + height +
                ", paddingLeft=" + paddingLeft +
                ", paddingTop=" + paddingTop +
                ", paddingRight=" + paddingRight +
                ", paddingBottom=" + paddingBottom +
                '}';
    }
}
