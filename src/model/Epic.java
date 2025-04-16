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

    public void addSubtask(Subtask subtask) {
        subtasksId.add(subtask.getId());
        setStartTime(subtask);
        setEndTime(subtask);
    }

    public List<Integer> getSubtasksId() {
        return new ArrayList<>(subtasksId) {
        };
    }

    public void clearSubtasks() {
        this.subtasksId.clear();
        this.startTime = null;
        this.endTime = null;
    }

    public void deleteSubtask(Subtask subtask) {
        subtasksId.remove(subtasksId.indexOf(subtask.getId()));
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(Subtask subtask) {
        if (subtasksId.size() == 1) {
            this.startTime = subtask.getStartTime();
        }
        if (this.startTime == null || this.startTime.isAfter(subtask.getStartTime())) {
            this.startTime = subtask.getStartTime();
        }
    }

    public void setEndTime(Subtask subtask) {
        if (subtasksId.size() == 1) {
            this.endTime = subtask.getEndTime();
        }
        if (this.endTime == null || this.endTime.isBefore(subtask.getEndTime())) {
            this.endTime = subtask.getEndTime();
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plusMinutes(duration.toMinutes());
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