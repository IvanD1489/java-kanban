public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        // 1. Создание задач
        //     1.1. Обычные задачи
        Task task1 = new Task("Задача 1", "Обычная задача", Statuses.NEW);
        TaskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Обычная задача", Statuses.NEW);
        TaskManager.createTask(task2);
        //     1.2 Эпик
        Epic epic1 = new Epic("Эпик 1", "Обычный эпик");
        TaskManager.createEpic(epic1);
        Epic epic2 = new Epic("Эпик 2", "Обычный эпик");
        TaskManager.createEpic(epic2);
        //     1.3 Подзадачи
        SubTask subTask11 = new SubTask("Подзадача 1-1", "Обычная подзадача", Statuses.NEW, epic1.getId());
        TaskManager.createSubTask(subTask11);
        SubTask subTask12 = new SubTask("Подзадача 1-2", "Обычная подзадача", Statuses.NEW, epic1.getId());
        TaskManager.createSubTask(subTask12);
        SubTask subTask21 = new SubTask("Подзадача 2-1", "Обычная подзадача", Statuses.NEW, epic2.getId());
        TaskManager.createSubTask(subTask21);
        // 2. Вывод задач по типу
        System.out.println("Вывод задач по типам:");
        System.out.println(TaskManager.getAllTasksByType("task")); // --> {1=Task@6f496d9f, 2=Task@723279cf}
        System.out.println(TaskManager.getAllTasksByType("subtask")); // --> {5=SubTask@2f4d3709, 6=SubTask@4e50df2e, 7=SubTask@1d81eb93}
        System.out.println(TaskManager.getAllTasksByType("epic")); // --> {3=Epic@7291c18f, 4=Epic@34a245ab}
        System.out.println();
        // 3. Удаление всех задач по типу "Обычный таск"
        TaskManager.deleteTasks("task");
        // Повторный вывод после удаления:
        System.out.println("Вывод задач после удаления:");
        System.out.println(TaskManager.getAllTasksByType("task")); // --> {}
        System.out.println();

        // 4. Получение задачи по идентификатору:
        Task task3 = new Task("Задача 3", "Обычная задача", Statuses.NEW);
        TaskManager.createTask(task3);
        System.out.println("Вывод несуществующей и существующей задачи:");
        // Несуществующая задача
        System.out.println(TaskManager.getTaskById(1)); // --> null
        // Существующая задача
        System.out.println(TaskManager.getTaskById(task3.getId())); // --> Task@10f87f48
        System.out.println();

        // 5. Обновление задачи:
        //     5.1 Обычной задачи
        System.out.println("Вывод обновленной информации о задаче:");
        System.out.println(TaskManager.getTaskById(task3.getId()).getDescription()); // --> Обычная задача
        task3 = new Task("Задача 3", "Обычная задача в работе", Statuses.IN_PROGRESS, task3.getId());
        TaskManager.createTask(task3);
        System.out.println(TaskManager.getTaskById(task3.getId()).getDescription()); // --> Обычная задача в работе
        System.out.println();

        //     5.2 Задачи в эпике со сменой статуса эпика:
        System.out.println("Автоматический подсчет статуса эпика:");
        System.out.println("До обновления (одна задача в статусе NEW)");
        System.out.println(epic2.getStatus()); // --> NEW
        subTask21 = new SubTask("Подзадача 2-1", "Обычная подзадача в работе", Statuses.IN_PROGRESS, epic2.getId(), subTask21.getId());
        TaskManager.createSubTask(subTask21);
        System.out.println("После обновления (одна задача в статусе IN_PROGRESS)");
        System.out.println(epic2.getStatus()); // --> IN_PROGRESS
        subTask21 = new SubTask("Подзадача 2-1", "Обычная подзадача в работе", Statuses.DONE, epic2.getId(), subTask21.getId());
        TaskManager.createSubTask(subTask21);
        System.out.println("После обновления (одна задача в статусе DONE)");
        System.out.println(epic2.getStatus()); // --> DONE
        System.out.println();

        // 6. Удаление задачи по идентификатору:
        System.out.println("Удаление задачи по идентификатору");
        System.out.println(TaskManager.getTaskById(task3.getId())); // --> Task@b4c966a
        TaskManager.deleteTask(task3.getId());
        System.out.println(TaskManager.getTaskById(task3.getId())); // --> null
    }
}
