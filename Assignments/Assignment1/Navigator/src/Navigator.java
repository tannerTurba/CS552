/*
 * Tanner Turba
 * October 15, 2023
 * CS 552 - Artificial Intelligence - Assignment 1
 * 
 * A program that reads in an input file describing a set of cities and then searches 
 * for paths between two user-specified cities using various search strategies.
 */
import java.io.*;
import java.util.*;

public class Navigator {
    private File file;
    private String initialCity;
    private String destinationCity;
    private Strategy searchStrategy = Strategy.A_STAR;
    private Heuristic hFunction = Heuristic.HAVERSINE;
    private boolean reachedIsUsed = true;
    private int verbosity = 0;
    private CityMap map;

    public static void main(String[] args) {
        System.out.println();
        new Navigator(args);
    }

    /**
     * Begins the execution of navigation.
     * @param args The configuration of the navigation.
     */
    public Navigator(String[] args) {
        parseArgs(args);
        this.map = new CityMap(destinationCity, searchStrategy, hFunction, verbosity);
        parseInput();
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

    /**
     * Sorts through the passed arguments and saves them in the Navigator properties.
     * @param args
     */
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

    /**
     * Sorts through the input file that contains cities, their coordinates, and their
     * relations to other cities.
     */
    private void parseInput() {
        boolean mapIsCompleted = false;
        boolean isFirstLine = true;
        Scanner inputReader;

        try {
            inputReader = new Scanner(file);
            String currentLine;
            while (inputReader.hasNextLine()) {
                currentLine = inputReader.nextLine();
                if (!isComment(currentLine)) {
                    isFirstLine = false;
                }
                if(!mapIsCompleted && !isComment(currentLine)) {
                    // Read coordinates into CityNode
                    CityNode city = readCity(currentLine);
                    map.put(city.getCityName(), city);
                }
                else if(!isFirstLine && !mapIsCompleted && isComment(currentLine)) {
                    mapIsCompleted = true;
                }
                else if(mapIsCompleted && !isComment(currentLine)) {
                    // Read distance between cities
                    readDistance(currentLine);
                }
            }
        }
        catch(FileNotFoundException e) {
            System.out.println("File not found!");
            e.printStackTrace();
        }
    }

    /**
     * Determines if a line read from a file is a comment.
     * @param line The line of text from a file. 
     * @return True if the line is a comment.
     */
    private boolean isComment(String line) {
        return line.charAt(0) == '#';
    }

    /**
     * Reads a city from the line in a file.
     * @param line The line of text from a file.
     * @return A CityNode that represents the line from the file.
     */
    private CityNode readCity(String line) {
        Scanner lineReader = new Scanner(line);
        lineReader.useDelimiter(",");
        String name = lineReader.next().trim();
        double latitude = Double.parseDouble(lineReader.next().trim());
        double longitude = Double.parseDouble(lineReader.next().trim());
        lineReader.close();
        map.addCoordinates(name, new double[]{latitude, longitude});

        return new CityNode(name);
    }

    /**
     * Reads the distance from one city to another from a file.
     * @param line The line of text from a file.
     */
    private void readDistance(String line) {
        Scanner lineReader = new Scanner(line);
        lineReader.useDelimiter(",");
        String name1 = lineReader.next().trim();
        String name2 = lineReader.next().trim();
        String distance = lineReader.next().trim();
        lineReader.close();
        
        map.get(name1).addDistance(name2, distance);
        map.get(name2).addDistance(name1, distance);
    }

    /**
     * Summarizes the search problem for output.
     * @return A summary of the search problem.
     */
    private String getSearchProblem() {
        String val = "* Reading data from [" + file.getName() + "]\n";
        val += "* Number of cities: " + map.size() + "\n";
        val += "* Searching for path from " + initialCity + " to " + destinationCity + " using " + searchStrategy + " Search\n";
        return val;
    }

    /**
     * Summarizes the search details. 
     * @param solution The CityNode that contains details about the completed search.
     * @return A summary of the search.
     */
    private String getSearchDetails(CityNode solution) {
        return solution + "* Search took " + map.getElapsedTime() + "ms\n";
    }

    /**
     * Prints output for verbosity level 0.
     * @param solution The CityNode that contains details about the completed search. 
     */
    public void level0(CityNode solution) {
        System.out.println(solution.getActions());
        System.out.printf("Distance: %.1f\n\n", solution.getPathCost());
        System.out.println("Total nodes generated      : " + map.getNumNodesGenerated());
        System.out.println("Nodes remaining on frontier: " + map.getNumNodesInFrontier());
    }

    /**
     * Prints output for verbosity level 1.
     * @param solution The CityNode that contains details about the completed search. 
     */
    public void level1(CityNode solution) {
        System.out.print(getSearchProblem());
        System.out.println(getSearchDetails(solution));
        level0(solution);
    }

    /**
     * Prints output for verbosity levels 2 and 3.
     * @param solution The CityNode that contains details about the completed search. 
     */
    public void level23(CityNode solution) {
        System.out.print(getSearchProblem());
        System.out.print(map.getGeneratedNodes());
        System.out.println(getSearchDetails(solution));
        level0(solution);
    }
}
