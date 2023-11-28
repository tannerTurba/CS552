package types;

// Sentence ::= UnarySentence | BinarySentence
abstract public class Sentence {
    protected Symbol symbol;

    public Symbol getSymbol() {
        return symbol;
    }

    public String toString() {
        return symbol.toString();
    }

    // public abstract Symbol asNegated();
}
