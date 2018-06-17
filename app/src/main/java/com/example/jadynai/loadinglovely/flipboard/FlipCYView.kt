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
class FlipCYView(context: Context, attributes: AttributeSet) : View(context, attributes) {

    val girls = arrayListOf(R.drawable.girl_0, R.drawable.girl_1, R.drawable.girl_2,
            R.drawable.girl_3, R.drawable.girl_4, R.drawable.girl_5, R.drawable.girl_6
            , R.drawable.girl_7, R.drawable.girl_8)

    private val UP_FLIP = -1
    private val DOWN_FLIP = 1


    private var startX: Float = 0f
    private var startY: Float = 0f

    //向下翻旋转角度,0~-180f
    private var rotateF = 0f
        get() = if (field < -180f) -180f else if (field > 0f) 0f else field

    //向上翻旋转角度,0~180f
    private var rotateS = 0f
        get() = if (field < 0f) 0f else if (field > 180f) 180f else field

    //翻动状态0为松手，1为向下翻，-1为向上翻
    private var statusFlip = 0

    //当前页
    private var curPage = 0
        get() {
            return if (field < 0) 0 else if (field > girls.lastIndex) girls.lastIndex else field
        }

    private val camera by lazy {
        val camera1 = Camera()
        //将摄像头拉远，翻页效果更加明显
        camera1.setLocation(0f, 0f, -20f)
        camera1
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
                            rotateF = ratio * -180f
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
            //放手的时候，有动画发生
            if (Math.abs(event.y - startY) <= centerY / 2) {
                //滑动距离小于1/4屏幕高，判定仍停留在当前页
                rotateF = 0f
                rotateS = 0f
                statusFlip = 0
                invalidate()
            } else {
                //滑动距离超过临界值，判定为跳过当前页
                if (statusFlip == DOWN_FLIP) {
                    //自动执行完下翻到上一页的动作
                    for (i in rotateF.toInt() downTo -180 step 6) {
                        invalidate()
                    }
                    curPage--
                } else {
                    //自动执行完上翻到下一页的动作
                    for (i in rotateS.toInt() until 180 step 6) {
                        invalidate()
                    }
                    curPage++
                }
                rotateF = 0f
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
            drawFirstHalf(canvas, lastBitmap, 0f)
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
            if (rotateF <= -90f) {
                //先绘制阴影
                drawSecondShadow(canvas, rotateF + 180f)
                drawSecondHalf(canvas, lastBitmap, rotateF + 180f)
            }
            //绘制覆盖在翻页Bitmap之上淡淡透明层，透明度固定
            drawFirstColor(canvas, 20)
        } else if (statusFlip == UP_FLIP) {
            if (rotateS >= 90f) {
                drawFirstShadow(canvas, rotateS - 180f)
                drawFirstHalf(canvas, nextBitmap, rotateS - 180f)
            }
            drawSecondColor(canvas, 20)
        }
    }

    /*
    * 绘制上半部分，以及上半部分的变化。
    * 上半部分角度由0  变化到 -90，递减
    * */
    fun drawFirstHalf(canvas: Canvas?, bitmap: Bitmap?, rotate: Float) {
        bitmap?.apply {
            canvas?.save()
            canvas?.clipRect(0, 0, width, height / 2)
            camera.save()
            //camera绕着X轴旋转，角度变化小于-90度，不再处理
            camera.rotateX(if (rotate <= -90f) -90f else rotate)
            camera.getMatrix(drawMatrix)
            camera.restore()
            //随着旋转角度变化的缩放值，只缩放Y轴
            drawMatrix.preScale(1.0f, (rotate + 90f) / 90f)
            drawMatrix.preTranslate(-centerX, -centerY)
            drawMatrix.postTranslate(centerX, centerY)
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
            canvas?.clipRect(0, height / 2, width, height)
            camera.rotateX(if (rotate >= 90f) 90f else rotate)
            camera.getMatrix(drawMatrix)
            camera.restore()
            drawMatrix.preScale(1.0f, (90f - rotate) / 90f)
            drawMatrix.preTranslate(-centerX, -centerY)
            drawMatrix.postTranslate(centerX, centerY)
            canvas?.drawBitmap(this, drawMatrix, null)
            canvas?.restore()
        }
    }

    /*
    * 上半部分阴影绘制
    * */
    fun drawFirstShadow(canvas: Canvas?, rotate: Float) {
        if (rotate >= -90f) {
            //阴影随着旋转角度实时改变透明度
            drawFirstColor(canvas, (153 * (90f - Math.abs(rotate)) / 90f).toInt())
        }
    }

    /*
    * 下半部分阴影绘制
    * */
    fun drawSecondShadow(canvas: Canvas?, rotate: Float) {
        if (rotate <= 90f) {
            drawSecondColor(canvas, (153 * (90f - Math.abs(rotate)) / 90f).toInt())
        }
    }

    /*
    * 覆盖在正在翻页的Bitmap之上，固定透明度
    * */
    private fun drawFirstColor(canvas: Canvas?, alpha: Int) {
        canvas?.apply {
            this.save()
            this.clipRect(0, 0, width, height / 2)
            this.drawARGB(alpha, 0, 0, 0)
            this.restore()
        }
    }

    private fun drawSecondColor(canvas: Canvas?, alpha: Int) {
        canvas?.apply {
            this.save()
            this.clipRect(0, height / 2, width, height)
            this.drawARGB(alpha, 0, 0, 0)
            this.restore()
        }
    }
}