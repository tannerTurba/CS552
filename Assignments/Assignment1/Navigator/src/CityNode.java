import java.util.*;

public class CityNode implements Comparable<CityNode> {
    public static Map<String, double[]> coordinates = new Hashtable<>();
    public Map<String, Double> distances = new Hashtable<>();
    public String cityName;
    public CityNode parent;
    public String actions = "Route found: ";
    public double pathCost = 0.0;
    private double f = 0.0;
    private double g = 0.0;
    private double h = 0.0;
    
    public CityNode(String cityName, String latitude, String longitude) {
        double[] coordinates = new double[2];
        coordinates[0] = Double.parseDouble(latitude);
        coordinates[1] = Double.parseDouble(longitude);
        addCoordinates(cityName, coordinates);
        this.cityName = cityName;
        this.actions += cityName;
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

    public void setPathCost(double cost) {
        pathCost = cost;
    }

    public double getPathCost() {
        return pathCost;
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
    public int compareTo(CityNode city2) {
        String goal = Navigator.getGoal();
        double f2 = 0.0;
        h = getHeuristic(cityName, goal);

        if(Navigator.getStrategy() == Strategy.GREEDY) {
            f = h;
            f2 = getHeuristic(city2.cityName, goal);
        }
        else if(Navigator.getStrategy() == Strategy.A_STAR) {
            f = h + pathCost;
            f2 = getHeuristic(city2.cityName, goal) + city2.pathCost;
        }
        else {
            f2 = city2.pathCost;
        }

        if(f - f2 > 0) {
            return 1;
        }
        else if(f - f2 < 0) {
            return -1;
        }
        return 0;
    }

    private double getHeuristic(String city1, String city2) {
        double[] coor1 = CityNode.coordinates.get(city1);
        double[] coor2 = CityNode.coordinates.get(city2);

        if (Navigator.getHeuristic() == Heuristic.HAVERSINE) {
            return haversine(coor1, coor2);
        }
        else {
            return euclidean(coor1, coor2);
        }
    }

    private double haversine(double[] city1, double[] city2) {
        double lon1 = Math.toRadians(city1[0]);
        double lon2 = Math.toRadians(city2[0]);
        double lat1 = Math.toRadians(city1[1]);
        double lat2 = Math.toRadians(city2[1]);

        double lon = lon2 - lon1;
        double lat = lat2 - lat1;
        double a = Math.pow(Math.sin(lat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(lon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 3958.8 * c;
    }

    private double euclidean(double[] city1, double[] city2) {
        double lon1 = city1[0];
        double lon2 = city2[0];
        double lat1 = city1[1];
        double lat2 = city2[1];

        double lon = lon2 - lon1;
        double lat = lat2 - lat1;
        return Math.sqrt(Math.pow(lon, 2) + Math.pow(lat, 2));
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
}
