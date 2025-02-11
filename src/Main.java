import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import status.Status;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("задача 1", "описание задачи 1", Status.NEW);
        Task task2 = new Task("задача 2", "описание задачи 2", Status.NEW);

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        Epic epic1 = new Epic("эпик 1", "описание эпика 1", Status.NEW);
        Epic epic2 = new Epic("эпик 2", "описание эпика 2", Status.NEW);

        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        Subtask subtask1FE1 = new Subtask("подзадача 1 для эпика 1", "описание подзадачи", Status.NEW, taskManager.getEpic(3));
        Subtask subtask2FE1 = new Subtask("подзадача 2 для эпика 1", "описание подзадачи", Status.NEW, taskManager.getEpic(3));
        Subtask subtask1FE2 = new Subtask("подзадача 1 для эпика 2", "описание подзадачи", Status.NEW, taskManager.getEpic(4));

        taskManager.addNewSubtask(subtask1FE1);
        taskManager.addNewSubtask(subtask2FE1);
        taskManager.addNewSubtask(subtask1FE2);

        System.out.println();
        System.out.println("Все созданные задачи");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        taskManager.deleteTask(task1);
        taskManager.deleteEpic(epic1);

        taskManager.getTasks();
        taskManager.getEpics();

        System.out.println();
        System.out.println("Задачи после удаления");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
    }

}
