package com.example.jadynai.loadinglovely.flipboard

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.jadynai.loadinglovely.R
import kotlinx.android.synthetic.main.activity_flip.*

class FlipActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flip)
        pre_tv.setOnClickListener {
            debug_view.drawMatrix.postRotate(45f)
            debug_view.drawMatrix.preTranslate(-debug_view.centerX, -debug_view.centerY)
            debug_view.drawMatrix.postTranslate(debug_view.centerX, debug_view.centerY)
            debug_view.invalidate()
        }
        post_tv.setOnClickListener {
            debug_view.drawMatrix.reset()
            debug_view.drawMatrix.postRotate(45f)
            debug_view.invalidate()
        }
    }
}
