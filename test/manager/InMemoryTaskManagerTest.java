package manager;

import main.java.manager.Managers;
import main.java.manager.TaskManager;
import main.java.tasks.Epic;
import main.java.tasks.Subtask;
import main.java.tasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Set;

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

        Assertions.assertEquals(initialTaskCount + 2, taskManager.getAllTasks().size(),
                "Количество задач должно увеличиться на 2.");

        Assertions.assertTrue(taskManager.getAllTasks().containsValue(testTask1),
                "Список задач должен содержать testTask1.");
        Assertions.assertTrue(taskManager.getAllTasks().containsValue(testTask2),
                "Список задач должен содержать testTask2.");
    }

    @Test
    void deleteAllTasks() {
        Task testTask1 = taskManager.createTask("TestTask1Name", "TestTask1Description");
        Task testTask2 = taskManager.createTask("TestTask2Name", "TestTask2Description");
        taskManager.deleteAllTasks();

        Assertions.assertTrue(taskManager.getAllTasks().isEmpty(), "Список не очистился.");
    }

    @Test
    void getTaskById() {
        Task testTask1 = taskManager.createTask("TestTask1Name", "TestTask1Description");

        Assertions.assertEquals(testTask1, taskManager.getTaskById(testTask1.getId()));
    }

    @Test
    void createTask() {
        Task testTask1 = taskManager.createTask("TestTask1Name", "TestTask1Description");
        int testTask1Id = testTask1.getId();
        Task testTask1Answer = taskManager.getTaskById(testTask1Id);

        Assertions.assertEquals(testTask1, testTask1Answer, "Не нашел созданную обычную задачу.");
    }

    @Test
    void createEpic() {
        Task testEpic1 = taskManager.createEpic("TestEpic1Name", "TestEpic1Description");
        int testEpic1Id = testEpic1.getId();
        Epic testEpic1Answer = (Epic) taskManager.getTaskById(testEpic1Id);

        Assertions.assertEquals(testEpic1, testEpic1Answer, "Не нашел созданную эпическую задачу.");
    }

    @Test
    void createSubtask() {
        Epic testEpic1 = taskManager.createEpic("TestEpic1Name", "TestEpic1Description");
        Subtask testSubtask1 = taskManager.createSubtask("TestSubtask1Name", "TestSubtask1Description", testEpic1.getId());
        int testSubtask1Id = testSubtask1.getId();
        Subtask testSubtask1Answer = (Subtask) taskManager.getTaskById(testSubtask1Id);

        Assertions.assertEquals(testSubtask1, testSubtask1Answer, "Не нашел созданную подзадачу.");

    }

    @Test
    void updateNameAndDescription() {
        Task testEpic1 = taskManager.createEpic("TestEpic1Name", "TestEpic1Description");
        taskManager.updateNameAndDescription(testEpic1.getId(), "TestEpic1NameNEW", "TestEpic1DescriptionNEW");

        Assertions.assertEquals("TestEpic1NameNEW", testEpic1.getName(), "Имя задачи не обновилось.");
        Assertions.assertEquals("TestEpic1DescriptionNEW", testEpic1.getDescription(), "Описание задачи не обновилось.");
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

        Assertions.assertEquals(subtasksListId, taskManager.getSubtasksById(testEpic1.getId()));
    }

    @Test
    void shouldReturnTrueWhenTwoTasksHaveSameId() {
        Task testTask1 = taskManager.createTask("TestTask1Name", "TestTask1Description");
        int testTask1Id = testTask1.getId();
        Task testTask2 = taskManager.getTaskById(testTask1Id);

        Assertions.assertEquals(testTask1, testTask2);
    }
}