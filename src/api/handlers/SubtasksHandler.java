package api.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.task.TaskManager;
import model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SubtasksHandler extends BaseHttpHandler {

    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoints endpoint = getEndpoint(httpExchange);

        switch (endpoint) {
            case Endpoints.GET_ALL -> handleGetSubtasks(httpExchange);
            case Endpoints.GET_ID -> handleGetSubtask(httpExchange);
            case Endpoints.POST_CREATE -> handleAddSubtask(httpExchange);
            case Endpoints.DELETE_ID -> handleDeleteSubtask(httpExchange);
            default -> sendNotFound(httpExchange, gson.toJson("Такого эндпоинта не существует"));
        }
    }

    public void handleGetSubtasks(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getSubtasks()));
    }

    public void handleGetSubtask(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        try {
            int id = Integer.parseInt(pathParts[2]);
            Subtask subtask = taskManager.getSubtask(id);

            if (subtask == null) {
                sendNotFound(exchange, gson.toJson("Задача не найдена"));
                return;
            }
            sendText(exchange, gson.toJson(subtask));
        } catch (NumberFormatException e) {
            sendIncorrectFormat(exchange, gson.toJson("Некорректный формат ID"));
        }
    }

    public void handleAddSubtask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        if (requestBody.isBlank()) {
            sendNotFound(exchange, gson.toJson(null));
            return;
        }

        try {
            Subtask subtask = gson.fromJson(requestBody, Subtask.class);

            if (subtask.getId() == 0) {
                int resultCode = taskManager.addNewSubtask(subtask);

                if (resultCode == -1) {
                    sendHasInteractions(exchange, gson.toJson("Задача пересекается с другой задачей"));
                    return;
                }

                sendCreate(exchange, gson.toJson("Задача успешно добавлена!"));
            } else {
                int resultCode = taskManager.updateSubtask(subtask);

                if (resultCode == -1) {
                    sendNotFound(exchange, gson.toJson("Задача не найдена"));
                    return;
                }
                if (resultCode == -2) {
                    sendHasInteractions(exchange, gson.toJson("Задача пересекается с другой задачей"));
                    return;
                }

                sendCreate(exchange, gson.toJson("Задача успешно обновлена!"));
            }
        } catch (Exception e) {
            sendNotFound(exchange, gson.toJson("Ошибка обработки задачи: " + e.getMessage()));
        }
    }

    public void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        try {
            int id = Integer.parseInt(pathParts[2]);
            Subtask subtask = taskManager.getSubtask(id);

            if (subtask == null) {
                sendNotFound(exchange, gson.toJson("Задача не найдена"));
                return;
            }
            taskManager.deleteSubtask(subtask);
            sendText(exchange, gson.toJson("Задача успешно удалена!"));
        } catch (NumberFormatException e) {
            sendIncorrectFormat(exchange, gson.toJson("Некорректный формат ID"));
        }
    }

    private Endpoints getEndpoint(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        if (pathParts.length == 2 && exchange.getRequestMethod().equals("GET")) {
            return Endpoints.GET_ALL;
        }
        if (pathParts.length == 2 && exchange.getRequestMethod().equals("POST")) {
            return Endpoints.POST_CREATE;
        }
        if (pathParts.length == 3 && exchange.getRequestMethod().equals("GET")) {
            return Endpoints.GET_ID;
        }
        if (pathParts.length == 3 && exchange.getRequestMethod().equals("DELETE")) {
            return Endpoints.DELETE_ID;
        }

        return Endpoints.UNKNOWN;
    }
}
