import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.Object.*;
import nomads.v210.*;

public class NomadServer implements Runnable {  
    private NomadServerThread clients[] = new NomadServerThread[3000];
    private NomadServerThread currentClient;
    private short clientThreadNum[] = new short[100000];
    private String IPsLoggedIn[] = new String[1000];
    private String users[] = new String[1000];
    
    private ServerSocket server = null;
    private Thread       thread = null;
    private int clientCount = 0;
    private int IPCount = 0;
    private int userCount = 0;
    private int eventNum = 0;
    private static int debugLine = 0;
    private static String[] children;
    private static Boolean requireLogin = false;
    private Calendar cal;
    long nowT,appT,diffT,lagT;

    int iDay;
    FileOutputStream out; // declare a file output object
    PrintStream p; // declare a print stream object

    // xxx
    NGrain myGrain;
    
    public NomadServer(int port) {  	    
	for (int i=0;i<100000;i++) {
	    clientThreadNum[i] = -1;
	}
	for (int i=0;i<1000;i++) {
	    IPsLoggedIn[i] = null;
	    users[i] = null;
	}
    	try {  
	    NGlobals.sPrint("  Binding to port " + port + ", please wait  ...");
	    server = new ServerSocket(port);  
	    NGlobals.sPrint("  Server started: " + server);
	    start(); 
	}
	catch(IOException ioe)  {  	   
	    NGlobals.sPrint("  Can not bind to port " + port + ": " + ioe.getMessage());
	    ioe.printStackTrace();
	    System.exit(1); 
	}
    }

    public void run()  {  
    	while (thread != null) {  
	    try {  
		NGlobals.sPrint("  Waiting for a client ..."); 
		addThread(server.accept()); 
	    }
	    catch(IOException ioe)  {  
		NGlobals.sPrint("  Server accept error: " + ioe); stop(); }
	}
    }
    
    public void start()  {  
    	if (thread == null)  {  
	    thread = new Thread(this); 
	    thread.start();
	}
    }

    public void stop() {  
    	if (thread != null)  {  
	    thread.stop(); 
	    thread = null;
	}
    }

    public synchronized void handle(int THREAD_ID, NGrain myGrain)  {  
	String tUser, IP, tempString;
	int loginStatus = 0;
	int cNum = -1;
	int cIPNum = -1;
	int nBlocks; //number of "blocks" of data
	int tCNum;
	int incIntData[] = new int[1000];
	byte incByteData[] = new byte[1000];

	byte incAppCmd, incAppDataType;
	int incAppDataLen;
	byte incAppID;

	NGrain inGrain;

    	NGlobals.sPrint("-----------------------------------------------------[" + debugLine++ + "]");
    	
		
	// =====================================================================================================
	// BEGIN Main data routing code
	// =====================================================================================================

	// Do the following for EACH client

	// 1 ---- READ ---------------------------------------
	// ---------------------------------------------------

	NGlobals.sPrint("===== READING =====");

	// Read in relevant SAND header info
	incAppID = myGrain.appID;
	incAppCmd = myGrain.command;
	incAppDataType = myGrain.dataType;
	incAppDataLen = myGrain.dataLen;

	// Print out at the SERVER level
	NGlobals.sPrint("appID: " + incAppID);
	NGlobals.sPrint("command: " + incAppCmd);
	NGlobals.sPrint("dataType: " + incAppDataType);
	NGlobals.sPrint("dataLen: " + incAppDataLen);

	// Get client number of inc client
	tCNum = clientThreadNum[THREAD_ID];
	currentClient = clients[tCNum];

	// REGISTER the client's appID with the SERVER client thread
	if (clients[tCNum].getAppID() == -1) {
	    NGlobals.sPrint("===== REGISTERING =====");

	    NGlobals.sPrint("  Setting client[" + tCNum + "] incAppID to: " + incAppID);
	    clients[tCNum].setAppID(incAppID);
	}

	// Read each specific BLOCK (INT OR BYTE)
	if (incAppDataType == NDataType.INT) {
	    for (int j = 0; j < incAppDataLen; j++) {
		//NGlobals.sPrint("INT: " + myGrain.iArray[j]);
	    }
	}
	if (incAppDataType == NDataType.BYTE) {
	    for (int j = 0; j < incAppDataLen; j++) {
		// NGlobals.sPrint("BYTE: " + (char) myGrain.bArray[j]);
	    }
	}
    

	// 2 ---- WRITE ---------------------------------------
	// ---------------------------------------------------

	// For each client SEND ALL DATA

	NGlobals.sPrint("===== WRITING =====");

	for (int c = 0; c < clientCount; c++) {
	
	    // Get the client off the master list
	    currentClient = clients[c];
	    NGlobals.sPrint("===> client[" + c + "] w/ id = " + currentClient.getAppID());

	    // Extra step for SOUND_SWARM_DISPLAY: need to send THREAD_ID

	    if (incAppID == NAppID.SOUND_SWARM) {
		if (currentClient.getAppID() == NAppID.SOUND_SWARM_DISPLAY) {
		    NGlobals.sPrint("Sending SOUND_SWARM:THREAD_ID to ---> SOUND_SWARM_DISPLAY: " + THREAD_ID);
		    int[] x = new int[1];
		    x[0] = THREAD_ID;
		    currentClient.threadSand.sendGrain(myGrain.appID, NCommand.SEND_THREAD_ID, NDataType.INT, 1, x);
		}
	    }
	    
	    NGlobals.sPrint("===> sending PASSTHROUGH network data");
	    myGrain.print();
	    // Write the data out
	    currentClient.threadSand.sendGrain(myGrain);

	}   
	// END --------------------------------------------------------------------
	NGlobals.sPrint("handle(DONE) " + THREAD_ID + ":" + myGrain.appID);

	// Free up memory
	if (myGrain != null) {
	    myGrain = null;
	}
    }

    
    // =====================================================================================================
    // END main data routing code --- handle() fn
    // =====================================================================================================
    
    public synchronized void remove(int THREAD_ID) {  
    	int pos = clientThreadNum[THREAD_ID];
	int tID;
	if (pos >= 0) {  
	    clientThreadNum[THREAD_ID] = -1;
	    NomadServerThread toTerminate = clients[pos];

	    if (pos < clientCount-1) {
		NGlobals.sPrint("Removing client thread " + THREAD_ID + " at " + pos);
		for (int i = pos+1; i < clientCount; i++) {
		    clients[i-1] = clients[i];
		    tID = clients[i-1].getThreadID();
		    clientThreadNum[tID] = (short)(i-1);
		}
	    }
	    clientCount--;
	    try {  
		toTerminate.close(); 
		toTerminate.stop(); 
	    }
	    catch(IOException ioe) {  
		NGlobals.sPrint("  Error closing thread: " + ioe); 
	    }
        }
    }
    
    private  synchronized void addThread(Socket socket) {  
	int tID;
    	NGlobals.sPrint("addThread(" + socket + ")");

	String IP = new String((socket.getInetAddress()).getHostAddress());

    	NGlobals.sPrint("     clientCount = " + clientCount);
    	NGlobals.sPrint("     clients.length = " + clients.length);


	

    	if (clientCount < clients.length) {  
	    NGlobals.sPrint("  Client accepted: " + socket);
	    NGlobals.sPrint("  IP = " + IP);

	    clients[clientCount] = new NomadServerThread(this, socket);
	    try {  
		clients[clientCount].open(); 
		clients[clientCount].setIP(IP);
		tID = clients[clientCount].getThreadID();
		clientThreadNum[tID] = (short)clientCount;
		clients[clientCount].start();  

		NGlobals.sPrint("  Client added to lookup array at slot # " + clientCount);
		clientCount++; 
	    }
	    catch(IOException ioe) {  
		NGlobals.sPrint("    Error opening thread: " + ioe); 
	    } 
        }
	else
	    NGlobals.sPrint("  Client refused: maximum " + clients.length + " reached.");
    }
    
    public static void main(String args[]) {  
    	NomadServer server = null;
	if (args.length != 1)
	    NGlobals.sPrint("Usage: java NomadServer port");
	else
	    server = new NomadServer(Integer.parseInt(args[0]));
    }

}

