package com.yandex.taskManager.service;

import com.yandex.taskManager.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private final List<Task> history = new ArrayList<>();

    private final static int MAX_HISTORY_SIZE = 10;

    @Override
    public List<Task> getHistory(){
        return List.copyOf(history);
    }

    @Override
    public void addHistory(Task task){
        history.add(task);
        if(history.size() > MAX_HISTORY_SIZE){
            history.removeFirst();
        }
    }

}
