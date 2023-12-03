package types;

public class Symbol {
    private boolean isNegated = false;
    private String value;

    public Symbol(String val, boolean isNegated) {
        super();
        this.value = val;
        this.isNegated = isNegated;
    }

    public void negate() {
        isNegated = !isNegated;
    }

    public Symbol asNegated() {
        return new Symbol(value, !isNegated);
    }

    public boolean isNegated() {
        return isNegated;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        if (isNegated) {
            return String.format("~%s", this.value);
        }
        else {
            return this.value;
        }
    }

    public boolean equals(Object s) {
        return this.toString().equals(s.toString());
    }
}
