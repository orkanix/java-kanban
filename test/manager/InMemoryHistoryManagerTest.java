package manager;

import manager.history.HistoryManager;
import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void addTheStoryToTheList() {
        Task task1 = new Task("Заголовок", "Описание задачи", Status.NEW, 1);
        Task task2 = new Task("Заголовок", "Описание задачи", Status.NEW, 1);
        Task task3 = new Task("Заголовок", "Описание задачи", Status.NEW, 2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    public void removeTheStoryOfTheList() {
        Task task1 = new Task("Заголовок", "Описание задачи", Status.NEW, 1);
        Task task2 = new Task("Заголовок", "Описание задачи", Status.NEW, 2);
        Task task3 = new Task("Заголовок", "Описание задачи", Status.NEW, 3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2);

        assertEquals(2, historyManager.getHistory().size());
        assertTrue(historyManager.getHistory().contains(task1));
        assertTrue(historyManager.getHistory().contains(task3));
        assertFalse(historyManager.getHistory().contains(task2));
    }
}