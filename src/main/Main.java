package main;

import main.java.manager.Managers;
import main.java.manager.TaskManager;
import main.java.tasks.Task;
import main.java.tasks.TaskStatus;

import java.util.*;

public class Main {
    private static final TaskManager taskManager = Managers.getDefault();
    // private static final HistoryManager historyManager = Managers.getDefaultHistory();

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {


        while (true) {
            System.out.println("-------------------------------------------------------");
            System.out.println("Список доступных команд:");
            System.out.println("0 - выход из программы.");
            System.out.println("1 - получить список всех задач.");
            System.out.println("2 - удалить все задачи.");
            System.out.println("3 - получить задачу по её ID.");
            System.out.println("4 - создать новую задачу.");
            System.out.println("5 - задать новое наименование и описание задаче.");
            System.out.println("6 - удалить задачу по её ID.");
            System.out.println("7 - изменить статус задачи (только не для эпических).");
            System.out.println("8 - получение списка подзадач эпической задачи.");
            System.out.println("9 - ...");
            System.out.println("10 - Вывести историю просмотров.");

            switch (commandReader()) {
                case 0 -> {
                    return;
                }
                case 1 -> {
                    HashMap<Integer, Task> tasks = taskManager.getAllTasks();
                    for (Task task : tasks.values()) {
                        System.out.println(task);
                    }
                }
                case 2 -> System.out.println(taskManager.deleteAllTasks());
                case 3 -> {
                    System.out.println("Введите ID задачи:");
                    int taskId = commandReader();
                    System.out.println(taskManager.getTaskById(taskId));
                }
                case 4 -> {
                    System.out.println("Введите тип задачи задачи (1 - обычный, 2 - эпический, 3 - подзадача):");
                    int taskType = commandReader();
                    System.out.println("Введите наименование задачи:");
                    String name = scanner.nextLine();
                    System.out.println("Введите описание задачи:");
                    String description = scanner.nextLine();
                    switch (taskType) {
                        case 1 -> System.out.println(taskManager.createTask(name, description));
                        case 2 -> System.out.println(taskManager.createEpic(name, description));
                        case 3 -> {
                            System.out.println("Введите ID эпической (родительской) задачи:");
                            int parentId = commandReader();
                            System.out.println(taskManager.createSubtask(name, description, parentId));
                        }
                    }
                }
                case 5 -> {
                    System.out.println("Введите ID задачи:");
                    int targetId = commandReader();
                    System.out.println("Введите наименование задачи:");
                    String name = scanner.nextLine();
                    System.out.println("Введите описание задачи:");
                    String description = scanner.nextLine();
                    System.out.println(taskManager.updateNameAndDescription(targetId, name, description));
                }
                case 6 -> {
                    System.out.println("Введите ID задачи:");
                    int targetId = commandReader();
                    System.out.println(taskManager.deleteTaskById(targetId));
                }
                case 7 -> {
                    System.out.println("Введите ID задачи:");
                    int targetId = commandReader();
                    System.out.println("Введите новый статус задачи (1 - NEW, 2 - IN_PROGRESS, 3 - DONE):");
                    int status = commandReader();
                    switch (status) {
                        case 1 -> System.out.println(taskManager.updateStatus(targetId, TaskStatus.NEW));
                        case 2 -> System.out.println(taskManager.updateStatus(targetId, TaskStatus.IN_PROGRESS));
                        case 3 -> System.out.println(taskManager.updateStatus(targetId, TaskStatus.DONE));
                        default -> System.out.println("Неверная команда.");
                    }
                }
                case 8 -> {
                    System.out.println("Введите ID задачи:");
                    int targetId = commandReader();
                    Set<Integer> subtasksId = taskManager.getSubtasksById(targetId);
                    for (int i : subtasksId) {
                        System.out.println(taskManager.getTaskById(i));
                    }
                }
                case 10 -> {
                    List<Task> result = taskManager.getHistory();
                    System.out.println(result);
                }
                default -> {
                    System.out.println("Неверная команда, введите число!");
                }
            }
        }
    }

    private static int commandReader() {
        while (!scanner.hasNextInt()) {
            System.out.println("Вы ввели не число!");
            scanner.next();
        }

        int command = scanner.nextInt();
        scanner.nextLine();
        return command;
    }
}
