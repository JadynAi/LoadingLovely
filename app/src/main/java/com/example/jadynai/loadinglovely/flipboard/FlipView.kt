package com.example.jadynai.loadinglovely.flipboard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.util.Log
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

    private val UP_FLIP = -1
    private val DOWN_FLIP = 1


    private var startX: Float = 0f
    private var startY: Float = 0f

    //向下翻旋转角度
    private var rotateF = 180f
        get() = if (field < 0f) 0f else if (field > 180f) 180f else field
    //向上翻旋转角度
    private var rotateS = 0f
        get() = if (field < 0f) 0f else if (field > 180f) 180f else field

    //是否移动上半部分0为松手，1为向下翻，-1为向上翻
    private var statusFlip = 0

    //当前页
    private var curPage = 0
        get() {
            return if (field < 0) 0 else if (field > girls.lastIndex) girls.lastIndex else field
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

    //当前Bitmap
    private var curBitmap: Bitmap? = null
        get() = BitmapUtils.compress(resources, girls.get(curPage), width, height)

    //上一张Bitmap
    private var lastBitmap: Bitmap? = null
        get() {
            if (curPage == 0) {
                return null
            }
            return BitmapUtils.compress(resources, girls.get(curPage - 1), width, height)
        }

    //下一张Bitmap
    private var nextBitmap: Bitmap? = null
        get() {
            if (curPage == girls.lastIndex) {
                return null
            }
            return BitmapUtils.compress(resources, girls.get(curPage + 1), width, height)
        }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.apply {
            when (this.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    startX = this.x
                    startY = this.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val x = this.x
                    val y = this.y
                    //当y运动距离大于x的1.5倍时，才判断为垂直翻动
                    val disY = y - startY
                    if (Math.abs(disY) > 1f && Math.abs(disY) >= Math.abs(x - startX) * 1.5f) {
                        if (statusFlip == 0) {
                            //滑动间距为正并且不是第一页判断为向下翻，滑动间距为负并且不是最后一页判断为向上翻
                            statusFlip = if (disY > 0 && curPage != 0) DOWN_FLIP
                            else if (disY < 0 && curPage != girls.lastIndex) UP_FLIP else 0
                        }
                        val ratio = Math.abs(disY) / centerY
                        if (statusFlip == DOWN_FLIP) {
                            //向下翻并且当前页不等于0
                            rotateF = (1 - ratio) * 180f
                            Log.d("cece", ": rotateF : " + rotateF);
                            invalidate()
                        } else if (statusFlip == UP_FLIP) {
                            //向上翻，并且不是最后一页
                            if (curPage != girls.lastIndex) {
                                rotateS = ratio * 180f
                                Log.d("cece", ": rotateS : " + rotateS);
                                invalidate()
                            }
                        }
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    resetData(this)
                }
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun resetData(event: MotionEvent) {
        if (statusFlip != 0) {
            drawMatrix.reset()
            //抬手的时候，有动画发生
            if (Math.abs(event.y - startY) <= centerY / 2) {
                //滑动距离小于1/4屏幕高，判定仍停留在当前页
                rotateF = 180f
                rotateS = 0f
                statusFlip = 0
                invalidate()
            } else {
                //滑动距离超过临界值，判定为跳过当前页
                if (statusFlip == DOWN_FLIP) {
                    //下翻到上一页
                    for (i in rotateF.toInt() downTo 0 step 6) {
                        invalidate()
                    }
                    curPage--
                } else {
                    //上翻到下一页
                    for (i in rotateS.toInt() until 180 step 6) {
                        invalidate()
                    }
                    curPage++
                }
                rotateF = 180f
                rotateS = 0f
                statusFlip = 0
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //绘制当前页底下的一层,翻页进行中
        if (statusFlip == DOWN_FLIP) {
            //向下翻，滑到上一页
            drawFirstHalf(canvas, lastBitmap, 180f)
            drawFirstShadow(canvas, rotateF)
        } else if (statusFlip == UP_FLIP) {
            drawSecondHalf(canvas, nextBitmap, 0f)
            drawSecondShadow(canvas, rotateS)
        }

        //绘制当前页
        drawFirstHalf(canvas, curBitmap, rotateF)
        drawSecondHalf(canvas, curBitmap, rotateS)

        //绘制当前页之上的一层，翻页完成后
        if (statusFlip == DOWN_FLIP) {
            if (rotateF <= 90f) {
                drawSecondShadow(canvas, rotateF)
                drawSecondHalf(canvas, lastBitmap, rotateF)
            }
        } else if (statusFlip == UP_FLIP) {
            if (rotateS >= 90f) {
                drawFirstShadow(canvas, rotateS)
                drawFirstHalf(canvas, nextBitmap, rotateS)
            }
        }
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
//        drawMatrix.reset()
            camera.rotateX(if (rotate <= 90f) 90f else rotate)
            camera.rotateY(180f)
            camera.getMatrix(drawMatrix)
            camera.restore()
            drawMatrix.preTranslate(-centerX, 0f)
            drawMatrix.postTranslate(centerX, centerY)
            //高度变矮
            drawMatrix.preScale(1.0f, (rotate - 90f) / 90f)
            //这里也可以旋转canvas，性能上无差别
            drawMatrix.preRotate(180f, centerX, centerY / 2)
            //or旋转Canvas
//        canvas?.rotate(180f, centerX, centerY / 2)
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
            camera.rotateX(if (rotate >= 90f) 90f else rotate)
            camera.getMatrix(drawMatrix)
            camera.restore()
            drawMatrix.preTranslate(-centerX, 0f)
            drawMatrix.postTranslate(centerX, centerY)
            //高度变矮
            drawMatrix.preScale(1.0f, (90f - rotate) / 90f)
            canvas?.drawBitmap(BitmapUtils.cropSaveSecondHalf(this), drawMatrix, null)
            canvas?.restore()
        }
    }

    /*
    * 上半部分蒙层绘制
    * */
    fun drawFirstShadow(canvas: Canvas?, rotate: Float) {
        if (rotate >= 90f) {
            canvas?.apply {
                this.save()
                this.clipRect(0, 0, width, height / 2)
                this.drawARGB((153 * (rotate - 90f) / 90f).toInt(), 0, 0, 0)
                this.restore()
            }
        }
    }

    /*
    * 下半部分蒙层绘制
    * */
    fun drawSecondShadow(canvas: Canvas?, rotate: Float) {
        if (rotate <= 90f) {
            canvas?.apply {
                this.save()
                this.clipRect(0, height / 2, width, height)
                this.drawARGB((153 * (90f - rotate) / 90f).toInt(), 0, 0, 0)
                this.restore()
            }
        }
    }
}