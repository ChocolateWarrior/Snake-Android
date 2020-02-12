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
import com.epam.snakeandroid.services.CarrotService;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SnakeEngine extends SurfaceView implements Runnable {

    private static final String IMG_PUFFER_FISH_PNG = "img/PufferFish.png";
    private static final String IMG_CARROT_PNG = "img/IconCarrot.png";
    private static final String IMG_POWDER_PNG = "img/Powder.png";
    private static final String SCORE = "Score:";
    private static final String COLOR_TEXT = "#333333";
    private static final String COLOR_BACKGROUND = "#ffdd99";

    private Thread thread = null;
    private InputStream inputStream;
    private SoundPool soundPool;
    private Direction direction = Direction.RIGHT;

    private final long FPS = 10;
    private final long MILLIS_PER_SECOND = 1000;
    private long nextFrameTime;

    private final int NUM_BLOCKS_WIDE = 14;
    private int screenX;
    private int screenY;
    private int numBlocksHigh;
    private int blockSize;

    private volatile boolean isPlaying;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private Paint paint;

    private SnakeService snakeService;
    private CarrotService carrotService;
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

    @Override
    public boolean performClick() {
        return super.performClick();
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

        carrotService = new CarrotService(random, field, snake);
        snakeService = new SnakeService(carrotService, soundPool, getContext(), field, snake);
        gameService = new GameService(snakeService, carrotService);
    }

    private void startNewGame() {
        carrotService.spawnNewCarrot();
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
        carrotService = new CarrotService(random, field, snake);
        snakeService = new SnakeService(carrotService, soundPool, getContext(), field, snake);
        gameService = new GameService(snakeService, carrotService);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.parseColor(COLOR_BACKGROUND));

            drawSnake();
            drawCarrot();
            drawText();

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawText() {
        paint.setColor(Color.parseColor(COLOR_TEXT));
        paint.setTextSize(100);
        paint.setFakeBoldText(true);
        canvas.drawText(SCORE + snakeService.getScore(), 80, 120, paint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawCarrot() {
        Carrot carrot = field.getCarrot();
        Rect carrotRect = getRect(carrot.getPosition());
        Bitmap carrotBitmap = getBitmapByFileName(IMG_CARROT_PNG);

        canvas.drawBitmap(carrotBitmap, null, carrotRect, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawSnake() {
        ArrayList snakeBody = new ArrayList<>(Arrays
                .asList(snakeService.getSnake().getSnakeBody().toArray()));

        Bitmap snakeHeadBitmap = getBitmapByFileName(IMG_PUFFER_FISH_PNG);
        Bitmap snakeBodyBitmap = getBitmapByFileName(IMG_POWDER_PNG);

        for (int i = 0; i < snake.getSize(); i++) {
            Node current = (Node) snakeBody.get(i);
            Rect currentRect = getRect(current);
            if (isHead(current)) {
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

    private Rect getRect(Node position) {
        return new Rect(position.getXCoordinate() * blockSize,
                (position.getYCoordinate() * blockSize),
                (position.getXCoordinate() * blockSize) + blockSize,
                (position.getYCoordinate() * blockSize) + blockSize);
    }

    private boolean isHead(Node current) {
        return snake.getHead().equals(current);
    }

    private boolean updateRequired() {
        if (nextFrameTime <= System.currentTimeMillis()) {
            nextFrameTime = System.currentTimeMillis() + MILLIS_PER_SECOND / FPS;
            return true;
        }
        return false;
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
}
