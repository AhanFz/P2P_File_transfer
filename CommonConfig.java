package src;

import java.io.FileReader;
import java.util.Scanner;

public class CommonConfig {
    public int noOfPreferredNeighbors;
    public int unchokingInterval;
    public int optimisticUnchokingInterval;
    public String fName;
    public int fSize;
    public int pSize;

    public void loadCommonFile() {
        final String configFileName = "Common.cfg";

        try (Scanner fileReader = new Scanner(new FileReader(configFileName))) {
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                String[] temp = line.split("\\s+");

                if (temp.length < 2) {
                    continue; // Skip invalid lines
                }

                switch (temp[0]) {
                    case "NumberOfPreferredNeighbors":
                        noOfPreferredNeighbors = Integer.parseInt(temp[1]);
                        break;
                    case "UnchokingInterval":
                        unchokingInterval = Integer.parseInt(temp[1]);
                        break;
                    case "OptimisticUnchokingInterval":
                        optimisticUnchokingInterval = Integer.parseInt(temp[1]);
                        break;
                    case "FileName":
                        fName = temp[1];
                        break;
                    case "FileSize":
                        fSize = Integer.parseInt(temp[1]);
                        break;
                    case "PieceSize":
                        pSize = Integer.parseInt(temp[1]);
                        break;
                    // Add more cases if needed
                    default:
                }
            }
        } catch (Exception ex) {
            System.out.println("Error while loading configuration: " + ex.getMessage());
        }
    }
}
