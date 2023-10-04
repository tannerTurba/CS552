import java.io.*;
import java.util.*;

public class IOManager {
    private File inputFile;
    private File outputFile;
    private int numOfCities = 0;

    public IOManager(File inputFile) {
        this.inputFile = inputFile;
    }

    public IOManager(File inputFile, File outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public void level0(CityNode solution) {
        System.out.println(solution.actions);
        System.out.printf("Distance: %.1f\n\n", solution.pathCost);
        System.out.println("Total nodes generated      : " + Stats.getNumNodesGenerated());
        System.out.println("Nodes remaining on frontier: " + Stats.getNodesInFrontier());
    }

    private String getSearchProblem(CityNode solution) {
        String val = "* Reading data from [" + inputFile.getName() + "]\n";
        val += "* Number of cities: " + numOfCities + "\n";
        val += "* Searching for path from " + Stats.getStartState() + " to " + Stats.getEndState() + " using " + Stats.getSearchStrategy() + " Search\n";
        return val;
    }

    private String getSearchDetails(CityNode solution) {
        String val = "";
        if (!solution.actions.equals("NO PATH")) {
            val += "* Goal found  : " + nodeSummary(solution);
        }
        val += "* Search took " + Stats.getElapsedTime() + "ms\n";
        return val;
    }

    private String nodeSummary(CityNode node) {
        String lastState;
        if (!node.actions.contains("->")) {
            lastState = "null";
        }
        else {
            String[] actions = node.actions.split(" -> ");
            lastState = actions[actions.length-1];
        }
        return String.format("%-13s  (p-> %-11s) [f=%6.1f; g=%6.1f; h=%6.1f]\n", node.cityName, lastState, node.getF(), node.getG(), node.getH());
    }

    public void level1(CityNode solution) {
        String val = getSearchProblem(solution);
        val += getSearchDetails(solution);
        val += nodeSummary(solution);
        System.out.println(val);
        level0(solution);
    }

    public void level2(CityNode solution) {
        String val = getSearchProblem(solution);
        CityNode node;
        while (!Stats.nodesGenerated.isEmpty()) {
            node = Stats.nodesGenerated.poll();
            val += "  Expanding   : " + nodeSummary(node);
        }

        val += getSearchDetails(solution);
        System.out.println(val);
        level0(solution);
    }
    
    public CityMap parseInput() {
        boolean mapIsCompleted = false;
        boolean isFirstLine = true;
        Scanner inputReader;
        CityMap map = new CityMap();

        try {
            inputReader = new Scanner(inputFile);
            String currentLine;
            while (inputReader.hasNextLine()) {
                currentLine = inputReader.nextLine();
                if (!isComment(currentLine)) {
                    isFirstLine = false;
                }
                if(!mapIsCompleted && !isComment(currentLine)) {
                    // Read coordinates into CityNode
                    CityNode city = readCity(currentLine);
                    map.put(city.getCityName(), city);
                }
                else if(!isFirstLine && !mapIsCompleted && isComment(currentLine)) {
                    mapIsCompleted = true;
                }
                else if(mapIsCompleted && !isComment(currentLine)) {
                    // Read distance between cities
                    readDistance(map, currentLine);
                }
            }
        }
        catch(FileNotFoundException e) {
            System.out.println("File not found!");
            e.printStackTrace();
        }
        numOfCities = map.size();
        return map;
    }

    private boolean isComment(String line) {
        return line.charAt(0) == '#';
    }

    private CityNode readCity(String line) {
        Scanner lineReader = new Scanner(line);
        lineReader.useDelimiter(",");
        String name = lineReader.next().trim();
        String latitude = lineReader.next().trim();
        String longitude = lineReader.next().trim();
        lineReader.close();

        return new CityNode(name, latitude, longitude);
    }

    private void readDistance(Map<String, CityNode> map, String line) {
        Scanner lineReader = new Scanner(line);
        lineReader.useDelimiter(",");
        String name1 = lineReader.next().trim();
        String name2 = lineReader.next().trim();
        String distance = lineReader.next().trim();
        lineReader.close();
        
        map.get(name1).addDistance(name2, distance);
        map.get(name2).addDistance(name1, distance);
    }
}
