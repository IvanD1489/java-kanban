package com.yandex.taskManager.server;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.model.TaskTypes;
import com.yandex.taskManager.server.adapters.DurationAdapter;
import com.yandex.taskManager.server.adapters.LocalDateTimeAdapter;
import com.yandex.taskManager.service.TaskManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String response;
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            if ("GET".equals(exchange.getRequestMethod())) {
                if (pathParts.length > 2) {
                    int id = Integer.parseInt(pathParts[2]);
                    Task epic = taskManager.getEpicById(id);
                    if (epic == null) {
                        sendNotFound(exchange);
                    } else {
                        if (pathParts.length == 3) {
                            response = gson.toJson(epic);
                            sendResponse(exchange, response, 200);
                        } else {
                            response = gson.toJson(taskManager.getEpicSubTasks(id));
                            sendResponse(exchange, response, 200);
                        }
                    }
                } else {
                    List<Task> epics = taskManager.getAllTasksByType(TaskTypes.EPIC);
                    response = gson.toJson(epics);
                    sendResponse(exchange, response, 200);
                }
            } else if ("POST".equals(exchange.getRequestMethod())) {
                createTaskFromRequest(exchange);
            } else if ("DELETE".equals(exchange.getRequestMethod())) {
                int id = Integer.parseInt(pathParts[2]);
                Epic epic = taskManager.getEpicById(id);
                if (epic != null) {
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

        Epic epic;
        try {
            epic = gson.fromJson(requestBody, Epic.class);
            taskManager.createEpic(epic);
            sendResponse(exchange, "Эпик создан", 201);
        } catch (JsonSyntaxException e) {
            sendResponse(exchange, "Некорректный формат данных", 400);
        } catch (Exception e) {
            sendResponse(exchange, "Во время создания задачи возникла ошибка", 500);
        }
    }
}