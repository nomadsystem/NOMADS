import java.net.*;
import java.io.*;

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

    public void sendNetUTF(String msg) {   
    	try {  
	    	streamOut.writeUTF(msg);
		streamOut.flush();
	    }
		catch(IOException ioe) {  
			System.out.println(THREAD_ID + " ERROR sending UTF: " + ioe.getMessage());
			server.remove(THREAD_ID);
			stop();
	    }
    }

    public void sendNetInt(int data) {

	try {
	    streamOut.writeInt(data);
	    streamOut.flush();
	}
	catch(IOException ioe) {  
	    System.out.println(THREAD_ID + " ERROR writing INT: " + ioe.getMessage());
	    server.remove(THREAD_ID);
	    stop();
	}
    }

    public void sendNetByte(byte data) {

	try {
	    streamOut.writeByte(data);
	    streamOut.flush();
	}
	catch(IOException ioe) {  
	    System.out.println(THREAD_ID + " ERROR writing BYTE: " + ioe.getMessage());
	    server.remove(THREAD_ID);
	    stop();
	}
    }

   
    public int getNetInt() {
	int tInt = 0;
	try {
	    tInt = streamIn.readInt();
	}
	catch(IOException ioe) {  
	    System.out.println(THREAD_ID + " ERROR reading INT: " + ioe.getMessage());
	    server.remove(THREAD_ID);
	    stop();
	}
	return tInt;
    }

    public byte getNetByte() {
	byte tByte = 0;
	try {
	    tByte = streamIn.readByte();
	}
	catch(IOException ioe) {  
	    System.out.println(THREAD_ID + " ERROR reading INT: " + ioe.getMessage());
	    server.remove(THREAD_ID);
	    stop();
	}
	return tByte;
    }

    public void run() {  
    	System.out.println("Server Thread " + THREAD_ID + " running.");
		while (true) {  
			try {  
			    server.handle(THREAD_ID, streamIn.readByte());
	       	        }
			catch(IOException ioe) {  
				System.out.println(THREAD_ID + " ERROR reading: " + ioe.getMessage());
				server.remove(THREAD_ID);
				stop();
		    }
	    }
    }

    public void open() throws IOException {  
    	streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
	streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
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

