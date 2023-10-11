import java.io.*;
import java.util.*;

public class CityMap extends Hashtable<String, CityNode> {
    private File inputFile;
    private String goal;
    public Map<String, double[]> coordinates = new Hashtable<>();
    private Queue<String> generatedNodes = new LinkedList<>();
    private int nodesGenerated, nodesInFrontier;
    private long startTime = 0;
    private long stopTime = 0;
    private Strategy strategy;

    public CityMap(File inputFile, String goal, Strategy strategy) {
        super();
        this.inputFile = inputFile;
        this.goal = goal;
        this.strategy = strategy;
        parseInput();
    }

    private void calcHeuristics(CityNode currentNode) {
        double g, h;
        h = getHeuristic(currentNode.cityName);
        g = currentNode.pathCost;
        if(Navigator.getStrategy() == Strategy.GREEDY) {
            currentNode.setF(h);
        }
        else if(Navigator.getStrategy() == Strategy.A_STAR) {
            currentNode.setF(h + g);
        }
        else {
            currentNode.setF(currentNode.pathCost);
        }
        currentNode.setG(g);
        currentNode.setH(h);
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
        addCoordinates(name, new double[]{latitude, longitude});

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

    public void addCoordinates(String cityName, double[] coordi) {
        coordinates.put(cityName, coordi);
    }

    public double getLatitude(String city) {
        return coordinates.get(city)[0];
    }

    public double getLongitude(String city) {
        return coordinates.get(city)[1];
    }

    private double getHeuristic(String city1) {
        double[] coor1 = coordinates.get(city1);
        double[] coor2 = coordinates.get(goal);

        if (Navigator.getHeuristic() == Heuristic.EUCLIDEAN) {
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

    public Collection<CityNode> expand(CityNode city, String goal) {
        // PriorityQueue<CityNode> nodesToReturn = new PriorityQueue<>();
        // ArrayList<CityNode> nodesToReturn = new ArrayList<>();
        Collection<CityNode> nodesToReturn = null;
        if (strategy == Strategy.GREEDY) {
            nodesToReturn = new PriorityQueue<>();
        }
        else {
            nodesToReturn = new ArrayList<>();
        }

        for (String child : city.distances.keySet()) {
            CityNode childNode = get(child);
            Double cost = city.pathCost + city.distances.get(childNode.cityName);
            String pathActions = city.actions + " -> " + childNode.cityName;
            CityNode newNode = new CityNode(childNode, city, pathActions, cost);
            calcHeuristics(newNode);
            nodesToReturn.add(newNode);
        }
        return nodesToReturn;
    }

    public CityNode uniformCostSearch(String initialState, String goalState) {
        startStopwatch();
        CityNode root = get(initialState);
        calcHeuristics(root);
        Frontier<CityNode> frontier = new Frontier<>(strategy, root);
        Map<String, CityNode> reached = null;
        if (strategy != Strategy.DEPTH) {
            reached = new HashMap<>();
            reached.put(root.cityName, root);
        }

        CityNode node;
        while (!frontier.isEmpty()) {
            node = frontier.poll();
            node.setEvalAction("  Expanding");
            generatedNodes.add(node.nodeSummary());
            nodesGenerated++;

            if (node.getCityName().equals(goalState)) {
                nodesInFrontier = frontier.size();
                stopStopwatch();
                node.setEvalAction("* Goal found");
                return node;
            }
            else if (strategy == Strategy.DEPTH && !node.getCityName().equals(initialState)) {
                for (CityNode child : expand(node, goalState)) {
                    frontier.add(child);
                }
            }
            else if (strategy != Strategy.DEPTH) {
                for (CityNode child : expand(node, goalState)) {
                    String state = child.cityName;
                    if (strategy == Strategy.BREADTH) {
                        if (state.equals(goalState)) {
                            nodesInFrontier = frontier.size();
                            stopStopwatch();
                            node.setEvalAction("* Goal found");
                            return node;
                        }
                        else if (!reached.containsKey(state)) {
                            reached.put(state, child);
                            frontier.add(child);
                        }
                    }
                    else {
                        if (reached.get(state) == null || child.pathCost < reached.get(state).pathCost) {
                            reached.put(state, child);
                            frontier.add(child);
                            child.setEvalAction("    Adding");
                            generatedNodes.add(child.nodeSummary());
                        }
                        else {
                            child.setEvalAction("    NOT Adding");
                            generatedNodes.add(child.nodeSummary());
                        }
                    }
                }
            }
        }
        stopStopwatch();
        return new CityNode(null, null, "NO PATH", -1);
    }

    // public String depthFirstSearch(String initialState, String goalState) {
    //     CityNode root = get(initialState);
    //     Stack<CityNode> frontier = new Stack<>();
    //     frontier.add(root);

    //     while (!frontier.empty()) {
    //         CityNode node = frontier.pop();
    //         if (node.getCityName().equals(goalState)) {
    //             Stats.setNodesInFrontier(frontier.size());
    //             return node.getActions();
    //         }
    //         else if (!node.getCityName().equals(initialState)) {
    //             for (CityNode child : expand(node, goalState)) {
    //                 frontier.add(child);
    //             }
    //         }
    //     }
    //     return "NO PATH";
    // }

    // public String breadthFirstSearch(String initialState, String goalState) {
    //     CityNode root = get(initialState);
    //     Map<String, CityNode> reached = new Hashtable<>();
    //     reached.put(initialState, root);
    //     Stack<CityNode> frontier = new Stack<>();
    //     frontier.add(root);

    //     while (!frontier.empty()) {
    //         CityNode node = frontier.pop();
    //         for (CityNode child : expand(node, goalState)) {
    //             String state = child.cityName;
    //             if (state.equals(goalState)) {
    //                 Stats.setNodesInFrontier(frontier.size());
    //                 return child.actions;
    //             }
    //             else if (!reached.containsKey(state)) {
    //                 reached.put(state, child);
    //                 frontier.add(child);
    //             }
    //         }
    //     }
    //     return "NO PATH";
    // }

    public Queue<String> getGeneratedNodes() {
        return generatedNodes;
    }

    public int getNumNodesGenerated() {
        return nodesGenerated;
    }

    public int getNumNodesInFrontier() {
        return nodesInFrontier;
    }

    public void startStopwatch() {
        startTime = System.currentTimeMillis();
    }

    public void stopStopwatch() {
        stopTime = System.currentTimeMillis();
    }

    public long getElapsedTime() {
        return stopTime - startTime;
    }

}
