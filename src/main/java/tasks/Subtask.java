package main.java.tasks;

public class Subtask extends Task {
    private int parentId;

    public int getParentId() {
        return parentId;
    }

    public Subtask(int id, String name, String description, int parentId) {
        super(id, name, description);
        this.parentId = parentId;
    }

    public static Subtask fromString(String csvLine) {
        String[] fields = csvLine.split(";");

        Subtask newSubtask = new Subtask(Integer.parseInt(fields[1]),
                fields[2],
                fields[3],
                Integer.parseInt(fields[5]));

        newSubtask.setStatus(TaskStatus.valueOf(fields[4]));

        return newSubtask;
    }

    @Override
    public String toString() {
        return String.format("SUBTASK;%d;%s;%s;%s;%d", getId(), getName(), getDescription(), getStatus(), parentId);
    }

}
