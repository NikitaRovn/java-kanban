package manager;

import main.java.manager.Managers;
import main.java.manager.TaskManager;
import main.java.tasks.Epic;
import main.java.tasks.Subtask;
import main.java.tasks.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicStatusTest {
    private TaskManager taskManager;
    private Epic testEpic1;
    private Subtask testSubtask1;
    private Subtask testSubtask2;
    private Subtask testSubtask3;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        testEpic1 = taskManager.createEpic("TestEpic1Name", "TestEpic1Description");
        testSubtask1 = taskManager.createSubtask("TestSubtask1Name", "TestSubtask1Description", testEpic1.getId());
        testSubtask2 = taskManager.createSubtask("TestSubtask2Name", "TestSubtask2Description", testEpic1.getId());
        testSubtask3 = taskManager.createSubtask("TestSubtask3Name", "TestSubtask3Description", testEpic1.getId());
    }

    @Test
    void shouldReturnNewIfAllSubtasksAreNew() {
        assertEquals(TaskStatus.NEW, testEpic1.getStatus(), "Все подзадачи NEW, эпик оказался не NEW.");
    }

    @Test
    void shouldReturnDoneIfAllSubtasksAreDone() {
        taskManager.updateStatus(testSubtask1.getId(), TaskStatus.DONE);
        taskManager.updateStatus(testSubtask2.getId(), TaskStatus.DONE);
        taskManager.updateStatus(testSubtask3.getId(), TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, testEpic1.getStatus(), "Все подзадачи DONE, эпик не DONE.");
    }

    @Test
    void shouldReturnInProgressIfSubtasksAreNewAndDone() {
        taskManager.updateStatus(testSubtask1.getId(), TaskStatus.NEW);
        taskManager.updateStatus(testSubtask2.getId(), TaskStatus.DONE);
        taskManager.updateStatus(testSubtask3.getId(), TaskStatus.NEW);

        assertEquals(TaskStatus.IN_PROGRESS, testEpic1.getStatus(), "Есть NEW и DONE подзадачи, эпик не IN_PROGRESS.");
    }

    @Test
    void shouldReturnInProgressIfAnySubtaskIsInProgress() {
        taskManager.updateStatus(testSubtask1.getId(), TaskStatus.IN_PROGRESS);
        taskManager.updateStatus(testSubtask2.getId(), TaskStatus.NEW);
        taskManager.updateStatus(testSubtask3.getId(), TaskStatus.DONE);

        assertEquals(TaskStatus.IN_PROGRESS, testEpic1.getStatus(), "Есть одна подзадача IN_PROGRESS, эпик не IN_PROGRESS.");
    }

    @Test
    void shouldReturnNewIfEpicHasNoSubtasks() {
        Epic testEpic2 = taskManager.createEpic("TestEpic2Name", "TestEpic2Description");
        assertEquals(TaskStatus.NEW, testEpic2.getStatus(), "Подзадач у эпика нет, а статус оказался не NEW.");
    }
}
