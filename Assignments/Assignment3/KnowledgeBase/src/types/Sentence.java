package types;

// Sentence ::= UnarySentence | BinarySentence
abstract public class Sentence {
    protected String value;

    public String getValue() {
        return value;
    }

    public String toString() {
        return value;
    }
}
