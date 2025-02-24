package manager;

import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> historyList = new ArrayList<>(10);

    public ArrayList<Task> getHistory() {
        return historyList;
    }

    public void add(Task item) {
        if (historyList.size() == 10) {
            historyList.removeFirst();
            historyList.add(item);
        } else {
            historyList.add(item);
        }
    }
}
