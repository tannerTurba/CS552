import java.util.*;

public class Variable {
    private int col;
    private int row;
    private int length;
    private boolean isAcross;
    private String assignment = null;
    public ArrayList<String> domain = new ArrayList<>();
    
    public Variable(int x, int y, int length, boolean isAcross) {
        this.col = x;
        this.row = y;
        this.length = length;
        this.isAcross = isAcross;
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

    // /**
    //  * @param isAcross the isAcross to set
    //  */
    // public void setIsAcross(boolean isAcross) {
    //     this.isAcross = isAcross;
    // }

}
