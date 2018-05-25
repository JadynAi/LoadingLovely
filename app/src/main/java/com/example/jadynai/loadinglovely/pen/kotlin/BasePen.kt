package com.example.jadynai.loadinglovely.pen.kotlin

import android.graphics.*
import android.support.annotation.IntRange
import android.view.MotionEvent

/**
 *@version:
 *@FileDescription:
 *@Author:jing
 *@Since:2018/5/23
 *@ChangeList:
 */
abstract class BasePen(w: Int, h: Int) {

    private var points = ArrayList<Point>()
    private var bitmap: Bitmap
    protected var canvas: Canvas
    protected var paint: Paint

    protected val curPoint: Point
        get() = if (points.isEmpty()) Point(0f, 0f) else points.last()

    protected val lastDis: Double
        get() {
            if (points.isEmpty()) {
                return 0.0
            }
            return getIndexDis(points.lastIndex)
        }

    val totalDis: Double
        get() {
            if (points.size <= 1) {
                return 0.0
            }
            var total = 0.0
            for (i in 1 until points.size) {
                total += getIndexDis(i)
            }
            return total
        }

    init {
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
        paint = generateSpecificPaint()
    }

    open fun setPenW(width: Float){

    }

    abstract fun generateSpecificPaint(): Paint

    fun onTouchEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                clearPoints()
                handlePoints(event)
            }
            MotionEvent.ACTION_MOVE -> {
                handlePoints(event)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
            }
        }
    }

    fun handlePoints(event: MotionEvent) {
        var x = event.getX()
        var y = event.getY()
        if (x > 0 && y > 0) {
            points.add(Point(x, y))
        }
    }

    fun onDraw(canvas: Canvas) {
        if (points.isNotEmpty()) {
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            drawDetail(canvas)
        }
    }

    abstract fun drawDetail(canvas: Canvas)

    private fun clearPoints() {
        points?.apply {
            clear()
        }
    }

    protected fun getPoints(): List<Point> {
        return ArrayList(points)
    }

    protected fun getIndexDis(@IntRange(from = 1) index: Int): Double {
        if (points.size <= 1) {
            return 0.0
        }
        if (index < 1 || index >= points.size) {
            return 0.0
        }
        val indexP = points[index]
        val lastP = points[index - 1]
        return Math.hypot((indexP.mX - lastP.mX).toDouble(), (indexP.mY - lastP.mY).toDouble())
    }

    fun clearDraw() {
        clearPoints()
        canvas?.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR)
    }

    private fun release() {
        points.clear()
        bitmap.recycle()
    }
}

class Point(x: Float, y: Float) {
    val mX = x
        get
    val mY = y
        get
}