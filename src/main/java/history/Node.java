package main.java.history;

import main.java.tasks.Task;

public class Node {
    Task currentTask;
    Node prevNode;
    Node nextNode;

    public Node(Task currentTask) {
        this.currentTask = currentTask;
        this.prevNode = null;
        this.nextNode = null;
    }

    public Node(Task currentTask, Node prevNode) {
        this.currentTask = currentTask;
        this.prevNode = prevNode;
        this.nextNode = null;
    }
}
