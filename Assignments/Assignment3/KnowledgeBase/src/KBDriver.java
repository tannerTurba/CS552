import parser.*;
import types.*;

public class KBDriver {
    private int proofIndex = 0;

    public static void main(String[] args) {
        System.out.println();
        new KBDriver(args);
    }

    public KBDriver(String[] args) {
        String fileName;
        if (args.length > 0) {
            fileName = args[0];
        }
        else {
            fileName = null;
        }
        
        Parser parser;
        if (fileName != null) {
            parser = new Parser(new Lexer(fileName));
        }
        else {
            parser = new Parser(new Lexer());
            System.out.println("Welcome to the Knowledge Base!");
            System.out.println("Please TELL or ASK me anything!");
            System.out.println("(type HELP for more information)");
        }

        Clauses kB = new Clauses();
        while (true) {
            if (fileName == null) {
                System.out.print("> ");
            }
            String cmd = parser.getCommand();

            if (fileName != null && !cmd.equalsIgnoreCase("eof")) {
                System.out.printf("> %s ", cmd.toUpperCase());
            }
    
            if (cmd.equalsIgnoreCase("tellc")) {
                Clause clause = parser.getClause();
                if (fileName != null) {
                    System.out.print(clause);
                }
                kB.add(clause);
            }
            else if (cmd.equalsIgnoreCase("print")) {
                System.out.println(kB);
            }
            else if (cmd.equalsIgnoreCase("eof")) {
                break;
            }
            else if (cmd.equalsIgnoreCase("ask")) {
                Sentence query = parser.getSentence();
                if (fileName != null) {
                    System.out.print(query + "\n");
                }

                if (PlResolution(kB, query, false)) {
                    System.out.printf("Yes, KB entails %s\n", query.toString());
                }
                else {
                    System.out.printf("No, KB does not entail %s\n", query.toString());
                }
            }
            else if (cmd.equalsIgnoreCase("proof")) {
                Sentence query = parser.getSentence();
                if (fileName != null) {
                    System.out.print(query + "\n");
                }

                if (!PlResolution(kB, query, false)) {
                    System.out.println("No proof exists");
                }
                else {
                    PlResolution(kB, query, true);
                }
            }
            else if (cmd.equalsIgnoreCase("tell")) {
                Sentence sentence = parser.getSentence();
                if (fileName != null) {
                    System.out.print(sentence + "\n");
                }
                sentence = convertToCNF(sentence);
                kB.addAll(deriveClauses(sentence));
            }
            else if (cmd.equalsIgnoreCase("cnf")) {
                Sentence sentence = parser.getSentence();
                if (fileName != null) {
                    System.out.print(sentence.toString() + "\n");
                }

                sentence = convertToCNF(sentence);
                Clauses x = deriveClauses(sentence);
                System.out.println("Result: " + x.toString().trim());
            }
            else if (cmd.equalsIgnoreCase("parse")) {
                Sentence sentence = parser.getSentence();
                if (fileName != null) {
                    System.out.print(sentence + "\n");
                }

                sentence.parse();;
            }
            else if (cmd.equalsIgnoreCase("exit") || cmd.equalsIgnoreCase("done") || cmd.equalsIgnoreCase("quit")) {
                System.out.println("Thank you for using the Knowledge Base!");
                break;
            }
            else if (cmd.equalsIgnoreCase("help")) {
                String help = """
                        * DONE, EXIT, QUIT  : Terminates the session.
                        * TELLC <clause>    : Adds the given <clause> to the knowledge base.
                        * PRINT             : Prints the clauses currently in the knowledge base.
                        * ASK <query>       : Determines if the knowledge base entails <query>.
                        * PROOF <query>     : Prints a proof of <query>.
                        * PARSE <sentence>  : Prints the parse tree of the given <sentence> in propositional logic.
                        * CNF <sentence>    : Prints the conjunctive normal form representation of the given <sentence> in propositional logic.
                        * TELL <sentence>   : Adds the clauses in the conjunctive normal form representation of <sentence> to the knowledge base.
                        """;
                System.out.println(help);
            }
        }
        parser.close();
    }

    public boolean PlResolution(Clauses kB, Sentence alpha, boolean isProof) {
        Clauses premises = new Clauses();
        Clauses negatedGoal = new Clauses();
        Sentence negAlpha = new UnarySentence(new UnarySentence(alpha));
        negAlpha = convertToCNF(negAlpha);
        Clauses fromNegAlpha = deriveClauses(negAlpha);
        for (Clause c : fromNegAlpha) {
            negatedGoal.add(c);
        }
        // if (isProof) {
        //     System.out.println("Proof:");
        //     proofIndex = kB.getProof();
        //     for (Clause c : fromNegAlpha) {
        //         proofIndex++;
        //         c.setProofIndex(proofIndex);
        //         System.out.printf("%d. %-15s [Negated Goal]\n", proofIndex, c.getClause());
        //     }
        // }

        Clauses clauses = new Clauses(kB);
        for (Clause c : clauses) {
            premises.add(c);
        }
        clauses.addAll(fromNegAlpha);
        clauses.sort();
        Clauses derived = new Clauses();
        // Clauses solution = new Clauses();
        while (true) {
            // beginning round
            for (int c1 = 0; c1 < clauses.size(); c1++) {
                for (int c2 = c1 + 1; c2 < clauses.size(); c2++) {
                    Clauses resolvents = PlResolve(clauses, clauses.get(c1).getCopy(null), clauses.get(c2).getCopy(null), isProof, derived);
                    if (resolvents.contains(Clause.EMPTY)) {
                        if (isProof) {
                            Clause solution = resolvents.get(resolvents.indexOf(Clause.EMPTY));
                            // System.out.println(solution);
                            solution.printProof(premises, negatedGoal);
                            Clause.clearExistingProofClauses();
                        }
                        return true;
                    }
                    derived.addAll(resolvents);
                }
            }
            if (clauses.contains(derived)) {
                return false;
            }
            clauses.addAll(derived);
            clauses.sort();
            clauses = clauses.factor();
        }
    }

    private Clauses PlResolve(Clauses clauses, Clause c1, Clause c2, boolean isProof, Clauses derived/*, Clauses solution*/) {
        Clause small, big;
        Clauses result = new Clauses();
        if (c1.size() <= c2.size()) {
            small = c1;
            big = c2;
        }
        else {
            small = c2;
            big = c1;
        }

        Sentence negated;
        if (small.size() == 1) {
            if (small.get(0).isNegated()) {
                negated = new UnarySentence(new UnarySentence(new Symbol(small.get(0).getValue(), false)));
            }
            else {
                negated = new UnarySentence(small.get(0));
            }
        }
        else {
            UnarySentence left, right;
            if (small.get(0).isNegated()) {
                left = new UnarySentence(new UnarySentence(new Symbol(small.get(0).getValue(), false)));
            }
            else {
                left = new UnarySentence(small.get(0));
            }
            if (small.get(1).isNegated()) {
                right = new UnarySentence(new UnarySentence(new Symbol(small.get(1).getValue(), false)));
            }
            else {
                right = new UnarySentence(small.get(1));
            }

            BinarySentence b = new BinarySentence(left, BinaryConnective.OR, right);
            for (int i = 2; i < small.size(); i++) {
                if (small.get(2).isNegated()) {
                    right = new UnarySentence(new UnarySentence(new Symbol(small.get(i).getValue(), false)));
                }
                else {
                    right = new UnarySentence(small.get(i));
                }
                b = new BinarySentence(new UnarySentence(b), BinaryConnective.OR, right);
            }
            negated = b;
        }
        negated = new UnarySentence(new UnarySentence(negated));
        negated = convertToCNF(negated);
        Clauses fromNeg = deriveClauses(negated);
        
        // for (Clause negClause : fromNeg) {
            // for (Symbol negatedTemp : negClause) {
                if (big.containsComplementaryLiterals(fromNeg)) {
                    Clause resolvent = big.getCopy("n/a");
                    resolvent.removeComplementaryLiterals(fromNeg);
                    // resolvent should contain all the literals from big and negatedTemp, aside from the complementary literals
                    if (!derived.contains(resolvent)) {
                        if (resolvent.size() == 0) {
                            resolvent = Clause.EMPTY;
                        }
                        resolvent.setParent1(big.getCopy(null));
                        resolvent.setParent2(small.getCopy("n/a"));
                        // small.setChild(resolvent);
                        // big.setChild(resolvent);
                        // if (isProof)
                        //     System.out.println(resolvent);
                        
                        if (resolvent == Clause.EMPTY) {
                            result.add(resolvent);
                            return result.factor();
                        }
                        else {
                            result.add(resolvent.getCopy(null));
                            return result.factor();
                        }
                    }
                }
            // }
        // }
        return result.factor();
    }

    public Sentence convertToCNF(Sentence sentence) {
        Sentence result = eliminateIff(sentence);
        result = eliminateIf(result);
        result = eliminateNOT(result, false);
        result = applyDistributivity(result);
        // if (result instanceof UnarySentence) {
        //     UnarySentence us = (UnarySentence)result;
        //     if (us.nestedSentence != null) {
        //         return us.nestedSentence;
        //     }
        // }
        return result;
    }

    private Sentence eliminateIff(Sentence sentence) {
        if (sentence instanceof BinarySentence) {
            BinarySentence binarySentence = (BinarySentence)sentence;
            UnarySentence u1 = (UnarySentence)eliminateIff(binarySentence.getS1());
            UnarySentence u2 = (UnarySentence)eliminateIff(binarySentence.getS2());
            if (binarySentence.getConnective() == BinaryConnective.IFF) {
                BinarySentence s1 = new BinarySentence(u1, BinaryConnective.IF, u2);
                BinarySentence s2 = new BinarySentence(u2, BinaryConnective.IF, u1);
                UnarySentence left = new UnarySentence(s1);
                UnarySentence right = new UnarySentence(s2);
                return new BinarySentence(left, BinaryConnective.AND, right);
            }
            return new BinarySentence(u1, binarySentence.getConnective(), u2);
        }
        else if (sentence instanceof UnarySentence) {
            UnarySentence unary = (UnarySentence)sentence;
            if (unary.nestedSentence != null) {
                unary.setNestedSentence(eliminateIff(unary.nestedSentence));
                return unary;
            }
            else if (unary.nestedUnary != null) {
                unary.setNestedUnary((UnarySentence)eliminateIff(unary.nestedUnary));
                return unary;
            }
        }
        return sentence;
    }

    private Sentence eliminateIf(Sentence sentence) {
        if (sentence instanceof BinarySentence) {
            BinarySentence binarySentence = (BinarySentence)sentence;
            UnarySentence u1 = (UnarySentence)eliminateIf(binarySentence.getS1());
            UnarySentence right = (UnarySentence)eliminateIf(binarySentence.getS2());
            BinaryConnective connective = binarySentence.getConnective();
            if (binarySentence.getConnective() == BinaryConnective.IF) {
                UnarySentence left = new UnarySentence(u1);
                return new BinarySentence(left, BinaryConnective.OR, right);
            }
            return new BinarySentence(u1, connective, right);
        }
        else if (sentence instanceof UnarySentence) {
            UnarySentence unary = (UnarySentence)sentence;
            if (unary.nestedSentence != null) {
                unary.setNestedSentence(eliminateIf(unary.nestedSentence));
                return unary;
            }
            else if (unary.nestedUnary != null) {
                unary.setNestedUnary((UnarySentence)eliminateIf(unary.nestedUnary));
                return unary;
            }
        }
        return sentence;
    }

    private Sentence eliminateNOT(Sentence sentence, boolean outterIsNegated) {
        if (sentence instanceof BinarySentence) {
            BinarySentence binarySentence = (BinarySentence)sentence;
            boolean leftIsNegated = binarySentence.getS1().isNegated;
            boolean rightIsNegated = binarySentence.getS2().isNegated;
            if (outterIsNegated) {
                binarySentence = (BinarySentence)negate(binarySentence, true);
            }

            UnarySentence left = (UnarySentence)eliminateNOT(binarySentence.getS1(), leftIsNegated && !binarySentence.getS1().isLiteral());
            UnarySentence right = (UnarySentence)eliminateNOT(binarySentence.getS2(), rightIsNegated && !binarySentence.getS2().isLiteral());
            return new BinarySentence(left, binarySentence.getConnective(), right);
        }
        else {
            UnarySentence unarySentence = (UnarySentence)sentence;
            boolean shouldNegate = unarySentence.isNegated;
            if (outterIsNegated && !(unarySentence.isSymbol() || unarySentence.isLiteral())) {
                if (unarySentence.nestedSentence != null && unarySentence.nestedSentence instanceof BinarySentence) {
                    return new UnarySentence((BinarySentence)negate(unarySentence.nestedSentence, shouldNegate));
                }
                else if (unarySentence.nestedSentence != null && unarySentence.nestedSentence instanceof UnarySentence) {
                    return (UnarySentence)negate(unarySentence.nestedSentence, shouldNegate);
                }
                else if (unarySentence.nestedUnary != null) {
                    return new UnarySentence(negate(unarySentence.nestedUnary.nestedSentence, shouldNegate));
                }
                else {
                    return (UnarySentence)negate(unarySentence, shouldNegate);
                }
            }
            else if (outterIsNegated && (unarySentence.isSymbol() || unarySentence.isLiteral())) {
                return negate(unarySentence, shouldNegate);
            }
            
            if (unarySentence.isSymbol() || unarySentence.isLiteral()) {
                // a || ~a
                return unarySentence.getLiteralValue();
            }
            else if (unarySentence.nestedUnary != null) {
                // ~(a)
                return eliminateNOT(unarySentence.nestedUnary, shouldNegate);
            }
            else {
                // (a)
                return new UnarySentence(eliminateNOT(unarySentence.nestedSentence, shouldNegate));
            }
        }
    }

    public Sentence negate(Sentence sentence, boolean outterIsNegated) { 
        if (sentence instanceof BinarySentence) {
            BinarySentence binary = (BinarySentence)sentence;
            if (binary.getConnective() == BinaryConnective.AND) {
                return new BinarySentence((UnarySentence)eliminateNOT(binary.getS1(), true), BinaryConnective.OR, (UnarySentence)eliminateNOT(binary.getS2(), true));
            }
            else {
                return new BinarySentence((UnarySentence)eliminateNOT(binary.getS1(), true), BinaryConnective.AND, (UnarySentence)eliminateNOT(binary.getS2(), true));
            }
        }
        else {
            UnarySentence unary = (UnarySentence)sentence;
            if (unary.isSymbol()) {
                // a -> ~a
                return new UnarySentence(unary);
            }
            else if (unary.isLiteral()) {
                // ~a -> a
                return unary.nestedUnary;
            }
            else if (unary.nestedUnary != null && unary.nestedUnary.nestedSentence != null) {
                // ~(a) -> (a)
                return unary.nestedUnary;
            }
            else if (unary.nestedUnary == null && unary.nestedSentence != null) {
                // (a) -> ~(a)
                return new UnarySentence(unary);
            }
            // ???
            return unary;
        }
        
    }

    private Sentence applyDistributivity(Sentence sentence) {
        if (sentence instanceof BinarySentence) {
            BinarySentence binarySentence = (BinarySentence)sentence;
            if (binarySentence.getConnective() == BinaryConnective.OR) {
                UnarySentence s1 = binarySentence.getS1();
                UnarySentence s2 = binarySentence.getS2();
                UnarySentence a = null, b = null, c = null, d = null;

                if (s1.nestedSentence != null && s1.nestedSentence instanceof BinarySentence) {
                    //(a ^ b) v c 
                    a = (UnarySentence)applyDistributivity(((BinarySentence)s1.nestedSentence).getS1());
                    b = (UnarySentence)applyDistributivity(((BinarySentence)s1.nestedSentence).getS2());
                }
                else {
                    // a v c
                    a = (UnarySentence)applyDistributivity(s1);
                    b = null;
                }

                if (s2.nestedSentence != null && s2.nestedSentence instanceof BinarySentence) {
                    // a v (c ^ d)
                    c = (UnarySentence)applyDistributivity(((BinarySentence)s2.nestedSentence).getS1());
                    d = (UnarySentence)applyDistributivity(((BinarySentence)s2.nestedSentence).getS2());
                }
                else {
                    // a v c
                    c = (UnarySentence)applyDistributivity(s2);
                    d = null;
                }

                if (a != null && b != null && c != null && d != null && ((BinarySentence)s1.nestedSentence).getConnective() == BinaryConnective.OR && ((BinarySentence)s2.nestedSentence).getConnective() == BinaryConnective.AND) {
                    // (a v b) v (c ^ d) == ((a v b) v c) ^ ((a v b) v d)
                    BinarySentence innerRight = new BinarySentence(a, BinaryConnective.OR, b);
                    UnarySentence r1 = new UnarySentence(innerRight);
                    UnarySentence left = new UnarySentence(new BinarySentence(r1, BinaryConnective.OR, c));
                    UnarySentence right = new UnarySentence(new BinarySentence(r1, BinaryConnective.OR, d));
                    return new BinarySentence(left, BinaryConnective.AND, right);
                }
                else if (a != null && b != null && c != null && d != null && ((BinarySentence)s1.nestedSentence).getConnective() == BinaryConnective.AND && ((BinarySentence)s2.nestedSentence).getConnective() == BinaryConnective.OR) {
                    // (a ^ b) v (c v d) == (a v (c v d)) ^ (b v (c v d)) 
                    BinarySentence innerLeft = new BinarySentence(c, BinaryConnective.OR, d);
                    UnarySentence l1 = new UnarySentence(innerLeft);
                    UnarySentence left = new UnarySentence(new BinarySentence(a, BinaryConnective.OR, l1));
                    UnarySentence right = new UnarySentence(new BinarySentence(b, BinaryConnective.OR, l1));
                    return new BinarySentence(left, BinaryConnective.AND, right);
                }
                
                if (a != null && b != null && c != null && d == null) {
                    //(a ^ b) v c == (a v c) ^ (b v c)
                    BinarySentence innerLeft = new BinarySentence(a, BinaryConnective.OR, c);
                    UnarySentence left = new UnarySentence(innerLeft);
                    BinarySentence innerRight = new BinarySentence(b, BinaryConnective.OR, c);
                    UnarySentence right = new UnarySentence(innerRight);
                    return new BinarySentence(left, BinaryConnective.AND, right);
                }
                else if (a != null && b == null && c != null && d != null) {
                    // a v (c ^ d) == (a v c) ^ (a v d)
                    BinarySentence innerLeft = new BinarySentence(a, BinaryConnective.OR, c);
                    UnarySentence left = new UnarySentence(innerLeft);
                    BinarySentence innerRight = new BinarySentence(a, BinaryConnective.OR, d);
                    UnarySentence right = new UnarySentence(innerRight);
                    return new BinarySentence(left, BinaryConnective.AND, right);
                }
                else if (a != null && b != null && c != null && d != null) {
                    // (a v b) ^ (c v d) == ((a v c) ^ (a v d)) ^ ((b v c) ^ (b v d))
                    BinarySentence innerLeft1 = new BinarySentence(a, BinaryConnective.OR, c);
                    UnarySentence l1 = new UnarySentence(innerLeft1);
                    BinarySentence innerRight1 = new BinarySentence(a, BinaryConnective.OR, d);
                    UnarySentence r1 = new UnarySentence(innerRight1);
                    UnarySentence left = new UnarySentence(new BinarySentence(l1, BinaryConnective.AND, r1));

                    BinarySentence innerLeft2 = new BinarySentence(b, BinaryConnective.OR, c);
                    UnarySentence l2 = new UnarySentence(innerLeft2);
                    BinarySentence innerRight2 = new BinarySentence(b, BinaryConnective.OR, d);
                    UnarySentence r2 = new UnarySentence(innerRight2);
                    UnarySentence right = new UnarySentence(new BinarySentence(l2, BinaryConnective.AND, r2));
                    return new BinarySentence(left, BinaryConnective.AND, right);
                }
                else {
                    // a v c == a v c
                    return binarySentence;
                }
            }
            UnarySentence left = (UnarySentence)applyDistributivity(binarySentence.getS1());
            UnarySentence right = (UnarySentence)applyDistributivity(binarySentence.getS2());
            return new BinarySentence(left, binarySentence.getConnective(), right);
        }
        else if (sentence instanceof UnarySentence) {
            UnarySentence unary = (UnarySentence)sentence;
            if (unary.nestedSentence != null) {
                unary.setNestedSentence(applyDistributivity(unary.nestedSentence));
                return unary;
            }
        }
        return sentence;
    }

    private Clauses deriveClauses(Sentence sentence) {
        Clauses result = new Clauses();
        if (sentence instanceof BinarySentence) {
            BinarySentence binary = (BinarySentence)sentence;
            UnarySentence left = (UnarySentence)binary.getS1();
            UnarySentence right = (UnarySentence)binary.getS2();


            if (binary.getConnective() == BinaryConnective.AND) {
                Clauses fromLeft = deriveClauses(left);
                Clauses fromRight = deriveClauses(right);
                if (fromLeft.size() != 0) {
                    result.addAll(fromLeft);
                }
                if (fromRight.size() != 0) {
                    result.addAll(fromRight);
                }
                return result;
            }
            else {
                Clauses fromLeft = deriveClauses(left);
                Clauses fromRight = deriveClauses(right);
                Clause disjunction = new Clause();
                for (Clause c : fromLeft) {
                    disjunction.addAll(c);
                }
                for (Clause c : fromRight) {
                    disjunction.addAll(c);
                }
                return new Clauses(disjunction);
            }
        }
        else if (sentence instanceof UnarySentence) {
            UnarySentence unary = (UnarySentence)sentence;
            if (unary.nestedSentence != null) {
                return deriveClauses(unary.nestedSentence);
            }
            if (unary.isLiteral()) {
                result.add(new Clause(unary));
            }
        }
        return result;
    }
}