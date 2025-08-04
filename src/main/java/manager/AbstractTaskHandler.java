package main.java.manager;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.java.tasks.Task;
import main.java.tasks.TaskDto;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

public abstract class AbstractTaskHandler<T extends Task> extends BaseHttpHandler implements HttpHandler {
    protected final TaskManager taskManager;
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    protected AbstractTaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected abstract Map<Integer, T> getAllTasks();

    protected abstract T getTaskById(int id);

    protected abstract T createTask(TaskDto taskDto);

    protected abstract T updateNameAndDescription(TaskDto taskDto);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        String method = exchange.getRequestMethod().toUpperCase();

        try {
            if (pathParts.length == 2) {
                handleAllTasks(exchange, method);
            } else if (pathParts.length == 3) {
                handleTaskById(exchange, method, pathParts[2]);
            } else {
                exchange.sendResponseHeaders(404, 0);
                exchange.close();
            }
        } catch (JsonSyntaxException e) {
            sendText(exchange, "Некорректный JSON в теле запроса.", 400);
        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, "Внутренняя ошибка сервера: " + e.getMessage(), 500);
        }
    }


    protected void handleAllTasks(HttpExchange exchange, String method) throws IOException {
        switch (method) {
            case "GET":
                sendText(exchange, gson.toJson(getAllTasks()), 200);
                break;
            case "POST":
                String body;
                try (InputStream inputStream = exchange.getRequestBody()) {
                    body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                }
                TaskDto taskDto = gson.fromJson(body, TaskDto.class);

                T task = null;
                if (taskDto.getId() != null) {
                    task = updateNameAndDescription(taskDto);
                } else {
                    task = createTask(taskDto);
                }

                if (task == null) {
                    sendText(exchange, "Задача пересекается с существующими или неверные данные.", 406);
                } else {
                    sendText(exchange, gson.toJson(task), 201);
                }
                break;
            default:
                sendMethodNotAllowed(exchange);
                break;
        }
    }

    protected void handleTaskById(HttpExchange exchange, String method, String idParam) throws IOException {
        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            sendText(exchange, "Некорректный id: " + idParam, 400);
            return;
        }

        switch (method) {
            case "GET":
                T task = getTaskById(id);
                if (task == null) {
                    sendNotFound(exchange, "Задачи с id: " + id + " нет.");
                } else {
                    sendText(exchange, gson.toJson(task), 200);
                }
                break;
            case "DELETE":
                String result = taskManager.deleteTaskById(id);
                sendText(exchange, result, 200);
                break;
            default:
                sendMethodNotAllowed(exchange);
                break;
        }
    }
}


