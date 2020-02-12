package com.epam.snakeandroid.services;

import android.os.Build;
import androidx.annotation.RequiresApi;
import com.epam.snakeandroid.Exceptions.GameLostException;
import com.epam.snakeandroid.entities.Direction;

public class GameService {

    private static final String YOU_LOST = "You lost!";
    private SnakeService snakeService;
    private CarrotService carrotService;

    public GameService(SnakeService snakeService,
                       CarrotService carrotService) {
        this.snakeService = snakeService;
        this.carrotService = carrotService;
    }

    @Override
    public String toString() {
        return "GameService{" +
                " snakeService=" + snakeService +
                ", carrotService=" + carrotService +
                '}';
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void update(Direction direction) throws IllegalArgumentException, GameLostException {
        if (!snakeService.hasLost()) {
            snakeService.updateSnakeSituation(direction);
        } else {
            snakeService.makeDeathSound();
            throw new GameLostException(YOU_LOST);
        }
    }

}
