/*
 * Tanner Turba
 * October 15, 2023
 * CS 552 - Artificial Intelligence - Assignment 1
 * 
 * This class represents a frontier that can be used for any search strategy.
 */
import java.util.*;

public class Frontier<T extends CityNode> {
    private Strategy strategy;
    private PriorityQueue<T> priorityQueue = null;
    private Queue<T> queue = null;
    private Stack<T> stack = null;
    
    /**
     * Frontier Contructor.
     * @param strategy The search strategy to be used.
     * @param t 
     */
    public Frontier(Strategy strategy, T t) {
        // Initialize a collection, depending on the search strategy.
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

    /**
     * Add an item to the frontier.
     * @param t The item to add.
     * @return True if the item was added.
     */
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

    /**
     * Removes and returns the first item from collection.
     * @return The item from the collection.
     */
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

    /**
     * @return The size of the collection.
     */
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

    /**
     * @return True if the collection is empty.
     */
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
