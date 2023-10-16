/*
 * Tanner Turba
 * October 15, 2023
 * CS 552 - Artificial Intelligence - Assignment 1
 * 
 * This class represents a node that contains all relevant information about
 * a city for a search problem. It implements the comparable interace to 
 * enable easier sorting in the search problem.
 */
import java.util.*;

public class CityNode implements Comparable<CityNode> {
    private Map<String, Double> distances = new Hashtable<>();
    private String cityName;
    private String actions = "Route found: ";
    private double pathCost = 0.0;
    private double f = 0.0;
    private double g = 0.0;
    private double h = 0.0;
    private String evalAction = "";
    
    /**
     * Node contructor
     * @param cityName Name of the city
     */
    public CityNode(String cityName) {
        this.cityName = cityName;
        this.actions += cityName;
    }

    /**
     * Node constructor
     * @param parent The parent of the node being created.
     * @param action The action that represents the selection of this node while searching.
     * @param pathCost The action cost of selecting this node while searching.
     */
    public CityNode(CityNode parent, String action, double pathCost) {
        this.cityName = parent.cityName;
        this.actions = action;
        this.pathCost = pathCost;
        this.f = pathCost;
        distances = parent.distances;
    }

    /**
     * Adds a distance to another city in this node. 
     * @param cityName The name of the city connected to this node.
     * @param distance The distance to the city. 
     */
    public void addDistance(String cityName, String distance) {
        if(!distances.containsKey(cityName)) {
            distances.put(cityName, Double.parseDouble(distance));
        }
        else {
            System.out.println("A distance to " + cityName + " already exists");
        }
    }

    /**
     * @return Pairs of cities and distances connected to the CityNode.
     */
    public Map<String, Double> getDistances() {
        return distances;
    }

    /**
     * @return The city name of the CityNode.
     */
    public String getCityName() {
        return cityName;
    }

    /**
     * @return The action that represents the selection of this node while searcing.
     */
    public String getActions() {
        return actions;
    }

    /**
     * @return The action cost of selecting this node while searching.
     */
    public double getPathCost() {
        return pathCost;
    }

    /**
     * @return The value of the F heuristic.
     */
    public double getF() {
        return f;
    }

    /**
     * Sets the value of the F heuristic.
     * @param f The value of the F heristic.
     */
    public void setF(double f) {
        this.f = f;
    }

    /**
     * Sets the value of the H heuristic.
     * @param h The value of the H heristic.
     */
    public void setH(double h) {
        this.h = h;
    }

    /**
     * Sets the value of the G heuristic.
     * @param h The value of the G heristic.
     */
    public void setG(double g) {
        this.g = g;
    }

    /**
     * Sets the action that represents the selection of this node while searching.
     * @param action The action that represents the selection of this node while searching.
     */
    public void setEvalAction(String action) {
        this.evalAction = action;
    }

    /**
     * @return A String that summarizes the value of the CityNode.
     */
    public String toString() {
        String lastState;
        if (!actions.contains("->")) {
            lastState = "null";
        }
        else {
            String[] actionsArray = actions.split(" -> ");
            lastState = actionsArray[actionsArray.length-2];
            if (lastState.contains("Route found: ")) {
                lastState = lastState.replace("Route found: ", "");
            }
        }
        return String.format("%-14s: %-13s  (p-> %-12s [f=%6.1f; g=%6.1f; h=%6.1f]\n", evalAction, cityName, lastState+')', f, g, h);
    }

    /**
     * Determines whether or not the selection of this CityNode introduces a cycle in the search.
     * @return true if this CityNode introduces a cycle.
     */
    public boolean isCycle() {
        String[] actionsArray = actions.split(" -> ");
        if (actionsArray.length <= 2) {
            return false;
        }

        String currentState = actionsArray[actionsArray.length-1];
        for (int i = 0; i < actionsArray.length - 1; i++) {
            if (actionsArray[i].contains(currentState)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(CityNode city2) {
        if(f - city2.getF() > 0) {
            return 1;
        }
        else if(f - city2.getF() < 0) {
            return -1;
        }
        return 0;
    }
}
