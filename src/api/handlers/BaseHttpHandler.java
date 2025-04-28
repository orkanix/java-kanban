package api.handlers;

import api.adapters.DurationAdapter;
import api.adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.task.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler implements HttpHandler {

    protected final TaskManager taskManager;

    protected final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create();

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void sendText(HttpExchange exchange, String text) throws IOException {
        exchange.sendResponseHeaders(200, text.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(text.getBytes(StandardCharsets.UTF_8));
        }
    }

    public void sendCreate(HttpExchange exchange, String text) throws IOException {
        exchange.sendResponseHeaders(201, text.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(text.getBytes(StandardCharsets.UTF_8));
        }
    }

    public void sendIncorrectFormat(HttpExchange exchange, String text) throws IOException {
        exchange.sendResponseHeaders(400, text.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(text.getBytes(StandardCharsets.UTF_8));
        }
    }

    public void sendNotFound(HttpExchange exchange, String text) throws IOException {
        exchange.sendResponseHeaders(404, text.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(text.getBytes(StandardCharsets.UTF_8));
        }
    }

    public void sendHasInteractions(HttpExchange exchange, String text) throws IOException {
        exchange.sendResponseHeaders(406, text.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(text.getBytes(StandardCharsets.UTF_8));
        }
    }
}
