package api.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.task.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestMethod().equals("GET")) {
            sendText(httpExchange, gson.toJson(taskManager.getHistory()));
            return;
        }
        sendNotFound(httpExchange, gson.toJson("Такого эндпоинта не существует"));
    }
}
