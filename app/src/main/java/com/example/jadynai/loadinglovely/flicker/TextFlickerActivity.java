package com.example.jadynai.loadinglovely.flicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.jadynai.loadinglovely.R;

public class TextFlickerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_flicker);

        TextFlickerView flickerTv = findViewById(R.id.flicker_tv);
        flickerTv.start();
    }
}
