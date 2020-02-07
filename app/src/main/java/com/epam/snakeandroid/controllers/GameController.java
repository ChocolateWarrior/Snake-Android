package com.epam.snakeandroid.controllers;


import android.os.Build;
import androidx.annotation.RequiresApi;
import com.epam.snakeandroid.services.GameService;

import java.io.IOException;

public class GameController {

    private GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void play() throws IOException {
        gameService.play();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void restart() throws IOException {
        play();
    }

}
