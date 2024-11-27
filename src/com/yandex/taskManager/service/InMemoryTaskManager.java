package com.yandex.taskManager.service;

import com.yandex.taskManager.model.*;

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
        if (checkTaskIntersection(task)) {
            return;
        }
        task.setId(getNewTaskId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void createSubTask(SubTask subTask) {
        if (checkTaskIntersection(subTask)) {
            return;
        }
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
        if (checkTaskIntersection(epic)) {
            return;
        }
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
                tasks.values()
                        .forEach(task -> historyManager.removeHistory(task.getId()));
                tasks.clear();
            }
            case SUBTASK -> {
                subTasks.values()
                        .forEach(task -> historyManager.removeHistory(task.getId()));
                subTasks.clear();
                epics.values()
                        .forEach(task -> {
                            task.clearChildren();
                            recalculateEpicData(task.getId());
                        });
            }
            case EPIC -> {
                epics.values()
                        .forEach(task -> historyManager.removeHistory(task.getId()));
                subTasks.values()
                        .forEach(task -> historyManager.removeHistory(task.getId()));
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
        epic.getChildrenIds()
                .forEach(childId -> children.add(subTasks.get(childId)));

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
            epics.get(id).getChildrenIds().forEach(this::deleteTask);
            epics.remove(id);
        }

        historyManager.removeHistory(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        Set<Task> set = new TreeSet<>(Comparator.comparing(Task::getStartTime));

        set.addAll(tasks.values());
        set.addAll(subTasks.values());

        return set;
    }

    @Override
    public boolean checkTaskIntersection(Task newTask) {
        Set<Task> set = getPrioritizedTasks();
        set.add(newTask);

        return set.stream().anyMatch(task1 ->
                set.stream().anyMatch(task2 ->
                        !task1.equals(task2) &&
                                (task1.getStartTime().isBefore(task2.getEndTime()) && task2.getStartTime().isBefore(task1.getEndTime()))
                )
        );
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
        boolean allDone = epicChildren.stream()
                .map(subTasks::get)
                .allMatch(child -> child.getStatus() == Statuses.DONE);
        boolean allNew = epicChildren.stream()
                .map(subTasks::get)
                .allMatch(child -> child.getStatus() == Statuses.NEW);

        if (allDone) {
            epic.setStatus(Statuses.DONE);
        } else if (allNew) {
            epic.setStatus(Statuses.NEW);
        } else {
            epic.setStatus(Statuses.IN_PROGRESS);
        }

    }

    protected void recalculateEpicDuration(int epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> epicChildren = epic.getChildrenIds();
        int childrenCount = epicChildren.size();
        if (childrenCount == 0) {
            epic.setDuration(0);
            return;
        }
        long totalDuration = epicChildren.stream()
                .map(subTasks::get)
                .mapToLong(SubTask::getDuration)
                .sum();
        epic.setDuration(totalDuration);
    }

    protected void recalculateEpicTime(int epicId) {
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
        if (minTime != null) {
            epic.setStartTime(minTime);
        }
        if (maxTime != null) {
            epic.setEndTime(maxTime);
        }
    }

    protected void recalculateEpicData(int epicId) {
        recalculateEpicStatus(epicId);
        recalculateEpicDuration(epicId);
        recalculateEpicTime(epicId);
    }

    private int getNewTaskId() {
        return taskSequence++;
    }

}
