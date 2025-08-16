package src;

import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.nio.charset.*;
import java.util.*;
import java.nio.*;
import java.lang.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.*;
import java.util.concurrent.*;

public class OptimisticUnchokeHandler implements Runnable 
{
    private int interval;
    private PeerAdmin peerAdminInstance;
    private Random randomObj = new Random();
    private ScheduledFuture<?> task = null;
    private ScheduledExecutorService scheduler = null;
    
    OptimisticUnchokeHandler(PeerAdmin padmin) 
    {
        this.peerAdminInstance = padmin;
        this.interval = padmin.getOptimisticUnchockingInterval();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }
    
    //this function initiates the unchoking by scheduling based on intervals
    public void startJob() 
    {
        this.task = this.scheduler.scheduleAtFixedRate(this, 6, this.interval, TimeUnit.SECONDS);
    }

    //function to shutdown the scheduler
    public void cancelJob() 
    {
        this.scheduler.shutdownNow();
    }
    
    public void run() {
        try 
        {
        	//Fetching a list of all the peers which are interested
        	List<String> isInterested = new ArrayList<String>(this.peerAdminInstance.getInterestedList());
            String optUnchoked;
            optUnchoked= this.peerAdminInstance.getOptimisticUnchokedPeer();
            isInterested.remove(optUnchoked);
            int length, val;
            length= isInterested.size();
            if (length>0) { val =1; }
            else {	val =0; }
            switch(val)
            {
            	case 1:
            		//getting the next interested peer randomly from the list
            		String nextPeer;
            		nextPeer= isInterested.get(randomObj.nextInt(length));
                    while (this.peerAdminInstance.getUnchokedList().contains(nextPeer)) {
                        isInterested.remove(nextPeer);
                        length--;
                        if(length > 0) 
                        {
                            nextPeer = isInterested.get(randomObj.nextInt(length));
                        }
                        else 
                        {
                            nextPeer = null;
                            break;
                        }
                    }
                    this.peerAdminInstance.setOptimisticUnchokdPeer(nextPeer);
                    
                    if(nextPeer != null) 
                    {
                        PeerHandler nextPeerHandler = this.peerAdminInstance.getPeerHandler(nextPeer);
                        nextPeerHandler.sendUnChokedMessage();
                        this.peerAdminInstance.getLogger()
                                .changeOptimisticallyUnchokedNeighbor(this.peerAdminInstance.getOptimisticUnchokedPeer());
                    } 
                    if (optUnchoked != null && !this.peerAdminInstance.getUnchokedList().contains(optUnchoked)) 
                    {
                        this.peerAdminInstance.getPeerHandler(optUnchoked).sendChokedMessage();
                    }  
            		break;
            		
            	case 0:
            		String currPeerOpt = this.peerAdminInstance.getOptimisticUnchokedPeer();
                    this.peerAdminInstance.setOptimisticUnchokdPeer(null);
                    if (currPeerOpt != null && !this.peerAdminInstance.getUnchokedList().contains(currPeerOpt)) 
                    {
                        PeerHandler nextPeerHandler = this.peerAdminInstance.getPeerHandler(currPeerOpt);
                        nextPeerHandler.sendChokedMessage();
                    }
                    if(this.peerAdminInstance.checkIfAllPeersAreDone()) 
                    {
                        this.peerAdminInstance.cancelChokes();
                    }
            		break; 
            }	  
        } 
        
        //handling exception
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
}
