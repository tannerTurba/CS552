import java.util.LinkedList;
import java.util.Queue;

public class Stats {
    private static int nodesInFrontier = 0;
    private static long startTime = 0;
    private static long stopTime = 0;
    private static String startState;
    private static String endState;
    private static Strategy searchStrategy = Strategy.A_STAR;
    public static Queue<CityNode> nodesGenerated = new LinkedList<>();
    public static int nodeCount = 0;

    public static void setSearchStrategy(Strategy s) {
        searchStrategy = s;
    }

    public static String getSearchStrategy() {
        return searchStrategy.name();
    }

    public static void setStartState(String state) {
        startState = state;
    }

    public static String getStartState() {
        return startState;
    }

    public static void setEndState(String state) {
        endState = state;
    }

    public static String getEndState() {
        return endState;
    }

    public static void startStopwatch() {
        startTime = System.currentTimeMillis();
    }

    public static void stopStopwatch() {
        stopTime = System.currentTimeMillis();
    }

    public static long getElapsedTime() {
        return stopTime - startTime;
    }

    public static void addNode(CityNode node) {
        nodesGenerated.add(node);
        nodeCount++;
    }

    public static CityNode getNodeGenerated() {
        return nodesGenerated.poll();
    }

    public static int getNumNodesGenerated() {
        return nodeCount;
    }

    public static void setNodesInFrontier(int nodes) {
        nodesInFrontier = nodes;
    }

    public static int getNodesInFrontier() {
        return nodesInFrontier;
    }
}