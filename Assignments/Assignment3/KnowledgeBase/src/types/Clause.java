package types;

import java.util.ArrayList;
import java.util.Comparator;

public class Clause extends ArrayList<Symbol> {
    public static Clause EMPTY = new Clause();
    private int proofIndex;

    public Clause() {
        super();
    }

    public Clause(Clause clause) {
        super();
        this.addAll(clause);
        sort();
        this.proofIndex = clause.getProofIndex();
    }

    public Clause(Sentence sentence) {
        super();
        this.add(sentence.getSymbol());
    }

    public Clause(Symbol symbol, int proofIndex) {
        super();
        this.add(symbol);
        this.proofIndex = proofIndex;
    }

    public int getProofIndex() {
        return proofIndex;
    }

    public void setProofIndex(int index) {
        proofIndex = index;
    }

    public String toString() {
        return String.format("(%s)", getClause());
    }

    public String getClause() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            if (i + 1 != size()) {
                sb.append(String.format("%s v ", get(i)));
            }
            else {
                sb.append(get(i));
            }
        }
        return String.format("%s", sb.toString());
    }

    public boolean add(Symbol symbol) {
        boolean retVal = super.add(symbol);
        sort();
        return retVal;
    }

    public void sort() {
        sort(Comparator.comparing(s -> s.getValue().toLowerCase()));
    }

    public boolean equals(Object s) {
        return this.toString().equals(s.toString());
    }

    public Clause getCopy() {
        return new Clause(this);
    }

    public boolean contains(Symbol symbol) {
        for (Symbol s : this) {
            if (s.equals(symbol)) {
                return true;
            }
        }
        return false;
    }
}
