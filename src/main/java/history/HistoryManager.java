package main.java.history;

import main.java.tasks.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task taskToAdd);

    void remove(int id);

    List<Task> getHistory();
}
