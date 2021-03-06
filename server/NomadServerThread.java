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
    private Boolean loginStatus = false;
    private DataInputStream  streamIn  =  null;
    private DataOutputStream streamOut = null;
    private NGrain threadGrain;
    public NSand threadSand;
    private Boolean runState = true;
    private Object runLock = new Object();
    private Object threadIDLock = new Object();


    public Boolean getRun() {
	synchronized (runLock) {
	    return threadSand.getRun();
	}
    }
	

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
	    server.removeByThreadID(THREAD_ID);
	    stop();
	}
    }
    
    public int getThreadID() {  
	synchronized (threadIDLock) {
	    return THREAD_ID;
	}
    }
    
    private Object loginStatusLock = new Object();

    public Boolean getLoginStatus() {
	//    	System.out.println("          getLoginStatus(" + loginStatus + ")");    
	synchronized (loginStatusLock) {
	    return loginStatus;
	}
    }
    
    public synchronized void setLoginStatus(Boolean status) {
    	// System.out.println("setLoginStatus(" + status + ")");
	synchronized (loginStatusLock) {
	    loginStatus = status;
	}
    }

    public int getAppID() {
    	return APP_ID;
    }
    
    public synchronized void setAppID(byte id) {
    	APP_ID = id;
    }
    
    public synchronized void setIP(String ip) {
    	IP = new String(ip);
    	// System.out.println("          setIP(" + IP + ")");
    }
    
    public String getIP() {
    	// System.out.println("          getIP(" + IP + ")");
    	return IP;
    }

    public synchronized void setUser(String u) {
    	// System.out.println("          setUser(" + u + ")");    
    	USER = new String(u);
    }
    
    public String getUser() {
    	// System.out.println("          getUser(" + USER + ")");
    	return USER;
    }

   
    public synchronized void run() {  
	Boolean myRun = true;
	System.out.println("Server Thread " + THREAD_ID + " running.");
	while (myRun && threadSand.getRun()) {
	    try {  
		byte tByte = streamIn.readByte();
		if (tByte > 0) {
		    NGlobals.dtPrint("THREAD " + getThreadID() + " :  getGrain:  read byte:  " + tByte);
		    threadGrain = threadSand.getGrain(tByte);
		    //threadGrain.print();
		    NGlobals.dtPrint("THREAD " + getThreadID() + " :  server->handle()");
		    if (threadSand.getRun()) {
			NGlobals.dtPrint("THREAD " + getThreadID() + ": CAN RUN");
			server.handle(THREAD_ID, threadGrain);
		    }
		    else {
			NGlobals.dtPrint("THREAd " + getThreadID() + " : NO RUN - REMOVE");
			myRun = false;
			threadSand.setRun(false);
			server.removeByThreadID(THREAD_ID);
			// interrupt();
			stop();
		    }
		}
	    }
	    catch(IOException ioe) {  
		System.out.println("LOG, THREAD " + THREAD_ID + " ERROR reading: " + ioe.getMessage());
		myRun = false;
		threadSand.setRun(false);
		server.removeByThreadID(THREAD_ID);
		// interrupt();
		stop();
	    }
	}
    }

    public synchronized void open() throws IOException {  
	NGlobals.sPrint("NomadServerThread:open()");

	threadSand = new NSand(socket);
	threadSand.openSocketStreams();

	streamIn = threadSand.getInStream();
	streamOut = threadSand.getOutStream();
    }

    public synchronized void close() throws IOException {  
	threadSand.closeSocketStreams();
	threadSand.disConnectSocket();
    	// if (socket != null)    
	//     socket.close();
	// if (streamIn != null)  
	//     streamIn.close();
	// if (streamOut != null) 
	//     streamOut.close();
	// threadSand.disconnect();
    }

}

