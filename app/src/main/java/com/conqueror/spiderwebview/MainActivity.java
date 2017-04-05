package com.conqueror.spiderwebview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Radar radar = new Radar(this);
//        radar.setMainPaintCoor(Color.RED);
//        setContentView(radar);
        setContentView(R.layout.activity_main);
    }
}
