package main;

public class Subtask extends Task{
    public int parentId;

    public Subtask(int id, String name, String description, int parentId) {
        super(id, name, description);
        this.parentId = parentId;
    }
}
