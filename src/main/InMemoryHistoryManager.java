package main;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private static final int MAX_HISTORY_SIZE = 10;
    private final ArrayDeque<Task> historyTaskList = new ArrayDeque<>(MAX_HISTORY_SIZE);

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyTaskList);
    }

    @Override
    public void addHistoryEntry(Task taskToAdd) {
        if (historyTaskList.size() >= MAX_HISTORY_SIZE) {
            historyTaskList.removeFirst();
        }
        historyTaskList.addLast(taskToAdd);
    }
}
