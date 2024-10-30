package com.yandex.taskManager.service;

import com.yandex.taskManager.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private int taskSequence = 1;

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void createTask(Task task) {
        task.setId(getNewTaskId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void createSubTask(SubTask subTask) {
        int epicId = subTask.getParentId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        subTask.setId(getNewTaskId());
        subTasks.put(subTask.getId(), subTask);
        epic.addChild(subTask.getId());
        recalculateEpicStatus(epicId);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(getNewTaskId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        int epicId = subTask.getParentId();
        recalculateEpicStatus(epicId);
    }

    @Override
    public void updateEpic(Epic epic) {
        final Epic oldEpic = epics.get(epic.getId());
        oldEpic.setName(epic.getName());
        oldEpic.setDescription(epic.getDescription());
    }

    @Override
    public List<Task> getAllTasksByType(TaskTypes taskType) {
        return switch (taskType) {
            case TASK -> new ArrayList<>(tasks.values());
            case SUBTASK -> new ArrayList<>(subTasks.values());
            case EPIC -> new ArrayList<>(epics.values());
        };
    }

    @Override
    public void deleteTasks(TaskTypes taskType) {
        switch (taskType) {
            case TASK -> {
                for (Task task : tasks.values()) {
                    historyManager.removeHistory(task.getId());
                }
                tasks.clear();
            }
            case SUBTASK -> {
                for (SubTask subTask : subTasks.values()) {
                    historyManager.removeHistory(subTask.getId());
                }
                subTasks.clear();
                for (Epic epic : epics.values()) {
                    epic.clearChildren();
                    recalculateEpicStatus(epic.getId());
                }
            }
            case EPIC -> {
                for (Epic epic : epics.values()) {
                    historyManager.removeHistory(epic.getId());
                }
                for (SubTask subTask : subTasks.values()) {
                    historyManager.removeHistory(subTask.getId());
                }
                subTasks.clear();
                epics.clear();
            }
        }
    }

    @Override
    public List<SubTask> getEpicSubTasks(int epicId) {
        List<SubTask> children = new ArrayList<>();
        if (!epics.containsKey(epicId)) {
            return children;
        }

        Epic epic = epics.get(epicId);
        for (int childId : epic.getChildrenIds()) {
            children.add(subTasks.get(childId));
        }

        return children;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            return null;
        }
        historyManager.addHistory(task);
        return task;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask == null) {
            return null;
        }
        historyManager.addHistory(subTask);
        return subTask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return null;
        }
        historyManager.addHistory(epic);
        return epics.get(id);
    }

    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (subTasks.containsKey(id)) {
            int parentId = subTasks.get(id).getParentId();
            epics.get(parentId).removeChild(id);
            subTasks.remove(id);
            recalculateEpicStatus(parentId);
        } else {
            List<Integer> childrenIds = new ArrayList<>(epics.get(id).getChildrenIds());
            for (int childId : childrenIds) {
                deleteTask(childId);
            }
            epics.remove(id);
        }

        historyManager.removeHistory(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void recalculateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> epicChildren = epic.getChildrenIds();
        int childrenCount = epicChildren.size();
        if (childrenCount == 0) {
            return;
        }
        boolean allDone = true;
        boolean allNew = true;
        for (int childId : epicChildren) {
            SubTask child = subTasks.get(childId);
            if (child.getStatus() != Statuses.DONE) {
                allDone = false;
            }
            if (child.getStatus() != Statuses.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            epic.setStatus(Statuses.DONE);
        } else if (allNew) {
            epic.setStatus(Statuses.NEW);
        } else {
            epic.setStatus(Statuses.IN_PROGRESS);
        }

    }

    private int getNewTaskId() {
        return taskSequence++;
    }

}
