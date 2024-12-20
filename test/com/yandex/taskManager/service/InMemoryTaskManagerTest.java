package com.yandex.taskManager.service;

import com.yandex.taskManager.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

class InMemoryTaskManagerTest {

    @Test
    void isEpicCreated() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Обычный эпик");

        // Исполнение
        taskManager.createEpic(epic1);

        // Проверка
        Assertions.assertNotNull(taskManager.getEpicById(epic1.getId()));
    }

    @Test
    void isSubTaskCreated() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Обычный эпик");
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 1", "Обычная подзадача", Statuses.NEW, epic1.getId(), 60, LocalDateTime.now());

        // Исполнение
        taskManager.createSubTask(subTask1);

        // Проверка
        Assertions.assertNotNull(taskManager.getSubTaskById(subTask1.getId()));
    }

    @Test
    void isTaskCreated() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now());

        // Исполнение
        taskManager.createTask(task1);

        // Проверка
        Assertions.assertNotNull(taskManager.getTaskById(task1.getId()));
    }

    @Test
    void isTasksDeletedByType() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now().plusYears(1));
        Task task2 = new Task("Задача 2", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now());
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        // Исполнение
        taskManager.deleteTasks(TaskTypes.TASK);

        // Проверка
        Assertions.assertEquals(taskManager.getAllTasksByType(TaskTypes.TASK), new ArrayList<Task>());
    }

    @Test
    void isTaskDeletedById() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now());
        taskManager.createTask(task1);
        // Исполнение
        taskManager.deleteTask(task1.getId());

        // Проверка
        Assertions.assertNull(taskManager.getTaskById(task1.getId()));
    }

    @Test
    void isEpicStatusUpdatedToInProgress() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Обычный эпик");
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 1", "Обычная подзадача", Statuses.NEW, epic1.getId(), 60, LocalDateTime.now());
        taskManager.createSubTask(subTask1);

        // Исполнение
        subTask1 = new SubTask("Подзадача 1", "Обычная подзадача", Statuses.IN_PROGRESS, epic1.getId(), 60, LocalDateTime.now(), subTask1.getId());
        taskManager.updateSubTask(subTask1);

        // Проверка
        Assertions.assertEquals(epic1.getStatus(), Statuses.IN_PROGRESS);
    }

    @Test
    void isEpicStatusUpdatedToDone() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Обычный эпик");
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 1", "Обычная подзадача", Statuses.NEW, epic1.getId(), 60, LocalDateTime.now().plusYears(1));
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("Подзадача 2", "Обычная подзадача", Statuses.NEW, epic1.getId(), 60, LocalDateTime.now());
        taskManager.createSubTask(subTask2);

        // Исполнение
        subTask1 = new SubTask("Подзадача 1", "Обычная подзадача", Statuses.DONE, epic1.getId(), 60, LocalDateTime.now().plusYears(1), subTask1.getId());
        taskManager.updateSubTask(subTask1);
        subTask2 = new SubTask("Подзадача 2", "Обычная подзадача", Statuses.DONE, epic1.getId(), 60, LocalDateTime.now(), subTask2.getId());
        taskManager.updateSubTask(subTask2);

        // Проверка
        Assertions.assertEquals(epic1.getStatus(), Statuses.DONE);
    }

    @Test
    void isSubTaskDeletedFromEpicChildren() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Обычный эпик");
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 1", "Обычная подзадача", Statuses.NEW, epic1.getId(), 60, LocalDateTime.now().plusYears(1));
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("Подзадача 2", "Обычная подзадача", Statuses.NEW, epic1.getId(), 60, LocalDateTime.now());
        taskManager.createSubTask(subTask2);
        // Исполнение
        taskManager.deleteTask(subTask2.getId());

        // Проверка
        Assertions.assertEquals(epic1.getChildrenIds().size(), 1);
    }

    @Test
    void isEpicStartTimeCalculated() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Обычный эпик");
        taskManager.createEpic(epic1);
        LocalDateTime now = LocalDateTime.now();
        SubTask subTask1 = new SubTask("Подзадача 1", "Обычная подзадача", Statuses.NEW, epic1.getId(), 60, now);

        // Исполнение
        taskManager.createSubTask(subTask1);

        // Проверка
        Assertions.assertEquals(epic1.getStartTime(), now);
    }

    @Test
    void isEpicEndTimeCalculated() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Обычный эпик");
        taskManager.createEpic(epic1);
        LocalDateTime now = LocalDateTime.now();
        SubTask subTask1 = new SubTask("Подзадача 1", "Обычная подзадача", Statuses.NEW, epic1.getId(), 60, now);
        SubTask subTask2 = new SubTask("Подзадача 2", "Обычная подзадача", Statuses.NEW, epic1.getId(), 30, now.plusMinutes(90));

        // Исполнение
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        // Проверка
        Assertions.assertEquals(epic1.getEndTime(), now.plusMinutes(120));
    }

    @Test
    void isEpicDurationCalculated() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Обычный эпик");
        taskManager.createEpic(epic1);
        LocalDateTime now = LocalDateTime.now();
        SubTask subTask1 = new SubTask("Подзадача 1", "Обычная подзадача", Statuses.NEW, epic1.getId(), 60, now);
        SubTask subTask2 = new SubTask("Подзадача 2", "Обычная подзадача", Statuses.NEW, epic1.getId(), 30, now.plusMinutes(90));

        // Исполнение
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        // Проверка
        Assertions.assertEquals(epic1.getDuration(), 90);
    }

    @Test
    void isTasksNonIntercepted() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now());
        Task task2 = new Task("Задача 2", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now().minusMinutes(90));

        // Исполнение
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        // Проверка
        Assertions.assertNotNull(taskManager.getTaskById(task2.getId()));
    }

    @Test
    void isTasksIntercepted() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now());
        Task task2 = new Task("Задача 2", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now().minusMinutes(30));

        // Исполнение
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        // Проверка
        Assertions.assertNull(taskManager.getTaskById(task2.getId()));
    }

    @Test
    void isTasksNonInterceptedByBorders() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();

        // Исполнение
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now());
        taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now().plusMinutes(60));
        taskManager.createTask(task2);

        // Проверка
        Assertions.assertNotNull(taskManager.getTaskById(task2.getId()));
    }

    @Test
    void isTasksInterceptedByBorders() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        ;

        // Исполнение
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now());
        taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now().plusMinutes(60).minusSeconds(1));
        taskManager.createTask(task2);

        // Проверка
        Assertions.assertNull(taskManager.getTaskById(task2.getId()));
    }

    @Test
    void isTasksNonInterceptedByUpdate() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now());
        taskManager.createTask(task1);
        task1 = new Task("Задача 2", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now().minusMinutes(30), task1.getId());

        // Исполнение
        taskManager.updateTask(task1);

        // Проверка
        Assertions.assertEquals(taskManager.getTaskById(task1.getId()).getName(), "Задача 2");
    }

    @Test
    void isTasksInterceptedByUpdate() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now());
        taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now().plusMinutes(90));
        taskManager.createTask(task2);
        task2 = new Task("Задача 2", "Очень обычная задача", Statuses.NEW, 60, LocalDateTime.now().minusMinutes(30), task2.getId());

        // Исполнение
        taskManager.updateTask(task2);

        // Проверка
        Assertions.assertEquals(taskManager.getTaskById(task2.getId()).getDescription(), "Обычная задача");
    }
}