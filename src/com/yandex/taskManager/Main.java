package com.yandex.taskManager;

import com.yandex.taskManager.model.*;
import com.yandex.taskManager.service.Managers;
import com.yandex.taskManager.service.TaskManager;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        // 1. Создание объкета менеджера с возможностью хранить данные в файловой системе
        TaskManager taskManager = Managers.getFileBackedTaskManager("filewriter.txt");
        for(Task task : taskManager.getPrioritizedTasks()){
            System.out.println(task);
        }
//        System.out.println(task.getStartTime());
//        System.out.println(task.getEndTime());
//        System.out.println();
//
//        Epic epic = new Epic("Эпик 1", "Обычный эпик");
//        taskManager.createEpic(epic);
//
//        SubTask subTask1 = new SubTask("Подзадача 1", "Обычная подзадача", Statuses.IN_PROGRESS, epic.getId(), 90, LocalDateTime.now());
//        taskManager.createSubTask(subTask1);
//
//        SubTask subTask2 = new SubTask("Подзадача 2", "Обычная подзадача", Statuses.IN_PROGRESS, epic.getId(), 30, LocalDateTime.now().plusMinutes(30));
//        taskManager.createSubTask(subTask2);
//
//        System.out.println(epic.getStartTime());
//        System.out.println(epic.getEndTime());
//        System.out.println(epic.getDuration());

//        // 2. Вывод задач по типу
//        System.out.println("Вывод задач по типам:");
//        System.out.println(taskManager.getAllTasksByType(TaskTypes.TASK));
//        System.out.println(taskManager.getAllTasksByType(TaskTypes.SUBTASK));
//        System.out.println(taskManager.getAllTasksByType(TaskTypes.EPIC));
//        System.out.println();
//
//        SubTask newTask = new SubTask("Подзадача 3", "Обычная подзадача", Statuses.IN_PROGRESS, 4, 8);
//        taskManager.updateSubTask(newTask);
//
//        // 3. Получение истории просмотра задач:
//        System.out.println(taskManager.getHistory());
    }
}
