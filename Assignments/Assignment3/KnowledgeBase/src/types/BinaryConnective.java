/*
 * Tanner Turba
 * December 4, 2023
 * CS 552 - Artificial Intelligence - Assignment 3
 * 
 * This is a class that represents the connectives used in a 
 * BinarySentence. These connectives are ^, v, =>, and <=>.
 */
package types;

public class BinaryConnective {
    /* Available BinaryConnectives */
    public final static BinaryConnective AND = new BinaryConnective("^");
    public final static BinaryConnective OR = new BinaryConnective("v");
    public final static BinaryConnective IF = new BinaryConnective("=>");
    public final static BinaryConnective IFF = new BinaryConnective("<=>");
    
    public String val;

    /**
     * Private default constructor: c
     */
    private BinaryConnective() {

    }
    
    /**
     * private constructor: don't allow just any BinaryConnectives to be made
     * Create a BinaryConnective
     * @param s the string to represent
     */
    private BinaryConnective(String s) { 
        val = s; 
    }
    
    /**
     * Gets the String representation of this BinaryConnective
     * @return the String value
     */
    public String toString() { 
        return val; 
    }
}
