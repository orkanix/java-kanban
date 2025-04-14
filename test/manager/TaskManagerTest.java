package manager;

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

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    public void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    public void correctAddNewTask() {
        Task task1 = new Task("Заголовок", "Описание задачи", Status.NEW, 1);
        taskManager.addNewTask(task1);

        assertEquals(task1, taskManager.getTask(1), "Задача не найдена!");
    }

    @Test
    public void correctGetEpicSubtasks() {
        Epic epic1 = new Epic("Эпик", "Описание эпика", Status.NEW, 1);
        Subtask subtask1 = new Subtask("Сабтаск", "Описание сабтаска", Status.IN_PROGRESS, epic1.getId(), 2, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(10));

        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);

        assertEquals(subtask1, taskManager.getEpicSubtasks(epic1.getId()).getFirst(), "Некорректное получание сабтаска!");
    }

    @Test
    public void correctUpdateEpic() {
        Epic epic1 = new Epic("Эпик", "Описание эпика", Status.NEW, 1);
        Epic changedEpic1 = new Epic("Измененный эпик", "Описание эпика", Status.NEW, 1);
        Subtask subtask1 = new Subtask("Сабтаск", "Описание сабтаска", Status.IN_PROGRESS, epic1.getId(), 3, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(10));

        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);
        taskManager.updateEpic(changedEpic1);

        assertEquals("Измененный эпик", taskManager.getEpic(1).getName(), "Некорректное изменение эпика!");
    }

    @Test
    public void correctDeleteEpic() {
        Epic epic1 = new Epic("Эпик", "Описание эпика", Status.NEW, 1);
        Subtask subtask1 = new Subtask("Сабтаск", "Описание сабтаска", Status.IN_PROGRESS, epic1.getId(), 2, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(10));

        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);
        taskManager.deleteEpic(epic1);

        assertNull(taskManager.getEpic(epic1.getId()), "Эпик не удален!");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Некорректное удаление эпика");
    }

    @Test
    public void correctDeleteAllEpics() {
        Epic epic1 = new Epic("Эпик", "Описание эпика", Status.NEW, 1);
        Epic epic2 = new Epic("Эпик", "Описание эпика", Status.NEW, 2);

        Subtask subtask1 = new Subtask("Сабтаск", "Описание сабтаска", Status.IN_PROGRESS, epic1.getId(), 3, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(10));
        Subtask subtask21 = new Subtask("Сабтаск", "Описание сабтаска", Status.IN_PROGRESS, epic2.getId(), 4, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(10));
        Subtask subtask22 = new Subtask("Сабтаск", "Описание сабтаска", Status.IN_PROGRESS, epic1.getId(), 5, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(10));

        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask21);
        taskManager.addNewSubtask(subtask22);
        taskManager.deleteEpics();

        assertTrue(taskManager.getEpics().isEmpty(), "Эпики не удалены!");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Подзадачи не удалены!");
    }

    @Test
    public void checkCorrectSortPrioritizedTasks() {
        Task task1 = new Task("Заголовок", "Описание задачи", Status.NEW, LocalDateTime.of(2020, 1, 20, 10, 50), Duration.ofMinutes(30));
        Task task2 = new Task("Заголовок", "Описание задачи", Status.IN_PROGRESS, LocalDateTime.of(2021, 1, 20, 10, 55), Duration.ofMinutes(30));
        Task task3 = new Task("Заголовок", "Описание задачи", Status.IN_PROGRESS, LocalDateTime.of(2021, 1, 20, 10, 55), Duration.ofMinutes(30));
        Task task4 = new Task("Заголовок", "Описание задачи", Status.NEW);
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);
        taskManager.addNewTask(task4);

        assertEquals(2, taskManager.getPrioritizedTasks().size());
        assertFalse(taskManager.getPrioritizedTasks().contains(task4));
    }
}
