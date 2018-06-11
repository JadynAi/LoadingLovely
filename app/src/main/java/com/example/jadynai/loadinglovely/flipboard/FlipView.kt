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

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }


    private var startX: Float = 0f
    private var startY: Float = 0f

    //向下翻旋转角度
    private var rotateF = 180f
    //向上翻旋转角度
    private var rotateS = 0f

    //是否移动上半部分0为松手，1为向下翻，-1为向上翻
    private var statusFlip = 0

    private val TAG = "cece"

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                startX = event?.x
                startY = event?.y
            }
            MotionEvent.ACTION_MOVE -> {
                val x = event?.x
                val y = event?.y
                //当y运动距离大于x的1.5倍时，才判断为垂直翻动
                val disY = y - startY
                if (Math.abs(disY) > 1f && Math.abs(disY) >= Math.abs(x - startX) * 1.5f) {
                    if (statusFlip == 0) {
                        statusFlip = if (disY > 0) 1 else -1
                    }
                    val ratio = Math.abs(disY) / centerY
                    if (statusFlip == 1) {
                        //向下翻
                        rotateF = (1 - ratio) * 180f
                    } else {
                        rotateS = ratio * 180f
                    }
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                resetData(event)
            }
        }
        return true
    }

    private fun resetData(event: MotionEvent?) {
        rotateF =180f
        rotateS = 0f
        statusFlip = 0
        invalidate()
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
        canvas?.clipRect(0, 0, width, height / 2)
        camera.save()
//        drawMatrix.reset()
        camera.rotateX(rotateF)
        camera.rotateY(180f)
        camera.getMatrix(drawMatrix)
        camera.restore()
        drawMatrix.preTranslate(-centerX, 0f)
        drawMatrix.postTranslate(centerX, centerY)
        //高度变矮
        drawMatrix.preScale(1.0f, rotateF / 180f)
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
        camera.rotateX(rotateS)
        camera.getMatrix(drawMatrix)
        camera.restore()
        drawMatrix.preTranslate(-centerX, 0f)
        drawMatrix.postTranslate(centerX, centerY)
        //高度变矮
        drawMatrix.preScale(1.0f, (90f - rotateS) / 90f)
        canvas?.drawBitmap(BitmapUtils.cropSaveSecondHalf(bitmap), drawMatrix, null)
        canvas?.restore()
    }
}