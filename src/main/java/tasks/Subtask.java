package main.java.tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int parentId;

    public int getParentId() {
        return parentId;
    }

    public Subtask(int id, String name, String description, int parentId) {
        super(id, name, description);
        this.parentId = parentId;
    }

    public static Subtask fromString(String csvLine) {
        String[] fields = csvLine.split(";");

        Subtask newSubtask = new Subtask(Integer.parseInt(fields[1]),
                fields[2],
                fields[3],
                Integer.parseInt(fields[5]));

        newSubtask.setStatus(TaskStatus.valueOf(fields[4]));
        newSubtask.setStartTime(LocalDateTime.parse(fields[6]));
        newSubtask.setDuration(Duration.parse(fields[7]));
        newSubtask.setEndTime();

        return newSubtask;
    }

    @Override
    public String toString() {
        return String.format("SUBTASK;%d;%s;%s;%s;%d;%s;%s;%s",
                getId(),
                getName(),
                getDescription(),
                getStatus(),
                parentId,
                getStartTime().toString(),
                getDuration().toString(),
                getEndTime().toString());
    }

}
