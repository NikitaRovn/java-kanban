package main.java.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private TaskStatus status;
    private LocalDateTime startTime;
    private Duration duration;
    private LocalDateTime endTime;

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
        this.startTime = LocalDateTime.MIN;
        this.duration = Duration.ZERO;
        this.endTime = LocalDateTime.MIN;
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
        newTask.setStartTime(LocalDateTime.parse(fields[6]));
        newTask.setDuration(Duration.parse(fields[7]));
        newTask.setEndTime();

        return newTask;
    }

    @Override
    public String toString() {
        return String.format("TASK;%d;%s;%s;%s;0;%s;%s;%s",
                id,
                name,
                description,
                status,
                getStartTime().toString(),
                getDuration().toString(),
                getEndTime().toString());
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
        setEndTime();
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        setEndTime();
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime() {
        this.endTime = startTime.plus(duration);
    }

    public boolean overlaps(Task other) {
        if (this.getStartTime() == null || this.getStartTime().equals(LocalDateTime.MIN) ||
                other.getStartTime() == null || other.getStartTime().equals(LocalDateTime.MIN)) {
            return false;
        }

        this.setEndTime();
        other.setEndTime();

        return this.getStartTime().isBefore(other.getEndTime()) &&
                other.getStartTime().isBefore(this.getEndTime());
    }
}
