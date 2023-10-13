import java.io.File;

public class Navigator {
    private File file;
    private String initialCity;
    private static String destinationCity;
    private Strategy searchStrategy = Strategy.A_STAR;
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
        map = new CityMap(file, destinationCity, searchStrategy, hFunction, verbosity);

        CityNode solution = map.search(initialCity, destinationCity, reachedIsUsed);

        if (verbosity == 1) {
            level1(solution);
        }
        else if (verbosity == 2 || verbosity == 3) {
            level23(solution);
        }
        else {
            level0(solution);
        }
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
            }
            else if(args[i].equals("-g")) {
                i++;
                destinationCity = args[i].replace('\"', '\"');
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

    private String getSearchProblem(CityNode solution) {
        String val = "* Reading data from [" + file.getName() + "]\n";
        val += "* Number of cities: " + map.size() + "\n";
        val += "* Searching for path from " + initialCity + " to " + destinationCity + " using " + searchStrategy + " Search\n";
        return val;
    }

    private String getSearchDetails(CityNode solution) {
        return solution.nodeSummary() + "* Search took " + map.getElapsedTime() + "ms\n";
    }

    public void level0(CityNode solution) {
        System.out.println(solution.getActions());
        System.out.printf("Distance: %.1f\n\n", solution.getPathCost());
        System.out.println("Total nodes generated      : " + map.getNumNodesGenerated());
        System.out.println("Nodes remaining on frontier: " + map.getNumNodesInFrontier());
    }

    public void level1(CityNode solution) {
        System.out.print(getSearchProblem(solution));
        System.out.println(getSearchDetails(solution));
        level0(solution);
    }

    public void level23(CityNode solution) {
        System.out.print(getSearchProblem(solution));
        System.out.print(map.getGeneratedNodes());
        System.out.println(getSearchDetails(solution));
        level0(solution);
    }
}
