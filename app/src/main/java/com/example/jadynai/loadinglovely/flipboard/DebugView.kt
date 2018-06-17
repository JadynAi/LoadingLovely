package com.example.jadynai.loadinglovely.flipboard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.View
import com.example.jadynai.loadinglovely.R


/**
 *@version:
 *@FileDescription:
 *@Author:jing
 *@Since:2018/6/9
 *@ChangeList:
 */
class DebugView(context: Context, attributes: AttributeSet) : View(context, attributes) {

    val girls = arrayListOf(R.drawable.girl_0, R.drawable.girl_1, R.drawable.girl_2,
            R.drawable.girl_3, R.drawable.girl_4, R.drawable.girl_5, R.drawable.girl_6)


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

    //当前Bitmap
    private var curBitmap: Bitmap? = null
        get() = BitmapUtils.compress(resources, girls.get(0), width, height)


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //绘制当前页
        drawFirstHalf(canvas, curBitmap, 0f)
        drawSecondHalf(canvas, curBitmap, 0f)

    }

    /*
    * 绘制上半部分，以及上半部分的变化。
    * 上半部分角度由180 变化到 90，递减
    * */
    fun drawFirstHalf(canvas: Canvas?, bitmap: Bitmap?, rotate: Float) {
        bitmap?.apply {
            canvas?.save()
            canvas?.clipRect(0, 0, width, height / 2)
            camera.save()
            camera.rotateX(0f)
            camera.getMatrix(drawMatrix)
            camera.restore()
            drawMatrix.preScale(1.0f, 1.0f)
            drawMatrix.preTranslate(-centerX, -centerY)
            drawMatrix.postTranslate(centerX, centerY)
            //高度变矮
            canvas?.drawBitmap(this, drawMatrix, null)
            canvas?.restore()
        }
    }

    /*
    * 绘制下半部分，以及下半部分的变化
    * 下半部分由0 变化到 90，递增
    * */
    fun drawSecondHalf(canvas: Canvas?, bitmap: Bitmap?, rotate: Float) {
        bitmap?.apply {
            canvas?.save()
            camera.save()
            canvas?.clipRect(0, centerY.toInt(), width, height)
            camera.rotateX(0f)
            camera.getMatrix(drawMatrix)
            camera.restore()
            drawMatrix.preScale(1.0f, 1f)
            drawMatrix.preTranslate(-centerX, -centerY)
            drawMatrix.postTranslate(centerX, centerY)
            //高度变矮

            canvas?.drawBitmap(this, drawMatrix, null)
            canvas?.restore()
        }
    }
}