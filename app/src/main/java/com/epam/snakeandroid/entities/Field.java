package com.epam.snakeandroid.entities;

import java.util.ArrayList;
import java.util.List;

public class Field {

    private int size;
    private List<Node> nodeField;
    private Soul soul;

    public Field(int size) {
        this.size = size;
        nodeField = new ArrayList<>(size);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<Node> getNodeField() {
        return nodeField;
    }

    public void setNodeField(List<Node> nodeField) {
        this.nodeField = nodeField;
    }

    public Soul getSoul() {
        return soul;
    }

    public void setSoul(Soul soul) {
        this.soul = soul;
    }
}
