/*
 * Tanner Turba
 * December 4, 2023
 * CS 552 - Artificial Intelligence - Assignment 3
 * 
 * This is a specialized class that represents a Unary Sentence, 
 * which is a type of Sentence and can be represented by the following: 
 * UnarySentence ::= Symbol | (Sentence) | ~UnarySentence
 */
package types;

public class UnarySentence extends Sentence {
    public boolean isNegated;
    public UnarySentence nestedUnary = null;
    public Sentence nestedSentence = null;

    /**
     * UnarySentence ::= ~UnarySentence
     * @param uSentence the UnarySentence to nest
     */
    public UnarySentence(UnarySentence uSentence) {
        super();
        this.isNegated = true;
        this.nestedUnary = uSentence;
        super.symbol = new Symbol(nestedUnary.getSymbol().getValue(), isNegated);
    }

    /**
     * UnarySentence ::= (Sentence)
     * @param sentence the Sentence to nest
     */
    public UnarySentence(Sentence sentence) {
        super();
        nestedSentence = sentence;
        this.isNegated = false;
        super.symbol = new Symbol('(' + nestedSentence.toString() + ')', isNegated);
    }

    /**
     * UnarySentence ::= Symbol
     * @param symbol the equivalent Symbol
     */
    public UnarySentence(Symbol symbol) {
        super();
        super.symbol = symbol;
        this.isNegated = false;
    }

    /**
     * Sets the nested Sentence
     * @param sentence the Sentence to nest
     */
    public void setNestedSentence(Sentence sentence) {
        nestedSentence = sentence;
        super.symbol = new Symbol('(' + nestedSentence.toString() + ')', isNegated);
    }

    /**
     * Set the nested UnarySentence
     * @param sentence the UnarySentence to nest
     */
    public void setNestedUnary(UnarySentence sentence) {
        nestedUnary = sentence;
        super.symbol = new Symbol(nestedUnary.toString(), isNegated);
    }

    /**
     * Determines if this Sentence represents a Symbol 
     * @return true if this Sentence represents a Symbol 
     */
    public boolean isSymbol() {
        return !isNegated && nestedSentence == null && nestedUnary == null;
    }

    /**
     * Determines if this Sentence represents a literal, which is a Symbol or a negated Symbol 
     * @return true if this Sentence represents a literal 
     */
    public boolean isLiteral() {
        if (isSymbol()) {
            return true;
        }
        // if UnarySentence is negated
        if (isNegated && nestedUnary != null) {
            // if UnarySentence is ~(...)
            if (nestedUnary.nestedSentence != null) {
                // not a literal
                return false;
            }
            // recurse
            return nestedUnary.isLiteral();
        }
        // false if not a symbol and not a literal
        return false;
    }

    /**
     * Get the literal value of this UnarySentence
     * Precondition: this UnarySentence is a literal
     * @return a UnarySentence containing the literal value
     */
    public UnarySentence getLiteralValue() {
        int negCount = countNegations();
        UnarySentence sym = getNestedSymbol();

        if (negCount % 2 == 0) {
            return sym;
        }
        return new UnarySentence(sym);
    }

    /**
     * Get the number of negations associated with this UnarySentence
     * @return the number of negations
     */
    private int countNegations() {
        if (isSymbol()) {
            return 0;
        }
        return 1 + nestedUnary.countNegations();
    }

    /**
     * Gets the Symbol that is nested in this UnarySentence
     * @return a Symbol
     */
    private UnarySentence getNestedSymbol() {
        if (isSymbol()) {
            // P == P
            return this;
        }
        return nestedUnary.getNestedSymbol();
    }

    /**
     * Prints the parsing of this UnarySentence
     */
    public void parse(String prefix, int indent) {
        if (nestedSentence == null && this.nestedUnary == null) {
            System.out.printf(" %s: [%s] Unary [symbol]: [%s]\n".indent(indent), prefix, this, this.getSymbol());
        }
        else if (nestedSentence != null) {
            System.out.printf(" %s: [%s] Unary [()]\n".indent(indent), prefix, this);
            nestedSentence.parse("Sub", indent+2);
        }
        else if (this.isNegated) {
            System.out.printf(" %s: [%s] Unary [~]\n".indent(indent), prefix, this);
            nestedUnary.parse("Sub", indent+2);
        }
    }
}
