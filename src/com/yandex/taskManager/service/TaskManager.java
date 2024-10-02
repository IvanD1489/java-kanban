package com.yandex.taskManager.service;

import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.model.SubTask;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.model.TaskTypes;

import java.util.ArrayList;

public interface TaskManager {

    void createTask(Task task);

    void createSubTask(SubTask subTask);

    void createEpic(Epic epic);

    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    ArrayList<Task> getAllTasksByType(TaskTypes taskType);

    void deleteTasks(TaskTypes taskType);

    ArrayList<SubTask> getEpicSubTasks(int epicId);

    Task getTaskById(int id);

    SubTask getSubTaskById(int id);

    Epic getEpicById(int id);

    void deleteTask(int id);

    ArrayList<Task> getHistory();

}
