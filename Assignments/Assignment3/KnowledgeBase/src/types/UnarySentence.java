package types;

// UnarySentence ::= Symbol | (Sentence) | ~UnarySentence
public class UnarySentence extends Sentence {
    
    public UnarySentence() {
        super();
        // Default constuctor - do nothing
    }

    public UnarySentence(String val) {
        super();
        super.value = val;
    }

    public UnarySentence(Sentence sentence) {
        super();
        super.value = '(' + sentence.getValue() + ')';
    }

    public UnarySentence negate() {
        super.value = '~' + getValue();
        return this;
    }
}
