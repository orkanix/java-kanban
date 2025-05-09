package api.handlers;

import api.ApiTestMethods;
import api.HttpTaskServer;
import manager.Managers;
import manager.task.TaskManager;
import model.Epic;
import model.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EpicsHandlerTest extends ApiTestMethods {

    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);

    EpicsHandlerTest() throws IOException {
    }

    @BeforeEach
    public void beforeEach() {
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
        httpTaskServer.start();
    }

    @AfterEach
    public void afterEach() {
        httpTaskServer.stop();
    }

    @Test
    public void checkGetEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание задачи2", Status.NEW);
        addEpic(epic);
        HttpResponse<String> response = getEpics();

        Epic[] epics = gson.fromJson(response.body(), Epic[].class);

        boolean taskFound = Arrays.stream(epics).anyMatch(t -> taskEquals(t, epic));

        assertTrue(taskFound, "Задача не найдена в ответе GET-запроса");
        assertEquals(200, response.statusCode());
    }

    @Test
    public void checkGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание задачи2", Status.NEW);
        addEpic(epic);
        HttpResponse<String> response = getEpic(1);

        Epic responseEpic = gson.fromJson(response.body(), Epic.class);

        boolean taskFound = taskEquals(responseEpic, epic);
        assertTrue(taskFound, "Задача не найдена в ответе GET-запроса");
        assertEquals(200, response.statusCode());
    }

    @Test
    public void checkAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание задачи2", Status.NEW);
        HttpResponse<String> responseAdd = addEpic(epic);
        HttpResponse<String> response = getEpics();

        Epic[] epics = gson.fromJson(response.body(), Epic[].class);

        boolean taskFound = Arrays.stream(epics).anyMatch(t -> taskEquals(t, epic));

        assertTrue(taskFound, "Задача не найдена в ответе GET-запроса");
        assertEquals(1, taskManager.getEpics().size(), "Некорректное добавление задачи");
        assertEquals(201, responseAdd.statusCode());
    }

    @Test
    public void checkDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Задача 1", "Описание задачи2", Status.NEW);
        addEpic(epic);
        HttpResponse<String> response = deleteEpic(1);

        assertTrue(taskManager.getTasks().isEmpty(), "Некорректное удаление задачи");
        assertEquals(200, response.statusCode());
    }

    @Test
    public void chechStatusCode404() throws IOException, InterruptedException {
        Epic epic = new Epic("Задача 1", "Описание задачи2", Status.NEW);
        addEpic(epic);
        HttpResponse<String> response = getEpic(2);

        assertEquals(404, response.statusCode());
    }
}