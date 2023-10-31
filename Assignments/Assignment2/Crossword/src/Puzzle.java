import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class Puzzle {
    private int width = 0;
    private int height = 0;
    private Assignment board;
    private Assignment solution;
    private PriorityQueue<Variable> variables = new PriorityQueue<>();
    public int numOfCalls = 0;
    public int numOfIntersections = 0;
    public StringBuilder sb = new StringBuilder();

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
                    String element = board.getElementAt(col, row).getValue();
                    if (Character.isDigit(element.charAt(0))) {
                        Variable var = null;
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

            // Add dictionary words of the same length to the domain of every corresponding variable.
            // Iterator<Variable> vars = variables.iterator();
            // while(vars.hasNext()) {
            //     Variable var = vars.next();
            //     for (String word : dictionary) {
            //         if (var.assignment.length == word.length()) {
            //             var.domain.add(word);
            //         }
            //     }
            // }

            // Find intersections for forward checking
            // if (Config.isLimitedForwardChecking || Config.valueOrder == IterationType.LEAST_CONSTRAINING_VALUE) {
                for (Variable var : variables) {
                    PriorityQueue<Variable> temp = new PriorityQueue<>(variables);
                    temp.remove(var);
                    for (Variable v : temp) {
                        for (int i = 0; i < var.assignment.length; i++) {
                            if (v.containsCell(var.assignment[i].getX(), var.assignment[i].getY())) {
                                numOfIntersections++;
                                var.intersections.put(i, v);
                                break;
                            }
                        }
                    }
                }
            // }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Assignment backTrackingSearch() {
        return backTrackingSearch(variables, solution, 0);
    }

    private Assignment backTrackingSearch(PriorityQueue<Variable> csp, Assignment assignment, int indent) {
        sb.append("Backtrack:\n".indent(indent));
        numOfCalls++;
        if (isComplete(csp)) {
            sb.append("Assignment is complete!\n".indent(indent + 2));
            return assignment;
        }
        Variable var = getUnassignedVariable(csp);
        sb.append(String.format("Trying values for X%s\n", var.getName()).indent(indent + 2));

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

        for (String value : var.domain) {
            // if (assignment.isConsistent(var, value, isLimitedForwardChecking)) {
            if (var.isConsistent(value)) {
                sb.append(String.format("Assignment { X%s = %s } is consistent\n", var.getName(), value).indent(indent + 2));
                var.setAssignment(value);
                //ConstrainPropegation?
                Assignment result = backTrackingSearch(csp, assignment, indent + 2);
                if (!result.getElementAt(0, 0).getValue().equals("FAILED")) {
                    return result;
                }
                var.undoAssignment();
            }
            sb.append(String.format("Assignment { X%s = %s } is inconsistent\n", var.getName(), value).indent(indent + 2));
        }
        return new Assignment("FAILED");
    }

    private boolean isComplete(PriorityQueue<Variable> assignment) {
        Iterator<Variable> vars = variables.iterator();
        while(vars.hasNext()) {
            Variable var = vars.next();
            if (!var.isAssigned()) {
                return false;
            }
        }
        return true;
    }

    private Variable getUnassignedVariable(PriorityQueue<Variable> vars) {
        // System.out.println(solution.toString());
        PriorityQueue<Variable> temp = new PriorityQueue<>();
        for (Variable var : vars) {
            temp.add(var);
        }

        Variable var;
        while (temp.peek() != null) {
            var = temp.poll();
            if (!var.isAssigned()) {
                return var;
            }
            temp.remove(var);
        }
        return null;
    }

    public PriorityQueue<Variable> getVariables() {
        return variables;
    }

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

    public String toString() {
        return board.asString(true);
    }
}
