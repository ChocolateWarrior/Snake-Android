package com.epam.snakeandroid.services;

import com.epam.snakeandroid.entities.Direction;
import com.epam.snakeandroid.entities.Node;
import com.epam.snakeandroid.entities.Snake;
import com.epam.snakeandroid.entities.Field;

import java.util.ArrayDeque;
import java.util.Deque;

public class SnakeService {

    private SoulService soulService;
    private Field field;
    private Snake snake;

    public SnakeService(SoulService soulService,
                        Field field,
                        Snake snake) {
        this.soulService = soulService;
        this.field = field;
        this.snake = snake;
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

    private void updateSnakeBody(Direction direction){
        Deque<Node> body = snake.getSnakeBody();

        body.removeLast();
        body.addFirst(getHeadByDirection(direction));
        snake.setHead(getHeadByDirection(direction));
        snake.setSnakeBody(body);
    }

    private void eatSpriteIfPossible() {
        if (snake.getHead().equals(field.getSoul().getPosition())) {
            growSnake();
            soulService.spawnNewSoul();
        }
    }

    private Node getHeadByDirection(Direction direction){

        Node oldHead = snake.getHead();

        switch (direction){
            case UP:
                return new Node(oldHead.getXCoordinate(),
                        (oldHead.getYCoordinate() - 1 + field.getSize()) % field.getSize(),
                        direction);
            case DOWN:
                return new Node(oldHead.getXCoordinate(),
                        (oldHead.getYCoordinate() + 1) % field.getSize(),
                        Direction.DOWN);
            case RIGHT:
                return new Node(
                        (oldHead.getXCoordinate() + 1) % field.getSize(),
                        oldHead.getYCoordinate(),
                        Direction.RIGHT);
            case LEFT:
                return new Node(
                        (oldHead.getXCoordinate() - 1 + field.getSize()) % field.getSize(),
                        oldHead.getYCoordinate(),
                        Direction.LEFT);
            default:
                return getHeadByDirection(snake.getHead().getDirection());
        }
    }

    private void growSnake() {

        Node tail = snake.getSnakeBody().getLast();

        switch (tail.getDirection()) {
            case RIGHT:
                snake.getSnakeBody()
                        .addLast(new Node(
                                (tail.getXCoordinate() - 1 + field.getSize()) % field.getSize(),
                                tail.getYCoordinate(),
                                tail.getDirection()));
                break;
            case DOWN:
                snake.getSnakeBody()
                        .addLast(new Node(tail.getXCoordinate(),
                                (tail.getYCoordinate() - 1 + field.getSize()) % field.getSize(),
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
