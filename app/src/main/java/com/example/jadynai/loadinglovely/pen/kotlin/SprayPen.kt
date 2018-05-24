package com.example.jadynai.loadinglovely.pen.kotlin

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import java.util.*

/**
 *@version:
 *@FileDescription:
 *@Author:jing
 *@Since:2018/5/24
 *@ChangeList:
 */
class SprayPen(w: Int, h: Int) : BasePen(w, h) {

    private var circleR: Float = 2f

    private var random: Random = Random()

    private var penW: Int = 40

    private var density: Int = 40

    private var totalNum: Int = 0

    init {
        var v = w / 504f
        circleR = if (v < 1f) 1f else v
        setSprayData()
    }

    fun setPenW(width: Float) {
        var w: Int = (width * 0.5f).toInt()
        penW = if (w <= 5) 5 else w
        setSprayData()
    }

    private fun setSprayData() {
        totalNum = penW / 10 * density
    }

    override fun generateSpecificPaint(): Paint {
        var paint = Paint()
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        return paint
    }

    override fun drawDetail(canvas: Canvas) {
        if (getPoints().isEmpty()) {
            return
        }
        val x = getCurPoint().mX
        val y = getCurPoint().mY
    }

    private fun drawSpray(x: Float, y: Float) {

    }
}