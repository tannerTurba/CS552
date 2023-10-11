import java.util.*;

public class Frontier<T> {
    private Strategy strategy;
    private PriorityQueue<T> priorityQueue = null;
    private Stack<T> stack = null;
    
    public Frontier(Strategy strategy, T t) {
        this.strategy = strategy;
        if (strategy == Strategy.BREADTH || strategy == Strategy.DEPTH) {
            stack = new Stack<>();
            stack.push(t);
        }
        else {
            priorityQueue = new PriorityQueue<>();
            priorityQueue.add(t);
        }
    }

    public boolean add(T t) {
        if (strategy == Strategy.BREADTH || strategy == Strategy.DEPTH) {
            return stack.add(t);
        }
        else {
            return priorityQueue.add(t);
        }
    } 

    public T poll() {
        if (strategy == Strategy.BREADTH || strategy == Strategy.DEPTH) {
            return stack.pop();
        }
        else {
            return priorityQueue.poll();
        }
    }

    public int size() {
        if (strategy == Strategy.BREADTH || strategy == Strategy.DEPTH) {
            return stack.size();
        }
        else {
            return priorityQueue.size();
        }
    }

    public boolean isEmpty() {
        if (strategy == Strategy.BREADTH || strategy == Strategy.DEPTH) {
            return stack.isEmpty();
        }
        else {
            return priorityQueue.isEmpty();
        }
    }
}
