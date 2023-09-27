import java.io.*;
import java.util.*;

public class IOManager {
    private File inputFile;
    private File outputFile;

    public IOManager(File inputFile) {
        this.inputFile = inputFile;
    }

    public IOManager(File inputFile, File outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }
    
    public Map<String, CityNode> parseInput() {
        boolean mapIsCompleted = false;
        boolean isFirstLine = true;
        Scanner inputReader;
        Map<String, CityNode> map = new Hashtable<>();

        try {
            inputReader = new Scanner(inputFile);
            String currentLine;
            while (inputReader.hasNextLine()) {
                currentLine = inputReader.nextLine();
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
                    readDistance(map, currentLine);
                }
                isFirstLine = false;
            }
        }
        catch(FileNotFoundException e) {
            System.out.println("File not found!");
            e.printStackTrace();
        }
        return map;
    }

    private boolean isComment(String line) {
        return line.charAt(0) == '#';
    }

    private CityNode readCity(String line) {
        Scanner lineReader = new Scanner(line);
        lineReader.useDelimiter(",");
        String name = lineReader.next().trim();
        String latitude = lineReader.next().trim();
        String longitude = lineReader.next().trim();
        lineReader.close();

        return new CityNode(name, latitude, longitude);
    }

    private void readDistance(Map<String, CityNode> map, String line) {
        Scanner lineReader = new Scanner(line);
        lineReader.useDelimiter(",");
        String name1 = lineReader.next().trim();
        String name2 = lineReader.next().trim();
        String distance = lineReader.next().trim();
        lineReader.close();
        
        map.get(name1).addDistance(name2, distance);
        map.get(name2).addDistance(name1, distance);
    }
}
