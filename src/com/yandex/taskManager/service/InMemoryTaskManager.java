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

    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    @Override
    public boolean createTask(Task task) {
        prioritizedTasks.add(task);
        if (checkTaskIntersection()) {
            prioritizedTasks.remove(task);
            return false;
        }

        task.setId(getNewTaskId());
        tasks.put(task.getId(), task);
        return true;
    }

    @Override
    public boolean createSubTask(SubTask subTask) {
        int epicId = subTask.getParentId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return false;
        }
        prioritizedTasks.add(subTask);
        if (checkTaskIntersection()) {
            prioritizedTasks.remove(subTask);
            return false;
        }
        subTask.setId(getNewTaskId());
        subTasks.put(subTask.getId(), subTask);
        epic.addChild(subTask.getId());
        recalculateEpicData(epicId);
        return true;
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(getNewTaskId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public boolean updateTask(Task task) {
        if (checkTaskIntersectionForUpdate(task)) {
            return false;
        }
        tasks.put(task.getId(), task);
        return true;
    }

    @Override
    public boolean updateSubTask(SubTask subTask) {
        if (checkTaskIntersectionForUpdate(subTask)) {
            return false;
        }
        subTasks.put(subTask.getId(), subTask);
        int epicId = subTask.getParentId();
        recalculateEpicData(epicId);
        return true;
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
                        .forEach(task -> {
                            historyManager.removeHistory(task.getId());
                            prioritizedTasks.remove(task);
                        });
                tasks.clear();
            }
            case SUBTASK -> {
                subTasks.values()
                        .forEach(task -> {
                            historyManager.removeHistory(task.getId());
                            prioritizedTasks.remove(task);
                        });
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
                prioritizedTasks.clear();
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
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public boolean checkTaskIntersection() {
        Set<Task> set = this.prioritizedTasks;

        return set.stream().anyMatch(task1 ->
                set.stream().anyMatch(task2 ->
                        !task1.equals(task2) &&
                                (task1.getStartTime().isBefore(task2.getEndTime()) && task2.getStartTime().isBefore(task1.getEndTime()))
                )
        );
    }

    @Override
    public boolean checkTaskIntersectionForUpdate(Task task) {
        Task oldTask = getTaskById(task.getId());
        if (oldTask == null) {
            oldTask = getSubTaskById(task.getId());
        }
        prioritizedTasks.remove(oldTask);
        prioritizedTasks.add(task);
        if (checkTaskIntersection()) {
            prioritizedTasks.remove(task);
            prioritizedTasks.add(oldTask);
            return true;
        }

        return false;
    }

    protected void addToMap(Task task) {
        if (task.getType() == TaskTypes.TASK) {
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        } else if (task.getType() == TaskTypes.SUBTASK) {
            final SubTask subTask = (SubTask) task;
            subTasks.put(task.getId(), subTask);
            epics.get(subTask.getParentId()).addChild(task.getId());
            prioritizedTasks.add(task);
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
        if (epicChildren.isEmpty()) {
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
        if (epicChildren.isEmpty()) {
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
        if (epicChildren.isEmpty()) {
            return;
        }
        Optional<LocalDateTime> minTime = epicChildren
                .stream()
                .map(this::getSubTaskById)
                .min(Comparator.comparing(Task::getStartTime))
                .map(Task::getStartTime);
        Optional<LocalDateTime> maxTime = epicChildren
                .stream()
                .map(this::getSubTaskById)
                .max(Comparator.comparing(Task::getEndTime))
                .map(Task::getEndTime);
        minTime.ifPresent(epic::setStartTime);
        maxTime.ifPresent(epic::setEndTime);
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
