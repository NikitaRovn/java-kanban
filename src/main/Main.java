package main;

import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static TaskManager taskManager = new TaskManager();

    public static void main(String[] args) {
        while (true) {
            System.out.println("Список доступных команд:");
            System.out.println("1 - получить список всех задач.");
            System.out.println("2 - удалить все задачи.");
            System.out.println("3 - получить задачу по её ID.");
            System.out.println("4 - создать новую задачу.");
            System.out.println("5 - задать новое наименование и описание задаче.");
            System.out.println("6 - удалить задачу по её ID.");
            System.out.println("7 - изменить статус задачи (только не для эпических).");
            System.out.println("9 - заполнить программу тестовыми данными.");

            switch (commandReader()) {
                case 1 -> getAllTask();
                case 2 -> removeAllTasks();
                case 3 -> getTaskById();
                case 4 -> addTask();
                case 5 -> updateTask();
                case 6 -> removeTask();
                case 7 -> updateTaskStatus();
                case 9 -> taskManager.fillTestData();
            }
        }



    }

    private static int commandReader() {
        int command = scanner.nextInt();
        scanner.nextLine();
        return command;
    }

    public static void getAllTask() {
        System.out.println(taskManager.getAllTasks());
    }

    public static void removeAllTasks() {
        System.out.println(taskManager.removeAllTasks());
    }

    public static void getTaskById() {
        System.out.println("Ввести ID:");
        int taskId = commandReader();
        System.out.println(taskManager.getTaskById(taskId));
    }

    public static void addTask() {
        System.out.println("Ввести наименование задачи:");
        String name = scanner.nextLine();
        System.out.println("Ввести описание задачи:");
        String description = scanner.nextLine();
        System.out.println(taskManager.addNewTask(name, description));
    }

    public static void updateTask() {
        System.out.println("Ввести ID задачи:");
        int taskId = commandReader();
        System.out.println("Ввести новое наименование задачи:");
        String newName = scanner.nextLine();
        System.out.println("Ввести новое описание задачи:");
        String newDescription = scanner.nextLine();
        System.out.println(taskManager.updateTaskById(taskId, newName, newDescription));
    }

    public static void removeTask() {
        System.out.println("Ввести ID задачи:");
        int taskId = commandReader();
        System.out.println(taskManager.removeTaskById(taskId));
    }

    public static void updateTaskStatus() {
        System.out.println("Ввести ID задачи:");
        int taskId = commandReader();
        System.out.println("Ввести новый статус задачи (1 - новая, 2 - в процессе, 3 - завершена):");
        int taskStatus = commandReader();
        System.out.println(taskManager.updateStatusTaskById(taskId, taskStatus));
    }
}
