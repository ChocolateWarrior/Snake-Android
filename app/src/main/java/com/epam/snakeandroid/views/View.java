package com.epam.snakeandroid.views;

public class View {

    private static final String FLUSH_STRING = "\033[H\033[2J";

    public void view(String viewed){
        System.out.print(viewed);
    }
    public void viewAsNewLine(String viewed){
        System.out.println(viewed);
    }
    public void viewEmpty() {
        System.out.println();
    }
    public void flush(){
        System.out.print(FLUSH_STRING);
    }
}
