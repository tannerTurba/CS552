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
                System.out.printf("\n> %s ", cmd.toUpperCase());
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
                    System.out.print(query);
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
                    System.out.print(sentence);
                }
                sentence = sentence.convertToCNF();
                kB.addAll(sentence.deriveClauses());
            }
            else if (cmd.equalsIgnoreCase("cnf")) {
                Sentence sentence = parser.getSentence();
                if (fileName != null) {
                    System.out.print(sentence);
                }

                sentence = sentence.convertToCNF();
                Clauses x = sentence.deriveClauses();
                System.out.println(x);
            }
            else if (cmd.equalsIgnoreCase("parse")) {
                Sentence sentence = parser.getSentence();
                if (fileName != null) {
                    System.out.print(sentence + "\n");
                }

                sentence.parse();
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
        Sentence negAlpha = new UnarySentence(new UnarySentence(alpha));
        negAlpha = negAlpha.convertToCNF();
        Clauses fromNegAlpha = negAlpha.deriveClauses();
        if (isProof) {
            System.out.println("Proof:");
            proofIndex = kB.getProof();
            for (Clause c : fromNegAlpha) {
                proofIndex++;
                c.setProofIndex(proofIndex);
                System.out.printf("%d. %-15s [Negated Goal]\n", proofIndex, c.getClause());
            }
        }

        Clauses clauses = new Clauses(kB);
        clauses.addAll(fromNegAlpha);
        clauses.sort();
        Clauses derived = new Clauses();
        Clauses solution = new Clauses();
        while (true) {
            // beginning round
            for (int c1 = 0; c1 < clauses.size(); c1++) {
                for (int c2 = c1 + 1; c2 < clauses.size(); c2++) {
                    Clauses resolvents = PlResolve(clauses, clauses.get(c1).getCopy(), clauses.get(c2).getCopy(), isProof, solution);
                    if (resolvents.contains(Clause.EMPTY)) {
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

    private Clauses PlResolve(Clauses clauses, Clause c1, Clause c2, boolean isProof, Clauses solution) {
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
        negated = negated.convertToCNF();
        Clauses fromNeg = negated.deriveClauses();
        
        for (Clause negClause : fromNeg) {
            for (Symbol negatedTemp : negClause) {
                if (!big.contains(negatedTemp)) {
                    result.add(big);
                }
                else if (big.contains(negatedTemp)) {
                    big.remove(negatedTemp);
                    if (!solution.contains(big)) {
                        proofIndex++;
                        int oldIndex = big.getProofIndex();
                        big.setProofIndex(proofIndex);
                        if (big.size() == 0) {
                            result.add(Clause.EMPTY);
                        }
                        else {
                            result.add(big);
                        }
        
                        if (isProof) {
                            solution.add(big);
                            String clausePrint = "()";
                            if (big.size() != 0) {
                                clausePrint = big.getClause();
                            }
                            String resolvedOn;
                            if (negatedTemp.isNegated()) {
                                resolvedOn = negatedTemp.getValue();
                            }
                            else {
                                resolvedOn = String.format("~%s", negatedTemp.getValue());
                            }
                            System.out.printf("%d. %-15s [Resolution on %s: %d, %d]\n", proofIndex, clausePrint, resolvedOn, oldIndex, small.getProofIndex());
                        }
                    }
                }
            }
        }
        return result.factor();
    }
}