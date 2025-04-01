package manager.task;

import model.*;
import manager.exceptions.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final String header = "id,type,name,status,description,epic";
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    public FileBackedTaskManager(File file, HashMap<Integer, Task> tasks, HashMap<Integer, Epic> epics, HashMap<Integer, Subtask> subtasks) {
        this.file = file;
        this.tasks = tasks;
        this.epics = epics;
        this.subtasks = subtasks;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        HashMap<Integer, Task> tasks = new HashMap<>();
        HashMap<Integer, Epic> epics = new HashMap<>();
        HashMap<Integer, Subtask> subtasks = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            while (br.ready()) {
                Task task = taskFromString(br.readLine());
                if (task instanceof Epic) {
                    epics.put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    subtasks.put(task.getId(), (Subtask) task);
                } else if (task instanceof Task) {
                    tasks.put(task.getId(), task);
                }
            }
            for (Subtask subtask : subtasks.values()) {
                epics.get(subtask.getEpicId()).addSubtask(subtask);
            }
            return new FileBackedTaskManager(file, tasks, epics, subtasks);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении данных", e);
        }
    }

    private void save() {
        List<Task> tasks = super.getTasks();
        List<Epic> epics = super.getEpics();
        List<Subtask> subtasks = super.getSubtasks();

        try (BufferedWriter dw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            dw.write(header);
            dw.newLine();
            for (Task task : tasks) {
                dw.write(getString(task));
                dw.newLine();
            }
            for (Epic epic : epics) {
                dw.write(getString(epic));
                dw.newLine();
            }
            for (Subtask subtask : subtasks) {
                dw.write(getString(subtask));
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

        switch (type) {
            case "TASK":
                return new Task(name, description, status, id);
            case "EPIC":
                return new Epic(name, description, status, id);
            case "SUBTASK":
                return new Subtask(name, description, status, Integer.parseInt(words[5]), id);
            default:
                return null;
        }
    }

    private String getString(Task task) {
        return String.format("%d,%s,%s,%s,%s", task.getId(), Types.TASK, task.getName(), task.getStatus(), task.getDescription());
    }

    private String getString(Epic epic) {
        return String.format("%d,%s,%s,%s,%s", epic.getId(), Types.EPIC, epic.getName(), epic.getStatus(), epic.getDescription());
    }

    private String getString(Subtask subtask) {
        return String.format("%d,%s,%s,%s,%s,%d", subtask.getId(), Types.SUBTASK, subtask.getName(), subtask.getStatus(), subtask.getDescription(), subtask.getEpicId());
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
    public void updateSubtask(Subtask subtask, int id) {
        super.updateSubtask(subtask, subtask.getId());
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
