package main.java.manager;

import main.java.history.HistoryManager;
import main.java.tasks.Epic;
import main.java.tasks.Subtask;
import main.java.tasks.Task;
import main.java.tasks.TaskStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class InMemoryTaskManager implements TaskManager {
    protected  int idTaskCounter = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();

    private int generateUniqueId() {
        int maxCurrentId = 0;

        for (int id : tasks.keySet()) {
            if (id > maxCurrentId) {
                maxCurrentId = id;
            }
        }

        if (maxCurrentId >= idTaskCounter) {
            idTaskCounter = maxCurrentId + 1;
        } else {
            idTaskCounter++;
        }
        return idTaskCounter;
    }

    @Override
    public HashMap<Integer, Task> getAllTasks() {
        return this.tasks;
    }

    @Override
    public Task getTaskById(int id) {
        Task targetTask = this.tasks.get(id);
        if (targetTask != null) {
            historyManager.add(targetTask);
        }
        return targetTask;
    }

    @Override
    public Set<Integer> getSubtasksById(int id) {
        Task targetTask = getTaskById(id);
        if (targetTask instanceof Epic) {
            return ((Epic) targetTask).getSubtasks();
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public Task createTask(String name, String description) {
        Task newTask = new Task(generateUniqueId(), name, description);
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    @Override
    public Epic createEpic(String name, String description) {
        Epic newEpic = new Epic(generateUniqueId(), name, description);
        tasks.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    @Override
    public Subtask createSubtask(String name, String description, int parentId) {
        Subtask newSubtask = new Subtask(generateUniqueId(), name, description, parentId);
        Task targetTask = getTaskById(parentId);
        if (!(targetTask instanceof Epic)) {
            return null;
        }
        Epic parentTask = (Epic) getTaskById(parentId);
        parentTask.addSubtask(newSubtask.getId());
        tasks.put(newSubtask.getId(), newSubtask);
        return newSubtask;
    }

    @Override
    public Task updateNameAndDescription(int id, String name, String description) {
        Task targetTask = getTaskById(id);
        targetTask.setName(name);
        targetTask.setDescription(description);
        return targetTask;
    }

    @Override
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

    @Override
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

    @Override
    public String deleteTaskById(int id) {
        if (this.tasks.containsKey(id)) {
            if (this.tasks.get(id) instanceof Subtask subtask) {
                int parentId = subtask.getParentId();
                Epic parentTask = (Epic) getTaskById(parentId);
                parentTask.removeSubTask(id);
            }

            if (this.tasks.get(id) instanceof Epic epic) {
                for (int subId : epic.getSubtasks()) {
                    tasks.remove(subId);
                }
            }
            this.tasks.remove(id);
            historyManager.remove(id);
            return String.format("Удаление успешно! Задача с ID: %d - удалена.", id);
        } else {
            return String.format("Удаление не удалось! Задача с ID: %d - не найдена.", id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}
