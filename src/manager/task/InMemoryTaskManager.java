package manager.task;

import manager.Managers;
import manager.history.HistoryManager;
import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
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

        return getEpic(epicId).getSubtasksId().stream().map(subtasks::get).collect(Collectors.toList());
    }

    @Override
    public int addNewTask(Task task) {
        if (task.getStartTime() != null && task.getEndTime() != null) {
            if (checkOverlayTasks(task, prioritizedTasks)) {
                return -1;
            } else {
                prioritizedTasks.add(task);
            }
        }
        task.setId(id);
        tasks.put(id, task);
        id++;
        return id - 1;
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
        if (subtask.getStartTime() != null && subtask.getEndTime() != null) {
            if (checkOverlayTasks(subtask, prioritizedTasks)) {
                return -1;
            } else {
                prioritizedTasks.add(subtask);
            }
        }
        subtask.setId(id);
        subtasks.put(id, subtask);
        tempEpic.addSubtask(subtask);
        checkStatus(tempEpic);
        checkEpicTime(tempEpic);
        id++;
        return id - 1;
    }

    @Override
    public void updateTask(Task task) {
        Task oldTask = tasks.get(task.getId());
        if (tasks.containsKey(task.getId())) {
            if (task.getStartTime() == null || task.getEndTime() == null) {
                prioritizedTasks.remove(oldTask);
            } else if (checkOverlayTasks(task, prioritizedTasks)) {
                return;
            }
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);

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
            System.out.println("Эпик с id " + epic.getId() + " обновлен.");
            return;
        }

        System.out.println("Эпик с id " + epic.getId() + " не найден.");
    }

    @Override
    public void updateSubtask(Subtask subtask, int id) {
        Subtask oldSubtask = subtasks.get(id);
        if (subtasks.containsKey(id)) {
            if (subtask.getStartTime() == null || subtask.getEndTime() == null) {
                prioritizedTasks.remove(oldSubtask);
            } else if (!(checkOverlayTasks(subtask, prioritizedTasks))) {
                prioritizedTasks.add(subtask);
                prioritizedTasks.remove(oldSubtask);
            } else {
                return;
            }
            subtasks.put(id, subtask);
            checkEpicTime(epics.get(subtask.getEpicId()));
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
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);

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
            epics.get(subtask.getEpicId()).deleteSubtask(subtask);
            historyManager.remove(subtask.getId());
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
            subtasks.remove(subtask.getId());
            checkStatus(epics.get(subtask.getEpicId()));
            checkEpicTime(epics.get(subtask.getEpicId()));
            subtask.setId(-1);
            subtask.setEpicId(-1);
            return;
        }

        System.out.println("Подзадача с id " + subtask.getId() + " не найдена.");
    }

    @Override
    public void deleteTasks() {
        tasks.values().forEach(task -> {
            prioritizedTasks.remove(task);
            historyManager.remove(task.getId());
        });

        tasks.clear();
        System.out.println(historyManager.getHistory());
    }

    @Override
    public void deleteEpics() {
        epics.values().forEach(epic -> {
            prioritizedTasks.remove(epic);
            historyManager.remove(epic.getId());
        });

        subtasks.values().forEach(subtask -> {
            prioritizedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        });

        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.values().forEach(subtask -> {
            prioritizedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        });

        epics.values().forEach(epic -> {
            epic.clearSubtasks();
            checkStatus(epic);
            checkEpicTime(epic);
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

    @Override
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


    protected boolean checkOverlayTasks(Task task, TreeSet<Task> prioritizedTasks) {
        return !(prioritizedTasks.isEmpty()) &&
                prioritizedTasks.stream()
                        .anyMatch((otherTask) -> this.checkOverlayTasks(task, otherTask));
    }

    private void checkEpicTime(Epic epic) {
        epic.setStartTime(null);
        epic.setEndTime(null);
        epic.setDuration(null);

        if (epic.getSubtasksId().isEmpty()) {
            return;
        }

        if (epic.getSubtasksId().size() == 1) {
            Subtask onlySubtask = subtasks.get(epic.getSubtasksId().getFirst());
            if (onlySubtask.getStartTime() != null && onlySubtask.getEndTime() != null && onlySubtask.getDuration() != null) {
                epic.setStartTime(onlySubtask.getStartTime());
                epic.setEndTime(onlySubtask.getEndTime());
                epic.setDuration(onlySubtask.getDuration());
            }
            return;
        }


        LocalDateTime minStart = null;
        LocalDateTime maxEnd = null;
        Duration totalDuration = Duration.ZERO;

        for (Integer id : epic.getSubtasksId()) {
            Subtask subtask = subtasks.get(id);
            if (subtask == null || subtask.getStartTime() == null || subtask.getEndTime() == null || subtask.getDuration() == null) {
                continue;
            }

            if (minStart == null || subtask.getStartTime().isBefore(minStart)) {
                minStart = subtask.getStartTime();
            }

            if (maxEnd == null || subtask.getEndTime().isAfter(maxEnd)) {
                maxEnd = subtask.getEndTime();
            }

            totalDuration = totalDuration.plus(subtask.getDuration());
        }

        epic.setStartTime(minStart);
        epic.setEndTime(maxEnd);
        epic.setDuration(totalDuration.isZero() ? null : totalDuration);
    }
}
