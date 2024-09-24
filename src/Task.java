public class Task {

    private String name;
    private String description;
    private int id;

    private Statuses status;

    public Task(String name, String description, Statuses status){
        this.name = name;
        this.description = description;
        this.id = TaskManager.getNewTaskId();
        this.status = status;
    }

    public Task(String name, String description, Statuses status, int id){
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Statuses getStatus() {
        return this.status;
    }

    public void setStatus(Statuses status) {
        this.status = status;
    }

}
