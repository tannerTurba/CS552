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

    // public BinarySentence negate() {
    //     if (connector == BinaryConnective.AND) {
    //         return new BinarySentence(s1.negate(), BinaryConnective.OR, s2.negate());
    //     }
    //     else {
    //         return new BinarySentence(s1.negate(), BinaryConnective.AND, s2.negate());
    //     }
    // }

    public void parse(String prefix, int indent) {
        System.out.printf("%s: [%s] Binary [%s]\n".indent(indent), prefix, this.toString(), this.getConnective().toString());
        s1.parse("LHS", indent);
        s2.parse("RHS", indent);
    }
}
