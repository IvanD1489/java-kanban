package com.yandex.taskManager.model.test;

import com.yandex.taskManager.model.Statuses;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.service.Managers;
import com.yandex.taskManager.service.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    public void shouldBeEquals() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW);
        taskManager.createTask(task1);

        // Исполнение
        Task task2 = taskManager.getTaskById(task1.getId());

        // Проверка
        Assertions.assertEquals(task1, task2);
    }

}