package main;

import java.util.ArrayList;

public class Task {
    private static int taskCounter; // Счетчик общего количества задач, он же служит для задания номера новой задаче.

    private String name; // Название, кратко описывающее суть задачи (например, «Переезд»).
    private String description; // Описание, в котором раскрываются детали.
    private int id; // Уникальный идентификационный номер задачи, по которому её можно будет найти.
    private taskStatus status; // Статус, отображающий её прогресс.


    private boolean isEpic; // Является ли задача эпической (имеющей подзадачи).
    private ArrayList<Task> subtasks; // Перечень подзадач для эпической задачи.
    private boolean isSubtask; // Является ли задача подзадачей другой задачи.
    private Task parentTask; // Экземпляр класса который является эпиком для этой задачи.

    public Task(String name, String description) {
        taskCounter++;

        this.name = name;
        this.description = description;
        this.id = taskCounter;
        this.status = taskStatus.NEW;
        this.isEpic = false;
        this.subtasks = new ArrayList<>();
        this.isSubtask = false;
        this.parentTask = null;
    }

    public int getId() {
        return this.id;
    }

    public boolean getEpicStatus() {
        return isEpic;
    }

    public ArrayList<Task> getSubtasks() {
        return this.subtasks;
    }

    public void updateNameAndDescription(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public taskStatus getStatus() {
        return this.status;
    }

    public void updateStatus(int newStatus) {
        switch (newStatus) {
            case 1 -> status = taskStatus.NEW;
            case 2 -> status = taskStatus.IN_PROGRESS;
            case 3 -> status = taskStatus.DONE;
        }

        if (this.isSubtask) {
            epicStatusUpdater(this.parentTask);
        }
    }

    private void epicStatusUpdater(Task parentTask) {


        if (parentTask.getSubtasks().stream().allMatch(task -> task.getStatus() == taskStatus.NEW)) {
            parentTask.updateStatus(1);
        } else if (parentTask.getSubtasks().stream().allMatch(task -> task.getStatus() == taskStatus.DONE)) {
            parentTask.updateStatus(3);
        } else {
            parentTask.updateStatus(2);
        }
    }

    public void setAsSubtask(Task parentTask) {
        this.isSubtask = true;
        this.parentTask = parentTask;
        setAsEpicTask(parentTask, this);
    }

    private void setAsEpicTask(Task parentTask, Task subtask) {
        parentTask.isEpic = true;
        parentTask.addSubtask(subtask);
    }

    private void addSubtask(Task subtask) {
        this.subtasks.add(subtask);
    }

    @Override
    public String toString() {
        return String.format("Задача ID:%d. %s. Статус: %s. Имеет подзадачи (является эпической): %b. Является подзадачей задачи ID: %d.%n",
                this.id, this.name, this.status, this.isEpic, this.isSubtask ? this.parentTask.id : 0);
    }
}
