package main;

import java.util.HashMap;
import java.util.Set;

public interface TaskManager {
    HashMap<Integer, Task> getAllTasks();

    Task getTaskById(int id);

    Set<Integer> getSubtasksById(int id);

    Task createTask(String name, String description);

    Epic createEpic(String name, String description);

    Subtask createSubtask(String name, String description, int parentId);

    Task updateNameAndDescription(int id, String name, String description);

    Task updateStatus(int id, TaskStatus status);

    String deleteAllTasks();

    String deleteTaskById(int id);
}
