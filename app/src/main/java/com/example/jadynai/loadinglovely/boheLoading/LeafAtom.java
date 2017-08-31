package com.example.jadynai.loadinglovely.boheLoading;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.view.animation.LinearInterpolator;

/**
 * @version:
 * @FileDescription:
 * @Author:jing
 * @Since:2017/8/22
 * @ChangeList:
 */

public class LeafAtom {

    public static final float PETIOLE_RATIO = 0.1f;//叶柄所占比例
    private static final String TAG = "LeafAtom";
    public static final int EXPRIENCE_OFFSET = 25;
    private float mX;
    private float mY;

    private float mWidth;
    private float mHeight;

    private PointF mBezierBottom;
    private PointF mBezierControl;
    private PointF mBezierTop;

    private long mPetioleTime;//叶柄动画的时间
    private long mArcTime;//左右轮廓弧线的时间
    private long mLastLineTime;//最后一段叶脉的时间

    private float mVeinBottomY;//叶脉最底端的Y轴坐标
    private float mOneNodeY;//第一个分叉节点
    private float mTwoNodeY;

    private Path mMainPath;
    private ValueAnimator mPetioleAnim;
    private ValueAnimator mArcAnim;
    private ValueAnimator mArcRightAnim;
    private ValueAnimator mLastAnim;

    private AnimatorSet mEngine;//集合动画，发动引擎
    private Path mOneLpath;
    private Path mOneRpath;
    private Path mTwoLpath;
    private Path mTwoRpath;


    public LeafAtom(int width, int height, long duration) {

        mWidth = width;
        mHeight = height;

        setStepTime(duration);

        mBezierBottom = new PointF(mWidth * 0.5f, mHeight * (1 - PETIOLE_RATIO));//左侧轮廓底部点
        mBezierControl = new PointF(0, mHeight * (1 - 3 * PETIOLE_RATIO));//左侧轮廓控制点
        mBezierTop = new PointF(mWidth * 0.5f, 0);//左侧轮廓顶部结束点

        mVeinBottomY = mHeight * (1 - PETIOLE_RATIO) - 10;//右侧轮廓底部点Y轴坐标，稍稍低一点
        mOneNodeY = mVeinBottomY * 4 / 5;//第一个节点的Y轴坐标
        mTwoNodeY = mVeinBottomY * 2 / 5;//第二个节点Y轴坐标
        initEngine();
        setOrginalStatus();
    }

    private void setStepTime(long duration) {
        mPetioleTime = (long) (duration * PETIOLE_RATIO);//绘制叶柄的时间
        mArcTime = (long) (duration * (1 - PETIOLE_RATIO) * 0.4f);//左右轮廓弧线的时间
        mLastLineTime = duration - mPetioleTime - mArcTime * 2;//最后一段叶脉的时间
    }

    /**
     * 初始化path引擎
     */
    private void initEngine() {
        //叶柄动画，Y轴变化由底部运动到叶柄高度的地方
        mPetioleAnim = ValueAnimator.ofFloat(mHeight, mHeight * (1 - PETIOLE_RATIO)).setDuration(mPetioleTime);
        //左右轮廓贝塞尔曲线，只需要只奥时间变化是从0~1的。起点、控制点、结束点都知道了
        mArcAnim = ValueAnimator.ofFloat(0, 1.0f).setDuration(mArcTime);
        //绘制叶脉的动画
        mLastAnim = ValueAnimator.ofFloat(mVeinBottomY, 0).setDuration(mLastLineTime);

        mPetioleAnim.setInterpolator(new LinearInterpolator());
        mArcAnim.setInterpolator(new LinearInterpolator());
        mLastAnim.setInterpolator(new LinearInterpolator());
        mArcRightAnim = mArcAnim.clone();

        mPetioleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mY = (float) animation.getAnimatedValue();
            }
        });
        mArcAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                computeArcPointF(animation, true);
            }
        });
        mArcRightAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                computeArcPointF(animation, false);
            }
        });
        mLastAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mY = (float) animation.getAnimatedValue();
                float tan = (float) Math.tan(Math.toRadians(30));
                if (mY <= mOneNodeY && mY > mTwoNodeY) {
                    mOneLpath.moveTo(mX, mOneNodeY);
                    mOneRpath.moveTo(mX, mOneNodeY);
                    //这里的参数x和y代表相对当前位置偏移量，y轴不加偏移量会空一截出来，这里的15是经验值
                    mMainPath.addPath(mOneLpath, 0, EXPRIENCE_OFFSET);
                    mMainPath.addPath(mOneRpath, 0, EXPRIENCE_OFFSET);
                    //第一个节点和第二个节点之间
                    float gapY = mOneNodeY - mY;
                    mOneLpath.rLineTo(-gapY * tan, -gapY);
                    mOneRpath.lineTo(mX + gapY * tan, mY);
                } else if (mY <= mTwoNodeY) {
                    mTwoLpath.moveTo(mX, mTwoNodeY);
                    mTwoRpath.moveTo(mX, mTwoNodeY);

                    //第二个节点，为避免线超出叶子，取此时差值的一半作计算
                    float gapY = (mTwoNodeY - mY) * 0.5f;
                    mMainPath.addPath(mTwoLpath, 0, EXPRIENCE_OFFSET);
                    mMainPath.addPath(mTwoRpath, 0, EXPRIENCE_OFFSET);

                    mTwoLpath.rLineTo(-gapY * tan, -gapY);
                    mTwoRpath.rLineTo(gapY * tan, -gapY);
                }
            }
        });

        mEngine = new AnimatorSet();
        mEngine.playSequentially(mPetioleAnim, mArcAnim, mArcRightAnim, mLastAnim);
        mEngine.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setOrginalStatus();
            }
        });
    }

    private void computeArcPointF(ValueAnimator animation, boolean isLeft) {
        float ratio = (float) animation.getAnimatedValue();
        //ratio从0~1变化，左右轮廓三个点不一样
        PointF bezierStart = isLeft ? mBezierBottom : mBezierTop;
        PointF bezierControl = isLeft ? mBezierControl : new PointF(mWidth, mHeight * (1 - 3 * PETIOLE_RATIO));
        PointF bezierEnd = isLeft ? mBezierTop : new PointF(mWidth * 0.5f, mVeinBottomY);
        PointF pointF = calculateCurPoint(ratio, bezierStart, bezierControl, bezierEnd);
        mX = pointF.x;
        mY = pointF.y;
    }

    public void drawGraph(Canvas canvas, Paint paint) {
        if (mEngine.isStarted()) {
            canvas.drawPath(mMainPath, paint);
            mMainPath.lineTo(mX, mY);
        } else {
            mEngine.start();
        }
    }

    private PointF calculateCurPoint(float t, PointF p0, PointF p1, PointF p2) {
        PointF point = new PointF();
        float temp = 1 - t;
        point.x = temp * temp * p0.x + 2 * t * temp * p1.x + t * t * p2.x;
        point.y = temp * temp * p0.y + 2 * t * temp * p1.y + t * t * p2.y;
        return point;
    }

    public void setWidthAndHeight(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public void setOrginalStatus() {
        mX = mWidth * 0.5f;
        mY = mHeight;
        if (null == mMainPath) {
            mMainPath = new Path();
            mOneLpath = new Path();
            mOneRpath = new Path();
            mTwoLpath = new Path();
            mTwoRpath = new Path();
        }
        mMainPath.rewind();
        mOneLpath.rewind();
        mOneRpath.rewind();
        mTwoLpath.rewind();
        mTwoRpath.rewind();

        mMainPath.moveTo(mX, mY);
    }

    public void endAndClear() {
        mPetioleAnim.end();
        mArcAnim.end();
        mLastAnim.end();
        mEngine.end();
        setOrginalStatus();
    }

    public void setTotalDuration(long totalDuration) {
        setStepTime(totalDuration);
    }

    public void start() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (mEngine.isPaused()) {
                mEngine.resume();
                return;
            }
        }
        if (mEngine.isRunning()) {
            mEngine.end();
        }
        mEngine.start();
    }

    public void pause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mEngine.pause();
        }
    }

}
