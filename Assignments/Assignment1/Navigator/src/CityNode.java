import java.util.*;

public class CityNode {
    private String cityName;
    private double[] coordinates = new double[2];
    private Map<String, Double> distances = new Hashtable<>();
    
    public CityNode(String cityName, String latitude, String longitude) {
        this.cityName = cityName;
        coordinates[0] = Double.parseDouble(latitude);
        coordinates[1] = Double.parseDouble(longitude);
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

    public double getLatitude() {
        return coordinates[0];
    }

    public double getLongitude() {
        return coordinates[1];
    }

    public double getDistanceTo(String someCtiy) {
        if(distances.containsKey(someCtiy)) {
            return distances.get(someCtiy).doubleValue();
        }
        return -1.0;
    }

    public String toString() {
        String result = "[" + coordinates[0] + ", " + coordinates[1] + "]\n";
        Iterator<String> neighbors = distances.keySet().iterator();
        
        String neighbor;
        while(neighbors.hasNext()) {
            neighbor = neighbors.next();
            result += "    |--> " + neighbor + ": " + distances.get(neighbor) + "\n";
        }
        return result;
    }
}
