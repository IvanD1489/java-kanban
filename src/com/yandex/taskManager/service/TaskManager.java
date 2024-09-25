package com.yandex.taskManager.service;

import com.yandex.taskManager.model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private int taskSequence = 1;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    public void createTask(Task task){
        task.setId(getNewTaskId());
        tasks.put(task.getId(), task);
    }

    public void createSubTask(SubTask subTask){
        subTask.setId(getNewTaskId());
        subTasks.put(subTask.getId(), subTask);
        int epicId = subTask.getParentId();
        Epic epic = epics.get(epicId);
        epic.addChild(subTask.getId());
        recalculateEpicStatus(epicId);
    }

    public void createEpic(Epic epic){
        epic.setId(getNewTaskId());
        epics.put(epic.getId(), epic);
    }

    public void updateTask(Task task){
        tasks.put(task.getId(), task);
    }

    public void updateSubTask(SubTask subTask){
        subTasks.put(subTask.getId(), subTask);
        int epicId = subTask.getParentId();
        recalculateEpicStatus(epicId);
    }

    public void updateEpic(Epic epic){
        final Epic oldEpic = epics.get(epic.getId());
        oldEpic.setName(epic.getName());
        oldEpic.setDescription(epic.getDescription());
    }

    public ArrayList<Task> getAllTasksByType(TaskTypes taskType) {
        return switch (taskType) {
            case TASK -> new ArrayList<>(tasks.values());
            case SUBTASK -> new ArrayList<>(subTasks.values());
            case EPIC -> new ArrayList<>(epics.values());
        };
    }

    public void deleteTasks(TaskTypes taskType){
        switch (taskType) {
            case TASK -> tasks.clear();
            case SUBTASK -> {
                subTasks.clear();
                for(Epic epic : epics.values()){
                    epic.clearChildren();
                    recalculateEpicStatus(epic.getId());
                }
            }
            case EPIC -> {
                subTasks.clear();
                epics.clear();
            }
        }
    }

    public ArrayList<SubTask> getEpicSubTasks(int epicId){
        ArrayList<SubTask> children = new ArrayList<>();
        if(!epics.containsKey(epicId)){
            return children;
        }

        Epic epic = epics.get(epicId);
        for(int childId : epic.getChildrenIds()){
            children.add(subTasks.get(childId));
        }

        return children;
    }

    public Task getTaskById(int id){
        return tasks.get(id);
    }

    public SubTask getSubTaskById(int id){
        return subTasks.get(id);
    }

    public Epic getEpicById(int id){
        return epics.get(id);
    }

    public void deleteTask(int id){
        if(tasks.containsKey(id)){
            tasks.remove(id);
        }
        else if(subTasks.containsKey(id)){
            int parentId = subTasks.get(id).getParentId();
            epics.get(parentId).removeChild(id);
            subTasks.remove(id);
            recalculateEpicStatus(parentId);
        }
        else{
            ArrayList<Integer> childrenIds = epics.get(id).getChildrenIds();
            for( int childId : childrenIds){
                deleteTask(childId);
            }
            epics.remove(id);
        }
    }

    private void recalculateEpicStatus(int epicId){
        Epic epic = epics.get(epicId);
        ArrayList<Integer> epicChildren = epic.getChildrenIds();
        int childrenCount = epicChildren.size();
        if(childrenCount == 0){
            return;
        }
        boolean allDone = true;
        boolean allNew = true;
        for(int childId : epicChildren){
            SubTask child = subTasks.get(childId);
            if(child.getStatus() != Statuses.DONE){
                allDone = false;
            }
            if(child.getStatus() != Statuses.NEW){
                allNew = false;
            }
        }

        if(allDone){
            epic.setStatus(Statuses.DONE);
        }
        else if(allNew){
            epic.setStatus(Statuses.NEW);
        }
        else{
            epic.setStatus(Statuses.IN_PROGRESS);
        }

    }

    private int getNewTaskId(){
        return taskSequence++;
    }

}
