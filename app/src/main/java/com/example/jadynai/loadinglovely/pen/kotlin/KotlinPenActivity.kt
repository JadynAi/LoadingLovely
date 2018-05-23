package com.example.jadynai.loadinglovely.pen.kotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.jadynai.loadinglovely.R
import kotlinx.android.synthetic.main.activity_pen.*

/**
 *@version:
 *@FileDescription:
 *@Author:jing
 *@Since:2018/5/23
 *@ChangeList:
 */
class KotlinPenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pen)
        clear_tv.setOnClickListener{
            pen_view.clearDraw()
        }
    }
}