package com.example.jadynai.loadinglovely.flipboard

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.example.jadynai.loadinglovely.R
import com.example.jadynai.loadinglovely.R.id.*

class FlipActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flip)
        val pre_tv = findViewById<TextView>(R.id.pre_tv)
        val debug_view = findViewById<DebugView>(R.id.debug_view)
        pre_tv.setOnClickListener {
            //缩放和skew搭配形成旋转效果
            debug_view.drawMatrix.postScale(0.707f, 0.707f)
            debug_view.drawMatrix.postSkew(-0.707f, 0.707f)
            debug_view.invalidate()
        }
        findViewById<View>(R.id.post_tv).setOnClickListener {
            debug_view.drawMatrix.reset()
            debug_view.drawMatrix.postRotate(45f)
            debug_view.invalidate()
        }
    }
}
