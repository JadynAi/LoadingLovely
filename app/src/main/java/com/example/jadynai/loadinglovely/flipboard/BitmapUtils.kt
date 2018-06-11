package com.example.jadynai.loadinglovely.flipboard

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.annotation.DrawableRes

/**
 *@version:
 *@FileDescription:
 *@Author:jing
 *@Since:2018/6/9
 *@ChangeList:
 */
class BitmapUtils {

    companion object {

        fun compress(resources: Resources, @DrawableRes drawableRes: Int, w: Int, h: Int): Bitmap {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeResource(resources, drawableRes, options)

            val outWidth = options.outWidth
            val outHeight = options.outHeight
            var sampleSize = 1

            if (outWidth > w || outHeight > h) {
                while (outWidth / sampleSize > w || outHeight / sampleSize > h) {
                    sampleSize *= 2
                }
            }
            options.inSampleSize = sampleSize
            options.inJustDecodeBounds = false

            val bitmap = BitmapFactory.decodeResource(resources, drawableRes, options)
            return Bitmap.createScaledBitmap(bitmap, w, h, false)
        }

        fun cropSaveFirstHalf(bitmap: Bitmap?): Bitmap? {
            bitmap?.apply {
                return Bitmap.createBitmap(this, 0, 0, width, height / 2)
            }
            return null
        }

        fun cropSaveSecondHalf(bitmap: Bitmap?): Bitmap? {
            bitmap?.apply {
                return Bitmap.createBitmap(this, 0, height / 2, width, height / 2)
            }
            return null
        }
    }
}