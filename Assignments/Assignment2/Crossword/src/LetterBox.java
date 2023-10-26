public class LetterBox {
    private String value = "#";
    private int count = 0;

    public LetterBox() {
        
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
}
