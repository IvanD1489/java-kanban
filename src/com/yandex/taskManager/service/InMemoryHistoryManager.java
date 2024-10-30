package com.yandex.taskManager.service;

import com.yandex.taskManager.model.DiyLinkedHashMap;
import com.yandex.taskManager.model.Node;
import com.yandex.taskManager.model.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final DiyLinkedHashMap hashHistory = new DiyLinkedHashMap();

    @Override
    public List<Task> getHistory() {
        return new ArrayList<Task>(hashHistory.values());
    }

    @Override
    public void addHistory(Task task) {
        if (hashHistory.containsKey(task.getId())) {
            removeHistory(task.getId());
        }
        hashHistory.put(task.getId(), task);
    }

    @Override
    public void removeHistory(int id) {
        hashHistory.remove(id);
    }


}