package com.example.jadynai.loadinglovely.pen;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

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

    private int mPenW = 40;

    //喷漆密度，设定为半径为10的圈内的点数
    private int mDensity = 40;

    private int mTotalNum;

    public SprayPen(int w, int h) {
        super(w, h);
        float v = w / 504f;
        mCricleR = v < 1 ? 1 : v;
        setSprayData();
    }

    @Override
    protected Paint generateSpecificPaint() {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
//        paint.setStrokeCap(Paint.Cap.ROUND);//结束的笔画为圆心
//        paint.setStrokeJoin(Paint.Join.ROUND);//连接处为圆角
        paint.setAntiAlias(true);
//        paint.setStrokeMiter(1.0f);//设置笔画倾斜度
        //模糊效果
//        paint.setMaskFilter(new BlurMaskFilter(BLUR_SIZE, BlurMaskFilter.Blur.SOLID));
        return paint;
    }

    @Override
    public void setPenWidth(float width) {
        super.setPenWidth(width);
        int w = (int) (width * 0.5f);
        mPenW = w < 5 ? 5 : w;
        setSprayData();
    }

    /**
     * 设置一些喷漆的属性
     */
    private void setSprayData() {
        mTotalNum = mPenW / 10 * mDensity;
    }

    @Override
    protected void drawDetail(Canvas canvas) {
        if (getPoints().isEmpty()) {
            return;
        }

        float x = getCurPoint().x;
        float y = getCurPoint().y;
        drawSpray(x, y);
    }

    private void drawSpray(float x, float y) {
        for (int i = 0; i < mTotalNum; i++) {
            float[] randomPoint = getRandomPoint(x, y, mPenW, false);
            mCanvas.drawCircle(randomPoint[0], randomPoint[1], mCricleR, mPaint);
        }
    }

    private float[] getRandomPoint(float baseX, float baseY, int r) {
        if (r <= 0) {
            r = 1;
        }
        float[] ints = new float[2];
        float x = mRandom.nextInt(r + 1);
        float y = (float) Math.sqrt(Math.pow(r, 2) - Math.pow(x, 2));
//        y = mRandom.nextInt((int) y);

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
}
