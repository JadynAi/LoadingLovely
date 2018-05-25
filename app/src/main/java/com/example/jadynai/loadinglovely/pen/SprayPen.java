package com.example.jadynai.loadinglovely.pen;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.Random;

/**
 * @version:
 * @FileDescription:
 * @Author:jing
 * @Since:2018/5/18
 * @ChangeList:
 */

public class SprayPen extends BasePen {

    private static final float BLUR_SIZE = 5f;

    private float mCricleR = 2;

    protected Random mRandom = new Random();

    private int mPenR = 40;

    //喷漆密度，设定为半径为10的圈内的点数
    private int mDensity = 40;

    private int mTotalNum;

    //判断滑动过快的标准距离
    private double mStandardDis;

    public SprayPen(int w, int h) {
        super(w, h);
        float v = ((float) (Math.hypot(w, h))) / 980f;
        mCricleR = v < 1 ? 1 : v;
        mStandardDis = Math.hypot(w, h) / 48;
        setSprayData();
    }

    @Override
    protected Paint generateSpecificPaint() {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
//        paint.setMaskFilter(new BlurMaskFilter(BLUR_SIZE, BlurMaskFilter.Blur.SOLID));
        return paint;
    }

    @Override
    public void setPenWidth(float width) {
        super.setPenWidth(width);
        int w = (int) (width * 0.5f);
        mPenR = w < 5 ? 5 : w;
        setSprayData();
    }

    /**
     * 设置一些喷漆的属性
     */
    private void setSprayData() {
        mTotalNum = mPenR / 10 * mDensity;
    }

    @Override
    protected void drawDetail(Canvas canvas) {
        if (getPoints().isEmpty()) {
            return;
        }
        //当确实在滑动的时候，并且距离过于小的时候，不绘制，避免某些点过浓.
        if (getTotalDis() >= mPenR * getPoints().size() && getLastDis() <= (mPenR / 2)) {
            return;
        }
        double gapCircle = getLastDis() - mPenR * 2;
        if (gapCircle >= mStandardDis) {
            //手速过快时
            float stepDis = mPenR * 1.6f;
            int v = (int) (getLastDis() / stepDis);
            float gapX = getPoints().get(getPoints().size() - 1).x - getPoints().get(getPoints().size() - 2).x;
            float gapY = getPoints().get(getPoints().size() - 1).y - getPoints().get(getPoints().size() - 2).y;
            for (int i = 1; i <= v; i++) {
                float x = (float) (getPoints().get(getPoints().size() - 2).x + (gapX * i * stepDis / getLastDis()));
                float y = (float) (getPoints().get(getPoints().size() - 2).y + (gapY * i * stepDis / getLastDis()));
                Log.d(TAG, "drawDetail calculate : " + calculate(i, 1, v));
                drawSpray(x, y, (int) (mTotalNum * calculate(i, 1, v)), mRandom.nextBoolean());
            }
        } else {
            //匀速
            drawSpray(getCurPoint().x, getCurPoint().y, mTotalNum, true);
        }
    }

    private void drawSpray(float x, float y, int totalNum, boolean isUniform) {
        for (int i = 0; i < totalNum; i++) {
            float[] randomPoint = getRandomPoint(x, y, mPenR, isUniform);
            mCanvas.drawCircle(randomPoint[0], randomPoint[1], mCricleR, mPaint);
        }
    }

    /**
     * 根据圆解析式求随机点
     */
    private float[] getRandomPoint(float baseX, float baseY, int r) {
        if (r <= 0) {
            r = 1;
        }
        float[] ints = new float[2];
        float x = mRandom.nextInt(r);
        float y = (float) Math.sqrt(Math.pow(r, 2) - Math.pow(x, 2));
        y = mRandom.nextInt((int) y);

        x = baseX + getRandomPNValue(x);
        y = baseY + getRandomPNValue(y);
        ints[0] = x;
        ints[1] = y;
        return ints;
    }

    /**
     * @param baseX
     * @param baseY
     * @param r
     * @param isUniform 是否让点均匀分布，否则自内向外由密至疏
     * @return
     */
    private float[] getRandomPoint(float baseX, float baseY, int r, boolean isUniform) {
        if (r <= 0) {
            r = 1;
        }
        float[] ints = new float[2];
        int degree = mRandom.nextInt(360);
        double curR;
        if (isUniform) {
            //均匀分布
            curR = Math.sqrt(mRandom.nextDouble()) * r;
        } else {
            //自内向外扩散
            curR = mRandom.nextInt(r);
        }
        curR = curR == 0 ? 1 : curR;
        float x = (float) (curR * Math.cos(Math.toRadians(degree)));
        float y = (float) (curR * Math.sin(Math.toRadians(degree)));
        x = baseX + getRandomPNValue(x);
        y = baseY + getRandomPNValue(y);
        ints[0] = x;
        ints[1] = y;
        return ints;
    }

    private float getRandomPNValue(float value) {
        return mRandom.nextBoolean() ? value : 0 - value;
    }

    /**
     * 使用（x-（min+max）/2)^2/（min-（min+max）/2）^2作为粒子密度比函数
     */
    private static float calculate(int index, int min, int max) {
        float maxProbability = 0.6f;
        float minProbability = 0.15f;
        if (max - min + 1 <= 4) {
            return maxProbability;
        }
        int mid = (max + min) / 2;
        int maxValue = (int) Math.pow(mid - min, 2);
        float ratio = (float) (Math.pow(index - mid, 2) / maxValue);
        if (ratio >= maxProbability) {
            return maxProbability;
        } else if (ratio <= minProbability) {
            return minProbability;
        } else {
            return ratio;
        }
    }
}
