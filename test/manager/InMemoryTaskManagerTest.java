package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
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
        Subtask subtask1 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1);

        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);

        assertEquals(subtask1, taskManager.getEpicSubtasks(epic1.getId()).getFirst(), "Некорректное получание сабтаска!");
    }

    @Test
    public void correctUpdateEpic() {
        Epic epic1 = new Epic("Эпик", "Описание эпика", Status.NEW, 1);
        Epic changedEpic1 = new Epic("Измененный эпик", "Описание эпика", Status.NEW, 1);
        Subtask subtask1 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1);

        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);
        taskManager.updateEpic(changedEpic1);

        assertEquals("Измененный эпик", taskManager.getEpic(1).getName(), "Некорректное изменение эпика!");
    }

    @Test
    public void correctDeleteEpic() {
        Epic epic1 = new Epic("Эпик", "Описание эпика", Status.NEW, 1);
        Subtask subtask1 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1);

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

        Subtask subtask1 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1);
        Subtask subtask21 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic2);
        Subtask subtask22 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic2);

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
    public void correctEpicChangeStatusToInProgress() {
        Epic epic1 = new Epic("Эпик", "Описание эпика", Status.NEW, 1);
        Subtask subtask11 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1);
        Subtask subtask12 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1);

        epic1.addSubtask(subtask11);
        epic1.addSubtask(subtask12);

        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask11);
        taskManager.addNewSubtask(subtask12);
        taskManager.updateSubtask(new Subtask("обновленный сабтаск 1 для эпика 1", "описание сабтаска", Status.DONE, epic1), 2);
        assertEquals(Status.IN_PROGRESS, epic1.getStatus(), "Неверно определен статус у эпика!");
    }

    @Test
    public void correctEpicChangeStatusToDone() {
        Epic epic1 = new Epic("Эпик", "Описание эпика", Status.NEW, 1);
        Subtask subtask11 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1);
        Subtask subtask12 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1);

        epic1.addSubtask(subtask11);
        epic1.addSubtask(subtask12);

        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask11);
        taskManager.addNewSubtask(subtask12);
        taskManager.updateSubtask(new Subtask("обновленный сабтаск 1 для эпика 1", "описание сабтаска", Status.DONE, epic1), 2);
        taskManager.updateSubtask(new Subtask("обновленный сабтаск 2 для эпика 1", "описание сабтаска", Status.DONE, epic1), 3);
        assertEquals(Status.DONE, epic1.getStatus(), "Неверно определен статус у эпика!");
    }
}