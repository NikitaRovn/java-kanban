package history;

import main.java.history.HistoryManager;
import main.java.manager.Managers;
import main.java.model.Task;
import main.java.manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.List;

class InMemoryHistoryManagerTest {
    @Test
    void addHistory() {
        TaskManager taskManager = Managers.getDefault();
        Task testTask1 = taskManager.createTask("TestTask1Name", "TestTask1Description");
        Task testTask1Answer = taskManager.getTaskById(testTask1.getId());

        Assertions.assertEquals(testTask1, testTask1Answer, "Задача не создалась.");
    }

    @Test
    void getHistory() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task testTask1 = taskManager.createTask("TestTask1Name", "TestTask1Description");
        historyManager.addHistory(testTask1);
        Task testTask2 = taskManager.createTask("TestTask2Name", "TestTask2Description");
        historyManager.addHistory(testTask2);

        ArrayDeque<Task> tasks = new ArrayDeque<>(List.of(
                testTask1,
                testTask2
        ));

        Assertions.assertIterableEquals(tasks, historyManager.getHistory(), "Возвращен не тот список задач, что ожидался.");
    }
}