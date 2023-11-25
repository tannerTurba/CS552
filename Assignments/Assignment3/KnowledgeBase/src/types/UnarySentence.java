package types;

// UnarySentence ::= Symbol | (Sentence) | ~UnarySentence
public class UnarySentence extends Sentence {
    
    public UnarySentence() {
        super();
        // Default constuctor - do nothing
    }

    public UnarySentence(String val, boolean isNegated) {
        super();
        super.symbol = new Symbol(val, isNegated);
    }

    public UnarySentence(Sentence sentence) {
        super();
        super.symbol = new Symbol('(' + sentence.toString() + ')', false);
    }

    public UnarySentence(Symbol symbol) {
        super();
        super.symbol = symbol;
    }
}
