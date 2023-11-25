import java.util.*;

import parser.*;
import types.*;

public class KBDriver {

    public static void main(String[] args) {
        System.out.println();
        new KBDriver(args);
    }

    public KBDriver(String[] args) {
        if (args.length == 0) {
            enterInteractiveMode();
        }

        Clauses kB = new Clauses();
        Parser parser = new Parser(new Lexer(args[0]));
        while (true) {
            String cmd = parser.getCommand();
    
            if (cmd.equalsIgnoreCase("tellc")) {
                kB.add(parser.getClause());
                // parser.consumeNextToken();
            }
            else if (cmd.equalsIgnoreCase("print")) {
                System.out.println(kB);
            }
            else if (cmd.equalsIgnoreCase("eof")) {
                break;
            }
            else if (cmd.equalsIgnoreCase("ask")) {
                Sentence query = parser.getSentence();
                if (PlResolution(kB, query.getSymbol())) {
                    System.out.printf("Yes, KB entails %s\n", query.toString());
                }
                else {
                    System.out.printf("No, KB does not entail %s\n", query.toString());
                }
            }
            
        }
    }

    private void enterInteractiveMode() {
        System.out.println("Welcome to the Knowledge Base!");
        System.out.println("Please TELL or ASK me anything!");
        System.out.println("(type HELP for more information)");
        
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            String command = input.split(" ")[0];

            if (command.equalsIgnoreCase("exit") || command.equalsIgnoreCase("done") || command.equalsIgnoreCase("quit")) {
                System.out.println("Thank you for using the Knowledge Base!");
                break;
            }
            else if (command.equalsIgnoreCase("help")) {
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
            else {
                System.out.printf("%s is not a recognized command\n", command);
            }

        }

        // Close the scanner when done
        scanner.close();
    }

    public boolean PlResolution(Clauses kB, Symbol alpha) {
        Clauses clauses = new Clauses(kB);
        clauses.add(new Clause(alpha.asNegated()));
        Clauses derived = new Clauses();
        while (true) {
            for (Clause c1 : clauses) {
                for (Clause c2 : clauses) {
                    Clause resolvents = PlResolve(c1, c2);
                    if (resolvents.toString().equals("(__)")) {  // If contains the empty clause
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

    private Clause PlResolve(Clause c1, Clause c2) {
        Clause bigC, smallC, result = new Clause();
        if (c1.size() >= c2.size()) {
            bigC = c1;
            smallC = c2;
        }
        else {
            bigC = c2;
            smallC = c1;
        }

        for (Symbol s1 : bigC) {
                Symbol negatedTemp = new Symbol(s1.getValue(), s1.isNegated());
                negatedTemp.negate();
                if (!smallC.contains(negatedTemp) && !result.contains(s1)) {
                    result.add(s1);
                }
                else if (smallC.contains(negatedTemp)) {
                    result.add(new Symbol("__", false));
                }
            }
        return result;
    }
}