package com.example.jadynai.loadinglovely.animtext;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * @version:
 * @FileDescription:
 * @Author:jing
 * @Since:2017/7/6
 * @ChangeList:
 */

public class CanvasView extends View {

    private static final String TAG = "CanvasView";
    private Paint mPaint;

    private Path mAnimPath;
    private PathMeasure mPathMeasure;
    private ValueAnimator mValueAnimator;
    private int mTextCount;

    private float[] mPoss = new float[2];
    private float[] mTan = new float[2];
    private Path mOrignalPath;

    public CanvasView(Context context) {
        super(context);
        init(context, null);
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, null);
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(3.5f);

        mAnimPath = new Path();
    }

    public void setPath(Path orignalPath) {
        mOrignalPath = orignalPath;
        if (null == mPathMeasure) {
            mPathMeasure = new PathMeasure();
        }
//        //先重置一下需要显示动画的path
        mAnimPath.reset();
        mAnimPath.moveTo(0, 0);
        mPathMeasure.setPath(orignalPath, false);
//        //这里仅仅是为了 计算一下每一段的duration
        mTextCount = 0;
        while (mPathMeasure.getLength() != 0) {
            mPathMeasure.nextContour();
            mTextCount++;
        }
        //经过上面这段计算duration代码的折腾 需要重新初始化pathMeasure
        mPathMeasure.setPath(orignalPath, false);
        mPaint.setStyle(Paint.Style.STROKE);
        initEngine();
    }

    private void initEngine() {
        if (null == mValueAnimator) {
            mValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
            mValueAnimator.setDuration(1000);
            mValueAnimator.setInterpolator(new LinearInterpolator());
            mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    //获取一个段落
                    mPathMeasure.getSegment(0, mPathMeasure.getLength() * value, mAnimPath, true);
//                    mPathMeasure.getPosTan(value * mPathMeasure.getLength(), mPoss, null);
//                    Log.d(TAG, "x : " + mPoss[0]);
//                    Log.d(TAG, "y : " + mPoss[1]);
                    invalidate();
                }
            });

            mValueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationRepeat(Animator animation) {
                    super.onAnimationRepeat(animation);
                    mPathMeasure.getSegment(0, mPathMeasure.getLength(), mAnimPath, true);
                    //绘制完一条Path之后，再绘制下一条
                    mPathMeasure.nextContour();
//                    //长度为0 说明一次循环结束
                    if (mPathMeasure.getLength() == 0) {
                        animation.end();
                    }
                    invalidate();
                }
            });
        } else {
        }
    }

    public void setTotalDuration(long duration) {
        handleEmptyOrignalPath();
        mValueAnimator.setDuration(duration / mTextCount);
    }

    public void start() {
        handleEmptyOrignalPath();
        if (mValueAnimator.isRunning()) {
            mValueAnimator.end();
        }
        mValueAnimator.start();
        invalidate();
    }

    private void handleEmptyOrignalPath() {
        if (null == mValueAnimator) {
            setPath(getDefPath());
        }
    }

    public void stop() {
        handleEmptyOrignalPath();
        mValueAnimator.end();
    }

    private Path getDefPath() {
        Path textPath = new Path();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setTextSize(50);
        String s = "空的";
        paint.getTextPath(s, 0, s.length(), 0, 50, textPath);
        textPath.close();
        return textPath;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
//        mPaint.setColor(Color.GREEN);
//        canvas.drawLine(0, 600, 1000, 600, mPaint);
//        canvas.save();
//
//        canvas.rotate(20, 0, 600);
//        mPaint.setColor(Color.RED);
//        canvas.drawLine(0, 600, 1000, 600, mPaint);
//        canvas.save();
//
//        canvas.rotate(30, 0, 600);
//        mPaint.setColor(Color.BLACK);
//        canvas.drawLine(0, 600, 1000, 600, mPaint);
//        canvas.save();

//        canvas.restoreToCount(2);
//        canvas.rotate(10, 0, 600);
//        mPaint.setColor(Color.BLUE);
//        canvas.drawLine(0, 600, 1000, 600, mPaint);
        if (null != mPathMeasure && mPathMeasure.getLength() == 0) {
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(mOrignalPath, mPaint);
            return;
        }
        canvas.drawPath(mAnimPath, mPaint);
    }


    static class Polygon {

        // Polygon coodinates.
        private float[] polyY, polyX;

        // Number of sides in the polygon.
        private int polySides;

        /**
         * Default constructor.
         *
         * @param px Polygon y coods.
         * @param py Polygon x coods.
         * @param ps Polygon sides count.
         */
        public Polygon(float[] px, float[] py, int ps) {
            polyX = px;
            polyY = py;
            polySides = ps;
        }

        /**
         * Checks if the Polygon contains a point.
         *
         * @param x Point horizontal pos.
         * @param y Point vertical pos.
         * @return Point is in Poly flag.
         * @see "http://alienryderflex.com/polygon/"
         */
        public boolean contains(float x, float y) {
            boolean oddTransitions = false;
            for (int i = 0, j = polySides - 1; i < polySides; j = i++) {
                if ((Float.compare(polyY[i], y) < 0 && Float.compare(polyY[j], y) >= 0) || (Float.compare(polyY[j], y) < 0 && Float.compare(polyY[i], y) >= 0)) {
                    if (Float.compare(polyX[i] + (y - polyY[i]) / (polyY[j] - polyY[i]) * (polyX[j] - polyX[i]), x) < 0) {
                        oddTransitions = !oddTransitions;
                    }
                }
            }
            return oddTransitions;
        }
    }
}
