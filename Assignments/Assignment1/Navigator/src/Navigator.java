import java.io.File;

public class Navigator {
    private String fileName;
    private String initialCity;
    private static String destinationCity;
    private static Strategy searchStrategy = Strategy.A_STAR;
    private static Heuristic hFunction = Heuristic.HAVERSINE;
    private boolean reachedIsUsed = true;
    private int verbosity = 0;

    public static void main(String[] args) {
        System.out.println();
        new Navigator(args);
    }

    public Navigator(String[] args) {
        parseArgs(args);
        IOManager ioManager = new IOManager(new File(fileName));
        CityMap map = ioManager.parseInput();

        // System.out.println(this);
        // System.out.println(map);

        CityNode solution = map.uniformCostSearch(initialCity, destinationCity);
        ioManager.level2(solution);
    }

    public String toString() {
        String strToPrint = "FileName: " + fileName + "\n";
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
                fileName = args[i];
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
            else if(args[i].equals("v")) {
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
}
