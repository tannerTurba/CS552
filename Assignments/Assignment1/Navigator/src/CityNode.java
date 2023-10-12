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
    
    public CityNode(String cityName) {
        this.cityName = cityName;
        this.actions += cityName;
    }

    public CityNode(CityNode child, String action, double pathCost) {
        this.cityName = child.cityName;
        this.actions = action;
        this.pathCost = pathCost;
        this.f = pathCost;
        distances = child.distances;
    }

    public void addDistance(String cityName, String distance) {
        if(!distances.containsKey(cityName)) {
            distances.put(cityName, Double.parseDouble(distance));
        }
        else {
            System.out.println("A distance to " + cityName + " already exists");
        }
    }

    public Map<String, Double> getDistances() {
        return distances;
    }

    public String getCityName() {
        return cityName;
    }

    public String getActions() {
        return actions;
    }

    public double getPathCost() {
        return pathCost;
    }

    public double getF() {
        return f;
    }

    public void setF(double f) {
        this.f = f;
    }

    public void setH(double h) {
        this.h = h;
    }

    public void setG(double g) {
        this.g = g;
    }

    public void setEvalAction(String action) {
        this.evalAction = action;
    }

    public String nodeSummary() {
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
