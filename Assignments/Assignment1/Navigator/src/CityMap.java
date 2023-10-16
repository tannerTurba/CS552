/*
 * Tanner Turba
 * October 15, 2023
 * CS 552 - Artificial Intelligence - Assignment 1
 * 
 * This class represents a collection of CityNodes and includes functionality 
 * to find a route from one city to another.
 */
import java.util.*;

public class CityMap extends Hashtable<String, CityNode> {
    private String goal;
    private Map<String, double[]> coordinates = new Hashtable<>();
    private StringBuilder sBuilder = new StringBuilder();
    private int nodesGenerated, nodesInFrontier;
    private long startTime = 0;
    private long stopTime = 0;
    private Strategy strategy;
    private Heuristic heuristic;
    private int verbosity = 0;

    /**
     * CityMap Constructor
     * @param goal The desired city to travel to.
     * @param strategy The search strategy to use.
     * @param heuristic The heuristic to use.
     * @param verbosity The verbosity of output.
     */
    public CityMap(String goal, Strategy strategy, Heuristic heuristic, int verbosity) {
        super();
        this.goal = goal;
        this.strategy = strategy;
        this.heuristic = heuristic;
        this.verbosity = verbosity;
    }

    /**
     * Calculate the heuristics of a CityNode.
     * @param currentNode The CityNode to calculate the heuristics for.
     */
    private void calcHeuristics(CityNode currentNode) {
        double g, h;
        h = getHeuristic(currentNode.getCityName()); //Estimated distance to destination via coordiates
        g = currentNode.getPathCost(); //Actual distance to desitnation

        //Set F value based on search strategy.
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

    /**
     * Calculate the heuristic of the current city to the goal city.
     * @param currentCity The current city.
     * @return The H-valued heuristic.
     */
    private double getHeuristic(String currentCity) {
        double[] coor1 = coordinates.get(currentCity);
        double[] coor2 = coordinates.get(goal);

        if (heuristic == Heuristic.EUCLIDEAN) {
            return euclidean(coor1, coor2);
        }
        else {
            return haversine(coor1, coor2);
        }
    }

    /**
     * Calculate the distance between two cities using the haversine function.
     * @param city1 One city.
     * @param city2 Another city.
     * @return The distance between two cities.
     */
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

    /**
     * Calculate the distance between two cities using the euclidean function.
     * @param city1 One city.
     * @param city2 Another city.
     * @return The distance between two cities.
     */
    private double euclidean(double[] city1, double[] city2) {
        double lon1 = city1[1];
        double lon2 = city2[1];
        double lat1 = city1[0];
        double lat2 = city2[0];

        double lon = lon2 - lon1;
        double lat = lat2 - lat1;
        return Math.sqrt(Math.pow(lon, 2) + Math.pow(lat, 2));
    }

    /**
     * Expands a CityNode to return a collection of children nodes.
     * @param city The CityNode to expand.
     * @return A collection of children nodes.
     */
    private Collection<CityNode> expand(CityNode city) {
        // Initialize a collection of result nodes based on the search stratgey.
        Collection<CityNode> nodesToReturn = null;
        if (strategy == Strategy.GREEDY) {
            nodesToReturn = new PriorityQueue<>();
        }
        else {
            nodesToReturn = new ArrayList<>();
        }

        // Calculate the heuristics and other values for each connecting city.
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

    /**
     * Find the most efficient route between two cities.
     * @param initialState The city to start from.
     * @param goalState The city to end at.
     * @param reachedIsUsed True if a reached table will be used.
     * @return A CityNode, containing resulting information about the search.
     */
    public CityNode search(String initialState, String goalState, boolean reachedIsUsed) {
        // Initialize frontier and reached tables.
        startStopwatch();
        CityNode root = get(initialState);
        calcHeuristics(root);
        Frontier<CityNode> frontier = new Frontier<>(strategy, root);
        Map<String, CityNode> reached = null;
        if (strategy != Strategy.DEPTH && reachedIsUsed) {
            reached = new HashMap<>();
            reached.put(root.getCityName(), root);
        }

        // Continue until the frontier is empty.
        CityNode node;
        while (!frontier.isEmpty()) {
            node = frontier.poll();
            nodesGenerated++;

            // Determine output to record for current node.
            if (node.isCycle() && verbosity == 3) {
                node.setEvalAction("    NOT Adding");
                sBuilder.append(node);
            }
            else {
                node.setEvalAction("  Expanding");
                sBuilder.append(node);
            }
            
            if (node.getCityName().equals(goalState) && strategy != Strategy.BREADTH) {
                // Set output and return if the goal state is found.
                nodesInFrontier = frontier.size();
                stopStopwatch();
                node.setEvalAction("* Goal found");
                return node;
            }
            else if (strategy == Strategy.DEPTH && !node.isCycle()) {
                // Expand and add to the frontier if using depth first search.
                for (CityNode child : expand(node)) {
                    frontier.add(child);
                }
            }
            else if (strategy != Strategy.DEPTH) {
                // Otherwise expand current node.
                for (CityNode child : expand(node)) {
                    String state = child.getCityName();
                    if (strategy == Strategy.BREADTH) {
                        if (state.equals(goalState)) {
                            // If the goal is reached, set output and return. 
                            nodesInFrontier = frontier.size();
                            stopStopwatch();
                            child.setEvalAction("* Goal found");
                            return child;
                        }
                        else if (reachedIsUsed && !reached.containsKey(state)) {
                            // Add current state to the reached table if not reached already.
                            reached.put(state, child);
                            frontier.add(child);
                            if (verbosity == 3) {
                                child.setEvalAction("    Adding");
                                sBuilder.append(child);
                            }
                        }
                    }
                    else {
                        if (reachedIsUsed && (reached.get(state) == null || child.getPathCost() < reached.get(state).getPathCost())) {
                            // Only add current state to the reached table if its better than the current value of state in the table.
                            reached.put(state, child);
                            frontier.add(child);
                            if (verbosity == 3) {
                                child.setEvalAction("    Adding");
                                sBuilder.append(child);
                            }
                        }
                        else if (verbosity == 3) {
                            child.setEvalAction("    NOT Adding");
                            sBuilder.append(child);
                        }
                    }
                }
            }
        }
        stopStopwatch();
        // No path found, return corresponding node.
        return new CityNode("NO PATH");
    }

    /**
     * @return A summary of all generated nodes.
     */
    public String getGeneratedNodes() {
        return sBuilder.toString();
    }

    /**
     * @return The number of nodes generated.
     */
    public int getNumNodesGenerated() {
        return nodesGenerated;
    }

    /**
     * @return The number of nodes remaining in the frontier.
     */
    public int getNumNodesInFrontier() {
        return nodesInFrontier;
    }

    /**
     * Start a stopwatch to begin recording the length of execution.
     */
    private void startStopwatch() {
        startTime = System.currentTimeMillis();
    }

    /**
     * Stop a stopwatch to end recording the length of execution.
     */
    private void stopStopwatch() {
        stopTime = System.currentTimeMillis();
    }

    /**
     * @return The length of execution.
     */
    public long getElapsedTime() {
        return stopTime - startTime;
    }

    /**
     * Add coordinates of a city to the CityMap
     * @param name The name of the city to add.
     * @param coordinates The corresponding coordinates.
     */
    public void addCoordinates(String name, double[] coordinates) {
        this.coordinates.put(name, coordinates);
    }
}
