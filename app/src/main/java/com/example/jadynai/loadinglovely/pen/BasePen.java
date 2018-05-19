package com.example.jadynai.loadinglovely.pen;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @version:
 * @FileDescription:
 * @Author:jing
 * @Since:2018/5/18
 * @ChangeList:
 */

public abstract class BasePen {

    protected String TAG = getClass().getName();

    protected Random mRandom = new Random();

    protected Paint mPaint;
    protected Canvas mCanvas;
    private Bitmap mBitmap;

    private List<Point> mPoints;

    public BasePen(int w, int h) {
        init(w, h);
    }

    private void init(int w, int h) {
        mPoints = new ArrayList<>();
        //特定的笔刷样式有特定的Paint
        mPaint = generateSpecificPaint();
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    public void setPenWidth(@FloatRange(from = 1, to = 100) float width) {

    }

    @NonNull
    protected abstract Paint generateSpecificPaint();

    public void onTouchEvent(MotionEvent event1) {
        switch (event1.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                clearPoints();
                break;
            case MotionEvent.ACTION_MOVE:
                float mX = event1.getX();
                float mY = event1.getY();
                mPoints.add(new Point(mX, mY));
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                clearPoints();
                break;
        }
    }

    public void onDraw(Canvas canvas) {
        if (mPoints != null && !mPoints.isEmpty()) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
            drawDetail(canvas);
        }
    }

    private void clearPoints() {
        if (mPoints == null) {
            return;
        }
        mPoints.clear();
    }

    protected Point getCurPoint() {
        if (mPoints == null || mPoints.isEmpty()) {
            return new Point(0, 0);
        }

        return mPoints.get(mPoints.size() - 1);
    }

    //由各个画笔实现,参数为所依赖的view的canvas
    protected abstract void drawDetail(Canvas canvas);

    protected float getRandomPNValue(float value) {
        return mRandom.nextBoolean() ? value : 0 - value;
    }

    public void clearDraw() {
        if (mCanvas == null) {
            return;
        }
        mCanvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
    }
}
