/*
 * Tanner Turba
 * November 1, 2023
 * CS 552 - Artificial Intelligence - Assignment 2
 * 
 * This class is used to help associate data with other pieces 
 * of data and is used in the implementation of the ac-3 algorithm. 
 */
public class Pair<K, V> {
    public final K key;
    public final V value;

    /**
     * Creates a key/value pair
     * @param key the key
     * @param value the value
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}