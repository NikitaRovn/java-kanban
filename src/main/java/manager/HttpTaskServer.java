package main.java.manager;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import main.java.tasks.Epic;
import main.java.tasks.Subtask;
import main.java.tasks.Task;
import main.java.tasks.TaskDto;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpTaskServer {
    public final HttpServer server;
    private final int port;
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    private final FileBackedTaskManager taskManager;

    public static void main(String[] args) throws IOException {
        FileBackedTaskManager taskManager = (FileBackedTaskManager) Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.start();
    }


    public HttpTaskServer(FileBackedTaskManager taskManager) throws IOException {
        this.port = 8080;
        this.taskManager = taskManager;
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/tasks", new TasksHandler(taskManager));
        server.createContext("/subtasks", new SubtasksHandler(taskManager));
        server.createContext("/epics", new EpicsHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizesHandler(taskManager));
    }

    public void start() {
        server.start();
        System.out.printf("HTTP-сервер запущен на порту: %d%n", port);
    }

    public void stop() {
        server.stop(1);
        System.out.println("HTTP-сервер остановлен.");
    }

    public FileBackedTaskManager getTaskManager() {
        return taskManager;
    }

    static class TasksHandler extends BaseHttpHandler implements HttpHandler {
        private final TaskManager taskManager;

        public TasksHandler(TaskManager taskManager) {
            this.taskManager = taskManager;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            InputStream inputStream;
            String body;
            Task task;
            int id;
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            String method = exchange.getRequestMethod().toUpperCase();

            if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
                switch (method) {
                    case "GET":
                        HashMap<Integer, Task> tasks = getSimpleTasks(taskManager);
                        String response = gson.toJson(tasks);

                        sendText(exchange, response, 200);

                        break;
                    case "POST":
                        inputStream = exchange.getRequestBody();
                        body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                        TaskDto taskDto = gson.fromJson(body, TaskDto.class);

                        if (taskDto.getId() != null) {
                            task = taskManager.updateNameAndDescription(taskDto.getId(), taskDto.getName(), taskDto.getDescription());
                            sendText(exchange, gson.toJson(task), 201);
                        } else {
                            task = taskManager.createTask(taskDto.getName(), taskDto.getDescription());
                            if (task == null) {
                                sendHasInteractions(exchange, "Задача пересекается с существующими.");
                            } else {
                                sendText(exchange, gson.toJson(task), 201);
                            }
                        }
                        break;
                    default:
                        sendText(exchange, "Запрос не распознан.", 500);
                        break;
                }

            } else if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
                try {
                    id = Integer.parseInt(pathParts[2]);
                } catch (NumberFormatException e) {
                    sendText(exchange, "Некорректный id: " + pathParts[2], 400);
                    return;
                }

                switch (method) {
                    case "GET":
                        task = taskManager.getTaskById(id);

                        if (task == null) {
                            sendNotFound(exchange, "Задачи с id: " + id + "нет.");
                        } else {
                            sendText(exchange, gson.toJson(task), 200);
                        }

                        break;
                    case "DELETE":
                        String result = taskManager.deleteTaskById(id);

                        sendText(exchange, result, 200);

                        break;
                    default:
                        break;
                }

            } else {
                exchange.sendResponseHeaders(404, 0);
                exchange.close();
            }


        }
    }

    static class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
        private final TaskManager taskManager;

        public SubtasksHandler(TaskManager taskManager) {
            this.taskManager = taskManager;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            InputStream inputStream;
            String body;
            Subtask subtask;
            int id;
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            String method = exchange.getRequestMethod().toUpperCase();

            if (pathParts.length == 2 && pathParts[1].equals("subtasks")) {
                switch (method) {
                    case "GET":
                        HashMap<Integer, Subtask> tasks = getSubtasks(taskManager);
                        String response = gson.toJson(tasks);

                        sendText(exchange, response, 200);

                        break;
                    case "POST":
                        inputStream = exchange.getRequestBody();
                        body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                        TaskDto taskDto = gson.fromJson(body, TaskDto.class);

                        if (taskDto.getId() != null) {
                            subtask = (Subtask) taskManager.updateNameAndDescription(taskDto.getId(), taskDto.getName(), taskDto.getDescription());
                            sendText(exchange, gson.toJson(subtask), 201);
                        } else {
                            subtask = taskManager.createSubtask(taskDto.getName(), taskDto.getDescription(), taskDto.getParentId());
                            if (subtask == null) {
                                sendHasInteractions(exchange, "Подзадача пересекается с существующими.");
                            } else {
                                sendText(exchange, gson.toJson(subtask), 201);
                            }
                        }
                        break;
                    default:
                        sendText(exchange, "Запрос не распознан.", 500);
                        break;
                }

            } else if (pathParts.length == 3 && pathParts[1].equals("subtasks")) {
                try {
                    id = Integer.parseInt(pathParts[2]);
                } catch (NumberFormatException e) {
                    sendText(exchange, "Некорректный id: " + pathParts[2], 400);
                    return;
                }

                switch (method) {
                    case "GET":
                        subtask = (Subtask) taskManager.getTaskById(id);

                        if (subtask == null) {
                            sendNotFound(exchange, "Задачи с id: " + id + "нет.");
                        } else {
                            sendText(exchange, gson.toJson(subtask), 200);
                        }

                        break;
                    case "DELETE":
                        String result = taskManager.deleteTaskById(id);

                        sendText(exchange, result, 200);

                        break;
                    default:
                        break;
                }

            } else {
                exchange.sendResponseHeaders(404, 0);
                exchange.close();
            }
        }
    }

    static class EpicsHandler extends BaseHttpHandler implements HttpHandler {
        TaskManager taskManager;

        public EpicsHandler(TaskManager taskManager) {
            this.taskManager = taskManager;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            InputStream inputStream;
            String body;
            Epic epic;
            int id;
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            String method = exchange.getRequestMethod().toUpperCase();

            if (pathParts.length == 2 && pathParts[1].equals("epics")) {
                switch (method) {
                    case "GET":
                        HashMap<Integer, Epic> tasks = getEpics(taskManager);
                        String response = gson.toJson(tasks);

                        sendText(exchange, response, 200);

                        break;
                    case "POST":
                        inputStream = exchange.getRequestBody();
                        body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                        TaskDto taskDto = gson.fromJson(body, TaskDto.class);

                        if (taskDto.getId() != null) {
                            epic = (Epic) taskManager.updateNameAndDescription(taskDto.getId(), taskDto.getName(), taskDto.getDescription());
                            sendText(exchange, gson.toJson(epic), 201);
                        } else {
                            epic = taskManager.createEpic(taskDto.getName(), taskDto.getDescription());
                            if (epic == null) {
                                sendHasInteractions(exchange, "Подзадача пересекается с существующими.");
                            } else {
                                sendText(exchange, gson.toJson(epic), 201);
                            }
                        }

                        break;
                    default:
                        sendText(exchange, "Запрос не распознан.", 500);
                        break;
                }

            } else if (pathParts.length == 3 && pathParts[1].equals("epics")) {
                try {
                    id = Integer.parseInt(pathParts[2]);
                } catch (NumberFormatException e) {
                    sendText(exchange, "Некорректный id: " + pathParts[2], 400);
                    return;
                }

                switch (method) {
                    case "GET":
                        epic = (Epic) taskManager.getTaskById(id);

                        if (epic == null) {
                            sendNotFound(exchange, "Задачи с id: " + id + " нет.");
                        } else {
                            sendText(exchange, gson.toJson(epic), 200);
                        }

                        break;
                    case "DELETE":
                        String result = taskManager.deleteTaskById(id);

                        sendText(exchange, result, 200);

                        break;
                    default:
                        break;
                }

            } else if (pathParts.length == 4 && pathParts[1].equals("epics") && pathParts[3].equals("subtasks")) {
                try {
                    id = Integer.parseInt(pathParts[2]);
                } catch (NumberFormatException e) {
                    sendText(exchange, "Некорректный id: " + pathParts[2], 400);
                    return;
                }

                epic = (Epic) taskManager.getTaskById(id);

                if (epic == null) {
                    sendNotFound(exchange, "Эпика с id: " + id + " нет.");
                } else {
                    Set<Integer> subtasks = epic.getSubtasks();

                    sendText(exchange, gson.toJson(subtasks), 200);
                }
            } else {
                exchange.sendResponseHeaders(404, 0);
                exchange.close();
            }
        }
    }

    static class HistoryHandler extends BaseHttpHandler implements HttpHandler {
        TaskManager taskManager;

        public HistoryHandler(TaskManager taskManager) {
            this.taskManager = taskManager;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod().toUpperCase();

            if (method.equals("GET")) {
                List<Task> history = taskManager.getHistory();

                sendText(exchange, gson.toJson(history), 200);
            }
        }
    }

    static class PrioritizesHandler extends BaseHttpHandler implements HttpHandler {
        FileBackedTaskManager taskManager;

        public PrioritizesHandler(FileBackedTaskManager taskManager) {
            this.taskManager = taskManager;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod().toUpperCase();

            if (method.equals("GET")) {
                List<Task> history = taskManager.getPrioritizedTasks();

                sendText(exchange, gson.toJson(history), 200);
            }
        }
    }

    public static HashMap<Integer, Task> getSimpleTasks(TaskManager manager) {
        HashMap<Integer, Task> result = new HashMap<>();
        for (Map.Entry<Integer, Task> entry : manager.getAllTasks().entrySet()) {
            Task task = entry.getValue();
            if (!(task instanceof Subtask) && !(task instanceof Epic)) {
                result.put(entry.getKey(), task);
            }
        }
        return result;
    }

    public static HashMap<Integer, Subtask> getSubtasks(TaskManager manager) {
        HashMap<Integer, Subtask> result = new HashMap<>();
        for (Map.Entry<Integer, Task> entry : manager.getAllTasks().entrySet()) {
            Task task = entry.getValue();
            if (task instanceof Subtask subtask) {
                result.put(entry.getKey(), subtask);
            }
        }
        return result;
    }

    public static HashMap<Integer, Epic> getEpics(TaskManager manager) {
        HashMap<Integer, Epic> result = new HashMap<>();
        for (Map.Entry<Integer, Task> entry : manager.getAllTasks().entrySet()) {
            Task task = entry.getValue();
            if (task instanceof Epic epic) {
                result.put(entry.getKey(), epic);
            }
        }
        return result;
    }
}

