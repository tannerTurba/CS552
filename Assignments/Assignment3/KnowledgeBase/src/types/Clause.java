/*
 * Tanner Turba
 * December 4, 2023
 * CS 552 - Artificial Intelligence - Assignment 3
 * 
 * This is the class that represents a Clause, which 
 * is made of many symbols OR'd together. Extends an 
 * ArrayList of Symbols to simplify this representation.
 */

package types;

import java.util.ArrayList;
import java.util.Comparator;

public class Clause extends ArrayList<Symbol> {
    public static Clause EMPTY = new Clause();
    private static int proofNum = 1;
    private static Clauses existingProofClauses = new Clauses();
    /* Object specific data for creating proofs */
    private Clause parent1 = null;
    private Clause parent2 = null;
    private Clause child = null;
    private int proofIndex = -1;
    private String resolvedOn = "";

    /**
     * Creates a new Clause
     */
    public Clause() {
        super();
    }

    /**
     * Creates a new Clause
     * @param clause Clause to combine with the new Clause
     */
    public Clause(Clause clause) {
        super();
        this.addAll(clause);
        sort();
        this.proofIndex = clause.getProofIndex();
        parent1 = clause.getParent1();
        parent2 = clause.getParent2();
        child = clause.getChild();
        resolvedOn = clause.resolvedOn;
    }

    /**
     * Creates a new Clause
     * @param sentence the Sentence to add to the new Clause
     */
    public Clause(Sentence sentence) {
        super();
        this.add(sentence.getSymbol());
    }

    /**
     * Gets the proof index
     * @return the index of this Clause in the proof
     */
    public int getProofIndex() {
        return proofIndex;
    }

    /**
     * Sets the proof index
     * @param index the new index of this Clause in the proof
     */
    public void setProofIndex(int index) {
        proofIndex = index;
    }

    /**
     * Gets the String representation of this Clause
     * @return All Symbols in this Clause OR'd together
     */
    public String toString() {
        return String.format("(%s)\n", getClause());
    }

    /**
     * Gets the String representation of this Clause
     * @return all the Symbols OR'd together
     */
    public String getClause() {
        StringBuilder sb = new StringBuilder();
        // Only put an OR between symbols.
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

    /**
     * Adds a Symbol to the Clause
     * @return true if the Symbol was added
     */
    public boolean add(Symbol symbol) {
        boolean retVal = super.add(symbol);
        sort();    // Sort after insertion to maintain order
        return retVal;
    }

    /**
     * Alphanumerically sorts this Clause
     */
    public void sort() {
        sort(Comparator.comparing(s -> s.getValue().toLowerCase()));
    }

    /**
     * Determines if this Clause is equal to another object.
     * @return true if equal
     */
    public boolean equals(Object s) {
        return this.toString().equals(s.toString());
    }

    /**
     * Gets a copy of this Clause
     * @return the copy of the Clause
     */
    public Clause getCopy() {
        return new Clause(this);
    }

    /**
     * Determines if this Clause contains the specified Symbol
     * @param symbol the Symbol to look for
     * @return true if this Clause contains the Symbol
     */
    public boolean contains(Symbol symbol) {
        for (Symbol s : this) {
            if (s.equals(symbol)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Determines if another Clause contains a complementary literal or Symbol in this Clause
     * @param clause the Clause to look in
     * @return true if the other Clause contains a complementary literal or Symbol in this Clause
     */
    public boolean containsComplementaryLiteral(Clause clause) {
        for (Symbol s : clause) {
            if (this.contains(s)) {
                resolvedOn = s.getValue();
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if a set of Clauses contains a complementary literal or Symbol in this Clause
     * @param clauses the set of Clauses to check
     * @return true if the set of Clauses contains a complementary literal or Symbol in this Clause
     */
    public boolean containsComplementaryLiterals(Clauses clauses) {
        for (Clause c : clauses) {
            if (this.containsComplementaryLiteral(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes any complementary literals(Symbols) from this Clause
     * @param clause the Clause to check
     */
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

    /**
     * Removes any complementary literals(Symbols) from this Clauses
     * @param clauses the set of Clauses to check
     */
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

    /**
     * Prints the proof of the resolution if one exists
     * @param premises the set of known premises
     * @param negated the set of known negated goals
     */
    public void printProof(Clauses premises, Clauses negated) { 
        proof(premises, negated);
        proofNum = 1;   // Reset proof index after printing
    }

    /**
     * The recursive helper method to print the proof
     * @param premises the set of known premises
     * @param negated the set of known negated goals
     */
    private void proof(Clauses premises, Clauses negated) {
        // recurse if there are any parents of this node.
        if (parent1 != null) {
            parent1.proof(premises, negated);
        }
        if (parent2 != null) {
            parent2.proof(premises, negated);
        }
        
        // get necessary values for printing.
        String clause = "()";
        if (this != Clause.EMPTY) {
            clause = getClause();
        }
        else {
            resolvedOn = parent1.getClause();
        }

        // if there isn't a line in the proof for this Clause, create a description and print
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

    /**
     * Clear the existing Clauses in the proof. Used after a proof is printed.
     */
    public static void clearExistingProofClauses() {
        existingProofClauses.clear();
    }
}
