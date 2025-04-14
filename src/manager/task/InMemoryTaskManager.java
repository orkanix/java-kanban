package manager.task;

import manager.Managers;
import manager.history.HistoryManager;
import model.*;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected int id = 1;

    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();

    HistoryManager historyManager = Managers.getDefaultHistory();

    Comparator<Task> taskComparator = new Comparator<Task>() {
        @Override
        public int compare(Task o1, Task o2) {
            if (o1.getStartTime() != null && o2.getStartTime() != null) {
                return o1.getStartTime().compareTo(o2.getStartTime());
            }
            return -1;
        }
    };

    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(taskComparator);

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
        if (!(epics.containsKey(epicId))) return new ArrayList<>();

        return getEpic(epicId).getSubtasksId().keySet().stream().map(id -> subtasks.get(id)).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public int addNewTask(Task task) {
        task.setId(id);
        tasks.put(id, task);
        if (task.getStartTime() != null && task.getEndTime() != null && !(checkOverlayTasks(task, prioritizedTasks))) {
            prioritizedTasks.add(task);
        }
        id++;

        return id - 1;
    }

    @Override
    public int addNewEpic(Epic epic) {
        epic.setId(id);
        epics.put(id, epic);
        if (epic.getStartTime() != null) {
            prioritizedTasks.add(epic);
        }
        id++;

        return id - 1;
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
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
        tempEpic.addSubtask(subtask);
        checkStatus(tempEpic);
        id++;
        historyManager.remove(subtask.getEpicId());

        return id - 1;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {

            if (task.getStartTime() != null) {
                prioritizedTasks.remove(tasks.get(task.getId()));
                prioritizedTasks.add(task);
            }

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

            if (epic.getStartTime() != null) {
                prioritizedTasks.remove(epics.get(epic.getId()));
                prioritizedTasks.add(epic);
            }

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

            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtasks.get(subtask.getId()));
                prioritizedTasks.add(subtask);
            }

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

            if (task.getStartTime() != null) {
                historyManager.remove(task.getId());
                prioritizedTasks.remove(task);
            }

            System.out.println("Задача с id " + task.getId() + " удалена.");
            return;
        }

        System.out.println("Задача с id " + task.getId() + " не найдена.");
    }

    @Override
    public void deleteEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epic.getSubtasksId().keySet().stream()
                    .forEach(key -> {
                        subtasks.remove(key);
                        historyManager.remove(key);
                    });

            if (epic.getStartTime() != null) {
                prioritizedTasks.remove(epic);
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
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
            checkStatus(getEpic(subtask.getEpicId()));
            subtask.setId(-1);
            subtask.setEpicId(-1);
            return;
        }

        System.out.println("Подзадача с id " + subtask.getId() + " не найдена.");
    }

    @Override
    public void deleteTasks() {
        tasks.keySet().stream().forEach(i -> historyManager.remove(i));

        tasks.values().stream().forEach(task -> {
            if (task.getStartTime() != null) {
                prioritizedTasks.remove(task);
            }
        });

        tasks.clear();
        System.out.println(historyManager.getHistory());
    }

    //переделать через stream
    @Override
    public void deleteEpics() {
        epics.keySet().stream().forEach(i -> historyManager.remove(i));

        epics.values().stream().forEach(epic -> {
            if (epic.getStartTime() != null) {
                prioritizedTasks.remove(epic);
            }
        });

        subtasks.keySet().stream().forEach(i -> historyManager.remove(i));

        subtasks.values().stream().forEach(subtask -> {
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
        });

        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.keySet().stream().forEach(i -> historyManager.remove(i));

        subtasks.values().stream().forEach(subtask -> {
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
        });

        subtasks.clear();

        epics.values().stream().forEach(epic -> {
            epic.getSubtasksId().clear();
            checkStatus(epic);
        });
    }

    private void checkStatus(Epic epic) {
        ArrayList<Subtask> tempList = getEpicSubtasks(epic.getId());

        if (tempList.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = tempList.stream()
                .filter(Objects::nonNull)
                .allMatch(subtask -> subtask.getStatus() == Status.NEW);

        boolean allDone = tempList.stream()
                .filter(Objects::nonNull)
                .allMatch(subtask -> subtask.getStatus() == Status.DONE);
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

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private static boolean checkOverlayTasks(Task task1, Task task2) {
        return !(task1.getEndTime().isBefore(task2.getStartTime()) || task2.getEndTime().isBefore(task1.getStartTime()));
    }


    private static boolean checkOverlayTasks(Task task, TreeSet<Task> prioritizedTasks) {
        return !(prioritizedTasks.isEmpty()) &&
                prioritizedTasks.stream()
                        .anyMatch((otherTask) -> checkOverlayTasks(task, otherTask));
    }
}
