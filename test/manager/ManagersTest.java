package manager;

import manager.history.HistoryManager;
import manager.task.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    public void returnOfAnInitializedTaskObjectOfTheManagerClass() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager, "Метод getDefault() должен возвращать проинициализированный объект!");
    }

    @Test
    public void returnOfAnInitializedHistoryObjectOfTheManagerClass() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "Метод getDefault() должен возвращать проинициализированный объект!");
    }
}