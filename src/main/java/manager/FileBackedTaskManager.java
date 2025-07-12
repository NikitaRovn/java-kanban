package main.java.manager;

import main.java.history.HistoryUtil;
import main.java.tasks.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path backupFile;
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparingInt(Task::getId)
    );


    public FileBackedTaskManager(Path backupFile) {
        this.backupFile = backupFile;
    }

    public static FileBackedTaskManager loadFromFile(Path file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.load();
        return manager;
    }

    public void save() {
        try (var writer = Files.newBufferedWriter(backupFile)) {
            writer.write("type;id;name;description;status;childsOrParent;startTime;duration;endTime");
            writer.newLine();

            for (Task task : getAllTasks().values()) {
                if (task instanceof Epic) {
                    writer.write(task.toString());
                    writer.newLine();
                }
            }
            for (Task task : getAllTasks().values()) {
                if (task instanceof Subtask) {
                    writer.write(task.toString());
                    writer.newLine();
                }
            }
            for (Task task : getAllTasks().values()) {
                if (!(task instanceof Epic) && !(task instanceof Subtask)) {
                    writer.write(task.toString());
                    writer.newLine();
                }
            }

            writer.newLine();

            List<Task> historyTasks = getHistory();
            List<Integer> historyIds = historyTasks.stream()
                    .map(Task::getId)
                    .toList();
            writer.write(HistoryUtil.toString(historyIds));
            writer.newLine();

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла", e);
        }
    }

    private void load() {
        if (!Files.exists(backupFile)) {
            return;
        }

        try (BufferedReader bufferedReader = Files.newBufferedReader(backupFile)) {
            bufferedReader.readLine();

            List<String> lines = new ArrayList<>();
            String line;

            while ((line = bufferedReader.readLine()) != null && !line.isBlank()) {
                lines.add(line);
            }

            for (String taskLine : lines) {
                String type = taskLine.split(";")[0];
                switch (TaskTypes.valueOf(type)) {
                    case EPIC -> {
                        Epic epic = Epic.fromString(taskLine);
                        getAllTasks().put(epic.getId(), epic);
                    }
                    case SUBTASK -> {
                        Subtask subtask = Subtask.fromString(taskLine);
                        getAllTasks().put(subtask.getId(), subtask);
                    }
                    case TASK -> {
                        Task task = Task.fromString(taskLine);
                        getAllTasks().put(task.getId(), task);
                    }
                }
            }

            for (Task task : getAllTasks().values()) {
                if (task instanceof Subtask subtask) {
                    Epic parent = (Epic) getAllTasks().get(subtask.getParentId());
                    if (parent != null) {
                        parent.addSubtask(subtask.getId());
                    }
                }
            }

            prioritizedTasks.clear();
            for (Task task : getAllTasks().values()) {
                updatePrioritizedTasks(task);
            }

            String historyLine = bufferedReader.readLine();
            if (historyLine != null && !historyLine.isBlank()) {
                List<Integer> history = HistoryUtil.fromString(historyLine);
                for (int id : history) {
                    Task task = getAllTasks().get(id);
                    if (task != null) {
                        historyManager.add(task);
                    }
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла", e);
        }
    }

    @Override
    public Task createTask(String name, String description) {
        Task task = super.createTask(name, description);

        if (isOverlappingWithExistingTasks(task)) {
            System.out.println("Пересечение по времени с другой задачей!");
            super.deleteTaskById(task.getId());
            return null;
        }

        updatePrioritizedTasks(task);
        save();
        return task;
    }

    @Override
    public Epic createEpic(String name, String description) {
        Epic epic = super.createEpic(name, description);
        updatePrioritizedTasks(epic);
        save();
        return epic;
    }

    @Override
    public Subtask createSubtask(String name, String description, int parentId) {
        Subtask subtask = super.createSubtask(name, description, parentId);

        if (subtask == null) {
            return null;
        }

        if (isOverlappingWithExistingTasks(subtask)) {
            System.out.println("Пересечение по времени с другой задачей!");
            super.deleteTaskById(subtask.getId());
            return null;
        }

        updatePrioritizedTasks(subtask);
        save();

        return subtask;
    }

    @Override
    public Task updateNameAndDescription(int id, String name, String description) {
        Task updatedTask = super.updateNameAndDescription(id, name, description);
        updatePrioritizedTasks(updatedTask);
        save();
        return updatedTask;
    }

    @Override
    public Task updateStatus(int id, TaskStatus status) {
        Task updatedTask = super.updateStatus(id, status);
        if (updatedTask != null) {
            updatePrioritizedTasks(updatedTask);
            save();
        }
        return updatedTask;
    }

    @Override
    public String deleteAllTasks() {
        String result = super.deleteAllTasks();
        prioritizedTasks.clear();
        save();
        return result;
    }

    @Override
    public String deleteTaskById(int id) {
        Task task = getAllTasks().get(id);
        if (task != null) {
            prioritizedTasks.remove(task);
        }
        String result = super.deleteTaskById(id);
        save();
        return result;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    private void updatePrioritizedTasks(Task task) {
        if (task == null) return;

        if (task instanceof Epic) {
            prioritizedTasks.remove(task);
            return;
        }

        prioritizedTasks.remove(task);

        if (task.getStartTime() != null && !task.getStartTime().equals(LocalDateTime.MIN)) {
            prioritizedTasks.add(task);
        }
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean isOverlappingWithExistingTasks(Task taskToCheck) {
        if (taskToCheck.getStartTime() == null || taskToCheck.getStartTime().equals(LocalDateTime.MIN)) {
            return false;
        }

        List<Task> sortedTasks = getPrioritizedTasks();

        return sortedTasks.stream()
                .filter(existingTask -> existingTask.getId() != taskToCheck.getId())
                .filter(existingTask -> !(existingTask instanceof Epic))
                .anyMatch(taskToCheck::overlaps);
    }
}
