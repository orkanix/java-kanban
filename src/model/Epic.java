package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Epic extends Task {

    private final HashMap<Integer, ArrayList<LocalDateTime>> subtasksIdTime = new HashMap<>();
    private LocalDateTime endTime;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public Epic(String name, String description, Status status, int id) {
        super(name, description, status, id);
    }

    public void addSubtask(Subtask subtask) {
        subtasksIdTime.computeIfAbsent(subtask.getId(), k -> new ArrayList<>(List.of(subtask.getStartTime(), subtask.getEndTime())));
        setStartTime();
        setEndTime();
    }

    public HashMap<Integer, ArrayList<LocalDateTime>> getSubtasksId() {
        return subtasksIdTime;
    }

    public void deleteSubtask(Subtask subtask) {
        subtasksIdTime.remove(subtask.getId());
    }

    private void setDuration() {
        this.duration = Duration.between(startTime, endTime);
    }

    public void setStartTime() {
        for (Integer id : subtasksIdTime.keySet()) {
            LocalDateTime startTime = subtasksIdTime.get(id).getFirst();
            if (this.startTime == null) {
                this.startTime = startTime;
                continue;
            }
            if (this.startTime.isAfter(startTime)) {
                this.startTime = startTime;
            }
        }
        if (this.startTime != null && this.endTime != null) {
            setDuration();
        }
    }

    public void setEndTime() {
        for (Integer id : subtasksIdTime.keySet()) {
            LocalDateTime endTime = subtasksIdTime.get(id).getLast();
            if (this.endTime == null) {
                this.endTime = endTime;
                continue;
            }
            if (this.endTime.isBefore(endTime)) {
                this.endTime = endTime;
            }
        }
        if (this.startTime != null && this.endTime != null) {
            setDuration();
        }
    }

    @Override
    public String toString() {
        return "model.Epic={"
                + "name='" + name + "', "
                + "description='" + description + "', "
                + "id=" + id + ", "
                + "status='" + status + "', "
                + "subtasks='" + subtasksIdTime.size() + "', "
                + "duration=" + duration + ", "
                + "startTime=" + startTime.format(TIME_FORMATTER) + ", "
                + "endTime=" + endTime.format(TIME_FORMATTER)
                + "}";
    }

}