package types;

public class UnaryConnective {
    public final static String NOT = "~";
    private String value;

    public UnaryConnective(String val) {
        value = val;
    }

    public boolean NotOp() {
        return value.equals(NOT);
    }

    public String toString() {
        return value;
    }
}
