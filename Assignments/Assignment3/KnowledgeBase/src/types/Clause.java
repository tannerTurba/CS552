package types;

import java.util.ArrayList;
import java.util.Comparator;

public class Clause extends ArrayList<Symbol> {
    public static Clause EMPTY = new Clause();
    private Clause parent1 = null;
    private Clause parent2 = null;
    private Clause child = null;
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
        String str = String.format("(%s)\n", getClause());
        if (parent1 != null) {
            str += "  P1: " + parent1.getClause() + "\n";
        }
        if (parent2 != null) {
            str += "  P2: " + parent2.getClause() + "\n";
        }
        if (child != null) {
            str += "  C:  " + child.getClause() + "\n";
        }
        return str;
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

    /**
     * @return Clause return the parent1
     */
    public Clause getParent1() {
        return parent1;
    }

    /**
     * @param parent1 the parent1 to set
     */
    public void setParent1(Clause parent1) {
        this.parent1 = parent1;
    }

    /**
     * @return Clause return the parent2
     */
    public Clause getParent2() {
        return parent2;
    }

    /**
     * @param parent2 the parent2 to set
     */
    public void setParent2(Clause parent2) {
        this.parent2 = parent2;
    }

    /**
     * @return Clause return the child
     */
    public Clause getChild() {
        return child;
    }

    /**
     * @param child the child to set
     */
    public void setChild(Clause child) {
        this.child = child;
    }

    public void printProof() {
        if (parent1 != null) {
            parent1.printProof();
        }
        if (parent2 != null) {
            parent2.printProof();
        }
        System.out.println(toString());
    }
}
