package com.epam.snakeandroid.services;

import android.os.Build;
import androidx.annotation.RequiresApi;
import com.epam.snakeandroid.entities.Direction;
import com.epam.snakeandroid.views.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GameService {

    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RESET = "\u001B[0m";

    //    private static final String UPPERCASE_LINE = " ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾";
    private static final String UPPERCASE_LINE = " ________________";
    private static final String UNDERSCORE_LINE = " ________________";
    private static final String TYPE_TO_EXIT = " Type \"q\" to exit";
    private static final String LOST_MESSAGE = "You have lost!";
    private static final String EXIT_CHARACTER = "q";
    private static final String YOU_CAN_ONLY_USE_CONTROLS = " You can only use " +
            "\"w/a/s/d\" to move OR \"q\" to quit";
    private static final String TRY_AGAIN = " Try again";

    private View view;
    private RenderService renderService;
    private SnakeService snakeService;
    private SoulService soulService;
    private BufferedReader bufferedReader;

    public GameService(View view,
                       RenderService renderService,
                       SnakeService snakeService,
                       SoulService soulService) {
        this.view = view;
        this.renderService = renderService;
        this.snakeService = snakeService;
        this.soulService = soulService;
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }

    public GameService(SnakeService snakeService, SoulService soulService) {
        this.snakeService = snakeService;
        this.soulService = soulService;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void play() throws IOException{
        showInitialGameState();
        playWhileNotInterrupted(bufferedReader.readLine());
    }

    @Override
    public String toString() {
        return "GameService{" +
                "view=" + view +
                ", renderService=" + renderService +
                ", snakeService=" + snakeService +
                ", soulService=" + soulService +
                ", bufferedReader=" + bufferedReader +
                '}';
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void playWhileNotInterrupted(String userInput) throws IOException {

        while (!userInput.equals(EXIT_CHARACTER)) {
            try {
                update(Direction.valueOf(userInput.toUpperCase()));
                showInterfaceBottom();
                if (snakeService.hasLost()){
                    view.viewAsNewLine(LOST_MESSAGE);
                    return;
                }
            } catch (IllegalArgumentException e){
                showIncorrectInputMessage();
            }
            userInput = bufferedReader.readLine();
        }
    }

    private void showIncorrectInputMessage() {
        view.view(ANSI_YELLOW);
        view.viewAsNewLine(YOU_CAN_ONLY_USE_CONTROLS);
        view.viewAsNewLine(TRY_AGAIN);
        view.view(ANSI_RESET);
    }

    private void showInterfaceBottom() {
        view.viewAsNewLine(UPPERCASE_LINE);
        view.viewAsNewLine(TYPE_TO_EXIT);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showInitialGameState(){
        view.flush();
        soulService.spawnNewSoul();
        renderService.render();
        view.viewAsNewLine(UNDERSCORE_LINE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void update(Direction direction) throws IllegalArgumentException {
        snakeService.updateSnakeSituation(direction);
//        view.viewAsNewLine(UNDERSCORE_LINE);
//        view.flush();
//        renderService.render();
    }

}
