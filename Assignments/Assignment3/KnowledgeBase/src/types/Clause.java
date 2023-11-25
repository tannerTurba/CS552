package types;

import java.util.ArrayList;

public class Clause extends ArrayList<Symbol>{

    public Clause() {
        super();
    }

    public Clause(Sentence sentence) {
        super();
        this.add(sentence.getSymbol());
    }

    public Clause(Symbol symbol) {
        super();
        this.add(symbol);
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
}
