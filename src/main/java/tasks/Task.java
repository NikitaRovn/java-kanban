package main.java.tasks;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private TaskStatus status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public int getId() {
        return id;
    }

    public static Task fromString(String csvLine) {
        String[] fields = csvLine.split(";");

        Task newTask = new Task(Integer.parseInt(fields[1]),
                fields[2],
                fields[3]);

        newTask.setStatus(TaskStatus.valueOf(fields[4]));

        return newTask;
    }

    @Override
    public String toString() {
        return String.format("TASK;%d;%s;%s;%s;0", id, name, description, status);
    }

}
