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
        super.value = String.format("%s %s %s", s1, connector, s2);
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
}
