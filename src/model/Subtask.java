package model;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
        this.epicId = epic.getId();
    }

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
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

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
