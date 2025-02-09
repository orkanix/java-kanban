import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Subtask> subtasks = new ArrayList<>();

    TaskManager taskManager = new TaskManager();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        taskManager.checkStatus(this);
    }

    public Epic(String name, String description, Status status, int id) {
        super(name, description, status, id);
        taskManager.checkStatus(this);
    }

    @Override
    public String toString() {
        return "Epic={"
                + "name='" + super.getName() + "', "
                + "description='" + super.getDescription() + "', "
                + "id=" + super.getId() + ", "
                + "status='" + super.getStatus() + "', "
                + "subtasks=" + subtasks.size()
                + "}";
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        System.out.println("Подзадача добавлена к эпику " + this);
        taskManager.checkStatus(this);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

}