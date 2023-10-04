import java.util.*;

public class CityMap extends Hashtable<String, CityNode> {

    public CityMap() {
        super();
    }

    public List<CityNode> expand(CityNode city, String goal) {
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

    public CityNode uniformCostSearch(String initialState, String goalState) {
        Stats.startStopwatch();
        CityNode root = get(initialState);
        PriorityQueue<CityNode> frontier = new PriorityQueue<>();
        Map<String, CityNode> reached = new HashMap<>();
        reached.put(root.cityName, root);
        frontier.add(root);
        Stats.nodesGenerated.add(root);

        CityNode node;
        while (!frontier.isEmpty()) {
            node = frontier.poll();
            if (node.getCityName().equals(goalState)) {
                Stats.setNodesInFrontier(frontier.size());
                Stats.stopStopwatch();
                return node;
            }
            for (CityNode child : expand(node, goalState)) {
                String state = child.cityName;
                if (reached.get(state) == null || child.pathCost < reached.get(state).pathCost) {
                    reached.put(state, child);
                    frontier.add(child);
                    Stats.nodesGenerated.add(child);
                }
            }
        }
        Stats.stopStopwatch();
        return new CityNode(null, null, "NO PATH", -1);
    }

    public String depthFirstSearch(String initialState, String goalState) {
        CityNode root = get(initialState);
        Stack<CityNode> frontier = new Stack<>();
        frontier.add(root);

        while (!frontier.empty()) {
            CityNode node = frontier.pop();
            if (node.getCityName().equals(goalState)) {
                Stats.setNodesInFrontier(frontier.size());
                return node.getActions();
            }
            else if (!node.getCityName().equals(initialState)) {
                for (CityNode child : expand(node, goalState)) {
                    frontier.add(child);
                }
            }
        }
        return "NO PATH";
    }

    public String breadthFirstSearch(String initialState, String goalState) {
        CityNode root = get(initialState);
        Map<String, CityNode> reached = new Hashtable<>();
        reached.put(initialState, root);
        Stack<CityNode> frontier = new Stack<>();
        frontier.add(root);

        while (!frontier.empty()) {
            CityNode node = frontier.pop();
            for (CityNode child : expand(node, goalState)) {
                String state = child.cityName;
                if (state.equals(goalState)) {
                    Stats.setNodesInFrontier(frontier.size());
                    return child.actions;
                }
                else if (!reached.containsKey(state)) {
                    reached.put(state, child);
                    frontier.add(child);
                }
            }
        }
        return "NO PATH";
    }
}
