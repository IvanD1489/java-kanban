package com.yandex.taskManager.service;

import com.yandex.taskManager.model.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

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
        recalculateEpicData(epicId);
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
        recalculateEpicData(epicId);
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
                    recalculateEpicData(epic.getId());
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
            recalculateEpicData(parentId);
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

    @Override
    public List<Task> getPrioritizedTasks(){
        Set<Task> set = new TreeSet<>(new Comparator<Task>() {
            @Override
            public int compare(Task s1, Task s2) {
                return s1.getStartTime().compareTo(s2.getStartTime());
            }
        });

        set.addAll(tasks.values());
        set.addAll(subTasks.values());

        return new ArrayList<>(set);
    }

    protected void addToMap(Task task) {
        if (task.getType() == TaskTypes.TASK) {
            tasks.put(task.getId(), task);
        } else if (task.getType() == TaskTypes.SUBTASK) {
            final SubTask subTask = (SubTask) task;
            subTasks.put(task.getId(), subTask);
            epics.get(subTask.getParentId()).addChild(task.getId());
        } else {
            epics.put(task.getId(), (Epic) task);
        }
        if (task.getId() >= taskSequence) {
            taskSequence = task.getId() + 1;
        }
    }

    protected void recalculateEpicStatus(int epicId) {
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

    protected void recalculateEpicDuration(int epicId){
        Epic epic = epics.get(epicId);
        List<Integer> epicChildren = epic.getChildrenIds();
        int childrenCount = epicChildren.size();
        if (childrenCount == 0) {
            epic.setDuration(0);
            return;
        }
        long totalDuration = 0;
        for (int childId : epicChildren) {
            SubTask child = subTasks.get(childId);
            totalDuration += child.getDuration();
        }
        epic.setDuration(totalDuration);
    }

    protected void recalculateEpicTime(int epicId){
        Epic epic = epics.get(epicId);
        List<Integer> epicChildren = epic.getChildrenIds();
        int childrenCount = epicChildren.size();
        if (childrenCount == 0) {
            return;
        }
        LocalDateTime minTime = List.copyOf(epicChildren)
                .stream()
                .min((t1, t2) -> {
                    Task task1 = getSubTaskById(t1);
                    Task task2 = getSubTaskById(t2);
                    return task1.getStartTime().compareTo(task2.getStartTime());
                })
                .map(t -> getSubTaskById(t).getStartTime())
                .orElse(null);
        LocalDateTime maxTime = List.copyOf(epicChildren)
                .stream()
                .max((t1, t2) -> {
                    SubTask task1 = getSubTaskById(t1);
                    SubTask task2 = getSubTaskById(t2);
                    return task1.getEndTime().compareTo(task2.getEndTime());
                })
                .map(t -> getSubTaskById(t).getEndTime())
                .orElse(null);
        if(minTime != null){
            epic.setStartTime(minTime);
        }
        if(maxTime != null){
            epic.setEndTime(maxTime);
        }
    }

    protected void recalculateEpicData(int epicId){
        recalculateEpicStatus(epicId);
        recalculateEpicDuration(epicId);
        recalculateEpicTime(epicId);
    }

    private int getNewTaskId() {
        return taskSequence++;
    }

}
