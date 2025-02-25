import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("задача 1", "описание задачи 1", Status.NEW);
        Task task2 = new Task("задача 2", "описание задачи 2", Status.NEW);

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        Epic epic1 = new Epic("эпик 1", "описание эпика 1", Status.NEW);
        Epic epic2 = new Epic("эпик 2", "описание эпика 2", Status.NEW);

        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);


        Subtask subtask1FE1 = new Subtask("подзадача 1 для эпика 1", "описание подзадачи", Status.NEW, epic1);
        Subtask subtask2FE1 = new Subtask("подзадача 2 для эпика 1", "описание подзадачи", Status.NEW, epic1);
        Subtask subtask1FE2 = new Subtask("подзадача 1 для эпика 2", "описание подзадачи", Status.NEW, epic2);

        taskManager.addNewSubtask(subtask1FE1);
        taskManager.addNewSubtask(subtask2FE1);
        taskManager.addNewSubtask(subtask1FE2);

        System.out.println();
        System.out.println("Эпики и сабтаски ДО изменения статусов:");
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        taskManager.updateSubtask(new Subtask("обновленный сабтаск 1 для эпика 1", "описание сабтаска", Status.DONE, epic1), 5);

        System.out.println();
        System.out.println(subtask1FE1.getStatus());

        System.out.println();
        System.out.println("Эпики и сабтаски ПОСЛЕ изменения статусов:");
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        taskManager.deleteTask(task1);
        taskManager.deleteEpic(epic1);

        taskManager.getTasks();
        taskManager.getEpics();

        System.out.println();
        System.out.println("Задачи ПОСЛЕ удаления:");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        System.out.println();
        System.out.println("Статус эпика ДО удаления подзадачи:");
        System.out.println(epic1.getStatus());

        taskManager.deleteSubtask(subtask1FE1);

        System.out.println("Статус эпика ПОСЛЕ удаления подзадачи:");
        System.out.println(epic1.getStatus());

        taskManager.deleteSubtasks();

        System.out.println(taskManager.getHistory());
    }

}
