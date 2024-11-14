package com.yandex.taskManager;

import com.yandex.taskManager.model.*;
import com.yandex.taskManager.service.Managers;
import com.yandex.taskManager.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        // 1. Создание объкета менеджера с возможностью хранить данные в файловой системе
        TaskManager taskManager = Managers.getFileBackedTaskManager("filewriter.txt");

        // 2. Вывод задач по типу
        System.out.println("Вывод задач по типам:");
        System.out.println(taskManager.getAllTasksByType(TaskTypes.TASK));
        System.out.println(taskManager.getAllTasksByType(TaskTypes.SUBTASK));
        System.out.println(taskManager.getAllTasksByType(TaskTypes.EPIC));
        System.out.println();

        // 3. Получение истории просмотра задач:
        System.out.println(taskManager.getHistory());
    }
}
