package com.yandex.taskManager.service;

import com.yandex.taskManager.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{

    private final ArrayList<Task> history = new ArrayList<>();

    @Override
    public ArrayList<Task> getHistory(){
        return history;
    }

    @Override
    public void addHistory(Task task){
        history.add(task);
        if(history.size() > 10){
            history.removeFirst();
        }
    }

}
