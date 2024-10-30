package com.yandex.taskManager.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiyLinkedHashMap {

    public Node head;
    public Node tail;
    private final Map<Integer, Node> taskNodes = new HashMap<>();

    public void put(Integer index, Task data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }

        taskNodes.put(index, newNode);
    }

    public boolean containsKey(Integer key) {
        return taskNodes.containsKey(key);
    }

    public void remove(Integer id) {
        Node node = taskNodes.get(id);
        if (node != null) {
            // Если элемент который удаляем - единственный в цепочке
            if (head == tail && head == node) {
                head = null;
                tail = null;
                // Если удаляем головной элемент
            } else if (node == head) {
                head = head.next;
                head.prev = null;
                // Если удаляем хвостовой элемент
            } else if (node == tail) {
                tail = tail.prev;
                tail.next = null;
                // Во всех других случаях
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }
            taskNodes.remove(id);
        }
    }

    public List<Task> values() {
        List<Task> history = new ArrayList<>();
        Node iterNode = head;
        while (iterNode != null) {
            history.add(iterNode.data);
            iterNode = iterNode.next;
        }

        return history;
    }

}
