package types;

import java.util.ArrayList;

public class Clause extends ArrayList<Symbol> {
    private int proofIndex;

    public Clause() {
        super();
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
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            if (i + 1 != size()) {
                sb.append(String.format("%s v ", get(i)));
            }
            else {
                sb.append(get(i));
            }
        }
        return String.format("(%s)", sb.toString());
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
}
