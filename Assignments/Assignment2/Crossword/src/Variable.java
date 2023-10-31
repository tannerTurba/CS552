import java.util.*;

public class Variable implements Comparable<Variable> {
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
        // int col = assignment[0].getX();
        // int row = assignment[0].getY();
        int index = 0;

        if (Config.isLimitedForwardChecking) {
            boolean returnEarly = true;
            for (int intersectionPoint : intersections.keySet()) {
                int x = assignment[intersectionPoint].getX();
                int y = assignment[intersectionPoint].getY();

                // Variable otherVar = var.intersections.get(intersectionPoint);
                int otherPoint = getCellIndex(x, y);
                if (value.charAt(intersectionPoint) == value.charAt(otherPoint)) {
                    returnEarly = false;
                    break;
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

        // if (isAcross()) {
        //     for (int x = col; x < assignment.length; x++) {
        //         String currentVal = getElementAt(x, row).getValue();
        //         if (!(currentVal.equals("_") || currentVal.equals(value.charAt(index) + ""))) {
        //             return false;
        //         }
        //         index++;
        //     }
        // }
        // else {
        //     for (int y = row; y < assignment.length; y++) {
        //         String currentVal = getElementAt(col, y).getValue();
        //         if (!(currentVal.equals("_") || currentVal.equals(value.charAt(index) + ""))) {
        //             return false;
        //         }
        //         index++;
        //     }
        // }
        return true;
    }

    @Override
    public int compareTo(Variable that) {
        int thisVarNum = Integer.parseInt(this.getName().replaceAll("(d|a)", ""));
        int thatVarNum = Integer.parseInt(that.getName().replaceAll("(d|a)", ""));
        
        if (Config.orderingHeuristic == VarOrdering.MINIMUM_REMAINING_VALUES) {
            int theseConsistencies = 0;
            int thoseConsistencies = 0;
            for (String str : this.domain) {
                if (isConsistent(str)) {
                    theseConsistencies++;
                }
            }
            for (String str : that.domain) {
                if (that.isConsistent(str)) {
                    thoseConsistencies++;
                }
            }

            if (theseConsistencies > thoseConsistencies) {
                return 1;
            }
            else if (theseConsistencies < thoseConsistencies) {
                return -1;
            }
            else {
                return 0;
            }
        }
        else if (Config.orderingHeuristic == VarOrdering.MOST_CONSTRAINING_VARIABLE) {
            if (this.intersections.size() > that.intersections.size()) {
                return 1;
            }
            else if (this.intersections.size() < that.intersections.size()) {
                return -1;
            }
            else {
                return 0;
            }
        }
        else if (Config.orderingHeuristic == VarOrdering.HYBRID) {
            int theseConsistencies = 0;
            int thoseConsistencies = 0;
            for (String str : this.domain) {
                if (isConsistent(str)) {
                    theseConsistencies++;
                }
            }
            for (String str : that.domain) {
                if (that.isConsistent(str)) {
                    thoseConsistencies++;
                }
            }

            int theseHeuristics = theseConsistencies + this.intersections.size();
            int thoseHeuristics = thoseConsistencies + that.intersections.size();
            if (theseHeuristics > thoseHeuristics) {
                return 1;
            }
            else if (theseHeuristics < thoseHeuristics) {
                return -1;
            }
            else {
                return 0;
            }
        }
        else {
            if (thisVarNum > thatVarNum) {
                return 1;
            }
            else if (thisVarNum < thatVarNum) {
                return -1;
            }
            else {
                if (this.isAcross) {
                    return -1;
                }
                return 1;
            }
        }
    }
}
