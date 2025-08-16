package src;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class PeerLogger {

    private String logFileName;
    private String peerId;
    private FileHandler peerLogFileHandler;
    private SimpleDateFormat dateFormat = null;
    private Logger peerLogger;

    public PeerLogger(String peerId) {
        this.peerId = peerId;
        startLogger();
    }

    public void startLogger() {
        try {
            this.dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");

            this.logFileName = "log_peer_" + this.peerId + ".log";
            this.peerLogFileHandler = new FileHandler(this.logFileName, false);
            System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");
            this.peerLogFileHandler.setFormatter(new SimpleFormatter());
            this.peerLogger = Logger.getLogger("PeerLogs");
            this.peerLogger.setUseParentHandlers(false);
            this.peerLogger.addHandler(this.peerLogFileHandler);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void logMessage(String format, Object... args) {
        Calendar c = Calendar.getInstance();
        String currTime = dateFormat.format(c.getTime());
        peerLogger.log(Level.INFO, String.format("[%s]: %s", currTime, String.format(format, args)));
    }

    public synchronized void genTCPConnLogSender(String peer) {
        logMessage("Peer [%s] makes a connection to Peer [%s].", peerId, peer);
    }

    public synchronized void genTCPConnLogReceiver(String peer) {
        logMessage("Peer [%s] is connected from Peer [%s].", peerId, peer);
    }

    public synchronized void changePreferredNeighbors(List<String> neighbors) {
        String neighborList = String.join(",", neighbors);
        logMessage("Peer [%s] has the preferred neighbors [%s].", peerId, neighborList);
    }

    public synchronized void changeOptimisticallyUnchokedNeighbor(String peer) {
        logMessage("Peer [%s] has the optimistically unchoked neighbor [%s].", peerId, peer);
    }

    public synchronized void unchokedNeighbor(String peer) {
        logMessage("Peer [%s] is unchoked by [%s].", peerId, peer);
    }

    public synchronized void chokingNeighbor(String peer) {
        logMessage("Peer [%s] is choked by [%s].", peerId, peer);
    }

    public synchronized void receiveHave(String peer, int index) {
        logMessage("Peer [%s] received the 'have' message from [%s] for the piece [%d].", peerId, peer, index);
    }

    public synchronized void receiveInterested(String peer) {
        logMessage("Peer [%s] received the 'interested' message from [%s].", peerId, peer);
    }

    public synchronized void receiveNotInterested(String peer) {
        logMessage("Peer [%s] received the 'not interested' message from [%s].", peerId, peer);
    }

    public synchronized void downloadPiece(String peer, int index, int pieces) {
        logMessage("Peer [%s] has downloaded the piece [%d] from [%s]. Now the number of pieces it has is [%d].",
                peerId, index, peer, pieces);
    }

    public synchronized void downloadComplete() {
        logMessage("Peer [%s] has downloaded the complete file.", peerId);
    }

    public void closeLogger() {
        try {
            if (peerLogFileHandler != null) {
                peerLogFileHandler.close();
            }
        } catch (Exception e) {
            System.out.println("Failed to close peer logger");
            e.printStackTrace();
        }
    }
}
