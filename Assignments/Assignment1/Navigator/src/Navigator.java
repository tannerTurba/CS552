import java.io.File;

public class Navigator {
    private String fileName;
    private String initialCity;
    private String destinationCity;
    private Strategy searchStrategy;
    private Heuristic hFunction;
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

        // map.expand("La Crosse");
        // map.expand("La Crescent");

        String solution = map.uniformCostSearch("La Crosse", "Minneapolis");
        System.out.println(solution);
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
            }
            else if(args[i].equals("-g")) {
                i++;
                destinationCity = args[i].replace('\"', '\"');
            }
            else if(args[i].equals("-s")) {
                i++;
                switch(args[i].toLowerCase()) {
                    case "greedy": searchStrategy = Strategy.GREEDY; break;
                    case "uniform": searchStrategy = Strategy.UNIFORM; break;
                    case "breadth": searchStrategy = Strategy.BREADTH; break;
                    case "depth": searchStrategy = Strategy.DEPTH; break;
                    default : searchStrategy = Strategy.A_STAR; break;
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
            else if(args[i].equals("v")) {
                i++;
                verbosity = Integer.parseInt(args[i]);
            }
        }
    }
}
