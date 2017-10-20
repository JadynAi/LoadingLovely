package com.example.jadynai.loadinglovely.animtext;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.jadynai.loadinglovely.R;

public class AnimTextActivity extends AppCompatActivity {

    private static final String TAG = "cece";
    private EditText mEditText;
    private CanvasView mCanvasView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anim_text);

        mEditText = (EditText) findViewById(R.id.editText3);
        mCanvasView = (CanvasView) findViewById(R.id.canvasView);

        findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCanvasView.setPath(getPath());
            }
        });

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCanvasView.start();
            }
        });
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCanvasView.stop();
            }
        });
    }

    private Path getPath() {
        Path textPath = new Path();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setTextSize(120);
        paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        String s = mEditText.getText().toString().length() <= 0 ? "cece" : mEditText.getText().toString();
        paint.getTextPath(s, 0, s.length(), 0, 200, textPath);
        textPath.close();
        return textPath;
    }
}
