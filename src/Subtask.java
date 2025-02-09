public class Subtask extends Task{

    private final Epic parent;

    public Subtask(String name, String description, Status status, Epic parent) {
        super(name, description, status);
        this.parent = parent;
        parent.addSubtask(this);
    }

    @Override
    public String toString() {
        return "Subtask={"
                + "name='" + super.getName() + "', "
                + "description='" + super.getDescription() + "', "
                + "id=" + super.getId() + ", "
                + "status='" + super.getStatus() + ", "
                + "parent=" + parent
                + "}";
    }

}
