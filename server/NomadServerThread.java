import java.net.*;
import java.io.*;
import nomads.v210.*;

public class NomadServerThread extends Thread
{  private NomadServer       server    = null;
    private Socket           socket    = null;
    private int              THREAD_ID        = -1;
    private int				 APP_ID    = -1;
    private String			IP;
    private String			USER;
    private int loginStatus = 0;
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
    
    public int getLoginStatus() {
    	System.out.println("          getLoginStatus(" + loginStatus + ")");    
    	return loginStatus;
    }
    
    public void setLoginStatus(byte status) {
    	System.out.println("setLoginStatus(" + status + ")");
    
    	loginStatus = status;
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
			    threadGrain = threadSand.getGrain(streamIn.readByte());
			    threadGrain.print();
			    server.handle(THREAD_ID, threadGrain);
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

