package model;

import static org.junit.jupiter.api.Assertions.*;

import manager.Managers;
import manager.task.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class EpicTest {

    Epic epic1;
    Subtask subtask1;

    @BeforeEach
    public void beforeEach() {
        epic1 = new Epic("Эпик", "Описание эпика", Status.NEW, 1);
        subtask1 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1.getId(), 1, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(10));
    }

    @Test
    public void correctAdditionOfSubtask() {
        epic1.addSubtask(subtask1);
        assertEquals(epic1.getId(), subtask1.getEpicId());
    }

    @Test
    public void correctRemovalOfSubtask() {
        epic1.addSubtask(subtask1);
        assertEquals(epic1.getId(), subtask1.getEpicId());

        epic1.deleteSubtask(subtask1);
        assertFalse(epic1.getSubtasksId().containsKey(subtask1.getId()));
    }

    @Test
    public void checkCorrectStartTimeAndEndTime() {
        Subtask subtask2 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1.getId(), 2, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(30));
        epic1.addSubtask(subtask1);
        epic1.addSubtask(subtask2);
        System.out.println(epic1);
    }

    @Test
    public void checkTheEpicStatusWhenAllTasksAreNEW() {
        TaskManager taskManager = Managers.getDefault();
        taskManager.addNewEpic(epic1);

        Subtask subtask2 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1.getId(), 1, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(10));

        taskManager.addNewSubtask(subtask2);
        epic1.addSubtask(subtask1);
        epic1.addSubtask(subtask2);

        assertEquals(Status.NEW, epic1.getStatus());
    }

    @Test
    public void checkTheEpicStatusWhenAllTasksAreDONE() {
        TaskManager taskManager = Managers.getDefault();
        taskManager.addNewEpic(epic1);

        Subtask subtask2 = new Subtask("Сабтаск", "Описание сабтаска", Status.DONE, epic1.getId(), 1, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(10));
        Subtask subtask3 = new Subtask("Сабтаск", "Описание сабтаска", Status.DONE, epic1.getId(), 1, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(10));

        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);
        epic1.addSubtask(subtask2);
        epic1.addSubtask(subtask3);

        assertEquals(Status.DONE, epic1.getStatus());
    }

    @Test
    public void checkTheEpicStatusWhenAllTasksAreINPROGRESS() {
        TaskManager taskManager = Managers.getDefault();
        taskManager.addNewEpic(epic1);

        Subtask subtask2 = new Subtask("Сабтаск", "Описание сабтаска", Status.IN_PROGRESS, epic1.getId(), 1, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(10));
        Subtask subtask3 = new Subtask("Сабтаск", "Описание сабтаска", Status.IN_PROGRESS, epic1.getId(), 1, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(10));

        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);
        epic1.addSubtask(subtask2);
        epic1.addSubtask(subtask3);

        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }

    @Test
    public void checkTheEpicStatusWhenAllTasksAreNEWandDONE() {
        TaskManager taskManager = Managers.getDefault();
        taskManager.addNewEpic(epic1);

        Subtask subtask2 = new Subtask("Сабтаск", "Описание сабтаска", Status.DONE, epic1.getId(), 1, LocalDateTime.of(2020, 1, 20, 10, 55), Duration.ofMinutes(10));

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        epic1.addSubtask(subtask1);
        epic1.addSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }

}