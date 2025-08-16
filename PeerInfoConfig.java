package src;

import src.RemotePeerInfo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class PeerInfoConfig {

    private HashMap<String, RemotePeerInfo> peerInfoMap;
    private ArrayList<String> peerList;

    public PeerInfoConfig() {
        this.peerInfoMap = new HashMap<>();
        this.peerList = new ArrayList<>();
    }

    public void loadConfigFile() {
        try (Scanner scanner = new Scanner(new File("PeerInfo.cfg"))) {
            while (scanner.hasNextLine()) {
                String[] tokens = scanner.nextLine().split("\\s+");
                RemotePeerInfo peerInfo = new RemotePeerInfo(tokens[0], tokens[1], tokens[2], tokens[3]);
                this.peerInfoMap.put(tokens[0], peerInfo);
                this.peerList.add(tokens[0]);
            }
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }

    public RemotePeerInfo getPeerConfig(String peerID) {
        return this.peerInfoMap.get(peerID);
    }

    public HashMap<String, RemotePeerInfo> getPeerInfoMap() {
        return this.peerInfoMap;
    }

    public ArrayList<String> getPeerList() {
        return this.peerList;
    }
}
