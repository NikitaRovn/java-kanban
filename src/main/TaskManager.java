package main;

import java.util.HashMap;
import java.util.Set;

public class TaskManager {
    private static int idTaskCounter = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();

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
        Task newTask = new Task(generateUniqueId(), name, description);
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    public Epic createEpic(String name, String description) {
        Epic newEpic = new Epic(generateUniqueId(), name, description);
        tasks.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    public Subtask createSubtask(String name, String description, int parentId) {
        Subtask newSubtask = new Subtask(generateUniqueId(), name, description, parentId);
        Epic parentTask = (Epic) getTaskById(parentId);
        parentTask.addSubtask(newSubtask.getId());
        tasks.put(newSubtask.getId(), newSubtask);
        return newSubtask;
    }

    public Task updateNameAndDescription(int id, String name, String description) {
        Task targetTask = getTaskById(id);
        targetTask.setName(name);
        targetTask.setDescription(description);
        return targetTask;
    }

    public Task updateStatus(int id, TaskStatus status) {
        Task targetTask = getTaskById(id);
        if (!(targetTask instanceof Epic)) {
            targetTask.setStatus(status);
            if (targetTask instanceof Subtask) {
                updateStatusEpic(((Subtask) targetTask).getParentId());
            }
            return targetTask;
        } else {
            return null;
        }
    }

    private void updateStatusEpic(int epicId) {
        Epic epicTask = (Epic) getTaskById(epicId);
        if (epicTask.getSubtasks().stream().allMatch(taskId -> getTaskById(taskId).getStatus() == TaskStatus.NEW)) {
            epicTask.setStatus(TaskStatus.NEW);
        } else if (epicTask.getSubtasks().stream().allMatch(taskId -> getTaskById(taskId).getStatus() == TaskStatus.DONE)) {
            epicTask.setStatus(TaskStatus.DONE);
        } else {
            epicTask.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public String deleteAllTasks() {
        if (this.tasks == null) {
            return "Удаление не удалось! Список задач не инициализирован.";
        } else if (this.tasks.isEmpty()) {
            return "Удаление не удалось! Нет задач для удаления.";
        } else {
            String result = String.format("Удаление успешно! Удалено %d задач.", this.tasks.size());
            tasks.clear();
            return result;
        }
    }

    public String deleteTaskById(int id) {
        if (this.tasks.containsKey(id)) {
            if (this.tasks.get(id) instanceof Epic epic) {
                for (int subId : epic.getSubtasks()) {
                    tasks.remove(subId);
                }
            }
            this.tasks.remove(id);
            return String.format("Удаление успешно! Задача с ID: %d - удалена.", id);
        } else {
            return String.format("Удаление не удалось! Задача с ID: %d - не найдена.", id);
        }
    }
}
