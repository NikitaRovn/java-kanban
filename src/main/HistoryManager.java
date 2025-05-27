package main;

import java.util.List;

public interface HistoryManager {
    void addHistoryEntry(Task taskToAdd);

    List<Task> getHistory();
}
