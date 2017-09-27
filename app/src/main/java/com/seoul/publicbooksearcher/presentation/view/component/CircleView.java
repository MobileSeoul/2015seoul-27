package com.seoul.publicbooksearcher.presentation.view.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.seoul.publicbooksearcher.R;

public class CircleView extends View {

    private static int DEFAULT_TITLE_COLOR = Color.CYAN;
    private static int DEFAULT_SUBTITLE_COLOR = Color.WHITE;

    private static String DEFAULT_TITLE = "Title";

    private static boolean DEFAULT_SHOW_TITLE = true;

    private static float DEFAULT_TITLE_SIZE = 25f;
    private static float DEFAULT_SUBTITLE_SIZE = 20f;

    private static int DEFAULT_STROKE_COLOR = Color.CYAN;
    private static int DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    private static int DEFAULT_FILL_COLOR = Color.DKGRAY;

    private static float DEFAULT_STROKE_WIDTH = 5f;
    private static float DEFAULT_FILL_RADIUS = 0.9f;

    private static final int DEFAULT_VIEW_SIZE = 96;

    private int mTitleColor = DEFAULT_TITLE_COLOR;
    private int mStrokeColor = DEFAULT_STROKE_COLOR;
    private int mBackgroundColor = DEFAULT_BACKGROUND_COLOR;
    private int mFillColor = DEFAULT_FILL_COLOR;

    private String mTitleText = DEFAULT_TITLE;

    private float mTitleSize = DEFAULT_TITLE_SIZE;
    private float mSubtitleSize = DEFAULT_SUBTITLE_SIZE;
    private float mStrokeWidth = DEFAULT_STROKE_WIDTH;
    private float mFillRadius = DEFAULT_FILL_RADIUS;

    private boolean mShowTitle = DEFAULT_SHOW_TITLE;

    private TextPaint mTitleTextPaint;

    private Paint mBackgroundPaint;
    private Paint mFillPaint;
    private RectF mInnerRectF;

    private int mViewSize;

    public CircleView(Context context) {
        super(context);
        init(null, 0);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CircleView, defStyle, 0);

        if(a.hasValue(R.styleable.CircleView_titleText)){
            mTitleText = a.getString(R.styleable.CircleView_titleText);
        }

        mTitleColor = a.getColor(R.styleable.CircleView_titleColor,DEFAULT_TITLE_COLOR);
        mBackgroundColor = a.getColor(R.styleable.CircleView_backgroundColorValue,DEFAULT_BACKGROUND_COLOR);
        mStrokeColor = a.getColor(R.styleable.CircleView_strokeColorValue,DEFAULT_STROKE_COLOR);
        mFillColor = a.getColor(R.styleable.CircleView_fillColor,DEFAULT_FILL_COLOR);

        mTitleSize = a.getDimension(R.styleable.CircleView_titleSize,DEFAULT_TITLE_SIZE);
        mSubtitleSize = a.getDimension(R.styleable.CircleView_subtitleSize,DEFAULT_SUBTITLE_SIZE);

        mStrokeWidth = a.getFloat(R.styleable.CircleView_strokeWidthSize,DEFAULT_STROKE_WIDTH);
        mFillRadius = a.getFloat(R.styleable.CircleView_fillRadius,DEFAULT_FILL_RADIUS);

        a.recycle();

        //Title TextPaint
        mTitleTextPaint = new TextPaint();
        mTitleTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTitleTextPaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        mTitleTextPaint.setTextAlign(Paint.Align.CENTER);
        mTitleTextPaint.setLinearText(true);
        mTitleTextPaint.setColor(mTitleColor);
        mTitleTextPaint.setTextSize(mTitleSize);

        //Background Paint
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(mBackgroundColor);

        //Fill Paint
        mFillPaint = new Paint();
        mFillPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setColor(mFillColor);

        mInnerRectF = new RectF();

    }

    private void invalidateTextPaints(){
        mTitleTextPaint.setTextSize(mTitleSize);
        invalidate();
    }

    private void invalidatePaints(){
        mBackgroundPaint.setColor(mBackgroundColor);
        //mFillPaint.setColor(mFillColor);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = resolveSize(DEFAULT_VIEW_SIZE, widthMeasureSpec);
        int height = resolveSize(DEFAULT_VIEW_SIZE, heightMeasureSpec);
        mViewSize = Math.min(width, height);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mInnerRectF.set(0, 0, mViewSize, mViewSize);
        mInnerRectF.offset((getWidth() - mViewSize) / 2, (getHeight() - mViewSize) / 2);

        float centerX = mInnerRectF.centerX();
        float centerY = mInnerRectF.centerY();

        canvas.drawArc(mInnerRectF, 0, 360, true, mBackgroundPaint);

        int xPos = (int) centerX;
        int yPos = (int) (centerY - (mTitleTextPaint.descent() + mTitleTextPaint.ascent()) / 2);


        if (mShowTitle) {
            canvas.drawText(mTitleText,
                    xPos,
                    yPos,
                    mTitleTextPaint);
        }
    }

    /**
     * Sets whether the view's title string will be shown.
     * @param flag The boolean value.
     */
    public void setShowTitle(boolean flag){
        this.mShowTitle = flag;
        invalidate();
    }

    /**
     * Gets the title string attribute value.
     * @return The title string attribute value.
     */
    public String getTitleText() {
        return mTitleText;
    }

    /**
     * Sets the view's title string attribute value.
     * @param title The example string attribute value to use.
     */
    public void setTitleText(String title) {
        mTitleText = title;
        invalidate();
    }

    /**
     * Gets the stroke color attribute value.
     * @return The stroke color attribute value.
     */
    public int getStrokeColor() {
        return mStrokeColor;
    }

    /**
     * Gets the background color attribute value.
     * @return The background color attribute value.
     */
    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    /**
     * Gets the fill color attribute value.
     * @return The fill color attribute value.
     */
    public int getFillColor() {
        return mStrokeColor;
    }

    /**
     * Sets the view's stroke color attribute value.
     * @param strokeColor The stroke color attribute value to use.
     */
    public void setStrokeColor(int strokeColor) {
        mStrokeColor = strokeColor;
        invalidatePaints();
    }

    /**
     * Sets the view's background color attribute value.
     * @param backgroundColor The background color attribute value to use.
     */
    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
        invalidatePaints();
    }

    /**
     * Sets the view's fill color attribute value.
     * @param fillColor The fill color attribute value to use.
     */
    public void setFillColor(int fillColor) {
        mFillColor = fillColor;
        invalidatePaints();
    }

    /**
     * Gets the stroke width dimension attribute value.
     * @return The stroke width dimension attribute value.
     */
    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    /**
     * Sets the view's stroke width attribute value.
     * @param strokeWidth The stroke width attribute value to use.
     */
    public void setBackgroundColor(float strokeWidth) {
        mStrokeWidth = strokeWidth;
        invalidate();
    }

    /**
     * Gets the fill radius dimension attribute value.
     * @return The fill radius dimension attribute value.
     */
    public float getFillRadius() {
        return mFillRadius;
    }

    /**
     * Sets the view's fill radius attribute value.
     * @param fillRadius The fill radius attribute value to use.
     */
    public void setFillRadius(float fillRadius) {
        mFillRadius = fillRadius;
        invalidate();
    }

    /**
     * Gets the title size dimension attribute value.
     * @return The title size dimension attribute value.
     */
    public float getTitleSize() {
        return mTitleSize;
    }

    /**
     * Sets the view's title size dimension attribute value.
     * @param titleSize The title size dimension attribute value to use.
     */
    public void setTitleSize(float titleSize) {
        mTitleSize = titleSize;
        invalidateTextPaints();
    }

    /**
     * Gets the subtitle size dimension attribute value.
     * @return The subtitle size dimension attribute value.
     */
    public float getSubtitleSize() {
        return mSubtitleSize;
    }

    /**
     * Sets the view's subtitle size dimension attribute value.
     * @param subtitleSize The subtitle size dimension attribute value to use.
     */
    public void setSubtitleSize(float subtitleSize) {
        mSubtitleSize = subtitleSize;
        invalidateTextPaints();
    }

}

