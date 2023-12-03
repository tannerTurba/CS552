package types;

import java.util.ArrayList;
import java.util.Comparator;

public class Clause extends ArrayList<Symbol> {
    public static Clause EMPTY = new Clause();
    private static int proofNum = 1;
    private static Clauses existingProofClauses = new Clauses();
    private Clause parent1 = null;
    private Clause parent2 = null;
    private Clause child = null;
    private int proofIndex = -1;
    private String resolvedOn = "";
    // private String description = "";

    public Clause() {
        super();
    }

    public Clause(Clause clause) {
        super();
        this.addAll(clause);
        sort();
        this.proofIndex = clause.getProofIndex();
        parent1 = clause.getParent1();
        parent2 = clause.getParent2();
        child = clause.getChild();
        resolvedOn = clause.resolvedOn;
        // description = clause.getDescription();
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
            str += "  P1: " + parent1.toString() + "\n";
        }
        if (parent2 != null) {
            str += "  P2: " + parent2.toString() + "\n";
        }
        if (child != null) {
            str += "  C:  " + child.toString() + "\n";
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

    public Clause getCopy(String description) {
        Clause newC = new Clause(this);

        // if (description != null) {
        //     newC.setDescription(description);
        // }
        return newC;
    }

    public boolean contains(Symbol symbol) {
        for (Symbol s : this) {
            if (s.equals(symbol)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsComplementaryLiteral(Clause clause) {
        for (Symbol s : clause) {
            if (this.contains(s)) {
                resolvedOn = s.getValue();
                return true;
            }
        }
        return false;
    }

    public boolean containsComplementaryLiterals(Clauses clauses) {
        for (Clause c : clauses) {
            if (this.containsComplementaryLiteral(c)) {
                return true;
            }
        }
        return false;
    }

    public void removeComplementaryLiteral(Clause clause) {
        for (Symbol s : clause) {
            if (this.contains(s)) {
                this.remove(s);
            }
            else {
                this.add(s);
            }
        }
    }

    public void removeComplementaryLiterals(Clauses clauses) {
        for (Clause c : clauses) {
            if (this.containsComplementaryLiteral(c)) {
                this.removeComplementaryLiteral(c);
            }
            else {
                for (Symbol s : c) {
                    s.negate();
                    this.add(s);
                }
            }
        }
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

    public void printProof(Clauses premises, Clauses negated) { 
        proof(premises, negated);
        proofNum = 1;
    }

    private void proof(Clauses premises, Clauses negated) {
        if (parent1 != null) {
            parent1.proof(premises, negated);
        }
        if (parent2 != null) {
            parent2.proof(premises, negated);
        }
        
        String clause = "()";
        if (this != Clause.EMPTY) {
            clause = getClause();
        }
        else {
            resolvedOn = parent1.getClause();
        }

        if (!existingProofClauses.contains(this)) {
            String description = "";
            if (premises.contains(this)) {
                description = "Premise";
            }
            else if (negated.contains(this)) {
                description = "Negated Goal";
            }
            else {
                int p1Index = parent1.getProofIndex();
                int p2Index = parent2.getProofIndex();
                if (existingProofClauses.contains(parent1)) {
                    p1Index = existingProofClauses.get(existingProofClauses.indexOf(parent1)).getProofIndex();
                }
                if (existingProofClauses.contains(parent2)) {
                    p2Index = existingProofClauses.get(existingProofClauses.indexOf(parent2)).getProofIndex();
                }
                description = String.format("Resolution on %s: %d, %d", resolvedOn, p1Index, p2Index);
            }
    
            System.out.printf("%2d. %-20s [%s]\n", proofNum, clause, description);
            if (proofIndex == -1) {
                proofIndex = proofNum;
            }
            existingProofClauses.add(this);
            proofNum++;
        }
    }

    public static void clearExistingProofClauses() {
        existingProofClauses.clear();
    }

    // /**
    //  * @return String return the description
    //  */
    // public String getDescription() {
    //     return description;
    // }

    // /**
    //  * @param description the description to set
    //  */
    // public void setDescription(String description) {
    //     this.description = description;
    // }

}
