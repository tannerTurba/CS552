/*
 * Tanner Turba
 * November 1, 2023
 * CS 552 - Artificial Intelligence - Assignment 2
 * 
 * This class describes what a Variable is in a CSP problem and
 * its interations with other Variables. Each variable consists of 
 * Cells from the assignment grid, which makes assigning those Cells
 * more managable. Each variable also contains a map of other Variables 
 * and the index in which that variable intersects itself.
 */
import java.util.*;

public class Variable {
    private String name;
    private boolean isAcross;
    public Cell[] assignment;
    public ArrayList<String> domain = new ArrayList<>();
    private boolean isAssigned = false;
    public Map<Integer, Variable> intersections = new HashMap<>();
    
    /**
     * Constructs a Variable
     * @param name The name of the variable
     * @param col the col its located in
     * @param row the row its located in
     * @param length the character length of the value this Variable represents
     * @param isAcross true if the variable runs horizontally in the puzzle
     * @param board the assignment group that this variable belongs to
     */
    public Variable(String name, int col, int row, int length, boolean isAcross, Assignment board) {
        this.name = name;
        this.assignment = new Cell[length];
        this.isAcross = isAcross;

        // Assign pre-existing cells to the Variable, based on its location and alignment.
        int index = 0;
        if (isAcross) {
            for (int x = col; x < col + length; x++) {
                assignment[index] = board.getElementAt(x, row);
                index++;
            }
        }
        else {
            for (int y = row; y < row + length; y++) {
                assignment[index] = board.getElementAt(col, y);
                index++;
            }
        }
    }

    /**
     * Determines if a Variable contains a Cell based on its coordinate location
     * @param x The x-coordinate of the cell
     * @param y The y-coordinate of the cell
     * @return true if the Variable contains the cell at (x,y)
     */
    public boolean containsCell(int x, int y) {
        int row = assignment[0].getY();
        int col = assignment[0].getX();
        if (isAcross) {
            return y == row && col <= x && x <= (col + assignment.length);
        }
        else {
            return x == col && row <= y && y <= (row + assignment.length);
        }
    }

    /**
     * Determines the index in which a Cell located at (x,y) lives within the Variable representation.
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @return The index in the variable you can find the cell at (x,y)
     */
    public int getCellIndex(int x, int y) {
        int row = assignment[0].getY();
        int col = assignment[0].getX();
        if (isAcross) {
            return x - col;
        }
        else {
            return y - row;
        }
    }

    /**
     * @return String return the assignment
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Cell cell : assignment) {
            sb.append(cell);
        }
        return sb.toString();
    }

    /**
     * @return True if the Variable is already assigned
     */
    public boolean isAssigned() {
        return isAssigned;
    }

    /**
     * Removes the assigned value from the Variable.
     */
    public void undoAssignment() {
        isAssigned = false;
        for (int i = 0; i < assignment.length; i++) {
            assignment[i].decrementCount();

            // Only reset a cell's value if it doesn't have multiple assignments.
            if (assignment[i].getCount() == 0) {
                assignment[i].setValue("_");
            }
        }
    }

    /**
     * @param assignment the assignment to set
     */
    public boolean setAssignment(String assignment) {
        isAssigned = true;
        if (assignment.length() == this.assignment.length) {
            for (int i = 0; i < assignment.length(); i++) {
                this.assignment[i].setValue(assignment.charAt(i) + "");
                this.assignment[i].incrementCount();
            }
            return true;
        }
        return false;
    }

    /**
     * @return boolean return the isAcross
     */
    public boolean isAcross() {
        return isAcross;
    }

    /**
     * @return The name of the Variable
     */
    public String getName() {
        return name;
    }

    /**
     * Determines the consistency of the Variable given the current assignment and the value to assign
     * @param value the value to check
     * @return true if the value is consistent for the Variable with the current assignment 
     */
    public boolean isConsistent(String value) {
        int index = 0;

        if (Config.isLimitedForwardChecking) {
            boolean returnEarly = true;

            for (int intersectionPoint : intersections.keySet()) {
                int x = assignment[intersectionPoint].getX();
                int y = assignment[intersectionPoint].getY();

                //Check with other variable
                Variable otherVar = intersections.get(intersectionPoint);
                int otherPoint = otherVar.getCellIndex(x, y);

                // For every variable that intersects with this Variable.
                for (String otherVal : otherVar.domain) {
                    // Check that the value in otherVar.domain that contains the same letter at the intersecting index as var.value
                    if (value.charAt(intersectionPoint) == otherVal.charAt(otherPoint)) {
                        returnEarly = false;
                        break;
                    }
                    else {
                        returnEarly = true;
                    }
                }
            }
            // Return failure when there isn't a consistent value at intersections.
            if (returnEarly) {
                return false;
            }
        }

        // Check that the variable has unassigned vals or that the potential value works with existing assignments.
        for (Cell cell : assignment) {
            if (!(cell.getValue().equals("_") || cell.getValue().equals(value.charAt(index) + ""))) {
                return false;
            }
            index++;
        }
        return true;
    }
}
