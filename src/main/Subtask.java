package main;

public class Subtask extends Task{
    private int parentId;

    public int getParentId() {
        return parentId;
    }

    public Subtask(int id, String name, String description, int parentId) {
        super(id, name, description);
        this.parentId = parentId;
    }
}
