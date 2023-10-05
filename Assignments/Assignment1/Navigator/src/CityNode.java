import java.util.*;

public class CityNode implements Comparable<CityNode> {
    public Map<String, Double> distances = new Hashtable<>();
    public String cityName;
    public CityNode parent;
    public String actions = "Route found: ";
    public double pathCost = 0.0;
    private double f = 999.0;
    private double g = 999.0;
    private double h = 999.0;
    
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

    // public String toString() {
    //     String result = "[" + getLatitude(cityName) + ", " + getLongitude(cityName) + "]\n";
    //     Iterator<String> neighbors = distances.keySet().iterator();
        
    //     String neighbor;
    //     while(neighbors.hasNext()) {
    //         neighbor = neighbors.next();
    //         result += "    |--> " + neighbor + ": " + distances.get(neighbor) + "\n";
    //     }
    //     return result;
    // }

    @Override
    public int compareTo(CityNode city2) {
        // String goal = Navigator.getGoal();
        // double f2 = 0.0;
        // h = getHeuristic(cityName, goal);

        // if(Navigator.getStrategy() == Strategy.GREEDY) {
        //     f = h;
        //     f2 = getHeuristic(city2.cityName, goal);
        // }
        // else if(Navigator.getStrategy() == Strategy.A_STAR) {
        //     f = h + pathCost;
        //     f2 = getHeuristic(city2.cityName, goal) + city2.pathCost;
        // }
        // else {
        //     f2 = city2.pathCost;
        // }

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
}
