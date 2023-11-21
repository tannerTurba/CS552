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

        Clauses clauses = new Clauses();
        Parser parser = new Parser(new Lexer(args[0]));
        while (true) {
            String cmd = parser.getCommand();
    
            if (cmd.equalsIgnoreCase("tellc")) {
                clauses.add(parser.getClause());
            }
            else if (cmd.equalsIgnoreCase("print")) {
                System.out.println(clauses);
            }
            else if (cmd.equalsIgnoreCase("eof")) {
                break;
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
}