package src;
public class RemotePeerInfo {
	public String pId;
	public String pAddress;
	public int pPort;
	public int cFile;

	public RemotePeerInfo(String peerId, String peerAddress, String peerPort, String containsFile) {
		this.pId = peerId;
		this.pAddress = peerAddress;
		this.pPort = Integer.parseInt(peerPort);
		this.cFile = Integer.parseInt(containsFile);
	}
	
}
