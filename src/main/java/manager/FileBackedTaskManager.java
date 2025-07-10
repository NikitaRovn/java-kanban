package main.java.manager;

import main.java.history.HistoryUtil;
import main.java.tasks.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path backupFile;

    public FileBackedTaskManager(Path backupFile) {
        this.backupFile = backupFile;
    }

    public static FileBackedTaskManager loadFromFile(Path file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.load();
        return manager;
    }

    private void save() {
        try (var writer = Files.newBufferedWriter(backupFile)) {
            writer.write("type;id;name;description;status;childsOrParent");
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
                    .collect(Collectors.toList());
            writer.write(HistoryUtil.toString(historyIds));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла", e);
        }
    }


    private void load() {
        if (!Files.exists(backupFile)) {
            return;
        }

        try (BufferedReader bufferedReader = Files.newBufferedReader(backupFile)) {
            String line = bufferedReader.readLine();
            if (line == null) return;

            List<String> lines = new ArrayList<>();
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
                        Epic parent = (Epic) getAllTasks().get(subtask.getParentId());
                        if (parent != null) {
                            parent.addSubtask(subtask.getId());
                        }
                    }
                    case TASK -> {
                        Task task = Task.fromString(taskLine);
                        getAllTasks().put(task.getId(), task);
                    }
                }
            }

            String historyLine = bufferedReader.readLine();
            if (historyLine != null) {
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
        save();
        return task;
    }

    @Override
    public Epic createEpic(String name, String description) {
        Epic epic = super.createEpic(name, description);
        save();
        return epic;
    }

    @Override
    public Subtask createSubtask(String name, String description, int parentId) {
        Subtask subtask = super.createSubtask(name, description, parentId);
        if (subtask != null) {
            save();
        }
        return subtask;
    }

    @Override
    public Task updateNameAndDescription(int id, String name, String description) {
        Task updatedTask = super.updateNameAndDescription(id, name, description);
        save();
        return updatedTask;
    }

    @Override
    public Task updateStatus(int id, TaskStatus status) {
        Task updatedTask = super.updateStatus(id, status);
        if (updatedTask != null) {
            save();
        }
        return updatedTask;
    }

    @Override
    public String deleteAllTasks() {
        String result = super.deleteAllTasks();
        save();
        return result;
    }

    @Override
    public String deleteTaskById(int id) {
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
}
