package com.milter.www.customviewgroupforblog;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private  StaggerLayout staggerLayout ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        staggerLayout = (StaggerLayout) findViewById(R.id.staggerlayout);

        String tag;

        for(int i = 0; i < 500 ; i++){
            tag = "我是标签：0"+i;
            TextView textView = new TextView(this);
            int color = i % 2 == 0 ? Color.CYAN : Color.YELLOW ;
            Random random = new Random();
            int fontSize = random.nextInt(30) +10;
            textView.setTextSize( fontSize );
            textView.setBackgroundColor(color);
            textView.setText(tag);
            staggerLayout.addView(textView);

        }
    }
}
