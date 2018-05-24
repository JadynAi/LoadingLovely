package com.example.jadynai.loadinglovely.pen.kotlin

import android.graphics.*
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
    private var canvas: Canvas
    private var paint: Paint

    init {
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
        paint = generateSpecificPaint()
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

    protected fun getCurPoint(): Point {
        return if (points.isEmpty()) Point(0f, 0f) else points.last()
    }

    protected fun getPoints(): List<Point> {
        return ArrayList(points)
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