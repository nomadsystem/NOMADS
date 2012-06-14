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

    NSand outSand;
    NSand inSand;
    
    public NomadServer(int port) {  	    
	for (int i=0;i<100000;i++) {
	    clientThreadNum[i] = -1;
	}
	for (int i=0;i<1000;i++) {
	    IPsLoggedIn[i] = null;
	    users[i] = null;
	}
    	try {  
	    globals.sPrint("  Binding to port " + port + ", please wait  ...");
	    server = new ServerSocket(port);  
	    globals.sPrint("  Server started: " + server);
	    start(); 
	}
	catch(IOException ioe)  {  	   
	    globals.sPrint("  Can not bind to port " + port + ": " + ioe.getMessage());
	    ioe.printStackTrace();
	    System.exit(1); 
	}

	outSand = new NSand();
	inSand = new NSand();

    }

    public void run()  {  
    	while (thread != null) {  
	    try {  
		globals.sPrint("  Waiting for a client ..."); 
		addThread(server.accept()); 
	    }
	    catch(IOException ioe)  {  
		globals.sPrint("  Server accept error: " + ioe); stop(); }
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

    public synchronized void handle(int THREAD_ID, byte incAppID)  {  
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

	NGrain inGrain;

    	globals.sPrint("-----------------------------------------------------[" + debugLine++ + "]");
    	
		
	// =====================================================================================================
	// BEGIN Main data routing code
	// =====================================================================================================

	// Do the following for EACH client

	// 1 ---- READ ---------------------------------------
	// ---------------------------------------------------

	globals.sPrint("reading data ...");


	// Get client number of inc client
	tCNum = clientThreadNum[THREAD_ID];
	currentClient = clients[tCNum];

	// Read in the COMMAND
	inSand.setSock(currentClient.getSock());
	inSand.openSocketStreams();
	
	NGrain myGrain = inSand.getGrain(incAppID); // by sending 

	// TOFIX:  change to getXXX acccessor functions

	incAppCmd = myGrain.command;
	incAppDataType = myGrain.dataType;
	incAppDataLen = myGrain.dataLen;

	globals.sPrint("appID: " + incAppID);
	globals.sPrint("command: " + incAppCmd);
	globals.sPrint("dataType: " + incAppDataType);
	globals.sPrint("dataLen: " + incAppDataLen);

	// Read each specific BLOCK
	if (incAppDataType == dataType.INT) {
	    for (int j = 0; j < incAppDataLen; j++) {
		globals.sPrint("INT: " + myGrain.iArray[j]);
	    }
	}
		
	if (incAppDataType == dataType.BYTE) {
	    for (int j = 0; j < incAppDataLen; j++) {
		globals.sPrint("BYTE: " + (char) myGrain.bArray[j]);
	    }
	}
    

	// 2 ---- WRITE ---------------------------------------
	// ---------------------------------------------------

	// For each client SEND ALL DATA

	globals.sPrint("sending data ...");

	for (int c = 0; c < clientCount; c++) {
	    
	    // Get the client off the master list
	    currentClient = clients[c];

	    myGrain.print();

	    // Write the data out
	    outSand.setSock(currentClient.getSock());
	    outSand.openSocketStreams();

	    outSand.sendGrain(myGrain);

	}   
	// END --------------------------------------------------------------------
	globals.sPrint("handle(DONE) " + THREAD_ID + ":" + myGrain.appID);

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
		globals.sPrint("Removing client thread " + THREAD_ID + " at " + pos);
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
		globals.sPrint("  Error closing thread: " + ioe); 
	    }
        }
    }
    
    private  synchronized void addThread(Socket socket) {  
	int tID;
    	globals.sPrint("addThread(" + socket + ")");

	String IP = new String((socket.getInetAddress()).getHostAddress());

    	globals.sPrint("     clientCount = " + clientCount);
    	globals.sPrint("     clients.length = " + clients.length);


    	if (clientCount < clients.length) {  
	    globals.sPrint("  Client accepted: " + socket);
	    globals.sPrint("  IP = " + IP);

	    clients[clientCount] = new NomadServerThread(this, socket);
	    try {  
		clients[clientCount].open(); 
		clients[clientCount].setIP(IP);
		tID = clients[clientCount].getThreadID();
		clientThreadNum[tID] = (short)clientCount;
		clients[clientCount].start();  

		globals.sPrint("  Client added to lookup array at slot # " + clientCount);
		clientCount++; 
	    }
	    catch(IOException ioe) {  
		globals.sPrint("    Error opening thread: " + ioe); 
	    } 
        }
	else
	    globals.sPrint("  Client refused: maximum " + clients.length + " reached.");
    }
    
    public static void main(String args[]) {  
    	NomadServer server = null;
	if (args.length != 1)
	    globals.sPrint("Usage: java NomadServer port");
	else
	    server = new NomadServer(Integer.parseInt(args[0]));
    }

}

