package manager.task;

import manager.Managers;
import manager.history.HistoryManager;
import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private int id = 1;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTask(int id) {
        if (tasks.get(id) != null) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        }
        return null;
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.get(id) != null) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        }
        return null;
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subtasks.get(id) != null) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        }
        return null;

    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> tempList = new ArrayList<>();

        if (epics.containsKey(epicId)) {
            for (Integer id : getEpic(epicId).getSubtasksId()) {
                tempList.add(subtasks.get(id));
            }
        }

        return tempList;
    }

    @Override
    public int addNewTask(Task task) {
        if (task.getId() == 0) {
            task.setId(id);
            tasks.put(id, task);
            id++;
            return id - 1;
        }
        tasks.put(task.getId(), task);
        id = task.getId() + 1;
        return task.getId();
    }

    @Override
    public int addNewEpic(Epic epic) {
        if (epic.getId() == 0) {
            epic.setId(id);
            epics.put(id, epic);
            id++;
            return id - 1;
        }
        epics.put(epic.getId(), epic);
        id = epic.getId() + 1;
        return epic.getId();
    }

    @Override
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
        historyManager.remove(subtask.getEpicId());

        return id - 1;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            System.out.println("Задача с id " + task.getId() + " обновлена.");
            return;
        }

        System.out.println("Задача с id " + task.getId() + " не найдена.");
    }

    @Override
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

    @Override
    public void updateSubtask(Subtask subtask, int id) {
        if (subtasks.containsKey(id)) {
            subtask.setId(id);
            subtasks.put(id, subtask);
            checkStatus(getEpic(subtask.getEpicId()));
            System.out.println("Подзадача с id " + id + " обновлена.");
            return;
        }


        System.out.println("Подзадача с id " + subtask.getId() + " не найдена.");
    }


    @Override
    public void deleteTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.remove(task.getId());
            historyManager.remove(task.getId());
            System.out.println("Задача с id " + task.getId() + " удалена.");
            return;
        }

        System.out.println("Задача с id " + task.getId() + " не найдена.");
    }

    @Override
    public void deleteEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            for (Integer key : epic.getSubtasksId()) {
                subtasks.remove(key);
                historyManager.remove(key);
            }

            epics.remove(epic.getId());
            historyManager.remove(epic.getId());
            System.out.println("Эпик с id " + epic.getId() + ", а также его подзадачи удалены.");
            return;
        }

        System.out.println("Эпик с id " + epic.getId() + " не найден.");
    }

    @Override
    public void deleteSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            getEpic(subtask.getEpicId()).deleteSubtask(subtask);
            subtasks.remove(subtask.getId());
            historyManager.remove(subtask.getId());
            checkStatus(getEpic(subtask.getEpicId()));
            subtask.setId(-1);
            subtask.setEpicId(-1);
            return;
        }

        System.out.println("Подзадача с id " + subtask.getId() + " не найдена.");
    }

    @Override
    public void deleteTasks() {
        for (Integer i : tasks.keySet()) {
            historyManager.remove(i);
        }
        tasks.clear();
        System.out.println(historyManager.getHistory());
    }

    @Override
    public void deleteEpics() {
        for (Integer i : epics.keySet()) {
            historyManager.remove(i);
        }
        for (Integer i : subtasks.keySet()) {
            historyManager.remove(i);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Integer i : subtasks.keySet()) {
            historyManager.remove(i);
        }
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
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

        System.out.println("Текущий статус эпика " + epic.getName() + " : " + epic.getStatus());
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
