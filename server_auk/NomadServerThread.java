import java.net.*;
import java.io.*;
import nomads.v210_auk.*;

public class NomadServerThread extends Thread
{  private NomadServer       server    = null;
    private Socket           socket    = null;
    private int              THREAD_ID        = -1;
    private int				 APP_ID    = -1;
    private String			IP;
    private String			USER;
    private byte buttonInitStatus = 0;
    private DataInputStream  streamIn  =  null;
    private DataOutputStream streamOut = null;
    private NGrain threadGrain;
    public NSand threadSand;

    class MyException extends Exception {
	public MyException(String msg){
	    super(msg);
	}
    }

    public NomadServerThread(NomadServer _server, Socket _socket) {  
    	super();
		server = _server;
		socket = _socket;
		THREAD_ID     = socket.getPort();
    }
    
    public Socket getSock() {
	return socket;
    }

    public void sendUTF(String msg) {   
    	try {  
	    streamOut.writeUTF(msg);
	    streamOut.flush();
	}
	catch(IOException ioe) {  
	    System.out.println(THREAD_ID + " ERROR sending: " + ioe.getMessage());
	    server.remove(THREAD_ID);
	    stop();
	}
    }
    
    public int getThreadID() {  
    	return THREAD_ID;
    }
    
    public byte getButtonInitStatus() {
    	System.out.println("          getLoginStatus(" + buttonInitStatus + ")");    
    	return buttonInitStatus;
    }
    
    public void setButtonInitStatus(byte status) {
    	System.out.println("setLoginStatus(" + status + ")");
    	buttonInitStatus = status;
    }

    public int getAppID() {
    	return APP_ID;
    }
    
    public void setAppID(byte id) {
    	APP_ID = id;
    }
    
    public void setIP(String ip) {
    	IP = new String(ip);
    	System.out.println("          setIP(" + IP + ")");
    }
    
    public String getIP() {
    	System.out.println("          getIP(" + IP + ")");
    	return IP;
    }

    public void setUser(String u) {
    	System.out.println("          setUser(" + u + ")");    
    	USER = new String(u);
    }
    
    public String getUser() {
    	System.out.println("          getUser(" + USER + ")");
    	return USER;
    }

   
    public synchronized void run() {  
    	System.out.println("Server Thread " + THREAD_ID + " running.");
		while (true) {
			try {  
			    NGlobals.sPrint("\n-----------------\nTHREAD:  getGrain:  waiting for data...\n");
			    byte tByte = streamIn.readByte();
			    if (tByte != 0) {
				NGlobals.sPrint("\nTHREAD:  getGrain:  read byte:  " + tByte);
				threadGrain = threadSand.getGrain(tByte);
				//threadGrain.print();
				NGlobals.sPrint("THREAD:  server->handle()\n");
				server.handle(THREAD_ID, threadGrain);
			    }
			    else {
				NGlobals.sPrint("\n-----\nTHREAD: " + THREAD_ID + " WARNING: 0 byte read\n");
			    }
	       	        }
			catch(IOException ioe) {  
				System.out.println(THREAD_ID + " ERROR reading: " + ioe.getMessage());
				server.remove(THREAD_ID);
				stop();
		    }
	    }
    }

    public void open() throws IOException {  
	NGlobals.sPrint("NomadServerThread:open()");

	threadSand = new NSand(socket);
	threadSand.openSocketStreams();

	streamIn = threadSand.getInStream();
	streamOut = threadSand.getOutStream();
    }

    public void close() throws IOException {  
    	if (socket != null)    
    		socket.close();
		if (streamIn != null)  
			streamIn.close();
		if (streamOut != null) 
			streamOut.close();
    }
}

