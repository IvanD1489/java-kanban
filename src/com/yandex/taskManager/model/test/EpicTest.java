package com.yandex.taskManager.model.test;

import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.service.Managers;
import com.yandex.taskManager.service.TaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class EpicTest {

    @Test
    public void shouldBeEquals() {
        // Подготовка
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Обычный эпик");
        taskManager.createEpic(epic1);

        // Исполнение
        Epic epic2 = taskManager.getEpicById(epic1.getId());

        // Проверка
        Assertions.assertEquals(epic1, epic2);
    }

}