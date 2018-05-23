package com.example.jadynai.loadinglovely.pen.kotlin

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent

/**
 *@version:
 *@FileDescription:
 *@Author:jing
 *@Since:2018/5/23
 *@ChangeList:
 */
abstract class BasePen(w: Int, h: Int) {

    var points = ArrayList<Point>()
    var bitmap: Bitmap
    var canvas: Canvas
    var paint: Paint

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
        }
    }

    fun clearPoints() {
        points?.apply {
            clear()
        }
    }
}

class Point(val x: Float, val y: Float)