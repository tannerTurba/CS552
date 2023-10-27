import java.util.*;

public class Variable implements Comparable<Variable> {
    private String name;
    private int col;
    private int row;
    private int length;
    private boolean isAcross;
    private VarOrdering orderingHeuristic;
    private String assignment = null;
    public ArrayList<String> domain = new ArrayList<>();
    
    public Variable(String name, int x, int y, int length, boolean isAcross, VarOrdering orderingHeuristic) {
        this.name = name;
        this.col = x;
        this.row = y;
        this.length = length;
        this.isAcross = isAcross;
        this.orderingHeuristic = orderingHeuristic;
    }

    /**
     * @return int return the x
     */
    public int getCol() {
        return col;
    }

    /**
     * @param x the x to set
     */
    public void setCol(int x) {
        this.col = x;
    }

    /**
     * @return int return the y
     */
    public int getRow() {
        return row;
    }

    /**
     * @param y the y to set
     */
    public void setRow(int y) {
        this.row = y;
    }

    /**
     * @return int return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }


    /**
     * @return String return the assignment
     */
    public String getAssignment() {
        return assignment;
    }

    /**
     * @param assignment the assignment to set
     */
    public void setAssignment(String assignment) {
        this.assignment = assignment;
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
            return 1;
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
