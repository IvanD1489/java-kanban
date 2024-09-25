package com.yandex.taskManager;

import com.yandex.taskManager.model.*;
import com.yandex.taskManager.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

        // 1. Создание задач
        //     1.1. Обычные задачи
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Обычная задача", Statuses.NEW);
        taskManager.createTask(task2);
        //     1.2 Эпик
        Epic epic1 = new Epic("Эпик 1", "Обычный эпик");
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic("Эпик 2", "Обычный эпик");
        taskManager.createEpic(epic2);
        //     1.3 Подзадачи
        SubTask subTask11 = new SubTask("Подзадача 1-1", "Обычная подзадача", Statuses.NEW, epic1.getId());
        taskManager.createSubTask(subTask11);
        SubTask subTask12 = new SubTask("Подзадача 1-2", "Обычная подзадача", Statuses.NEW, epic1.getId());
        taskManager.createSubTask(subTask12);
        SubTask subTask21 = new SubTask("Подзадача 2-1", "Обычная подзадача", Statuses.NEW, epic2.getId());
        taskManager.createSubTask(subTask21);
        // 2. Вывод задач по типу
        System.out.println("Вывод задач по типам:");
        System.out.println(taskManager.getAllTasksByType(TaskTypes.TASK)); // --> {1=com.yandex.taskManager.model.Task@6f496d9f, 2=com.yandex.taskManager.model.Task@723279cf}
        System.out.println(taskManager.getAllTasksByType(TaskTypes.SUBTASK)); // --> {5=com.yandex.taskManager.model.SubTask@2f4d3709, 6=com.yandex.taskManager.model.SubTask@4e50df2e, 7=com.yandex.taskManager.model.SubTask@1d81eb93}
        System.out.println(taskManager.getAllTasksByType(TaskTypes.EPIC)); // --> {3=com.yandex.taskManager.model.Epic@7291c18f, 4=com.yandex.taskManager.model.Epic@34a245ab}
        System.out.println();
        // 3. Удаление всех задач по типу "Обычный таск"
        taskManager.deleteTasks(TaskTypes.TASK);
        // Повторный вывод после удаления:
        System.out.println("Вывод задач после удаления:");
        System.out.println(taskManager.getAllTasksByType(TaskTypes.TASK)); // --> {}
        System.out.println();

        // 4. Получение задачи по идентификатору:
        Task task3 = new Task("Задача 3", "Обычная задача", Statuses.NEW);
        taskManager.createTask(task3);
        System.out.println("Вывод несуществующей и существующей задачи:");
        // Несуществующая задача
        System.out.println(taskManager.getTaskById(1)); // --> null
        // Существующая задача
        System.out.println(taskManager.getTaskById(task3.getId())); // --> com.yandex.taskManager.model.Task@10f87f48
        System.out.println();

        // 5. Обновление задачи:
        //     5.1 Обычной задачи
        System.out.println("Вывод обновленной информации о задаче:");
        System.out.println(taskManager.getTaskById(task3.getId()).getDescription()); // --> Обычная задача
        task3 = new Task("Задача 3", "Обычная задача в работе", Statuses.IN_PROGRESS, task3.getId());
        taskManager.updateTask(task3);
        System.out.println(taskManager.getTaskById(task3.getId()).getDescription()); // --> Обычная задача в работе
        System.out.println();

        //     5.2 Задачи в эпике со сменой статуса эпика:
        System.out.println("Автоматический подсчет статуса эпика:");
        System.out.println("До обновления (одна задача в статусе NEW)");
        System.out.println(epic2.getStatus()); // --> NEW
        subTask21 = new SubTask("Подзадача 2-1", "Обычная подзадача в работе", Statuses.IN_PROGRESS, epic2.getId(), subTask21.getId());
        taskManager.updateSubTask(subTask21);
        System.out.println("После обновления (одна задача в статусе IN_PROGRESS)");
        System.out.println(epic2.getStatus()); // --> IN_PROGRESS
        subTask21 = new SubTask("Подзадача 2-1", "Обычная подзадача в работе", Statuses.DONE, epic2.getId(), subTask21.getId());
        taskManager.updateSubTask(subTask21);
        System.out.println("После обновления (одна задача в статусе DONE)");
        System.out.println(epic2.getStatus()); // --> DONE
        System.out.println();

        // 6. Удаление задачи по идентификатору:
        System.out.println("Удаление задачи по идентификатору:");
        System.out.println(taskManager.getTaskById(task3.getId())); // --> com.yandex.taskManager.model.Task@b4c966a
        taskManager.deleteTask(task3.getId());
        System.out.println(taskManager.getTaskById(task3.getId())); // --> null
        System.out.println();

        // 7. Получение подзадач по идентификатору эпика:
        System.out.println("Получение подзадач по идентификатору эпика:");
        System.out.println(taskManager.getEpicSubTasks(epic1.getId()));
    }
}
