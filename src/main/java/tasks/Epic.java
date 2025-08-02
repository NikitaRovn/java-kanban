package main.java.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Epic extends Task {
    private Set<Integer> subtasksId = new HashSet<>();

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public Set<Integer> getSubtasks() {
        return subtasksId;
    }

    public void addSubtask(int id) {
        subtasksId.add(id);
    }

    public void removeSubTask(int id) {
        subtasksId.remove(id);
    }

    public static Epic fromString(String csvLine) {
        String[] fields = csvLine.split(";");

        Set<Integer> subtasks = Arrays.stream(fields[5].split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toSet());

        Epic newEpic = new Epic(Integer.parseInt(fields[1]),
                fields[2],
                fields[3]);

        newEpic.subtasksId = subtasks;
        if (fields.length > 6 && !fields[6].isEmpty()) {
            newEpic.setStartTime(LocalDateTime.parse(fields[6]));
        } else {
            newEpic.setStartTime(LocalDateTime.MIN);
        }

        if (fields.length > 7 && !fields[7].isEmpty()) {
            newEpic.setDuration(Duration.parse(fields[7]));
        } else {
            newEpic.setDuration(Duration.ZERO);
        }

        if (fields.length > 8 && !fields[8].isEmpty()) {
            newEpic.setEndTime();
        }

        return newEpic;
    }

    @Override
    public String toString() {
        String subtasks;

        if (subtasksId == null || subtasksId.isEmpty()) {
            subtasks = "0";
        } else {
            subtasks = subtasksId.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }

        return String.format("EPIC;%d;%s;%s;%s;%s;%s;%s;%s",
                getId(),
                getName(),
                getDescription(),
                getStatus(),
                subtasks,
                getStartTime().toString(),
                getDuration().toString(),
                getEndTime().toString());
    }

}
