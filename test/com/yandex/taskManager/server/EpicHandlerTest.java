package com.yandex.taskManager.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yandex.taskManager.model.*;
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

class EpicHandlerTest {

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


    public EpicHandlerTest() throws IOException {

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
    public void isGettingAllEpics() throws IOException, InterruptedException {
        // Подготовка
        Epic epic1 = new Epic("Эпик 1", "Обычный эпик");
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic("Эпик 2", "Обычный эпик");
        taskManager.createEpic(epic2);

        // Исполнение
        String toCheckWith = gson.toJson(taskManager.getAllTasksByType(TaskTypes.EPIC));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверка
        assertEquals(200, response.statusCode());
        assertEquals(toCheckWith, response.body());
    }

    @Test
    public void isGettingOneEpicInfo() throws IOException, InterruptedException {
        // Подготовка
        Epic epic1 = new Epic("Эпик 1", "Обычный эпик");
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic("Эпик 2", "Обычный эпик");
        taskManager.createEpic(epic2);

        // Исполнение
        String toCheckWith = gson.toJson(taskManager.getEpicById(epic1.getId()));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверка
        assertEquals(200, response.statusCode());
        assertEquals(toCheckWith, response.body());
    }

    @Test
    public void isEpicCreated() throws IOException, InterruptedException {
        // Подготовка
        Epic epic1 = new Epic("Эпик 1", "Обычный эпик");
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic("Эпик 1", "Обычный эпик");

        // Исполнение
        String toCheckWith = gson.toJson(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(toCheckWith)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверка
        assertEquals(201, response.statusCode());
        assertNotNull(taskManager.getEpicById(epic1.getId() + 1));
    }

    @Test
    public void isEpicDeleted() throws IOException, InterruptedException {
        // Подготовка
        Epic epic1 = new Epic("Эпик 1", "Обычный эпик");
        taskManager.createEpic(epic1);

        // Исполнение

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверка
        assertEquals(200, response.statusCode());
        assertNull(taskManager.getEpicById(epic1.getId()));
    }

    @Test
    public void isGotEpicChildrenInfo() throws IOException, InterruptedException {
        // Подготовка
        Epic epic1 = new Epic("Эпик 1", "Обычный эпик");
        taskManager.createEpic(epic1);
        SubTask task1 = new SubTask("Задача 1", "Обычная задача", Statuses.NEW, epic1.getId(), 60, LocalDateTime.now());
        taskManager.createSubTask(task1);
        SubTask task2 = new SubTask("Задача 2", "Обычная задача", Statuses.NEW, epic1.getId(), 60, LocalDateTime.now().plusYears(1));
        taskManager.createSubTask(task2);

        String toCheckWith = gson.toJson(taskManager.getEpicSubTasks(epic1.getId()));

        // Исполнение

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic1.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверка
        assertEquals(200, response.statusCode());
        assertEquals(toCheckWith, response.body());
    }

}