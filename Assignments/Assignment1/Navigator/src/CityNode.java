import java.util.*;

public class CityNode implements Comparable<CityNode> {
    public Map<String, Double> distances = new Hashtable<>();
    public String cityName;
    public CityNode parent;
    public String actions = "Route found: ";
    public double pathCost = 0.0;
    private double f = 0.0;
    private double g = 0.0;
    private double h = 0.0;
    private String evalAction = "";
    
    public CityNode(String cityName) {
        this.cityName = cityName;
        this.actions += cityName;
    }

    public CityNode(CityNode child, CityNode parent, String action, double pathCost) {
        this.cityName = child.cityName;
        this.parent = parent;
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

    public String getCityName() {
        return cityName;
    }

    public String getActions() {
        return actions;
    }

    public double getDistanceTo(String someCtiy) {
        if(distances.containsKey(someCtiy)) {
            return distances.get(someCtiy).doubleValue();
        }
        return -1.0;
    }

    public void setPathCost(double cost) {
        pathCost = cost;
    }

    public double getPathCost() {
        return pathCost;
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

    public double getF() {
        return f;
    }

    public double getH() {
        return h;
    }

    public double getG() {
        return g;
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

    public String getEvalAction() {
        return evalAction;
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
}
