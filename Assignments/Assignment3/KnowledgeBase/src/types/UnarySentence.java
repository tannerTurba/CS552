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
        if (isSymbol()) {
            return true;
        }
        if (isNegated && nestedUnary != null) {
            if (nestedUnary.nestedSentence != null) {
                return false;
            }
            return nestedUnary.isLiteral();
        }
        return false;
    }

    // Precondition: This is a literal
    public UnarySentence getLiteralValue() {
        int negCount = countNegations();
        UnarySentence sym = getNestedSymbol();

        if (negCount % 2 == 0) {
            return sym;
        }
        return new UnarySentence(sym);
    }

    private int countNegations() {
        if (isSymbol()) {
            return 0;
        }
        return 1 + nestedUnary.countNegations();
    }

    private UnarySentence getNestedSymbol() {
        if (isSymbol()) {
            // P == P
            return this;
        }
        return nestedUnary.getNestedSymbol();
    }

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
