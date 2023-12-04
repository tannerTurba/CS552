/*
 * Tanner Turba
 * December 4, 2023
 * CS 552 - Artificial Intelligence - Assignment 3
 * 
 * This is a specialized class that represents a Unary Sentence, 
 * which is a type of Sentence and can be represented by the following: 
 * BinarySentence ::= UnarySentence ^ UnarySentence
 *                  | UnarySentence v UnarySentence
 *                  | UnarySentence => UnarySentence
 *                  | UnarySentence <=> UnarySentence
 */
package types;

public class BinarySentence extends Sentence {
    private UnarySentence left;
    private BinaryConnective connector;
    private UnarySentence right;

    /**
     * Creates a BinarySentence
     * @param left the left UnarySentence in the BinarySentence
     * @param connector the BinaryConnective to use
     * @param right the right UnarySentence in the BinarySentence
     */
    public BinarySentence(UnarySentence left, BinaryConnective connector, UnarySentence right) {
        super();
        this.left = left;
        this.connector = connector;
        this.right = right;
        super.symbol = new Symbol(String.format("%s %s %s", left.toString(), connector.toString(), right.toString()), false);
    }

    /**
     * Get the left UnarySentence
     * @return the left UnarySentence
     */
    public UnarySentence getLeft() {
        return left;
    }

    /**
     * Gets the BinaryConnective
     * @return the BinaryConnective
     */
    public BinaryConnective getConnective() {
        return connector;
    }
    
    /**
     * Get the right UnarySentence
     * @returnthe right UnarySentence
     */
    public UnarySentence getRight() {
        return right;
    }
    
    /**
     * Prints the parsing of this UnarySentence
     */
    public void parse(String prefix, int indent) {
        System.out.printf("%s: [%s] Binary [%s]\n".indent(indent), prefix, this.toString(), this.getConnective().toString());
        left.parse("LHS", indent);
        right.parse("RHS", indent);
    }
}
