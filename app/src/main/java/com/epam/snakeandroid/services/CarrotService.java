package com.epam.snakeandroid.services;

import com.epam.snakeandroid.entities.*;
import java.util.Random;

public class CarrotService {

    private Snake snake;
    private Random random;
    private Field field;

    public CarrotService(Random random,
                         Field field,
                         Snake snake) {
        this.random = random;
        this.field = field;
        this.snake = snake;
    }

    public void spawnNewCarrot() {
        Carrot carrot = randomizeCarrot();
        while (snakeContainsCarrotNode(carrot)) {
            carrot = randomizeCarrot();
        }
        field.setCarrot(carrot);
    }

    private boolean snakeContainsCarrotNode(Carrot carrot) {
        return snake.getSnakeBody().contains(carrot.getPosition());
    }

    private Carrot randomizeCarrot() {
        return new Carrot(
                new Node(random.nextInt(field.getSizeX()),
                        random.nextInt(field.getSizeY()),
                        Direction.RIGHT));
    }


}
