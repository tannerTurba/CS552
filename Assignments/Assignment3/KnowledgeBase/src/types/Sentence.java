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

    public void parse() {
        parse("Orig", 0);
    }

    public abstract void parse(String prefix, int indent);
}
