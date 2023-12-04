/*
 * Tanner Turba
 * December 4, 2023
 * CS 552 - Artificial Intelligence - Assignment 3
 * 
 * This is the base class that represents a Sentence in propositional logic, 
 * which can be defined as follows: 
 * Sentence ::= UnarySentence | BinarySentence
 */
package types;

abstract public class Sentence {
    protected Symbol symbol;
    
    /**
     * Gets the symbol that holds and maintains the value of the sentence
     * @return the Symbol of this Sentence. 
     */
    public Symbol getSymbol() {
        return symbol;
    }

    /**
     * Gets the String representation of this Sentence
     */
    public String toString() {
        return symbol.toString();
    }

    /**
     * Begins the parsing process for this Sentence
     */
    public void parse() {
        parse("Orig", 0);
    }

    /**
     * The recursive method for printing the parse of this Sentence.
     * Must be implemented for each possible type of this Sentence.
     * @param prefix the prefix to use in printing
     * @param indent the amound of inden to use
     */
    public abstract void parse(String prefix, int indent);
}
