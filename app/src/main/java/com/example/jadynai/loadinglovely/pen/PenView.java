package com.example.jadynai.loadinglovely.pen;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @version:
 * @FileDescription:
 * @Author:jing
 * @Since:2018/5/18
 * @ChangeList:
 */

public class PenView extends View {

    private BasePen mBasePen;

    public PenView(Context context) {
        super(context);
        init(context);
    }

    public PenView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PenView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != 0 && h != 0) {
            if (mBasePen == null) {
                mBasePen = new SprayPen(w, h);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        MotionEvent event1 = MotionEvent.obtain(event);
        mBasePen.onTouchEvent(event1);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBasePen.onDraw(canvas);
    }

    public void clearDraw() {
        mBasePen.clearDraw();
        invalidate();
    }
}
