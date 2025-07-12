package manager;

import main.java.manager.FileBackedTaskManager;
import main.java.tasks.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private Path tempFile;
    private FileBackedTaskManager fileBackedTaskManager;

    @BeforeEach
    void setUpFileManager() throws IOException {
        tempFile = Files.createTempFile("temp", ".csv");
        Files.writeString(tempFile, "");
        fileBackedTaskManager = new FileBackedTaskManager(tempFile);
        taskManager = fileBackedTaskManager;
    }

    @AfterEach
    void tearDownFileManager() throws IOException {
        taskManager = null;
        fileBackedTaskManager = null;
        Files.deleteIfExists(tempFile);
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return fileBackedTaskManager;
    }

    @Test
    void loadsEmptyManagerFromFile() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getAllTasks().isEmpty(), "Список задач должен быть пуст.");
        assertTrue(loadedManager.getHistory().isEmpty(), "История должна быть пустой.");
    }

    @Test
    void writesToFileWhenTaskCreated() throws IOException {
        assertTrue(Files.exists(tempFile), "Временный файл не был создан.");

        fileBackedTaskManager.createTask("TestTask1Name", "TestTask1Description");

        assertTrue(Files.size(tempFile) > 0, "Файл должен содержать данные после создания задачи.");
    }

    @Test
    void createsTasksWithoutOverlap() {
        Task testTask1 = fileBackedTaskManager.createTask("TestTask1Name", "TestTask1Description");
        fileBackedTaskManager.setStartTime(testTask1.getId(), "2025-07-15T10:00:00");
        fileBackedTaskManager.setDuration(testTask1.getId(), 60);

        Task testTask2 = fileBackedTaskManager.createTask("TestTask2Name", "TestTask2Description");
        fileBackedTaskManager.setStartTime(testTask2.getId(), "2025-07-15T11:00:01");
        fileBackedTaskManager.setDuration(testTask2.getId(), 60);

        assertNotNull(testTask2, "Задача должна быть создана, так как не пересекается с предыдущей.");
        assertEquals(2, fileBackedTaskManager.getAllTasks().size(), "В менеджере должно быть 2 задачи.");
    }

    @Test
    void createsTasksThatTouchBorders() {
        Task testTask1 = fileBackedTaskManager.createTask("TestTask1Name", "TestTask1Description");
        fileBackedTaskManager.setStartTime(testTask1.getId(), "2025-07-15T10:00:00");
        fileBackedTaskManager.setDuration(testTask1.getId(), 60);

        Task testTask2 = fileBackedTaskManager.createTask("TestTask2Name", "TestTask2Description");
        fileBackedTaskManager.setStartTime(testTask2.getId(), "2025-07-15T09:00:00");
        fileBackedTaskManager.setDuration(testTask2.getId(), 60);

        assertNotNull(testTask2, "Задача должна быть создана, так как заканчивается перед началом другой.");
        assertEquals(2, fileBackedTaskManager.getAllTasks().size());

        Task testTask3 = fileBackedTaskManager.createTask("TestTask3Name", "TestTask3Description");
        fileBackedTaskManager.setStartTime(testTask3.getId(), "2025-07-15T11:00:00");
        fileBackedTaskManager.setDuration(testTask3.getId(), 60);

        assertNotNull(testTask3, "Задача должна быть создана, так как начинается после окончания предыдущей.");
        assertEquals(3, fileBackedTaskManager.getAllTasks().size());
    }

    @Test
    void createsTasksWithoutTime() {
        Task testTask1 = fileBackedTaskManager.createTask("TestTask1Name", "TestTask1Description");
        Task testTask2 = fileBackedTaskManager.createTask("TestTask2Name", "TestTask2Description");

        assertNotNull(testTask1, "Первая задача без времени не была создана.");
        assertNotNull(testTask2, "Вторая задача без времени не была создана.");
        assertEquals(2, fileBackedTaskManager.getAllTasks().size(), "Обе задачи без времени должны быть добавлены.");

        Task testTask3 = fileBackedTaskManager.createTask("TestTask3Name", "TestTask3Description");
        fileBackedTaskManager.setStartTime(testTask3.getId(), "2025-07-15T10:00:00");
        fileBackedTaskManager.setDuration(testTask3.getId(), 30);

        assertNotNull(testTask3, "Задача со временем не была создана.");
        assertEquals(3, fileBackedTaskManager.getAllTasks().size(), "Всего должно быть 3 задачи.");
    }
}