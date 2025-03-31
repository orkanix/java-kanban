package manager;

import manager.exceptions.ManagerSaveException;
import manager.task.FileBackedTaskManager;
import manager.task.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManagerTest {

    File tempFile;
    TaskManager taskManager;

    @BeforeEach
    public void beforeEach() throws IOException {
        tempFile = File.createTempFile("taskManagerData", "csv");
        taskManager = new FileBackedTaskManager(tempFile.getAbsolutePath());
    }

    @Test
    public void checkAddEmptyToFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(tempFile))) {
            List<String> tasks = new ArrayList<>();
            int counter = 0;
            while (br.ready()) {
                tasks.add(br.readLine());
                counter++;
            }
            assertEquals(counter, tasks.size(), "Неверное количество сохраненных задач.");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла!", e);
        }
    }

    @Test
    public void checkAddNewTasks() {
        Task task1 = new Task("Заголовок", "Описание задачи", Status.NEW);
        Task task2 = new Task("Заголовок", "Описание задачи", Status.IN_PROGRESS);
        Task task3 = new Task("Заголовок", "Описание задачи", Status.NEW);
        Epic epic1 = new Epic("Эпик", "Описание эпика", Status.NEW);

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);
        taskManager.addNewEpic(epic1);

        Subtask subtask1 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1);
        Subtask subtask2 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1);

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        try (BufferedReader br = new BufferedReader(new FileReader(tempFile))) {
            List<String> tasks = new ArrayList<>();
            int counter = 0;
            while (br.ready()) {
                tasks.add(br.readLine());
                counter++;
            }
            assertEquals(counter, tasks.size(), "Неверное количество сохраненных задач.");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла!", e);
        }
    }

    @Test
    public void checkEmptyLoadData() {
        TaskManager otherTaskManager = new FileBackedTaskManager(tempFile.getAbsolutePath());
        assertEquals(0, otherTaskManager.getTasks().size());
        assertEquals(0, otherTaskManager.getEpics().size());
        assertEquals(0, otherTaskManager.getSubtasks().size());
    }

    @Test
    public void checkLoadData() {
        Task task1 = new Task("Заголовок", "Описание задачи", Status.NEW);
        Task task2 = new Task("Заголовок", "Описание задачи", Status.IN_PROGRESS);
        Task task3 = new Task("Заголовок", "Описание задачи", Status.NEW);
        Epic epic1 = new Epic("Эпик", "Описание эпика", Status.NEW);

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);
        taskManager.addNewEpic(epic1);

        Subtask subtask1 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1);
        Subtask subtask2 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1);

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        //подгружаем задачи из файла
        TaskManager otherTaskManager = new FileBackedTaskManager(tempFile.getAbsolutePath());
        assertEquals(3, otherTaskManager.getTasks().size());
        assertEquals(1, otherTaskManager.getEpics().size());
        assertEquals(2, otherTaskManager.getSubtasks().size());

        assertEquals(new Task("Заголовок", "Описание задачи", Status.NEW, 1), otherTaskManager.getTask(1));
    }
}
