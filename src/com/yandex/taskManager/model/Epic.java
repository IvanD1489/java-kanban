package com.yandex.taskManager.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> childrenIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Statuses.NEW);
    }

    public Epic(String name, String description, int id) {
        super(name, description, Statuses.NEW, id);
    }

    public void addChild(int childId) {
        if (!childrenIds.contains(childId)) {
            this.childrenIds.add(childId);
        }
    }

    public void removeChild(int childId) {
        this.childrenIds.remove(Integer.valueOf(childId));
    }

    public void clearChildren() {
        childrenIds.clear();
    }

    public List<Integer> getChildrenIds() {
        return this.childrenIds;
    }

}
