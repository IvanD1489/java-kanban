package com.yandex.taskManager.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yandex.taskManager.model.Statuses;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.model.TaskTypes;
import com.yandex.taskManager.server.adapters.DurationAdapter;
import com.yandex.taskManager.server.adapters.LocalDateTimeAdapter;
import com.yandex.taskManager.service.Managers;
import com.yandex.taskManager.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskHandlerTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager taskManager = Managers.getDefault();
    ;
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    public TaskHandlerTest() throws IOException {

    }

    @BeforeEach
    public void setUp() {
        taskManager.deleteTasks(TaskTypes.TASK);
        taskManager.deleteTasks(TaskTypes.SUBTASK);
        taskManager.deleteTasks(TaskTypes.EPIC);
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void isGettingAllTasksInfo() throws IOException, InterruptedException {
        // Подготовка
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now());
        taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now().plusYears(1));
        taskManager.createTask(task2);

        // Исполнение
        String toCheckWith = gson.toJson(taskManager.getAllTasksByType(TaskTypes.TASK));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверка
        assertEquals(200, response.statusCode());
        assertEquals(toCheckWith, response.body());
    }

    @Test
    public void isGettingOneTaskInfo() throws IOException, InterruptedException {
        // Подготовка
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now());
        taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now().plusYears(1));
        taskManager.createTask(task2);

        // Исполнение
        String toCheckWith = gson.toJson(taskManager.getTaskById(task1.getId()));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверка
        assertEquals(200, response.statusCode());
        assertEquals(toCheckWith, response.body());
    }

    @Test
    public void isTaskCreated() throws IOException, InterruptedException {
        // Подготовка
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now());
        taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now().plusYears(1));
        taskManager.createTask(task2);
        Task task3 = new Task("Задача 3", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now().plusYears(2));

        // Исполнение
        String toCheckWith = gson.toJson(task3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(toCheckWith)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверка
        assertEquals(201, response.statusCode());
        assertNotNull(taskManager.getTaskById(task2.getId() + 1));
    }

    @Test
    public void isTaskUpdated() throws IOException, InterruptedException {
        // Подготовка
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now());
        taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now().plusYears(1));
        taskManager.createTask(task2);
        task2 = new Task("Задача 2", "Очень обычная задача", Statuses.NEW, 60, LocalDateTime.now().plusYears(1), task2.getId());

        // Исполнение
        String toCheckWith = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(toCheckWith)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверка
        assertEquals(201, response.statusCode());
        assertEquals(taskManager.getTaskById(task2.getId()).getDescription(), "Очень обычная задача");
    }

    @Test
    public void isTaskDeleted() throws IOException, InterruptedException {
        // Подготовка
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now());
        taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Обычная задача", Statuses.NEW, 60, LocalDateTime.now().plusYears(1));
        taskManager.createTask(task2);

        // Исполнение

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task2.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверка
        assertEquals(200, response.statusCode());
        assertNull(taskManager.getTaskById(task2.getId()));
    }

}