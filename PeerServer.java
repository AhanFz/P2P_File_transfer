package src;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import src.CommonConfig;
import src.PeerAdmin;
import src.PeerHandler;
import src.PeerInfoConfig;
import src.RemotePeerInfo;

public class PeerServer implements Runnable {
    private String peerID;
    private ServerSocket listener;
    private PeerAdmin peerAdmin;
    private boolean isTerminated;

    public PeerServer(String peerID, ServerSocket listener, PeerAdmin admin) {
        this.peerID = peerID;
        this.listener = listener;
        this.peerAdmin = admin;
        this.isTerminated = false;
    }

    public void run() {
        while (!this.isTerminated) {
            try {
                Socket neighborSocket = this.listener.accept();
                PeerHandler neighborHandler = new PeerHandler(neighborSocket, this.peerAdmin);
                new Thread(neighborHandler).start();
                String neighborAddress = neighborSocket.getInetAddress().toString();
                int neighborPort = neighborSocket.getPort();
            } catch (SocketException e) {
                break;
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
