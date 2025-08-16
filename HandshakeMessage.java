package src;

import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;


public class HandshakeMessage 
{
	private String peerID;
    private String headerHandshake;
    
    //Initializing the peer id and the directory for handshake
    public HandshakeMessage(String peerID) 
    {
        this.headerHandshake = "P2PFILESHARINGPROJ";
        this.peerID = peerID;
    }
    
    //get function to return the peerID
    public String getPeerID()
    {
        return this.peerID;
    }

    //Writing the handshake messages to buffered output stream
    public byte[] buildHandShakeMessage() 
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //using try catch block for handling exception
        try 
        {
            outputStream.write(this.headerHandshake.getBytes(StandardCharsets.UTF_8));
            outputStream.write(new byte[10]);
            outputStream.write(this.peerID.getBytes(StandardCharsets.UTF_8));
        } 
        catch(IOException e)
        {
        	System.out.println(e);
        }
        catch(Exception e) 
        {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    //reading the handshake messages
    public void readHandShakeMessage(byte[] msgHandshake){
        String messageStr = new String(msgHandshake,StandardCharsets.UTF_8);
        this.peerID = messageStr.substring(28,32);
    }
}
