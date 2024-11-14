package com.yandex.taskManager.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public TaskTypes getType() {
        return TaskTypes.EPIC;
    }

    @Override
    public String toString() {
        return super.getId() + "," + "EPIC," + super.getName() + "," + super.getStatus() + "," + super.getDescription() + ",";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(childrenIds, epic.childrenIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), childrenIds);
    }
}
