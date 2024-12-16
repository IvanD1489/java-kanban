package com.yandex.taskManager.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.taskManager.service.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String response;
            if ("GET".equals(exchange.getRequestMethod())) {
                response = gson.toJson(taskManager.getHistory());
                sendResponse(exchange, response, 200);
            } else {
                sendResponse(exchange, "Метод не поддерживается", 405);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendResponse(exchange, "Во время выполнения запроса возникла ошибка", 500);
        }
    }
}