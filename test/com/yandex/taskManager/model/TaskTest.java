package com.yandex.taskManager.model;

import com.yandex.taskManager.service.Managers;
import com.yandex.taskManager.service.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class TaskTest {

    @Test
    public void shouldBeEquals() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now());
        taskManager.createTask(task1);

        // Исполнение
        Task task2 = taskManager.getTaskById(task1.getId());

        // Проверка
        Assertions.assertEquals(task1, task2);
    }

}