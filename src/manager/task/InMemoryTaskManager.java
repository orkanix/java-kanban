package manager.task;

import manager.Managers;
import manager.history.HistoryManager;
import model.*;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected int id = 1;

    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();

    protected HistoryManager historyManager = Managers.getDefaultHistory();

    protected Comparator<Task> taskComparator = new Comparator<Task>() {
        @Override
        public int compare(Task o1, Task o2) {
            if (o1.getStartTime() != null && o2.getStartTime() != null) {
                return o1.getStartTime().compareTo(o2.getStartTime());
            }
            return -1;
        }
    };

    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(taskComparator);

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
    public List<Subtask> getEpicSubtasks(int epicId) {
        if (!(epics.containsKey(epicId))) return new ArrayList<>();

        return subtasks.values().stream().filter(subtask -> subtask.getEpicId() == epicId).collect(Collectors.toList());
    }

    @Override
    public int addNewTask(Task task) {
        if (task.getStartTime() != null && task.getEndTime() != null && !(checkOverlayTasks(task, prioritizedTasks))) {
            task.setId(id);
            tasks.put(id, task);
            prioritizedTasks.add(task);
            id++;

            return id - 1;
        } else if (task.getStartTime() == null && task.getEndTime() == null) {
            //задача может быть без времени
            task.setId(id);
            tasks.put(id, task);
            id++;
        }
        return -1;
    }

    @Override
    public int addNewEpic(Epic epic) {
        epic.setId(id);
        epics.put(id, epic);
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

        if (subtask.getStartTime() != null && subtask.getEndTime() != null && !(checkOverlayTasks(subtask, prioritizedTasks))) {
            subtask.setId(id);
            subtasks.put(id, subtask);
            prioritizedTasks.add(subtask);

            tempEpic.addSubtask(subtask);
            checkStatus(tempEpic);
            checkDuration(tempEpic);
            id++;
            historyManager.remove(subtask.getEpicId());
        } else if (subtask.getStartTime() == null && subtask.getEndTime() == null) {
            //подзадача может быть без времени
            subtask.setId(id);
            subtasks.put(id, subtask);

            tempEpic.addSubtask(subtask);
            checkStatus(tempEpic);
            checkDuration(tempEpic);
            id++;
            historyManager.remove(subtask.getEpicId());
        }

        return id - 1;
    }

    @Override
    public void updateTask(Task task) {
        Task oldTask = tasks.get(task.getId());
        if (tasks.containsKey(task.getId())) {

            if (task.getStartTime() != null && task.getEndTime() != null && !(checkOverlayTasks(task, prioritizedTasks))) {
                prioritizedTasks.remove(oldTask);
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
            if (subtask.getStartTime() != null && subtask.getEndTime() != null && (checkOverlayTasks(subtask, prioritizedTasks))) {
                prioritizedTasks.remove(subtasks.get(subtask.getId()));
                prioritizedTasks.add(subtask);
                subtasks.put(id, subtask);
                System.out.println("пересчитываю duration:");
                checkDuration(epics.get(subtask.getEpicId()));
            } else if (subtask.getStartTime() == null && subtask.getEndTime() == null) {
                subtasks.put(id, subtask);
            }
            checkStatus(epics.get(subtask.getEpicId()));
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
            epic.getSubtasksId()
                    .forEach(key -> {
                        prioritizedTasks.remove(subtasks.get(key));
                        subtasks.remove(key);
                        historyManager.remove(key);
                    });


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
            historyManager.remove(subtask.getId());
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
            subtasks.remove(subtask.getId());
            checkStatus(epics.get(subtask.getEpicId()));
            checkDuration(epics.get(subtask.getEpicId()));
            subtask.setId(-1);
            subtask.setEpicId(-1);
            return;
        }

        System.out.println("Подзадача с id " + subtask.getId() + " не найдена.");
    }

    @Override
    public void deleteTasks() {
        tasks.keySet().forEach(i -> historyManager.remove(i));

        tasks.values().forEach(task -> {
            if (task.getStartTime() != null) {
                prioritizedTasks.remove(task);
            }
        });

        tasks.clear();
        System.out.println(historyManager.getHistory());
    }

    @Override
    public void deleteEpics() {
        epics.keySet().forEach(i -> historyManager.remove(i));

        epics.values().forEach(epic -> {
            if (epic.getStartTime() != null) {
                prioritizedTasks.remove(epic);
            }
        });

        subtasks.keySet().forEach(i -> historyManager.remove(i));

        subtasks.values().forEach(subtask -> {
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
        });

        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.keySet().forEach(i -> historyManager.remove(i));

        subtasks.values().forEach(subtask -> {
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
        });

        epics.values().forEach(epic -> {
            epic.clearSubtasks();
            checkStatus(epic);
            checkDuration(epic);
        });

        subtasks.clear();
    }

    private void checkStatus(Epic epic) {
        List<Subtask> tempList = getEpicSubtasks(epic.getId());

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
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    private boolean checkOverlayTasks(Task task1, Task task2) {
        return !(task1.getEndTime().isBefore(task2.getStartTime()) || task2.getEndTime().isBefore(task1.getStartTime()));
    }


    public boolean checkOverlayTasks(Task task, TreeSet<Task> prioritizedTasks) {
        return !(prioritizedTasks.isEmpty()) &&
                prioritizedTasks.stream()
                        .anyMatch((otherTask) -> this.checkOverlayTasks(task, otherTask));
    }

    public void checkDuration(Epic epic) {
        Duration result = null;
        if (epic.getSubtasksId().isEmpty()) {
            epic.setDuration(null);
            return;
        }
        for (Integer id : epic.getSubtasksId()) {
            epic.setStartTime(subtasks.get(id));
            epic.setEndTime(subtasks.get(id));
            if (result == null) {
                result = subtasks.get(id).getDuration();
            } else {
                result = result.plus(subtasks.get(id).getDuration());
            }
        }
        epic.setDuration(result);
    }
}
