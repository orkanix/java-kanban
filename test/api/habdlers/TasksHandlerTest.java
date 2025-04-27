package api.habdlers;

import api.ApiTestMethods;
import api.HttpTaskServer;
import manager.Managers;
import manager.task.TaskManager;
import model.Status;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TasksHandlerTest extends ApiTestMethods {

    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);

    TasksHandlerTest() throws IOException {
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
    public void checkGetTasks() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание задачи2", Status.NEW);
        addTask(task);
        HttpResponse<String> response = getTasks();

        Task[] tasks = gson.fromJson(response.body(), Task[].class);

        boolean taskFound = Arrays.stream(tasks).anyMatch(t -> taskEquals(t, task));

        assertTrue(taskFound, "Задача не найдена в ответе GET-запроса");
        assertEquals(200, response.statusCode());
    }

    @Test
    public void checkGetTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание задачи2", Status.NEW);
        addTask(task);
        HttpResponse<String> response = getTask(1);

        Task responseTask = gson.fromJson(response.body(), Task.class);

        boolean taskFound = taskEquals(responseTask, task);
        assertTrue(taskFound, "Задача не найдена в ответе GET-запроса");
        assertEquals(200, response.statusCode());
    }

    @Test
    public void checkAddTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание задачи2", Status.NEW);
        HttpResponse<String> responseAdd = addTask(task);
        HttpResponse<String> response = getTasks();

        Task[] tasks = gson.fromJson(response.body(), Task[].class);

        boolean taskFound = Arrays.stream(tasks).anyMatch(t -> taskEquals(t, task));

        assertTrue(taskFound, "Задача не найдена в ответе GET-запроса");
        assertEquals(1, taskManager.getTasks().size(), "Некорректное добавление задачи");
        assertEquals(201, responseAdd.statusCode());
    }

    @Test
    public void checkUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание задачи2", Status.NEW);
        addTask(task);
        task = new Task("измененный заголовок", "Описание задачи2", Status.NEW, 1);
        HttpResponse<String> responseUpdate = updateTask(task);
        HttpResponse<String> response = getTasks();

        Task[] tasks = gson.fromJson(response.body(), Task[].class);

        Task finalTask = task;
        boolean taskFound = Arrays.stream(tasks).anyMatch(t -> taskEquals(t, finalTask));

        assertTrue(taskFound, "Некорректное обновление задачи");
        assertEquals(1, taskManager.getTasks().size(), "Некорректное количество задач");
        assertEquals(201, responseUpdate.statusCode());
    }

    @Test
    public void checkDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание задачи2", Status.NEW);
        addTask(task);
        HttpResponse<String> response = deleteTask(1);

        assertTrue(taskManager.getTasks().isEmpty(), "Некорректное удаление задачи");
        assertEquals(200, response.statusCode());
    }

    @Test
    public void chechStatusCode404() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание задачи1", Status.NEW);
        addTask(task);
        HttpResponse<String> response = getTask(2);

        assertEquals(404, response.statusCode());
    }

    @Test
    public void chechStatusCode406() throws IOException, InterruptedException {
        Task task1 = new Task("Заголовок", "Описание задачи", Status.NEW, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(30));
        Task task2 = new Task("Заголовок", "Описание задачи", Status.IN_PROGRESS, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(30));
        addTask(task1);
        HttpResponse<String> response = addTask(task2);

        assertEquals(406, response.statusCode());
    }
}