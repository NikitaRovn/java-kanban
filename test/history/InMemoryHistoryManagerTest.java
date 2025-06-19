package history;

import main.java.history.HistoryManager;
import main.java.manager.Managers;
import main.java.tasks.Task;
import main.java.manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {
    @Test
    void add() {
        TaskManager taskManager = Managers.getDefault();
        Task testTask1 = taskManager.createTask("TestTask1Name", "TestTask1Description");
        Task testTask1Answer = taskManager.getTaskById(testTask1.getId());

        assertEquals(testTask1, testTask1Answer, "Задача не создалась.");
    }

    @Test
    void getHistory() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task testTask1 = taskManager.createTask("TestTask1Name", "TestTask1Description");
        historyManager.add(testTask1);
        Task testTask2 = taskManager.createTask("TestTask2Name", "TestTask2Description");
        historyManager.add(testTask2);

        ArrayDeque<Task> tasks = new ArrayDeque<>(List.of(
                testTask1,
                testTask2
        ));

        Assertions.assertIterableEquals(tasks, historyManager.getHistory(), "Возвращен не тот список задач, что ожидался.");
    }

    @Test
    void addTasksAndCheck() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task testTask1 = new Task(1, "TestTask1Name", "TestTask1Description");
        historyManager.add(testTask1);
        Task testTask2 = new Task(2, "TestTask2Name", "TestTask2Description");
        historyManager.add(testTask2);
        Task testTask3 = new Task(3, "TestTask3Name", "TestTask3Description");
        historyManager.add(testTask3);

        assertEquals(List.of(testTask1, testTask2, testTask3), historyManager.getHistory());
    }

    @Test
    void addTasksAndRemove() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task testTask1 = new Task(1, "TestTask1Name", "TestTask1Description");
        historyManager.add(testTask1);
        Task testTask2 = new Task(2, "TestTask2Name", "TestTask2Description");
        historyManager.add(testTask2);
        Task testTask3 = new Task(3, "TestTask3Name", "TestTask3Description");
        historyManager.add(testTask3);

        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(2, history.getFirst().getId());
    }

    @Test
    void notAddDuplicateTaskInHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task testTask1 = new Task(1, "TestTask1Name", "TestTask1Description");
        historyManager.add(testTask1);
        Task testTask2 = new Task(2, "TestTask2Name", "TestTask2Description");
        historyManager.add(testTask2);
        Task testTask3 = new Task(3, "TestTask3Name", "TestTask3Description");
        historyManager.add(testTask3);
        Task testTask2Duplicate = new Task(2, "TestTask2DuplicateName", "TestTask2DuplicateDescription");
        historyManager.add(testTask2Duplicate);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(testTask2Duplicate, history.get(2));
    }

    @Test
    void doNothingIfRemovingNotExistingTask() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task testTask1 = new Task(1, "TestTask1Name", "TestTask1Description");
        historyManager.add(testTask1);

        historyManager.remove(2);

        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void deleteLastTask() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task testTask1 = new Task(1, "TestTask1Name", "TestTask1Description");
        historyManager.add(testTask1);

        historyManager.remove(1);

        assertTrue(historyManager.getHistory().isEmpty());
    }
}