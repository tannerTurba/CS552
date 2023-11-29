package types;

// UnarySentence ::= Symbol | (Sentence) | ~UnarySentence
public class UnarySentence extends Sentence {
    public boolean isNegated;
    public UnarySentence nestedUnary = null;
    public Sentence nestedSentence = null;

    public UnarySentence() {
        super();
        // Default constuctor - do nothing
    }

    // UnarySentence ::= ~UnarySentence
    public UnarySentence(UnarySentence uSentence) {
        super();
        this.isNegated = true;
        this.nestedUnary = uSentence;
        super.symbol = new Symbol(nestedUnary.getSymbol().getValue(), isNegated);
    }

    // UnarySentence ::= (Sentence)
    public UnarySentence(Sentence sentence) {
        super();
        nestedSentence = sentence;
        this.isNegated = false;
        super.symbol = new Symbol('(' + nestedSentence.toString() + ')', isNegated);
    }

    // UnarySentence ::= Symbol
    public UnarySentence(Symbol symbol) {
        super();
        super.symbol = symbol;
        this.isNegated = false;
    }

    public void setNestedSentence(Sentence sentence) {
        nestedSentence = sentence;
        super.symbol = new Symbol('(' + nestedSentence.toString() + ')', isNegated);
    }

    public void setNestedUnary(UnarySentence sentence) {
        nestedUnary = sentence;
        super.symbol = new Symbol(nestedUnary.toString(), isNegated);
    }

    public boolean isSymbol() {
        return !isNegated && nestedSentence == null && nestedUnary == null;
    }

    public boolean isLiteral() {
        return isSymbol() || isNegated && nestedUnary != null && nestedSentence == null && nestedUnary.nestedSentence == null;
    }
}
