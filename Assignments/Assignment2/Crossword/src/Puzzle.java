import java.io.*;
import java.util.*;

public class Puzzle {
    private int width = 0;
    private int height = 0;
    private Assignment board;
    private Assignment solution;
    private PriorityQueue<Variable> variables = new PriorityQueue<>();

    public Puzzle(File file, Data dictionary, VarOrdering orderingHeuristic) {
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
                            variables.add(new Variable(element + "-a", col, row, length, true, orderingHeuristic, solution));
                        }
                        if (board.isDown(col, row)) {
                            int length = calcLength(col, row, false);
                            variables.add(new Variable(element + "-d", col, row, length, false, orderingHeuristic, solution));
                        }
                    }
                }
            }

            // Add dictionary words of the same length to the domain of every corresponding variable.
            Iterator<Variable> vars = variables.iterator();
            while(vars.hasNext()) {
                Variable var = vars.next();
                for (String word : dictionary) {
                    if (var.assignment.length == word.length()) {
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

    private Assignment backTrackingSearch(PriorityQueue<Variable> csp, Assignment assignment) {
        if (isComplete(csp)) {
            return assignment;
        }
        Variable var = getUnassignedVariable(csp);
        for (String value : var.domain) {
            if (assignment.isConsistent(var, value)) {
                var.setAssignment(value);
                //ConstrainPropegation?
                Assignment result = backTrackingSearch(csp, assignment);
                if (!result.getElementAt(0, 0).getValue().equals("FAILURE")) {
                    return result;
                }
                var.undoAssignment();
            }
        }
        return new Assignment("FAILURE");
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
        PriorityQueue<Variable> temp = new PriorityQueue<>(vars);
        Variable var;

        while (temp.peek() != null) {
            var = temp.poll();
            if (!var.isAssigned()) {
                return var;
            }
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
        StringBuilder b = new StringBuilder("Board:\n");
        b.append(board.toString());
        return b.toString();
    }
}
