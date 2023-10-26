import java.io.*;
import java.util.*;

public class Puzzle {
    private int width = 0;
    private int height = 0;
    private Assignment board;
    private Assignment solution;
    private Map<String, Variable> variables = new Hashtable<>();

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
                        if (board.isAcross(col, row)) {
                            int length = calcLength(col, row, true);
                            variables.put(element + "a", new Variable(col, row, length, true));
                        }
                        if (board.isDown(col, row)) {
                            int length = calcLength(col, row, false);
                            variables.put(element + "d", new Variable(col, row, length, false));
                        }
                    }
                }
            }

            // Add dictionary words of the same length to the domain of every corresponding variable.
            for (Variable var : variables.values()) {
                for (String word : dictionary) {
                    if (var.getLength() == word.length()) {
                        var.domain.add(word);
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Assignment backTrackingSearch() {
        return backTrackingSearch(variables, solution);
    }

    private Assignment backTrackingSearch(Map<String, Variable> csp, Assignment assignment) {
        if (isComplete(csp)) {
            return assignment;
        }
        Variable var = getUnassignedVariable(csp);
        for (String value : var.domain) {
            if (assignment.isConsistent(var, value)) {
                assignment.setAssignment(var, value);
                //ConstrainPropegation?
                Assignment result = backTrackingSearch(csp, assignment);
                if (!result.getElementAt(0, 0).getValue().equals("FAILURE")) {
                    return result;
                }
                assignment.undoAssignment(var);
            }
        }
        return new Assignment("FAILURE");
    }

    private boolean isComplete(Map<String, Variable> assignment) {
        for (Variable var : assignment.values()) {
            if (var.getAssignment() == null) {
                return false;
            }
        }
        return true;
    }

    private Variable getUnassignedVariable(Map<String, Variable> vars) {
        for (Variable var : vars.values()) {
            if (var.getAssignment() == null) {
                return var;
            }
        }
        return null;
    }

    public Map<String, Variable> getVariables() {
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
        StringBuilder b = new StringBuilder("Board:\n");
        b.append(board.toString());
        // b.append("\nSolution\n");
        // b.append(solution.toString());
        return b.toString();
    }
}
