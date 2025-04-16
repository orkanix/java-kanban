package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {

    static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    protected String name;
    protected String description;
    protected int id;
    protected Status status;

    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = null;
        this.duration = null;
    }

    public Task(String name, String description, Status status, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = null;
        this.duration = null;
    }

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, Status status, int id, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    @Override
    public String toString() {
        String startTimeStr = (startTime != null) ? startTime.format(TIME_FORMATTER) : null;
        String endTimeStr = (getEndTime() != null) ? getEndTime().format(TIME_FORMATTER) : null;

        return "model.Task={"
                + "name='" + name + "', "
                + "description='" + description + "', "
                + "id=" + id + ", "
                + "status='" + status + "', "
                + "duration=" + duration + ", "
                + "startTime=" + startTimeStr + ", "
                + "endTime=" + endTimeStr
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

    public Duration getDuration() {
        return duration;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;

        Task tempTask = (Task) obj;
        return Objects.equals(id, tempTask.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plusMinutes(duration.toMinutes());
    }
}