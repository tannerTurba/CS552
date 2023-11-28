package types;

import java.util.*;

//Can be used as a CNFSentence.
public class Clauses extends ArrayList<Clause>{
    public Clauses() {
        super();
    }

    public Clauses(Clauses clausesToAdd) {
        super();
        for (Clause c : clausesToAdd) {
            add(new Clause(c));
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            if (i + 1 != size()) {
                sb.append(String.format("%s\n", get(i).toString()));
            }
            else {
                sb.append(get(i).toString());
            }
        }
        return sb.toString();
    }

    public boolean contains(Clauses set) {
        for (Clause s : set) {
            if (!this.contains(s)) {
                return false;
            }
        }
        return true;
    }

    public int getProof() {
        StringBuilder sb = new StringBuilder();
        int i;
        for (i = 0; i < size(); i++) {
            get(i).setProofIndex(i+1);
            if (i + 1 != size()) {
                sb.append(String.format("%d. %-15s [Premise]\n", i+1, get(i).getClause()));
            }
            else {
                sb.append(String.format("%d. %-15s [Premise]", i+1, get(i).getClause()));
            }
        }
        System.out.println(sb.toString());
        return i;
    }
}
