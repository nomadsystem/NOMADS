import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.Object.*;
import nomads.v210_auk.*;

public class NomadServer implements Runnable {  

    // == vvv AUK only vvv ==
    int MAX_THREAD_IDS = 100000;
    int MAX_IPS = 2000;
    int MAX_THREADS = 5000;
    int MAX_DISP_THR = 500;
    // == ^^^ AUK only ^^^ ==

    // Thread locking

    private NomadServerThread clients[] = new NomadServerThread[MAX_THREADS];
    public NomadServerThread clientsChecked[] = new NomadServerThread[MAX_THREADS];
    public NomadServerThread dispClientsChecked[] = new NomadServerThread[MAX_DISP_THR];

    private int clientThrList[] = new int[MAX_THREAD_IDS];

    private int clientNumAppIDList[] = new int[MAX_THREADS];

    private int clientThreadNum[] = new int[MAX_THREAD_IDS];
    private Object clientThreadNumLock = new Object();
    private Object clientLock = new Object();

    private Object clientThrListLock = new Object();
    private Object clientNumAppIDLock = new Object();

    // == vvv AUK only vvv ==
    private NomadServerThread dispClients[] = new NomadServerThread[MAX_DISP_THR];
    private int dispClientThreadNum[] = new int[MAX_THREAD_IDS];
    private Object dispClientThreadNumLock = new Object();
    private Object dispClientLock = new Object();
    // == ^^^ AUK only ^^ ==

    private void setClientThrList(int i, int tID) {
	synchronized(clientThrListLock) {
	    clientThrList[i] = tID;
	}
    }

    private int getClientNumThrList(int i) {
		synchronized(clientThrListLock) {
	    return clientThrList[i];
	    	}
    }


    private void setClientNumAppIDList(int i, int aID) {
	synchronized(clientNumAppIDLock) {
	    clientNumAppIDList[i] = aID;
	}
    }


    private int getClientNumAppIDList(int i) {
		synchronized(clientNumAppIDLock) {
	    return clientNumAppIDList[i];
	    	}
    }

    private synchronized void checkClients() {
	// CHECK double check the client count
	NGlobals.dtPrint("checkClients() - - - - -");
	NomadServerThread tT;
	int tC = 0;
	for (int c=0; c<getClientCount(); c++) {
	    // NGlobals.dtPrint("getClient: 15 (remove)");
	    tT = getClient(c);
	    if (tT != null) {
		if (tT.getRun()) {
		    clientsChecked[tC] = tT; 
		    tC++;
		}
	    }
	}
	setClientCount(tC);
	
	// Put only clean clients back on the list

	for (int c=0; c<getClientCount(); c++) {
	    tT = null;
	    tT = clientsChecked[c];
	    setClient(c,tT);
	}

	NGlobals.dtPrint("- - - - -clientCount = " + getClientCount());
	// - CHECK
    }

    private synchronized void checkDispClients() {
	// CHECK double check the client count
	NGlobals.dtPrint("checkDispClients() - - - - -");
	NomadServerThread tT;
	int tC = 0;
	for (int c=0; c<getDispClientCount(); c++) {
	    // NGlobals.dtPrint("getClient: 15 (remove)");
	    tT = getDispClient(c);
	    if (tT != null) {
		if (tT.getRun()) {
		    dispClientsChecked[tC++] = tT; 
		}
	    }
	}
	
	// Put only clean clients back on the list

	for (int c=0; c<getDispClientCount(); c++) {
	    tT = dispClientsChecked[c];
	    setDispClient(c,tT);
	}

	setDispClientCount(tC);
	NGlobals.dtPrint("- - - - -clientCount = " + getClientCount());
	// - CHECK
    }


    private NomadServerThread getClient(int i) {
	synchronized(clientLock) {
	    NomadServerThread tC = clients[i];
	    NomadServerThread tT;

	    if (tC == null) {
		NGlobals.dtPrint("BOGUS(1) GET currentClient - null " + i);
		return null;
	    }
	    else if (tC.threadSand.getRun() == false) {
		NGlobals.dtPrint("BOGUS(2) GET currentClient - getRun() " + i);
		return tC;
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


    // == vvv AUK only vvv ==
    private NomadServerThread getDispClient(int i) {
	synchronized(dispClientLock) {
	    NomadServerThread tC = dispClients[i];
	    if (tC == null) {
		NGlobals.dtPrint("BOGUS(3) GET currentDisplayClient - null " + i);
		return null;
	    }
	    else if (tC.getRun() == false) {
		NGlobals.dtPrint("BOGUS(4) GET currentDisplayClient - getRun() -> returning client (tC)" + i);
		return tC;
		// return null;
	    }
	    else {
		return tC;
	    }
	}
    }

    private int getDispClientsLength() {
		synchronized(dispClientLock) {
	    return dispClients.length;
	    	}
    }

    private void setDispClient(int i, NomadServerThread nsThread) {
	synchronized(dispClientLock) {
	    dispClients[i] = nsThread;
	}
    }

    private int getDispClientThreadNum (int i) {
		synchronized(dispClientThreadNumLock) {
	    return dispClientThreadNum[i];
	    	}
    }

    private void setDispClientThreadNum (int i, int j) {
	synchronized(dispClientThreadNumLock) {
	    dispClientThreadNum[i] = j;
	}
    }
    // == ^^^ AUK only ^^^ ==

    private int clientCount = 0;
    private Object clientCountLock = new Object();

    private int getClientCount() {
		synchronized(clientCountLock) {
	    return clientCount;
	    	}
    }

    private void setClientCount(int c) {
	synchronized(clientCountLock) {
	    clientCount = c;
	}
    }


    // == vvv AUK only vvv ==
    private int dispClientCount = 0;
    private Object dispClientCountLock = new Object();

    private String IPsLoggedIn[] = new String[MAX_IPS];
    private String users[] = new String[MAX_IPS];
    //

    private int getDispClientCount() {
		synchronized(dispClientCountLock) {
	return dispClientCount;
		}
    }

    private void setDispClientCount(int c) {
		synchronized(dispClientCountLock) {
	dispClientCount = c;
		}
    }
    // == ^^^ AUK only ^^^ ==

    private ServerSocket server = null;
    private Thread       thread = null;

    // == vvv AUK only vvv ==
    private int IPCount = 0;
    private int userCount = 0;
    private int eventNum = 0;
    // == ^^^ AUK only ^^^ ==

    private static int debugLine = 0;
    
    private static String[] children;
    private Calendar cal;
    private static Boolean requireLogin = false;
    long nowT,appT,diffT,lagT;

    NGrain LPPGrain=null;
    NGrain LDPGrain=null;
    NGrain LCPGrain=null;

    // vvv AUK only vvv
    private static byte _DISCUSS_STATUS = 0;
    private static byte _CLOUD_STATUS = 0;
    private static byte _POINTER_STATUS = 0;
    private static byte _DROPLET_STATUS = 0;
    private static int _DROPLET_VOLUME = 100;
    private static byte _CLOUD_SOUND_STATUS = 0;
    private static int _CLOUD_SOUND_VOLUME = 100;
    private static byte _POINTER_TONE_STATUS = 0;
    private static int _POINTER_TONE_VOLUME = 100;
    private static int _SYNTH_VOLUME = 100;
    private String _SEND_PROMPT_ON = new String ("Auksalaq NOMADS");
    private ArrayList<String> discussStringCached = new ArrayList<String>(Arrays.asList(" "));
    private static int MAX_CACHED_DISCUSS_STRINGS = 15;
    // ^^^ AUK only ^^^

    int iDay;
    FileOutputStream out; // declare a file output object
    PrintStream p; // declare a print stream object



    // ============================================================
    // CONSTRUCTOR FUNCTION

    public NomadServer(int port) {

	for (int i=0;i<MAX_THREAD_IDS;i++) {
	    setClientThreadNum(i,-1);
	    //	    clientDisplayNumFromThreadID[i] = -1;
	}

	// vvv AUK only vvv
	for (int i=0;i<MAX_DISP_THR;i++) {
	    setDispClientThreadNum(i,-1);
	    setDispClient(i,null);
	}
	// ^^^ AUK only 

	// DT - deleted 2/10/13
	// for (int i=0;i<MAX_THREADS;i++) {
	//     setClient(i,null);
	// }

	try {  
	    NGlobals.sPrint("  Binding to port " + port + ", please wait  ...");
	    server = new ServerSocket(port);  
	    NGlobals.sPrint("  Server started: " + server);
	    start(); 
	    // nECThread = new NomadsErrCheckThread(this);
	    // nECThread.start();
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

    // H1

    // ================================================================
    //    handle ( THREAD_ID , grain )
    // ================================================================

    public void handle(int THREAD_ID, NGrain myGrain)  {  
	String tUser, IP, tempString;
	Boolean tLoginStatus = false;
	int cNum = -1;
	int cIPNum = -1;
	int nBlocks; //number of "blocks" of data
	int tCNum;
	int tMDNum;

	byte incAppCmd, incAppDataType;
	int incAppDataLen;
	byte incAppID;

	int[] x1 = new int[1];
	int[] x2 = new int[2];
	int[] x3 = new int[3];

	NomadServerThread currentClient;
	NomadServerThread inClient;
	int inClientNum;

	// Do the following for EACH client

	NGrain inGrain;
	byte tCommand;

	if (myGrain == null) {
	    NGlobals.dtPrint("handle() got BOGUS (5) GRAIN - PUNTING");
	    return;
	}

	// 1: READ =================================================================================================================

	
	

	// Read in relevant SAND header info
	incAppID = myGrain.appID;
	incAppCmd = myGrain.command;
	incAppDataType = myGrain.dataType;
	incAppDataLen = myGrain.dataLen;

	if (incAppCmd != NCommand.SEND_SPRITE_XY) {
	    NGlobals.sPrint("--------------------------------------------------------------------[" + debugLine++ + "]");

	    NGlobals.sPrint("======= READING =======");
	    NGlobals.sPrint("\tcientCount = " + getClientCount());
	    NGlobals.sPrint("\tmainDisplayClientCount = " + getDispClientCount());

	    // Print out at the SERVER level
	    if (true) {
		NGlobals.sPrint("appID: " + NAppID.printID(incAppID));
		NGlobals.sPrint("command: " + NCommand.printCmd(incAppCmd));
		NGlobals.sPrint("dataType: " + NDataType.printDT(incAppDataType));
		NGlobals.sPrint("dataLen: " + incAppDataLen);
	    }
	}
	else {
	    NGlobals.dtPrint("  -> SEND_SPRITE_XY/tcientCount = " + getClientCount() + "/tmainDisplayClientCount = " + getDispClientCount());

	}

	tCNum = getClientThreadNum(THREAD_ID);
	if (tCNum == -1) {
	    NGlobals.dtPrint("handle() got NULL THREAD PUNTING");
	    removeByThreadID(THREAD_ID);
	    return;
	}

	//myGrain.print();

	// Thread admin stuff ---------------------------------------------------------------------

	// NGlobals.dtPrint("getClient: 1");
	currentClient = getClient(tCNum);

	if (currentClient == null) {
	    NGlobals.dtPrint("handle() got BOGUS (6) currentClient " + tCNum + "PUNTING");
	    removeByPos(tCNum);
	    checkClients();
	    return;
	}


	// Login and THREAD registration ----------------------------------------------------------

	// 1: check if client thread is registered
	//    if not reg, REGISTER the client's appID with the SERVER client thread
	//
	//    otherwise KICK if not registering as the very first thing
	//

	if (currentClient.getAppID() == -1) {
	    if (incAppCmd != NCommand.REGISTER) {
		NGlobals.sPrint("ERROR:  you must REGISTER your app first before sending data\n");
		removeByThreadID(THREAD_ID);
		return;
	    }
	    else {
		NGlobals.sPrint("===== REGISTERING (ONE TIME) =====");
		NGlobals.sPrint("  Setting client[" + tCNum + "] incAppID to: " + NAppID.printID(incAppID));
		currentClient.setAppID(incAppID);
		setClientThrList(tCNum,THREAD_ID);
		setClientNumAppIDList(tCNum,incAppID);

		// Add to MAIN_DISPLAY cache for speedup, hopefully now threadsafe too

		if (incAppID == NAppID.OPERA_MAIN) {
		    int TDC = getDispClientCount();
		    int TDL = getDispClientsLength();
		    int TID = currentClient.getThreadID();

		    NGlobals.sPrint("appID: " + NAppID.printID(incAppID));
		    if (TDC < TDL) {  
			NGlobals.sPrint("  ----------------> Adding client to OPERA_MAIN cache @ " + getDispClientCount());
			setDispClient(TDC,currentClient);

			setDispClientThreadNum(TID, TDC);
			// OLDL:  mainDisplayClientPosPtr[mainDisplayClientCount] = currentClient.getPos(); // xxx cgetClientCount();
			// NOW: not sure we need this anymore
			//			clientDisplayNumFromThreadID[THREAD_ID] = (short)mainDisplayClientCount;
			setDispClientCount(TDC+1);
			NGlobals.sPrint("mainDisplayClientCount = " + getDispClientCount());
		    }
		    else {
			NGlobals.sPrint("  ERROR:  current client > MAX_DISPLAY_THREADS, client NOT added to DISPLAY cache");
		    }
		}
	    }
	}
	
	// NGlobals.sPrint("----------- PRE-INIT ---------------");
	// NGlobals.sPrint("appID: " + NAppID.printID(incAppID));


	// DT CLASS server checks login here

	// 2: INIT =================================================================================================================
	
	// TODO:  what other apps need init?  EG., OperaMain display too? - to set last state in case of a crash
	//IP = currentClient.getIP();   	// Grab IP (more for data logging)

	// XMERGE(1) This made into a nice loop in CLASSROOM, TODO

	// INIT for CONDUCTOR_PANEL - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - WI

	// NGlobals.dtPrint("getClient: 2");
	currentClient = getClient(tCNum);

	if (currentClient == null) {
	    NGlobals.dtPrint("BOGUS (7) CLIENT (null) " + tCNum + " removing but NOT PUNTING");
	    removeByPos(tCNum);
	    checkClients();
	    // return;
	}
	else if (currentClient.threadSand.getRun() == false) {
	    NGlobals.dtPrint("BOGUS (8) CLIENT (getRun)" + tCNum + " removing");
	    removeByPos(tCNum);
	}
	

	if ((currentClient.getAppID() == NAppID.CONDUCTOR_PANEL) && (currentClient.getButtonInitStatus() == 0)) {

	    NGlobals.lPrint("  Sending button states to CONDUCTOR PANEL from SERVER.");
	    byte d[] = new byte[1];

	    d[0] = _DISCUSS_STATUS;
	    if (currentClient.threadSand.getRun()) {
		currentClient.threadSand.sendGrainL(NAppID.SERVER, NCommand.SET_DISCUSS_STATUS, NDataType.UINT8, 1, d);
	    }
	    else {
		removeByThreadID(currentClient.getThreadID());
	    }
	    NGlobals.lPrint("_DISCUSS_STATUS: " + d[0]);

	    d[0] = _CLOUD_STATUS;
	    if (currentClient.threadSand.getRun()) {
		currentClient.threadSand.sendGrainL(NAppID.SERVER, NCommand.SET_CLOUD_STATUS, NDataType.UINT8, 1, d);
	    }
	    else {
		removeByThreadID(currentClient.getThreadID());
	    }
	    NGlobals.lPrint("_CLOUD_STATUS:  " + d[0]);

	    d[0] = _POINTER_STATUS;
	    if (currentClient.threadSand.getRun()) {
		currentClient.threadSand.sendGrainL(NAppID.SERVER, NCommand.SET_POINTER_STATUS, NDataType.UINT8, 1, d);
	    }
	    else {
		removeByThreadID(currentClient.getThreadID());
	    }

	    NGlobals.lPrint("_POINTER_STATUS:  " + d[0]);

	    int ix[] = new int[1];
	    ix[0] = _DROPLET_VOLUME;
	    if (currentClient.threadSand.getRun()) {
		currentClient.threadSand.sendGrainL(NAppID.SERVER, NCommand.SET_DROPLET_VOLUME, NDataType.INT32, 1, ix);
	    }
	    else {
		removeByThreadID(currentClient.getThreadID());
	    }

	    NGlobals.lPrint("_DROPLET_VOLUME:  " + ix[0]);

	    d[0] = _DROPLET_STATUS;
	    if (currentClient.threadSand.getRun()) {
		currentClient.threadSand.sendGrainL(NAppID.SERVER, NCommand.SET_DROPLET_STATUS, NDataType.UINT8, 1, d);
	    }
	    else {
		removeByThreadID(currentClient.getThreadID());
	    }

	    NGlobals.lPrint("_DROPLET_STATUS:  " + d[0]);

	    d[0] = _CLOUD_SOUND_STATUS;
	    if (currentClient.threadSand.getRun()) {
		currentClient.threadSand.sendGrainL(NAppID.SERVER, NCommand.SET_CLOUD_SOUND_STATUS, NDataType.UINT8, 1, d);
	    }
	    else {
		removeByThreadID(currentClient.getThreadID());
	    }

	    NGlobals.lPrint("_CLOUD_SOUND_STATUS:  " + d[0]);

	    ix[0] = _CLOUD_SOUND_VOLUME;
	    if (currentClient.threadSand.getRun()) {
		currentClient.threadSand.sendGrainL(NAppID.SERVER, NCommand.SET_CLOUD_SOUND_VOLUME, NDataType.INT32, 1, ix);
	    }
	    else {
		removeByThreadID(currentClient.getThreadID());
	    }

	    NGlobals.lPrint("_CLOUD_SOUND_VOLUME:  " + ix[0]);
			
	    d[0] = _POINTER_TONE_STATUS;
	    if (currentClient.threadSand.getRun()) {
		currentClient.threadSand.sendGrainL(NAppID.SERVER, NCommand.SET_POINTER_TONE_STATUS, NDataType.UINT8, 1, d);
	    }
	    else {
		removeByThreadID(currentClient.getThreadID());
	    }

	    NGlobals.lPrint("_POINTER_TONE_STATUS:  " + d[0]);

	    ix[0] = _POINTER_TONE_VOLUME;
	    if (currentClient.threadSand.getRun()) {
		currentClient.threadSand.sendGrainL(NAppID.SERVER, NCommand.SET_POINTER_TONE_VOLUME, NDataType.INT32, 1, ix);
	    }
	    else {
		removeByThreadID(currentClient.getThreadID());
	    }

	    NGlobals.lPrint("_POINTER_TONE_VOLUME:  " + ix[0]);

	    ix[0] = _SYNTH_VOLUME;
	    if (currentClient.threadSand.getRun()) {
		currentClient.threadSand.sendGrainL(NAppID.SERVER, NCommand.SET_SYNTH_VOLUME, NDataType.INT32, 1, ix);
	    }
	    else {
		removeByThreadID(currentClient.getThreadID());
	    }

	    NGlobals.lPrint("_SYNTH_VOLUME:  " + ix[0]);
			
	    String tString = _SEND_PROMPT_ON;
	    byte[] tStringAsBytes = tString.getBytes();
	    int tLen = _SEND_PROMPT_ON.length();
	    if (currentClient.threadSand.getRun()) {
		currentClient.threadSand.sendGrainL(NAppID.SERVER, (byte)NCommand.SEND_PROMPT_ON, (byte)NDataType.CHAR, tLen, tStringAsBytes);
	    }
	    else {
		removeByThreadID(currentClient.getThreadID());
	    }

	    NGlobals.lPrint("ACP: Prompt " + tString + " sent"); 

	    currentClient.setButtonInitStatus((byte)1);
	    // tempString = new String("CNT:" + maxClients);
	    // getClient(cNum).send((byte)NAppID.MONITOR, tempString);
	    // NGlobals.lPrint("  Sending " + tempString + " to MONITOR client [" + cNum + "] from SERVER");
			
	}

	// XMERGE(1) This made into a nice loop in CLASSROOM, TODO

	// INIT for OPERA_CLIENT -- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - WI
	// NGlobals.dtPrint("getClient: 3");
	currentClient = getClient(tCNum);

	if (currentClient == null) {
	    NGlobals.dtPrint("BOGUS (9) CLIENT (null) " + tCNum + " removing but NOT PUNTING");
	    removeByPos(tCNum);
	    checkClients();
	    // return;
	}
	else if (currentClient.threadSand.getRun() == false) {
	    NGlobals.dtPrint("BOGUS (10) CLIENT (getRun)" + tCNum + " removing but NOT PUNTING");
	    removeByPos(tCNum);
	    checkClients();
	}

	// NGlobals.sPrint("----------- OC-INIT ---------------");
	// NGlobals.sPrint("appID: " + NAppID.printID(incAppID));

	if ((incAppID == NAppID.OPERA_CLIENT) && (currentClient.getButtonInitStatus() == 0)) {
	    

	    NGlobals.lPrint("  Sending button states to OPERA CLIENT from SERVER / CONDUCTOR_PANEL.");
	    byte d[] = new byte[1];

	    d[0] = _DISCUSS_STATUS;
	    if (currentClient.threadSand.getRun()) {
		currentClient.threadSand.sendGrainL(NAppID.CONDUCTOR_PANEL, NCommand.SET_DISCUSS_STATUS, NDataType.UINT8, 1, d);
	    }
	    else {
		removeByThreadID(currentClient.getThreadID());
	    }

	    NGlobals.lPrint("_DISCUSS_STATUS: " + d[0]);

	    d[0] = _CLOUD_STATUS;
	    if (currentClient.threadSand.getRun()) {
		currentClient.threadSand.sendGrainL(NAppID.CONDUCTOR_PANEL, NCommand.SET_CLOUD_STATUS, NDataType.UINT8, 1, d);
	    }
	    else {
		removeByThreadID(currentClient.getThreadID());
	    }

	    NGlobals.lPrint("_CLOUD_STATUS:  " + d[0]);

	    d[0] = _POINTER_STATUS;
	    if (currentClient.threadSand.getRun()) {
		currentClient.threadSand.sendGrainL(NAppID.CONDUCTOR_PANEL, NCommand.SET_POINTER_STATUS, NDataType.UINT8, 1, d);
	    }
	    else {
		removeByThreadID(currentClient.getThreadID());
	    }

	    NGlobals.lPrint("_POINTER_STATUS:  " + d[0]);

	    int ix[] = new int[1];
	    ix[0] = _DROPLET_VOLUME;
	    if (currentClient.threadSand.getRun()) {
		currentClient.threadSand.sendGrainL(NAppID.CONDUCTOR_PANEL, NCommand.SET_DROPLET_VOLUME, NDataType.INT32, 1, ix);
	    }
	    else {
		removeByThreadID(currentClient.getThreadID());
	    }

	    NGlobals.lPrint("_DROPLET_VOLUME:  " + ix[0]);

	    d[0] = _DROPLET_STATUS;
	    if (currentClient.threadSand.getRun()) {
		currentClient.threadSand.sendGrainL(NAppID.CONDUCTOR_PANEL, NCommand.SET_DROPLET_STATUS, NDataType.UINT8, 1, d);
	    }
	    else {
		removeByThreadID(currentClient.getThreadID());
	    }

	    NGlobals.lPrint("_DROPLET_STATUS:  " + d[0]);

	    d[0] = _CLOUD_SOUND_STATUS;
	    if (currentClient.threadSand.getRun()) {
		currentClient.threadSand.sendGrainL(NAppID.CONDUCTOR_PANEL, NCommand.SET_CLOUD_SOUND_STATUS, NDataType.UINT8, 1, d);
	    }
	    else {
		removeByThreadID(currentClient.getThreadID());
	    }

	    NGlobals.lPrint("_CLOUD_SOUND_STATUS:  " + d[0]);

	    ix[0] = _CLOUD_SOUND_VOLUME;
	    if (currentClient.threadSand.getRun()) {
		currentClient.threadSand.sendGrainL(NAppID.CONDUCTOR_PANEL, NCommand.SET_CLOUD_SOUND_VOLUME, NDataType.INT32, 1, ix);
	    }
	    else {
		removeByThreadID(currentClient.getThreadID());
	    }

	    NGlobals.lPrint("_CLOUD_SOUND_VOLUME:  " + ix[0]);
			
	    d[0] = _POINTER_TONE_STATUS;
	    if (currentClient.threadSand.getRun()) {
		currentClient.threadSand.sendGrainL(NAppID.CONDUCTOR_PANEL, NCommand.SET_POINTER_TONE_STATUS, NDataType.UINT8, 1, d);
	    }
	    else {
		removeByThreadID(currentClient.getThreadID());
	    }

	    NGlobals.lPrint("_POINTER_TONE_STATUS:  " + d[0]);

	    ix[0] = _POINTER_TONE_VOLUME;
	    if (currentClient.threadSand.getRun()) {
		currentClient.threadSand.sendGrainL(NAppID.CONDUCTOR_PANEL, NCommand.SET_POINTER_TONE_VOLUME, NDataType.INT32, 1, ix);
	    }
	    else {
		removeByThreadID(currentClient.getThreadID());
	    }

	    NGlobals.lPrint("_POINTER_TONE_VOLUME:  " + ix[0]);
			
	    String tString = _SEND_PROMPT_ON;
	    byte[] tStringAsBytes = tString.getBytes();
	    int tLen = _SEND_PROMPT_ON.length();
	    if (currentClient.threadSand.getRun()) {
		currentClient.threadSand.sendGrainL(NAppID.SERVER, (byte)NCommand.SEND_PROMPT_ON, (byte)NDataType.CHAR, tLen, tStringAsBytes);
	    }
	    else {
		removeByThreadID(currentClient.getThreadID());
	    }

	    NGlobals.lPrint("ACP: Prompt " + tString + " sent"); 

	    // XMERGE(2):  is this happening in CLASSROOM?  I don't think we're sending out a buffer of previous DISCUSS entries

	    //Cached Discuss String
	    for (int i=0;i<discussStringCached.size();i++) {
		tString = discussStringCached.get(i);
		tStringAsBytes = tString.getBytes();
		tLen = tString.length();
		if (currentClient.threadSand.getRun()) {
		    currentClient.threadSand.sendGrainL(NAppID.SERVER, (byte)NCommand.SEND_CACHED_DISCUSS_STRING, (byte)NDataType.CHAR, tLen, tStringAsBytes);
		}
		else {
		    removeByThreadID(currentClient.getThreadID());
		}

		NGlobals.lPrint("ACP: Cached Discuss String " + tString + " arrayElt# " + i + " sent");
	    }
			
	    currentClient.setButtonInitStatus((byte)1);
	}

	// INIT for OPERA_MAIN - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - WI

	// HACKISH - sends message back to server, mimicking a send from the CONDUCTOR_PANEL
	// effectively getting the message back from the server as if the CP had sent it

	// FIX:  just have the cached synth volume sent when an OM connects (along with the other attributes like alpha, etc.. perhaps even cached text and cloud)

	// NGlobals.dtPrint("getClient: 4");
	currentClient = getClient(tCNum);

	if (currentClient == null) {
	    NGlobals.dtPrint("BOGUS (11) CLIENT (null) " + tCNum + " removing but NOT PUNTING");
	    removeByPos(tCNum);
	    checkClients();
	    // return;

	}
	else if (currentClient.threadSand.getRun() == false) {
	    NGlobals.dtPrint("BOGUS (12) CLIENT (getRun)" + tCNum + " removing but NOT PUNTING");
	    removeByPos(tCNum);
	    checkClients();
	}
	else if (incAppID == NAppID.OPERA_MAIN) {
	    NGlobals.sPrint("----------- PRE-HACKISH OPERA_MAIN VOLUME SET ---------------");
	    NGlobals.sPrint("  Sending button states to OPERA MAIN from SERVER / CONDUCTOR_PANEL.");
	    int ix[] = new int[1];
	    ix[0] = _SYNTH_VOLUME;
	    if (currentClient.threadSand.getRun()) {
		currentClient.threadSand.sendGrainL(NAppID.CONDUCTOR_PANEL, NCommand.SET_SYNTH_VOLUME, NDataType.INT32, 1, ix);
	    }
	    else {
		removeByThreadID(currentClient.getThreadID());
	    }

	    NGlobals.sPrint("_SYNTH_VOLUME:  " + ix[0]);
	}


	// ====================================================================================================W
	// BEGIN Main data routing code
	//
	//    each    if (incAppID ==   )    block below corresponds to a single app's input data GRAIN
	//    depending on who is sending us data
	//    we cycle through all (or a subset of) clientList and send data out
	//
	// ====================================================================================================W

	// 3 ---- WRITE ===========================================================================================================

	if (incAppCmd != NCommand.SEND_SPRITE_XY) {
	    NGlobals.sPrint("===== WRITING =====");
	}

	// FILTER out some commands that don't need to go past this point

	if (incAppCmd != NCommand.REGISTER) {
	    
	    // TODO:  add code to cache the button statuses
	    //        IDEA:  use button_status[array]

	    // XMERGE:  the above is done in CLASSROOM

	    // XMERGE(4):  in CLASSROOM there are a bunch of rejects here, possible that it's done below with if() exclusions

	    // Cache CONDUCTOR_PANEL and send out data to clients and main  = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 

	    // NGlobals.sPrint("----------- APPCMD != REGISTER (top level of WRITE section) ---------------");
	    // NGlobals.sPrint("appID: " + NAppID.printID(incAppID));



	    // I. ==> incoming appID = CONDUCTOR_PANEL = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
	    
	    if (incAppID == NAppID.CONDUCTOR_PANEL) {
		// scroll through all clientList // TODO: FIX: SPEEDUP: change to separate APPID[client] arrays

		NGlobals.sPrint("----------- appID = CONDUCTOR PANEL ---------------");

		// Store various FEATURE STATES - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

		if (incAppCmd == NCommand.SET_DISCUSS_STATUS) {
		    _DISCUSS_STATUS = myGrain.bArray[0];
		}

		else if (incAppCmd == NCommand.SET_CLOUD_STATUS) {
		    _CLOUD_STATUS = myGrain.bArray[0];
		}

		else if (incAppCmd == NCommand.SET_POINTER_STATUS) {
		    _POINTER_STATUS = myGrain.bArray[0];
		}

		else if (incAppCmd == NCommand.SET_DROPLET_STATUS) {
		    _DROPLET_STATUS = myGrain.bArray[0];
		}

		else if (incAppCmd == NCommand.SET_DROPLET_VOLUME) {
		    _DROPLET_VOLUME = myGrain.iArray[0];
		}

		else if (incAppCmd == NCommand.SET_CLOUD_SOUND_STATUS) {
		    _CLOUD_SOUND_STATUS = myGrain.bArray[0];
		}

		else if (incAppCmd == NCommand.SET_CLOUD_SOUND_VOLUME) {
		    _CLOUD_SOUND_VOLUME = myGrain.iArray[0];
		}
				
		else if (incAppCmd == NCommand.SET_POINTER_TONE_STATUS) {
		    _POINTER_TONE_STATUS = myGrain.bArray[0];
		}

		else if (incAppCmd == NCommand.SET_POINTER_TONE_VOLUME) {
		    _POINTER_TONE_VOLUME = myGrain.iArray[0];
		}

		else if (incAppCmd == NCommand.SET_SYNTH_VOLUME) {
		    _SYNTH_VOLUME = myGrain.iArray[0];
		}
		else if (incAppCmd == NCommand.SEND_PROMPT_ON) {
		    _SEND_PROMPT_ON = new String(myGrain.bArray);
		}

		// semi - speedup, now uses separate list for OPERA_MAIN_DISPLAY
		for (int c = 0; c < getDispClientCount(); c++) {
		    
		    // Get the client off the master list
		    currentClient = getDispClient(c);
		    if (currentClient == null) {
			NGlobals.dtPrint("BOGUS (13) CLIENT (null) " + c + " removing");
			removeByPos(c);
		    }
		    else if (currentClient.threadSand.getRun() == false) {
			NGlobals.dtPrint("BOGUS (14) CLIENT (getRun)" + c + " removing");
			removeByPos(c);
		    }


		    NGlobals.sPrint("   sending to ===> display client[" + c + "] w/ appID = " + NAppID.printID((byte)currentClient.getAppID()));
		    if (currentClient.threadSand.getRun()) {
			currentClient.threadSand.sendGrainL(myGrain);
		    }
		    else {
			removeByThreadID(currentClient.getThreadID());
		    }
		}

		for (int c = 0; c < getClientCount(); c++) {
		    // Get the client off the master list
		    // NGlobals.dtPrint("getClient: 5");
		    currentClient = getClient(c);

		    if (currentClient == null) {
			NGlobals.dtPrint("BOGUS (15) CLIENT (null) " + c + " removing");
			removeByPos(c);
			checkClients();
			c--;
		    }
		    else if (currentClient.threadSand.getRun() == false) {
			NGlobals.dtPrint("BOGUS (16) CLIENT (getRun)" + c + " removing");
			removeByPos(c);
			checkClients();
			c--;
		    }

		    // XMERGE:  seems like we'd also be sending data back to "the sender" here


		    // send data from CONDUCTOR_PANEL to ===> CONDUCTOR PANEL - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		    else if (currentClient.getAppID() == NAppID.CONDUCTOR_PANEL) {
			if (incAppCmd == NCommand.SEND_PROMPT_ON) {
			    NGlobals.sPrint("   sending to ===> client[" + c + "] w/ appID = " + NAppID.printID((byte)currentClient.getAppID()));
			    //myGrain.print();
			    // Write the data out
			    if (currentClient.threadSand.getRun()) {
				currentClient.threadSand.sendGrainL(myGrain);
			    }
			    else {
				removeByThreadID(currentClient.getThreadID());
			    }

			}
			else if (incAppCmd == NCommand.SEND_PROMPT_OFF) {
			    NGlobals.sPrint("   sending to ===> client[" + c + "] w/ appID = " + NAppID.printID((byte)currentClient.getAppID()));
			    //myGrain.print();
			    // Write the data out
			    if (currentClient.threadSand.getRun()) {
				currentClient.threadSand.sendGrainL(myGrain);
			    }
			    else {
				removeByThreadID(currentClient.getThreadID());
			    }

			}
		    }


		    // send data from CONDUCTOR_PANEL to ===> OPERA CLIENT - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		    else if (currentClient.getAppID() == NAppID.OPERA_CLIENT) {
			NGlobals.dtPrint(">>OPERA_CLIENT");
			if ((incAppCmd != NCommand.SET_CLOUD_ALPHA) && 
			    (incAppCmd != NCommand.SET_DISCUSS_ALPHA) &&
			    (incAppCmd != NCommand.SET_POINTER_ALPHA)) {

			    NGlobals.sPrint("   sending to ===> client[" + c + "] w/ appID = " + NAppID.printID((byte)currentClient.getAppID()));
			    //myGrain.print();
			    // Write the data out
			    if (currentClient.threadSand.getRun()) {
				currentClient.threadSand.sendGrainL(myGrain);
			    }
			    else {
				removeByThreadID(currentClient.getThreadID());
			    }

			}
		    }

		    // send data from CONDUCTOR_PANEL to ===> OPERA MAIN - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		    // if (currentClient.getAppID() == NAppID.OPERA_MAIN) {
		    // 	NGlobals.dtPrint(">>OPERA_MAIN");
		    // 	if ((incAppCmd != NCommand.SET_DROPLET_VOLUME) && 
		    // 	    (incAppCmd != NCommand.SET_DROPLET_STATUS)) {
		    // 	    NGlobals.sPrint("   sending to ===> client[" + c + "] w/ appID = " + NAppID.printID((byte)currentClient.getAppID()));
		    // 	    //myGrain.print();
		    // 	    // Write the data out
		    // 	    if (currentClient.threadSand.getRun()) {
		    // 		currentClient.threadSand.sendGrainL(myGrain);
		    // 	    }
		    // 	    else {
		    // 		removeByThreadID(currentClient.getThreadID());
		    // 	    }

		    // 	}
		    // }

		}
	    }  

	    // II. ==> incoming appID = OC_DISCUSS = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 

	    else if (incAppID == NAppID.OC_DISCUSS) {

		NGlobals.sPrint("----------- appID = OC_DISCUSS ---------------");
		NGlobals.sPrint("appID: " + NAppID.printID(incAppID));
		
		// Double check that the cmd is SEND_MESSAGE and that DISCUSS_STATUS IS ON

		if (incAppCmd == NCommand.SEND_MESSAGE && (_DISCUSS_STATUS == 1)) {
			    	
		    //caches discuss strings from input
		    discussStringCached.add(new String(myGrain.bArray));
		    if (discussStringCached.size() > MAX_CACHED_DISCUSS_STRINGS) {
			discussStringCached.remove(0);
		    }

		    // send out ==> to all clients
			    	
		    // semi - speedup, now uses separate list for OPERA_MAIN_DISPLAY
		    for (int c = 0; c < getDispClientCount(); c++) {

			// Get the client off the master list
			currentClient = getDispClient(c);
			if (currentClient == null) {
			    NGlobals.dtPrint("BOGUS (17) CLIENT (null) " + c + " removing");
			    removeByPos(c);
			}
			else if (currentClient.threadSand.getRun() == false) {
			    NGlobals.dtPrint("BOGUS (18) CLIENT (getRun)" + c + " removing");
			    removeByPos(c);
			}
			else {

			    NGlobals.sPrint("   sending to ===> display client[" + c + "] w/ appID = " + NAppID.printID((byte)currentClient.getAppID()));
			    if (currentClient.threadSand.getRun()) {
				currentClient.threadSand.sendGrainL(myGrain);
			    }
			    else {
				removeByThreadID(currentClient.getThreadID());
			    }
			}
		    }

		    // then scroll through the rest clientList // TODO: FIX: SPEEDUP: change to separate APPID[client] arrays
		    for (int c = 0; c < getClientCount(); c++) {
			// Get the client off the master list
			// NGlobals.dtPrint("getClient: 6");
			currentClient = getClient(c);
			if (currentClient == null) {
			    NGlobals.dtPrint("BOGUS (19) CLIENT (null) " + c + " removing");
			    removeByPos(c);
			    checkClients();
			    c--;
			}
			else if (currentClient.threadSand.getRun() == false) {
			    NGlobals.dtPrint("BOGUS (20) CLIENT (getRun)" + c + " removing");
			    removeByPos(c);
			    checkClients();
			    c--;
			}

			// send data to ===> OPERA CLIENT - - - - - - - - -  - - - - - - - - - - - - - - - - - - - -
			else if (currentClient.getAppID() == NAppID.OPERA_CLIENT) {
			    NGlobals.sPrint("   sending to ===> client[" + c + "] w/ appID = " + NAppID.printID((byte)currentClient.getAppID()));
			    //myGrain.print();
			    // Write the data out
			    if (currentClient.threadSand.getRun()) {
				// inClientNum = getClientThreadNum(THREAD_ID);
				// inClient = getClient(inClientNum);
				currentClient.threadSand.sendGrainL(myGrain);
			    }
			    else {
				removeByThreadID(currentClient.getThreadID());
			    }
			}

			// // send data to ===> OPERA MAIN - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
			// else if (currentClient.getAppID() == NAppID.OPERA_MAIN) {
			//     NGlobals.sPrint("   sending to ===> client[" + c + "] w/ appID = " + NAppID.printID((byte)currentClient.getAppID()));
			//     //myGrain.print();
			//     // Write the data out
			//     if (currentClient.threadSand.getRun()) {
			// 	currentClient.threadSand.sendGrainL(myGrain);
			//     }
			//     else {
			// 	removeByThreadID(currentClient.getThreadID());
			//     }

			// }

		    }
		}   
	    }

	    // III. incoming appID = OC_CLOUD = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 

	    else if (incAppID == NAppID.OC_CLOUD && (_CLOUD_STATUS == 1)) {

		NGlobals.sPrint(" -------------------- appID == OC_CLOUD ---------------");
		NGlobals.sPrint("appID: " + NAppID.printID(incAppID));

		if (incAppCmd == NCommand.SEND_MESSAGE) {

		    // FIXED: SPEDUP: changed to separate APPID[client] arrays
		    NGlobals.sPrint("mainDisplayClientCount = " + getDispClientCount());

		    for (int c = 0; c < getDispClientCount(); c++) {
			// Get the client off the master list
			NGlobals.sPrint("   sending to ===> display client[" + c + "]");
			NGlobals.dtPrint("     getDispClient: 7");
			currentClient = getDispClient(c);
			if (currentClient == null) {
			    NGlobals.dtPrint("BOGUS (21) CLIENT (null) " + c + " removing");
			    removeByPos(c);
			}
			else if (currentClient.threadSand.getRun() == false) {
			    NGlobals.dtPrint("BOGUS (22) CLIENT (getRun)" + c + " removing");
			    removeByPos(c);
			}
			else {

			    NGlobals.sPrint("     w/ appID = " + NAppID.printID((byte)currentClient.getAppID()));
			    
			    // XMERGE:  need to fix this for the main display client list
			    
			    if (currentClient.threadSand.getRun()) {
				currentClient.threadSand.sendGrainL(myGrain);
			    }
			}

		    }

		}

	    }   
	    
	    // IV. incoming appID = OC_POINTER = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
	    //
	    //   only need to send to main displays (no need to cycle through list)
    
	    else if ((incAppID == NAppID.OC_POINTER || incAppID == NAppID.JOC_POINTER) && (_POINTER_STATUS == 1)) {
		if (incAppCmd == NCommand.SEND_SPRITE_XY) {
		    int TCN = 0;

		    // Speedup, now uses separate list for OPERA_MAIN_DISPLAY
		    for (int c = 0; c < getDispClientCount(); c++) {

			// Get the client off the master list
			currentClient = getDispClient(c);

			if (currentClient == null) {
			    NGlobals.dtPrint("BOGUS (23) CLIENT (null) " + c + " removing");
			    removeByPos(c);
			}
			else if (currentClient.threadSand.getRun() == false) {
			    NGlobals.dtPrint("BOGUS (24) CLIENT (getRun)" + c + " removing");
			    removeByPos(c);
			}
			else {

			    // NGlobals.sPrint("   sending to ===> display client[" + c + "] w/ appID = " + NAppID.printID((byte)currentClient.getAppID()));
			    // 
			    // CUSTOM DATA PACKING into 3 ints: THREAD_ID, x, y
			    x3[0] = THREAD_ID;
			    x3[1] = myGrain.iArray[0];
			    x3[2] = myGrain.iArray[1];
			    if (currentClient.threadSand.getRun()) {
				currentClient.threadSand.sendGrainL(incAppID, NCommand.SEND_SPRITE_THREAD_XY, NDataType.INT32, 3, x3);
			    }
			    else {
				removeByThreadID(currentClient.getThreadID());
			    }
			}
		    }
		}
	    }
	}

	else if (false) {
	    NGlobals.sPrint("===> sending PASSTHROUGH network data");
	    for (int c = 0; c < getClientCount(); c++) {

		// Get the client off the master list
		// NGlobals.dtPrint("getClient: 9");
		currentClient = getClient(c);
		if (currentClient == null) {
		    NGlobals.dtPrint("BOGUS (25) CLIENT (null) " + c + " removing");
		    removeByPos(c);
		    checkClients();
		    c--;
		}
		else if (currentClient.threadSand.getRun() == false) {
		    NGlobals.dtPrint("BOGUS (26) CLIENT (getRun)" + c + " removing");
		    removeByPos(c);
		    checkClients();
		    c--;
		}
		else {
		    NGlobals.sPrint("===> client[" + c + "] w/ appID = " + currentClient.getAppID());
		    
		    // Extra step for SOUND_SWARM_DISPLAY: need to send THREAD_ID
		    
		    
		    //myGrain.print();
		    // Write the data out
		    if (currentClient.threadSand.getRun()) {
			currentClient.threadSand.sendGrainL(myGrain);
		    }
		    else {
			removeByThreadID(currentClient.getThreadID());
		    }
		}

	    }   
	    // END --------------------------------------------------------------------
	    if (incAppCmd != NCommand.SEND_SPRITE_XY) {
		NGlobals.sPrint("handle(DONE) " + THREAD_ID + ":" + myGrain.appID);
	    }
	}

	// DT 2-16-13:  moved this from outside the above "if (false)" loop
	//              saw it was in the classroom server code
	// Free up memory
	if (myGrain != null) {
	    myGrain = null;
	}


	if (incAppCmd != NCommand.SEND_SPRITE_XY) {
	    NGlobals.sPrint("---------------------------------------------------------------- handle() done");
	}	
    }

    // =====================================================================================================
    // END main data routing code --- handle() fn
    // =====================================================================================================

    // -- remove by THREAD_ID --------------------------------------

    // R2

    public synchronized void removeByThreadID(int THREAD_ID) {  
	NGlobals.sPrint("removeByThreadID ----------");

	int pos,dPos,tC,c;
	
	int tID, tAID;
	NomadServerThread toTerminate,tT;
	NomadServerThread currentClient;

	int ix[] = new int[1];

	pos = getClientThreadNum(THREAD_ID);

	NGlobals.dtPrint("(rbtid) removing THREAD_ID " + THREAD_ID + " at pos " + pos);
	NGlobals.dtPrint("(rbtid)     clientCount = " + getClientCount());
	NGlobals.dtPrint("(rbtid)     clients.length = " + getClientsLength());

	// remove from lists ---------------------------------------------------------------------------------

	// I. SEND REMOVE/DELETE TO SWARM DISPLAY CLIENTS -----------------------------------  XMERGE:  AUK only

	// if (false) {

	if (pos >= 0) {
	    toTerminate = getClient(pos);   // DT NOTE:  maybe a better way to do this LATER

	    if (toTerminate == null) {
		NGlobals.dtPrint("  (rbtid) got BOGUS (27) client from getClient() - using cached values");
		tAID = getClientNumAppIDList(pos);
		tID = getClientNumThrList(pos);
	    }
	    else {
		NGlobals.dtPrint(" (rbtid) got ok client from getClient()");
		tAID = toTerminate.getAppID();
		tID = THREAD_ID;
	    }
	    
	    if (tAID == NAppID.OPERA_CLIENT) {  
		// send out to OPERA_MAIN / SOUND_SWARM_DISPLAY - - - - - - - - - - - - - - - - - - - - - - - - -
		// semi - speedup, now uses separate list for OPERA_MAIN_DISPLAY
		for (c = 0; c < getDispClientCount(); c++) {
		    
		    // Get the client off the master list
		    ix[0] = tID;
		    currentClient = getDispClient(c);
		    
		    NGlobals.sPrint("   sending to ===> display client[" + c + "] w/ appID = " + NAppID.printID((byte)currentClient.getAppID()));
		    if (currentClient.threadSand.getRun()) {
			NGlobals.sPrint("Sending DELETE_SPRITE ---> OPERA_MAIN: " + THREAD_ID);
			currentClient.threadSand.sendGrainL(NAppID.SERVER, NCommand.DELETE_SPRITE, NDataType.INT32, 1, ix);
		    }
		    else {
			NGlobals.sPrint("BOGUS (28) getRun() from -> sending DELETE_SPRITE to OPERA_MAIN: " + THREAD_ID);
			removeByThreadID(currentClient.getThreadID());
		    }
		}
	    }
	}
    	 
	// II. DELETE FROM MAIN DISPLAY LIST -----------------------------------  XMERGE:  AUK only
	if (pos >= 0) {
	    // XMERGE:  AUK ONLY
	    // rem from main display list 
	    toTerminate = getClient(pos);
	    
	    if (toTerminate != null) {
		if (toTerminate.getAppID() == NAppID.OPERA_MAIN) {
		    dPos = getDispClientThreadNum(THREAD_ID);
		    setDispClientThreadNum(THREAD_ID, -1);
		    
		    if (dPos < getDispClientCount()-1) {
			NGlobals.sPrint("Removing thread from MAIN_DISPLAY cache" + THREAD_ID + " at " + dPos);
			for (int i = dPos+1; i < getDispClientCount(); i++) {            
			    tT = getDispClient(i);
			    tID = tT.getThreadID();
			    setDispClient((i-1),tT);
			    setDispClientThreadNum(tID,(i-1));
			}
		    }
		    tC = getDispClientCount();
		    setDispClientCount(tC-1);
		}
		// ^^^ AUK ^^^
	    }
	}

	// }  // END if (false) 

	// III. REGULAR LISTS -----------------------------------

	if (pos >= 0) {
	    // XMERGE:  main list -------------------------------------------
	    setClientThreadNum(THREAD_ID, -1);
	    toTerminate = getClient(pos);

	    // rem from client list
	    if (pos < getClientCount()-1) {
		NGlobals.dtPrint("   (rbtid) removing client thread " + THREAD_ID + " at " + pos);
		for (int i = pos+1; i < getClientCount(); i++) {
		    tT = getClient(i);
		    if (tT != null) {
			setClient((i-1), tT);
			tID = tT.getThreadID();
			tAID = tT.getAppID();
			setClientThrList((i-1),tID);
			setClientNumAppIDList((i-1),tAID);
			setClientThreadNum(tID,(i-1));
		    }
		    else {
			checkClients();
		    }
		}
	    }

	    // tC = getClientCount();
	    // setClientCount(tC-1);
	
	    try {  
		if (toTerminate == null) {
		    NGlobals.dtPrint("(  (rbtid)  BOGUS (29) toTerminate - checking client count");
		    checkClients();
		}
		else {
		    NGlobals.dtPrint("   (rbtid) valid toTerminate - TRYING TO SHUT DOWN THREAD >> PROPERLY <<");
		    toTerminate.close(); 
		    toTerminate.stop(); 
		    toTerminate.interrupt();
		    toTerminate = null;
		    checkClients();
		}
	    }
	    catch(IOException ioe) {  
		NGlobals.dtPrint("  (rbtid) error closing thread: " + ioe); 
	    }
	}
	
	checkClients();
	checkDispClients();
    }	     

// -- remove by position --------------------------------------

// R1
public synchronized void removeByPos(int pos) {  
    NGlobals.sPrint("removeByPos ----------");

    int dPos,tC,c;
    int THREAD_ID;
    int tID, tAID;
    NomadServerThread toTerminate,tT;
    NomadServerThread currentClient;

    int ix[] = new int[1];

    NGlobals.sPrint("removing THREAD at pos " + pos);
    NGlobals.sPrint("     clientCount = " + getClientCount());
    NGlobals.sPrint("     clients.length = " + getClientsLength());

    // if (false) {

    // remove from lists ------------------------------------------------
	
    if (pos >= 0) {
	toTerminate = getClient(pos);

	// NGlobals.dtPrint("getClient: 10 (remove)");
	    
	// XMERGE:  AUK only
	// SEND REMOVE/DELETE TO SWARM DISPLAY CLIENTS ---------------------------------------------------
	// NOW:  this should be done via the cached pointers for speeding up

	if (toTerminate != null) {
	    THREAD_ID = toTerminate.getThreadID();
	    if (toTerminate.getAppID() == NAppID.OPERA_CLIENT) {  
		    
		    
		// send out to OPERA_MAIN / SOUND_SWARM_DISPLAY - - - - - - - - - - - - - - - - - - - - - - - - -
		    
		// semi - speedup, now uses separate list for OPERA_MAIN_DISPLAY
		for (c = 0; c < getDispClientCount(); c++) {
			
		    // Get the client off the master list
		    ix[0] = THREAD_ID;
		    currentClient = getDispClient(c);
			
		    NGlobals.sPrint("   sending to ===> display client[" + c + "] w/ appID = " + NAppID.printID((byte)currentClient.getAppID()));
		    if (currentClient.threadSand.getRun()) {
			NGlobals.sPrint("Sending DELETE_SPRITE ---> OPERA_MAIN: " + THREAD_ID);
			currentClient.threadSand.sendGrainL(NAppID.SERVER, NCommand.DELETE_SPRITE, NDataType.INT32, 1, ix);
		    }
		    else {
			NGlobals.sPrint("BOGUS (3) getRun() from -> sending DELETE_SPRITE to OPERA_MAIN: " + THREAD_ID);
			removeByThreadID(currentClient.getThreadID());
		    }
		}
	    }
	}
    }
    	 
    if (pos >=0) {
	// XMERGE:  AUK ONLY
	// rem from main display list 
	toTerminate = getClient(pos);

	if (toTerminate != null) {
	    THREAD_ID = toTerminate.getThreadID();

	    if (toTerminate.getAppID() == NAppID.OPERA_MAIN) {
		dPos = getDispClientThreadNum(THREAD_ID);
		setDispClientThreadNum(THREAD_ID, -1);
		    
		if (dPos < getDispClientCount()-1) {
		    NGlobals.sPrint("Removing thread from MAIN_DISPLAY cache" + THREAD_ID + " at " + dPos);
		    for (int i = dPos+1; i < getDispClientCount(); i++) {            
			tT = getDispClient(i);
			tID = tT.getThreadID();
			setDispClient((i-1),tT);
			setDispClientThreadNum(tID,(i-1));
		    }
		}
		tC = getDispClientCount();
		// setDispClientCount(tC-1);
	    }
	    // ^^^ AUK ^^^
	}
    }

    // } // END matches if (false)


    if (pos >= 0) {
	// XMERGE:  main list -------------------------------------------
	toTerminate = getClient(pos);
   
	// rem from client list
	if (pos < getClientCount()-1) {
	    NGlobals.sPrint("Removing client thread at " + pos);
	    for (int i = pos+1; i < getClientCount(); i++) {
		tT = getClient(i);
		if (tT != null) {
		    setClient((i-1), tT);
		    tID = tT.getThreadID();
		    tAID = tT.getAppID();
		    setClientThrList((i-1),tID);
		    setClientNumAppIDList((i-1),tAID);

		    setClientThreadNum(tID,(i-1));
		}
		else {
		    checkClients();
		}
	    }
	}

	if (toTerminate == null) {
	    NGlobals.dtPrint(" (rbtid) SERVER: BOGUS (31) toTerminate but not PUNTING");
	    checkClients();
	    // return;
	}
	
	if (toTerminate != null) {
	    try {  
		toTerminate.close(); 
		toTerminate.stop(); 
		toTerminate.interrupt();
		toTerminate = null;
	    }
	    catch(IOException ioe) {  
		NGlobals.sPrint("  Error closing thread: " + ioe); 
	    }
	}
	    
    }
	
    checkClients();
    checkDispClients();
}


// --- add thread ------------------------------------------------------------------

private void addThread(Socket socket) {  
    int tID;
    int i,tC,c,tL,tCC;
    NGlobals.sPrint("addThread(" + socket + ")");
    NomadServerThread tThread, tT;

    NomadServerThread tHolder;
    NGlobals.sPrint("addThread(" + socket + ")");

    String IP = new String((socket.getInetAddress()).getHostAddress());

    checkClients();

    tC = getClientCount();
    tL = getClientsLength();

    NGlobals.sPrint("     clientCount = " + tC);
    NGlobals.sPrint("     clients.length = " + tL);

    if (tC < tL+1) {
	//	if (getClientCount() < getClientsLength()) {  
	NGlobals.sPrint("  Client accepted, adding to Global Client List: " + socket + " w/IP " + IP);
	tHolder = new NomadServerThread(this, socket);
	setClient(tC,tHolder);


	try {  
	    tHolder.open();
	    tHolder.setIP(IP);
	    tID = tHolder.getThreadID();
	    setClientThreadNum(tID,tC);
	    tHolder.setPos(tC);
	    tHolder.start();
	    setClientCount(tC+1);
	}
	catch(IOException ioe) {  
	    NGlobals.sPrint("    Error opening thread: " + ioe); 
	} 


    }
    else
	NGlobals.sPrint("  Client refused: maximum " + getClientsLength() + " reached.");
}

public static void main(String args[]) {  
    NomadServer server = null;
    if (args.length != 1)
	NGlobals.sPrint("Usage: java NomadServer port");
    else
	server = new NomadServer(Integer.parseInt(args[0]));
}

}

// FINIS

// --- remove by position -------------------------------------

// public void removeByPos(int pos) {  
// 	int tC,c;
// 	int tID,dPos;
// 	NomadServerThread toTerminate,tT;
// 	NomadServerThread inClient;

// 	NGlobals.dtPrint("getClient: 16 (removeByPos)");
// 	inClient = getClient(pos);
// 	toTerminate = getClient(pos);
// 	//	synchronized(threadLock) {
	    
// 	NGlobals.dtPrint("removing THREAD at pos " + pos);

// 	NGlobals.sPrint("     clientCount = " + getClientCount());
// 	NGlobals.sPrint("     clients.length = " + getClientsLength());

// 	// XMERGE:  need to fix this so that it uses the other method
// 	//          AND see if it's even necessary
// 	//          I guess you have to, if you "removeByBos" you have to do both ...


// 	if ((pos >= 0) && (pos < getClientCount()-1)) {
	
// 	    // remove from lists ---------------------------------------------------
	    
// 	    // XMERGE:  AUK ONLY
// 	    // rem from main display list 
// 	    if (toTerminate.getAppID() == NAppID.OPERA_MAIN) {
// 		dPos = getDispClientThreadNum(THREAD_ID);
// 		if (dPos < getDispClientCount()-1) {
// 		    NGlobals.sPrint("Removing thread from MAIN_DISPLAY cache" + THREAD_ID + " at " + dPos);
// 		    for (int i = dPos+1; i < getDispClientCount(); i++) {            
// 			tT = getDispClient(i);
// 			setDispClient((i-1),tT);
// 			tID = tT.getThreadID();
// 			setDispClientThreadNum(tID,(i-1));
// 		    }
// 		}
// 		tC = getDispClientCount();
// 		setDispClientCount(tC-1);
// 	    }
	
// 	    // remove from main listr list
	
// 	    NGlobals.sPrint("Removing client thread " + THREAD_ID + " at " + pos);
// 	    for (int i = pos+1; i < getClientCount(); i++) {
// 		NGlobals.dtPrint("getClient: 14 (remove)");
// 		tT = getClient(i);
// 		setClient((i-1), tT);
// 		tID = tT.getThreadID();
// 		setClientThreadNum(tID,(i-1));
// 	    }
// 	}
	
// 	tC = getClientCount();
// 	setClientCount(tC-1);



// 	if (inClient.getAppID() == NAppID.OPERA_MAIN) {

// 	    tID = inClient.getThreadID();

// 	    if (dPos >=0) {
// 		if (dPos < mainDisplayClientCount-1) {
// 		    NGlobals.sPrint("Removing thread from MAIN_DISPLAY cache" + tID + " at " + dPos);

// 		    // remove from main display PTR list
// 		    for (int i = dPos+1; i < mainDisplayClientCount; i++) {
// 			mainDisplayClientPosPtr[i-1] = mainDisplayClientPosPtr[i]; // shuffle
// 			NGlobals.dtPrint("getClient: 17 (removeByPos)");
// 			tID = getClient(mainDisplayClientPosPtr[i-1]).getThreadID();
// 			// clientDisplayNumFromThreadID[tID] = (short)(i-1);
// 		    }
// 		    // clientDisplayNumFromThreadID[mainDisplayClientCount] = -1;
// 		}
// 	    }
// 	    mainDisplayClientCount--;
// 	}
    
// 	if (pos >= 0) {  
// 	    // setClientThreadNum(THREAD_ID, -1);
// 	    NGlobals.dtPrint("getClient: 18 (removeByPos)");
// 	    toTerminate = getClient(pos);
	    
// 	    if (toTerminate == null) {
// 		NGlobals.dtPrint("SERVER: BOGUS toTerminate");
// 		return;
// 	    }
	    
// 	    // setClient(pos,null);  // XYZ for garbage collect
	    
// 	    if (pos < getClientCount()-1) {
// 		NGlobals.sPrint("Removing client thread at pos" + pos);
// 		for (int i = pos+1; i < getClientCount(); i++) {
// 		    NGlobals.dtPrint("getClient: 19 (removeByPos)");
// 		    tT = getClient(i);
// 		    setClient((i-1), tT);
// 		    tID = tT.getThreadID();
// 		    setClientThreadNum(tID,(i-1));
// 		}
// 	    }
	    
// 	    tC = getClientCount();
// 	    setClientCount(tC-1);
	    
// 	    try {
// 		toTerminate.close(); 
// 		toTerminate.stop(); 
// 		toTerminate.interrupt();
// 		toTerminate = null;
// 	    }
// 	    catch(IOException ioe) {  
// 		NGlobals.sPrint("  Error closing thread: " + ioe); 
// 	    }
	    
// 	}		// XYZ double check the client count

// 	tC=0;
// 	for (c=0; c<getClientCount(); c++) {
// 	    NGlobals.dtPrint("getClient: 20 (removeByPos)");
// 	    tT = getClient(c);
// 	    if (tT != null) {
// 		if (tT.getRun()) {
// 		    tC++;
// 		}
// 	    }
// 	}
// 	setClientCount(tC);
// }
// //    }    // matches synchronized(threadLock)
