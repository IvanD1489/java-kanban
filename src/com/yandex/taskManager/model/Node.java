package com.yandex.taskManager.model;

public class Node {
    Task data;
    Node next;
    Node prev;

    public Node(Task data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }
}

