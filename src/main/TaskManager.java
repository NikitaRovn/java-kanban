package main;

import java.util.HashMap;
import java.util.Set;

public class TaskManager {
    private static int idTaskCounter = 0;
    private HashMap<Integer, Task> tasks;

    private static int generateUniqueId() {
        return idTaskCounter++;
    }

    public HashMap<Integer, Task> getAllTasks() {
        return this.tasks;
    }

    public Task getTaskById(int id) {
        return this.tasks.get(id);
    }

    public Set<Integer> getSubtasksById(int id) {
        Task targetTask = getTaskById(id);
        if (targetTask instanceof Epic) {
            return ((Epic) targetTask).getSubtasks();
        } else {
            return null;
        }
    }

    public Task createTask(String name, String description) {
        return new Task(generateUniqueId(), name, description);
    }

    public Epic createEpic(String name, String description) {
        return new Epic(generateUniqueId(), name, description);
    }

    public Subtask createSubtask(String name, String description, int parentId) {
        Subtask targetTask = new Subtask(generateUniqueId(), name, description, parentId);
        Epic parentTask = (Epic) getTaskById(parentId);
        parentTask.addSubtask(targetTask.id);
        return targetTask;
    }

    public Task updateNameAndDescription(int id, String name, String description) {
        Task targetTask = getTaskById(id);
        targetTask.name = name;
        targetTask.description = description;
        return targetTask;
    }

    public Task updateStatus(int id, taskStatus status) {
        Task targetTask = getTaskById(id);
        if (!(targetTask instanceof Epic)) {
            targetTask.status = status;
            if (targetTask instanceof Subtask) {
                updateStatusEpic(((Subtask) targetTask).parentId);
            }
            return targetTask;
        } else {
            return null;
        }
    }

    private void updateStatusEpic(int epicId) {
        Epic epicTask = (Epic) getTaskById(epicId);
        if (epicTask.subtasksId.stream().allMatch(taskId -> getTaskById(taskId).status == taskStatus.NEW)) {
            epicTask.status = taskStatus.NEW;
        } else if (epicTask.subtasksId.stream().allMatch(taskId -> getTaskById(taskId).status == taskStatus.DONE)) {
            epicTask.status = taskStatus.DONE;
        } else {
            epicTask.status = taskStatus.IN_PROGRESS;
        }
    }

    public String deleteAllTasks() {
        if (this.tasks == null) {
            return "Удаление не удалось! Список задач не инициализирован.";
        } else if (this.tasks.isEmpty()) {
            return "Удаление не удалось! Нет задач для удаления.";
        } else {
            return String.format("Удаление успешно! Удалено %d задач.", this.tasks.size());
        }
    }

    public String deleteTaskById(int id) {
        if (this.tasks.containsKey(id)) {
            this.tasks.remove(id);
            return String.format("Удаление успешно! Задача с ID: %d - удалена.", id);
        } else {
            return String.format("Удаление не удалось! Задача с ID: %d - не найдена.", id);
        }
    }
}
