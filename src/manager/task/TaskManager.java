package manager.task;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    List<Subtask> getEpicSubtasks(int epicId);

    int addNewTask(Task task);

    int addNewEpic(Epic epic);

    int addNewSubtask(Subtask subtask);

    void updateTask(Task task, int id);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask, int id);

    void deleteTask(Task task);

    void deleteEpic(Epic epic);

    void deleteSubtask(Subtask subtask);

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
