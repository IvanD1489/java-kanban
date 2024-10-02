package com.yandex.taskManager.service;

import com.yandex.taskManager.model.Task;

import java.util.ArrayList;

public interface HistoryManager {

    ArrayList<Task> getHistory();

    void addHistory(Task task);

}
