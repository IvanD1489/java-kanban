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
        Node newNode = new Node(data, null, null);
        if (head == null) {
            head = newNode;
        } else {
            tail.setNext(newNode);
            newNode.setPrev(tail);
        }
        tail = newNode;

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
                head = head.getNext();
                head.setPrev(null);
                // Если удаляем хвостовой элемент
            } else if (node == tail) {
                tail = tail.getPrev();
                tail.setNext(null);
                // Во всех других случаях
            } else {
                node.getPrev().setNext(node.getNext());
                node.getNext().setPrev(node.getPrev());
            }
            taskNodes.remove(id);
        }
    }

    public List<Task> values() {
        List<Task> history = new ArrayList<>(taskNodes.size());
        Node iterNode = head;
        while (iterNode != null) {
            history.add(iterNode.getData());
            iterNode = iterNode.getNext();
        }

        return history;
    }

}
