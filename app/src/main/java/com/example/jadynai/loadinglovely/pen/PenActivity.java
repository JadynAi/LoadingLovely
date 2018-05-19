package com.example.jadynai.loadinglovely.pen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.jadynai.loadinglovely.R;

public class PenActivity extends AppCompatActivity {

    private PenView mPenView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pen);

        mPenView = findViewById(R.id.pen_view);

        findViewById(R.id.clear_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPenView.clearDraw();
            }
        });
    }
}
