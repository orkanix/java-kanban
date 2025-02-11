package model;

import status.Status;

import java.util.Objects;

public class Task {

    protected String name;
    protected String description;
    protected int id;
    protected Status status;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;

        System.out.println("Задача " + name + " создана!");
    }

    public Task(String name, String description, Status status, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;

        System.out.println("Задача " + name + " создана!");
    }

    @Override
    public String toString() {
        return "model.Task={"
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

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;

        Task tempTask = (Task) obj;
        return Objects.equals(name, tempTask.name) &&
                Objects.equals(description, tempTask.description) &&
                Objects.equals(id, tempTask.id) &&
                Objects.equals(status, tempTask.status);
    }

    @Override
    public int hashCode() {
        return  Objects.hash(name, description, id, status);
    }
}