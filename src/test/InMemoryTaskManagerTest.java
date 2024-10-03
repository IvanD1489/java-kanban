package test;

import com.yandex.taskManager.model.*;
import com.yandex.taskManager.service.Managers;
import com.yandex.taskManager.service.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        SubTask subTask1 = new SubTask("Подзадача 1", "Обычная подзадача", Statuses.NEW, epic1.getId());

        // Исполнение
        taskManager.createSubTask(subTask1);

        // Проверка
        Assertions.assertNotNull(taskManager.getSubTaskById(subTask1.getId()));
    }

    @Test
    void isTaskCreated() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW);

        // Исполнение
        taskManager.createTask(task1);

        // Проверка
        Assertions.assertNotNull(taskManager.getTaskById(task1.getId()));
    }

    @Test
    void isTasksDeletedByType() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW);
        Task task2 = new Task("Задача 2", "Обычная задача", Statuses.NEW);
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
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW);
        taskManager.createTask(task1);
        // Исполнение
        taskManager.deleteTask(task1.getId());

        // Проверка
        Assertions.assertNull(taskManager.getTaskById(task1.getId()));
    }
}