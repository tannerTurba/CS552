import java.io.*;

public class Solve {
    private File dictionaryFile;
    private File puzzleFile;
    private int verbosity;
    private VarOrdering orderingHeuristic = VarOrdering.STATIC;
    private IterationType valueOrder = IterationType.STATIC;
    private boolean isLimitedForwardChecking = false;
    private boolean shouldPreprocess = false;

    public static void main(String[] args) {
        System.out.println();
        new Solve(args);
    }

    public Solve(String[] args) {
        parseArgs(args);
        Data dataDictionary = new Data(dictionaryFile);
        System.out.println(dataDictionary);
        System.out.println();

        Puzzle puzzel = new Puzzle(puzzleFile, dataDictionary, orderingHeuristic, isLimitedForwardChecking);
        Assignment a = puzzel.backTrackingSearch();
        System.out.println(puzzel);
        System.out.println(a);
    }

    /**
     * Sorts through the passed arguments and saves them in the Solve properties.
     * @param args
     */
    private void parseArgs(String[] args) {
        for(int i = 0; i < args.length; i++) {
            if(args[i].equals("-d")) {
                i++;
                dictionaryFile = new File(args[i]);
            }
            else if(args[i].equals("-p")) {
                i++;
                puzzleFile = new File(args[i]);
            }
            else if(args[i].equals("-vs") || args[i].equals("--variable-selection")) {
                i++;
                if (args[i].toLowerCase().equals("mrv")) {
                    orderingHeuristic = VarOrdering.MINIMUM_REMAINING_VALUES;
                }
                else if (args[i].toLowerCase().equals("deg")) {
                    orderingHeuristic = VarOrdering.MOST_CONSTRAINING_VARIABLE;
                }
                else if (args[i].toLowerCase().equals("mrv+deg")) {
                    orderingHeuristic = VarOrdering.HYBRID;
                }
                else {
                    orderingHeuristic = VarOrdering.STATIC;
                }
            }
            else if(args[i].equals("-vo") || args[i].equals("--value-order")) {
                i++;
                if (args[i].toLowerCase().equals("lcv")) {
                    valueOrder = IterationType.LEAST_CONSTRAINING_VALUE;
                }
                else {
                    valueOrder = IterationType.STATIC;
                }
            }
            else if(args[i].equals("-lfc") || args[i].equals("--limited-forward-check")) {
                isLimitedForwardChecking = true;
            }
            else if(args[i].equals("-v")) {
                i++;
                verbosity = Integer.parseInt(args[i]);
            }
            else if(args[i].equals("--preprocess")) {
                shouldPreprocess = true;
            }
        }
    }

}
