package api.habdlers;

import api.ApiTestMethods;
import api.HttpTaskServer;
import manager.Managers;
import manager.task.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SubtasksHandlerTest extends ApiTestMethods {

    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);

    SubtasksHandlerTest() throws IOException {
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
    public void checkGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("эпик", "описание эпика", Status.NEW);
        Subtask subtask = new Subtask("Задача 1", "Описание задачи2", Status.NEW, 1);
        addEpic(epic);
        addSubtask(subtask);
        HttpResponse<String> response = getSubtasks();

        Subtask[] subtasks = gson.fromJson(response.body(), Subtask[].class);

        boolean taskFound = Arrays.stream(subtasks).anyMatch(s -> taskEquals(s, subtask));

        assertTrue(taskFound, "Задача не найдена в ответе GET-запроса");
        assertEquals(200, response.statusCode());
    }

    @Test
    public void checkGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("эпик", "описание эпика", Status.NEW);
        Subtask subtask = new Subtask("Задача 1", "Описание задачи2", Status.NEW, 1);
        addEpic(epic);
        addSubtask(subtask);
        HttpResponse<String> response = getSubtask(2);

        Subtask responseTask = gson.fromJson(response.body(), Subtask.class);

        boolean taskFound = taskEquals(responseTask, subtask);
        assertTrue(taskFound, "Задача не найдена в ответе GET-запроса");
        assertEquals(200, response.statusCode());
    }

    @Test
    public void checkAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("эпик", "описание эпика", Status.NEW);
        Subtask subtask = new Subtask("Задача 1", "Описание задачи2", Status.NEW, 1);
        addEpic(epic);
        HttpResponse<String> responseAdd = addSubtask(subtask);
        HttpResponse<String> response = getSubtasks();

        Subtask[] subtasks = gson.fromJson(response.body(), Subtask[].class);

        boolean taskFound = Arrays.stream(subtasks).anyMatch(s -> taskEquals(s, subtask));

        assertTrue(taskFound, "Задача не найдена в ответе GET-запроса");
        assertEquals(1, taskManager.getSubtasks().size(), "Некорректное добавление задачи");
        assertEquals(201, responseAdd.statusCode());
    }

    @Test
    public void checkUpdateTask() throws IOException, InterruptedException {
        Epic epic = new Epic("эпик", "описание эпика", Status.NEW);
        Subtask subtask = new Subtask("Задача 1", "Описание задачи2", Status.NEW, 1);
        addEpic(epic);
        addSubtask(subtask);
        subtask = new Subtask("измененный заголовок", "Описание задачи2", Status.NEW, 1, 2);
        HttpResponse<String> responseUpdate = updateSubtask(subtask);
        HttpResponse<String> response = getSubtasks();

        Subtask[] subtasks = gson.fromJson(response.body(), Subtask[].class);

        Subtask subtask1 = subtask;
        boolean taskFound = Arrays.stream(subtasks).anyMatch(s -> taskEquals(s, subtask1));

        assertTrue(taskFound, "Некорректное обновление задачи");
        assertEquals(1, taskManager.getSubtasks().size(), "Некорректное количество задач");
        assertEquals(201, responseUpdate.statusCode());
    }

    @Test
    public void checkDeleteTask() throws IOException, InterruptedException {
        Epic epic = new Epic("эпик", "описание эпика", Status.NEW);
        Subtask subtask = new Subtask("Задача 1", "Описание задачи1", Status.NEW, 1);
        addEpic(epic);
        addSubtask(subtask);
        HttpResponse<String> response = deleteSubtask(2);

        assertTrue(taskManager.getSubtasks().isEmpty(), "Некорректное удаление задачи");
        assertEquals(200, response.statusCode());
    }

    @Test
    public void chechStatusCode404() throws IOException, InterruptedException {
        Epic epic = new Epic("эпик", "описание эпика", Status.NEW);
        Subtask subtask = new Subtask("Задача 1", "Описание задачи1", Status.NEW, 1);
        addEpic(epic);
        addSubtask(subtask);
        HttpResponse<String> response = getSubtask(3);

        assertEquals(404, response.statusCode());
    }

    @Test
    public void chechStatusCode406() throws IOException, InterruptedException {
        Epic epic = new Epic("эпик", "описание эпика", Status.NEW);
        Subtask subtask1 = new Subtask("Задача 1", "Описание задачи1", Status.NEW, 1, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Задача 2", "Описание задачи2", Status.NEW, 1, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(30));
        addEpic(epic);
        addSubtask(subtask1);
        HttpResponse<String> response = addSubtask(subtask2);

        assertEquals(406, response.statusCode());
    }

}