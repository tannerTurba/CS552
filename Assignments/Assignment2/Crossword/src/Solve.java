/*
 * Tanner Turba
 * November 1, 2023
 * CS 552 - Artificial Intelligence - Assignment 2
 * 
 * This class contains the argument processing and output for the program's results.
 */
import java.io.*;

public class Solve {
    private File dictionaryFile;
    private File puzzleFile;
    private int verbosity;
    private long startTime = 0;
    private long stopTime = 0;

    public static void main(String[] args) {
        System.out.println();
        new Solve(args);
    }

    public Solve(String[] args) {
        parseArgs(args);
        Data dataDictionary = new Data(dictionaryFile);

        Puzzle puzzel = new Puzzle(puzzleFile, dataDictionary);
        startStopwatch();
        Assignment a = puzzel.backTrackingSearch();
        stopStopwatch();

        if (verbosity == 0) {
            verbosity0(a, puzzel);
        }
        else if (verbosity == 1) {
            verbosity1(a, puzzel);
        }
        else {
            verbosity2(a, puzzel, dataDictionary);
        }
    }

    private void verbosity2(Assignment solution, Puzzle puzzel, Data dictionary) { 
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("* Reading dictionary from [../%s]\n", dictionaryFile.getPath()));
        sb.append(String.format("** Dictionary has %d words\n\n", dictionary.size()));

        sb.append(String.format("* Reading puzzle from [../%s]\n", puzzleFile.getPath()));
        sb.append("** Puzzle\n");
        sb.append(puzzel.toString() + '\n');

        sb.append(String.format("* CSP has %d variables\n", puzzel.getVariables().size()));
        sb.append(String.format("* CSP has %d constraints\n", puzzel.numOfIntersections / 2));
        sb.append("* Attempting to solve crossword puzzle...\n");
        
        sb.append(puzzel.sb.toString());

        System.out.println(sb.toString());
        verbosity0(solution, puzzel);
    }

    private void verbosity1(Assignment solution, Puzzle puzzel) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("* Reading dictionary from [../%s]\n", dictionaryFile.getPath()));
        sb.append(String.format("* Reading puzzle from [../%s]\n", puzzleFile.getPath()));
        sb.append(String.format("* CSP has %d variables\n", puzzel.getVariables().size()));
        sb.append(String.format("* CSP has %d constraints\n", puzzel.numOfIntersections / 2));
        sb.append("* Attempting to solve crossword puzzle...\n");

        System.out.println(sb.toString());
        verbosity0(solution, puzzel);
    }

    private void verbosity0(Assignment solution, Puzzle puzzle) {
        boolean wasSuccessful = !solution.toString().contains("FAILED");
        StringBuilder sb = new StringBuilder();

        if(wasSuccessful) {
            sb.append("SUCCESS! ");
        }
        else {
            sb.append("FAILED; ");
        }
        sb.append(String.format("Solving took %dms (%d recursive calls)\n\n", getElapsedTime(), puzzle.numOfCalls));

        if (wasSuccessful) {
            sb.append(solution.toString());
        }
        System.out.println(sb.toString());
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
                    Config.orderingHeuristic = VarOrdering.MINIMUM_REMAINING_VALUES;
                }
                else if (args[i].toLowerCase().equals("deg")) {
                    Config.orderingHeuristic = VarOrdering.MOST_CONSTRAINING_VARIABLE;
                }
                else if (args[i].toLowerCase().equals("mrv+deg")) {
                    Config.orderingHeuristic = VarOrdering.HYBRID;
                }
                else {
                    Config.orderingHeuristic = VarOrdering.STATIC;
                }
            }
            else if(args[i].equals("-vo") || args[i].equals("--value-order")) {
                i++;
                if (args[i].toLowerCase().equals("lcv")) {
                    Config.valueOrder = IterationType.LEAST_CONSTRAINING_VALUE;
                }
                else {
                    Config.valueOrder = IterationType.STATIC;
                }
            }
            else if(args[i].equals("-lfc") || args[i].equals("--limited-forward-check")) {
                Config.isLimitedForwardChecking = true;
            }
            else if(args[i].equals("-v")) {
                i++;
                verbosity = Integer.parseInt(args[i]);
                if (verbosity >= 2) {
                    Config.isVerbosity2 = true;
                }
            }
            else if(args[i].equals("--preprocess")) {
                Config.shouldPreprocess = true;
            }
        }
    }

    /**
     * Start a stopwatch to begin recording the length of execution.
     */
    private void startStopwatch() {
        startTime = System.currentTimeMillis();
    }

    /**
     * Stop a stopwatch to end recording the length of execution.
     */
    private void stopStopwatch() {
        stopTime = System.currentTimeMillis();
    }

    /**
     * @return The length of execution.
     */
    public long getElapsedTime() {
        return stopTime - startTime;
    }
}
