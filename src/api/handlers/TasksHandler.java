package api.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.task.TaskManager;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TasksHandler extends BaseHttpHandler {

    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoints endpoint = getEndpoint(httpExchange);

        switch (endpoint) {
            case Endpoints.GET_ALL -> handleGetTasks(httpExchange);
            case Endpoints.GET_ID -> handleGetTask(httpExchange);
            case Endpoints.POST_CREATE -> handleAddTask(httpExchange);
            case Endpoints.DELETE_ID -> handleDeleteTask(httpExchange);
            default -> sendNotFound(httpExchange, gson.toJson("Такого эндпоинта не существует"));
        }
    }

    public void handleGetTasks(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getTasks()));
    }

    public void handleGetTask(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        try {
            int id = Integer.parseInt(pathParts[2]);
            Task task = taskManager.getTask(id);

            if (task == null) {
                sendNotFound(exchange, gson.toJson("Задача не найдена"));
                return;
            }
            sendText(exchange, gson.toJson(task));
        } catch (NumberFormatException e) {
            sendIncorrectFormat(exchange, gson.toJson("Некорректный формат ID"));
        }
    }

    public void handleAddTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        if (requestBody.isBlank()) {
            sendNotFound(exchange, gson.toJson(null));
            return;
        }

        try {
            Task task = gson.fromJson(requestBody, Task.class);

            if (task.getId() == 0) {
                int resultCode = taskManager.addNewTask(task);

                if (resultCode == -1) {
                    sendHasInteractions(exchange, gson.toJson("Задача пересекается с другой задачей"));
                    return;
                }

                sendCreate(exchange, gson.toJson("Задача успешно добавлена!"));
            } else {
                int resultCode = taskManager.updateTask(task);

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

    public void handleDeleteTask(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        try {
            int id = Integer.parseInt(pathParts[2]);
            Task task = taskManager.getTask(id);

            if (task == null) {
                sendNotFound(exchange, gson.toJson("Задача не найдена"));
                return;
            }
            taskManager.deleteTask(task);
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
