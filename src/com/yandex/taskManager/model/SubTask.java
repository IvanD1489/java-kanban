package com.yandex.taskManager.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {

    private final int parentId;

    public SubTask(String name, String description, Statuses status, int parentId, int duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.parentId = parentId;
    }

    public SubTask(String name, String description, Statuses status, int parentId, long duration, LocalDateTime startTime, int id) {
        super(name, description, status, duration, startTime, id);
        this.parentId = parentId;
    }

    public int getParentId() {
        return this.parentId;
    }

    @Override
    public TaskTypes getType() {
        return TaskTypes.SUBTASK;
    }

    @Override
    public String toString() {
        return super.getId() + "," + "SUBTASK," + super.getName() + "," + super.getStatus() + "," +
                super.getDescription() + "," + super.getDuration() + "," + super.getStartTime() + "," + parentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return parentId == subTask.parentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parentId);
    }
}
