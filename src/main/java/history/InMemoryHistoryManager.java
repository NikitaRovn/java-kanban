package main.java.history;

import main.java.tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> historyTaskList = new HashMap<>();
    private Node firstNode;
    private Node lastNode;

    @Override
    public List<Task> getHistory() {

        List<Task> historyList = new ArrayList<>();
        Node current = firstNode;

        while (current != null) {
            historyList.add(current.currentTask);
            current = current.nextNode;
        }

        return historyList;
    }

    @Override
    public void add(Task taskToAdd) {
        if (historyTaskList.containsKey(taskToAdd.getId())) {
            this.remove(taskToAdd);
        }

        Node newNode;

        if (historyTaskList.isEmpty()) {
            newNode = new Node(taskToAdd);
            firstNode = newNode;
            lastNode = newNode;
        } else {
            newNode = new Node(taskToAdd, lastNode);
            lastNode.nextNode = newNode;
        }

        lastNode = newNode;

        historyTaskList.put(taskToAdd.getId(), newNode);
    }

    @Override
    public void remove(Task taskToDelete) {
        if (firstNode.equals(lastNode) && firstNode.currentTask.equals(taskToDelete)) {
            historyTaskList.clear();
            firstNode = null;
            lastNode = null;
        } else {
            Node currentNode = historyTaskList.get(taskToDelete.getId());

            if (currentNode.prevNode == null) {
                currentNode.nextNode.prevNode = null;
                firstNode = currentNode.nextNode;
                historyTaskList.remove(currentNode.currentTask.getId());
            } else if (currentNode.nextNode == null) {
                currentNode.prevNode.nextNode = null;
                lastNode = currentNode.prevNode;
                historyTaskList.remove(currentNode.currentTask.getId());
            } else {
                currentNode.prevNode.nextNode = currentNode.nextNode;
                currentNode.nextNode.prevNode = currentNode.prevNode;
                historyTaskList.remove(currentNode.currentTask.getId());
            }
        }
    }
}
