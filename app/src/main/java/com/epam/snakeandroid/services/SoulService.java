package com.epam.snakeandroid.services;

import com.epam.snakeandroid.entities.*;
import java.util.Random;

public class SoulService {

    private Snake snake;
    private Random random;
    private Field field;

    public SoulService(Random random,
                       Field field,
                       Snake snake) {
        this.random = random;
        this.field = field;
        this.snake = snake;
    }

    public void spawnNewSoul() {
        Soul soul = randomizeSoul();
        while (snakeContainsSoulNode(soul)) {
            soul = randomizeSoul();
        }
        field.setSoul(soul);
    }

    private boolean snakeContainsSoulNode(Soul soul) {
        return snake.getSnakeBody().contains(soul.getPosition());
    }

    private Soul randomizeSoul() {
        return new Soul(
                new Node(random.nextInt(field.getSizeX()),
                        random.nextInt(field.getSizeY()),
                        Direction.RIGHT));
    }


}
