package api.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.task.TaskManager;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {

    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoints endpoint = getEndpoint(httpExchange);

        switch (endpoint) {
            case Endpoints.GET_ALL -> handleGetEpics(httpExchange);
            case Endpoints.GET_ID -> handleGetEpic(httpExchange);
            case Endpoints.POST_CREATE -> handleAddEpic(httpExchange);
            case Endpoints.DELETE_ID -> handleDeleteEpic(httpExchange);
            case Endpoints.GET_ALL_SUBTASKS -> handleGetEpicSubtasks(httpExchange);
            default -> sendNotFound(httpExchange, gson.toJson("Такого эндпоинта не существует"));
        }
    }

    public void handleGetEpics(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getEpics()));
    }

    public void handleGetEpic(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        try {
            int id = Integer.parseInt(pathParts[2]);
            Epic epic = taskManager.getEpic(id);

            if (epic == null) {
                sendNotFound(exchange, gson.toJson("Задача не найдена"));
                return;
            }
            sendText(exchange, gson.toJson(epic));
        } catch (NumberFormatException e) {
            sendIncorrectFormat(exchange, gson.toJson("Некорректный формат ID"));
        }
    }

    public void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        try {
            int id = Integer.parseInt(pathParts[2]);
            Epic epic = taskManager.getEpic(id);

            if (epic == null) {
                sendNotFound(exchange, gson.toJson("Задача не найдена"));
                return;
            }

            List<Subtask> subtasks = taskManager.getEpicSubtasks(id);

            if (subtasks.isEmpty()) {
                sendNotFound(exchange, gson.toJson("Список подзадач пуст"));
                return;
            }
            sendText(exchange, gson.toJson(subtasks));
        } catch (NumberFormatException e) {
            sendIncorrectFormat(exchange, gson.toJson("Некорректный формат ID"));
        }
    }

    public void handleAddEpic(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        if (requestBody.isBlank()) {
            sendNotFound(exchange, gson.toJson(null));
            return;
        }

        try {
            Epic epic = gson.fromJson(requestBody, Epic.class);

            if (epic.getId() == 0) {
                int resultCode = taskManager.addNewEpic(epic);

                if (resultCode == -1) {
                    sendHasInteractions(exchange, gson.toJson("Задача пересекается с другой задачей"));
                    return;
                }

                sendCreate(exchange, gson.toJson("Задача успешно добавлена!"));
            }
        } catch (Exception e) {
            sendNotFound(exchange, gson.toJson("Ошибка обработки задачи: " + e.getMessage()));
        }
    }

    public void handleDeleteEpic(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        try {
            int id = Integer.parseInt(pathParts[2]);
            Epic epic = taskManager.getEpic(id);

            if (epic == null) {
                sendNotFound(exchange, gson.toJson("Задача не найдена"));
                return;
            }
            taskManager.deleteEpic(epic);
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
        if (pathParts.length == 4 && exchange.getRequestMethod().equals("GET")) {
            return Endpoints.GET_ALL_SUBTASKS;
        }
        return Endpoints.UNKNOWN;
    }
}
