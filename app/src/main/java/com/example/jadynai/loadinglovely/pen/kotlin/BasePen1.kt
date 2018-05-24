package com.example.jadynai.loadinglovely.pen.kotlin

import android.graphics.*
import android.support.annotation.FloatRange
import android.util.Log
import android.view.MotionEvent
import java.util.*

/**
 * @version:
 * @FileDescription:
 * @Author:jing
 * @Since:2018/5/18
 * @ChangeList:
 */

abstract class BasePen1(w: Int, h: Int) {

    protected var TAG = javaClass.name

    protected var mPaint: Paint? = null
    protected var mCanvas: Canvas? = null
    private var mBitmap: Bitmap? = null

    private var mPoints: MutableList<com.example.jadynai.loadinglovely.pen.Point>? = null

    protected val curPoint: com.example.jadynai.loadinglovely.pen.Point
        get() = if (mPoints == null || mPoints!!.isEmpty()) {
            com.example.jadynai.loadinglovely.pen.Point(0f, 0f)
        } else mPoints!![mPoints!!.size - 1]

    protected val points: List<com.example.jadynai.loadinglovely.pen.Point>
        get() {
            val points = ArrayList<com.example.jadynai.loadinglovely.pen.Point>()
            if (mPoints == null) {
                return points
            }
            for (point in mPoints!!) {
                points.add(point.clone())
            }
            return points
        }

    init {
        init(w, h)
    }

    private fun init(w: Int, h: Int) {
        mPoints = ArrayList()
        //特定的笔刷样式有特定的Paint
        mPaint = generateSpecificPaint()
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap!!)
    }

    fun setPenWidth(@FloatRange(from = 1.0, to = 100.0) width: Float) {

    }

    protected abstract fun generateSpecificPaint(): Paint

    fun onTouchEvent(event1: MotionEvent) {
        Log.d(TAG, "onTouchEvent: " + event1.actionMasked)
        when (event1.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                clearPoints()
                handlePoints(event1)
            }
            MotionEvent.ACTION_MOVE -> handlePoints(event1)
            MotionEvent.ACTION_UP -> {
            }
        }
    }

    private fun handlePoints(event1: MotionEvent) {
        val x = event1.x
        val y = event1.y
        if (x > 0 && y > 0) {
            mPoints!!.add(com.example.jadynai.loadinglovely.pen.Point(x, y))
        }
    }

    fun onDraw(canvas: Canvas) {
        if (mPoints != null && !mPoints!!.isEmpty()) {
            canvas.drawBitmap(mBitmap!!, 0f, 0f, null)
            drawDetail(canvas)
        }
    }

    //由各个画笔实现,参数为所依赖的view的canvas
    protected abstract fun drawDetail(canvas: Canvas)

    private fun clearPoints() {
        if (mPoints == null) {
            return
        }
        mPoints!!.clear()
    }

    fun clearDraw() {
        if (mCanvas == null) {
            return
        }
        clearPoints()
        mCanvas!!.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR)
    }

    fun release() {
        if (mPoints != null) {
            mPoints!!.clear()
            mPoints = null
        }
        if (mBitmap != null) {
            mBitmap!!.recycle()
            mBitmap = null
        }
    }
}
