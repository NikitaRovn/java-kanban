package manager;

import main.java.manager.FileBackedTaskManager;
import main.java.manager.HttpTaskServer;
import main.java.tasks.Task;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerHistoryTest {

    private static HttpTaskServer server;
    private static FileBackedTaskManager manager;
    private static final int PORT = 8080;
    private static final String URL = "http://localhost:" + PORT;

    @BeforeAll
    static void startServer() throws IOException {
        manager = new FileBackedTaskManager(Path.of("test-history.csv"));
        server = new HttpTaskServer(manager);
        server.start();
    }

    @AfterAll
    static void stopServer() throws IOException {
        server.stop();
        Files.deleteIfExists(Path.of("test-history.csv"));
    }

    @Test
    void getHistory_shouldReturnHistoryTasks() throws Exception {
        Task task1 = manager.createTask("Task 1", "Description 1");
        Task task2 = manager.createTask("Task 2", "Description 2");

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        String body = response.body();
        assertNotNull(body);
        assertTrue(body.contains("\"id\":" + task1.getId()));
        assertTrue(body.contains("\"id\":" + task2.getId()));
    }
}