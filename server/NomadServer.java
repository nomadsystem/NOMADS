import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.Object.*;
import nomads.v210.*;

public class NomadServer implements Runnable {  
    private NomadServerThread clients[] = new NomadServerThread[5000];
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
    byte currentPollType;

    private byte modStates[] = new byte[6];
    int numModStates;
    
    public NomadServer(int port) {  	    
	for (int i=0;i<100000;i++) {
	    clientThreadNum[i] = -1;
	}
	for (int i=0;i<1000;i++) {
	    IPsLoggedIn[i] = null;
	    users[i] = null;
	}

	numModStates = (int)(NCommand.MOD_STATE_END-NCommand.MOD_STATE_START)+1;
	NGlobals.dtPrint("  numModStates = " + numModStates);

	// Use this now for "button states"
	for (int i=0;i<numModStates;i++) {
	    modStates[i] = 0;
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

   public synchronized void getFiles(String iDir) {
	File dir = new File(iDir); 
	children = dir.list(new DirFilter(".")); 
	if (children == null) { // Either dir does not exist or is not a directory 
	} else { 
	    for (int i=0; i<children.length; i++) { // Get filename of file or directory 
		String filename = children[i]; 
		NGlobals.sPrint("Getting file:  " + filename); 
	    } 
	} // It is also possible to filter the list of returned files. 
	// This example does not return any files that start with `.'. 

	// FilenameFilter filter = new FilenameFilter() { 
	// 	public boolean accept(File dir, String name) {
	// 	    return !name.startsWith("."); 
	// 	} 
	//     };
	
	// children = dir.list(filter); 
    }

    private synchronized short checkIP (String IP) {
    	NGlobals.sPrint("          checkIP(" + IP + ")");
	
    	for (int i = 0; i < IPCount; i++) {
	    if (IPsLoggedIn[i] == null)
		return (short)-1;
	    if (IPsLoggedIn[i].equals(IP))
		return (short)i;
	}
	return (short)-1;
    }

    class DirFilter implements FilenameFilter {
	String afn;
	DirFilter(String afn) { this.afn = afn; }
	public boolean accept(File dir, String name) {
	    // Strip path information:
	    String f = new File(name).getName();
	    return f.indexOf(afn) != -1;
	}
    }

    // ================================================================
    //    handle ( THREAD_ID , grain )
    // ================================================================

    public synchronized void handle(int THREAD_ID, NGrain myGrain)  {  
	String tUser, IP, tempString;
	Boolean tLoginStatus = false;
	int cNum = -1;
	int cIPNum = -1;
	int nBlocks; //number of "blocks" of data
	int tCNum;
	int incIntData[] = new int[1000];
	byte incByteData[] = new byte[1000];

	byte incAppCmd, incAppDataType;
	int incAppDataLen;
	byte incAppID;

	int modStateOffset;

	byte tCommand;
	NGrain inGrain;

    	NGlobals.sPrint("-----------------------------------------------------[" + debugLine++ + "]");
    	
	// Do the following for EACH client

	// 1 ---- READ ------------------------------------------------------------------
	// ------------------------------------------------------------------------------

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

	// Thread admin stuff ---------------------------------------------------------------------

	// Get client number of inc client
	tCNum = clientThreadNum[THREAD_ID];

	if (tCNum < 0) {
	    NGlobals.sPrint("   ERROR:  client thread not found.");
	    // TODO:  send the bye command!!!
	    remove(THREAD_ID);
	    return;
	}

	currentClient = clients[tCNum];

	// Login and THREAD registration ----------------------------------------------------------

	// 1: check if client thread is registered
	//    if not reg, REGISTER the client's appID with the SERVER client thread
	if (currentClient.getAppID() == -1) {
	    NGlobals.sPrint("===== REGISTERING =====");
	    NGlobals.sPrint("  Setting client[" + tCNum + "] incAppID to: " + incAppID);
	    currentClient.setAppID(incAppID);

	    // Send button states to the ICP
	    if (incAppID == NAppID.INSTRUCTOR_PANEL) {
		for (int i=0; i<numModStates; i++) {
		    tCommand = (byte)(NCommand.MOD_STATE_START+i);
		    byte[] dx = new byte[1];
		    dx[0] = modStates[i];
		    NGlobals.dtPrint("Sending button state (" + tCommand + "/" + dx[0] + ") to ICP " + currentClient.getThreadID());
		    currentClient.threadSand.sendGrain(NAppID.SERVER, tCommand, NDataType.UINT8, 1, dx);
		}
	    }
	}

	// 2: check login
	//      only the LOGIN app can log you in
	//      if you're not logged in, you get booted

	tLoginStatus = currentClient.getLoginStatus();
	if ((incAppID == NAppID.BINDLE) && (incAppCmd == NCommand.LOGIN)) {
	    if (tLoginStatus == true) {
		// send back "you're already logged in" message / LOGIN_STATUS w/ value = 2
		NGlobals.sPrint("  LOGIN client [" + tCNum + "] already logged in.\n" + incAppID);

		byte[] dx = new byte[1];
		dx[0] = 2;
		currentClient.threadSand.sendGrain(NAppID.SERVER, NCommand.LOGIN_STATUS, NDataType.UINT8, 1, dx);
	    }
	    else {
		// Log the client in
		String tString = new String(myGrain.bArray);
		NGlobals.sPrint("Got username: " + tString);
		clients[tCNum].setUser(tString);

		// Set new login status 
		clients[tCNum].setLoginStatus(true);
		tLoginStatus = currentClient.getLoginStatus();

		NGlobals.sPrint("  LOGIN client [" + tCNum + "] logging in, sending back confirmation info.\n" + incAppID);

		// send back "successful login" message / LOGIN_STATUS w/ value = 1
		byte[] dx = new byte[1];
		dx[0] = 1;
		currentClient.threadSand.sendGrain(NAppID.SERVER, NCommand.LOGIN_STATUS, NDataType.UINT8, 1, dx);

		// Send button states (to the BINDLE -> implied by this coming from LOGIN)
		for (int i=0; i<numModStates; i++) {
		    tCommand = (byte)(NCommand.MOD_STATE_START+i);
		    dx[0] = modStates[i];
		    NGlobals.dtPrint("Sending button state to BINDLE (" + tCommand + "/" + dx[0] + ") to BINDLE " + currentClient.getThreadID());
		    currentClient.threadSand.sendGrain(NAppID.SERVER, tCommand, NDataType.UINT8, 1, dx);
		}
	    }
	}

	// Comment this out to turn back on login : LOGIN TOGGLE

	tLoginStatus = true;

	// Kick -------------------------------------------------------------------------------------

	// X:  if you're not the LOGIN app providing the correct info, you get booted here
	if (tLoginStatus == false) {
	    NGlobals.sPrint("   WARNING:  client THREAD NOT logged in.");
	    remove(THREAD_ID);
	    // TODO: send back some sand data re: login info
	    return;
	}

	// ====================================================================================
	//  At this point we're logged in, and we have your SAND data GRAIN
	// ====================================================================================

	NGlobals.sPrint("   client THREAD logged in.");

	// Grab IP (more for data logging)

	IP = currentClient.getIP();

	// Grab incoming button state messages from INSTRUCTOR_PANEL

	if (incAppID == NAppID.INSTRUCTOR_PANEL) {
	    if ( (incAppCmd >= NCommand.MOD_STATE_START) && (incAppCmd <= NCommand.MOD_STATE_END) ) {
		modStateOffset = (int)(incAppCmd-NCommand.MOD_STATE_START);
		modStates[modStateOffset] = myGrain.bArray[0];
	    }
	}

	// 2 ---- WRITE -----------------------------------------------------------------
	// ------------------------------------------------------------------------------

	// ====================================================================================================R
	// BEGIN Main data routing code
	//
	//    each    if (incAppID ==   )    block below corresponds to a single app's input data GRAIN
	//    depending on who is sending us data
	//    we cycle through all (or a subset of) clients and send data out
	//
	// ====================================================================================================R

	NGlobals.sPrint("===== WRITING =====");

	// Send out any messages from the ICP

	if (incAppID == NAppID.INSTRUCTOR_PANEL) {
	    for (int c = 0; c < clientCount; c++) {
		currentClient = clients[c];
		if (currentClient.getAppID() == NAppID.BINDLE) {
		    NGlobals.dtPrint("Sending button state to BINDLE " + currentClient.getThreadID());
		    // myGrain.print();
		    currentClient.threadSand.sendGrain(myGrain);
		}
	    }
	}

	if (incAppID == NAppID.TEACHER_POLL) {
	    currentPollType = incAppCmd;
	}

	if ((incAppID == NAppID.STUDENT_POLL) && (incAppCmd != currentPollType)) {
	    return;
	}

	// Send bulk data to BINDLE
	
	if ( (incAppID == NAppID.DISCUSS_PROMPT) || 
	     (incAppID == NAppID.CLOUD_PROMPT) || 
	     //	     (incAppID == NAppID.POLL_PROMPT) ||
	     (incAppID == NAppID.INSTRUCT_EMRG_SYNTH_PROMPT) || 
	     //	     (incAppID == NAppID.POLL_PROMPT) ||
	     (incAppID == NAppID.DISCUSS) ||
	     (incAppID == NAppID.INSTRUCTOR_DISCUSS) ||
	     (incAppID == NAppID.TEACHER_POLL) ||
	     (incAppID == NAppID.INSTRUCTOR_SEQUENCER) ) {

	    for (int c = 0; c < clientCount; c++) {
		currentClient = clients[c];
		if ( currentClient.getAppID() == NAppID.BINDLE) {
		    NGlobals.dtPrint("Sending bulk data to BINDLE " + currentClient.getThreadID());
		    // myGrain.print();
		    currentClient.threadSand.sendGrain(myGrain);
		}
	    }
	}

	// Send bulk data to INSTRUCTOR_PANEL

	if ( (incAppID == NAppID.CLOUD_CHAT) ||
	     (incAppID == NAppID.STUDENT_POLL) ||
	     //	     (incAppID == NAppID.POLL_PROMPT) ||
	     (incAppID == NAppID.TEACHER_POLL) ||

	     (incAppID == NAppID.STUDENT_SEQUENCER) ||
	     (incAppID == NAppID.DISCUSS) ||
	     (incAppID == NAppID.INSTRUCTOR_DISCUSS) ||
	     (incAppID == NAppID.DISCUSS_PROMPT) ||
	     (incAppID == NAppID.STUDENT_SAND_POINTER) ||
	     (incAppID == NAppID.STUD_EMRG_SYNTH) ) { 

	    for (int c = 0; c < clientCount; c++) {
		currentClient = clients[c];
		if ( currentClient.getAppID() == NAppID.INSTRUCTOR_PANEL) {
		    NGlobals.dtPrint("Sending bulk data to ICP " + currentClient.getThreadID());
		    // myGrain.print();
		    currentClient.threadSand.sendGrain(myGrain);
		}
	    }
	}
	    
	// Sound SWARM Routing Logic ==========================================================================R

	// TODO:  send 3 ints instead, 1st int is the THREAD_ID

	if (incAppID == NAppID.SOUND_SWARM) {
	    for (int c = 0; c < clientCount; c++) {
		
		// Get the client off the master list
		currentClient = clients[c];
		NGlobals.sPrint("===> client[" + c + "] w/ id = " + currentClient.getAppID());
		
		if (currentClient.getAppID() == NAppID.SOUND_SWARM_DISPLAY) {
		    NGlobals.sPrint("Sending SOUND_SWARM:THREAD_ID to ---> SOUND_SWARM_DISPLAY: " + THREAD_ID);
		    int[] x = new int[1];
		    x[0] = THREAD_ID;
		    currentClient.threadSand.sendGrain(myGrain.appID, NCommand.SEND_THREAD_ID, NDataType.INT, 1, x);
		    NGlobals.sPrint("Sending SOUND_SWARM: x/y coordinates\n");
		    currentClient.threadSand.sendGrain(myGrain);
		    // myGrain.print();

		}
	    }

	}


	// GENERIC Logic =============================================================
	//    -for each client SEND ALL DATA

	else if (1 == 0) {
	    NGlobals.sPrint("===> sending PASSTHROUGH network data");
	    for (int c = 0; c < clientCount; c++) {
		
		// Get the client off the master list
		currentClient = clients[c];
		NGlobals.sPrint("===> client[" + c + "] w/ appID = " + currentClient.getAppID());
		
		// Extra step for SOUND_SWARM_DISPLAY: need to send THREAD_ID
		
		
		// myGrain.print();
		// Write the data out
		currentClient.threadSand.sendGrain(myGrain);
		
	    }
	  
	    NGlobals.sPrint("handle(DONE) " + THREAD_ID + ":" + myGrain.appID);
	}
	// END --------------------------------------------------------------------
	

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

