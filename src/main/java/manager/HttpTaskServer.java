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
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpTaskServer {
    private final HttpServer server;
    private final int port;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) throws IOException {
        FileBackedTaskManager taskManager = (FileBackedTaskManager) Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.start();
    }


    public HttpTaskServer(FileBackedTaskManager  taskManager) throws IOException {
        this.port = 8080;
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

    public static class TasksHandler extends AbstractTaskHandler<Task> {
        public TasksHandler(TaskManager taskManager) {
            super(taskManager);
        }

        @Override
        public Map<Integer, Task> getAllTasks() {
            return HttpTaskServer.getSimpleTasks(taskManager);
        }

        @Override
        public Task getTaskById(int id) {
            return taskManager.getTaskById(id);
        }

        @Override
        public Task createTask(TaskDto taskDto) {
            return taskManager.createTask(taskDto.getName(), taskDto.getDescription());
        }

        @Override
        public Task updateNameAndDescription(TaskDto taskDto) {
            return taskManager.updateNameAndDescription(taskDto.getId(), taskDto.getName(), taskDto.getDescription());
        }
    }

    class SubtasksHandler extends AbstractTaskHandler<Subtask> {
        public SubtasksHandler(TaskManager taskManager) {
            super(taskManager);
        }

        @Override
        protected Map<Integer, Subtask> getAllTasks() {
            return HttpTaskServer.getSubtasks(taskManager);
        }

        @Override
        protected Subtask getTaskById(int id) {
            Task task = taskManager.getTaskById(id);
            return (task instanceof Subtask subtask) ? subtask : null;
        }

        @Override
        protected Subtask createTask(TaskDto taskDto) {
            return taskManager.createSubtask(taskDto.getName(), taskDto.getDescription(), taskDto.getParentId());
        }

        @Override
        protected Subtask updateNameAndDescription(TaskDto taskDto) {
            Task task = taskManager.updateNameAndDescription(taskDto.getId(), taskDto.getName(), taskDto.getDescription());
            return (task instanceof Subtask subtask) ? subtask : null;
        }
    }

    class EpicsHandler extends AbstractTaskHandler<Epic> {
        public EpicsHandler(TaskManager taskManager) {
            super(taskManager);
        }


        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            if (pathParts.length == 4 && pathParts[1].equals("epics") && pathParts[3].equals("subtasks")) {
                handleEpicSubtasks(exchange, pathParts[2]);
            } else {
                super.handle(exchange);
            }
        }

        private void handleEpicSubtasks(HttpExchange exchange, String idParam) throws IOException {
            int id;
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                sendText(exchange, "Некорректный id: " + idParam, 400);
                return;
            }

            Epic epic = getTaskById(id);

            if (epic == null) {
                sendNotFound(exchange, "Эпика с id: " + id + " нет.");
            } else {
                Set<Integer> subtaskIds = epic.getSubtasks() != null ? epic.getSubtasks() : Set.of();
                sendText(exchange, gson.toJson(subtaskIds), 200);
            }
        }

        @Override
        protected Map<Integer, Epic> getAllTasks() {
            return HttpTaskServer.getEpics(taskManager);
        }

        @Override
        protected Epic getTaskById(int id) {
            Task task = taskManager.getTaskById(id);
            return (task instanceof Epic epic) ? epic : null;
        }

        @Override
        protected Epic createTask(TaskDto taskDto) {
            return taskManager.createEpic(taskDto.getName(), taskDto.getDescription());
        }

        @Override
        protected Epic updateNameAndDescription(TaskDto taskDto) {
            Task task = taskManager.updateNameAndDescription(taskDto.getId(), taskDto.getName(), taskDto.getDescription());
            return (task instanceof Epic epic) ? epic : null;
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

        public PrioritizesHandler(FileBackedTaskManager  taskManager) {
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

