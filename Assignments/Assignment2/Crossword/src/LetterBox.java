public class LetterBox {
    private String value = "#";
    private int count = 0;
    private int x;
    private int y;

    public LetterBox(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public LetterBox(String value) {
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

    public void incrementCount() {
        count++;
    }

    public void decrementCount() {
        count--;
    }

    public int getCount() {
        return count;
    }

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
