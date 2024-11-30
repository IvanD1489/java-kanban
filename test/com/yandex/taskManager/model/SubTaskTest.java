package com.yandex.taskManager.model;

import com.yandex.taskManager.service.Managers;
import com.yandex.taskManager.service.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class SubTaskTest {

    @Test
    public void shouldBeEquals() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Обычный эпик");
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 1", "Обычная подзадача", Statuses.NEW, epic1.getId(), 60, LocalDateTime.now());
        taskManager.createSubTask(subTask1);

        // Исполнение
        SubTask subTask2 = taskManager.getSubTaskById(subTask1.getId());

        // Проверка
        Assertions.assertEquals(subTask1, subTask2);
    }

    @Test
    public void shouldNotHaveSubTaskAsEpic() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Обычный эпик");
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 1", "Обычная подзадача", Statuses.NEW, epic1.getId(), 60, LocalDateTime.now());
        taskManager.createSubTask(subTask1);

        // Исполнение
        SubTask subTask2 = new SubTask("Подзадача 1", "Обычная подзадача", Statuses.NEW, subTask1.getId(), 60, LocalDateTime.now());
        taskManager.createSubTask(subTask2);

        // Проверка
        Assertions.assertNull(taskManager.getSubTaskById(3));
    }

}