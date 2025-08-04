package manager;

import com.google.gson.reflect.TypeToken;
import main.java.manager.*;
import main.java.tasks.Task;
import main.java.tasks.TaskDto;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

import static main.java.manager.HttpTaskServer.gson;
import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTasksTest {
    private HttpTaskServer server;
    private TaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        manager = Managers.getDefault();
        server = new HttpTaskServer((FileBackedTaskManager) manager);
        manager.deleteAllTasks();
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void testAddTask() throws IOException, InterruptedException {
        TaskDto dto = new TaskDto(null, "Test task", "Test description", null);

        String json = gson.toJson(dto);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        Type type = new TypeToken<HashMap<Integer, Task>>() {
        }.getType();
        HashMap<Integer, Task> tasks = gson.fromJson(getResponse.body(), type);

        assertEquals(1, tasks.size());
        Task addedTask = tasks.values().iterator().next();
        assertEquals("Test task", addedTask.getName());
    }
}