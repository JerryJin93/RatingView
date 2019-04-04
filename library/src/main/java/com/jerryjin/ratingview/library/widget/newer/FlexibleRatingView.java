package com.jerryjin.ratingview.library.widget.newer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.jerryjin.ratingview.library.Logger;
import com.jerryjin.ratingview.library.R;
import com.jerryjin.ratingview.library.UI;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.DecimalFormat;

import static com.jerryjin.ratingview.library.Logger.TYPE_ERROR;

/**
 * Author: Jerry
 * Generated at: 2019/2/21 10:33
 * GitHub: https://github.com/JerryJin93
 * Blog:
 * WeChat: enGrave93
 * Version: 0.0.3
 * Description: A flexible selection view for Android.
 */
public class FlexibleRatingView extends View {

    public static final int SHAPE_STAR = 0;
    public static final int SHAPE_CIRCLE = 1;
    public static final int SHAPE_CUSTOMIZED = 2;
    public static final int MODE_RENDERING = 0;
    public static final int MODE_IMAGE = 1;
    public static final int MODE_ORIGIN = 0;
    public static final int MODE_FRACTION = 1;
    public static final int DEFAULT_STAR_OFF_PADDING_LEFT = 0;
    public static final int DEFAULT_STAR_OFF_PADDING_TOP = 1; // xhdpi -> 1px, xxhdpi -> 2px.
    public static final int DEFAULT_STAR_OFF_PADDING_RIGHT = 2; // xhdpi -> 2px, xxhdpi -> 3px.
    public static final int DEFAULT_STAR_OFF_PADDING_BOTTOM = 3; // xhdpi -> 3px, xxhdpi -> 4px.
    private static final String TAG = FlexibleRatingView.class.getSimpleName();
    private static final int DEFAULT_CAPACITY = 5;
    private static final int DEFAULT_MAX_SCORES = 5;
    private static final int DEFAULT_COLOR = Color.parseColor("#FA700C");
    private static final int DEFAULT_STAR_ON_IMAGE_ID = R.drawable.star1;
    private static final int DEFAULT_STAR_OFF_IMAGE_ID = R.drawable.star2;
    private static final float ERR_FRACTION = -1;
    private static int DEFAULT_MARGINS;
    private static int DEFAULT_ITEM_SIZE;
    private Context mContext;
    private Paint mPaint;
    private int mItemSize;
    private int mRadius;
    private int mMargins;
    private int width, height, paddingLeft, paddingTop, paddingRight, paddingBottom;
    private int offImagePaddingLeft, offImagePaddingTop, offImagePaddingRight, offImagePaddingBottom;
    private int mCapacity;
    private int mColor;
    @ShapeType
    private int mShapeType;
    @RenderingMode
    private int mDrawingMode;
    private int mMaxScores;
    private float mScores;
    private float mFraction;
    private int mOnImageResourceId;
    private int mOffImageResourceId;
    private boolean mEnableTouch;
    private OnScoreChangeListener listener;
    private ShapeView mCustomizedShape;
    private boolean mUsePalette;
    @OutputMode
    private int mOutputMode;
    private Bitmap on, off;
    private CustomShapeDepiction offDepiction;
    private CustomShapeDepiction onDepiction;

    public FlexibleRatingView(Context context) {
        this(context, null, 0);
    }

    public FlexibleRatingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlexibleRatingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * n角星路径
     *
     * @param num 几角星
     * @param R   外接圆半径
     * @param r   内接圆半径
     * @return n角星路径
     */
    public static Path nStarPath(int num, float R, float r) {
        Path path = new Path();
        float perDeg = 360f / num;
        float degA = perDeg / 2 / 2;
        float degB = 360f / (num - 1) / 2 - degA / 2 + degA;
        path.moveTo(
                (float) (Math.cos(rad(degA + perDeg * 0)) * R + R * Math.cos(rad(degA))),
                (float) (-Math.sin(rad(degA + perDeg * 0)) * R + R));
        for (int i = 0; i < num; i++) {
            path.lineTo(
                    (float) (Math.cos(rad(degA + perDeg * i)) * R + R * Math.cos(rad(degA))),
                    (float) (-Math.sin(rad(degA + perDeg * i)) * R + R));
            path.lineTo(
                    (float) (Math.cos(rad(degB + perDeg * i)) * r + R * Math.cos(rad(degA))),
                    (float) (-Math.sin(rad(degB + perDeg * i)) * r + R));
        }
        path.close();
        return path;
    }

    /**
     * Transform degrees to Radians.
     *
     * @param deg Given degrees.
     * @return The transformed value.
     */
    public static float rad(float deg) {
        return (float) (deg * Math.PI / 180);
    }

    /**
     * 画正n角星的路径:
     *
     * @param num 角数
     * @param R   外接圆半径
     * @return 画正n角星的路径
     */
    public static Path regularStarPath(int num, float R) {
        float degA, degB;
        if (num % 2 == 1) {//奇数和偶数角区别对待
            degA = 360f / num / 2 / 2;
            degB = 180 - degA - 360f / num / 2;
        } else {
            degA = 360f / num / 2;
            degB = 180 - degA - 360f / num / 2;
        }
        float r = (float) (R * Math.sin(rad(degA)) / Math.sin(rad(degB)));
        return nStarPath(num, R, r);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec, height), height);
        Logger.print(TYPE_ERROR, TAG, "resultH", getMeasuredWidth() + "");
    }

    private int measureWidth(int widthMeasureSpec, int measuredHeight) {
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        int result;
        if (mShapeType != SHAPE_CUSTOMIZED) {
            if (specMode == MeasureSpec.EXACTLY) {
                result = specSize + getPaddingLeft() + getPaddingRight();
            } else {
                result = (mCapacity - 1) * mMargins + measuredHeight * mCapacity + getPaddingLeft() + getPaddingRight();
                if (specMode == MeasureSpec.AT_MOST) {
                    result = Math.min(specSize, result);
                }
            }
        } else {
            result = (mCapacity - 1) * mMargins + mCustomizedShape.getWidth() * mCapacity + getPaddingLeft() + getPaddingRight();
            Logger.print(TYPE_ERROR, TAG, "result", result + "");
        }

        return result;
    }

    private int measureHeight(int heightMeasureSpec) {
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        int result;
        if (mShapeType != SHAPE_CUSTOMIZED) {
            if (specMode == MeasureSpec.EXACTLY) {
                result = specSize;
            } else {
                if (mDrawingMode == MODE_RENDERING) {
                    result = getPaddingTop() + DEFAULT_ITEM_SIZE + getPaddingBottom();
                } else {
                    result = getPaddingTop() + off.getHeight() + getPaddingBottom();
                }

                if (specMode == MeasureSpec.AT_MOST) {
                    result = Math.min(specSize, result);
                }
            }
        } else {
            Logger.print(TYPE_ERROR, "height", " " + mCustomizedShape.getHeight());
            result = getPaddingLeft() + mCustomizedShape.getHeight() + getPaddingRight();
        }
        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();
        mItemSize = height - paddingTop - paddingBottom;
        mRadius = (int) (mItemSize / 2f);
        invalidateBitmaps(height);
        Logger.print(TYPE_ERROR, TAG, "oldW:", "" + oldw, "oldH:", "" + oldh, "newW: ", "" + w, "newH: ", "" + h);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        mContext = context;
        DEFAULT_ITEM_SIZE = (int) UI.dp2px(mContext, 20);
        DEFAULT_MARGINS = 0;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(Color.LTGRAY);
        mPaint.setStrokeWidth(1);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlexibleRatingView);
            mCapacity = ta.getInt(R.styleable.FlexibleRatingView_capacity, DEFAULT_CAPACITY);
            mMaxScores = ta.getInt(R.styleable.FlexibleRatingView_maxScores, DEFAULT_MAX_SCORES);
            mItemSize = ta.getDimensionPixelSize(R.styleable.FlexibleRatingView_view_size, DEFAULT_ITEM_SIZE);
            mColor = ta.getColor(R.styleable.FlexibleRatingView_color, DEFAULT_COLOR);
            mMargins = ta.getDimensionPixelSize(R.styleable.FlexibleRatingView_margins, DEFAULT_MARGINS);
            mScores = ta.getFloat(R.styleable.FlexibleRatingView_scores, 0);
            mShapeType = ta.getInt(R.styleable.FlexibleRatingView_shape, SHAPE_STAR);
            mDrawingMode = ta.getInt(R.styleable.FlexibleRatingView_drawing_mode, MODE_RENDERING);
            mEnableTouch = ta.getBoolean(R.styleable.FlexibleRatingView_touch_mode, false);
            mOnImageResourceId = ta.getResourceId(R.styleable.FlexibleRatingView_on_img, DEFAULT_STAR_ON_IMAGE_ID);
            mOffImageResourceId = ta.getResourceId(R.styleable.FlexibleRatingView_off_image, DEFAULT_STAR_OFF_IMAGE_ID);
            offImagePaddingLeft = ta.getDimensionPixelSize(R.styleable.FlexibleRatingView_off_image_padding_left, 0);
            offImagePaddingTop = ta.getDimensionPixelSize(R.styleable.FlexibleRatingView_off_image_padding_top, 0);
            offImagePaddingRight = ta.getDimensionPixelSize(R.styleable.FlexibleRatingView_off_image_padding_right, 0);
            offImagePaddingBottom = ta.getDimensionPixelSize(R.styleable.FlexibleRatingView_off_image_padding_bottom, 0);
            mOutputMode = ta.getInt(R.styleable.FlexibleRatingView_output_mode, MODE_ORIGIN);
            mUsePalette = ta.getBoolean(R.styleable.FlexibleRatingView_use_palette, false);
            ta.recycle();
        } else {
            mCapacity = DEFAULT_CAPACITY;
            mMaxScores = DEFAULT_MAX_SCORES;
            mMargins = DEFAULT_MARGINS;
            mColor = DEFAULT_COLOR;
            mEnableTouch = false;
            mItemSize = DEFAULT_ITEM_SIZE;
            mScores = 0;
            mShapeType = SHAPE_STAR;
            mDrawingMode = MODE_RENDERING;
            mOnImageResourceId = DEFAULT_STAR_ON_IMAGE_ID;
            mOffImageResourceId = DEFAULT_STAR_OFF_IMAGE_ID;
            offImagePaddingLeft = offImagePaddingTop = offImagePaddingRight = offImagePaddingBottom = 0;
            mOutputMode = MODE_ORIGIN;
            mUsePalette = false;
        }
        mFraction = mScores / mMaxScores;
        if (mDrawingMode == MODE_IMAGE) {
            Drawable onStar = mContext.getResources().getDrawable(mOnImageResourceId);
            Drawable offStar = mContext.getResources().getDrawable(mOffImageResourceId);
            if (offStar == null) {
                offStar = mContext.getResources().getDrawable(DEFAULT_STAR_OFF_IMAGE_ID);
            }
            on = drawableToBitmap(onStar);
            off = drawableToBitmap(offStar);
            // By default, based on the offImage
            offDepiction = new CustomShapeDepiction(mOffImageResourceId, off.getWidth(), off.getHeight(),
                    offImagePaddingLeft, offImagePaddingTop, offImagePaddingRight, offImagePaddingBottom);

            Logger.print(TYPE_ERROR, TAG, "w", off.getWidth() + "", "h", "" + off.getHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(getCachingBitmap(mShapeType), 0, 0, null);
    }

    private void invalidateBitmaps(int height) {
        int oldHeight = off.getHeight();
        float scale = height * 1f / oldHeight;

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        off = Bitmap.createBitmap(off, 0, 0, off.getWidth(), off.getHeight(), matrix, false);
        offDepiction.refresh(off);
    }

    private void drawStars(Canvas canvas) {
        canvas.save();
        canvas.translate(paddingLeft, paddingTop);
        if (mDrawingMode == MODE_RENDERING) {
            Path starPath = regularStarPath(5, mRadius);
            int offset = getStarsOffset(5);
            for (int i = 0; i < mCapacity; i++) {
                canvas.drawPath(starPath, mPaint);
                canvas.translate(mRadius * 2 + mMargins - offset, 0);
            }
        } else {
            int width = offDepiction.getWidth();
            for (int i = 0; i < mCapacity; i++) {
                canvas.drawBitmap(off, 0, 0, null);
                canvas.translate(width + mMargins, 0);
            }
        }
        canvas.restore();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEnableTouch) {
            checkTouch(new PointF(event.getX(), event.getY()));
        }
        return super.onTouchEvent(event);
    }

    // problems
    private void checkTouch(PointF touchPoint) {
        int area = getEffectiveAreaWidth(mShapeType);
        if (touchPoint.x < paddingLeft) {
            setFraction(0);
        } else if (touchPoint.x > area + paddingLeft) {
            setFraction(1);
        }

        if (touchPoint.y > height) {
            return;
        }
        float fraction = touchPoint.x / width;
        setFraction(fraction);
        if (listener != null) {
            // TODO: 2019/2/25 输出处理过的分数
            float recomputedFraction = getRecomputedFraction(touchPoint);
            if (mOutputMode == MODE_ORIGIN) {
                listener.onScoreChangeListener(getRecomputedScores(recomputedFraction));
            } else if (mOutputMode == MODE_FRACTION) {
                listener.onScoreChangeListener(recomputedFraction);
            }
        }
    }

    /**
     * Get the width of effective area of the current view, which means the padding around are excluded.
     *
     * @param shapeType The current shape type in the container.
     * @return The calculated width.
     */
    private int getEffectiveAreaWidth(@ShapeType int shapeType) {
        if (shapeType == SHAPE_CUSTOMIZED) {
            return mCapacity * mItemSize + (mCapacity - 1) * mMargins;
        } else if (shapeType == SHAPE_STAR) {
            if (mDrawingMode == MODE_IMAGE) {
                return mCapacity * mItemSize + (mCapacity - 1) * mMargins + (offDepiction.getPaddingLeft() + offDepiction.getPaddingRight()) * mCapacity;
            } else {
                return mCapacity * mItemSize + (mCapacity - 1) * mMargins + getStarsOffset(mCapacity) * 2 * mCapacity;
            }

        } else {
            return mCapacity * mCustomizedShape.getWidth() + (mCapacity - 1) * mMargins;
        }
    }

    private void drawShapes(Canvas canvas, ShapeView view) {
        canvas.save();
        canvas.translate(paddingLeft, paddingTop);
        if (mDrawingMode == MODE_RENDERING) {
            int offset = view.getOffset();
            for (int i = 0; i < mCapacity; i++) {
                view.draw(canvas);
//                view.setWidth((int) UI.dp2px(getContext(), 40));
//                view.setHeight(view.getWidth());
//                requestLayout();
                canvas.translate(view.getWidth() + mMargins - offset, 0);
                Logger.print(TYPE_ERROR, TAG,
                        "trans w", "" + (view.getWidth() + mMargins - offset), "originW", "" + view.getWidth(), "margins",
                        "" + mMargins);
            }
        } else {
            int width = offDepiction.getWidth();
            for (int i = 0; i < mCapacity; i++) {
                canvas.drawBitmap(off, 0, 0, null);
                canvas.translate(width + mMargins, 0);
            }
        }
        canvas.restore();
    }

    /**
     * Get the bitmap to draw on the canvas.
     *
     * @param shapeType The type of the shape.
     * @return The final processed caching bitmap ready to draw.
     */
    private Bitmap getCachingBitmap(@ShapeType int shapeType) {
        Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        if (mDrawingMode == MODE_RENDERING) {
            fillPaint.setColor(mColor);
        } else {
            if (on != null && mUsePalette) {
                Palette palette = Palette.from(on).generate();
                Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();

                if (vibrantSwatch != null) {
                    fillPaint.setColor(vibrantSwatch.getRgb());
                } else {
                    fillPaint.setColor(mColor);
                }
            } else {
                fillPaint.setColor(mColor);
            }
        }
        fillPaint.setStyle(Paint.Style.FILL);
//        fillPaint.setShader(new LinearGradient(0, 0,
//                getRatingAreaWidth(mStarWidth, mStarCount, mGapBetween),
//                0, mRatingColor, Color.RED, Shader.TileMode.CLAMP));
        fillPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        Bitmap ratingBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas ratingCanvas = new Canvas(ratingBitmap);

        switch (shapeType) {
            case SHAPE_STAR:
                drawStars(ratingCanvas);
                break;
            case SHAPE_CIRCLE:
                drawCircles(ratingCanvas);
                break;
            case SHAPE_CUSTOMIZED:
                fillPaint.setColor(mCustomizedShape.getOverlayColor());
                drawShapes(ratingCanvas, mCustomizedShape);
                break;
        }

        Bitmap finalStar = Bitmap.createBitmap(ratingBitmap);
        Canvas finalCanvas = new Canvas(finalStar);
        finalCanvas.save();

        float adjustedFraction = getAdjustedFraction();
        Logger.print(TYPE_ERROR, TAG, "newFraction", "" + adjustedFraction);

        if (mEnableTouch) {
            finalCanvas.clipRect(paddingLeft, 0, width * mFraction, height);
        } else {
            finalCanvas.clipRect(paddingLeft, 0, width * adjustedFraction, height);
        }

        finalCanvas.drawRect(paddingLeft, 0, width, height, fillPaint);

        finalCanvas.restore();

        finalCanvas.drawBitmap(finalStar, 0, 0, mPaint);
        return finalStar;
    }

    /**
     * Retrieve the number of how many margins passed by in terms of the current scores.
     *
     * @return The number of margins passed by.
     */
    private int getMarginPassedCount() { // ok
        float per = mMaxScores * 1f / mCapacity;
        float[] perS = new float[mCapacity + 1];
        for (int i = 0; i < perS.length; i++) {
            perS[i] = i * per;
        }
        // hitArea starts from zero.
        int hitArea = -1;
        for (int i = 0; i < perS.length; i++) {
            if (i + 1 <= mCapacity) {
                if (perS[i] <= mFraction * mMaxScores && perS[i + 1] >= mFraction * mMaxScores) {
                    hitArea = i;
                    break;
                }
            }
        }
        Log.e(TAG, "hitArea: " + hitArea);
        return hitArea + 1 - 1;
    }

    private float getAdjustedFraction() {
        int marginPassedCount = getMarginPassedCount();
        float adjustedFraction;
        Logger.print(TYPE_ERROR, TAG, "old fraction", mFraction + "");
        if (mFraction <= 0 || mFraction >= 1f) {
            if (mFraction <= 0) {
                adjustedFraction = 0;
            } else {
                adjustedFraction = 1f;
            }
        } else {
            if (mDrawingMode == MODE_IMAGE) {
                // ok
                adjustedFraction = (paddingLeft + marginPassedCount * mMargins
                        + (offDepiction.getWidth() - offDepiction.getPaddingLeft() - offDepiction.getPaddingRight()) * mCapacity * mFraction +
                        marginPassedCount * (offDepiction.getPaddingLeft() + offDepiction.getPaddingRight())) / width;
            } else {
                if (mShapeType == SHAPE_STAR || mShapeType == SHAPE_CIRCLE) {
                    // TODO: 2019/2/26 cal problems
                    int offset = getStarsOffset(mCapacity);
                    adjustedFraction = (paddingLeft + marginPassedCount * mMargins + (mItemSize - offset) * mCapacity * mFraction
                            + marginPassedCount * offset) / width;
                } else {
                    // ok
                    adjustedFraction = (paddingLeft + marginPassedCount * mMargins + mFraction * mCapacity *
                            (mCustomizedShape.getWidth() - mCustomizedShape.getPaddingLeft() - mCustomizedShape.getPaddingRight())
                            + marginPassedCount * (mCustomizedShape.getPaddingLeft() + mCustomizedShape.getPaddingRight())) / width;
                }
            }
        }
        Logger.print(TYPE_ERROR, TAG, "newer fraction", adjustedFraction + "");
        return adjustedFraction;
    }

    /**
     * Recomputing the real fraction according to the given the touch point object.
     *
     * @param touchPoint Collected touch point data when touching the screen.
     * @return Adjusted fraction.
     */
    private float getRecomputedFraction(PointF touchPoint) {
        // getAdjustedFraction的逆向过程
        float recomputedFraction = 0;
        if (mDrawingMode == MODE_IMAGE) {
            int[] pos = new int[2];
            if (isHit(touchPoint, pos)) {
                RectF[] rectFs = getItemsRectS();
                float len;
                int hitOffset = pos[1];
                if (hitOffset <= 0) {
                    // left
                    len = pos[0] * (offDepiction.getWidth() - offDepiction.getPaddingLeft() - offDepiction.getPaddingRight());
                } else {
                    // right
                    len = (pos[0] + 1) * (offDepiction.getWidth() - offDepiction.getPaddingLeft() - offDepiction.getPaddingRight());
                }
                if (hitOffset <= 0) {
                    len += (touchPoint.x - rectFs[pos[0]].left);
                    Logger.print(TYPE_ERROR, TAG, "LEFT", "" + ((touchPoint.x - rectFs[pos[0]].left)), "off", off.getWidth() + "");
                }

                recomputedFraction = len / (mCapacity * (offDepiction.getWidth() - offDepiction.getPaddingLeft() - offDepiction.getPaddingRight()));
            }
        }
        return recomputedFraction;
    }

    private float getRecomputedScores(float fraction) {
        DecimalFormat format = new DecimalFormat();
        format.applyPattern("#.#");
        return Float.parseFloat(format.format(fraction * mMaxScores));
    }

    private RectF[] getItemsRectS() {
        RectF[] rectFs = new RectF[mCapacity];
        RectF tmp;
        int top = getTop();
        int bottom = getBottom();
        int offset = getStarsOffset(mCapacity);
        if (mDrawingMode == MODE_RENDERING) {
            for (int i = 0; i < mCapacity; i++) {
                rectFs[i] = tmp = new RectF();
                int left;
                if (mShapeType == SHAPE_STAR) {

                } else if (mShapeType == SHAPE_CIRCLE) {
                    left = paddingLeft + i * (mMargins + mItemSize);
                    tmp.set(left, top, left + mItemSize, bottom);
                } else {
                    left = paddingLeft + i * (mMargins + mCustomizedShape.getWidth());
                    tmp.set(left, top, left + mCustomizedShape.getWidth(), bottom);
                }
            }
        } else {
            for (int i = 0; i < mCapacity; i++) {
                rectFs[i] = tmp = new RectF();
                int left = paddingLeft + i * (mMargins + off.getWidth());
                tmp.set(left, top, left + off.getWidth(), bottom);
            }
        }
        return rectFs;
    }

    /**
     * Determine whether the given touch point is hit or not, and set the hit data.
     *
     * @param touchPoint The given touch point.
     * @param hitData    An array which only has two elements.
     *                   <br/>
     *                   Index 0 refers to hitArea, which also means how many margins passed, Index 1 refers to
     *                   hit offset. If it's negative, it means the hit point is among the drawing area, outside otherwise.
     * @return True if was hit, false otherwise.
     */
    private boolean isHit(PointF touchPoint, @Size(2) int[] hitData) {
        boolean isHit = false;
        RectF[] rectFS = getItemsRectS();
        PointF tmp = new PointF(touchPoint.x + getLeft(), touchPoint.y + getTop());
        if (rectFS != null && rectFS.length != 0) {
            for (int i = 0; i < rectFS.length; i++) {
                if (rectFS[i].contains(tmp.x, tmp.y)
                        || (i + 1 < rectFS.length && (rectFS[i + 1].left > tmp.x && tmp.x > rectFS[i].right))
                        || (i - 1 >= 0 && (rectFS[i - 1].right < tmp.x && tmp.x < rectFS[i].left))) {
                    if (hitData != null && hitData.length != 0) {
                        hitData[0] = i;
                        hitData[1] = (int) (tmp.x - rectFS[i].right);
                    }
                    isHit = true;
                    break;
                }
            }
        }
        return isHit;
    }

    // (n - 2) * 180
    private int getStarsOffset(int num) {
        float perDeg = 360f / num;
        float degA = perDeg / 2 / 2;
        double first = Math.cos(rad(degA)) * mRadius;
        Logger.print(TYPE_ERROR, TAG, "radius ", "" + mRadius, " first ", "" + first);
        Logger.print(TYPE_ERROR, TAG, "offset", "" + (mRadius - first));
        return (int) (mRadius - first);
    }

    private void drawCircles(Canvas canvas) {
        canvas.save();
        canvas.translate(paddingLeft, paddingTop);
        if (mDrawingMode == MODE_RENDERING) {
            int offset = 0;
            for (int i = 0; i < mCapacity; i++) {
                canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
                canvas.translate(mItemSize + mMargins - offset, 0);
            }
        } else {
            int width = offDepiction.getWidth();
            for (int i = 0; i < mCapacity; i++) {
                canvas.drawBitmap(off, 0, 0, null);
                canvas.translate(width + mMargins, 0);
            }
        }
        canvas.restore();
    }

    public int getCapacity() {
        return mCapacity;
    }

    public void setCapacity(int capacity) {
        this.mCapacity = capacity;
        invalidate();
    }

    @ShapeType
    public int getShapeType() {
        return mShapeType;
    }

    public void setShapeType(@ShapeType int shapeType) {
        this.mShapeType = shapeType;
        invalidate();
    }

    @RenderingMode
    public int getDrawingMode() {
        return mDrawingMode;
    }

    public void setDrawingMode(@RenderingMode int mode) {
        this.mDrawingMode = mode;
        invalidate();
    }

    public float getScores() {
        return mScores;
    }

    public void setScores(float scores) {
        this.mScores = scores;
        mFraction = scores / mMaxScores;
        invalidate();
    }

    @DrawableRes
    public int getOnImageResourceId() {
        return mOnImageResourceId;
    }

    public void setOnImageResourceId(@DrawableRes int onImageResourceId) {
        this.mOnImageResourceId = onImageResourceId;
        on = drawableToBitmap(onImageResourceId);
        if (mDrawingMode == MODE_IMAGE) {
            invalidate();
        }
    }

    @DrawableRes
    public int getOffImageResourceId() {
        return mOffImageResourceId;
    }

    public void setOffImageResourceId(@DrawableRes int offImageResourceId) {
        this.mOffImageResourceId = offImageResourceId;
        offDepiction.setDrawableId(mOffImageResourceId);
        off = drawableToBitmap(offImageResourceId);
        if (mDrawingMode == MODE_IMAGE) {
            invalidate();
        }
    }

    public boolean isTouchable() {
        return mEnableTouch;
    }

    public void setEnableTouch(boolean mEnableTouch) {
        this.mEnableTouch = mEnableTouch;
    }

    public void setShapeCustomized(ShapeView shapeCustomized) {
        this.mCustomizedShape = shapeCustomized;
        this.mShapeType = SHAPE_CUSTOMIZED;
        requestLayout();
        invalidate();
    }

    public void setOnScoreChangeListener(OnScoreChangeListener listener) {
        this.listener = listener;
    }

    public int getMargins() {
        return mMargins;
    }

    public void setMargins(int mMargins) {
        this.mMargins = mMargins;
    }

    public float getFraction() {
        return mFraction;
    }

    public void setFraction(float mFraction) {
        this.mFraction = mFraction;
        invalidate();
    }

    public void setMaxScores(int mMaxScores) {
        this.mMaxScores = mMaxScores;
        invalidate();
    }

    public int getMaxScores() {
        return mMaxScores;
    }

    public CustomShapeDepiction getCustomShapeResource() {
        return offDepiction;
    }

    public void setCustomShapeResource(CustomShapeDepiction depiction) {
        this.offDepiction = depiction;
        invalidate();
    }

    @OutputMode
    public int getOutputMode() {
        return mOutputMode;
    }

    public void setOutputMode(@OutputMode int mOutputMode) {
        this.mOutputMode = mOutputMode;
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        // problem will occur if uses ((BitmapDrawable)drawable).getBitmap(). The real width and height are not accurate.
        if (drawable != null) {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    private Bitmap drawableToBitmap(@DrawableRes int drawableId) {
        Drawable drawable = mContext.getResources().getDrawable(drawableId);
        return drawableToBitmap(drawable);
    }

    @SuppressWarnings("WeakerAccess")
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    @IntDef({SHAPE_STAR, SHAPE_CIRCLE, SHAPE_CUSTOMIZED})
    public @interface ShapeType {
    }

    @SuppressWarnings("WeakerAccess")
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    @IntDef({MODE_RENDERING, MODE_IMAGE})
    public @interface RenderingMode {
    }

    public interface OnScoreChangeListener {
        void onScoreChangeListener(float outScores);
    }

    // TODO: 2019/2/21 Fix margins.

    /*
      情况比较复杂
      1. 显示模式
        1) 不存在触摸调整视觉的问题，所以根据传值，加以修正直接显示
      2. 打分模式
        1) 一定要跟手，不能因为有margins或者五角星没有贴紧而校正导致视觉上的不跟手
        2) 输出的分数是处理过的，不同于显示的view的百分比
     */

    @SuppressWarnings("WeakerAccess")
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    @IntDef({MODE_ORIGIN, MODE_FRACTION})
    public @interface OutputMode {
    }

    // No gravity
}