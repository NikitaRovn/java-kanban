package main.java.manager;

import main.java.tasks.Task;
import main.java.tasks.TaskTypes;

public class FileBackedTaskManager extends InMemoryTaskManager {
    FileBackedTaskManager() {

    }

    private void save() {

    }

    private String taskToString(Task task) {

    }

    private Task taskFromString(String rawString) {
        String[] fields = rawString.split(",");
        TaskTypes taskType = TaskTypes.valueOf(fields[0]);
        switch (taskType) {
            case TASK ->

        }
    }
}
