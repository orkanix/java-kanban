package model;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
        this.epicId = epic.getId();
        System.out.println("ID эпика: " + epic.getId());
        System.out.println();
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
}
