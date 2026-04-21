package com.example.allgoods.UI.Customer.Reviews.View;

import android.content.Context;

import android.graphics.Canvas;

import android.graphics.Color;

import android.graphics.Paint;

import android.util.AttributeSet;

import android.view.MotionEvent;

import android.view.View;
import android.graphics.Path;
import android.graphics.RectF;

public class CustomRatingSeekBar extends View {

    private Paint paint;

    private Paint thumbPaint;

    private float progress = 0f;

    private Paint backgroundPaint;

    public CustomRatingSeekBar(Context context) {

        super(context);

        init();

    }

    public CustomRatingSeekBar(Context context, AttributeSet attrs) {

        super(context, attrs);

        init();

    }

    public CustomRatingSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);

        init();

    }

    private void init() {

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setColor(Color.parseColor("#E9EDF3"));

        paint.setStyle(Paint.Style.FILL);

        thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        thumbPaint.setColor(Color.BLACK);

        paint.setStrokeCap(Paint.Cap.ROUND);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.parseColor("#E9EDF3"));
        backgroundPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float height = getHeight();
        float centerY = height / 2f;
        float thumbRadius = dpToPx(10);

        float padding = thumbRadius;
        float availableWidth = getWidth() - (padding * 2);

        float barHeight = dpToPx(20);
        float bgRadius = barHeight / 2f;

        RectF backgroundRect = new RectF(padding, centerY - (barHeight / 2f), getWidth() - padding, centerY + (barHeight / 2f));
        backgroundPaint.setColor(Color.parseColor("#FFFFFF"));
        canvas.drawRoundRect(backgroundRect, bgRadius, bgRadius, backgroundPaint);

        canvas.save();
        Path clipPath = new Path();
        clipPath.addRoundRect(backgroundRect, bgRadius, bgRadius, Path.Direction.CW);
        canvas.clipPath(clipPath);

        paint.setColor(Color.parseColor("#F5F6FA"));
        int segments = 100;
        for (int i = 0; i < segments; i++) {
            float ratio = (float) i / segments;
            float stroke = dpToPx(4) + (barHeight * ratio);

            float startX = padding + (availableWidth * ratio);
            float endX = padding + (availableWidth * ((float) (i + 1) / segments));

            paint.setStrokeWidth(stroke);
            canvas.drawLine(startX, centerY, endX, centerY, paint);
        }
        canvas.restore();

        // ball seekbar
        float thumbX = padding + (progress * availableWidth);
        canvas.drawCircle(thumbX, centerY, thumbRadius, thumbPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float thumbRadius = dpToPx(10);
        float padding = thumbRadius;
        float availableWidth = getWidth() - (padding * 2);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float x = event.getX() - padding;
                progress = x / availableWidth;

                progress = Math.max(0f, Math.min(progress, 1f));

                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    public float getProgressValue() {

        return progress * 5f;

    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredHeight = dpToPx(60);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int height;

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        int width = MeasureSpec.getSize(widthMeasureSpec);

        setMeasuredDimension(width, height);
    }

}
