package manager;

import main.java.manager.FileBackedTaskManager;
import main.java.manager.Managers;
import main.java.manager.TaskManager;
import main.java.tasks.Epic;
import main.java.tasks.Subtask;
import main.java.tasks.Task;
import main.java.tasks.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void testInitSetup() {
        taskManager = Managers.getDefault();
    }

    @Test
    void getAllTasks() {
        int initialTaskCount = taskManager.getAllTasks().size();

        Task testTask1 = taskManager.createTask("TestTask1Name", "TestTask1Description");
        Task testTask2 = taskManager.createTask("TestTask2Name", "TestTask2Description");

        assertEquals(initialTaskCount + 2, taskManager.getAllTasks().size(),
                "Количество задач должно увеличиться на 2.");

        assertTrue(taskManager.getAllTasks().containsValue(testTask1),
                "Список задач должен содержать testTask1.");
        assertTrue(taskManager.getAllTasks().containsValue(testTask2),
                "Список задач должен содержать testTask2.");
    }

    @Test
    void deleteAllTasks() {
        Task testTask1 = taskManager.createTask("TestTask1Name", "TestTask1Description");
        Task testTask2 = taskManager.createTask("TestTask2Name", "TestTask2Description");
        taskManager.deleteAllTasks();

        assertTrue(taskManager.getAllTasks().isEmpty(), "Список не очистился.");
    }

    @Test
    void getTaskById() {
        Task testTask1 = taskManager.createTask("TestTask1Name", "TestTask1Description");

        assertEquals(testTask1, taskManager.getTaskById(testTask1.getId()));
    }

    @Test
    void createTask() {
        Task testTask1 = taskManager.createTask("TestTask1Name", "TestTask1Description");
        int testTask1Id = testTask1.getId();
        Task testTask1Answer = taskManager.getTaskById(testTask1Id);

        assertEquals(testTask1, testTask1Answer, "Не нашел созданную обычную задачу.");
    }

    @Test
    void createEpic() {
        Task testEpic1 = taskManager.createEpic("TestEpic1Name", "TestEpic1Description");
        int testEpic1Id = testEpic1.getId();
        Epic testEpic1Answer = (Epic) taskManager.getTaskById(testEpic1Id);

        assertEquals(testEpic1, testEpic1Answer, "Не нашел созданную эпическую задачу.");
    }

    @Test
    void createSubtask() {
        Epic testEpic1 = taskManager.createEpic("TestEpic1Name", "TestEpic1Description");
        Subtask testSubtask1 = taskManager.createSubtask("TestSubtask1Name", "TestSubtask1Description", testEpic1.getId());
        int testSubtask1Id = testSubtask1.getId();
        Subtask testSubtask1Answer = (Subtask) taskManager.getTaskById(testSubtask1Id);

        assertEquals(testSubtask1, testSubtask1Answer, "Не нашел созданную подзадачу.");

    }

    @Test
    void updateNameAndDescription() {
        Task testEpic1 = taskManager.createEpic("TestEpic1Name", "TestEpic1Description");
        taskManager.updateNameAndDescription(testEpic1.getId(), "TestEpic1NameNEW", "TestEpic1DescriptionNEW");

        assertEquals("TestEpic1NameNEW", testEpic1.getName(), "Имя задачи не обновилось.");
        assertEquals("TestEpic1DescriptionNEW", testEpic1.getDescription(), "Описание задачи не обновилось.");
    }

    @Test
    void deleteTaskById() {
        Task testTask1 = taskManager.createTask("TestTask1Name", "TestTask1Description");
        taskManager.deleteTaskById(testTask1.getId());

        Assertions.assertNull(taskManager.getTaskById(0), "Задача не удалилась.");
    }

    @Test
    void getSubtasksById() {
        Task testEpic1 = taskManager.createEpic("TestEpic1Name", "TestEpic1Description");
        Subtask testSubtask1 = taskManager.createSubtask("TestSubtask1Name", "TestSubtask1Description", testEpic1.getId());
        Subtask testSubtask2 = taskManager.createSubtask("TestSubtask2Name", "TestSubtask2Description", testEpic1.getId());
        Set<Integer> subtasksListId = Set.of(testSubtask1.getId(), testSubtask2.getId());

        assertEquals(subtasksListId, taskManager.getSubtasksById(testEpic1.getId()));
    }

    @Test
    void shouldReturnTrueWhenTwoTasksHaveSameId() {
        Task testTask1 = taskManager.createTask("TestTask1Name", "TestTask1Description");
        int testTask1Id = testTask1.getId();
        Task testTask2 = taskManager.getTaskById(testTask1Id);

        assertEquals(testTask1, testTask2);
    }

    private Path tempFile;

    @Test
    void saveAndLoadEmptyFile() throws IOException {
        tempFile = Files.createTempFile("temp", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loaded.getAllTasks().isEmpty(), "Список не пустой.");
        assertTrue(loaded.getHistory().isEmpty(), "История не пустая.");
    }

    @Test
    void shouldSaveAndLoadMultipleTasks() throws IOException {
        Path tempFile = Files.createTempFile("temp", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task = manager.createTask("TestTask1Name", "TestTask1Description");
        Epic epic = manager.createEpic("TestEpic1Name", "TestEpic1Description");
        Subtask subtask = manager.createSubtask("TestSubtask1Name", "TestSubtask1description", epic.getId());

        manager.getTaskById(task.getId());
        manager.getTaskById(epic.getId());
        manager.getTaskById(subtask.getId());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        HashMap<Integer, Task> loadedTasks = loaded.getAllTasks();

        assertEquals(3, loadedTasks.size(), "Задачи не три.");
        assertTrue(loadedTasks.containsKey(task.getId()));
        assertTrue(loadedTasks.containsKey(epic.getId()));
        assertTrue(loadedTasks.containsKey(subtask.getId()));
    }

    @Test
    void workTempFile() throws IOException {
        Path tempFile = Files.createTempFile("temp", ".csv");
        assertTrue(Files.exists(tempFile), "Временный файл не существует.");

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        manager.createTask("TestTask1Name", "TestTask1Description");

        assertTrue(Files.size(tempFile) > 0, "Файл не содержит данные.");
    }

    @Test
    void correctlySerializeAndDeserializeTask() throws IOException {
        Path tempFile = Files.createTempFile("temp", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task original = manager.createTask("TestTask1Name", "TestTask1Description");
        manager.updateStatus(original.getId(), TaskStatus.DONE);
        manager.getTaskById(original.getId());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        Task deserialized = loaded.getAllTasks().get(original.getId());

        assertNotNull(deserialized, "Задача не загрузилась.");
        assertEquals(original.getName(), deserialized.getName());
        assertEquals(original.getDescription(), deserialized.getDescription());
        assertEquals(TaskStatus.DONE, deserialized.getStatus());
    }
}