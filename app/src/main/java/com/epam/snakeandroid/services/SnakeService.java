package com.epam.snakeandroid.services;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.SoundPool;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.epam.snakeandroid.entities.Direction;
import com.epam.snakeandroid.entities.Field;
import com.epam.snakeandroid.entities.Node;
import com.epam.snakeandroid.entities.Snake;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class SnakeService {

    private SoulService soulService;
    private SoundPool soundPool;
    private Field field;
    private Snake snake;
    private int score;
    private int eatSound;
    private int deathSound;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SnakeService(SoulService soulService,
                        SoundPool soundPool,
                        Context context,
                        Field field,
                        Snake snake) {
        this.soulService = soulService;
        this.soundPool = soundPool;
        this.field = field;
        this.snake = snake;

        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("sound/eat.ogg");
            eatSound = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sound/ah.ogg");
            deathSound = soundPool.load(descriptor, 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Snake getSnake() {
        return snake;
    }

    public int getScore() {
        return score;
    }

    void updateSnakeSituation(Direction direction) {
        updateSnakeBody(direction);
        eatSpriteIfPossible();
    }

    boolean hasLost() {
        ArrayDeque<Node> tail = new ArrayDeque<>(snake.getSnakeBody());
        tail.removeFirst();
        return tail.contains(snake.getHead());
    }

    void makeDeathSound() {
        makeSound(deathSound);
    }

    private void updateSnakeBody(Direction direction) {
        Deque<Node> body = snake.getSnakeBody();
        body.removeLast();
        body.addFirst(getHeadByDirection(direction));
        snake.setHead(getHeadByDirection(direction));
        snake.setSnakeBody(body);
    }

    private void eatSpriteIfPossible() {
        if (snake.getHead().equals(field.getSoul().getPosition())) {
            makeSound(eatSound);
            growSnake();
            score++;
            soulService.spawnNewSoul();
        }
    }

    private void makeSound(int sound) {
        soundPool.play(sound, 1, 1, 0, 0, 1);
    }

    private Node getHeadByDirection(Direction direction) {

        Node oldHead = snake.getHead();

        switch (direction) {
            case UP:
                return new Node(oldHead.getXCoordinate(),
                        (oldHead.getYCoordinate() - 1 + field.getSizeY()) % field.getSizeY(),
                        direction);
            case DOWN:
                return new Node(oldHead.getXCoordinate(),
                        (oldHead.getYCoordinate() + 1) % field.getSizeY(),
                        Direction.DOWN);
            case RIGHT:
                return new Node(
                        (oldHead.getXCoordinate() + 1) % field.getSizeX(),
                        oldHead.getYCoordinate(),
                        Direction.RIGHT);
            case LEFT:
                return new Node(
                        (oldHead.getXCoordinate() - 1 + field.getSizeX()) % field.getSizeX(),
                        oldHead.getYCoordinate(),
                        Direction.LEFT);
            default:
                return getHeadByDirection(snake.getHead().getDirection());
        }
    }

    private void growSnake() {

        Node tail = snake.getSnakeBody().getLast();
        snake.incrementSize();

        switch (tail.getDirection()) {
            case RIGHT:
                snake.getSnakeBody()
                        .addLast(new Node(
                                (tail.getXCoordinate() - 1 + field.getSizeX()) % field.getSizeX(),
                                tail.getYCoordinate(),
                                tail.getDirection()));
                break;
            case DOWN:
                snake.getSnakeBody()
                        .addLast(new Node(tail.getXCoordinate(),
                                (tail.getYCoordinate() - 1 + field.getSizeY()) % field.getSizeY(),
                                tail.getDirection()));
                break;
            case LEFT:
                snake.getSnakeBody()
                        .addLast(new Node(tail.getXCoordinate() + 1,
                                tail.getYCoordinate(),
                                tail.getDirection()));
                break;
            case UP:
                snake.getSnakeBody()
                        .addLast(new Node(tail.getXCoordinate(),
                                tail.getYCoordinate() + 1,
                                tail.getDirection()));
                break;
        }

    }
}
