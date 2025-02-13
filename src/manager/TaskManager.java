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
        Epic tempEpic = getEpic(subtask.getEpicId());

        if (tempEpic == null) {
            System.out.println("Эпика с id " + subtask.getEpicId() + " для подзадачи не существует");
            return -1;
        }

        subtask.setId(id);
        subtasks.put(id, subtask);
        tempEpic.getSubtasksId().add(subtask.getId());
        checkStatus(tempEpic);
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
            Epic epicInMemory = epics.get(epic.getId());
            epicInMemory.setName(epic.getName());
            epicInMemory.setDescription(epic.getDescription());
            epics.put(epic.getId(), epicInMemory);

            System.out.println("Эпик с id " + epic.getId() + " обновлен.");
            return;
        }

        System.out.println("Эпик с id " + epic.getId() + " не найден.");
    }

    public void updateSubtask(Subtask subtask, int id) {
        if (subtasks.containsKey(id)) {
            subtask.setId(id);
            subtasks.put(id, subtask);
            checkStatus(getEpic(subtask.getEpicId()));
            System.out.println("Подзадача с id " + id + " обновлена.");
            System.out.println(subtasks);
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
            for (Integer key : epic.getSubtasksId()) {
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
            checkStatus(getEpic(subtask.getEpicId()));
            return;
        }

        System.out.println("Подзадача с id " + subtask.getId() + " не найдена.");
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasksId().clear();
            checkStatus(epic);
        }
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
