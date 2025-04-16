package model;

import java.time.Duration;
import java.time.LocalDateTime;

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

    public Subtask(String name, String description, Status status, int epicId, int id) {
        super(name, description, status, id);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status, Epic epic, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epic.getId();
    }

    public Subtask(String name, String description, Status status, int epicId, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status, int epicId, int id, LocalDateTime startTime, Duration duration) {
        super(name, description, status, id, startTime, duration);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "model.Subtask={"
                + "name='" + name + "', "
                + "description='" + description + "', "
                + "id=" + id + ", "
                + "status='" + status + "', "
                + "parent=" + epicId + ", "
                + "duration=" + (duration != null ? duration : null) + ", "
                + "startTime=" + (startTime != null ? startTime.format(TIME_FORMATTER) : null) + ", "
                + "endTime=" + (startTime != null ? getEndTime().format(TIME_FORMATTER) : null)
                + "}";
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
