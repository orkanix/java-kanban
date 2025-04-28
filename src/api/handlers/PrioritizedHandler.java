package api.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.task.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestMethod().equals("GET")) {
            sendText(httpExchange, gson.toJson(taskManager.getPrioritizedTasks()));
        }
    }
}
