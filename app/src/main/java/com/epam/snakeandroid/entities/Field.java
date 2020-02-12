package com.epam.snakeandroid.entities;

import java.util.ArrayList;
import java.util.List;

public class Field {

    private int sizeX;
    private int sizeY;
    private List<Node> nodeField;
    private Carrot carrot;

    public Field(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.nodeField = new ArrayList<>(sizeX * sizeY);
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public Carrot getCarrot() {
        return carrot;
    }

    public void setCarrot(Carrot carrot) {
        this.carrot = carrot;
    }

    @Override
    public String toString() {
        return "Field{" +
                "sizeX=" + sizeX +
                ", sizeY=" + sizeY +
                ", nodeField=" + nodeField +
                ", carrot=" + carrot +
                '}';
    }
}
