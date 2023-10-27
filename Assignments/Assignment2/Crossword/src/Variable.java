import java.util.*;

public class Variable implements Comparable<Variable> {
    private String name;
    private boolean isAcross;
    private VarOrdering orderingHeuristic;
    public LetterBox[] assignment;
    public ArrayList<String> domain = new ArrayList<>();
    private boolean isAssigned = false;
    
    public Variable(String name, int col, int row, int length, boolean isAcross, VarOrdering orderingHeuristic, Assignment board) {
        this.name = name;
        this.assignment = new LetterBox[length];
        this.isAcross = isAcross;
        this.orderingHeuristic = orderingHeuristic;

        int index = 0;
        if (isAcross) {
            for (int x = col; x < length; x++) {
                assignment[index] = board.getElementAt(x, row);
                index++;
            }
        }
        else {
            for (int y = row; y < length; y++) {
                assignment[index] = board.getElementAt(col, y);
                index++;
            }
        }
    }

    /**
     * @return String return the assignment
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (LetterBox box : assignment) {
            sb.append(box);
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

    @Override
    public int compareTo(Variable that) {
        int thisVarNum = Integer.parseInt(this.getName().split("-")[0]);
        int thatVarNum = Integer.parseInt(that.getName().split("-")[0]);
        
        if (orderingHeuristic == VarOrdering.MINIMUM_REMAINING_VALUES) {
            if (this.domain.size() < that.domain.size()) {
                return 1;
            }
            else if (this.domain.size() > that.domain.size()) {
                return -1;
            }
            return 0;
        }
        else if (orderingHeuristic == VarOrdering.MOST_CONSTRAINING_VARIABLE) {
            return 1;
        }
        else if (orderingHeuristic == VarOrdering.HYBRID) {
            return 1;
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
                    return 1;
                }
                return -1;
            }
        }
    }
}
