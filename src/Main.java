import manager.Managers;
import manager.history.HistoryManager;
import manager.task.TaskManager;
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

        Epic epic1 = new Epic("Эпик", "Описание эпика", Status.NEW);
        Epic epic2 = new Epic("Эпик", "Описание эпика", Status.NEW);

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1);
        Subtask subtask2 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1);
        Subtask subtask3 = new Subtask("Сабтаск", "Описание сабтаска", Status.NEW, epic1);

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        System.out.println("-------Сохраненный порядок истории------");
        taskManager.getEpic(3);
        taskManager.getTask(1);
        taskManager.getEpic(4);
        taskManager.getSubtask(6);
        taskManager.getSubtask(5);
        taskManager.getSubtask(7);
        System.out.println(taskManager.getHistory());
        System.out.println("----------------------------------------");
        System.out.println();
        System.out.println("-------История после удаления эпика------");
        taskManager.deleteEpic(epic1);
        System.out.println(taskManager.getHistory());
        System.out.println("------------------------------------------");
    }

}
