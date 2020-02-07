package com.epam.snakeandroid.engines;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.RequiresApi;
import com.epam.snakeandroid.entities.*;
import com.epam.snakeandroid.services.GameService;
import com.epam.snakeandroid.services.RenderService;
import com.epam.snakeandroid.services.SnakeService;
import com.epam.snakeandroid.services.SoulService;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SnakeEngine extends SurfaceView implements Runnable {

    private Thread thread;
    private Context context;
    private SoundPool soundPool;
    private int crashSound;
    private int eatSoulSound;
    private Direction direction = Direction.RIGHT;
    private int screenX;
    private int screenY;
    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 40;
    private int numBlocksHigh;


    // The size in pixels of a snake segment
    private int blockSize;

    // Control pausing between updates
    private long nextFrameTime;
    // Update the game 10 times per second
    private final long FPS = 10;
    // There are 1000 milliseconds in a second
    private final long MILLIS_PER_SECOND = 1000;
    // How many points does the player have
    private int score;

    private volatile boolean isPlaying;
    // A canvas for our paint
    private Canvas canvas;
    // Required to use canvas
    private SurfaceHolder surfaceHolder;
    // Some paint for our canvas
    private Paint paint;

    private RenderService renderService;
    private SnakeService snakeService;
    private SoulService soulService;
    private BufferedReader bufferedReader;

    private GameService gameService;

    private Snake snake;
    private Field field;
    private Soul soul;
    private Random random;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SnakeEngine(Context context, Point size) {
        super(context);
        screenX = size.x;
        screenY = size.y;

        blockSize = screenX / NUM_BLOCKS_WIDE;
        numBlocksHigh = screenY / blockSize;
        field = new Field(NUM_BLOCKS_WIDE);

        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool = new SoundPool.Builder().setMaxStreams(10).build();

        try {
            // Create objects of the 2 required classes
            // Use m_Context because this is a reference to the Activity
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Prepare the two sounds in memory
            descriptor = assetManager.openFd("get_mouse_sound.ogg");
            eatSoulSound = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("death_sound.ogg");
            crashSound = soundPool.load(descriptor, 0);

        } catch (IOException e) {
            // Error
        }

        surfaceHolder = getHolder();
        paint = new Paint();

        snake = new Snake();
        random = new Random();
        soulService = new SoulService(random, field, snake);
        snakeService = new SnakeService(soulService, field, snake);
        gameService = new GameService(snakeService, soulService);

        // Start the game
        startNewGame();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void run() {
        // Update 10 times a second
        if(updateRequired()) {
            update();
            draw();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void update() {
        System.out.println(gameService);
        gameService.update(direction);
    }

    public void pause() {
        isPlaying = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void startNewGame() {

        snake = new Snake();
        // Get Bob ready for dinner
        soulService.spawnNewSoul();
        // Reset the score
        score = 0;

        // Setup nextFrameTime so an update is triggered
        nextFrameTime = System.currentTimeMillis();
    }

    public void draw() {
        // Get a lock on the canvas
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            // Fill the screen with Game Code School blue
            canvas.drawColor(Color.argb(255, 26, 128, 182));

            // Set the color of the paint to draw the snake white
            paint.setColor(Color.argb(255, 255, 255, 255));

            // Scale the HUD text
            paint.setTextSize(90);
//            canvas.drawText("Score:" + score, 10, 70, paint);

            ArrayList snakeBody = (ArrayList) snake.getSnakeBody();
            // Draw the snake one block at a time
            for (int i = 0; i < snake.getSize(); i++) {

                canvas.drawRect(((Node)snakeBody.get(i)).getXCoordinate() * blockSize,
                        ((Node)snakeBody.get(i)).getYCoordinate() * blockSize,
                        (((Node)snakeBody.get(i)).getXCoordinate() * blockSize) + blockSize,
                        (((Node)snakeBody.get(i)).getYCoordinate() * blockSize) + blockSize,
                        paint);
            }

            // Set the color of the paint to draw Bob red
            paint.setColor(Color.argb(255, 255, 0, 0));

            Soul soul = field.getSoul();
            canvas.drawRect(soul.getPosition().getXCoordinate() * blockSize,
                    (soul.getPosition().getYCoordinate() * blockSize),
                    (soul.getPosition().getXCoordinate() * blockSize) + blockSize,
                    (soul.getPosition().getYCoordinate() * blockSize) + blockSize,
                    paint);

            // Unlock the canvas and reveal the graphics for this frame
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }


    public boolean updateRequired() {

        // Are we due to update the frame
        if(nextFrameTime <= System.currentTimeMillis()){
            // Tenth of a second has passed

            // Setup when the next update will be triggered
            nextFrameTime =System.currentTimeMillis() + MILLIS_PER_SECOND / FPS;

            // Return true so that the update and draw
            // functions are executed
            return true;
        }

        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {

        if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            if (motionEvent.getX() >= screenX / 2) {
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
