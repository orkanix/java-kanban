package api.habdlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.task.TaskManager;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {

    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoint endpoint = getEndpoint(httpExchange);

        switch (endpoint) {
            case Endpoint.GET_ALL -> handleGetEpics(httpExchange);
            case Endpoint.GET_ID -> handleGetEpic(httpExchange);
            case Endpoint.POST_CREATE -> handleAddEpic(httpExchange);
            case Endpoint.DELETE_ID -> handleDeleteEpic(httpExchange);
            case Endpoint.GET_ALL_SUBTASKS -> handleGetEpicSubtasks(httpExchange);
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

        try {
            Epic epic = gson.fromJson(requestBody, Epic.class);

            if (epic.getId() == 0) {
                int resultCode = taskManager.addNewEpic(epic);

                if (resultCode == -1) {
                    sendHasInteractions(exchange, gson.toJson("Задача пересекается с другой задачей"));
                    return;
                }

                sendCreate(exchange, gson.toJson("Задача успешно добавлена!"));
            } else {
                int resultCode = taskManager.updateEpic(epic);

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

    private Endpoint getEndpoint(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        if (pathParts.length == 2 && exchange.getRequestMethod().equals("GET")) {
            return Endpoint.GET_ALL;
        }
        if (pathParts.length == 2 && exchange.getRequestMethod().equals("POST")) {
            return Endpoint.POST_CREATE;
        }
        if (pathParts.length == 3 && exchange.getRequestMethod().equals("GET")) {
            return Endpoint.GET_ID;
        }
        if (pathParts.length == 3 && exchange.getRequestMethod().equals("DELETE")) {
            return Endpoint.DELETE_ID;
        }
        if (pathParts.length == 4 && exchange.getRequestMethod().equals("GET")) {
            return Endpoint.GET_ALL_SUBTASKS;
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint {
        GET_ALL,
        GET_ID,
        GET_ALL_SUBTASKS,
        POST_CREATE,
        DELETE_ID,
        UNKNOWN
    }
}
