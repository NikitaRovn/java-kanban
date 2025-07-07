package main.java.manager;

import main.java.history.HistoryManager;
import main.java.history.InMemoryHistoryManager;

import java.nio.file.Path;

public class Managers {
    public static TaskManager getDefault() {
        Path projectRoot = Path.of("").toAbsolutePath();
        Path backupFilePath = projectRoot.resolve("backupFile.csv");
        return FileBackedTaskManager.loadFromFile(backupFilePath);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
