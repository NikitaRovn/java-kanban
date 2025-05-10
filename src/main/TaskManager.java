package main;

import java.util.Collection;
import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> tasksList = new HashMap<>();

    public Task addNewTask(String name, String description) {
        Task newTask = new Task(name, description);
        tasksList.put(newTask.getId(), newTask);
        return newTask;
    }

    public Collection<Task> getAllTasks() {
        return tasksList.values();
    }

    public boolean removeAllTasks() {
        tasksList.clear();
        return true;
    }

    public Task getTaskById(int taskId) {
        return tasksList.get(taskId);
    }

    public Collection<Task> getSubtaskByEpicId(int epicId) {
        Task targetTask = getTaskById(epicId);
        return targetTask.getEpicStatus() ? targetTask.getSubtasks() : null;
    }

    public Task updateTaskById(int taskId, String newName, String newDescription) {
        Task targetTask = getTaskById(taskId);
        targetTask.updateNameAndDescription(newName, newDescription);
        return targetTask;
    }

    public Task updateStatusTaskById(int taskId, int statusId) {
        Task targetTask = getTaskById(taskId);
        if (!targetTask.getEpicStatus()) {
            targetTask.updateStatus(statusId);
        }
        return targetTask;
    }

    public boolean removeTaskById(int taskId) {
        Task targetTask = getTaskById(taskId);
        tasksList.remove(taskId, targetTask);
        return true;
    }

    public Task setTaskAsSubtask(int childTaskId, int parentTaskId) {
        Task childTask = getTaskById(childTaskId);
        childTask.setAsSubtask(getTaskById(parentTaskId));
        return childTask;
    }

    public boolean fillTestData() {
        Task testTask1 = addNewTask("Погулять", "Наслаждаясь своим свободным временем и силами на это, взять себя в руки и направится на покорение ближайших и не очень дорог");
        Task testTask2 = addNewTask("Поспать", "Независимо от того продуктивный это был день или не очень, нужно поспать");
        Task testTask3 = addNewTask("Учеба", "Заняться прохождением курса обучения Яндекс Практикума");
        Task testTask4 = addNewTask("Открыть сайт", "Тебе требуется открыть сайт в интернете, например с помощью компьютера или телефона");
        setTaskAsSubtask(testTask4.getId(), testTask2.getId());
        Task testTask5 = addNewTask("Залогиниться", "Ввести свой логин и пароль на сайте чтобы пройти аутентификацию");
        setTaskAsSubtask(testTask5.getId(), testTask2.getId());
        Task testTask6 = addNewTask("Собраться", "Соберись с мыслями, выкинь всек лишнее из головы и приготовься морально к занятиям");
        setTaskAsSubtask(testTask6.getId(), testTask2.getId());

        return true;
    }
}
