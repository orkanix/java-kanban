package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;

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
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> tempList = new ArrayList<>();

        if (epics.containsKey(epicId)) {
            for (Integer id : getEpic(epicId).getSubtasksId()) {
                tempList.add(subtasks.get(id));
            }
        }

        if (tempList.isEmpty()) {
            System.out.println("Подзадачи для эпика с id " + epicId + " не найдены.");
            return new ArrayList<>();
        }

        return tempList;
    }

    public int addNewTask(Task task) {
        task.setId(id);
        tasks.put(id, task);
        id++;

        return id - 1;
    }

    public int addNewEpic(Epic epic) {
        epic.setId(id);
        epics.put(id, epic);
        id++;

        return id - 1;
    }

    public int addNewSubtask(Subtask subtask) {
        if (getEpic(subtask.getEpicId()) == null) {
            System.out.println("Эпика с id " + subtask.getEpicId() + " для подзадачи не существует");
            return -1;
        }

        subtask.setId(id);
        subtasks.put(id, subtask);
        getEpic(subtask.getEpicId()).getSubtasksId().add(subtask.getId());
        checkStatus(getEpic(subtask.getEpicId()));
        id++;

        return id - 1;
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            System.out.println("Задача с id " + task.getId() + " обновлена.");
            return;
        }

        System.out.println("Задача с id " + task.getId() + " не найдена.");
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            System.out.println("Эпик с id " + epic.getId() + " обновлен.");
            return;
        }

        System.out.println("Эпик с id " + epic.getId() + " не найден.");
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            checkStatus(getEpic(subtask.getEpicId()));
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
        ArrayList<Integer> keysToRemove = new ArrayList<>();
        if (epics.containsKey(epic.getId())) {
            for (Integer i : subtasks.keySet()) {
                if (epic.getSubtasksId().contains(i)) {
                    keysToRemove.add(i);
                }
            }

            for (Integer key : keysToRemove) {
                subtasks.remove(key);
            }

            epics.remove(epic.getId());
            System.out.println("Эпик с id " + epic.getId() + ", а также его подзадачи удалены.");
            return;
        }

        System.out.println("Эпик с id " + epic.getId() + " не найден.");
    }

    public void deleteSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            getEpic(subtask.getEpicId()).deleteSubtask(subtask);
            subtasks.remove(subtask.getId());
            System.out.println("Подзадача с id " + subtask.getId() + " удалена.");
            checkStatus(getEpic(subtask.getEpicId()));
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
        subtasks.clear();
        System.out.println("Все эпики удалены");
    }

    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasksId().clear();
            checkStatus(epic);
        }
        System.out.println("Все подзадачи удалены.");
    }

    public void setStatus(Subtask subtask, Status status) {
        subtask.setStatus(status);
        checkStatus(getEpic(subtask.getEpicId()));
        System.out.println("Успешная смена статуса на: " + status);
    }



    private void checkStatus(Epic epic) {
        ArrayList<Subtask> tempList = getEpicSubtasks(epic.getId());

        if (tempList.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Subtask subtask : tempList) {
            if (subtask != null) {
                if (subtask.getStatus() != Status.NEW) {
                    allNew = false;
                }

                if (subtask.getStatus() != Status.DONE) {
                    allDone = false;
                }
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

        System.out.println("Текущий статус эпика " + epic.getName() + " : " + epic.getStatus());
    }

}
