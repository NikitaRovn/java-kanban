package main.java.history;

import main.java.tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> historyTaskList = new HashMap<>();
    private Node firstNode;
    private Node lastNode;

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task taskToAdd) {
        if (taskToAdd == null) return;

        remove(taskToAdd.getId());
        linkLast(taskToAdd);
    }

    @Override
    public void remove(int id) {
        Node node = historyTaskList.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }


    private void linkLast(Task task) {
        Node newNode = new Node(task, lastNode, null);

        if (lastNode != null) {
            lastNode.nextNode = newNode;
        } else {
            firstNode = newNode;
        }

        lastNode = newNode;
        historyTaskList.put(task.getId(), newNode);
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node current = firstNode;

        while (current != null) {
            tasks.add(current.currentTask);
            current = current.nextNode;
        }

        return tasks;
    }

    private void removeNode(Node node) {
        Node prev = node.prevNode;
        Node next = node.nextNode;

        if (prev != null) {
            prev.nextNode = next;
        } else {
            firstNode = next;
        }

        if (next != null) {
            next.prevNode = prev;
        } else {
            lastNode = prev;
        }

        node.prevNode = null;
        node.nextNode = null;
    }
}
