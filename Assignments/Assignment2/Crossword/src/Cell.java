public class Cell {
    private String value = "#";
    private int count = 0;
    private int x;
    private int y;

    /**
     * Constructor for a cell with a location.
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructor for a cell with a value, but no location
     * @param value the value the cell should hold
     */
    public Cell(String value) {
        this.value = value;
    }

    /**
     * @return String return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * increment the number of assignments using this cell
     */
    public void incrementCount() {
        count++;
    }

    /**
     * decrement the number of assignments using this cell
     */
    public void decrementCount() {
        count--;
    }

    /**
     * @return the number of assignments using this cell
     */
    public int getCount() {
        return count;
    }

    /**
     * @return the string representation
     */
    public String toString() {
        return value;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return int return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @return int return the y
     */
    public int getY() {
        return y;
    }

}
