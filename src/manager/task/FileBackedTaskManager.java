package manager.task;

import model.*;
import manager.exceptions.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String HEADER = "id,type,name,status,description,duration,startTime,endTime,epic";
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            while (br.ready()) {
                Task task = taskFromString(br.readLine());
                if (task instanceof Epic) {
                    fileBackedTaskManager.epics.put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    fileBackedTaskManager.subtasks.put(task.getId(), (Subtask) task);
                    if (task.getStartTime() != null && task.getEndTime() != null && !(fileBackedTaskManager.checkOverlayTasks(task, fileBackedTaskManager.prioritizedTasks))) {
                        fileBackedTaskManager.prioritizedTasks.add(task);
                    }
                } else if (task instanceof Task) {
                    fileBackedTaskManager.tasks.put(task.getId(), task);
                    if (task.getStartTime() != null && task.getEndTime() != null && !(fileBackedTaskManager.checkOverlayTasks(task, fileBackedTaskManager.prioritizedTasks))) {
                        fileBackedTaskManager.prioritizedTasks.add(task);
                    }
                }
            }

            fileBackedTaskManager.subtasks.values()
                    .forEach(subtask -> fileBackedTaskManager.epics.get(subtask.getEpicId()).addSubtask(subtask));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении данных", e);
        }
        return fileBackedTaskManager;
    }

    private void save() {
        try (BufferedWriter dw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            dw.write(HEADER);
            dw.newLine();

            for (Task task : getTasks()) {
                dw.write(getString(task));
                dw.newLine();
            }
            for (Subtask subtask : getSubtasks()) {
                dw.write(getString(subtask));
                dw.newLine();
            }
            for (Epic epic : getEpics()) {
                dw.write(getString(epic));
                dw.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных!", e);
        }
    }

    private static Task taskFromString(String str) {
        String[] words = str.split(",");

        int id = Integer.parseInt(words[0]);
        String type = words[1];
        String name = words[2];
        Status status = Status.valueOf(words[3]);
        String description = words[4];
        Duration duration;
        LocalDateTime startTime;
        LocalDateTime endTime;
        if (words[5].equals("null")) {
            duration = null;
        } else {
            duration = Duration.parse(words[5]);
        }
        if (words[6].equals("null")) {
            startTime = null;
        } else {
            startTime = LocalDateTime.parse(words[6]);
        }
        if (words[7].equals("null")) {
            endTime = null;
        } else {
            endTime = LocalDateTime.parse(words[7]);
        }

        switch (type) {
            case "TASK":
                return new Task(name, description, status, id, startTime, duration);
            case "EPIC":
                return new Epic(name, description, status, id, startTime, duration, endTime);
            case "SUBTASK":
                return new Subtask(name, description, status, Integer.parseInt(words[8]), id, startTime, duration);
            default:
                return null;
        }
    }

    private String getString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s", task.getId(), Types.TASK, task.getName(), task.getStatus(), task.getDescription(), task.getDuration(), task.getStartTime(), task.getEndTime());
    }

    private String getString(Epic epic) {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s", epic.getId(), Types.EPIC, epic.getName(), epic.getStatus(), epic.getDescription(), epic.getDuration(), epic.getStartTime(), epic.getEndTime());
    }

    private String getString(Subtask subtask) {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s,%d", subtask.getId(), Types.SUBTASK, subtask.getName(), subtask.getStatus(), subtask.getDescription(), subtask.getDuration(), subtask.getStartTime(), subtask.getEndTime(), subtask.getEpicId());
    }

    @Override
    public int addNewTask(Task task) {
        int result = super.addNewTask(task);
        save();
        return result;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int result = super.addNewEpic(epic);
        save();
        return result;
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int result = super.addNewSubtask(subtask);
        save();
        return result;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(Task task) {
        super.deleteTask(task);
        save();
    }

    @Override
    public void deleteEpic(Epic epic) {
        super.deleteEpic(epic);
        save();
    }

    @Override
    public void deleteSubtask(Subtask subtask) {
        super.deleteSubtask(subtask);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

}
