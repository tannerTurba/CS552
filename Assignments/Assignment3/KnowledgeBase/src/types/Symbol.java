/*
 * Tanner Turba
 * December 4, 2023
 * CS 552 - Artificial Intelligence - Assignment 3
 * 
 * This is the class that represents the Symbol or literal values, which 
 * are the atomic values that sentences are made of.
 */
package types;

public class Symbol {
    private boolean isNegated = false;
    private String value;

    /**
     * Create new Symbol object
     * @param val the value of the Symbol, not including negations
     * @param isNegated true if the Symbol is negated
     */
    public Symbol(String val, boolean isNegated) {
        this.value = val;
        this.isNegated = isNegated;
    }

    /**
     * Negates the Symbol
     */
    public void negate() {
        isNegated = !isNegated;
    }

    /**
     * Returns the boolean flag to determine if the Symbol is negated
     * @return true if the Symbol is negated
     */
    public boolean isNegated() {
        return isNegated;
    }

    /**
     * Gets the value
     * @return the value of the Symbol, not including negations
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the String representation of the Symbol
     * @return the value of the Symbol, including negations
     */
    public String toString() {
        if (isNegated) {
            return String.format("~%s", this.value);
        }
        else {
            return this.value;
        }
    }

    /**
     * Determines if another Object is equal to this Symbol
     * @return true if the Object's string value is equal to this string value
     */
    public boolean equals(Object s) {
        return this.toString().equals(s.toString());
    }
}
