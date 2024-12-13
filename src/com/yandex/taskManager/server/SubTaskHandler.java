package com.yandex.taskManager.server;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.taskManager.model.SubTask;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.model.TaskTypes;
import com.yandex.taskManager.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public SubTaskHandler(TaskManager taskManager) {
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
                    Task subTask = taskManager.getSubTaskById(id);
                    if (subTask != null) {
                        response = gson.toJson(subTask);
                        sendResponse(exchange, response, 200);
                    } else {
                        sendNotFound(exchange);
                    }
                } else {
                    List<Task> subTasks = taskManager.getAllTasksByType(TaskTypes.SUBTASK);
                    response = gson.toJson(subTasks);
                    sendResponse(exchange, response, 200);
                }
            } else if ("POST".equals(exchange.getRequestMethod())) {
                createTaskFromRequest(exchange);
            } else if ("DELETE".equals(exchange.getRequestMethod())) {
                int id = Integer.parseInt(pathParts[2]);
                SubTask subTask = taskManager.getSubTaskById(id);
                if (subTask != null) {
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

        SubTask subTask;
        try {
            subTask = gson.fromJson(requestBody, SubTask.class);
            if (subTask.getId() == 0) {
                if (taskManager.createSubTask(subTask)) {
                    sendResponse(exchange, "Подзадача создана", 201);
                } else {
                    sendHasInteractions(exchange);
                }
            } else {
                if (taskManager.updateSubTask(subTask)) {
                    sendResponse(exchange, "Подзадача обновлена", 201);
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