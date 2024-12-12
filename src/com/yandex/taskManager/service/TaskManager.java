package com.yandex.taskManager.service;

import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.model.SubTask;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.model.TaskTypes;

import java.util.List;

public interface TaskManager {

    boolean createTask(Task task);

    boolean createSubTask(SubTask subTask);

    void createEpic(Epic epic);

    boolean updateTask(Task task);

    boolean updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    List<Task> getAllTasksByType(TaskTypes taskType);

    void deleteTasks(TaskTypes taskType);

    List<SubTask> getEpicSubTasks(int epicId);

    Task getTaskById(int id);

    SubTask getSubTaskById(int id);

    Epic getEpicById(int id);

    void deleteTask(int id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    boolean checkTaskIntersection();

    boolean checkTaskIntersectionForUpdate(Task task);

}
