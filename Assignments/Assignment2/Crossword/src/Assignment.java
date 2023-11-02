/*
 * Tanner Turba
 * November 1, 2023
 * CS 552 - Artificial Intelligence - Assignment 2
 * 
 * This class describes the grid of data that makes the cross-
 * word puzzle. This is responsible for storing and representing 
 * the solution of the puzzle.
 */
public class Assignment {
    private int width;
    private int height;
    public Cell[] values;

    /**
     * Creates a new assignment grid based on dimensions
     * @param width width of grid
     * @param height height of grid
     */
    public Assignment(int width, int height) {
        this.width = width;
        this.height = height;
        values = new Cell[width * height];

        // Initialize the Cells at each coordinate.
        for (int i = 0; i < values.length; i++) {
            int x = i % width;
            int y = i / width;
            values[i] = new Cell(x, y);
        }
    }

    /**
     * Create an assignment that carries a single value, used to report failures.
     * @param value the value of the assignment.
     */
    public Assignment(String value) {
        width = 1;
        height = 1;
        Cell cell = new Cell(value);
        values = new Cell[]{cell};
    }
    
    /**
     * Gets the Cell located at the specified coordinate
     * @param col the column the Cell is in.
     * @param row the row the Cell is in.
     * @return the desired Cell.
     */
    public Cell getElementAt(int col, int row) {
        return values[(row * width) + col];
    }

    /**
     * Determines if the Cell at the specfied location marks the beginning of a horizontal variable.
     * @param col the column that the Cell is in.
     * @param row the row that the Cell is in.
     * @return true if the Cell is the beginning of a horizontal variable.
     */
    public boolean isAcross(int col, int row) {
        int index = (row * width) + col - 1;
        return index == -1 || index % width == width - 1 || values[index].getValue().equals("#");
    }

    /**
     * Determines if the Cell at the specfied location marks the beginning of a vertical variable.
     * @param col the column that the Cell is in.
     * @param row the row that the Cell is in.
     * @return true if the Cell is the beginning of a vertical variable.
     */
    public boolean isDown(int col, int row) {
        int index = ((row - 1) * width) + col;
        return index < 0 || values[index].getValue().equals("#");
    }

    /**
     * @return String return the assignment
     */
    public String toString() {
        return asString(false);
    }

    /**
     * Creates a String representation of the Assignment
     * @param printPoundSigns True if '#' in empty cells are desired.
     * @return a String representation of the Assignment
     */
    public String asString(boolean printPoundSigns) {
        StringBuilder b = new StringBuilder();
        
        for(int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (!getElementAt(col, row).toString().equals("#")) {
                    b.append(getElementAt(col, row));
                }
                else if (printPoundSigns) {
                    b.append("#");
                }
                else {
                    b.append(" ");
                }
                b.append(" ");
            }
            b.append("\n");
        }
        return b.toString();
    }
}
