package main.java.history;

import main.java.model.Task;

import java.util.List;

public interface HistoryManager {
    void addHistory(Task taskToAdd);

    List<Task> getHistory();
}
