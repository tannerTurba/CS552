package types;

/* BinarySentence ::= UnarySentence ^ UnarySentence
                    | UnarySentence v UnarySentence
                    | UnarySentence => UnarySentence
                    | UnarySentence <=> UnarySentence */
public class BinarySentence extends Sentence {
    private UnarySentence s1;
    private BinaryConnective connector;
    private UnarySentence s2;

    public BinarySentence(UnarySentence s1, BinaryConnective connector, UnarySentence s2) {
        super();
        this.s1 = s1;
        this.connector = connector;
        this.s2 = s2;
        super.symbol = new Symbol(String.format("%s %s %s", s1.toString(), connector.toString(), s2.toString()), false);
    }

    public UnarySentence getS1() {
        return s1;
    }

    public BinaryConnective getConnective() {
        return connector;
    }
    
    public UnarySentence getS2() {
        return s2;
    }

    public BinarySentence negate() {
        UnarySentence left = getS1();
        UnarySentence right = getS2();
        BinaryConnective connective = getConnective();
        left = left.negate();
        right = right.negate();

        if (connective == BinaryConnective.AND) {   // ^
            return new BinarySentence(left, BinaryConnective.OR, right);
        }
        else {  // v
            return new BinarySentence(left, BinaryConnective.AND, right);
        }
    }

    public void parse(String prefix, int indent) {
        System.out.printf("%s: [%s] Binary [%s]\n".indent(indent), prefix, this.toString(), this.getConnective().toString());

        if (this.getS1() instanceof UnarySentence) {
            UnarySentence unary = (UnarySentence)this.getS1();
            unary.parse("LHS", indent);
        }
        if (this.getS2() instanceof UnarySentence) {
            UnarySentence unary = (UnarySentence)this.getS2();
            unary.parse("RHS", indent);
        }
    }
}
