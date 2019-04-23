package com.example.jadynai.loadinglovely.pullarc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * @version:
 * @FileDescription:
 * @Author:jing
 * @Since:2018/7/13
 * @ChangeList:
 */
public class ArcContainer extends RelativeLayout {

    Context mContext;

    Path mClipPath;

    Paint mPaint;

    private int mOriginalHeight;

    private PointF mPointStart;
    private PointF mPointEnd;
    private PointF mPointMid;

    public ArcContainer(Context context) {
        super(context);
        init(context, null);
    }

    public ArcContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        mClipPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mOriginalHeight == 0) {
            mOriginalHeight = getMeasuredHeight();
            mPointStart = new PointF(0, mOriginalHeight);
            mPointEnd = new PointF(getMeasuredWidth(), mOriginalHeight);
            mPointMid = new PointF(getMeasuredWidth(), mOriginalHeight);
        }
        mClipPath.reset();
        mClipPath.moveTo(0, mOriginalHeight);
        mPointMid.y = getMeasuredHeight();
        mClipPath.cubicTo(0, mOriginalHeight, getMeasuredWidth() / 2,
                calculateBezierPeak(0, mOriginalHeight,
                        getMeasuredWidth() / 2, getMeasuredHeight(), getMeasuredWidth(), mOriginalHeight)[1],
                getMeasuredWidth(), mOriginalHeight);
        mClipPath.lineTo(getMeasuredWidth(), getHeight());
        mClipPath.lineTo(0, getHeight());
        mClipPath.close();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int saveCount = canvas.saveLayer(0, 0, getMeasuredWidth(), getMeasuredHeight(), null, Canvas.ALL_SAVE_FLAG);
        super.dispatchDraw(canvas);
        canvas.drawPath(mClipPath, mPaint);
        canvas.restoreToCount(saveCount);
    }

    private float[] calculateBezierPeak(int startX, int startY, int midX, int midY, int endX, int endY) {
        float temp = 0.5f;
        float x = (float) ((midX - Math.pow(temp, 2) * (startX + endX)) / (2 * Math.pow(temp, 2)));
        float y = (float) ((midY - Math.pow(temp, 2) * (startY + endY)) / (2 * Math.pow(temp, 2)));
        return new float[] {x, y};
    }
}
