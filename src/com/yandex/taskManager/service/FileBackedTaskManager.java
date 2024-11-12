package com.yandex.taskManager.service;

import com.yandex.taskManager.model.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createTask(subTask);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTasks(TaskTypes taskType) {
        super.deleteTasks(taskType);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    public Task fromString(String value){
        String[] data = value.split(",");
        int id = Integer.parseInt(data[0]);
        TaskTypes type = TaskTypes.valueOf(data[1]);
        String name = data[2];
        Statuses status = Statuses.valueOf(data[3]);
        String desc = data[4];
        int parentId = Integer.parseInt(data[5]);
        if(type == TaskTypes.TASK){
            return new Task(name, desc, status, id);
        }
        else  if(type == TaskTypes.SUBTASK){
            return new SubTask(name, desc, status, parentId, id);
        }
        else{
            return new Epic(name, desc, id);
        }
    }

    protected void recalculateEpicStatus(int epicId) {
        super.recalculateEpicStatus(epicId);
        save();
    }

    private void save() throws IOException {
        final List<Task> tasks = super.getAllTasksByType(TaskTypes.TASK);
        final List<Task> subTasks = super.getAllTasksByType(TaskTypes.SUBTASK);
        final List<Task> epics = super.getAllTasksByType(TaskTypes.EPIC);

        Writer fileWriter = new FileWriter("filewriter.txt", true);

        for(Task task : tasks){
            String taskStringify = task.toString();

        }
    }

}
