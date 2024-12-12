package com.yandex.taskManager.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import com.google.gson.Gson;

public class Task {

    private String name;
    private String description;
    private int id;
    private Duration duration;
    private LocalDateTime startTime;
    private Statuses status;

    public Task(String name, String description, Statuses status, long duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = startTime;
    }

    public Task(String name, String description, Statuses status, long duration, LocalDateTime startTime, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = startTime;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Statuses getStatus() {
        return this.status;
    }

    public TaskTypes getType() {
        return TaskTypes.TASK;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return this.startTime.plus(duration);
    }

    public long getDuration() {
        return this.duration.toMinutes();
    }

    public void setStatus(Statuses status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDuration(long duration) {
        this.duration = Duration.ofMinutes(duration);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return id + "," + "TASK," + name + "," + status + "," + description + "," + duration.toMinutes() + "," + startTime + ",";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }
}
