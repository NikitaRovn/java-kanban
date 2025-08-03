package main.java.manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import main.java.history.HistoryManager;
import main.java.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpTaskServer {
    public static int serverPort = 8080;

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);

        server.createContext("/tasks", new TasksHandler(taskManager));
        server.createContext("/subtasks", new SubtasksHandler(taskManager));
        server.createContext("/epics", new EpicsHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizesHandler(taskManager));

        server.start();

    }

    static class TasksHandler extends BaseHttpHandler implements HttpHandler {
        private final TaskManager taskManager;
        Gson gson = new Gson();

        public TasksHandler(TaskManager taskManager) {
            this.taskManager = taskManager;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            InputStream inputStream;
            String body;
            Map<String, String> data;
            Task task;
            int id;
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            String method = exchange.getRequestMethod().toUpperCase();

            if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
                switch (method) {
                    case "GET":
                        HashMap<Integer, Task> tasks = taskManager.getAllTasks();
                        String response = gson.toJson(tasks);

                        sendText(exchange, response, 200);

                        break;
                    case "POST":
                        inputStream = exchange.getRequestBody();
                        body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                        data = gson.fromJson(body, Map.class);

                        if (data.containsKey("id") && data.get("id") != null) {
                            task = taskManager.updateNameAndDescription(Integer.parseInt(data.get("id")), data.get("name"), data.get("description"));
                            sendText(exchange, gson.toJson(task), 201);
                        } else {
                            task = taskManager.createTask(data.get("name"), data.get("description"));
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
                id = Integer.parseInt(pathParts[2]);

                switch (method) {
                    case "GET":
                        task = taskManager.getTaskById(id);

                        if (task == null) {
                            sendNotFound(exchange, "Задачи с id: " + id + "нет.");
                        } else {
                            sendText(exchange, gson.toJson(task), 200);
                        }

                        break;
                    case "POST":
                        break;
                    case "DELETE":
                        inputStream = exchange.getRequestBody();
                        body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                        data = gson.fromJson(body, Map.class);

                        String result = taskManager.deleteTaskById(Integer.parseInt(data.get("id")));

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
        TaskManager taskManager;

        public SubtasksHandler(TaskManager taskManager) {
            this.taskManager = taskManager;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {

        }
    }

    static class EpicsHandler extends BaseHttpHandler implements HttpHandler {
        TaskManager taskManager;

        public EpicsHandler(TaskManager taskManager) {
            this.taskManager = taskManager;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {

        }
    }

    static class HistoryHandler extends BaseHttpHandler implements HttpHandler {
        TaskManager taskManager;

        public HistoryHandler(TaskManager taskManager) {
            this.taskManager = taskManager;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {

        }
    }

    static class PrioritizesHandler implements HttpHandler {
        TaskManager taskManager;

        public PrioritizesHandler(TaskManager taskManager) {
            this.taskManager = taskManager;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {

        }
    }
}

