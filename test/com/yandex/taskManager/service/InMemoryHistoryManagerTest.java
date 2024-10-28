package com.yandex.taskManager.service;

import com.yandex.taskManager.model.Statuses;
import com.yandex.taskManager.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class InMemoryHistoryManagerTest {

    @Test
    void isHistoryStored() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW);
        taskManager.createTask(task1);
        taskManager.getTaskById(task1.getId());
        List<Task> checkHistory = new ArrayList<>();
        checkHistory.add(task1);

        // Исполнение
        List<Task> history = taskManager.getHistory();

        // Проверка
        Assertions.assertEquals(history, checkHistory);
    }

    @Test
    void isHistoryAdded() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW);
        taskManager.createTask(task1);
        taskManager.getTaskById(task1.getId());

        // Исполнение
        List<Task> history = taskManager.getHistory();

        // Проверка
        Assertions.assertEquals(history.size(), 1);
    }

    @Test
    void isReplacingAlreadyViewedTask() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Обычная задача", Statuses.NEW);
        taskManager.createTask(task2);
        List<Task> checkHistory = new ArrayList<>();
        checkHistory.add(task2);
        checkHistory.add(task1);

        // Исполнение
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());


        // Проверка
        Assertions.assertEquals(taskManager.getHistory(), checkHistory);
    }

    @Test
    void isRemovedFromHistoryWhenDeleted() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Обычная задача", Statuses.NEW);
        taskManager.createTask(task2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        List<Task> checkHistory = new ArrayList<>();
        checkHistory.add(task2);

        // Исполнение
        taskManager.deleteTask(task1.getId());

        // Проверка
        Assertions.assertEquals(taskManager.getHistory(), checkHistory);
    }

}