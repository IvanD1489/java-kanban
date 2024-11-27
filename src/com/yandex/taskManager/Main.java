package com.yandex.taskManager;

import com.yandex.taskManager.model.*;
import com.yandex.taskManager.service.Managers;
import com.yandex.taskManager.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        
        TaskManager taskManager = Managers.getFileBackedTaskManager("filewriter.txt");
        for (Task task : taskManager.getPrioritizedTasks()) {
            System.out.println(task);
        }

    }
}
