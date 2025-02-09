public class Task {

    private String name;
    private String description;
    private int id;
    private Status status;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.id = TaskManager.id;
        this.status = status;

        TaskManager.id++;

        System.out.println("Задача c id " + id + " создана!");
    }

    public Task(String name, String description, Status status, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;

        TaskManager.id++;

        System.out.println("Задача " + name + " создана!");
    }

    @Override
    public String toString() {
        return "Task={"
                + "name='" + name + "', "
                + "description='" + description + "', "
                + "id=" + id + ", "
                + "status='" + status
                + "}";
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}