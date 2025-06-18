package main.java.tasks;

import java.util.HashSet;
import java.util.Set;

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
}
