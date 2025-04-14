package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class TaskTest {

    @Test
    public void objectsMustBeEqualToEachOther() {
        Task task1 = new Task("Заголовок", "Описание задачи", Status.NEW, 1);
        Task task2 = new Task("Заголовок", "Описание задачи", Status.NEW, 1);

        assertEquals(task1, task2, "Задачи не равны!");
    }

    @Test
    public void successorObjectsMustBeEqualToEachOther() {
        Epic epic1 = new Epic("Эпик", "Описание эпика", Status.NEW, 2);
        Epic epic2 = new Epic("Эпик", "Описание эпика", Status.NEW, 2);

        assertEquals(epic1, epic2, "Эпики не равны!");

        Subtask subtask1 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1);
        Subtask subtask2 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic2);

        assertEquals(subtask1, subtask2, "Сабтаски не равны!");
    }

    @Test
    public void checkCorrectEndTime() {
        Task task1 = new Task("Заголовок", "Описание задачи", Status.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(10));
        System.out.println(task1);
    }
}