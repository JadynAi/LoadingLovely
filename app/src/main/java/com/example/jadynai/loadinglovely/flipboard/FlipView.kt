package com.example.jadynai.loadinglovely.flipboard

import android.content.Context
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.jadynai.loadinglovely.R


/**
 *@version:
 *@FileDescription:
 *@Author:jing
 *@Since:2018/6/9
 *@ChangeList:
 */
class FlipView(context: Context, attributes: AttributeSet) : View(context, attributes) {

    val girls = arrayListOf(R.drawable.girl_0, R.drawable.girl_1, R.drawable.girl_2,
            R.drawable.girl_3, R.drawable.girl_4, R.drawable.girl_5, R.drawable.girl_6)


    private var startX: Float? = 0f
    private var startY: Float? = 0f

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                startX = event?.x
                startY = event?.y
            }
            MotionEvent.ACTION_MOVE -> {
                val x = event?.x
                val y = event?.y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
            }
        }
        return true
    }

    private val camera by lazy {
        Camera()
    }

    private val drawMatrix by lazy {
        Matrix()
    }

    private val centerX by lazy {
        width / 2.toFloat()
    }

    private val centerY by lazy {
        height / 2.toFloat()
    }

    private val bitmap by lazy {
        BitmapUtils.compress(resources, girls.last(), width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawFirstHalf(canvas)
        drawSecondHalf(canvas)
    }

    /*
    * 绘制上半部分，以及上半部分的变化。
    * 上半部分角度由180 变化到 90，递减
    * */
    fun drawFirstHalf(canvas: Canvas?) {
        canvas?.save()
        camera.save()
        canvas?.clipRect(0, 0, width, height / 2)
        camera.rotateX(180f)
        camera.rotateY(180f)
        camera.getMatrix(drawMatrix)
        camera.restore()
        drawMatrix.preTranslate(-centerX, 0f)
        drawMatrix.postTranslate(centerX, centerY)
        //高度变矮
//        drawMatrix.preScale(1.0f, 0.6f)
        //这里也可以旋转canvas，性能上无差别
        drawMatrix.preRotate(180f, centerX, centerY / 2)
        //or旋转Canvas
//        canvas?.rotate(180f, centerX, centerY / 2)
        canvas?.drawBitmap(bitmap, drawMatrix, null)
        canvas?.restore()
    }

    /*
    * 绘制下半部分，以及下半部分的变化
    * 下半部分由0 变化到 90，递增
    * */
    fun drawSecondHalf(canvas: Canvas?) {
        canvas?.save()
        camera.save()
        canvas?.clipRect(0, height / 2, width, height)
        camera.rotateX(0f)
        camera.getMatrix(drawMatrix)
        camera.restore()
        drawMatrix.preTranslate(-centerX, 0f)
        drawMatrix.postTranslate(centerX, 0f)
        //高度变矮
//        drawMatrix.preScale(1.0f, 1 - (rotate / 90f))
        canvas?.drawBitmap(bitmap, drawMatrix, null)
        canvas?.restore()
    }
}