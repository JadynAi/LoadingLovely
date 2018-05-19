package com.example.jadynai.loadinglovely.pen;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

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

    private int mPenW = 40;

    public SprayPen(int w, int h) {
        super(w, h);
        float v = w / 504f;
        mCricleR = v < 1 ? 1 : v;
    }

    @Override
    protected Paint generateSpecificPaint() {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeCap(Paint.Cap.ROUND);//结束的笔画为圆心
        paint.setStrokeJoin(Paint.Join.ROUND);//连接处为圆角
        paint.setAntiAlias(true);
        paint.setStrokeMiter(1.0f);//设置笔画倾斜度
        //模糊效果
//        paint.setMaskFilter(new BlurMaskFilter(BLUR_SIZE, BlurMaskFilter.Blur.SOLID));
        return paint;
    }

    @Override
    public void setPenWidth(float width) {
        super.setPenWidth(width);
        int w = (int) (width * 0.5f);
        mPenW = w < 5 ? 5 : w;
    }

    @Override
    protected void drawDetail(Canvas canvas) {
        for (int i = 0; i < 80; i++) {
            float[] randomPoint = getRandomPoint(getCurPoint().x, getCurPoint().y, mRandom.nextInt(mPenW));
            mCanvas.drawCircle(randomPoint[0], randomPoint[1], mCricleR, mPaint);
        }
    }

    /**
     * 以basex和basey为原点，计算半径为r上圆的点
     *
     * @param baseX
     * @param baseY
     * @param r
     * @return
     */
    private float[] getRandomPoint(float baseX, float baseY, int r) {
        if (r <= 0) {
            r = 1;
        }
        float[] ints = new float[2];
        float x = mRandom.nextInt(r);
        float y = (float) Math.sqrt(r * r - x * x);

        x = baseX + getRandomPNValue(x);
        y = baseY + getRandomPNValue(y);
        ints[0] = x;
        ints[1] = y;
        return ints;
    }
}
