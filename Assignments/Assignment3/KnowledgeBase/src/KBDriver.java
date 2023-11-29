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
    
            if (cmd.equalsIgnoreCase("tellc")) {
                kB.add(parser.getClause());
            }
            else if (cmd.equalsIgnoreCase("print")) {
                System.out.println(kB);
            }
            else if (cmd.equalsIgnoreCase("eof")) {
                break;
            }
            else if (cmd.equalsIgnoreCase("ask")) {
                Sentence query = parser.getSentence();
                query = convertToCNF(query);
                // Clauses searchClauses = new Clauses();
                // deriveClauses(searchClauses, query);

                if (PlResolution(kB, query, false)) {
                    System.out.printf("Yes, KB entails %s\n", query.toString());
                }
                else {
                    System.out.printf("No, KB does not entail %s\n", query.toString());
                }
            }
            else if (cmd.equalsIgnoreCase("proof")) {
                Sentence query = parser.getSentence();
                
                if (!PlResolution(kB, query, false)) {
                    System.out.println("No proof exists");
                }
                else {
                    PlResolution(kB, query, true);
                }
                System.out.println();
            }
            else if (cmd.equalsIgnoreCase("tell")) {
                Sentence sentence = parser.getSentence();
                sentence = convertToCNF(sentence);
                deriveClauses(kB, sentence);
            }
            else if (cmd.equalsIgnoreCase("cnf")) {
                Sentence sentence = parser.getSentence();
                sentence = convertToCNF(sentence);
                System.out.println(sentence);
            }
            else if (cmd.equalsIgnoreCase("parse")) {
                Sentence sentence = parser.getSentence();
                parse(sentence);
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
        alpha = new UnarySentence(new UnarySentence(alpha));
        alpha = convertToCNF(alpha);
        if (isProof) {
            System.out.println("Proof:");
            proofIndex = kB.getProof() + 1;
            System.out.printf("%d. %-15s [Negated Goal]\n", proofIndex, alpha.getSymbol());
        }

        Clauses clauses = new Clauses(kB);
        clauses.add(new Clause(alpha.getSymbol(), proofIndex));
        Clauses derived = new Clauses();
        while (true) {
            for (int c1 = 0; c1 < clauses.size(); c1++) {
                for (int c2 = 0; c2 < clauses.size(); c2++) {
                    Clause resolvents = PlResolve(clauses, c1, c2, isProof);
                    if (resolvents.size() == 0) {
                        return true;
                    }
                    derived.add(resolvents);
                }
            }
            if (clauses.contains(derived)) {
                return false;
            }
            clauses.addAll(derived);
        }
    }

    private Clause PlResolve(Clauses clauses, int c1, int c2, boolean isProof) {
        Clause bigC, smallC, result = new Clause();
        if (clauses.get(c1).size() <= clauses.get(c2).size()) {
            bigC = clauses.get(c1);
            smallC = clauses.get(c2);
        }
        else {
            bigC = clauses.get(c2);
            smallC = clauses.get(c1);
        }

        for (Symbol s1 : bigC) {
                Symbol negatedTemp = new Symbol(s1.getValue(), !s1.isNegated());
                if (!smallC.contains(negatedTemp) && !result.contains(s1)) {
                    result.add(s1);
                }
                else if (smallC.contains(negatedTemp)) {
                    proofIndex++;
                    int oldIndex = smallC.getProofIndex();
                    smallC.remove(negatedTemp);
                    smallC.setProofIndex(proofIndex);

                    if (isProof) {
                        String clausePrint = "()";
                        if (smallC.size() != 0) {
                            clausePrint = smallC.getClause();
                        }
                        System.out.printf("%d. %-15s [Resolution on %s: %d, %d]\n", proofIndex, clausePrint, s1.getValue(), oldIndex, bigC.getProofIndex());
                    }
                    result.addAll(smallC);
                }
            }
        return result;
    }

    public Sentence convertToCNF(Sentence sentence) {
        // System.out.println(sentence);
        Sentence result = eliminateIff(sentence);
        // System.out.println(result);
        result = eliminateIf(result);
        // System.out.println(result);
        result = eliminateNOT(result, false);
        // System.out.println(result);
        result = applyDistributivity(result);
        // System.out.println(result);
        if (result instanceof UnarySentence) {
            UnarySentence us = (UnarySentence)result;
            if (us.nestedSentence != null) {
                return us.nestedSentence;
            }
        }
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
        }
        return sentence;
    }

    private Sentence eliminateNOT(Sentence sentence, boolean outterIsNegated) {
        if (sentence instanceof BinarySentence) {
            BinarySentence binarySentence = (BinarySentence)sentence;
            boolean leftIsNegated = binarySentence.getS1().isNegated;
            boolean rightIsNegated = binarySentence.getS2().isNegated;
            if (outterIsNegated) {
                binarySentence = negateBinary(binarySentence);
            }

            UnarySentence left = (UnarySentence)eliminateNOT(binarySentence.getS1(), leftIsNegated);
            UnarySentence right = (UnarySentence)eliminateNOT(binarySentence.getS2(), rightIsNegated);
            return new BinarySentence(left, binarySentence.getConnective(), right);
        }
        else {
            UnarySentence unarySentence = (UnarySentence)sentence;
            boolean shouldNegate = unarySentence.isNegated;
            if (outterIsNegated && !(unarySentence.isSymbol() || unarySentence.isLiteral())) {
                if (unarySentence.nestedSentence != null && unarySentence.nestedSentence instanceof BinarySentence) {
                    unarySentence = new UnarySentence(negateBinary((BinarySentence)unarySentence.nestedSentence));
                }
                else if (unarySentence.nestedSentence != null && unarySentence.nestedSentence instanceof UnarySentence) {
                    unarySentence = negateUnary((UnarySentence)unarySentence.nestedSentence);
                }
                else {
                    unarySentence = negateUnary(unarySentence);
                }
            }
            
            if (unarySentence.isSymbol() || unarySentence.isLiteral()) {
                // a || ~a
                return unarySentence;
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

    private BinarySentence negateBinary(BinarySentence binarySentence) {
        UnarySentence left = binarySentence.getS1();
        UnarySentence right = binarySentence.getS2();
        BinaryConnective connective = binarySentence.getConnective();
        left = negateUnary(left);
        right = negateUnary(right);

        if (connective == BinaryConnective.AND) {   // ^
            return new BinarySentence(left, BinaryConnective.OR, right);
        }
        else {  // v
            return new BinarySentence(left, BinaryConnective.AND, right);
        }
    }

    private UnarySentence negateUnary(UnarySentence unary) {
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

    private Sentence applyDistributivity(Sentence sentence) {
        if (sentence instanceof BinarySentence) {
            BinarySentence binarySentence = (BinarySentence)sentence;
            if (binarySentence.getConnective() == BinaryConnective.OR) {
                UnarySentence s1 = binarySentence.getS1();
                UnarySentence s2 = binarySentence.getS2();
                if ((BinarySentence)s1.nestedSentence != null && ((BinarySentence)s1.nestedSentence).getConnective() == BinaryConnective.AND) {
                    //(a ^ b) v c == (a v c) ^ (b v c)
                    UnarySentence a = (UnarySentence)applyDistributivity(((BinarySentence)s1.nestedSentence).getS1());
                    UnarySentence b = (UnarySentence)applyDistributivity(((BinarySentence)s1.nestedSentence).getS2());
                    UnarySentence c = (UnarySentence)applyDistributivity(s2);

                    BinarySentence innerLeft = new BinarySentence(a, BinaryConnective.OR, c);
                    UnarySentence left = new UnarySentence(innerLeft);
                    BinarySentence innerRight = new BinarySentence(b, BinaryConnective.OR, c);
                    UnarySentence right = new UnarySentence(innerRight);
                    return new BinarySentence(left, BinaryConnective.AND, right);
                }
                else if ((BinarySentence)s2.nestedSentence != null && ((BinarySentence)s2.nestedSentence).getConnective() == BinaryConnective.AND) {
                    // a v (b ^ c) == (a v b) ^ (a v c)
                    UnarySentence a = (UnarySentence)applyDistributivity(s1);
                    UnarySentence b = (UnarySentence)applyDistributivity(((BinarySentence)s2.nestedSentence).getS1());
                    UnarySentence c = (UnarySentence)applyDistributivity(((BinarySentence)s2.nestedSentence).getS2());
    
                    BinarySentence innerLeft = new BinarySentence(a, BinaryConnective.OR, b);
                    UnarySentence left = new UnarySentence(innerLeft);
                    BinarySentence innerRight = new BinarySentence(a, BinaryConnective.OR, c);
                    UnarySentence right = new UnarySentence(innerRight);
                    return new BinarySentence(left, BinaryConnective.AND, right);
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

    private void parse(Sentence sentence) {
        parse(sentence, "Orig", 0);
    }

    private void parse(Sentence sentence, String prefix, int indent) {
        if (sentence instanceof BinarySentence) {
            BinarySentence binary = (BinarySentence)sentence;
            System.out.printf("%s: [%s] Binary [%s]\n".indent(indent), prefix, binary.toString(), binary.getConnective().toString());

            if (binary.getS1() instanceof UnarySentence) {
                UnarySentence unary = (UnarySentence)binary.getS1();
                parseUnary(unary, "LHS", indent);
            }
            if (binary.getS2() instanceof UnarySentence) {
                UnarySentence unary = (UnarySentence)binary.getS2();
                parseUnary(unary, "RHS", indent);
            }
        }
    }

    private void parseUnary(UnarySentence unary, String prefix, int indent) {
        if (unary.nestedSentence == null && unary.nestedUnary == null) {
            System.out.printf(" %s: [%s] Unary [symbol]: [%s]\n".indent(indent), prefix, unary, unary.getSymbol());
        }
        else if (unary.nestedSentence != null) {
            System.out.printf(" %s: [%s] Unary [()]\n".indent(indent), prefix, unary);
            parse(unary.nestedSentence, "Sub", indent+2);
        }
        else if (unary.isNegated) {
            System.out.printf(" %s: [%s] Unary [~]\n".indent(indent), prefix, unary);
            parseUnary(unary.nestedUnary, "Sub", indent+2);
        }
    }

    private Clause deriveClauses(Clauses kB, Sentence sentence) {
        Clause result = new Clause();
        if (sentence instanceof BinarySentence) {
            BinarySentence binary = (BinarySentence)sentence;
            UnarySentence left = (UnarySentence)binary.getS1();
            UnarySentence right = (UnarySentence)binary.getS2();

            if (binary.getConnective() == BinaryConnective.AND) {
                Clause fromLeft = deriveClauses(kB, left);
                Clause fromRight = deriveClauses(kB, right);
                if (fromLeft.size() != 0) {
                    kB.add(fromLeft);
                }
                if (fromRight.size() != 0) {
                    kB.add(fromRight);
                }
                return Clause.EMPTY;
            }

            if (left.isLiteral() && binary.getConnective() == BinaryConnective.OR) {
                result.add(left.getSymbol());
            }
            else {
                result.addAll(deriveClauses(kB, left));
            }
            if (right.isLiteral() && binary.getConnective() == BinaryConnective.OR) {
                result.add(right.getSymbol());
            }
            else {
                result.addAll(deriveClauses(kB, right));
            }
        }
        else if (sentence instanceof UnarySentence) {
            UnarySentence unary = (UnarySentence)sentence;
            if (unary.nestedSentence != null) {
                return deriveClauses(kB, unary.nestedSentence);
            }
            if (unary.isLiteral()) {
                kB.add(new Clause(unary));
            }
        }
        return result;
    }
}