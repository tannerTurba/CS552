import java.io.*;
import java.util.*;

public class CityMap extends Hashtable<String, CityNode> {
    private File inputFile;
    private String goal;
    private Map<String, double[]> coordinates = new Hashtable<>();
    private StringBuilder sBuilder = new StringBuilder();
    private int nodesGenerated, nodesInFrontier;
    private long startTime = 0;
    private long stopTime = 0;
    private Strategy strategy;
    private Heuristic heuristic;
    private int verbosity = 0;

    public CityMap(File inputFile, String goal, Strategy strategy, Heuristic heuristic, int verbosity) {
        super();
        this.inputFile = inputFile;
        this.goal = goal;
        this.strategy = strategy;
        this.heuristic = heuristic;
        this.verbosity = verbosity;
        parseInput();
    }

    private void parseInput() {
        boolean mapIsCompleted = false;
        boolean isFirstLine = true;
        Scanner inputReader;

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
                    put(city.getCityName(), city);
                }
                else if(!isFirstLine && !mapIsCompleted && isComment(currentLine)) {
                    mapIsCompleted = true;
                }
                else if(mapIsCompleted && !isComment(currentLine)) {
                    // Read distance between cities
                    readDistance(currentLine);
                }
            }
        }
        catch(FileNotFoundException e) {
            System.out.println("File not found!");
            e.printStackTrace();
        }
    }

    private boolean isComment(String line) {
        return line.charAt(0) == '#';
    }

    private CityNode readCity(String line) {
        Scanner lineReader = new Scanner(line);
        lineReader.useDelimiter(",");
        String name = lineReader.next().trim();
        double latitude = Double.parseDouble(lineReader.next().trim());
        double longitude = Double.parseDouble(lineReader.next().trim());
        lineReader.close();
        coordinates.put(name, new double[]{latitude, longitude});

        return new CityNode(name);
    }

    private void readDistance(String line) {
        Scanner lineReader = new Scanner(line);
        lineReader.useDelimiter(",");
        String name1 = lineReader.next().trim();
        String name2 = lineReader.next().trim();
        String distance = lineReader.next().trim();
        lineReader.close();
        
        get(name1).addDistance(name2, distance);
        get(name2).addDistance(name1, distance);
    }

    private void calcHeuristics(CityNode currentNode) {
        double g, h;
        h = getHeuristic(currentNode.getCityName());
        g = currentNode.getPathCost();
        if(strategy == Strategy.GREEDY) {
            currentNode.setF(h);
        }
        else if(strategy == Strategy.A_STAR) {
            currentNode.setF(h + g);
        }
        else {
            currentNode.setF(currentNode.getPathCost());
        }
        currentNode.setG(g);
        currentNode.setH(h);
    }

    private double getHeuristic(String city1) {
        double[] coor1 = coordinates.get(city1);
        double[] coor2 = coordinates.get(goal);

        if (heuristic == Heuristic.EUCLIDEAN) {
            return euclidean(coor1, coor2);
        }
        else {
            return haversine(coor1, coor2);
        }
    }

    private double haversine(double[] city1, double[] city2) {
        double lon1 = Math.toRadians(city1[1]);
        double lon2 = Math.toRadians(city2[1]);
        double lat1 = Math.toRadians(city1[0]);
        double lat2 = Math.toRadians(city2[0]);

        double lon = lon2 - lon1;
        double lat = lat2 - lat1;
        double a = Math.pow(Math.sin(lat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(lon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 3958.8 * c;
    }

    private double euclidean(double[] city1, double[] city2) {
        double lon1 = city1[1];
        double lon2 = city2[1];
        double lat1 = city1[0];
        double lat2 = city2[0];

        double lon = lon2 - lon1;
        double lat = lat2 - lat1;
        return Math.sqrt(Math.pow(lon, 2) + Math.pow(lat, 2));
    }

    private Collection<CityNode> expand(CityNode city, String goal) {
        Collection<CityNode> nodesToReturn = null;
        if (strategy == Strategy.GREEDY) {
            nodesToReturn = new PriorityQueue<>();
        }
        else {
            nodesToReturn = new ArrayList<>();
        }

        for (String child : city.getDistances().keySet()) {
            CityNode childNode = get(child);
            Double cost = city.getPathCost() + city.getDistances().get(childNode.getCityName());
            String pathActions = city.getActions() + " -> " + childNode.getCityName();
            CityNode newNode = new CityNode(childNode, pathActions, cost);
            calcHeuristics(newNode);
            nodesToReturn.add(newNode);
        }
        return nodesToReturn;
    }

    public CityNode search(String initialState, String goalState, boolean reachedIsUsed) {
        startStopwatch();
        CityNode root = get(initialState);
        calcHeuristics(root);
        Frontier<CityNode> frontier = new Frontier<>(strategy, root);
        Map<String, CityNode> reached = null;
        if (strategy != Strategy.DEPTH && reachedIsUsed) {
            reached = new HashMap<>();
            reached.put(root.getCityName(), root);
        }

        CityNode node;
        while (!frontier.isEmpty()) {
            node = frontier.poll();
            if (node.isCycle() && verbosity == 3) {
                node.setEvalAction("    NOT Adding");
                sBuilder.append(node.nodeSummary());
            }
            else {
                node.setEvalAction("  Expanding");
                sBuilder.append(node.nodeSummary());
            }
            
            nodesGenerated++;
            if (node.getCityName().equals(goalState) && strategy != Strategy.BREADTH) {
                nodesInFrontier = frontier.size();
                stopStopwatch();
                node.setEvalAction("* Goal found");
                return node;
            }
            else if (strategy == Strategy.DEPTH && !node.isCycle()) {
                for (CityNode child : expand(node, goalState)) {
                    frontier.add(child);
                }
            }
            else if (strategy != Strategy.DEPTH) {
                for (CityNode child : expand(node, goalState)) {
                    String state = child.getCityName();
                    if (strategy == Strategy.BREADTH) {
                        if (state.equals(goalState)) {
                            nodesInFrontier = frontier.size();
                            stopStopwatch();
                            child.setEvalAction("* Goal found");
                            return child;
                        }
                        else if (reachedIsUsed && !reached.containsKey(state)) {
                            reached.put(state, child);
                            frontier.add(child);
                            if (verbosity == 3) {
                                child.setEvalAction("    Adding");
                                sBuilder.append(child.nodeSummary());
                            }
                        }
                    }
                    else {
                        if (reachedIsUsed && (reached.get(state) == null || child.getPathCost() < reached.get(state).getPathCost())) {
                            reached.put(state, child);
                            frontier.add(child);
                            if (verbosity == 3) {
                                child.setEvalAction("    Adding");
                                sBuilder.append(child.nodeSummary());
                            }
                        }
                        else if (verbosity == 3) {
                            child.setEvalAction("    NOT Adding");
                            sBuilder.append(child.nodeSummary());
                        }
                    }
                }
            }
        }
        stopStopwatch();
        return new CityNode("NO PATH");
    }

    public String getGeneratedNodes() {
        return sBuilder.toString();
    }

    public int getNumNodesGenerated() {
        return nodesGenerated;
    }

    public int getNumNodesInFrontier() {
        return nodesInFrontier;
    }

    private void startStopwatch() {
        startTime = System.currentTimeMillis();
    }

    private void stopStopwatch() {
        stopTime = System.currentTimeMillis();
    }

    public long getElapsedTime() {
        return stopTime - startTime;
    }
}
