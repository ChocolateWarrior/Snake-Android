package com.epam.snakeandroid.entities;

import java.util.ArrayDeque;
import java.util.Deque;

public class Snake {

    private int size;
    private Node head;
    private Deque<Node> snakeBody;

    public Snake() {
        size = 3;
        head = new Node(0, 0, Direction.UP);
        snakeBody = new ArrayDeque<>(size);
        snakeBody.add(head);
        snakeBody.add(new Node(0, 1, Direction.UP));
        snakeBody.add(new Node(0, 2, Direction.UP));
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Node getHead() {
        return head;
    }

    public void setHead(Node head) {
        this.head = head;
    }

    public Deque<Node> getSnakeBody() {
        return snakeBody;
    }

    public void setSnakeBody(Deque<Node> snakeBody) {
        this.snakeBody = snakeBody;
    }
}
