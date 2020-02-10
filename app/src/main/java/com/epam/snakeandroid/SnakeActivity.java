package com.epam.snakeandroid;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import androidx.annotation.RequiresApi;
import com.epam.snakeandroid.engines.SnakeEngine;

public class SnakeActivity extends Activity {

    private SnakeEngine snakeEngine;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point fieldSize = new Point();
        display.getSize(fieldSize);

        snakeEngine = new SnakeEngine(this, fieldSize);
        setContentView(snakeEngine);
    }

    @Override
    protected void onResume() {
        super.onResume();
        snakeEngine.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        snakeEngine.pause();
    }

}
