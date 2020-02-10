package com.epam.snakeandroid.Exceptions;

import androidx.annotation.Nullable;

public class GameLostException extends RuntimeException {

    public GameLostException(String message) {
        super(message);
    }

    @Nullable
    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
