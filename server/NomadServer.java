import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.Object.*;
import nomads.v210.*;

public class NomadServer implements Runnable {  

    // Thread locking

    private NomadServerThread clients[] = new NomadServerThread[5000];
    private int clientThreadNum[] = new int[100000];
    private Object clientThreadNumLock = new Object();
    private Object clientLock = new Object();

    private NomadServerThread getClient(int i) {
	synchronized(clientLock) {
	    NomadServerThread tC = clients[i];
	    if (tC == null) {
		NGlobals.dtPrint("BOGUS GET currentClient - null " + i);
		return null;
	    }
	    else if (tC.getRun() == false) {
		NGlobals.dtPrint("BOGUS GET currentClient - getRun() " + i);
		return null;
	    }
	    else {
		return tC;
	    }
	}
	//	return tC;
    }

    private int getClientsLength() {
	synchronized(clientLock) {
	    return clients.length;
	}
    }

    private void setClient(int i, NomadServerThread nsThread) {
	synchronized(clientLock) {
	    clients[i] = nsThread;
	}
    }

    private int getClientThreadNum (int i) {
	synchronized(clientThreadNumLock) {
	    return clientThreadNum[i];
	}
    }

    private void setClientThreadNum (int i, int j) {
	synchronized(clientThreadNumLock) {
	    clientThreadNum[i] = j;
	}
    }


    private int clientCount = 0;
    private Object clientCountLock = new Object();

    private int getClientCount () {
	return clientCount;
    }

    private void setClientCount (int i) {
	clientCount = i;
    }

    // private String IPsLoggedIn[] = new String[1000];
    // private String users[] = new String[1000];
    
    private ServerSocket server = null;
    private Thread       thread = null;

    // private int IPCount = 0;
    // private int userCount = 0;
    // private int eventNum = 0;

    private static int debugLine = 0;

    private static String[] children;
    private Calendar cal;
    private static Boolean requireLogin = false;
    long nowT,appT,diffT,lagT;

    NGrain LPPGrain=null;
    NGrain LDPGrain=null;
    NGrain LCPGrain=null;

    int iDay;
    FileOutputStream out; // declare a file output object
    PrintStream p; // declare a print stream object

    byte currentPollType;

    private byte modStates[] = new byte[6];
    int stateOffset;
    int numModStates;
    // ^^^ CLAS only ^^^
    
    // vvv CLASS only vvv (was from early attempts in 2012)
    // for better thread synchronization
    private Object threadLock;
    private Object serverLock;
    private Object readLock;
    private Object writeLock;
    // ^^^ CLASS ^^^

    // ============================================================
    // CONSTRUCTOR FUNCTION

    public NomadServer(int port) {  	    

	// vvv CLASS only vvv
	threadLock = new Object();
	serverLock = new Object();
	readLock = new Object();
	writeLock = new Object();
	// ^^^ CLASS ^^^

	for (int i=0;i<100000;i++) {
	    setClientThreadNum(i,-1);
	}
	// for (int i=0;i<1000;i++) {
	//     IPsLoggedIn[i] = null;
	//     users[i] = null;
	// }

	// vvv CLASS only vvv
	numModStates = (int)(NCommand.MOD_STATE_END-NCommand.MOD_STATE_START)+1;
	NGlobals.dtPrint("  numModStates = " + numModStates);

	// Use this now for "button states"
	for (int i=0;i<numModStates;i++) {
	    modStates[i] = 0;
	}
	// ^^^ CLASS ^^^

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

    // --------------------
    // RUN
    // --------------------

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

    // private synchronized int checkIP (String IP) {
    // 	NGlobals.sPrint("          checkIP(" + IP + ")");
	
    // 	for (int i = 0; i < IPCount; i++) {
    // 	    if (IPsLoggedIn[i] == null)
    // 		return (int)-1;
    // 	    if (IPsLoggedIn[i].equals(IP))
    // 		return (int)i;
    // 	}
    // 	return (int)-1;
    // }

    class DirFilter implements FilenameFilter {
	String afn;
	DirFilter(String afn) { this.afn = afn; }
	public boolean accept(File dir, String name) {
	    // Strip path information:
	    String f = new File(name).getName();
	    return f.indexOf(afn) != -1;
	}
    }

    private String printID (byte id) {
	String[] idList = new String[255];
	int i;
	for(i=0;i<255;i++) {
	    idList[i] = null;
	}

	// Populate the list

	idList[NAppID.SERVER] = new String("SERVER");
	idList[NAppID.INSTRUCTOR_PANEL] = new String("INSTRUCTOR_PANEL");
	idList[NAppID.BINDLE] = new String("BINDLE");
	idList[NAppID.DISCUSS] = new String("DISCUSS");
	idList[NAppID.DISCUSS_PROMPT] = new String("DISCUSS_PROMPT");
	idList[NAppID.CLOUD_CHAT] = new String("CLOUD_CHAT");
	idList[NAppID.CLOUD_PROMPT] = new String("CLOUD_PROMPT");
	idList[NAppID.STUDENT_POLL] = new String("STUDENT_POLL");
	idList[NAppID.TEACHER_POLL] = new String("TEACHER_POLL");

	// Print out the id as a string
	if (idList[id] != null) {
	    String rString = new String(idList[id] + "[" + id + "]");
	    return rString;	
	}
	else {
	    String rString = new String("UNKNOWN[" + id + "]");
	    return rString;	
	}
    }

    // ================================================================
    //    handle ( THREAD_ID , grain )
    // ================================================================

    public void handle(int THREAD_ID, NGrain myGrain)  {  
	// synchronized(threadLock) {
	//	synchronized(serverLock) {
	NomadServerThread currentClient,tC;

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

	if (myGrain == null) {
	    NGlobals.dtPrint("BOGUS GRAIN - PUNTING");
	    return;
	}

	// 1: READ =================================================================================================================

    	NGlobals.sPrint("-----------------------------------------------------[" + debugLine++ + "]");
    	
	// synchronized (threadLock) {
	// Do the following for EACH client
	// NOMADS CLASSROOM

	NGlobals.sPrint("===== READING =====");

	// Read in relevant SAND header info
	incAppID = myGrain.appID;
	incAppCmd = myGrain.command;
	incAppDataType = myGrain.dataType;
	incAppDataLen = myGrain.dataLen;

	// Get client number of inc client
	// synchronized (threadLock) {
	
	tCNum = getClientThreadNum(THREAD_ID);
	if (tCNum == -1) {
	    NGlobals.dtPrint("NULL THREAD");
	    return;
	}
	//}

	// Print out at the SERVER level
	NGlobals.sPrint("appID: " + incAppID);
	NGlobals.sPrint("command: " + incAppCmd);
	NGlobals.sPrint("dataType: " + incAppDataType);
	NGlobals.sPrint("dataLen: " + incAppDataLen);

	// Thread admin stuff ---------------------------------------------------------------------

	if (tCNum < 0) {
	    NGlobals.sPrint("   ERROR:  client thread not found.");
	    // TODO:  send the bye command!!!
	    removeByThreadID(THREAD_ID);
	    return;
	}

	// synchronized (threadLock) {
	currentClient = getClient(tCNum);
	if (currentClient == null) {
	    NGlobals.dtPrint("BOGUS currentClient " + tCNum);
	    removeByPos(tCNum);
	    return;
	}
	// }

	// Login and THREAD registration ----------------------------------------------------------

	// 1: check if client thread is registered
	//    if not reg, REGISTER the client's appID with the SERVER client thread

	if (currentClient.getAppID() == -1) {
	    NGlobals.sPrint("===== REGISTERING =====");
	    NGlobals.sPrint("  Setting client[" + tCNum + "] incAppID to: " + incAppID);
	    // synchronized (threadLock) {
	    currentClient.setAppID(incAppID);
	    // }

	    // Send button states to the ICP
	    if (incAppID == NAppID.INSTRUCTOR_PANEL) {
		// Log the client in
		String tStringX = new String("KHAN");
		// synchronized (threadLock) {
		currentClient.setUser(tStringX);
		// Set new login status 
		currentClient.setLoginStatus(true);
		// }
		for (int i=0; i<numModStates; i++) {
		    tCommand = (byte)(NCommand.MOD_STATE_START+i);
		    byte[] dx = new byte[1];
		    dx[0] = modStates[i];
		    NGlobals.sPrint("Sending ----> button state (" + tCommand + "/" + dx[0] + ") to ICP " + currentClient.getThreadID());
		    if (currentClient.threadSand.getRun()) {
			currentClient.threadSand.sendGrainL(NAppID.SERVER, tCommand, NDataType.UINT8, 1, dx);
		    }
		    else {
			removeByThreadID(currentClient.getThreadID());
		    }
		}
	    }
	}

	// DT this only here in CLASS

	// 2: check login
	//      only the LOGIN app can log you in
	//      if you're not logged in, you get booted

	// synchronized (threadLock) {
	tLoginStatus = currentClient.getLoginStatus();
	// }
	if ((incAppID == NAppID.BINDLE) && (incAppCmd == NCommand.LOGIN)) {
	    if (tLoginStatus == true) {
		// send back "you're already logged in" message / LOGIN_STATUS w/ value = 2
		NGlobals.sPrint("  LOGIN client [" + tCNum + "] already logged in.\n" + incAppID);

		byte[] dx = new byte[1];
		dx[0] = 2;
		if (currentClient.threadSand.getRun()) {
		    currentClient.threadSand.sendGrainL(NAppID.SERVER, NCommand.LOGIN_STATUS, NDataType.UINT8, 1, dx);
		}
		else {
		    removeByThreadID(currentClient.getThreadID());
		}
	    }
	    else {
		// Log the client in
		String tString = new String(myGrain.bArray);
		NGlobals.sPrint("Got username: " + tString);
		//		synchronized (threadLock) {
		getClient(tCNum).setUser(tString);
		// Set new login status 
		getClient(tCNum).setLoginStatus(true);
		// }
		tLoginStatus = currentClient.getLoginStatus();

		NGlobals.sPrint("  LOGIN client [" + tCNum + "] logging in, sending ----> back confirmation info.\n" + incAppID);

		// send back "successful login" message / LOGIN_STATUS w/ value = 1
		byte[] dx = new byte[1];
		dx[0] = 1;
		if (currentClient.threadSand.getRun()) {
		    currentClient.threadSand.sendGrainL(NAppID.SERVER, NCommand.LOGIN_STATUS, NDataType.UINT8, 1, dx);
		}
		else {
		    removeByThreadID(currentClient.getThreadID());
		}

		// INIT STATES FOR BINDLE - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

		// Send button states (to the BINDLE -> implied by this coming from LOGIN)
		for (int i=0; i<numModStates; i++) {
		    tCommand = (byte)(NCommand.MOD_STATE_START+i);
		    dx[0] = modStates[i];
		    NGlobals.sPrint("Sending ----> button state to BINDLE (" + tCommand + "/" + dx[0] + ") to BINDLE " + currentClient.getThreadID());
		    if (currentClient.threadSand.getRun()) {
			currentClient.threadSand.sendGrainL(NAppID.SERVER, tCommand, NDataType.UINT8, 1, dx);
		    }
		    else {
			removeByThreadID(currentClient.getThreadID());
		    }

		}

		if (LPPGrain != null) {
		    NGlobals.dtPrint("Sending POLL PROMPT");
		    if (currentClient.threadSand.getRun()) {
			currentClient.threadSand.sendGrainL(LPPGrain);
		    }
		    else {
			removeByThreadID(currentClient.getThreadID());
		    }

		}
		if (LDPGrain != null) {
		    NGlobals.dtPrint("Sending DISCUSS PROMPT");
		    if (currentClient.threadSand.getRun()) {
			currentClient.threadSand.sendGrainL(LDPGrain);
		    }
		    else {
			removeByThreadID(currentClient.getThreadID());
		    }

		}
		if (LCPGrain != null) {
		    NGlobals.dtPrint("Sending CLOUD PROMPT");
		    if (currentClient.threadSand.getRun()) {
			currentClient.threadSand.sendGrainL(LCPGrain);
		    }
		    else {
			removeByThreadID(currentClient.getThreadID());
		    }

		}
	    }
	}

	// Comment this out to turn back on login : LOGIN TOGGLE

	tLoginStatus = true;

	// Kick -------------------------------------------------------------------------------------

	// X:  if you're not the LOGIN app providing the correct info, you get booted here
	if (tLoginStatus == false) {
	    NGlobals.sPrint("   WARNING:  client THREAD NOT logged in.");
	    removeByThreadID(THREAD_ID);
	    // TODO: send back some sand data re: login info
	    return;
	}

	if (incAppID == NAppID.DISCUSS_PROMPT) {
	    LDPGrain = myGrain;
	}
	else if (incAppID == NAppID.CLOUD_PROMPT) {
	    LCPGrain = myGrain;
	}
	else if (incAppID == NAppID.TEACHER_POLL) {
	    LPPGrain = myGrain;
	}

	// ====================================================================================
	//  At this point we're logged in, and we have your SAND data GRAIN
	// ====================================================================================

	NGlobals.sPrint("   client THREAD logged in.");

	// Grab IP (more for data logging)
	// synchronized (threadLock) {
	IP = currentClient.getIP();
	//}

	// Grab incoming button state messages from INSTRUCTOR_PANEL
	
	// XMERGE(1):  this unrolled in Auksalaq
	
	if (incAppID == NAppID.INSTRUCTOR_PANEL) {
	    if ( (incAppCmd >= NCommand.MOD_STATE_START) && (incAppCmd <= NCommand.MOD_STATE_END) ) {
		// synchronized(serverLock) {
		modStateOffset = (int)(incAppCmd-NCommand.MOD_STATE_START);
		modStates[modStateOffset] = myGrain.bArray[0];
		// }
	    }
	}
	// } // matches synchronized(threadLock)

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
	

	// Rejects ====================================

	// 1.  DISCUSS data when DISCUSS is off

	// synchronized (serverLock) {
	stateOffset = (NCommand.SET_DISCUSS_STATUS-NCommand.MOD_STATE_START);
	//}
	if (((incAppID == NAppID.DISCUSS) ||
	     (incAppID == NAppID.INSTRUCTOR_DISCUSS)) &&
	    (modStates[stateOffset] == 0)) {
	    NGlobals.dtPrint(" --- RRRR rejected DISCUSS data (DISCUSS_OFF) ---");
	    return;
	}

	// 2.  CLOUD chat data when CLOUD is off
	// synchronized (serverLock) {
	stateOffset = (NCommand.SET_CLOUD_STATUS-NCommand.MOD_STATE_START);
	//}

	if ((incAppID == NAppID.CLOUD_CHAT) &&
	    (modStates[stateOffset] == 0)) {
	    NGlobals.dtPrint(" --- RRRR rejected CLOUD data (CLOUD_OFF) ---");
	    return;
	}


	// 3.  POLL data when POLL is off

	//	synchronized (serverLock) {
	stateOffset = (NCommand.SET_POLL_STATUS-NCommand.MOD_STATE_START);
	//	}
	if ((incAppID == NAppID.STUDENT_POLL) &&
	    (modStates[stateOffset] == 0)) {
	    NGlobals.dtPrint(" --- RRRR rejected POLL data (POLL_OFF) ---");
	    return;
	}

	// Get client number of inc client
	String myUser;
	//	synchronized (threadLock) {
	tCNum = getClientThreadNum(THREAD_ID);
	tC = getClient(tCNum);
	if (tC == null) {
	    NGlobals.dtPrint("BOGUS CLIENT (null) " + tCNum + " removing");
	    removeByPos(tCNum);
	    return;
	}
	else if (tC.getRun() == false) {
	    NGlobals.dtPrint("BOGUS CLIENT (getRun)" + tCNum + " removing");
	    removeByPos(tCNum);
	    return;
	}
	else {
	    myUser = tC.getUser();
	}
	
	//}
	// if (incAppID == NAppID.INSTRUCTOR_PANEL || 
	//     incAppID == NAppID.DISCUSS_PROMPT || 
	//     incAppID == NAppID.TEACHER_POLL || 
	//     incAppID == NAppID.CLOUD_PROMPT || 
	//     incAppID == NAppID.INSTRUCTOR_DISCUSS) {
	//     myUser = new String("KHAN");
	// }



	if ((incAppDataType == NDataType.CHAR || incAppDataType == NDataType.BYTE) && (incAppDataLen > 1)) {
	    String msg = new String(myGrain.bArray);
	    if (incAppCmd == NCommand.LOGIN) {
		String tip = getClient(tCNum).getIP();
		NGlobals.csvPrint("LOG, user = " + myUser + ", login from IP " + tip);
	    }
	    else {
		NGlobals.csvPrint("LOG, user = " + myUser + ", msg = " + msg + ", appID = " + printID(incAppID) + ", appCmd = " + incAppCmd);
	    }
	}
	if ((incAppDataType == NDataType.UINT8 || incAppDataType == NDataType.BYTE) && (incAppDataLen == 1)) {
	    int val = (int)myGrain.bArray[0];
	    NGlobals.csvPrint("LOG, user = " + myUser + ", bval = " + val + ", appID = " + printID(incAppID) + ", appCmd = " + incAppCmd);
	}
	if ((incAppDataType == NDataType.INT32 || incAppDataType == NDataType.INT) && (incAppDataLen > 0)) {
	    int val = (int)myGrain.iArray[0];
	    NGlobals.csvPrint("LOG, user = " + myUser + ", val = " + val + ", appID = " + printID(incAppID) + ", appCmd =" + incAppCmd);
	}

	NGlobals.dtPrint("CHECKPOINT 1 - STATES");

	// ==> Incoming app is INSTRUCTOR PANEL
	//
	// send out any messages from it

	if (incAppID == NAppID.INSTRUCTOR_PANEL) {
	    for (int c = 0; c < getClientCount(); c++) {
		NGlobals.dtPrint("    IN");
		// synchronized (threadLock) {
		currentClient = getClient(c);

		if (currentClient == null) {
		    NGlobals.dtPrint("BOGUS CLIENT (null) " + c + " removing");
		    removeByPos(c);
		}
		else if (currentClient.threadSand.getRun() == false) {
		    NGlobals.dtPrint("BOGUS CLIENT (getRun)" + c + " removing");
		    removeByPos(c);
		}
		else {
		
		    if ((currentClient.getAppID() == NAppID.BINDLE) || (currentClient.getAppID() == NAppID.INSTRUCTOR_PANEL)) {
			NGlobals.sPrint("Sending ----> button state to BINDLE/ICP " + currentClient.getThreadID());		    
			// myGrain.print();
			currentClient.threadSand.sendGrainL(myGrain);
		    }
		}
	    }
	}

	// ==> Incoming app is TEACHER_POLL
	//
	// stash teh poll type
	if (incAppID == NAppID.TEACHER_POLL) {
	    // synchronized(serverLock) {
	    currentPollType = incAppCmd;
	    // }
	}


	// ==> incoming app ID is student poll, but poll is responding to incorrect type
	//
	// so return

	if ((incAppID == NAppID.STUDENT_POLL) && (incAppCmd != currentPollType) ) {
	    return;
	}

	NGlobals.dtPrint("CHECKPOINT 2 - prompts");

	// ==> incoming app ID is DISCUS_PROMPT or CLOUD_PROMPT or TEACHER_POLL (PROMPT)
	//
	// send prompt data to BINDLE and ICP
	
	if ( (incAppID == NAppID.DISCUSS_PROMPT) || 
	     (incAppID == NAppID.CLOUD_PROMPT) || 
	     (incAppID == NAppID.TEACHER_POLL)) {
	    NGlobals.dtPrint("    IN");

	    //	     (incAppID == NAppID.POLL_PROMPT) ||
	    // (incAppID == NAppID.INSTRUCT_EMRG_SYNTH_PROMPT) || 
	    //	     (incAppID == NAppID.POLL_PROMPT) ||
	    // (incAppID == NAppID.INSTRUCTOR_SEQUENCER) ) { //

	    for (int c = 0; c < getClientCount(); c++) {
		// synchronized (threadLock) {
		currentClient = getClient(c);

		if (currentClient == null) {
		    NGlobals.dtPrint("BOGUS CLIENT (null) " + c + " removing");
		    removeByPos(c);
		}
		else if (currentClient.threadSand.getRun() == false) {
		    NGlobals.dtPrint("BOGUS CLIENT (getRun)" + c + " removing");
		    removeByPos(c);
		}

		else {
		    if ( (currentClient.getAppID() == NAppID.BINDLE) || (currentClient.getAppID() == NAppID.INSTRUCTOR_PANEL)) {
			NGlobals.sPrint("Sending ----> prompt data to BINDLE " + currentClient.getThreadID());
			// myGrain.print();
			currentClient.threadSand.sendGrainL(myGrain);
		    }
		}
	    }
	}

	NGlobals.dtPrint("CHECKPOINT 3 - BINDLE (non discuss) to ICP");
	
	// ==> Incoming appd is is CLOUD_CHAT or STUDENT POLL
	//     send BINDLE data to INSTRUCTOR_PANEL

	if ( (incAppID == NAppID.CLOUD_CHAT) ||
	     (incAppID == NAppID.STUDENT_POLL)) {
	    NGlobals.dtPrint("    IN");

	    //	     (incAppID == NAppID.POLL_PROMPT) ||

	    // (incAppID == NAppID.STUDENT_SEQUENCER) ||
	    // (incAppID == NAppID.STUDENT_SAND_POINTER) ||
	    // (incAppID == NAppID.STUD_EMRG_SYNTH) ) { //

	    for (int c = 0; c < getClientCount(); c++) {
		// synchronized (threadLock) {
		currentClient = getClient(c);

		if (currentClient == null) {
		    NGlobals.dtPrint("BOGUS CLIENT (null) " + c + " removing");
		    removeByPos(c);
		}
		else if (currentClient.threadSand.getRun() == false) {
		    NGlobals.dtPrint("BOGUS CLIENT (getRun)" + c + " removing");
		    removeByPos(c);
		}
		else {

		    // send to ==> INSTRUCTOR_PANEL
		    //
		    if ( currentClient.getAppID() == NAppID.INSTRUCTOR_PANEL) {
			NGlobals.sPrint("Sending ----> BINDLE data to ICP " + currentClient.getThreadID());
			// myGrain.print();
			currentClient.threadSand.sendGrainL(myGrain);
		    }
		}
	    }
	}

	NGlobals.dtPrint("CHECKPOINT 4 - DISCUSS to ICP and BINDLE");

	// Send DISCUSS data to BINDLE and IOCP
	// synchronized (serverLock) {
	stateOffset = (NCommand.SET_DISCUSS_STATUS-NCommand.MOD_STATE_START);
	//}	

	// ==> inc app id is DISCUSS or INSTRUCTOR DISCUSS

	//    send out data

	if (((incAppID == NAppID.DISCUSS) ||
	     (incAppID == NAppID.INSTRUCTOR_DISCUSS)) &&
	    (modStates[stateOffset] == 1)) {
	    NGlobals.dtPrint("    IN");

	    for (int c = 0; c < getClientCount(); c++) {
		currentClient = getClient(c);
		
		if (currentClient == null) {
		    NGlobals.dtPrint("BOGUS CLIENT (null) " + c + " removing");
		    removeByPos(c);
		}
		else if (currentClient.threadSand.getRun() == false) {
		    NGlobals.dtPrint("BOGUS CLIENT (getRun)" + c + " removing");
		    removeByPos(c);
		}
		else {
		    // synchronized (threadLock) {
		    NGlobals.dtPrint("CHECKPOINT 4a - getting client " + c);
		    
		    if ( currentClient.getAppID() == NAppID.BINDLE) {
			NGlobals.dtPrint("CHECKPOINT 4b - BINDLE!");
			// myGrain.print();
			NGlobals.sPrint("Sending ---> discuss data to BINDLE/ICP " + currentClient.getThreadID());
			currentClient.threadSand.sendGrainL(myGrain);
		    }
		    else if ( currentClient.getAppID() == NAppID.INSTRUCTOR_PANEL) {
			NGlobals.dtPrint("CHECKPOINT 4b - ICP!");
			NGlobals.sPrint("Sending ----> discuss data to ICP " + currentClient.getThreadID());
			currentClient.threadSand.sendGrainL(myGrain);
		    }
		    NGlobals.dtPrint("CHECKPOINT 4x - done with client " + c);
		}
	    }
	}
	
	NGlobals.dtPrint("CHECKPOINT 5 - OTHER APPS");

	// Sound SWARM Routing Logic ==========================================================================R

	// TODO:  send 3 ints instead, 1st int is the THREAD_ID

	if (incAppID == NAppID.SOUND_SWARM) {
	    NGlobals.dtPrint("SOUND SWARM");

	    for (int c = 0; c < getClientCount(); c++) {
		
		// Get the client off the master list
		// synchronized (threadLock) {
		currentClient = getClient(c);
		if (currentClient == null) {
		    NGlobals.dtPrint("BOGUS CLIENT (null) " + c + " removing");
		    removeByPos(c);
		}
		else if (currentClient.threadSand.getRun() == false) {
		    NGlobals.dtPrint("BOGUS CLIENT (getRun)" + c + " removing");
		    removeByPos(c);
		}
		else {
		    // }
		    NGlobals.sPrint("===> client[" + c + "] w/ id = " + currentClient.getAppID());
		    
		    if (currentClient.getAppID() == NAppID.INSTRUCTOR_PANEL) {
			NGlobals.sPrint("Sending ----> SOUND_SWARM:THREAD_ID to ---> SOUND_SWARM_DISPLAY: " + THREAD_ID);
			int[] x3 = new int[3];

			x3[0] = THREAD_ID;
			x3[1] = myGrain.iArray[0];
			x3[2] = myGrain.iArray[1];

			currentClient.threadSand.sendGrainL(NAppID.SOUND_SWARM, NCommand.SEND_SPRITE_XY, NDataType.INT32, 3, x3);
			
			NGlobals.sPrint("Sending ----> SOUND_SWARM: x/y coordinates\n");
			// currentClient.threadSand.sendGrainL(myGrain);
			
			// myGrain.print();
			
		    }
		}
		
	    }
	}

	// GENERIC Logic =============================================================
	//    -for each client SEND ALL DATA

	else if (1 == 0) {
	    NGlobals.sPrint("===> sending PASSTHROUGH network data");
	    NGlobals.dtPrint("    IN");
	    for (int c = 0; c < getClientCount(); c++) {
		
		// Get the client off the master list
		// synchronized (threadLock) {
		currentClient = getClient(c);
		if (currentClient == null) {
		    NGlobals.dtPrint("BOGUS CLIENT (null) " + c + " removing");
		    removeByPos(c);
		}
		else if (currentClient.threadSand.getRun() == false) {
		    NGlobals.dtPrint("BOGUS CLIENT (getRun)" + c + " removing");
		    removeByPos(c);
		}
		else {
		    // }
		    NGlobals.sPrint("===> client[" + c + "] w/ appID = " + currentClient.getAppID());
		    
		    // Extra step for SOUND_SWARM_DISPLAY: need to send THREAD_ID
		    
		    
		    // myGrain.print();
		    // Write the data out
		}
		
	    }
	}

	NGlobals.sPrint("handle(DONE) " + THREAD_ID + ":" + myGrain.appID);
	// END --------------------------------------------------------------------

	NGlobals.dtPrint("CHECKPOINT X - END");
	NGlobals.dtPrint("--------------------------------------- \n");

	// } // matches synchronized(threadLock)
	// Free up memory
	if (myGrain != null) {
	    myGrain = null;
	}
	
    }
    //	} // matches synchronized(threadLock)
    // =====================================================================================================
    // END main data routing code --- handle() fn
    // =====================================================================================================
    
    // -- remove by THREAD_ID --------------------------------------

    public void removeByThreadID(int THREAD_ID) {  
	int pos,tC,c;
	int tID;
	NomadServerThread toTerminate,tT;

	//	synchronized(threadLock) {
	    
	pos = getClientThreadNum(THREAD_ID);
	NGlobals.dtPrint("removing THREAD_ID " + THREAD_ID + " at pos " + pos);
	NGlobals.sPrint("     clientCount = " + getClientCount());
	NGlobals.sPrint("     clients.length = " + getClientsLength());
	    

	// remove from lists ---------------------------------------------------

	if (pos >= 0) {  
	    setClientThreadNum(THREAD_ID, -1);
	    toTerminate = getClient(pos);

	    // setClient(pos,null);  // XYZ for garbage collect

	    // rem from client list
	    if (pos < getClientCount()-1) {
		NGlobals.sPrint("Removing client thread " + THREAD_ID + " at " + pos);
		for (int i = pos+1; i < getClientCount(); i++) {
		    tT = getClient(i);
		    setClient((i-1), tT);
		    tID = tT.getThreadID();
		    setClientThreadNum(tID,(i-1));
		}
	    }
	    
	    tC = getClientCount();
	    setClientCount(tC-1);
	    
	    try {
		if (toTerminate == null) {
		    NGlobals.dtPrint("SERVER: BOGUS toTerminate");
		    return;
		}
		else {
		    toTerminate.close(); 
		    toTerminate.stop(); 
		    toTerminate.interrupt();
		    toTerminate = null;
		}
		    
	    }
	    catch(IOException ioe) {  
		NGlobals.sPrint("  Error closing thread: " + ioe); 
	    }
	}
	    
	// CHECK double check the client count
	tC=0;
	for (c=0; c<getClientCount(); c++) {
	    tT = getClient(c);
	    if (tT != null) {
		if (tT.getRun()) {
		    tC++;
		}
	    }
	}
	// - CHECK 

	setClientCount(tC);
    }
    //    }    // matches synchronized(threadLock)


    // -- remove by POSITION --------------------------------------

    public void removeByPos(int pos) {  
	int tC,c;
	int tID;
	NomadServerThread toTerminate,tT;

	//	synchronized(threadLock) {
	    
	NGlobals.dtPrint("removing THREAD at pos " + pos);
	NGlobals.sPrint("     clientCount = " + getClientCount());
	NGlobals.sPrint("     clients.length = " + getClientsLength());
	    
	if (pos >= 0) {  
	    // setClientThreadNum(THREAD_ID, -1);
	    toTerminate = getClient(pos);
	    
	    if (toTerminate == null) {
		NGlobals.dtPrint("SERVER: BOGUS toTerminate");
		return;
	    }
	    
	    // setClient(pos,null);  // XYZ for garbage collect
	    
	    if (pos < getClientCount()-1) {
		NGlobals.sPrint("Removing client thread at pos" + pos);
		for (int i = pos+1; i < getClientCount(); i++) {
		    tT = getClient(i);
		    setClient((i-1), tT);
		    tID = tT.getThreadID();
		    setClientThreadNum(tID,(i-1));
		}
	    }
	    
	    tC = getClientCount();
	    setClientCount(tC-1);
	    
	    try {
		toTerminate.close(); 
		toTerminate.stop(); 
		toTerminate.interrupt();
		toTerminate = null;
	    }
	    catch(IOException ioe) {  
		NGlobals.sPrint("  Error closing thread: " + ioe); 
	    }
	    
	}		// XYZ double check the client count

	tC=0;
	for (c=0; c<getClientCount(); c++) {
	    tT = getClient(c);
	    if (tT != null) {
		if (tT.getRun()) {
		    tC++;
		}
	    }
	}
	setClientCount(tC);
    }
    //    }    // matches synchronized(threadLock)


    private void addThread(Socket socket) {  
	//	synchronized(threadLock) {
	int tID;
	int i,tC,c,tL,tCC;
	NGlobals.sPrint("addThread(" + socket + ")");
	NomadServerThread tThread, tT;

	String IP = new String((socket.getInetAddress()).getHostAddress());

	// XYZ double check the client count
	tC=0;
	NGlobals.dtPrint("Checking clients ...");
	tCC = getClientCount();
    
	for (c=0; c<tCC; c++) {
	    tT = getClient(c);
	    if (tT != null) {
		if (tT.getRun()) {
		    NGlobals.dtPrint("clients " + c + "ok");
		    tC++;
		}
	    }
	    else {
		removeByPos(c);
		NGlobals.dtPrint("client " + c + " BOGUS");
		tCC = getClientCount();
	    }
	}
	setClientCount(tC);


	tC = getClientCount();
	tL = getClientsLength();
	NGlobals.sPrint("     clientCount = " + tC);
	NGlobals.sPrint("     clients.length = " + tL);

	if (tC < tL) {  
	    NGlobals.sPrint("  Client accepted: " + socket);
	    NGlobals.sPrint("  IP = " + IP);
	    tThread = new NomadServerThread(this, socket);
	    setClient(tC, tThread);
	    try {
		tThread.open(); 
		tThread.setIP(IP);
		tID = tThread.getThreadID();
		setClientThreadNum(tID, tC);
		tThread.start();  

		NGlobals.sPrint("  Client added to lookup array at slot # " + tC);
		setClientCount(tC+1);
	    }
	    catch(IOException ioe) {  
		NGlobals.sPrint("    Error opening thread: " + ioe); 
	    } 
	}
	else
	    NGlobals.sPrint("  Client refused: maximum " + getClientsLength() + " reached.");
    }
    //    }  // matches synchronized(threadLock)
    
    public static void main(String args[]) {  
    	NomadServer server = null;
	if (args.length != 1)
	    NGlobals.sPrint("Usage: java NomadServer port");
	else
	    server = new NomadServer(Integer.parseInt(args[0]));
    }

}

