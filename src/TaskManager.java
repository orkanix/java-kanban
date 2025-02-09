import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    public static int id = 1;

    private HashMap<Status, ArrayList<Task>> taskList = new HashMap<>();

    public ArrayList<Task> getTasks() {

        ArrayList<Task> tempTasks = new ArrayList<>();

        for (Status status : taskList.keySet()) {
            for (Task task : taskList.get(status)) {
                if (task.getClass() == Task.class) {
                    tempTasks.add(task);
                }
            }
        }

        return tempTasks;
    }

    public ArrayList<Task> getEpics() {

        ArrayList<Task> tempTasks = new ArrayList<>();

        for (Status status : taskList.keySet()) {
            for (Task task : taskList.get(status)) {
                if (task.getClass() == Epic.class) {
                    tempTasks.add(task);
                }
            }
        }

        return tempTasks;
    }

    public ArrayList<Task> getSubtasks() {

        ArrayList<Task> tempTasks = new ArrayList<>();

        for (Status status : taskList.keySet()) {
            for (Task task : taskList.get(status)) {
                if (task.getClass() == Subtask.class) {
                    tempTasks.add(task);
                }
            }
        }

        return tempTasks;
    }

    public Task getTaskForId(int id) {
        for (Status status : taskList.keySet()) {
            for (Task task : taskList.get(status)) {
                if (id == task.getId()) {
                    return task;
                }
            }
        }

        System.out.println("Задача с id " + id + " не найдена");
        return null;
    }

    public void addTask(Task task) {
        taskList.putIfAbsent(task.getStatus(), new ArrayList<>());
        taskList.get(task.getStatus()).add(task);
    }

    public void updateTask(Task task) {
        for (Status status : taskList.keySet()) {
            ArrayList<Task> tempList = taskList.get(status);

            if (tempList.removeIf(el -> el.getId() == task.getId())) {
                tempList.add(task);
                System.out.println("Задача с id " + task.getId() + " обновлена.");
                System.out.println(task);
                taskList.put(status, tempList);
                return;
            }
        }

        System.out.println("Задача с id " + task.getId() + " не найдена для обновления.");
    }


    public void removeTasks() {
        for (Status status : taskList.keySet()) {
            taskList.get(status).removeIf(task -> task.getClass() == Task.class);
        }
    }

    public void removeEpics() {
        for (Status status : taskList.keySet()) {
            taskList.get(status).removeIf(task -> task.getClass() == Epic.class || task instanceof Epic);
        }
    }

    public void removeSubtasks() {
        for (Status status : taskList.keySet()) {
            taskList.get(status).removeIf(task -> task.getClass() == Subtask.class);
        }
    }

    public void removeTaskForId(int id) {
        for (Status status : taskList.keySet()) {
            for (Task task : taskList.get(status)) {
                if (id == task.getId()) {
                    taskList.get(status).remove(task);
                    System.out.println("Успешное удаление записи");
                    return;
                }
            }
        }

        System.out.println("Задача с id " + id + " не найдена");
    }

    public void checkStatus(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;


        for (Subtask subtask : epic.getSubtasks()) {
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
