package com.epam.snakeandroid.entities;

import java.util.ArrayDeque;
import java.util.Deque;

public class Snake {

    private int size;
    private Node head;
    private Deque<Node> snakeBody;

    public Snake() {
        size = 2;
        head = new Node(0, 0, Direction.UP);
        snakeBody = new ArrayDeque<>(size);
        snakeBody.add(head);
        snakeBody.add(new Node(0, 1, Direction.UP));
    }

    public int getSize() {
        return size;
    }

    public Node getHead() {
        return head;
    }

    public Deque<Node> getSnakeBody() {
        return snakeBody;
    }

    public void setHead(Node head) {
        this.head = head;
    }

    public void setSnakeBody(Deque<Node> snakeBody) {
        this.snakeBody = snakeBody;
    }

    public void incrementSize() {
        this.size++;
    }
}
