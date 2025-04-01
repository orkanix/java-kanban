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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTaskManagerTest {

    File tempFile;

    @BeforeEach
    public void beforeEach() throws IOException {
        tempFile = File.createTempFile("taskManagerData", "csv");
        tempFile = new File("/Users/dmitrsol/Desktop/taskManagerData.csv");
    }

    @Test
    public void checkAddEmptyToFile() throws IOException {
        TaskManager taskManager = new FileBackedTaskManager(tempFile);
        Task task1 = new Task("Заголовок", "Описание задачи", Status.NEW);

        taskManager.addNewTask(task1);
        taskManager.deleteTask(task1);

        //осталась только шапка
        assertEquals(37, Files.size(tempFile.toPath()), "Файл должен быть пустым после удаления всех задач.");
    }

    @Test
    public void checkAddNewTasks() {
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(tempFile);
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
        TaskManager otherTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(0, otherTaskManager.getTasks().size());
        assertEquals(0, otherTaskManager.getEpics().size());
        assertEquals(0, otherTaskManager.getSubtasks().size());
    }

    @Test
    public void checkLoadData() {
        TaskManager saveTaskManager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Заголовок", "Описание задачи", Status.NEW);
        Task task2 = new Task("Заголовок", "Описание задачи", Status.IN_PROGRESS);
        Task task3 = new Task("Заголовок", "Описание задачи", Status.NEW);
        Epic epic1 = new Epic("Эпик", "Описание эпика", Status.NEW);

        saveTaskManager.addNewTask(task1);
        saveTaskManager.addNewTask(task2);
        saveTaskManager.addNewTask(task3);
        saveTaskManager.addNewEpic(epic1);

        Subtask subtask1 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1);
        Subtask subtask2 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1);

        saveTaskManager.addNewSubtask(subtask1);
        saveTaskManager.addNewSubtask(subtask2);

        TaskManager loadTaskManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(saveTaskManager.getTask(1).getId(), loadTaskManager.getTask(1).getId());
        assertEquals(saveTaskManager.getTask(1).getName(), loadTaskManager.getTask(1).getName());
        assertEquals(saveTaskManager.getTask(1).getDescription(), loadTaskManager.getTask(1).getDescription());
        assertEquals(saveTaskManager.getTask(1).getStatus(), loadTaskManager.getTask(1).getStatus());

        assertEquals(saveTaskManager.getEpic(4).getId(), loadTaskManager.getEpic(4).getId());
        assertEquals(saveTaskManager.getEpic(4).getName(), loadTaskManager.getEpic(4).getName());
        assertEquals(saveTaskManager.getEpic(4).getDescription(), loadTaskManager.getEpic(4).getDescription());
        assertEquals(saveTaskManager.getEpic(4).getStatus(), loadTaskManager.getEpic(4).getStatus());
        assertEquals(saveTaskManager.getEpic(4).getSubtasksId(), loadTaskManager.getEpic(4).getSubtasksId());

        assertEquals(saveTaskManager.getSubtask(5).getId(), loadTaskManager.getSubtask(5).getId());
        assertEquals(saveTaskManager.getSubtask(5).getName(), loadTaskManager.getSubtask(5).getName());
        assertEquals(saveTaskManager.getSubtask(5).getDescription(), loadTaskManager.getSubtask(5).getDescription());
        assertEquals(saveTaskManager.getSubtask(5).getStatus(), loadTaskManager.getSubtask(5).getStatus());
        assertEquals(saveTaskManager.getSubtask(5).getEpicId(), loadTaskManager.getSubtask(5).getEpicId());
    }
}
