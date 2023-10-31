
public class Assignment {
    private int width;
    private int height;
    public Cell[] values;

    public Assignment(int width, int height) {
        this.width = width;
        this.height = height;
        values = new Cell[width * height];
        for (int i = 0; i < values.length; i++) {
            int x = i % width;
            int y = i / width;
            values[i] = new Cell(x, y);
        }
    }

    public Assignment(String value) {
        width = 1;
        height = 1;
        Cell cell = new Cell(value);
        values = new Cell[]{cell};
    }
    
    public Cell getElementAt(int col, int row) {
        return values[(row * width) + col];
    }

    public boolean isAcross(int col, int row) {
        int index = (row * width) + col - 1;
        return index == -1 || index % width == width - 1 || values[index].getValue().equals("#");
    }

    public boolean isDown(int col, int row) {
        int index = ((row - 1) * width) + col;
        return index < 0 || values[index].getValue().equals("#");
    }

    public String toString() {
        return asString(false);
    }

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
