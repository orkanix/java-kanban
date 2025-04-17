package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subtasksId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public Epic(String name, String description, Status status, int id) {
        super(name, description, status, id);
    }

    public Epic(String name, String description, Status status, int id, LocalDateTime startTime, Duration duration) {
        super(name, description, status, id, startTime, duration);
    }

    public Epic(String name, String description, Status status, int id, LocalDateTime startTime, Duration duration, LocalDateTime endTime) {
        super(name, description, status, id, startTime, duration);
        this.endTime = endTime;
    }

    public void addSubtask(Subtask subtask) {
        subtasksId.add(subtask.getId());
    }

    public List<Integer> getSubtasksId() {
        return new ArrayList<>(subtasksId) {
        };
    }

    public void clearSubtasks() {
        this.subtasksId.clear();
    }

    public void deleteSubtask(Subtask subtask) {
        subtasksId.remove(subtasksId.indexOf(subtask.getId()));
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "model.Epic={"
                + "name='" + name + "', "
                + "description='" + description + "', "
                + "id=" + id + ", "
                + "status='" + status + "', "
                + "subtasks='" + subtasksId.size() + "', "
                + "duration=" + (duration != null ? duration : null) + ", "
                + "startTime=" + (startTime != null ? startTime.format(TIME_FORMATTER) : null) + ", "
                + "endTime=" + (endTime != null ? endTime.format(TIME_FORMATTER) : null)
                + "}";
    }

}