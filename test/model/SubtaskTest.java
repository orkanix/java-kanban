package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void getEpicId() {
        Epic epic1 = new Epic("Эпик", "Описание эпика", Status.NEW, 1);
        Subtask subtask1 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1);

        assertEquals(epic1.getId(), subtask1.getEpicId());
    }
}