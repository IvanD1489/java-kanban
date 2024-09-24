public class SubTask extends Task{

    private int parentId;

    public SubTask(String name, String description, Statuses status, int parentId) {
        super(name, description, status);
        this.parentId = parentId;
    }

    public SubTask(String name, String description, Statuses status, int parentId, int id) {
        super(name, description, status, id);
        this.parentId = parentId;
    }

    public void setParentId(int parentId){
        this.parentId = parentId;
    }

    public int getParentId(){
        return this.parentId;
    }

    @Override
    public void setStatus(Statuses status) {
        super.setStatus(status);
        TaskManager.recalculateEpicStatus(this.parentId);
    }
}
