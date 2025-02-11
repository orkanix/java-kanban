package model;

import status.Status;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
        this.epicId = epic.getId();
        epic.addSubtask(this);
        System.out.println("ID эпика: " + epic.getId());
    }

    @Override
    public String toString() {
        return "model.Subtask={"
                + "name='" + name + "', "
                + "description='" + description + "', "
                + "id=" + id + ", "
                + "status='" + status + ", "
                + "parent=" + epicId
                + "}";
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public void setStatus(Status status) {

    }
}
