package com.jerryjin.ratingview.library.widget.older;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jerryjin.ratingview.library.R;
import com.jerryjin.ratingview.library.UI;

import java.math.BigDecimal;

/**
 * Only for researching use.
 */
@Deprecated
public class RatingView extends View {

    private static final float DEFAULT_RATING = 0f;
    private static final int DEFAULT_RATING_COLOR = Color.parseColor("#ff982a");
    private static final int DEFAULT_STAR_COUNT = 5;
    private static final int DEFAULT_DP_OF_STAR = 20;
    private static final int LINE_COLOR = Color.GRAY;
    private Paint mPaint;
    private int lineWidth = 3;
    private float mRating;
    private int mRatingColor;
    private float mStarWidth;
    private float mStarHeight;
    private int mStarCount = DEFAULT_STAR_COUNT;
    private int mGapBetween;
    private OnRatingChangeListener mListener;


    public RatingView(Context context) {
        super(context);
    }

    public RatingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        Context mContext = getContext();


        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(LINE_COLOR);
        mPaint.setStrokeWidth(lineWidth);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RatingView);

        mRating = ta.getFloat(R.styleable.RatingView_rating, DEFAULT_RATING);
        mRatingColor = ta.getColor(R.styleable.RatingView_ratingColor, DEFAULT_RATING_COLOR);
        mStarCount = ta.getInt(R.styleable.RatingView_starCount, DEFAULT_STAR_COUNT);
        mStarWidth = (int) ta.getDimension(R.styleable.RatingView_starSize, UI.dp2px(mContext, DEFAULT_DP_OF_STAR));
        mGapBetween = (int) ta.getDimension(R.styleable.RatingView_starGap, UI.dp2px(mContext, 5));

        mStarHeight = (float) (Math.sin(Math.toRadians(18)) * mStarWidth);

        ta.recycle();
    }

    private float recomputeOutRating(float rating) {
        BigDecimal bigDecimal = new BigDecimal(rating);
        return bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    private void recomputeRating(float rating) {
        mRating = (int) recomputeOutRating(rating);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec) {
        float result;
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        if (widthSpecMode == MeasureSpec.EXACTLY) {
            result = widthSpecSize;
        } else {
            result = (float) (0.5 * mStarWidth - Math.sin(Math.toRadians(18 * mStarWidth))
                    + DEFAULT_STAR_COUNT * mStarWidth +
                    (DEFAULT_STAR_COUNT - 1) * mGapBetween);
            if (widthSpecMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, widthSpecSize);
            }
        }
        return (int) result;
    }

    private int measureHeight(int heightMeasureSpec) {
        float result;
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (heightSpecMode == MeasureSpec.EXACTLY) {
            result = heightSpecSize;
        } else {
            result = (float) (mStarWidth * Math.cos(Math.toRadians(18)))
                    + lineWidth * 2 + 5 + (float) (0.5 * mStarWidth);
            if (heightSpecMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, heightSpecSize);
            }
        }
        return (int) result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //drawStars(canvas, mStarCount, mPaint);
        canvas.drawBitmap(getRatingBitmap(), 0, 0, null);
    }


    /**
     * Drawing stars.
     *
     * @param count the number of stars to draw.
     */
    private void drawStars(Canvas canvas, int count, Paint paint) {
        canvas.save();
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                canvas.translate(mStarWidth + mGapBetween, 0);
            }
            try {
                drawSingleStar(canvas, paint);
            } catch (OutOfRatingException e) {
                mRating = 0;
                invalidate();
                e.printStackTrace();
            }
        }
        canvas.restore();
    }

    /**
     * Drawing one actual star.
     */
    private void drawSingleStar(Canvas canvas, Paint paint) throws OutOfRatingException {
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.parseColor("#ffd167"));
        if (mRating == 0.2f || mRating == 0.4f || mRating == 0.6f || mRating == 0.8f || mRating == 1 || mRating > 1) {
            paint.setStyle(Paint.Style.FILL);
        } else if (mRating < 0) {
            throw new OutOfRatingException("Rating must be bigger or equals to zero");
        }
        Path path = getStarPath();
        canvas.drawPath(path, paint);
    }

    public RatingView setRatingProgress(float rating) {
        if (mRating != rating) {
            if (mListener != null) {
                mRating = rating;
                invalidate();
                mListener.onRatingChange(mRating);
            }
        }
        return this;
    }

    public float getRating() {
        return mRating;
    }

    public int getStarCount() {
        return mStarCount;
    }

    public RatingView setOnRatingChangeListener(OnRatingChangeListener listener) {
        this.mListener = listener;
        return this;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        checkTouch(new PointF(event.getX(), event.getY()));
        return super.onTouchEvent(event);
    }

    private void checkTouch(PointF touchPoint) {
        int paddingLeft = getPaddingLeft();
        float ratingAreaWidth = getRatingAreaWidth(mStarWidth, mStarCount, mGapBetween);
        if (touchPoint.x < paddingLeft
                || touchPoint.x > paddingLeft + ratingAreaWidth
                || touchPoint.y > getRatingAreaHeight()) {
            return;
        }
        //mPaint.setStyle(Paint.Style.FILL);
        mRating = (touchPoint.x - paddingLeft) / ratingAreaWidth;
        if (mListener != null) {
            mListener.onRatingChange(recomputeOutRating(mRating));
        }
        invalidate();
    }

    private Path getStarPath() {
        Path path = new Path();
        float xA = (float) (5 + 0.5 * mStarWidth);
        float yA = 10;
        float[] points = fivePoints(xA, yA, (int) mStarWidth);
        path.moveTo(points[0], points[1]);
        for (int i = 0; i < points.length - 1; i++) {
            path.lineTo(points[i], points[i += 1]);
        }
        return path;
    }

    private Bitmap getRatingBitmap() {
        Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(mRatingColor);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setShader(new LinearGradient(0, 0,
                getRatingAreaWidth(mStarWidth, mStarCount, mGapBetween),
                0, mRatingColor, Color.RED, Shader.TileMode.CLAMP));
        fillPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));


        int width = (int) getRatingAreaWidth(mStarWidth, mStarCount, mGapBetween);
        int height = getRatingAreaHeight();
        Bitmap starBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas starCanvas = new Canvas(starBitmap);
        drawStars(starCanvas, mStarCount, mPaint);

        Bitmap finalStar = Bitmap.createBitmap(starBitmap);
        Canvas finalCanvas = new Canvas(finalStar);
        finalCanvas.save();
        finalCanvas.clipRect(0, 0, mRating * width, height);
        finalCanvas.drawRect(0, 0, width, height, fillPaint);

        finalCanvas.restore();

        finalCanvas.drawBitmap(finalStar, 0, 0, mPaint);


        return finalStar;
    }

    private float getRatingAreaWidth(float starWidth, int starCount, int gapBetween) {
        return starCount * starWidth + (starCount - 1) * gapBetween;
    }

    private int getRatingAreaHeight() {
        return getHeight() - 5 - (int) (0.5 * mStarWidth);
    }

    private float[] fivePoints(float xA, float yA, int rFive) {
        float xB = 0;
        float xC = 0;
        float xD = 0;
        float xE = 0;
        float yB = 0;
        float yC = 0;
        float yD = 0;
        float yE = 0;
        xD = (float) (xA - rFive * Math.sin(Math.toRadians(18)));
        xC = (float) (xA + rFive * Math.sin(Math.toRadians(18)));
        yD = yC = (float) (yA + Math.cos(Math.toRadians(18)) * rFive);
        yB = yE = (float) (yA + Math.sqrt(Math.pow((xC - xD), 2) - Math.pow((rFive / 2), 2)));
        xB = xA + (rFive / 2);
        xE = xA - (rFive / 2);
        return new float[]{xA, yA, xD, yD, xB, yB, xE, yE, xC, yC, xA, yA};
    }

    public interface OnRatingChangeListener {
        void onRatingChange(float rating);
    }

}
