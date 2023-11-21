package types;

import java.util.*;

public class Clauses extends ArrayList<Clause>{
    public Clauses() {
        super();
    }

    public Clauses(Clauses clausesToAdd) {
        super(clausesToAdd);
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
}
