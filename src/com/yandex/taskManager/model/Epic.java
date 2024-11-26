package com.yandex.taskManager.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private final List<Integer> childrenIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Statuses.NEW, 0, LocalDateTime.now());
    }

    public Epic(String name, String description, int id) {
        super(name, description, Statuses.NEW, 0, LocalDateTime.now(), id);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime(){
        return this.endTime;
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
        return super.getId() + "," + "EPIC," + super.getName() + "," + super.getStatus() + "," + super.getDescription() + "," + duration.toMinutes() + "," + startTime + ",";
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
