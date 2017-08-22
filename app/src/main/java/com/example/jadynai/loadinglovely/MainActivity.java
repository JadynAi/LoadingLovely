package com.example.jadynai.loadinglovely;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private LeafAnimView mLeafView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLeafView = (LeafAnimView) findViewById(R.id.leadfView);
        
        findViewById(R.id.start_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLeafView.start();
            }
        });
        
        findViewById(R.id.end_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLeafView.pause();
            }
        });
    }
    
}
