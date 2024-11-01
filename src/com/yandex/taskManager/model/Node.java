package com.yandex.taskManager.model;

public class Node {
    private Task data;
    private Node next;
    private Node prev;

    public Node(Task data, Node next, Node prev) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }

    public Node getNext() {
        return this.next;
    }

    public Node getPrev() {
        return this.prev;
    }

    public Task getData() {
        return this.data;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public void setData(Task data) {
        this.data = data;
    }

}

