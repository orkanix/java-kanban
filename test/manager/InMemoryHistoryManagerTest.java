package manager;

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
        Task task2 = new Task("Заголовок", "Описание задачи", Status.NEW, 2);

        historyManager.add(task1);
        historyManager.add(task2);

        assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    public void replacingThe10ValueWhenTheSheetOverflows() {
        Task task1 = new Task("Заголовок", "Описание задачи", Status.NEW, 1);
        Task task2 = new Task("Заголовок", "Описание задачи", Status.NEW, 2);
        Task task3 = new Task("Заголовок", "Описание задачи", Status.NEW, 3);
        Task task4 = new Task("Заголовок", "Описание задачи", Status.NEW, 4);
        Task task5 = new Task("Заголовок", "Описание задачи", Status.NEW, 5);
        Task task6 = new Task("Заголовок", "Описание задачи", Status.NEW, 6);
        Task task7 = new Task("Заголовок", "Описание задачи", Status.NEW, 7);
        Task task8 = new Task("Заголовок", "Описание задачи", Status.NEW, 8);
        Task task9 = new Task("Заголовок", "Описание задачи", Status.NEW, 9);
        Task task10 = new Task("Заголовок", "Описание задачи", Status.NEW, 10);
        Task task11 = new Task("Заголовок", "Описание задачи", Status.NEW, 11);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task5);
        historyManager.add(task6);
        historyManager.add(task7);
        historyManager.add(task8);
        historyManager.add(task9);
        historyManager.add(task10);

        assertNotNull(historyManager.getHistory().getFirst(), "Задача в списке не существует!");
        assertEquals(task1, historyManager.getHistory().getFirst(), "Некорректная задача под индексом 1!");

        historyManager.add(task11);

        assertEquals(task2, historyManager.getHistory().getFirst(), "Некорректная задача под индексом 1!");
    }
}