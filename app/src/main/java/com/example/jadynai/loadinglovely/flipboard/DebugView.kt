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

    val girls = arrayListOf(R.drawable.girl_0)


    private val camera by lazy {
        Camera()
    }

    val drawMatrix by lazy {
        Matrix()
    }

    val centerX by lazy {
        width / 2.toFloat()
    }

    val centerY by lazy {
        height / 2.toFloat()
    }

    //当前Bitmap
    private var curBitmap: Bitmap? = null
        get() = BitmapUtils.compress(resources, girls.get(0), width, height)


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //绘制当前页
        drawFirstHalf(canvas, curBitmap)
    }

    /*
    * 绘制上半部分，以及上半部分的变化。
    * 上半部分角度由180 变化到 90，递减
    * */
    fun drawFirstHalf(canvas: Canvas?, bitmap: Bitmap?) {
        bitmap?.apply {
            canvas?.save()
            canvas?.drawBitmap(this, drawMatrix, null)
            canvas?.restore()
        }
    }
}