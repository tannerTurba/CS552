import java.util.*;

public class Frontier<T> {
    private Strategy strategy;
    private PriorityQueue<T> priorityQueue = null;
    private Queue<T> queue = null;
    private Stack<T> stack = null;
    
    public Frontier(Strategy strategy, T t) {
        this.strategy = strategy;
        if (strategy == Strategy.DEPTH) {
            stack = new Stack<>();
            stack.push(t);
        }
        else if (strategy == Strategy.BREADTH) {
            queue = new LinkedList<>();
            queue.add(t);
        }
        else {
            priorityQueue = new PriorityQueue<>();
            priorityQueue.add(t);
        }
    }

    public boolean add(T t) {
        if (strategy == Strategy.DEPTH) {
            return stack.add(t);
        }
        else if (strategy == Strategy.BREADTH) {
            return queue.add(t);
        }
        else {
            return priorityQueue.add(t);
        }
    } 

    public T poll() {
        if (strategy == Strategy.DEPTH) {
            return stack.pop();
        }
        else if (strategy == Strategy.BREADTH) {
            return queue.poll();
        }
        else {
            return priorityQueue.poll();
        }
    }

    public int size() {
        if (strategy == Strategy.DEPTH) {
            return stack.size();
        }
        else if (strategy == Strategy.BREADTH) {
            return queue.size();
        }
        else {
            return priorityQueue.size();
        }
    }

    public boolean isEmpty() {
        if (strategy == Strategy.DEPTH) {
            return stack.isEmpty();
        }
        else if (strategy == Strategy.BREADTH) {
            return queue.isEmpty();
        }
        else {
            return priorityQueue.isEmpty();
        }
    }
}
