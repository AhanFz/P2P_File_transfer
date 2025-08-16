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

public class ChokeHandler implements Runnable 
{
    private int interval;
    private int countPrefNeighbours;
    private PeerAdmin peerAdmin;
    private Random randomObj = new Random();
    private ScheduledFuture<?> task = null;
    private ScheduledExecutorService scheduler = null;
   
    ChokeHandler(PeerAdmin padmin) 
    {
        this.peerAdmin = padmin;
        this.countPrefNeighbours = padmin.getNoOfPreferredNeighbors();
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.interval = padmin.getUnchockingInterval();
        
    }

    //this function initiates the choking by scheduling based on intervals
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
    	int count = 0, iteratorCount;
        try {
        	//creating a list to get the interested neighbors list for choking and unchoking
        	List<String> isInterested = new ArrayList<String>(this.peerAdmin.getInterestedList());
            HashSet<String> listUnchoked = new HashSet<>(this.peerAdmin.getUnchokedList());
            HashSet<String> buffList = new HashSet<>();
            int val;
            if (isInterested.size() > 0) {val =1;}
            else { val =0;}
            switch(val){
            	case 1: 
            		
            		//finding the number of neighbors between peers who are interested and preferred neighbor list
            		iteratorCount = Math.min(this.countPrefNeighbours, isInterested.size());
            		
                    if (this.peerAdmin.getCompletedPieceCount() == this.peerAdmin.getPieceCount()) 
                    {
                        for (int i = 0; i < iteratorCount; i++) 
                        {
                        	//randomly selecting the next peer based on the interested peers list
                            String nextPeer = isInterested.get(this.randomObj.nextInt(isInterested.size()));
                            PeerHandler nextPeerHandler = this.peerAdmin.getPeerHandler(nextPeer);
                            
                            while (buffList.contains(nextPeer)) 
                            {
                                nextPeer = isInterested.get(this.randomObj.nextInt(isInterested.size()));
                                nextPeerHandler = this.peerAdmin.getPeerHandler(nextPeer);
                            }
                            if (!listUnchoked.contains(nextPeer)) 
                            {
                                if (this.peerAdmin.getOptimisticUnchokedPeer() == null
                                        || this.peerAdmin.getOptimisticUnchokedPeer().compareTo(nextPeer) != 0) 
                                {
                                    nextPeerHandler.sendUnChokedMessage();
                                }
                            } 
                            else 
                            {
                                listUnchoked.remove(nextPeer);
                            }
                            buffList.add(nextPeer);
                            nextPeerHandler.resetDownloadRate();
                        }
                    } 
                    else 
                    {
                    	//creating a map to get the download rate for all the peers who are passing the data
                        Map<String, Integer> mapDownloads = new HashMap<>(this.peerAdmin.getDownloadRates());
                        Map<String, Integer> rates = mapDownloads.entrySet().stream()
                                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                        Iterator<Map.Entry<String, Integer>> iteratorObj = rates.entrySet().iterator();
                        
                        while (count < iteratorCount && iteratorObj.hasNext()) 
                        {
                            Map.Entry<String, Integer> entryMap = iteratorObj.next();
                            if (isInterested.contains(entryMap.getKey())) 
                            {
                                PeerHandler nextPeerHandler = this.peerAdmin.getPeerHandler(entryMap.getKey());
                                if (!listUnchoked.contains(entryMap.getKey())) 
                                {
                                    String optUnchoke = this.peerAdmin.getOptimisticUnchokedPeer();
                                    if (optUnchoke == null || optUnchoke.compareTo(entryMap.getKey()) != 0) 
                                    {
                                        nextPeerHandler.sendUnChokedMessage();
                                    }
                                } 
                                else {
                                    listUnchoked.remove(entryMap.getKey());
                                }
                                buffList.add(entryMap.getKey());
                                nextPeerHandler.resetDownloadRate();
                                count++;
                            }
                        }
                    }
                    this.peerAdmin.updateUnchokedList(buffList);
                    //updating the neighbor list by creating new arraylist 
                    int BuffSize = buffList.size();
                    if(BuffSize > 0)
                    {
                        this.peerAdmin.getLogger().changePreferredNeighbors(new ArrayList<>(buffList));
                    }
                    for (String peer : listUnchoked) 
                    {
                        PeerHandler nextPeerHandler = this.peerAdmin.getPeerHandler(peer);
                        nextPeerHandler.sendChokedMessage();
                    }
            		break;
            		
            	case 0: 
            		this.peerAdmin.resetUnchokedList();
                    for (String peer : listUnchoked) 
                    {
                        PeerHandler nextPeerHandler = this.peerAdmin.getPeerHandler(peer);
                        nextPeerHandler.sendChokedMessage();
                    }
                    if(this.peerAdmin.checkIfAllPeersAreDone()) 
                    {
                        this.peerAdmin.cancelChokes();
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
