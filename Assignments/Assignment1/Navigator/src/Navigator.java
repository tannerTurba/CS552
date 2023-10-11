import java.io.File;
import java.util.Queue;

public class Navigator {
    private File file;
    private String initialCity;
    private static String destinationCity;
    private static Strategy searchStrategy = Strategy.A_STAR;
    private static Heuristic hFunction = Heuristic.HAVERSINE;
    private boolean reachedIsUsed = true;
    private int verbosity = 0;
    private CityMap map;

    public static void main(String[] args) {
        System.out.println();
        new Navigator(args);
    }

    public Navigator(String[] args) {
        parseArgs(args);
        map = new CityMap(file, destinationCity, searchStrategy);

        CityNode solution = map.uniformCostSearch(initialCity, destinationCity);

        if (verbosity == 3) {
            level3(solution);
        }
        else if (verbosity == 1) {
            level1(solution);
        }
        else if (verbosity == 2) {
            level2(solution);
        }
        else {
            level0(solution);
        }
    }

    public String toString() {
        String strToPrint = "FileName: " + file.getName() + "\n";
        strToPrint += "InitialCity: " + initialCity + "\n";
        strToPrint += "DestinationCity: " + destinationCity + "\n";
        strToPrint += "SearchStrategy: " + searchStrategy + "\n";
        strToPrint += "Heuristic Function: " + hFunction + "\n";
        strToPrint += "Reached is used: " + reachedIsUsed + "\n";
        strToPrint += "Verbosity: " + verbosity + "\n";
        return strToPrint;
    }

    private void parseArgs(String[] args) {
        for(int i = 0; i < args.length; i++) {
            if(args[i].equals("-f")) {
                i++;
                file = new File(args[i]);
            }
            else if(args[i].equals("-i")) {
                i++;
                initialCity = args[i].replace('\"', '\"');
                Stats.setStartState(initialCity);
            }
            else if(args[i].equals("-g")) {
                i++;
                destinationCity = args[i].replace('\"', '\"');
                Stats.setEndState(destinationCity);
            }
            else if(args[i].equals("-s")) {
                i++;
                if (args[i].toLowerCase().equals("greedy")) {
                    searchStrategy = Strategy.GREEDY;
                }
                else if (args[i].toLowerCase().equals("uniform")) {
                    searchStrategy = Strategy.UNIFORM;
                }
                else if (args[i].toLowerCase().equals("breadth")) {
                    searchStrategy = Strategy.BREADTH;
                }
                else if (args[i].toLowerCase().equals("depth")) {
                    searchStrategy = Strategy.DEPTH;
                }
                else {
                    searchStrategy = Strategy.A_STAR;
                }
                Stats.setSearchStrategy(searchStrategy);
            }
            else if(args[i].equals("-h")) {
                i++;
                switch(args[i].toLowerCase()) {
                    case "euclidean": hFunction = Heuristic.EUCLIDEAN; break;
                    default : hFunction = Heuristic.HAVERSINE; break;
                }
            }
            else if(args[i].equals("--no-reached")) {
                reachedIsUsed = false;
            }
            else if(args[i].equals("-v")) {
                i++;
                verbosity = Integer.parseInt(args[i]);
            }
        }
    }

    public static Strategy getStrategy() {
        return searchStrategy;
    }

    public static Heuristic getHeuristic() {
        return hFunction;
    }

    public static String getGoal() {
        return destinationCity;
    }

    public void level0(CityNode solution) {
        System.out.println(solution.actions);
        System.out.printf("Distance: %.1f\n\n", solution.pathCost);
        System.out.println("Total nodes generated      : " + map.getNumNodesGenerated());
        System.out.println("Nodes remaining on frontier: " + map.getNumNodesInFrontier());
    }

    private String getSearchProblem(CityNode solution) {
        String val = "* Reading data from [" + file.getName() + "]\n";
        val += "* Number of cities: " + map.size() + "\n";
        val += "* Searching for path from " + Stats.getStartState() + " to " + Stats.getEndState() + " using " + Stats.getSearchStrategy() + " Search\n";
        return val;
    }

    private String getSearchDetails(CityNode solution) {
        return solution.nodeSummary() + "* Search took " + map.getElapsedTime() + "ms\n";
    }

    public void level1(CityNode solution) {
        String val = getSearchProblem(solution);
        val += getSearchDetails(solution);
        val += solution.nodeSummary();
        System.out.println(val);
        level0(solution);
    }

    public void level2(CityNode solution) {
        String val = getSearchProblem(solution);
        Queue<String> generatedNodes = map.getGeneratedNodes();
        while (!generatedNodes.isEmpty()) {
            String summary = generatedNodes.poll();
            String x = summary.strip().split(" ")[0];
            if (summary.strip().split(" ")[0].equals("Expanding")) {
                val += summary;
            }
        }
        val += getSearchDetails(solution);
        System.out.println(val);
        level0(solution);
    }

    public void level3(CityNode solution) {
        String val = getSearchProblem(solution);
        Queue<String> generatedNodes = map.getGeneratedNodes();
        while (!generatedNodes.isEmpty()) {
            val += generatedNodes.poll();
        }
        val += getSearchDetails(solution);
        System.out.println(val);
        level0(solution);
    }
}
