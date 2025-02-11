package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import status.Status;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private int id = 1;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public ArrayList<Task> getTasks() {
        return new ArrayList<Task>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<Epic>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<Subtask>(subtasks.values());
    }

    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }

        System.out.println("Задача с id " + id + " не найдена.");
        return null;
    }

    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        }

        System.out.println("Эпик с id " + id + " не найден.");
        return null;
    }

    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        }

        System.out.println("Подзадача с id " + id + " не найдена.");
        return null;
    }

    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> tempList = new ArrayList<>();

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                tempList.add(subtask);
            }
        }

        if (tempList.isEmpty()) {
            System.out.println("Подзадачи для эпика с id " + epicId + " не найдены.");
            return new ArrayList<>();
        }

        return tempList;
    }

    public int addNewTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            System.out.println("Задача с id " + task.getId() + " уже существует.");
            return -1;
        }

        tasks.put(id, task);
        task.setId(id);
        id++;

        return id - 1;
    }

    public int addNewEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            System.out.println("Эпик с id " + epic.getId() + " уже существует.");
            return -1;
        }

        epics.put(id, epic);
        epic.setId(id);
        id++;

        return id - 1;
    }

    public int addNewSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            System.out.println("Подзадача с id " + subtask.getId() + " уже существует.");
            return -1;
        }

        if (getEpic(subtask.getEpicId()) == null) {
            System.out.println("Эпика с id " + subtask.getEpicId() + " для подзадачи не существует");
            return -1;
        }

        subtasks.put(id, subtask);
        subtask.setId(id);
        checkStatus(getEpic(subtask.getEpicId()));
        id++;

        return id - 1;
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.remove(task.getId());
            tasks.put(task.getId(), task);
            System.out.println("Задача с id " + task.getId() + " обновлена.");
            return;
        }

        System.out.println("Задача с id " + task.getId() + " не найдена.");
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.remove(epic.getId());
            epics.put(epic.getId(), epic);
            checkStatus(epic);
            System.out.println("Эпик с id " + epic.getId() + " обновлен.");
            return;
        }

        System.out.println("Эпик с id " + epic.getId() + " не найден.");
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.remove(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
            System.out.println("Подзадача с id " + subtask.getId() + " обновлена.");
            return;
        }

        System.out.println("Подзадача с id " + subtask.getId() + " не найдена.");
    }

    public void deleteTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.remove(task.getId());
            System.out.println("Задача с id " + task.getId() + " удалена.");
            return;
        }

        System.out.println("Задача с id " + task.getId() + " не найдена.");
    }

    public void deleteEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            ArrayList<Subtask> tempList = getEpicSubtasks(epic.getId());
            for (Subtask subtask : tempList) {
                subtasks.remove(subtask.getId());
            }

            epics.remove(epic.getId());
            System.out.println("Эпик с id " + epic.getId() + ", а также его подзадачи удалены.");
            return;
        }

        System.out.println("Эпик с id " + epic.getId() + " не найден.");
    }

    public void deleteSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.remove(subtask.getId());
            System.out.println("Подзадача с id " + subtask.getId() + " удалена.");
            return;
        }

        System.out.println("Подзадача с id " + subtask.getId() + " не найдена.");
    }

    public void deleteTasks() {
        tasks.clear();
        System.out.println("Все задачи удалены.");
    }

    public void deleteEpics() {
        epics.clear();
        System.out.println("Все эпики удалены");
        //все подзадачи эпиков удалить
    }

    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            checkStatus(epic);
        }
        System.out.println("Все подзадачи удалены.");
    }



    private void checkStatus(Epic epic) {
        if (getEpicSubtasks(epic.getId()).isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;


        for (Subtask subtask : getEpicSubtasks(epic.getId())) {
            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }

            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            epic.setStatus(Status.NEW);
        }
        else if (allDone) {
            epic.setStatus(Status.DONE);
        }
        else {
            epic.setStatus(Status.IN_PROGRESS);
        }

        System.out.println("Текущий статус эпика: " + epic.getStatus());
    }

}
