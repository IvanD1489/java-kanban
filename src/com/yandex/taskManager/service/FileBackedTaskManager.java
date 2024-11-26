package com.yandex.taskManager.service;

import com.yandex.taskManager.exceptions.ManagerSaveException;
import com.yandex.taskManager.model.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final String dataPath;

    private static final String FILE_HEADER = "id,type,name,status,description,duration,start,epic\n";

    public FileBackedTaskManager(String dataPath) {
        this.dataPath = dataPath;
        File dir = new File(this.dataPath);
        loadFromFile(dir);
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTasks(TaskTypes taskType) {
        super.deleteTasks(taskType);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    public Task fromString(String value) {
        String[] data = value.split(",");
        int id = Integer.parseInt(data[0]);
        TaskTypes type = TaskTypes.valueOf(data[1]);
        String name = data[2];
        Statuses status = Statuses.valueOf(data[3]);
        String desc = data[4];
        long duration = Long.parseLong(data[5]);
        LocalDateTime startTime = LocalDateTime.parse(data[6], DateTimeFormatter.ISO_DATE_TIME);
        if (type == TaskTypes.TASK) {
            return new Task(name, desc, status, duration, startTime, id);
        } else if (type == TaskTypes.SUBTASK) {
            return new SubTask(name, desc, status, Integer.parseInt(data[7]), duration, startTime, id);
        } else {
            return new Epic(name, desc, id);
        }
    }

    private void loadFromFile(File file) throws ManagerSaveException {
        try (FileReader reader = new FileReader(file.getPath())) {
            BufferedReader br = new BufferedReader(reader);

            // First row is column names
            br.readLine();

            while (br.ready()) {
                String line = br.readLine();
                Task newTask = fromString(line);
                addToMap(newTask);
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private void save() throws ManagerSaveException {
        final List<Task> tasks = super.getAllTasksByType(TaskTypes.TASK);
        final List<Task> subTasks = super.getAllTasksByType(TaskTypes.SUBTASK);
        final List<Task> epics = super.getAllTasksByType(TaskTypes.EPIC);

        try (Writer fileWriter = new FileWriter(this.dataPath)) {
            fileWriter.append(FILE_HEADER);

            for (Task task : tasks) {
                fileWriter.append(task.toString()).append("\n");
            }
            for (Task task : epics) {
                fileWriter.append(task.toString()).append("\n");
            }
            for (Task task : subTasks) {
                fileWriter.append(task.toString()).append("\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

}
