package com.epam.snakeandroid.services;

import android.os.Build;
import androidx.annotation.RequiresApi;
import com.epam.snakeandroid.entities.Direction;
import com.epam.snakeandroid.entities.Node;
import com.epam.snakeandroid.entities.Snake;
import com.epam.snakeandroid.entities.Field;
import com.epam.snakeandroid.views.View;

import java.util.NoSuchElementException;

public class RenderService {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_BLUE = "\u001B[34m";

//    private static final String TRIANGLE_RIGHT = " \u25BA ";
//    private static final String TRIANGLE_DOWN = " \u25BC ";
//    private static final String TRIANGLE_LEFT = " \u25C4 ";
//    private static final String TRIANGLE_UP = " \u25B2 ";
    private static final String TRIANGLE_RIGHT = " > ";
    private static final String TRIANGLE_DOWN = " v ";
    private static final String TRIANGLE_LEFT = " < ";
    private static final String TRIANGLE_UP = " ^ ";
    private static final String CIRCLE = " o ";
    private static final String SPACE = "   ";

    private Field field;
    private Snake snake;
    private View view;

    public RenderService(Field field,
                         Snake snake,
                         View view) {
        this.field = field;
        this.snake = snake;
        this.view = view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void render() {
        for (int i = 0; i < field.getSize(); i++) {
            for (int j = 0; j < field.getSize(); j++) {
                Node currentNode = new Node(j, i, Direction.RIGHT);

                if (isBodyPart(currentNode)) {
                    if (isHead(currentNode)) {
                        displayHead(snake.getHead());
                    } else {
                        view.view(ANSI_RED + CIRCLE + ANSI_RESET);
                    }
                } else if (isSprite(currentNode)) {
                    view.view(ANSI_BLUE + CIRCLE + ANSI_RESET);
                } else {
                    view.view(SPACE);
                }
            }
            view.viewEmpty();
        }
    }

    private boolean isSprite(Node currentNode) {
        return field.getSoul().getPosition().equals(currentNode);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean isHead(Node currentNode) {
        return snake.getSnakeBody().stream()
                .filter(e -> e.equals(currentNode))
                .findFirst()
                .orElseThrow(NoSuchElementException::new)
                .equals(snake.getHead());


    }

    private boolean isBodyPart(Node currentNode) {
        return snake.getSnakeBody().contains(currentNode);
    }

    private void displayHead(Node head) {
        switch (head.getDirection()) {
            case RIGHT:
                view.view(ANSI_RED + TRIANGLE_RIGHT + ANSI_RESET);
                break;
            case DOWN:
                view.view(ANSI_RED + TRIANGLE_DOWN + ANSI_RESET);
                break;
            case LEFT:
                view.view(ANSI_RED + TRIANGLE_LEFT + ANSI_RESET);
                break;
            case UP:
                view.view(ANSI_RED + TRIANGLE_UP + ANSI_RESET);
                break;
        }
    }

}
