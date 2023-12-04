/*
 * Tanner Turba
 * December 4, 2023
 * CS 552 - Artificial Intelligence - Assignment 3
 * 
 * This is the class that represents a set of Clauses, which
 * is the reason it extends an ArrayList of Clauses.
 */
package types;

import java.util.*;

public class Clauses extends ArrayList<Clause> {
    
    /**
     * Creates an empty set of Clauses
     */
    public Clauses() {
        super();
    }

    /**
     * Creates a set of Clauses that contain another set of Clauses
     * @param clausesToAdd the set of Clauses to add
     */
    public Clauses(Clauses clausesToAdd) {
        super();
        for (Clause c : clausesToAdd) {
            add(new Clause(c));
        }
    }

    /**
     * Creates a set of Clauses, preloaded with a single Clause
     * @param clauseToAdd the Clause to add
     */
    public Clauses(Clause clauseToAdd) {
        super();
        add(clauseToAdd);
    }

    /**
     * Gets the String representation of this set of Clauses
     * @return this set of Clauses AND'ed together
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // only have ANDs between Clauses
        for (int i = 0; i < size(); i++) {
            if (i + 1 != size()) {
                sb.append(String.format("%s ^ ", get(i).toString().trim()));
            }
            else {
                sb.append(get(i).toString());
            }
        }
        return sb.toString();
    }

    /**
     * Determines if this set of Clauses contains a subset of Clauses
     * @param set the subset of Clauses
     * @return true if this contains the subset of Clauses
     */
    public boolean contains(Clauses set) {
        for (Clause s : set) {
            if (!this.contains(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if this set of Clauses contains a Clause
     * @param c the specified Clause
     * @return true if contains the Clause
     */
    public boolean contains(Clause c) {
        for (Clause s : this) {
            if (s.getClause().equals(c.getClause())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a Clause to this set of Clauses
     * @return true if addition was successful
     */
    public boolean add(Clause clause) {
        boolean isAdded = super.add(clause);
        sort();     // sort to maintain order
        return isAdded;
    }

    /**
     * Sorts this set of Clauses by length, longest to shortest.
     */
    public void sort() {
        sort(new Comparator<Clause>() {

            @Override
            public int compare(Clause o1, Clause o2) {
                return o2.size() - o1.size();
            }
            
        });
    }

    /**
     * Factors this set of Clauses to remove any duplicate Clauses
     * @return the factored set of Clauses
     */
    public Clauses factor() {
        Clauses result = new Clauses();
        for (Clause c : this) {
            if (!result.contains(c)) {
                result.add(c);
            }
        }
        return result;
    }
}
