import java.io.*;
import java.util.*;

public class Data extends ArrayList<String> {
    
    public Data(File file) {
        super();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                add(scanner.nextLine().strip());
            }
            scanner.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        this.forEach(x -> {
            sBuilder.append(x);
            sBuilder.append("\n");
        });
        return sBuilder.toString();
    }
}
