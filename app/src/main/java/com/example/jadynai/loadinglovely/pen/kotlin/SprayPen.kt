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

    private var penR: Int = 40

    private var density: Int = 40

    private var totalNum: Int = 0

    private var standardDis: Double = 0.0

    init {
        var v = w / 504f
        circleR = if (v < 1f) 1f else v
        standardDis = Math.hypot(w.toDouble(), h.toDouble()) / 48
        setSprayData()
    }

    override fun setPenW(width: Float) {
        var w: Int = (width * 0.5f).toInt()
        penR = if (w <= 5) 5 else w
        setSprayData()
    }

    private fun setSprayData() {
        totalNum = penR / 10 * density
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

        if (totalDis >= penR * getPoints().size && lastDis <= (penR / 2)) {
            return
        }

        val gapCircle = lastDis - penR * 2
        if (gapCircle >= standardDis) {
            val stepDis = penR * 1.6f
            val v = (lastDis / stepDis).toInt()
            val gapX = getPoints().last().mX - getPoints().get(getPoints().lastIndex - 1).mX
            val gapY = getPoints().last().mY - getPoints().get(getPoints().lastIndex - 1).mY
            for (i in 1..v) {
                val x = (getPoints().get(getPoints().lastIndex - 1).mX + gapX * i * stepDis / lastDis).toFloat()
                val y = (getPoints().get(getPoints().lastIndex - 1).mY + gapY * i * stepDis / lastDis).toFloat()
                drawSpray(x, y, (totalNum * calculate(i, x.toInt(), y.toInt())).toInt())
            }
        } else {
            drawSpray(curPoint.mX, curPoint.mY, totalNum)
        }
    }

    private fun drawSpray(x: Float, y: Float, totalNum: Int) {
        for (i in 0 until totalNum) {
            val randomPoint = getRandomPoint(x, y, penR, true)
            canvas.drawCircle(randomPoint[0], randomPoint[1], circleR, paint)
        }
    }

    private fun getRandomPoint(baseX: Float, baseY: Float, r: Int, isUniform: Boolean): FloatArray {
        var r = r
        if (r <= 0) {
            r = 1
        }
        val ints = FloatArray(2)
        val degree = random.nextInt(360)
        var curR: Double
        if (isUniform) {
            //均匀分布
            curR = Math.sqrt(random.nextDouble()) * r
        } else {
            //自内向外扩散
            curR = random.nextInt(r).toDouble()
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

    private fun getRandomPNValue(value: Float): Float {
        return if (random.nextBoolean()) value else 0 - value
    }

    companion object {

        private val BLUR_SIZE = 5f

        /**
         * 使用（x-（min+max）/2)^2/（min-（min+max）/2）^2作为粒子密度比函数
         */
        private fun calculate(index: Int, min: Int, max: Int): Float {
            val maxProbability = 0.6f
            val minProbability = 0.15f
            if (max - min + 1 <= 4) {
                return maxProbability
            }
            val mid = (max + min) / 2
            val maxValue = Math.pow((mid - min).toDouble(), 2.0).toInt()
            val ratio = (Math.pow((index - mid).toDouble(), 2.0) / maxValue).toFloat()
            return if (ratio >= maxProbability) {
                maxProbability
            } else if (ratio <= minProbability) {
                minProbability
            } else {
                ratio
            }
        }
    }
}