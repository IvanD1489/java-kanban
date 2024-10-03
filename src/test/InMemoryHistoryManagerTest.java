package test;

import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.model.Statuses;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.service.Managers;
import com.yandex.taskManager.service.TaskManager;
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
}