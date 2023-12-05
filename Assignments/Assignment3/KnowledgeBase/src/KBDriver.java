import parser.*;
import types.*;

public class KBDriver {
    /**
     * Starts program execution
     * @param args
     */
    public static void main(String[] args) {
        System.out.println();
        new KBDriver(args);
    }

    /**
     * Initializes an instance of KBDriver
     * @param args should contain the filepath, if desired
     */
    public KBDriver(String[] args) {
        String fileName;
        if (args.length > 0) {
            fileName = args[0];
        }
        else {
            fileName = null;
        }
        
        // Create parser
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

        // Initialize knowledge base to add data to
        Clauses kB = new Clauses();
        while (true) {
            // If reading from input, prompt '>'
            if (fileName == null) {
                System.out.print("> ");
            }
            String cmd = parser.getCommand();

            // If reading from file, show command
            if (fileName != null && !cmd.equalsIgnoreCase("eof")) {
                System.out.printf("> %s ", cmd.toUpperCase());
            }
    
            // Continue based on command
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

                // Determine if KB entails the query
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

                // Print proof if a solution exists
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

                // Convert sentence to CNF and derive clauses to add to the KB
                sentence = convertToCNF(sentence);
                kB.addAll(deriveClauses(sentence));
            }
            else if (cmd.equalsIgnoreCase("cnf")) {
                Sentence sentence = parser.getSentence();
                if (fileName != null) {
                    System.out.print(sentence.toString() + "\n");
                }

                // Convert sentence to CNF and derive clauses
                sentence = convertToCNF(sentence);
                Clauses x = deriveClauses(sentence);
                System.out.println("Result: " + x.toString().trim());
            }
            else if (cmd.equalsIgnoreCase("parse")) {
                Sentence sentence = parser.getSentence();
                if (fileName != null) {
                    System.out.print(sentence + "\n");
                }

                // parse the sentence
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

    /**
     * Determines if the knowledge base entails alpha
     * @param kB the knowledge base
     * @param alpha the sentence to check for
     * @param isProof true if finding a proof
     * @return
     */
    public boolean PlResolution(Clauses kB, Sentence alpha, boolean isProof) {
        Clauses premises = new Clauses();
        Clauses negatedGoal = new Clauses();

        // Find the clauses from the negated goal by converting to CNF and deriving clauses
        Sentence negAlpha = new UnarySentence(new UnarySentence(alpha));
        negAlpha = convertToCNF(negAlpha);
        Clauses fromNegAlpha = deriveClauses(negAlpha);
        for (Clause c : fromNegAlpha) {
            negatedGoal.add(c);
        }

        // Create a set of all clauses from the KB and from the negated goal
        Clauses clauses = new Clauses(kB);
        for (Clause c : clauses) {
            premises.add(c);
        }
        clauses.addAll(fromNegAlpha);
        clauses.sort();

        Clauses derived = new Clauses();
        while (true) {
            // for all pair of clauses, find their resolvents by resolving complementary literals
            for (int c1 = 0; c1 < clauses.size(); c1++) {
                for (int c2 = c1 + 1; c2 < clauses.size(); c2++) {
                    Clauses resolvents = PlResolve(clauses.get(c1).getCopy(), clauses.get(c2).getCopy(), isProof, derived);

                    // if resolvents contains {}, then print the proof(if necessary) and return true
                    if (resolvents.contains(Clause.EMPTY)) {
                        if (isProof) {
                            Clause solution = resolvents.get(resolvents.indexOf(Clause.EMPTY));
                            solution.printProof(premises, negatedGoal);
                            Clause.clearExistingProofClauses();
                        }
                        return true;
                    }
                
                    // otherwise, add all resolvents to the set of derived clauses and repeat
                    derived.addAll(resolvents);
                }
            }
            // Return false the original set of clauses contains the derived clauses
            if (clauses.contains(derived)) {
                return false;
            }
            clauses.addAll(derived);
            clauses.sort();
            clauses = clauses.factor();
        }
    }

    /**
     * Create resolvents by resolving complementary literals
     * @param c1 clause 1
     * @param c2 clause 2
     * @param isProof true, if generating a proof
     * @param derived the set of already derived clauses from previoys resolves
     * @return the set of clauses that are generated
     */
    private Clauses PlResolve(Clause c1, Clause c2, boolean isProof, Clauses derived) {
        // Find the larger set and smaller Clause
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

        // get clauses from negated clause
        Clauses fromNeg = negateClause(small);

        // If there is a complementary literal in 'fromNeg' inside 'big'
        if (big.containsComplementaryLiterals(fromNeg)) {
            // Create resolvent and set necessary references for later use in the proof
            Clause resolvent = big.getCopy();
            resolvent.removeComplementaryLiterals(fromNeg);
            if (!derived.contains(resolvent)) {
                if (resolvent.size() == 0) {
                    resolvent = Clause.EMPTY;
                }
                resolvent.setParent1(big.getCopy());
                resolvent.setParent2(small.getCopy());
                
                if (resolvent == Clause.EMPTY) {
                    result.add(resolvent);
                    return result.factor();
                }
                else {
                    result.add(resolvent.getCopy());
                    return result.factor();
                }
            }
        }
        return result.factor();
    }

    /**
     * Negate a clause
     * @param c the clause to negate
     * @return a set of Clauses that result from negating the clause
     */
    private Clauses negateClause(Clause c) {
        // Wrap Symbols of clauses in Unary or Binary Sentences
        Sentence negated;
        if (c.size() == 1) {
            if (c.get(0).isNegated()) {
                negated = new UnarySentence(new UnarySentence(new Symbol(c.get(0).getValue(), false)));
            }
            else {
                negated = new UnarySentence(c.get(0));
            }
        }
        else {
            UnarySentence left, right;
            if (c.get(0).isNegated()) {
                left = new UnarySentence(new UnarySentence(new Symbol(c.get(0).getValue(), false)));
            }
            else {
                left = new UnarySentence(c.get(0));
            }
            if (c.get(1).isNegated()) {
                right = new UnarySentence(new UnarySentence(new Symbol(c.get(1).getValue(), false)));
            }
            else {
                right = new UnarySentence(c.get(1));
            }

            BinarySentence b = new BinarySentence(left, BinaryConnective.OR, right);
            for (int i = 2; i < c.size(); i++) {
                if (c.get(2).isNegated()) {
                    right = new UnarySentence(new UnarySentence(new Symbol(c.get(i).getValue(), false)));
                }
                else {
                    right = new UnarySentence(c.get(i));
                }
                b = new BinarySentence(new UnarySentence(b), BinaryConnective.OR, right);
            }
            negated = b;
        }

        // Wrap generated sentence in ~(...) and convert to CNF before returning
        negated = new UnarySentence(new UnarySentence(negated));
        negated = convertToCNF(negated);
        return deriveClauses(negated);
    }

    /**
     * Convert the sentence to CNF form
     * @param sentence the sentence to convert
     * @return a CNF compliant sentence
     */
    public Sentence convertToCNF(Sentence sentence) {
        Sentence result = eliminateIff(sentence);
        result = eliminateIf(result);
        result = eliminateNot(result, false);
        result = applyDistributivity(result);
        return result;
    }

    /**
     * Eliminate all biconditionals from sentence
     * @param sentence the sentence to modify
     * @return the new sentence
     */
    private Sentence eliminateIff(Sentence sentence) {
        if (sentence instanceof BinarySentence) {
            BinarySentence binarySentence = (BinarySentence)sentence;

            // Recurse to eliminate the biconditional in nested sentences
            UnarySentence u1 = (UnarySentence)eliminateIff(binarySentence.getLeft());
            UnarySentence u2 = (UnarySentence)eliminateIff(binarySentence.getRight());

            // If a biconditional, reformulate to equivalent sentence before returning
            if (binarySentence.getConnective() == BinaryConnective.IFF) {
                BinarySentence s1 = new BinarySentence(u1, BinaryConnective.IF, u2);
                BinarySentence s2 = new BinarySentence(u2, BinaryConnective.IF, u1);
                UnarySentence left = new UnarySentence(s1);
                UnarySentence right = new UnarySentence(s2);
                return new BinarySentence(left, BinaryConnective.AND, right);
            }

            // otherwise return new sentence with reformulated left and right
            return new BinarySentence(u1, binarySentence.getConnective(), u2);
        }
        else if (sentence instanceof UnarySentence) {
            UnarySentence unary = (UnarySentence)sentence;

            // eliminate biconditional in nested sentence or nested unary 
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

    /**
     * Eliminate the conditional from a specified sentence
     * @param sentence the Sentence to eliminate the conditional from
     * @return a sentence not containing the conditional
     */
    private Sentence eliminateIf(Sentence sentence) {
        if (sentence instanceof BinarySentence) {
            BinarySentence binarySentence = (BinarySentence)sentence;

            // Recurse to eliminate the conditional in nested sentences
            UnarySentence u1 = (UnarySentence)eliminateIf(binarySentence.getLeft());
            UnarySentence right = (UnarySentence)eliminateIf(binarySentence.getRight());

            // If a conditional, reformulate to equivalent sentence before returning
            BinaryConnective connective = binarySentence.getConnective();
            if (binarySentence.getConnective() == BinaryConnective.IF) {
                UnarySentence left = new UnarySentence(u1);
                return new BinarySentence(left, BinaryConnective.OR, right);
            }

            // otherwise return new sentence with reformulated left and right
            return new BinarySentence(u1, connective, right);
        }
        else if (sentence instanceof UnarySentence) {
            UnarySentence unary = (UnarySentence)sentence;

            // eliminate conditional in nested sentence or nested unary 
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

    /**
     * Eliminate the logical Not from a Sentence
     * @param sentence the Sentence to eliminate Not from 
     * @param outterIsNegated
     * @return
     */
    private Sentence eliminateNot(Sentence sentence, boolean outterIsNegated) {
        if (sentence instanceof BinarySentence) {
            BinarySentence binarySentence = (BinarySentence)sentence;
            boolean leftIsNegated = binarySentence.getLeft().isNegated;
            boolean rightIsNegated = binarySentence.getRight().isNegated;

            // Negate current sentence if nested in a negated Unary
            if (outterIsNegated) {
                binarySentence = (BinarySentence)negate(binarySentence);
            }

            // Recurse to eliminate the conditional in nested sentences
            UnarySentence left = (UnarySentence)eliminateNot(binarySentence.getLeft(), leftIsNegated && !binarySentence.getLeft().isLiteral());
            UnarySentence right = (UnarySentence)eliminateNot(binarySentence.getRight(), rightIsNegated && !binarySentence.getRight().isLiteral());
            return new BinarySentence(left, binarySentence.getConnective(), right);
        }
        else {
            UnarySentence unarySentence = (UnarySentence)sentence;
            boolean shouldNegate = unarySentence.isNegated;
            // If nested in a negated sentence (i.e ~(...)), and is not a literal
            if (outterIsNegated && !unarySentence.isLiteral()) {
                // If nested sentence is a BinarySentence
                if (unarySentence.nestedSentence != null && unarySentence.nestedSentence instanceof BinarySentence) {
                    return new UnarySentence((BinarySentence)negate(unarySentence.nestedSentence));
                }
                // If nested sentence is a UnarySentence
                else if (unarySentence.nestedSentence != null && unarySentence.nestedSentence instanceof UnarySentence) {
                    return (UnarySentence)negate(unarySentence.nestedSentence);
                }
                // Otherwise is a nested UnarySentence, so negate
                else {
                    return (UnarySentence)negate(unarySentence);
                }
            }
            // If nested in a sentence and is a literal or symbol
            else if (outterIsNegated && (unarySentence.isSymbol() || unarySentence.isLiteral())) {
                return negate(unarySentence);
            }
            
            // If not nested in a negated sentence...
            if (unarySentence.isSymbol() || unarySentence.isLiteral()) {
                // a || ~a
                return unarySentence.getLiteralValue();
            }
            else if (unarySentence.nestedUnary != null) {
                // ~(a)
                return eliminateNot(unarySentence.nestedUnary, shouldNegate);
            }
            else {
                // (a)
                return new UnarySentence(eliminateNot(unarySentence.nestedSentence, shouldNegate));
            }
        }
    }

    /**
     * Negates a Sentence
     * @param sentence the Sentence to negate
     * @return a negated version of the Sentence
     */
    public Sentence negate(Sentence sentence) { 
        if (sentence instanceof BinarySentence) {
            BinarySentence binary = (BinarySentence)sentence;

            // Negate and switch signs
            if (binary.getConnective() == BinaryConnective.AND) {
                return new BinarySentence((UnarySentence)negate(binary.getLeft()), BinaryConnective.OR, (UnarySentence)negate(binary.getRight()));
                // return new BinarySentence((UnarySentence)eliminateNot(binary.getLeft(), true), BinaryConnective.OR, (UnarySentence)eliminateNot(binary.getRight(), true));
            }
            else {
                return new BinarySentence((UnarySentence)negate(binary.getLeft()), BinaryConnective.AND, (UnarySentence)negate(binary.getRight()));
                // return new BinarySentence((UnarySentence)eliminateNot(binary.getLeft(), true), BinaryConnective.AND, (UnarySentence)eliminateNot(binary.getRight(), true));
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

    /**
     * Distributes the associativity where needed 
     * @param sentence The Sentence to apply to
     * @return A Sentence 
     */
    private Sentence applyDistributivity(Sentence sentence) {
        if (sentence instanceof BinarySentence) {
            BinarySentence binarySentence = (BinarySentence)sentence;
            UnarySentence s1 = binarySentence.getLeft();
            UnarySentence s2 = binarySentence.getRight();
            UnarySentence a = null, b = null, c = null, d = null;

            // Get all four parts of a BinarySentence, if they exist
            if (s1.nestedSentence != null && s1.nestedSentence instanceof BinarySentence) {
                //(a ^ b) v c 
                a = (UnarySentence)applyDistributivity(((BinarySentence)s1.nestedSentence).getLeft());
                b = (UnarySentence)applyDistributivity(((BinarySentence)s1.nestedSentence).getRight());
            }
            else {
                // a v c
                a = (UnarySentence)applyDistributivity(s1);
                b = null;
            }
            if (s2.nestedSentence != null && s2.nestedSentence instanceof BinarySentence) {
                // a v (c ^ d)
                c = (UnarySentence)applyDistributivity(((BinarySentence)s2.nestedSentence).getLeft());
                d = (UnarySentence)applyDistributivity(((BinarySentence)s2.nestedSentence).getRight());
            }
            else {
                // a v c
                c = (UnarySentence)applyDistributivity(s2);
                d = null;
            }

            if (binarySentence.getConnective() == BinaryConnective.OR) {

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
                
                if (a != null && b != null && c != null && d == null && ((BinarySentence)s1.nestedSentence).getConnective() == BinaryConnective.AND) {
                    //(a ^ b) v c == (a v c) ^ (b v c)
                    BinarySentence innerLeft = new BinarySentence(a, BinaryConnective.OR, c);
                    UnarySentence left = new UnarySentence(innerLeft);
                    BinarySentence innerRight = new BinarySentence(b, BinaryConnective.OR, c);
                    UnarySentence right = new UnarySentence(innerRight);
                    return new BinarySentence(left, BinaryConnective.AND, right);
                }
                else if (a != null && b == null && c != null && d != null && ((BinarySentence)s2.nestedSentence).getConnective() == BinaryConnective.AND) {
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

            // Reform Sentence
            UnarySentence left = (UnarySentence)applyDistributivity(binarySentence.getLeft());
            UnarySentence right = (UnarySentence)applyDistributivity(binarySentence.getRight());
            return new BinarySentence(left, binarySentence.getConnective(), right);
        }
        else if (sentence instanceof UnarySentence) {
            UnarySentence unary = (UnarySentence)sentence;
            // Apply to nested Sentence and set
            if (unary.nestedSentence != null) {
                unary.setNestedSentence(applyDistributivity(unary.nestedSentence));
                return unary;
            }
        }
        return sentence;
    }

    /**
     * Pull Clauses from a Sentence that contains many clauses
     * @param sentence the Sentence containing Clauses
     * @return a set of Clauses
     */
    private Clauses deriveClauses(Sentence sentence) {
        Clauses result = new Clauses();
        if (sentence instanceof BinarySentence) {
            BinarySentence binary = (BinarySentence)sentence;
            UnarySentence left = (UnarySentence)binary.getLeft();
            UnarySentence right = (UnarySentence)binary.getRight();
            Clauses fromLeft = deriveClauses(left);
            Clauses fromRight = deriveClauses(right);

            // Add Clauses from left and right and add them to the set separately
            if (binary.getConnective() == BinaryConnective.AND) {
                if (fromLeft.size() != 0) {
                    result.addAll(fromLeft);
                }
                if (fromRight.size() != 0) {
                    result.addAll(fromRight);
                }
                return result;
            }
            else {
                // Create a disjunction of all clauses, and add to set of Clauses
                Clause disjunction = new Clause();
                for (Clause c : fromLeft) {
                    disjunction.addAll(c);
                }
                for (Clause c : fromRight) {
                    disjunction.addAll(c);
                }
                return new Clauses(disjunction).factor();
            }
        }
        else if (sentence instanceof UnarySentence) {
            UnarySentence unary = (UnarySentence)sentence;
            if (unary.nestedSentence != null) {
                // Recurse to the nested sentence level.
                return deriveClauses(unary.nestedSentence);
            }
            if (unary.isLiteral()) {
                // Return the literal value
                result.add(new Clause(unary));
            }
        }
        return result;
    }
}