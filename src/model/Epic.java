package model;

import status.Status;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtasksId = new ArrayList<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public Epic(String name, String description, Status status, int id) {
        super(name, description, status, id);
    }

    public void addSubtask(Subtask subtask) {
        subtasksId.add(subtask.getId());
    }

    @Override
    public String toString() {
        return "model.Epic={"
                + "name='" + name + "', "
                + "description='" + description + "', "
                + "id=" + id + ", "
                + "status='" + status + "', "
                + "subtasks=" + subtasksId.size()
                + "}";
    }

}