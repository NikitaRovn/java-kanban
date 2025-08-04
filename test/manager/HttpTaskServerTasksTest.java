package manager;

import main.java.manager.*;
import main.java.tasks.Task;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

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
        Task task = new Task(0, "Test task", "Test description");

        String json = HttpTaskServer.gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks")).POST(HttpRequest.BodyPublishers.ofString(json)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        HashMap<Integer, Task> tasks = manager.getAllTasks();
        assertEquals(0, tasks.size());
        assertEquals("Test task", task.getName());
    }
}