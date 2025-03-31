package manager.task;

import model.*;
import manager.exceptions.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager{

    private final String file;
    private final HashMap<Types, ArrayList<Task>> actualTasksList;

    public FileBackedTaskManager(String file) {
        this.file = file;
        this.actualTasksList = loadFromFile(new File(file));

        if (actualTasksList.isEmpty()) {
            return;
        }

        for (Task task : actualTasksList.get(Types.TASK)) {
            addNewTask(task);
        }
        for (Task epic : actualTasksList.get(Types.EPIC)) {
            addNewEpic((Epic) epic);
        }
        for (Task subtask : actualTasksList.get(Types.SUBTASK)) {
            addNewSubtask((Subtask) subtask);
        }
    }

    public static HashMap<Types, ArrayList<Task>> loadFromFile(File file) {
        HashMap<Types, ArrayList<Task>> loadedMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            while (br.ready()) {
                Task task = taskFromString(br.readLine());
                if (task instanceof Epic) {
                    loadedMap.putIfAbsent(Types.EPIC, new ArrayList<>());
                    loadedMap.get(Types.EPIC).add(task);
                } else if (task instanceof Subtask) {
                    loadedMap.putIfAbsent(Types.SUBTASK, new ArrayList<>());
                    loadedMap.get(Types.SUBTASK).add(task);
                } else if (task instanceof Task) {
                    loadedMap.putIfAbsent(Types.TASK, new ArrayList<>());
                    loadedMap.get(Types.TASK).add(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении данных", e);
        }
        return loadedMap;
    }

    public void save() {
        List<Task> tasks = super.getTasks();
        List<Epic> epics = super.getEpics();
        List<Subtask> subtasks = super.getSubtasks();

        for (Task task : tasks) {
            if (!actualTasksList.containsKey(Types.TASK)) {
                actualTasksList.computeIfAbsent(Types.TASK, k -> new ArrayList<>());
            }
            if (!actualTasksList.get(Types.TASK).contains(task)) {
                actualTasksList.get(Types.TASK).add(task);
            }
        }
        for (Epic epic : epics) {
            if (!actualTasksList.containsKey(Types.EPIC)) {
                actualTasksList.computeIfAbsent(Types.EPIC, k -> new ArrayList<>());
            }
            if (!actualTasksList.get(Types.EPIC).contains(epic)) {
                actualTasksList.get(Types.EPIC).add(epic);
            }
        }
        for (Subtask subtask : subtasks) {
            if (!actualTasksList.containsKey(Types.SUBTASK)) {
                actualTasksList.computeIfAbsent(Types.SUBTASK, k -> new ArrayList<>());
            }
            if (!actualTasksList.get(Types.SUBTASK).contains(subtask)) {
                actualTasksList.get(Types.SUBTASK).add(subtask);
            }
        }

        try (BufferedWriter dw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            dw.write("id,type,name,status,description,epic");
            dw.newLine();
            for (Types type : actualTasksList.keySet()) {
                for (Task task : actualTasksList.get(type)) {
                    if (task instanceof Epic) {
                        dw.write(getString((Epic) task));
                    } else if (task instanceof Subtask) {
                        dw.write(getString((Subtask) task));
                    } else if (task instanceof Task) {
                        dw.write(getString(task));
                    }
                    dw.newLine();
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных!", e);
        }
    }

    private static Status parseStatus(String statusStr) {
        switch (statusStr) {
            case "DONE":
                return Status.DONE;
            case "IN_PROGRESS":
                return Status.IN_PROGRESS;
            case "NEW":
            default:
                return Status.NEW;
        }
    }


    private static Task taskFromString(String str) {
        String[] words = str.split(",");

        int id = Integer.parseInt(words[0]);
        String type = words[1];
        String name = words[2];
        Status status = parseStatus(words[3]);
        String description = words[4];

        switch (type) {
            case "TASK":
                return new Task(name, description, status, id);
            case "EPIC":
                return new Epic(name, description, status, id);
            case "SUBTASK":
                return new Subtask(name, description, status, Integer.parseInt(words[5]));
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
