package manager;

import main.java.manager.TaskManager;
import main.java.tasks.Epic;
import main.java.tasks.Subtask;
import main.java.tasks.Task;
import main.java.tasks.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    void getAllTasksReturnsAllCreated() {
        int initialTaskCount = taskManager.getAllTasks().size();

        Task firstCreatedTask = taskManager.createTask("TestTask1Name", "TestTask1Description");
        Task secondCreatedTask = taskManager.createTask("TestTask2Name", "TestTask2Description");

        assertEquals(initialTaskCount + 2, taskManager.getAllTasks().size(), "Количество задач после добавления должно увеличиться на 2.");
        assertTrue(taskManager.getAllTasks().containsValue(firstCreatedTask), "Первая задача должна присутствовать в менеджере.");
        assertTrue(taskManager.getAllTasks().containsValue(secondCreatedTask), "Вторая задача должна присутствовать в менеджере.");
    }

    @Test
    void deleteAllTasksRemovesAll() {
        taskManager.createTask("TestTask1Name", "TestTask1Description");
        taskManager.createTask("TestTask2Name", "TestTask2Description");

        taskManager.deleteAllTasks();

        assertTrue(taskManager.getAllTasks().isEmpty(), "Все задачи должны быть удалены.");
    }

    @Test
    void getTaskByIdReturnsCorrectTask() {
        Task createdTask = taskManager.createTask("TestTask1Name", "TestTask1Description");

        Task foundTask = taskManager.getTaskById(createdTask.getId());
        assertEquals(createdTask, foundTask, "Найденная задача должна совпадать с созданной.");
    }

    @Test
    void createTaskAddsTask() {
        Task createdTask = taskManager.createTask("TestTask1Name", "TestTask1Description");
        Task retrievedTask = taskManager.getTaskById(createdTask.getId());

        assertEquals(createdTask, retrievedTask, "Созданная и извлечённая задачи должны совпадать.");
    }

    @Test
    void createEpicAddsEpic() {
        Epic createdEpic = taskManager.createEpic("TestEpic1Name", "TestEpic1Description");
        Epic retrievedEpic = (Epic) taskManager.getTaskById(createdEpic.getId());

        assertEquals(createdEpic, retrievedEpic, "Созданный и извлечённый эпик должны совпадать.");
    }

    @Test
    void createSubtaskAddsSubtask() {
        Epic parentEpic = taskManager.createEpic("TestEpic1Name", "TestEpic1Description");
        Subtask createdSubtask = taskManager.createSubtask("TestSubTask1Name", "TestSubTask1Description", parentEpic.getId());
        Subtask retrievedSubtask = (Subtask) taskManager.getTaskById(createdSubtask.getId());

        assertEquals(createdSubtask, retrievedSubtask, "Созданная и извлечённая подзадача должны совпадать.");
        assertNotNull(taskManager.getTaskById(createdSubtask.getParentId()), "Родительский эпик должен существовать.");
    }

    @Test
    void updateTaskNameAndDescriptionChangesFields() {
        Task taskToUpdate = taskManager.createTask("TestTask1Name", "TestTask1Description");
        taskManager.updateNameAndDescription(taskToUpdate.getId(), "TestTask1NameUpdated", "TestTask1DescriptionUpdated");

        assertEquals("TestTask1NameUpdated", taskToUpdate.getName(), "Имя задачи должно обновиться.");
        assertEquals("TestTask1DescriptionUpdated", taskToUpdate.getDescription(), "Описание задачи должно обновиться.");
    }

    @Test
    void deleteTaskByIdRemovesTask() {
        Task taskToDelete = taskManager.createTask("TestTask1Name", "TestTask1Description");
        taskManager.deleteTaskById(taskToDelete.getId());

        assertNull(taskManager.getTaskById(taskToDelete.getId()), "Задача должна быть удалена.");
    }

    @Test
    void getSubtasksByEpicIdReturnsCorrectSet() {
        Epic parentEpic = taskManager.createEpic("TestEpic1Name", "TestEpic1Description");
        Subtask firstSubtask = taskManager.createSubtask("TestSubTask1Name", "TestSubTask1Description", parentEpic.getId());
        Subtask secondSubtask = taskManager.createSubtask("TestSubTask2Name", "TestSubTask2Description", parentEpic.getId());

        Set<Integer> expectedSubtaskIds = Set.of(firstSubtask.getId(), secondSubtask.getId());
        assertEquals(expectedSubtaskIds, taskManager.getSubtasksById(parentEpic.getId()), "Набор ID подзадач должен совпадать.");
    }

    @Test
    void tasksWithSameIdAreEqual() {
        Task originalTask = taskManager.createTask("TestTask1Name", "TestTask1Description");
        Task retrievedTask = taskManager.getTaskById(originalTask.getId());

        assertEquals(originalTask, retrievedTask, "Задачи с одинаковым ID должны быть равны.");
    }

    @Test
    void updateTaskStatusChangesStatus() {
        Task taskToUpdate = taskManager.createTask("TestTask1Name", "TestTask1Description");
        taskManager.updateStatus(taskToUpdate.getId(), TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, taskManager.getTaskById(taskToUpdate.getId()).getStatus(), "Статус задачи должен обновиться на DONE.");
    }

    @Test
    void updateSubtaskStatusChangesStatus() {
        Epic parentEpic = taskManager.createEpic("TestEpic1Name", "TestEpic1Description");
        Subtask subtaskToUpdate = taskManager.createSubtask("TestSubTask1Name", "TestSubTask1Description", parentEpic.getId());
        taskManager.updateStatus(subtaskToUpdate.getId(), TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, taskManager.getTaskById(subtaskToUpdate.getId()).getStatus(), "Статус подзадачи должен обновиться на DONE.");
    }

    @Test
    void epicStatusNewWhenAllSubtasksNew() {
        Epic parentEpic = taskManager.createEpic("TestEpic1Name", "TestEpic1Description");
        taskManager.createSubtask("TestSubTask1Name", "TestSubTask1Description", parentEpic.getId());
        taskManager.createSubtask("TestSubTask2Name", "TestSubTask2Description", parentEpic.getId());

        assertEquals(TaskStatus.NEW, taskManager.getTaskById(parentEpic.getId()).getStatus(), "Если все подзадачи NEW, эпик должен быть NEW.");
    }

    @Test
    void epicStatusDoneWhenAllSubtasksDone() {
        Epic parentEpic = taskManager.createEpic("TestEpic1Name", "TestEpic1Description");
        Subtask firstSubtask = taskManager.createSubtask("TestSubTask1Name", "TestSubTask1Description", parentEpic.getId());
        Subtask secondSubtask = taskManager.createSubtask("TestSubTask2Name", "TestSubTask2Description", parentEpic.getId());

        taskManager.updateStatus(firstSubtask.getId(), TaskStatus.DONE);
        taskManager.updateStatus(secondSubtask.getId(), TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, taskManager.getTaskById(parentEpic.getId()).getStatus(), "Если все подзадачи DONE, эпик должен быть DONE.");
    }

    @Test
    void epicStatusInProgressWhenSubtasksNewAndDone() {
        Epic parentEpic = taskManager.createEpic("TestEpic1Name", "TestEpic1Description");
        Subtask newSubtask = taskManager.createSubtask("TestSubTask1Name", "TestSubTask1Description", parentEpic.getId());
        Subtask doneSubtask = taskManager.createSubtask("TestSubTask2Name", "TestSubTask2Description", parentEpic.getId());

        taskManager.updateStatus(newSubtask.getId(), TaskStatus.NEW);
        taskManager.updateStatus(doneSubtask.getId(), TaskStatus.DONE);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTaskById(parentEpic.getId()).getStatus(), "Если есть NEW и DONE подзадачи, эпик должен быть IN_PROGRESS.");
    }

    @Test
    void epicStatusInProgressWhenAnySubtaskInProgress() {
        Epic parentEpic = taskManager.createEpic("TestEpic1Name", "TestEpic1Description");
        Subtask inProgressSubtask = taskManager.createSubtask("TestSubTask1Name", "TestSubTask1Description", parentEpic.getId());
        Subtask newSubtask = taskManager.createSubtask("TestSubTask2Name", "TestSubTask2Description", parentEpic.getId());

        taskManager.updateStatus(inProgressSubtask.getId(), TaskStatus.IN_PROGRESS);
        taskManager.updateStatus(newSubtask.getId(), TaskStatus.NEW);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTaskById(parentEpic.getId()).getStatus(), "Если есть IN_PROGRESS подзадача, эпик должен быть IN_PROGRESS.");
    }

    @Test
    void epicStatusNewWhenNoSubtasks() {
        Epic epicWithoutSubtasks = taskManager.createEpic("TestEpic1Name", "TestEpic1Description");
        assertEquals(TaskStatus.NEW, taskManager.getTaskById(epicWithoutSubtasks.getId()).getStatus(), "Если подзадач нет, эпик должен быть NEW.");
    }

    @Test
    void historyAddsUniqueTasksInOrder() {
        Task firstViewedTask = taskManager.createTask("TestTask1Name", "TestTask1Description");
        Task secondViewedTask = taskManager.createTask("TestTask2Name", "TestTask2Description");

        taskManager.getTaskById(firstViewedTask.getId());
        taskManager.getTaskById(secondViewedTask.getId());
        taskManager.getTaskById(firstViewedTask.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать только уникальные задачи.");
        assertEquals(secondViewedTask, history.get(0), "Последняя просмотренная задача должна быть первой в истории.");
        assertEquals(firstViewedTask, history.get(1), "Первая просмотренная задача должна быть второй в истории.");
    }

    @Test
    void historyRemovesDeletedTasks() {
        Task firstCreatedTask = taskManager.createTask("TestTask1Name", "TestTask1Description");
        Task secondCreatedTask = taskManager.createTask("TestTask2Name", "TestTask2Description");
        Task thirdCreatedTask = taskManager.createTask("TestTask3Name", "TestTask3Description");

        taskManager.getTaskById(firstCreatedTask.getId());
        taskManager.getTaskById(secondCreatedTask.getId());
        taskManager.getTaskById(thirdCreatedTask.getId());

        taskManager.deleteTaskById(secondCreatedTask.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size(), "История должна уменьшиться после удаления задачи.");
        assertFalse(history.contains(secondCreatedTask), "Удалённая задача не должна присутствовать в истории.");
    }
}