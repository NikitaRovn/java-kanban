package main.java.history;

import main.java.tasks.Task;

public class Node {
    public Task currentTask;
    public Node prevNode;
    public Node nextNode;

    public Node(Task currentTask) {
        this.currentTask = currentTask;
    }

    public Node(Task currentTask, Node prevNode, Node nextNode) {
        this.currentTask = currentTask;
        this.prevNode = prevNode;
        this.nextNode = nextNode;
    }
}
