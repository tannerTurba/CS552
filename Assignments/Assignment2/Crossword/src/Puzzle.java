/*
 * Tanner Turba
 * November 1, 2023
 * CS 552 - Artificial Intelligence - Assignment 2
 * 
 * This is the class that the Solve class interacts with since it contains
 * the functionality for solving the crossword, including the back tracking
 * search method. This class also contains some information about the puzzle
 * and handles input that defines the board. 
 */
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class Puzzle {
    private int width = 0;
    private int height = 0;
    private Assignment board;
    private Assignment solution;
    private ArrayList<Variable> variables = new ArrayList<>();
    public int numOfCalls = 0;
    public int numOfIntersections = 0;
    public StringBuilder sb = new StringBuilder();

    /**
     * Creates a new puzzle, which includes the collection of all variables needed to solve the CSP
     * @param file the input file for the board information
     * @param dictionary the collection of legal words to use
     */
    public Puzzle(File file, Data dictionary) {
        try {
            Scanner scanner = new Scanner(file);
            width = scanner.nextInt();
            height = scanner.nextInt();
            board = new Assignment(width, height);
            solution = new Assignment(width, height);
            
            // Set the 'board,' which is an in-memory representation of the input file.
            for(int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    String element = scanner.next();
                    if (!element.equals("#")) {
                        board.getElementAt(col, row).setValue(element);
                        solution.getElementAt(col, row).setValue("_");
                    }
                }
                scanner.nextLine();
            }
            scanner.close();

            // Once the board is set, find every possible variable that needs to be completed.
            for(int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    // For every digit on the board...
                    String element = board.getElementAt(col, row).getValue();
                    if (Character.isDigit(element.charAt(0))) {
                        Variable var = null;

                        // Create and populate a Variable.
                        if (board.isAcross(col, row)) {
                            int length = calcLength(col, row, true);
                            var = new Variable(element + "a", col, row, length, true, solution);
                            for (String word : dictionary) {
                                if (var.assignment.length == word.length()) {
                                    var.domain.add(word);
                                }
                            }
                            variables.add(var);
                        }
                        if (board.isDown(col, row)) {
                            int length = calcLength(col, row, false);
                            var = new Variable(element + "d", col, row, length, false, solution);
                            for (String word : dictionary) {
                                if (var.assignment.length == word.length()) {
                                    var.domain.add(word);
                                }
                            }
                            variables.add(var);
                        }
                    }
                }
            }

            // Find the points of intersection for each Variable
            for (Variable var : variables) {
                ArrayList<Variable> temp = new ArrayList<>(variables);
                temp.remove(var);
                for (Variable v : temp) {
                    for (int i = 0; i < var.assignment.length; i++) {
                        // Store inersection information in a map
                        if (v.containsCell(var.assignment[i].getX(), var.assignment[i].getY())) {
                            numOfIntersections++;
                            var.intersections.put(i, v);
                            break;
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start the back track search process
     * @return An assignment grid that contains the solution to the crossword puzzle
     */
    public Assignment backTrackingSearch() {
        // Preprocessing data for printing and solving.
        if (Config.shouldPreprocess) {
            sb.append("** Preprocessing: constraint propagation\n");
            Queue<Integer> domainPopulations = new LinkedList<>();
            for (Variable var : variables) {
                domainPopulations.add(var.domain.size());
            }
            if (!ac3(variables)) {
                return new Assignment("FAILED");
            }
            for (Variable var : variables) {
                sb.append(String.format("  X%s: Revised domain has %d values (was %d)\n", var.getName(), var.domain.size(), domainPopulations.poll()));
                domainPopulations.add(var.domain.size());
            }
        }
        sb.append("** Running backtracking search...\n");
        return backTrackingSearch(variables, solution, 0);
    }

    /**
     * The recursive method used to solve the crossword
     * @param csp The Variables used in the csp
     * @param assignment The Assignment grid for the solution
     * @param indent The amount of indent used when printing stats
     * @return An assignment grid that contains the solution to the crossword puzzle
     */
    private Assignment backTrackingSearch(ArrayList<Variable> csp, Assignment assignment, int indent) {
        numOfCalls++;
        if (Config.isVerbosity2) {
            sb.append("Backtrack:\n".indent(indent));
        }

        // Return if the csp is considered complete
        if (isComplete(csp)) {
            if (Config.isVerbosity2) {
                sb.append("Assignment is complete!\n".indent(indent + 2));
            }
            return assignment;
        }

        // Get the next unassigned variable and sort its domain
        Variable var = getUnassignedVariable(csp);
        sortDomainVals(var);
        if (Config.isVerbosity2) {
            sb.append(String.format("Trying values for X%s\n", var.getName()).indent(indent + 2));
        }

        // For each value in the domain, test the value in the assignment for a potential solution
        for (String value : var.domain) {
            if (var.isConsistent(value)) {
                if (Config.isVerbosity2) {
                    sb.append(String.format("Assignment { X%s = %s } is consistent\n", var.getName(), value).indent(indent + 2));
                }

                // Set the assignment recurse
                var.setAssignment(value);
                Assignment result = backTrackingSearch(csp, assignment, indent + 2);

                // If failed, undo the assignment
                if (!result.getElementAt(0, 0).getValue().equals("FAILED")) {
                    return result;
                }
                var.undoAssignment();
            }
            if (Config.isVerbosity2) {
                sb.append(String.format("Assignment { X%s = %s } is inconsistent\n", var.getName(), value).indent(indent + 2));
            }
        }
        // Out of values, return failure
        return new Assignment("FAILED");
    }

    /**
     * The AC-3 method used for contraint propagation and preprocessing
     * @param csp The Variables to preprocess
     * @return false if a failure is detected
     */
    private boolean ac3(ArrayList<Variable> csp) {
        // Create a queue that where the stored values is the variable(v) being searched, and a pair containing an index of v that intersects with another variable(x)
        Queue<Pair<Variable, Pair<Integer, Variable>>> queue = new LinkedList<>();
        for (Variable var : csp) {
            for (Entry<Integer, Variable> adjacent : var.intersections.entrySet()) {
                queue.add(new Pair<>(var, new Pair<>(adjacent.getKey(), adjacent.getValue())));
            }
        }

        while (!queue.isEmpty()) {
            Pair<Variable, Pair<Integer, Variable>> pair = queue.poll();
            if (revise(pair.key, pair.value)) {
                // If domain is empty -> no solution so return failure
                if (pair.key.domain.size() == 0) {
                    return false;
                }
                for (Entry<Integer, Variable> neighbor : pair.key.intersections.entrySet()) {
                    Variable x_k = neighbor.getValue();
                    int index_i = neighbor.getKey();
                    Cell intersectionPoint = pair.key.assignment[index_i];
                    int index_k = x_k.getCellIndex(intersectionPoint.getX(), intersectionPoint.getY());     //Find index where x_i intersects with x_k
                    queue.add(new Pair<>(x_k, new Pair<>(index_k, pair.key)));         //Add x_k to queue
                }
            }
        }
        return true;
    }

    /**
     * Determines if the domain of the Variable x_i can be modified and makes those changes
     * @param x_i the Variable whose domain may be modified
     * @param x_j the <index, Variable> pair that identifies a point of intersection.
     * @return  true if the domain of x_i is modified.
     */
    private boolean revise(Variable x_i, Pair<Integer, Variable> x_j) {
        boolean revised = false;
        ArrayList<String> elementsToRemove = new ArrayList<>();
        for (String dom_i : x_i.domain) {
            // Find the index of intersection in the Variable x_j
            Cell intersectionPoint = x_i.assignment[x_j.key];
            int index_j = x_j.value.getCellIndex(intersectionPoint.getX(), intersectionPoint.getY());

            // Remove a value from x_i's domain if there is a corresponding value in x_j's domain that makes it consistent
            boolean shouldRemove = true;
            for (String dom_j : x_j.value.domain) {
                if (dom_i.charAt(x_j.key) == dom_j.charAt(index_j)) {
                    shouldRemove = false;
                    break;
                }
            }
            if(shouldRemove) {
                elementsToRemove.add(dom_i);
                revised = true;
            }
        }

        // Remove the values from the domain after looping to avoid concurrencey error
        x_i.domain.removeAll(elementsToRemove);
        return revised;
    }

    /**
     * Determines if all Variables have been assigned
     * @param variables the set Variables to check
     * @return true if all variables have been assigned
     */
    private boolean isComplete(ArrayList<Variable> variables) {
        Iterator<Variable> vars = variables.iterator();
        while(vars.hasNext()) {
            Variable var = vars.next();
            if (!var.isAssigned()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sorts the domain values in the specified Variable
     * @param var the Variable who's domain's values will be sorted
     */
    private void sortDomainVals(Variable var) {
        if (Config.valueOrder == IterationType.LEAST_CONSTRAINING_VALUE) {
            var.domain.sort(new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    int s1Conflicts = 0;
                    int s2Conflicts = 0;

                    // For each intersection point
                    for (Entry<Integer, Variable> intersection : var.intersections.entrySet()) {
                        // Get the index value of the intersection for this variable and that variable
                        int thisIndex = intersection.getKey();
                        Cell cell = var.assignment[thisIndex];
                        int thatIndex = intersection.getValue().getCellIndex(cell.getX(), cell.getY());

                        // If the cell is still unassigned...
                        if (cell.getCount() == 0) {
                            // Loop through every possible value for that variable
                            for (String thatVal : intersection.getValue().domain) {
                                // Increment number of conflicts for corresponding value if char vals do not match at intersecting indices. 
                                if (s1.charAt(thisIndex) != thatVal.charAt(thatIndex)) {
                                    s1Conflicts++;
                                }
                                if (s2.charAt(thisIndex) != thatVal.charAt(thatIndex)) {
                                    s2Conflicts++;
                                }
                            }
                        }
                    }

                    // Return value based on total number of coflicts.
                    if (s1Conflicts < s2Conflicts) {
                        return 1;
                    }
                    else if(s1Conflicts > s2Conflicts) {
                        return -1;
                    }
                    else {
                        return 0;
                    }
                }
            });
        }
        else {
            // Static - sort alphabetically
            var.domain.sort(Comparator.comparing(s -> s.toLowerCase()));
        }
    }

    /**
     * Gets the next unassigned Variable from the set of Variables
     * @param vars the set of Variables
     * @return the next unassigned Variable
     */
    private Variable getUnassignedVariable(ArrayList<Variable> vars) {
        // Only sort unassigned Variables, so remove assigned ones
        ArrayList<Variable> temp = new ArrayList<>(vars);
        temp.removeIf(var -> (var.isAssigned() == true));

        temp.sort(new Comparator<Variable>() {
            @Override
            public int compare(Variable v1, Variable v2) {
                if (Config.orderingHeuristic == VarOrdering.MINIMUM_REMAINING_VALUES) {
                    // Make comparison based on number of domain values are consistent with the current assignment
                    int v1Consistencies = 0;
                    int v2Consistencies = 0;
                    for (String str : v1.domain) {
                        if (v1.isConsistent(str)) {
                            v1Consistencies++;
                        }
                    }
                    for (String str : v2.domain) {
                        if (v2.isConsistent(str)) {
                            v2Consistencies++;
                        }
                    }
        
                    if (v1Consistencies < v2Consistencies) {
                        return -1;
                    }
                    else if (v1Consistencies > v2Consistencies) {
                        return 1;
                    }
                    else {
                        return 0;
                    }
                }
                else if (Config.orderingHeuristic == VarOrdering.MOST_CONSTRAINING_VARIABLE) {
                    // Make comparsion based on the number of intersections in each Variable.
                    int v1Intersections = v1.intersections.size();
                    int v2Intersections = v2.intersections.size();

                    if (v1Intersections < v2Intersections) {
                        return -1;
                    }
                    else if (v1Intersections > v2Intersections) {
                        return 1;
                    }
                    else {
                        return 0;
                    }
                }
                else if (Config.orderingHeuristic == VarOrdering.HYBRID) {
                    // Use Minimum Remaining Values or Most Contraining Variable if there's a tie.
                    int v1Consistencies = 0;
                    int v2Consistencies = 0;
                    for (String str : v1.domain) {
                        if (v1.isConsistent(str)) {
                            v1Consistencies++;
                        }
                    }
                    for (String str : v2.domain) {
                        if (v2.isConsistent(str)) {
                            v2Consistencies++;
                        }
                    }

                    if (v1Consistencies < v2Consistencies) {
                        return -1;
                    }
                    else if (v1Consistencies > v2Consistencies) {
                        return 1;
                    }
                    else {
                        int v1Intersections = v1.intersections.size();
                        int v2Intersections = v2.intersections.size();

                        if (v1Intersections < v2Intersections) {
                            return -1;
                        }
                        else if (v1Intersections > v2Intersections) {
                            return 1;
                        }
                        else {
                            return 0;
                        }
                    }
                }
                else {
                    // Static: Sort based on number and alignmnet
                    int v1VarNum = Integer.parseInt(v1.getName().replaceAll("(d|a)", ""));
                    int v2VarNum = Integer.parseInt(v2.getName().replaceAll("(d|a)", ""));
        
                    if (v1VarNum > v2VarNum) {
                        return 1;
                    }
                    else if (v1VarNum < v2VarNum) {
                        return -1;
                    }
                    else {
                        if (v1.isAcross()) {
                            return -1;
                        }
                        return 1;
                    }
                }
            }
        });
        return temp.get(0);
    }

    /**
     * @return the set of Variables
     */
    public ArrayList<Variable> getVariables() {
        return variables;
    }

    /**
     * Calculate the length of a Variable
     * @param col the column the Variable is in
     * @param row the row the Variable is in
     * @param isAcross true if Variable is horizontal
     * @return the length of the variable
     */
    private int calcLength(int col, int row, boolean isAcross) {
        int count = 0;
        while (!board.getElementAt(col, row).getValue().equals("#")) {
            count++;
            if (isAcross) {
                col++;
                if (col >= width) {
                    return count;
                }
            }
            else {
                row++;
                if (row >= height) {
                    return count;
                }
            }
        }
        return count;
    }

    /**
     * @return String representation of Puzzle
     */
    public String toString() {
        return board.asString(true);
    }
}
