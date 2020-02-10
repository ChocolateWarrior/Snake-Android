package com.epam.snakeandroid.engines;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.RequiresApi;
import com.epam.snakeandroid.Exceptions.GameLostException;
import com.epam.snakeandroid.entities.*;
import com.epam.snakeandroid.services.GameService;
import com.epam.snakeandroid.services.SnakeService;
import com.epam.snakeandroid.services.SoulService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class SnakeEngine extends SurfaceView implements Runnable {

    private Thread thread = null;
    private SoundPool soundPool;
    private int crashSound = -1;
    private int eatSoulSound = -1;
    private Direction direction = Direction.RIGHT;

    private int screenX;
    private int screenY;
    private int numBlocksHigh;
    private final int NUM_BLOCKS_WIDE = 20;
    private int blockSize;

    private long nextFrameTime;
    private final long FPS = 10;
    private final long MILLIS_PER_SECOND = 1000;

    private volatile boolean isPlaying;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private Paint paint;

    private SnakeService snakeService;
    private SoulService soulService;
    private GameService gameService;

    private Snake snake;
    private Field field;
    private Random random;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SnakeEngine(Context context, Point size) {
        super(context);

        screenX = size.x;
        screenY = size.y;

        blockSize = screenX / NUM_BLOCKS_WIDE;
        numBlocksHigh = screenY / blockSize;
        field = new Field(NUM_BLOCKS_WIDE, numBlocksHigh);

        soundPool = new SoundPool.Builder().setMaxStreams(10).build();

//        try {
//            AssetManager assetManager = context.getAssets();
//            AssetFileDescriptor descriptor;
//
//            descriptor = assetManager.openFd("get_mouse_sound.ogg");
//            eatSoulSound = soundPool.load(descriptor, 0);
//
//            descriptor = assetManager.openFd("death_sound.ogg");
//            crashSound = soundPool.load(descriptor, 0);
//
//        } catch (IOException e) {
//            System.out.println("No sound available ");
//        }

        paint = new Paint();
        snake = new Snake();
        random = new Random();
        surfaceHolder = getHolder();

        soulService = new SoulService(random, field, snake);
        snakeService = new SnakeService(soulService, field, snake);
        gameService = new GameService(snakeService, soulService);

        startNewGame();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void run() {
        while (isPlaying) {
            if (updateRequired()) {
                update();
                draw();
            }
        }
    }

    public void pause() {
        isPlaying = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    private void startNewGame() {
        soulService.spawnNewSoul();
        nextFrameTime = System.currentTimeMillis();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void update() {
        try {
            gameService.update(direction);
        } catch (GameLostException gle) {
            restart();
        }
    }

    private void restart() {
        snake = new Snake();
        soulService = new SoulService(random, field, snake);
        snakeService = new SnakeService(soulService, field, snake);
        gameService = new GameService(snakeService, soulService);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            //BACKGROUND COLOR
            canvas.drawColor(Color.parseColor("#ffdd99"));

            drawSnake();
            drawSoul();
            drawText();

            // Unlock the canvas and reveal the graphics for this frame
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawText() {
        paint.setColor(Color.parseColor("#333333"));
        paint.setTextSize(100);
        paint.setFakeBoldText(true);
        canvas.drawText("Score:" + snakeService.getScore(), 80, 120, paint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawSoul() {
        paint.setColor(Color.parseColor("#76EA76"));
        Soul soul = field.getSoul();
        canvas.drawOval(soul.getPosition().getXCoordinate() * blockSize,
                (soul.getPosition().getYCoordinate() * blockSize),
                (soul.getPosition().getXCoordinate() * blockSize) + blockSize,
                (soul.getPosition().getYCoordinate() * blockSize) + blockSize,
                paint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawSnake() {

        paint.setColor(Color.parseColor("#ff2e63"));
        ArrayList snakeBody = new ArrayList(Arrays
                .asList(snakeService.getSnake().getSnakeBody().toArray()));

        for (int i = 0; i < snake.getSize(); i++) {
            Node current = (Node) snakeBody.get(i);
            if (snake.getHead().equals(current)) {
                canvas.drawOval(current.getXCoordinate() * blockSize,
                        current.getYCoordinate() * blockSize,
                        (current.getXCoordinate() * blockSize) + blockSize,
                        (current.getYCoordinate() * blockSize) + blockSize,
                        paint);
            } else {
                paint.setColor(Color.parseColor("#ffb6b9"));
                canvas.drawRoundRect(current.getXCoordinate() * blockSize,
                        current.getYCoordinate() * blockSize,
                        (current.getXCoordinate() * blockSize) + blockSize,
                        (current.getYCoordinate() * blockSize) + blockSize,
                        35, 35, paint);
            }
        }
    }


    private boolean updateRequired() {
        if (nextFrameTime <= System.currentTimeMillis()) {
            nextFrameTime = System.currentTimeMillis() + MILLIS_PER_SECOND / FPS;
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            if (motionEvent.getX() >= screenX / 2f) {
                switch (direction) {
                    case UP:
                        direction = Direction.RIGHT;
                        break;
                    case RIGHT:
                        direction = Direction.DOWN;
                        break;
                    case DOWN:
                        direction = Direction.LEFT;
                        break;
                    case LEFT:
                        direction = Direction.UP;
                        break;
                }
            } else {
                switch (direction) {
                    case UP:
                        direction = Direction.LEFT;
                        break;
                    case LEFT:
                        direction = Direction.DOWN;
                        break;
                    case DOWN:
                        direction = Direction.RIGHT;
                        break;
                    case RIGHT:
                        direction = Direction.UP;
                        break;
                }
            }
        }
        return true;
    }

}
