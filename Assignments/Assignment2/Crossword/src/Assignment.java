
public class Assignment {
    private int width;
    private int height;
    private LetterBox[] values;

    public Assignment(int width, int height) {
        this.width = width;
        this.height = height;
        values = new LetterBox[width * height];
        for (int i = 0; i < values.length; i++) {
            values[i] = new LetterBox();
        }
    }

    public Assignment(String value) {
        width = 1;
        height = 1;
        LetterBox box = new LetterBox(value);
        values = new LetterBox[]{box};
    }

    public boolean isConsistent(Variable var, String value) {
        int col = var.getCol();
        int row = var.getRow();
        int index = 0;

        if (var.isAcross()) {
            for (int x = col; x < var.getLength(); x++) {
                String currentVal = getElementAt(x, row).getValue();
                if (!(currentVal.equals("_") || currentVal.equals(value.charAt(index) + ""))) {
                    return false;
                }
                index++;
            }
        }
        else {
            for (int y = row; y < var.getLength(); y++) {
                String currentVal = getElementAt(col, y).getValue();
                if (!(currentVal.equals("_") || currentVal.equals(value.charAt(index) + ""))) {
                    return false;
                }
                index++;
            }
        }
        return true;
    }

    public void setAssignment(Variable var, String value) {
        int col = var.getCol();
        int row = var.getRow();
        int index = 0;

        if (var.isAcross()) {
            for (int x = col; x < var.getLength(); x++) {
                LetterBox box = getElementAt(x, row);
                box.incrementCount();
                box.setValue(value.charAt(index) + "");
                index++;
            }
        }
        else {
            for (int y = row; y < var.getLength(); y++) {
                LetterBox box = getElementAt(col, y);
                box.incrementCount();
                box.setValue(value.charAt(index) + "");
                index++;
            }
        }
        var.setAssignment(value);
    }

    public void undoAssignment(Variable var) {
        int col = var.getCol();
        int row = var.getRow();

        if (var.isAcross()) {
            for (int x = col; x < var.getLength(); x++) {
                LetterBox box = getElementAt(x, row);
                box.decrementCount();
                if (box.getCount() == 0) {
                    box.setValue("_");
                }
            }
        }
        else {
            for (int y = row; y < var.getLength(); y++) {
                LetterBox box = getElementAt(col, y);
                box.decrementCount();
                if (box.getCount() == 0) {
                    box.setValue("_");
                }
            }
        }
        var.setAssignment(null);
    }

    public LetterBox getElementAt(int col, int row) {
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
        StringBuilder b = new StringBuilder();

        for(int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                b.append(getElementAt(col, row));
                b.append(" ");
            }
            b.append("\n");
        }
        return b.toString();
    }
}
