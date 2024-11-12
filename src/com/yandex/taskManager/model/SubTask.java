package com.yandex.taskManager.model;

public class SubTask extends Task {

    private final int parentId;

    public SubTask(String name, String description, Statuses status, int parentId) {
        super(name, description, status);
        this.parentId = parentId;
    }

    public SubTask(String name, String description, Statuses status, int parentId, int id) {
        super(name, description, status, id);
        this.parentId = parentId;
    }

    public int getParentId() {
        return this.parentId;
    }

    @Override
    public String toString() {
        return super.getId() + "," + "SUBTASK," + super.getName() + "," + super.getStatus() + "," + super.getDescription() + "," + parentId;
    }

}
