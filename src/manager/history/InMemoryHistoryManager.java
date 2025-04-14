package manager.history;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    public class LinkedListHistory<T extends Task> {

        private final Map<Integer, Node<T>> history;
        private Node<T> head;
        private Node<T> tail;

        public LinkedListHistory() {
            this.history = new HashMap<>();
            this.head = null;
            this.tail = null;
        }

        public void linkLast(T task) {
            Node<T> newNode = new Node<>(task);
            if (tail == null) {
                head = tail = newNode;
            } else {
                newNode.prev = tail;
                tail.next = newNode;
                tail = newNode;
            }
            history.put(task.getId(), newNode);
        }

        public List<T> getTasks() {
            List<T> tasks = new ArrayList<>();
            Node<T> current = head;
            while (current != null) {
                tasks.add(current.data);
                current = current.next;
            }
            return tasks;
        }

        public void remove(Node<T> node) {
            if (node == null) return;

            if (node.prev != null) {
                node.prev.next = node.next;
            } else {
                head = node.next;
            }

            if (node.next != null) {
                node.next.prev = node.prev;
            } else {
                tail = node.prev;
            }

            history.remove(node.data.getId());
        }
    }

    private final LinkedListHistory<Task> linkedListHistory = new LinkedListHistory<>();

    @Override
    public List<Task> getHistory() {
        return linkedListHistory.getTasks();
    }

    @Override
    public void add(Task task) {
        if (linkedListHistory.history.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkedListHistory.linkLast(task);
    }


    @Override
    public void remove(int id) {
        Node<Task> current = linkedListHistory.history.get(id);
        if (current != null) {
            linkedListHistory.remove(current);
            linkedListHistory.history.remove(id);
        }
    }
}
