package com.yandex.taskManager.service;

import com.yandex.taskManager.model.DiyLinkedHashMap;
import com.yandex.taskManager.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final DiyLinkedHashMap hashHistory = new DiyLinkedHashMap();

    @Override
    public List<Task> getHistory() {
        return hashHistory.values();
    }

    @Override
    public void addHistory(Task task) {
        removeHistory(task.getId());
        hashHistory.put(task.getId(), task);
    }

    @Override
    public void removeHistory(int id) {
        hashHistory.remove(id);
    }


}
