import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class Puzzle {
    private int width = 0;
    private int height = 0;
    private Assignment board;
    private Assignment solution;
    private ArrayList<Variable> variables = new ArrayList<>();
    public int numOfCalls = 0;
    public int numOfIntersections = 0;
    public StringBuilder sb = new StringBuilder();

    public Puzzle(File file, Data dictionary) {
        try {
            Scanner scanner = new Scanner(file);
            width = scanner.nextInt();
            height = scanner.nextInt();
            board = new Assignment(width, height);
            solution = new Assignment(width, height);
            
            // Set the 'board,' which is an in-memory representation of the input file.
            for(int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    String element = scanner.next();
                    if (!element.equals("#")) {
                        board.getElementAt(col, row).setValue(element);
                        solution.getElementAt(col, row).setValue("_");
                    }
                }
                scanner.nextLine();
            }
            scanner.close();

            // Once the board is set, find every possible variable that needs to be completed.
            for(int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    String element = board.getElementAt(col, row).getValue();
                    if (Character.isDigit(element.charAt(0))) {
                        Variable var = null;
                        if (board.isAcross(col, row)) {
                            int length = calcLength(col, row, true);
                            var = new Variable(element + "a", col, row, length, true, solution);
                            for (String word : dictionary) {
                                if (var.assignment.length == word.length()) {
                                    var.domain.add(word);
                                }
                            }
                            variables.add(var);
                        }
                        if (board.isDown(col, row)) {
                            int length = calcLength(col, row, false);
                            var = new Variable(element + "d", col, row, length, false, solution);
                            for (String word : dictionary) {
                                if (var.assignment.length == word.length()) {
                                    var.domain.add(word);
                                }
                            }
                            variables.add(var);
                        }
                    }
                }
            }

            // Find intersections for forward checking
            for (Variable var : variables) {
                ArrayList<Variable> temp = new ArrayList<>(variables);
                temp.remove(var);
                for (Variable v : temp) {
                    for (int i = 0; i < var.assignment.length; i++) {
                        if (v.containsCell(var.assignment[i].getX(), var.assignment[i].getY())) {
                            numOfIntersections++;
                            var.intersections.put(i, v);
                            break;
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Assignment backTrackingSearch() {
        return backTrackingSearch(variables, solution, 0);
    }

    private Assignment backTrackingSearch(ArrayList<Variable> csp, Assignment assignment, int indent) {
        numOfCalls++;
        if (Config.isVerbosity2) {
            sb.append("Backtrack:\n".indent(indent));
        }
        if (isComplete(csp)) {
            if (Config.isVerbosity2) {
                sb.append("Assignment is complete!\n".indent(indent + 2));
            }
            return assignment;
        }

        Variable var = getUnassignedVariable(csp);
        sortDomainVals(var);
        if (Config.isVerbosity2) {
            sb.append(String.format("Trying values for X%s\n", var.getName()).indent(indent + 2));
        }

        for (String value : var.domain) {
            if (var.isConsistent(value)) {
                if (Config.isVerbosity2) {
                    sb.append(String.format("Assignment { X%s = %s } is consistent\n", var.getName(), value).indent(indent + 2));
                }
                var.setAssignment(value);
                //ConstrainPropegation?
                Assignment result = backTrackingSearch(csp, assignment, indent + 2);
                if (!result.getElementAt(0, 0).getValue().equals("FAILED")) {
                    return result;
                }
                var.undoAssignment();
            }
            if (Config.isVerbosity2) {
                sb.append(String.format("Assignment { X%s = %s } is inconsistent\n", var.getName(), value).indent(indent + 2));
            }
        }
        return new Assignment("FAILED");
    }

    private boolean isComplete(ArrayList<Variable> assignment) {
        Iterator<Variable> vars = variables.iterator();
        while(vars.hasNext()) {
            Variable var = vars.next();
            if (!var.isAssigned()) {
                return false;
            }
        }
        return true;
    }

    private void sortDomainVals(Variable var) {
        if (Config.valueOrder == IterationType.LEAST_CONSTRAINING_VALUE) {
            var.domain.sort(new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    int s1Conflicts = 0;
                    int s2Conflicts = 0;

                    // For each intersection point
                    for (Entry<Integer, Variable> intersection : var.intersections.entrySet()) {
                        // Get the index value of the intersection for this variable and that variable
                        int thisIndex = intersection.getKey();
                        Cell cell = var.assignment[thisIndex];
                        int thatIndex = intersection.getValue().getCellIndex(cell.getX(), cell.getY());

                        // If the cell is still unassigned...
                        if (cell.getCount() == 0) {
                            // Loop through every possible value for that variable
                            for (String thatVal : intersection.getValue().domain) {
                                // Increment number of conflicts for corresponding value if char vals do not match at intersecting indices. 
                                if (s1.charAt(thisIndex) != thatVal.charAt(thatIndex)) {
                                    s1Conflicts++;
                                }
                                if (s2.charAt(thisIndex) != thatVal.charAt(thatIndex)) {
                                    s2Conflicts++;
                                }
                            }
                        }
                    }

                    // Return value based on total number of coflicts.
                    if (s1Conflicts < s2Conflicts) {
                        return 1;
                    }
                    else if(s1Conflicts > s2Conflicts) {
                        return -1;
                    }
                    else {
                        return 0;
                    }
                }
            });
        }
        else {
            var.domain.sort(Comparator.comparing(s -> s.toLowerCase()));
        }
    }

    private Variable getUnassignedVariable(ArrayList<Variable> vars) {
        ArrayList<Variable> temp = new ArrayList<>(vars);
        temp.removeIf(var -> (var.isAssigned() == true));
        temp.sort(new Comparator<Variable>() {
            @Override
            public int compare(Variable v1, Variable v2) {
                if (Config.orderingHeuristic == VarOrdering.MINIMUM_REMAINING_VALUES) {
                    int v1Consistencies = 0;
                    int v2Consistencies = 0;
                    if (!v1.isAssigned()) {
                        for (String str : v1.domain) {
                            if (v1.isConsistent(str)) {
                                v1Consistencies++;
                            }
                        }
                    }
                    else {
                        v2Consistencies = Integer.MAX_VALUE;
                    }

                    if (!v2.isAssigned()) {
                        for (String str : v2.domain) {
                            if (v2.isConsistent(str)) {
                                v2Consistencies++;
                            }
                        }
                    }
                    else {
                        v2Consistencies = Integer.MAX_VALUE;
                    }
        
                    if (v1Consistencies < v2Consistencies) {
                        return -1;
                    }
                    else if (v1Consistencies > v2Consistencies) {
                        return 1;
                    }
                    else {
                        return 0;
                    }
                }
                else if (Config.orderingHeuristic == VarOrdering.MOST_CONSTRAINING_VARIABLE) {
                    int v1Intersections = 0;
                    int v2Intersections = 0;

                    for (Variable var : v1.intersections.values()) {
                        if (!var.isAssigned()) {
                            v1Intersections++;
                        }
                    }
                    for (Variable var : v2.intersections.values()) {
                        if (!var.isAssigned()) {
                            v2Intersections++;
                        }
                    }

                    if (v1Intersections < v2Intersections) {
                        return -1;
                    }
                    else if (v1Intersections > v2Intersections) {
                        return 1;
                    }
                    else {
                        return 0;
                    }
                }
                else if (Config.orderingHeuristic == VarOrdering.HYBRID) {
                    int v1Consistencies = 0;
                    int v2Consistencies = 0;
                    if (!v1.isAssigned()) {
                        for (String str : v1.domain) {
                            if (v1.isConsistent(str)) {
                                v1Consistencies++;
                            }
                        }
                    }
                    else {
                        v2Consistencies = Integer.MAX_VALUE;
                    }

                    if (!v2.isAssigned()) {
                        for (String str : v2.domain) {
                            if (v2.isConsistent(str)) {
                                v2Consistencies++;
                            }
                        }
                    }
                    else {
                        v2Consistencies = Integer.MAX_VALUE;
                    }
        
                    if (v1Consistencies < v2Consistencies) {
                        return -1;
                    }
                    else if (v1Consistencies > v2Consistencies) {
                        return 1;
                    }
                    else {
                        int v1Intersections = 0;
                        int v2Intersections = 0;
                        for (Variable var : v1.intersections.values()) {
                            if (!var.isAssigned()) {
                                v1Intersections++;
                            }
                        }
                        for (Variable var : v2.intersections.values()) {
                            if (!var.isAssigned()) {
                                v2Intersections++;
                            }
                        }

                        if (v1Intersections < v2Intersections) {
                            return -1;
                        }
                        else if (v1Intersections > v2Intersections) {
                            return 1;
                        }
                        else {
                            return 0;
                        }
                    }
                }
                else {
                    int v1VarNum = Integer.parseInt(v1.getName().replaceAll("(d|a)", ""));
                    int v2VarNum = Integer.parseInt(v2.getName().replaceAll("(d|a)", ""));
        
                    if (v1VarNum > v2VarNum) {
                        return 1;
                    }
                    else if (v1VarNum < v2VarNum) {
                        return -1;
                    }
                    else {
                        if (v1.isAcross()) {
                            return -1;
                        }
                        return 1;
                    }
                }
            }
            
        });
        return temp.get(0);
    }

    public ArrayList<Variable> getVariables() {
        return variables;
    }

    private int calcLength(int col, int row, boolean isAcross) {
        int count = 0;
        while (!board.getElementAt(col, row).getValue().equals("#")) {
            count++;
            if (isAcross) {
                col++;
                if (col >= width) {
                    return count;
                }
            }
            else {
                row++;
                if (row >= height) {
                    return count;
                }
            }
        }
        return count;
    }

    public String toString() {
        return board.asString(true);
    }
}
