package manager;

import manager.task.InMemoryTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return (InMemoryTaskManager) Managers.getDefault();
    }

    @Test
    public void correctEpicChangeStatusToInProgress() {
        Epic epic1 = new Epic("Эпик", "Описание эпика", Status.NEW, 1);

        Subtask subtask11 = new Subtask("Сабтаск", "Описание сабтаска", Status.IN_PROGRESS, epic1.getId(), 2, LocalDateTime.of(2019, 1, 20, 10, 55), Duration.ofMinutes(10));
        Subtask subtask12 = new Subtask("Сабтаск", "Описание сабтаска", Status.IN_PROGRESS, epic1.getId(), 3, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(10));

        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask11);
        taskManager.addNewSubtask(subtask12);
        taskManager.updateSubtask((new Subtask("обновленный сабтаск 1 для эпика 1", "описание сабтаска", Status.DONE, epic1.getId(), 2, LocalDateTime.of(2021, 1, 20, 10, 55), Duration.ofMinutes(10))), 2);

        System.out.println(taskManager.getEpics());
        assertEquals(Status.IN_PROGRESS, epic1.getStatus(), "Неверно определен статус у эпика!");
    }

    @Test
    public void correctEpicChangeStatusToDone() {
        Epic epic1 = new Epic("Эпик", "Описание эпика", Status.NEW, 1);

        Subtask subtask11 = new Subtask("Сабтаск", "Описание сабтаска", Status.IN_PROGRESS, epic1.getId(), 2, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(10));
        Subtask subtask12 = new Subtask("Сабтаск", "Описание сабтаска", Status.IN_PROGRESS, epic1.getId(), 3, LocalDateTime.of(2019, 1, 20, 10, 55), Duration.ofMinutes(10));

        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask11);
        taskManager.addNewSubtask(subtask12);
        taskManager.updateSubtask(new Subtask("обновленный сабтаск 1 для эпика 1", "описание сабтаска", Status.DONE, epic1), 2);
        taskManager.updateSubtask(new Subtask("обновленный сабтаск 2 для эпика 1", "описание сабтаска", Status.DONE, epic1), 3);
        System.out.println(taskManager.getSubtasks());
        assertEquals(Status.DONE, epic1.getStatus(), "Неверно определен статус у эпика!");
    }

    @Test
    public void clearingTheIDsOfDeletedSubtasks() {
        Epic epic1 = new Epic("Эпик", "Описание эпика", Status.NEW, 1);
        Subtask subtask1 = new Subtask("Сабтаск", "Описание сабтаска", Status.IN_PROGRESS, epic1.getId(), 3, LocalDateTime.of(2019, 1, 20, 10, 55), Duration.ofMinutes(10));
        Subtask subtask2 = new Subtask("Сабтаск", "Описание сабтаска", Status.IN_PROGRESS, epic1.getId(), 4, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(10));

        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        taskManager.deleteSubtask(subtask1);

        assertEquals(-1, subtask1.getId(), "ID подзадачи не очищен!");
        assertEquals(-1, subtask1.getEpicId(), "ID эпика не очищен!");
    }

    @Test
    public void cleaningUpOutdatedEpicSubtasks() {
        Epic epic1 = new Epic("Эпик", "Описание эпика", Status.NEW, 1);
        Subtask subtask1 = new Subtask("Сабтаск", "Описание сабтаска", Status.IN_PROGRESS, epic1.getId(), 10, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(10));
        Subtask subtask2 = new Subtask("Сабтаск", "Описание сабтаска", Status.IN_PROGRESS, epic1.getId(), 11, LocalDateTime.of(2019, 1, 20, 10, 55), Duration.ofMinutes(10));

        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assertEquals(2, epic1.getSubtasksId().size(), "Некорректное добавление элементов в список подзадач.");

        taskManager.deleteSubtask(subtask1);

        assertEquals(1, epic1.getSubtasksId().size(), "Некорректная очистка неактуальных элементов в списке подзадач.");
    }

    @Test
    public void checkDeleteTasksInHistory() {
        Task task1 = new Task("Заголовок", "Описание задачи", Status.NEW);
        Task task2 = new Task("Заголовок", "Описание задачи", Status.NEW);
        Task task3 = new Task("Заголовок", "Описание задачи", Status.NEW);
        Epic epic1 = new Epic("Эпик", "Описание эпика", Status.NEW);

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);
        taskManager.addNewEpic(epic1);

        Subtask subtask1 = new Subtask("Сабтаск", "Описание сабтаска", Status.IN_PROGRESS, epic1.getId(), 10, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(10));
        Subtask subtask2 = new Subtask("Сабтаск", "Описание сабтаска", Status.IN_PROGRESS, epic1.getId(), 11, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(10));

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getTask(3);
        taskManager.getEpic(4);
        taskManager.getSubtask(5);
        taskManager.getSubtask(6);

        taskManager.deleteEpics();

        assertFalse(taskManager.getHistory().contains(subtask2));
        assertTrue(taskManager.getHistory().contains(task1));
    }
}