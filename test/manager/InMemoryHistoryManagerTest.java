package manager;

import manager.history.HistoryManager;
import manager.task.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void checkEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    public void addDoplicateTask() {
        Task task1 = new Task("Заголовок", "Описание задачи", Status.NEW, 1);
        Task task2 = new Task("Заголовок", "Описание задачи", Status.NEW, 1);
        Task task3 = new Task("Заголовок", "Описание задачи", Status.NEW, 3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    public void addTheStoryToTheList() {
        Task task1 = new Task("Заголовок", "Описание задачи", Status.NEW, 1);
        Task task2 = new Task("Заголовок", "Описание задачи", Status.NEW, 2);
        Task task3 = new Task("Заголовок", "Описание задачи", Status.NEW, 3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        assertEquals(3, historyManager.getHistory().size());
    }

    @Test
    public void removeTheFirstStoryOfTheList() {
        Task task1 = new Task("Заголовок", "Описание задачи", Status.NEW, 1);
        Task task2 = new Task("Заголовок", "Описание задачи", Status.NEW, 2);
        Task task3 = new Task("Заголовок", "Описание задачи", Status.NEW, 3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(1);

        assertEquals(2, historyManager.getHistory().size());
        assertFalse(historyManager.getHistory().contains(task1));
        assertTrue(historyManager.getHistory().contains(task2));
        assertTrue(historyManager.getHistory().contains(task3));
    }

    @Test
    public void removeTheMiddleStoryOfTheList() {
        Task task1 = new Task("Заголовок", "Описание задачи", Status.NEW, 1);
        Task task2 = new Task("Заголовок", "Описание задачи", Status.NEW, 2);
        Task task3 = new Task("Заголовок", "Описание задачи", Status.NEW, 3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2);

        assertEquals(2, historyManager.getHistory().size());
        assertTrue(historyManager.getHistory().contains(task1));
        assertFalse(historyManager.getHistory().contains(task2));
        assertTrue(historyManager.getHistory().contains(task3));
    }

    @Test
    public void removeTheLastStoryOfTheList() {
        Task task1 = new Task("Заголовок", "Описание задачи", Status.NEW, 1);
        Task task2 = new Task("Заголовок", "Описание задачи", Status.NEW, 2);
        Task task3 = new Task("Заголовок", "Описание задачи", Status.NEW, 3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(3);

        assertEquals(2, historyManager.getHistory().size());
        assertTrue(historyManager.getHistory().contains(task1));
        assertTrue(historyManager.getHistory().contains(task2));
        assertFalse(historyManager.getHistory().contains(task3));
    }

    @Test
    public void test() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик", "Описание эпика", Status.NEW, 1);
        Subtask subtask1 = new Subtask("Сабтаск", "Описание сабтаска", Status.IN_PROGRESS, epic1.getId(), 2, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(10));

        taskManager.addNewEpic(epic1);
        System.out.println(taskManager.getEpics());
        taskManager.addNewSubtask(subtask1);

        assertEquals(subtask1, taskManager.getEpicSubtasks(epic1.getId()).getFirst(), "Некорректное получание сабтаска!");
    }
}