import java.util.*;

public class Variable {
    private String name;
    private boolean isAcross;
    public Cell[] assignment;
    public ArrayList<String> domain = new ArrayList<>();
    private boolean isAssigned = false;
    public Map<Integer, Variable> intersections = new HashMap<>();
    
    public Variable(String name, int col, int row, int length, boolean isAcross, Assignment board) {
        this.name = name;
        this.assignment = new Cell[length];
        this.isAcross = isAcross;

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

    public boolean isAssigned() {
        return isAssigned;
    }

    public void undoAssignment() {
        isAssigned = false;
        for (int i = 0; i < assignment.length; i++) {
            assignment[i].decrementCount();
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

    public String getName() {
        return name;
    }

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
                for (String otherVal : otherVar.domain) {
                    if (value.charAt(intersectionPoint) == otherVal.charAt(otherPoint)) {
                        returnEarly = false;
                        break;
                    }
                    else {
                        returnEarly = true;
                    }
                }
            }
            if (returnEarly) {
                return false;
            }
            //foreach variable, otherVar, intersecting with var
                //if there exists a value in otherVar.domain that contains the same letter at the intersecting index as var.value, break loop
            //return false
        }

        for (Cell cell : assignment) {
            if (!(cell.getValue().equals("_") || cell.getValue().equals(value.charAt(index) + ""))) {
                return false;
            }
            index++;
        }
        return true;
    }
}
