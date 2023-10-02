import java.util.*;

public class CityNode implements Comparable<CityNode> {
    public static Map<String, double[]> coordinates = new Hashtable<>();
    public Map<String, Double> distances = new Hashtable<>();
    public String cityName;
    public CityNode parent;
    public String actions;
    public double pathCost = 0;
    
    public CityNode(String cityName, String latitude, String longitude) {
        double[] coordinates = new double[2];
        coordinates[0] = Double.parseDouble(latitude);
        coordinates[1] = Double.parseDouble(longitude);
        addCoordinates(cityName, coordinates);
        this.cityName = cityName;
        this.actions = cityName;
    }

    public CityNode(CityNode child, CityNode parent, String action, double pathCost) {
        this.cityName = child.cityName;
        this.parent = parent;
        this.actions = action;
        this.pathCost = pathCost;
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

    public void addCoordinates(String cityName, double[] coordi) {
        coordinates.put(cityName, coordi);
    }

    public static double getLatitude(String city) {
        return coordinates.get(city)[0];
    }

    public static double getLongitude(String city) {
        return coordinates.get(city)[1];
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

    public String toString() {
        String result = "[" + getLatitude(cityName) + ", " + getLongitude(cityName) + "]\n";
        Iterator<String> neighbors = distances.keySet().iterator();
        
        String neighbor;
        while(neighbors.hasNext()) {
            neighbor = neighbors.next();
            result += "    |--> " + neighbor + ": " + distances.get(neighbor) + "\n";
        }
        return result;
    }

    @Override
    public int compareTo(CityNode o) {
        if(pathCost < o.pathCost) {
            return 1;
        }
        else if(pathCost > o.pathCost) {
            return -1;
        }
        else {
            return 0;
        }
    }
}
