package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EpicTest {

    Epic epic1;
    Subtask subtask1;

    @BeforeEach
    public void beforeEach() {
        epic1 = new Epic("Эпик", "Описание эпика", Status.NEW, 1);
        subtask1 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1);
    }

    @Test
    public void correctAdditionOfSubtask() {
        epic1.addSubtask(subtask1);
        assertTrue(epic1.getId() == subtask1.getEpicId());
    }

    @Test
    public void correctRemovalOfSubtask() {
        epic1.addSubtask(subtask1);
        assertTrue(epic1.getId() == subtask1.getEpicId());

        epic1.deleteSubtask(subtask1);
        assertFalse(epic1.getSubtasksId().contains(subtask1.getId()));
    }
}