package com.example.jadynai.loadinglovely.pen.kotlin

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.jadynai.loadinglovely.pen.BasePen
import java.util.*

/**
 * @version:
 * @FileDescription:
 * @Author:jing
 * @Since:2018/5/18
 * @ChangeList:
 */

class SprayPen1(w: Int, h: Int) : BasePen(w, h) {

    private var mCricleR = 2f

    protected var mRandom = Random()

    private var mPenW = 40

    //喷漆密度，设定为半径为10的圈内的点数
    private val mDensity = 40

    private var mTotalNum: Int = 0

    init {
        val v = w / 504f
        mCricleR = if (v < 1) 1f else v
        setSprayData()
    }

    override fun generateSpecificPaint(): Paint {
        val paint = Paint()
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        //        paint.setStrokeCap(Paint.Cap.ROUND);//结束的笔画为圆心
        //        paint.setStrokeJoin(Paint.Join.ROUND);//连接处为圆角
        paint.isAntiAlias = true
        //        paint.setStrokeMiter(1.0f);//设置笔画倾斜度
        //模糊效果
        //        paint.setMaskFilter(new BlurMaskFilter(BLUR_SIZE, BlurMaskFilter.Blur.SOLID));
        return paint
    }

    override fun setPenWidth(width: Float) {
        super.setPenWidth(width)
        val w = (width * 0.5f).toInt()
        mPenW = if (w < 5) 5 else w
        setSprayData()
    }

    /**
     * 设置一些喷漆的属性
     */
    private fun setSprayData() {
        mTotalNum = mPenW / 10 * mDensity
    }

    override fun drawDetail(canvas: Canvas) {
        if (points.isEmpty()) {
            return
        }

        val x = curPoint.x
        val y = curPoint.y
        drawSpray(x, y)
    }

    private fun drawSpray(x: Float, y: Float) {
        for (i in 0 until mTotalNum) {
            val randomPoint = getRandomPoint(x, y, mPenW, false)
            mCanvas.drawCircle(randomPoint[0], randomPoint[1], mCricleR, mPaint)
        }
    }

    private fun getRandomPoint(baseX: Float, baseY: Float, r: Int): FloatArray {
        var r = r
        if (r <= 0) {
            r = 1
        }
        val ints = FloatArray(2)
        var x = mRandom.nextInt(r + 1).toFloat()
        var y = Math.sqrt(Math.pow(r.toDouble(), 2.0) - Math.pow(x.toDouble(), 2.0)).toFloat()
        //        y = mRandom.nextInt((int) y);

        x = baseX + getRandomPNValue(x)
        y = baseY + getRandomPNValue(y)
        ints[0] = x
        ints[1] = y
        return ints
    }

    private fun getRandomPNValue(value: Float): Float {
        return if (mRandom.nextBoolean()) value else 0 - value
    }

    /**
     * @param baseX
     * @param baseY
     * @param r
     * @param isUniform 是否让点均匀分布，否则自内向外由密至疏
     * @return
     */
    private fun getRandomPoint(baseX: Float, baseY: Float, r: Int, isUniform: Boolean): FloatArray {
        var r = r
        if (r <= 0) {
            r = 1
        }
        val ints = FloatArray(2)
        val degree = mRandom.nextInt(360)
        var curR: Double
        if (isUniform) {
            //均匀分布
            curR = Math.sqrt(mRandom.nextDouble()) * r
        } else {
            //自内向外扩散
            curR = mRandom.nextInt(r).toDouble()
        }
        curR = if (curR == 0.0) 1.0 else curR
        var x = (curR * Math.cos(Math.toRadians(degree.toDouble()))).toFloat()
        var y = (curR * Math.sin(Math.toRadians(degree.toDouble()))).toFloat()
        x = baseX + getRandomPNValue(x)
        y = baseY + getRandomPNValue(y)
        ints[0] = x
        ints[1] = y
        return ints
    }

    companion object {

        private val BLUR_SIZE = 5f
    }
}
