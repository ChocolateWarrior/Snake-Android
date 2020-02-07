package com.epam.snakeandroid;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import androidx.annotation.RequiresApi;
import com.epam.snakeandroid.controllers.GameController;
import com.epam.snakeandroid.engines.SnakeEngine;
import com.epam.snakeandroid.entities.Snake;
import com.epam.snakeandroid.entities.Field;
import com.epam.snakeandroid.services.GameService;
import com.epam.snakeandroid.services.RenderService;
import com.epam.snakeandroid.services.SnakeService;
import com.epam.snakeandroid.services.SoulService;
import com.epam.snakeandroid.views.View;

import java.io.IOException;
import java.util.Random;

public class SnakeActivity extends Activity {

    private SnakeEngine snakeEngine;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        android.view.Display display = getWindowManager().getDefaultDisplay();

        Point fieldSize = new Point();
        display.getSize(fieldSize);

        snakeEngine = new SnakeEngine(this, fieldSize);
        setContentView(snakeEngine);
//        setContentView(R.layout.activity_main);
    }

    // Start the thread in snakeEngine
    @Override
    protected void onResume() {
        super.onResume();
        snakeEngine.resume();
    }

    // Stop the thread in snakeEngine
    @Override
    protected void onPause() {
        super.onPause();
        snakeEngine.pause();
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
//    public static void main(String[] args) throws IOException {
//
//        Random random = new Random();
//        Snake snake = new Snake();
//        Field field = new Field(6);
//        View view = new View();
//
//        SoulService soulService = new SoulService(random,
//                field,
//                snake);
//        SnakeService snakeService = new SnakeService(soulService,
//                field,
//                snake);
//        RenderService renderService = new RenderService(field,
//                snake,
//                view);
//        GameService gameService = new GameService(view,
//                renderService,
//                snakeService,
//                soulService);
//
//        GameController gameController = new GameController(gameService);
//        gameController.play();
//
//    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return snakeEngine.onTouchEvent(motionEvent);
    }
}
