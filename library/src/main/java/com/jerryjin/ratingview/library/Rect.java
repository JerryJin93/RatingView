package com.jerryjin.ratingview.library;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;

import com.jerryjin.ratingview.library.widget.newer.ShapeView;

/**
 * Author: Jerry
 * Generated at: 2019/2/22 10:34
 * WeChat: enGrave93
 * Description: Single rect.
 */
public class Rect extends ShapeView {

    public Rect(int width, int height) {
        super(width, height);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void draW(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        //paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.LTGRAY);
        paint.setStrokeWidth(3);
        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), 0, 0, paint);
    }

    @Override
    protected int getItemCount() {
        return 5;
    }

    @Override
    protected int getOverlayColor() {
        return Color.parseColor("#fa700c");
    }

    @Override
    protected int getItemOffset() {
        return 0;
    }

    @Override
    protected int getPaddingLeft() {
        return 0;
    }

    @Override
    protected int getPaddingTop() {
        return 0;
    }

    @Override
    protected int getPaddingRight() {
        return 0;
    }

    @Override
    protected int getPaddingBottom() {
        return 0;
    }
}