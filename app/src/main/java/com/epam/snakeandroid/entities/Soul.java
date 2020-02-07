package com.epam.snakeandroid.entities;

public class Soul {
    private Node position;

    public Soul(Node position) {
        this.position = position;
    }

    public Node getPosition() {
        return position;
    }

    public void setPosition(Node position) {
        this.position = position;
    }
}
