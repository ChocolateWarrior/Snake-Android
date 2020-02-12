package com.epam.snakeandroid.engines;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.*;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SnakeEngine extends SurfaceView implements Runnable {

    private static final String IMG_PUFFERFISH_PNG = "img/Pufferfish.png";
    private static final String IMG_CARROT_PNG = "img/IconCarrot.png";
    private static final String IMG_POWDER_PNG = "img/Powder.png";

    private Thread thread = null;
    private InputStream inputStream;
    private SoundPool soundPool;
    private Direction direction = Direction.RIGHT;

    private final int NUM_BLOCKS_WIDE = 20;
    private int screenX;
    private int screenY;
    private int numBlocksHigh;
    private int blockSize;

    private final long FPS = 10;
    private final long MILLIS_PER_SECOND = 1000;
    private long nextFrameTime;

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
        init(size);
        startNewGame();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init(Point size) {
        screenX = size.x;
        screenY = size.y;

        blockSize = screenX / NUM_BLOCKS_WIDE;
        numBlocksHigh = screenY / blockSize;
        field = new Field(NUM_BLOCKS_WIDE, numBlocksHigh);

        soundPool = new SoundPool.Builder().setMaxStreams(10).build();
        paint = new Paint();
        snake = new Snake();
        random = new Random();
        surfaceHolder = getHolder();

        soulService = new SoulService(random, field, snake);
        snakeService = new SnakeService(soulService, soundPool, getContext(), field, snake);
        gameService = new GameService(snakeService, soulService);
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void restart() {
        snake = new Snake();
        soulService = new SoulService(random, field, snake);
        snakeService = new SnakeService(soulService, soundPool, getContext(), field, snake);
        gameService = new GameService(snakeService, soulService);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.parseColor("#ffdd99"));

            drawSnake();
            drawSoul();
            drawText();

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
        Bitmap carrotBitmap = getBitmapByFileName(IMG_CARROT_PNG);

        Soul soul = field.getSoul();
        Rect soulRect = new Rect(soul.getPosition().getXCoordinate() * blockSize,
                (soul.getPosition().getYCoordinate() * blockSize),
                (soul.getPosition().getXCoordinate() * blockSize) + blockSize,
                (soul.getPosition().getYCoordinate() * blockSize) + blockSize);

        canvas.drawBitmap(carrotBitmap, null, soulRect, null);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawSnake() {

        ArrayList snakeBody = new ArrayList<>(Arrays
                .asList(snakeService.getSnake().getSnakeBody().toArray()));

        Bitmap snakeHeadBitmap = getBitmapByFileName(IMG_PUFFERFISH_PNG);
        Bitmap snakeBodyBitmap = getBitmapByFileName(IMG_POWDER_PNG);

        for (int i = 0; i < snake.getSize(); i++) {
            Node current = (Node) snakeBody.get(i);
            Rect currentRect = new Rect(current.getXCoordinate() * blockSize,
                    current.getYCoordinate() * blockSize,
                    (current.getXCoordinate() * blockSize) + blockSize,
                    (current.getYCoordinate() * blockSize) + blockSize);
            if (snake.getHead().equals(current)) {
                canvas.drawBitmap(snakeHeadBitmap, null, currentRect, null);
            } else {
                canvas.drawBitmap(snakeBodyBitmap, null, currentRect, null);
            }
        }
    }

    private Bitmap getBitmapByFileName(String filename) {

        Bitmap snakeHeadBitmap = null;
        AssetManager assetManager = getContext().getAssets();
        try {
            inputStream = assetManager.open(filename);
            snakeHeadBitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return snakeHeadBitmap;
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

        if (isActionUp(motionEvent)) {
            if (isRightOrLeftScreenSide(motionEvent)) {
                changeDirection(false);
                performClick();
            } else {
                changeDirection(true);
                performClick();
            }
        }
        return true;
    }

    private boolean isRightOrLeftScreenSide(MotionEvent motionEvent) {
        return motionEvent.getX() >= screenX / 2f;
    }

    private boolean isActionUp(MotionEvent motionEvent) {
        return (motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP;
    }

    private void changeDirection(boolean isPrev) {
        List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.values()));

        direction = isPrev ? directions
                .get((directions.indexOf(direction) + 1) % directions.size())
                : directions
                .get((directions.indexOf(direction) - 1 + directions.size()) % directions.size());
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
