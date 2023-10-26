import java.io.*;
import java.util.*;

public class Puzzle {
    private int width = 0;
    private int height = 0;
    private String[] board;
    private String[] solution;
    private Map<String, Variable> variables = new Hashtable<>();

    public Puzzle(File file, Data dictionary) {
        try {
            Scanner scanner = new Scanner(file);
            width = scanner.nextInt();
            height = scanner.nextInt();
            board = new String[width * height];
            solution = new String[width * height];
            
            // Set the 'board,' which is an in-memory representation of the input file.
            for(int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    String element = scanner.next();
                    setBoardElementAt(col, row, element);
                    if (element.equals("#")) {
                        setElementAt(solution, col, row, "#");
                    }
                    else {
                        setElementAt(solution, col, row, "_");
                    }
                }
                scanner.nextLine();
            }
            scanner.close();

            // Once the board is set, find every possible variable that needs to be completed.
            for(int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    char element = getBoardElementAt(col, row).charAt(0);
                    if (Character.isDigit(element)) {
                        if (isAcross(col, row)) {
                            int length = calcLength(col, row, true);
                            variables.put(element + "-a", new Variable(col, row, length, true));
                        }
                        if (isDown(col, row)) {
                            int length = calcLength(col, row, false);
                            variables.put(element + "-d", new Variable(col, row, length, false));
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

    public String[] backTrackingSearch() {
        return backTrackingSearch(variables, solution);
    }

    private String[] backTrackingSearch(Map<String, Variable> csp, String[] assignment) {
        if (isComplete(csp)) {
            return assignment;
        }
        Variable var = getUnassignedVariable(csp);
        for (String value : var.domain) {
            if (isConsistent(var, value, assignment)) {
                setAssignment(var, value, assignment);
                //ConstrainPropegation?
                String[] result = backTrackingSearch(csp, assignment);
                if (!result[0].equals("FAILURE")) {
                    return result;
                }
                undoAssignment(var, assignment);
            }
        }
        return new String[]{"FAILURE"};
    }

    private boolean isConsistent(Variable var, String value, String[] assignment) {
        int col = var.getCol();
        int row = var.getRow();
        int index = 0;

        if (var.isAcross()) {
            for (int x = col; x < var.getLength(); x++) {
                String currentVal = getElementAt(assignment, x, row);
                if (!(currentVal.equals("_") || currentVal.equals(value.charAt(index) + ""))) {
                    return false;
                }
                index++;
            }
        }
        else {
            for (int y = row; y < var.getLength(); y++) {
                String currentVal = getElementAt(assignment, col, y);
                char test = value.charAt(index);
                if (!(currentVal.equals("_") || currentVal.equals(value.charAt(index) + ""))) {
                    return false;
                }
                index++;
            }
        }
        return true;
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

    //#region GettersSetters
    private void setAssignment(Variable var, String value, String[] assignment) {
        int col = var.getCol();
        int row = var.getRow();
        int index = 0;

        if (var.isAcross()) {
            for (int x = col; x < var.getLength(); x++) {
                setElementAt(assignment, x, row, value.charAt(index) + "");
                index++;
            }
        }
        else {
            for (int y = row; y < var.getLength(); y++) {
                setElementAt(assignment, col, y, value.charAt(index) + "");
                index++;
            }
        }
        var.setAssignment(value);
    }

    //TODO: removes assignments that should stay, so find a way to represent double assignments
    private void undoAssignment(Variable var, String[] assignment) {
        int col = var.getCol();
        int row = var.getRow();

        if (var.isAcross()) {
            for (int x = col; x < var.getLength(); x++) {
                setElementAt(assignment, x, row, "_");
            }
        }
        else {
            for (int y = row; y < var.getLength(); y++) {
                setElementAt(assignment, col, y, "_");
            }
        }
        var.setAssignment(null);
    }

    private String getBoardElementAt(int col, int row) {
        return board[(row * width) + col];
    }

    private void setElementAt(String[] board, int col, int row, String s) {
        board[(row * width) + col] = s;
    }

    private String getElementAt(String[] board, int col, int row) {
        return board[(row * width) + col];
    }

    private void setBoardElementAt(int col, int row, String s) {
        board[(row * width) + col] = s;
    }

    public Map<String, Variable> getVariables() {
        return variables;
    }
    //#endregion

    private boolean isAcross(int col, int row) {
        int index = (row * width) + col - 1;
        return index == -1 || index % width == width - 1 || board[index].equals("#");
    }

    private boolean isDown(int col, int row) {
        int index = ((row - 1) * width) + col;
        return index < 0 || board[index].equals("#");
    }

    private int calcLength(int col, int row, boolean isAcross) {
        int count = 0;
        while (!getBoardElementAt(col, row).equals("#")) {
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
        StringBuilder s = new StringBuilder("Solution:\n");

        for(int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                b.append(getBoardElementAt(col, row));
                b.append(" ");
                s.append(getElementAt(solution, col, row));
                s.append(" ");
            }
            b.append("\n");
            s.append("\n");
        }

        b.append("\n" + s.toString());
        return b.toString();
    }
}
