package com.yandex.taskManager.server;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.model.TaskTypes;
import com.yandex.taskManager.server.adapters.DurationAdapter;
import com.yandex.taskManager.server.adapters.LocalDateTimeAdapter;
import com.yandex.taskManager.service.Managers;
import com.yandex.taskManager.service.TaskManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String response;
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            if ("GET".equals(exchange.getRequestMethod())) {
                if (pathParts.length == 3) {
                    int id = Integer.parseInt(pathParts[2]);
                    Task task = taskManager.getTaskById(id);
                    if (task != null) {
                        response = gson.toJson(task);
                        sendResponse(exchange, response, 200);
                    } else {
                        sendNotFound(exchange);
                    }
                } else {
                    List<Task> tasks = taskManager.getAllTasksByType(TaskTypes.TASK);
                    response = gson.toJson(tasks);
                    sendResponse(exchange, response, 200);
                }
            } else if ("POST".equals(exchange.getRequestMethod())) {
                createTaskFromRequest(exchange);
            } else if ("DELETE".equals(exchange.getRequestMethod())) {
                int id = Integer.parseInt(pathParts[2]);
                Task task = taskManager.getTaskById(id);
                if (task != null) {
                    taskManager.deleteTask(id);
                    sendResponse(exchange, "Успешно удалено", 200);
                } else {
                    sendNotFound(exchange);
                }
            } else {
                sendResponse(exchange, "Метод не поддерживается", 405);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendResponse(exchange, "Во время выполнения запроса возникла ошибка", 500);
        }
    }

    private void createTaskFromRequest(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        Task task;
        try {
            task = gson.fromJson(requestBody, Task.class);
            if (task.getId() == 0) {
                if (taskManager.createTask(task)) {
                    sendResponse(exchange, "Задача создана", 201);
                } else {
                    sendHasInteractions(exchange);
                }
            } else {
                if (taskManager.updateTask(task)) {
                    sendResponse(exchange, "Задача обновлена", 201);
                } else {
                    sendHasInteractions(exchange);
                }
            }
        } catch (JsonSyntaxException e) {
            sendResponse(exchange, "Некорректный формат данных", 400);
        } catch (Exception e) {
            sendResponse(exchange, "Во время создания задачи возникла ошибка", 500);
        }
    }
}