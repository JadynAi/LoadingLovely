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
        // 保留一份原始Path，用作绘制最终填色的文字
        mOrignalPath = orignalPath;
        if (null == mPathMeasure) {
            mPathMeasure = new PathMeasure();
        }
        //先重置一下需要显示动画的path
        mAnimPath.reset();
        mAnimPath.moveTo(0, 0);
        mPathMeasure.setPath(orignalPath, false);
        // getLength（）方法获得的是当前path的长度；而nextContour（）方法是将Path切换到下一段Path，多应用在复杂path中
        mTextCount = 0;
        // 计算文字总共有多少段Path
        while (mPathMeasure.nextContour()) {
            mTextCount++;
        }
        // PathMeasure重新设置一次
        mPathMeasure.setPath(orignalPath, false);
        mPaint.setStyle(Paint.Style.STROKE);
        initEngine();
    }

    private void initEngine() {
        if (null == mValueAnimator) {
            // 如果一个文本Path包含n个小Path，那么属性动画会Repeat运行n次，每一段小path默认动画时间为900ms
            mValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
            mValueAnimator.setDuration(900);
            mValueAnimator.setInterpolator(new LinearInterpolator());
        }
        // 引擎无限次重复发动
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                // 将一段小Path从0%到100%赋值到mAnimPath中，调用重绘
                mPathMeasure.getSegment(0, mPathMeasure.getLength() * value, mAnimPath, true);
                invalidate();
            }
        });

        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
                //绘制完一条Path之后，再绘制下一条，直到完成为止。
                if (!mPathMeasure.nextContour()) {
                    animation.end();
                }
                invalidate();
            }
        });
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
        if (null != mPathMeasure && mPathMeasure.getLength() == 0) {
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(mOrignalPath, mPaint);
            return;
        }
        canvas.drawPath(mAnimPath, mPaint);
    }
}
