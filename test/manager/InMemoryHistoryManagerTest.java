package manager;

import manager.history.HistoryManager;
import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    //нет наследования потому что нет общих методов с TaskManager

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
}