import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private static int taskSequence = 1;

    private static final HashMap<Integer, Task> tasks = new HashMap<>();
    private static final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private static final HashMap<Integer, Epic> epics = new HashMap<>();

    public static int getNewTaskId(){
        return taskSequence++;
    }

    public static Object getAllTasksByType(String taskType) {
        return switch (taskType) {
            case "task" -> tasks;
            case "subtask" -> subTasks;
            case "epic" -> epics;
            default -> null;
        };
    }

    public static void deleteTasks(String taskType){
        switch (taskType) {
            case "task" -> tasks.clear();
            case "subtask" -> {
                subTasks.clear();
                for(int epicId : epics.keySet()){
                    recalculateEpicStatus(epicId);
                }
            }
            case "epic" -> {
                subTasks.clear();
                epics.clear();
            }
        }
    }

    public static Task getTaskById(int id){
        return tasks.get(id);
    }

    public static SubTask getSubTaskById(int id){
        return subTasks.get(id);
    }

    public static Epic getEpic(int id){
        return epics.get(id);
    }

    public static void createTask(Task task){
        tasks.put(task.getId(), task);
    }

    public static void createSubTask(SubTask subTask){
        subTasks.put(subTask.getId(), subTask);
        int epicId = subTask.getParentId();
        Epic epic = epics.get(epicId);
        epic.addChild(subTask.getId());
        recalculateEpicStatus(epicId);
    }

    public static void createEpic(Epic epic){
        epics.put(epic.getId(), epic);
        recalculateEpicStatus(epic.getId());
    }

    public static void deleteTask(int id){
        if(tasks.containsKey(id)){
            tasks.remove(id);
        }
        else if(subTasks.containsKey(id)){
            int parentId = subTasks.get(id).getParentId();
            subTasks.remove(id);
            recalculateEpicStatus(parentId);
        }
        else{
            ArrayList<Integer> childrenIds = epics.get(id).getChildrenIds();
            for( int childId : childrenIds){
                deleteTask(childId);
            }
            epics.remove(id);
        }
    }

    public static void recalculateEpicStatus(int epicId){
        Epic epic = epics.get(epicId);
        ArrayList<Integer> epicChildren = epic.getChildrenIds();
        int childrenCount = epicChildren.size();
        if(childrenCount == 0){
            return;
        }
        boolean allDone = true;
        boolean allNew = true;
        for(int childId : epicChildren){
            SubTask child = subTasks.get(childId);
            if(child.getStatus() != Statuses.DONE){
                allDone = false;
            }
            if(child.getStatus() != Statuses.NEW){
                allNew = false;
            }
        }

        if(allDone){
            epic.setStatus(Statuses.DONE);
        }
        else if(allNew){
            epic.setStatus(Statuses.NEW);
        }
        else{
            epic.setStatus(Statuses.IN_PROGRESS);
        }
    }

}
