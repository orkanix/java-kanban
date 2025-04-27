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

class PrioritizedHandlerTest extends ApiTestMethods {

    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);

    PrioritizedHandlerTest() throws IOException {
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
    public void checkGetPrioritized() throws IOException, InterruptedException {
        Task task1 = new Task("Заголовок 1", "Описание задачи 1", Status.NEW, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(30));
        Task task2 = new Task("Заголовок 2", "Описание задачи 2", Status.NEW, LocalDateTime.of(2021, 1, 20, 10, 55), Duration.ofMinutes(30));
        addTask(task1);
        addTask(task2);
        getTask(1);
        getTask(2);
        HttpResponse<String> response = getPrioritized();

        Task[] tasks = gson.fromJson(response.body(), Task[].class);

        System.out.println(taskManager.getPrioritizedTasks());

        boolean taskFound = Arrays.stream(tasks)
                .allMatch(task ->
                        Arrays.stream(tasks)
                                .anyMatch(t -> taskEquals(t, task))
                );

        assertTrue(taskFound, "Задача не найдена в ответе GET-запроса");
        assertEquals(2, taskManager.getPrioritizedTasks().size(), "Неверное количество записей в истории");
        assertEquals(200, response.statusCode());
    }

}