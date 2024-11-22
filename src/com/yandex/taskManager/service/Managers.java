package com.yandex.taskManager.service;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedTaskManager(String dataPath) {
        return new FileBackedTaskManager(dataPath);
    }
}
