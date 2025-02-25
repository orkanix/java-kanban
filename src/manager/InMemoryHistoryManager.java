package manager;

import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final int MAX_HISTORY_COUNT = 10;

    private final ArrayList<Task> historyList = new ArrayList<>(MAX_HISTORY_COUNT);

    public ArrayList<Task> getHistory() {
        return new ArrayList<>(historyList);
    }

    public void add(Task item) {
        if (item == null) {
            return;
        }
        if (historyList.size() == MAX_HISTORY_COUNT) {
            historyList.removeFirst();
        }
        historyList.add(item);
    }
}
