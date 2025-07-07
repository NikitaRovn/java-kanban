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
        Task testTask1 = taskManager.createTask("TestTask1Name", "TestTask1Description");
        Task testTask2 = taskManager.createTask("TestTask2Name", "TestTask2Description");
        HashMap<Integer, Task> tasksAnswer = taskManager.getAllTasks();
        HashMap<Integer, Task> tasks = new HashMap<>() {{
            put(testTask1.getId(), testTask1);
            put(testTask2.getId(), testTask2);
        }};

        Assertions.assertEquals(tasks, tasksAnswer, "Полученный список всех задач не совпадает с поданным.");
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

        Assertions.assertEquals(testTask1, taskManager.getTaskById(0));
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
        Task testEpic1 = taskManager.createEpic("TestEpic1Name", "TestEpic1Description");
        Subtask testSubtask1 = taskManager.createSubtask("TestSubtask1Name", "TestSubtask1Description", 0);
        int testSubtask1Id = testSubtask1.getId();
        Subtask testSubtask1Answer = (Subtask) taskManager.getTaskById(testSubtask1Id);

        Assertions.assertEquals(testSubtask1, testSubtask1Answer, "Не нашел созданную подзадачу.");

    }

    @Test
    void updateNameAndDescription() {
        Task testEpic1 = taskManager.createEpic("TestEpic1Name", "TestEpic1Description");
        taskManager.updateNameAndDescription(0, "TestEpic1NameNEW", "TestEpic1DescriptionNEW");

        Assertions.assertEquals("TestEpic1NameNEW", taskManager.getTaskById(0).getName(), "Имя задачи не обновилось.");
        Assertions.assertEquals("TestEpic1DescriptionNEW", taskManager.getTaskById(0).getDescription(), "Описание задачи не обновилось.");
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
        Subtask testSubtask1 = taskManager.createSubtask("TestSubtask1Name", "TestSubtask1Description", 0);
        Subtask testSubtask2 = taskManager.createSubtask("TestSubtask2Name", "TestSubtask2Description", 0);
        Set<Integer> subtasksListId = Set.of(1, 2);

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