import java.util.*;

public class CityMap extends Hashtable<String, CityNode> {

    public CityMap() {
        super();
    }

    public List<CityNode> expand(CityNode city) {
        ArrayList<CityNode> nodesToReturn = new ArrayList<>();
        for (String child : city.distances.keySet()) {
            CityNode childNode = get(child);
            Double cost =  + city.pathCost + city.distances.get(childNode.cityName);
            String pathActions = city.actions + " -> " + childNode.cityName;
            CityNode newNode = new CityNode(childNode, city, pathActions, cost);
            nodesToReturn.add(newNode);
        }
        return nodesToReturn;
    }

    public String uniformCostSearch(String initialState, String goalState) {
        CityNode root = get(initialState);
        PriorityQueue<CityNode> frontier = new PriorityQueue<>();
        Map<String, CityNode> reached = new HashMap<>();
        frontier.add(root);

        CityNode node;
        while(!frontier.isEmpty()) {
            node = frontier.poll();
            if(node.getCityName().equals(goalState)) {
                return node.getActions();
            }
            for (CityNode child : expand(node)) {
                String state = child.cityName;
                if(!state.equals(goalState) || reached.get(state) == null || child.pathCost < reached.get(state).pathCost) {
                    reached.put(state, child);
                    frontier.add(child);
                }
            }
        }
        return "No solution available";
    }
}
