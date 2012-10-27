import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.Object.*;
import nomads.v210_auk.*;

public class NomadServer implements Runnable {  

    int MAX_THREADS = 5000;
    int MAX_DISP_THR = 500;
    int MAX_THREAD_IDS = 100000;
    int MAX_IPS = 2000;

    private NomadServerThread clientList[] = new NomadServerThread[MAX_THREADS];
    private NomadServerThread mainDisplayClientList[] = new NomadServerThread[MAX_DISP_THR];

    private short clientNumFromThreadID[] = new short[MAX_THREAD_IDS];
    private short clientDisplayNumFromThreadID[] = new short[MAX_THREAD_IDS];

    private String IPsLoggedIn[] = new String[MAX_IPS];
    private String users[] = new String[MAX_IPS];

    private ServerSocket server = null;
    private Thread       thread = null;
    private int clientCount = 0;
    private int mainDisplayClientCount = 0;
    private int IPCount = 0;

    private int userCount = 0;
    private int eventNum = 0;
    private static int debugLine = 0;
    private static String[] children;
    private Calendar cal;
    long nowT,appT,diffT,lagT;

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

    int iDay;
    int skipper = 0;
    int aSkipper = 0;
    int pToggle = 0;
    FileOutputStream out; // declare a file output object
    PrintStream p; // declare a print stream object

    NGrain myGrain;

    Boolean handleActive = false;
    long handleStart=0;
    long handleEnd = 1;
    long millis=0;  // not used yet
    private Boolean runState=true; // nuy
    Calendar now;
    int errFlag = 0;
    int resetCtr = 0;
    int maxResets = 5;

    long hDiff = 0;
    float hAvg = 500;

    NomadServerThread currentClientHolder;
    private int spriteSkipper=0;
    private int spriteTicker=0;

    private int maxClients=10;

    public synchronized int getSpriteTicker() {
	return spriteTicker;
    }

    public synchronized void setSpriteTicker(int t) {
	spriteTicker = t;
    }

    public synchronized int getSpriteSkipper() {
	return spriteSkipper;
    }

    public synchronized void setSpriteSkipper(int s) {
	spriteSkipper = s;
    }

    public synchronized int getMaxClients() {
	return maxClients;
    }

    public synchronized void setMaxClients(int m) {
	maxClients = m;
    }

    public synchronized int getClientCount() {
	return clientCount;
    }

    public synchronized void setClientCount(int c) {
	clientCount = c;
    }

    private class NomadsErrCheckThread extends Thread {
	NomadServer server; //Replace with current class name

	public NomadsErrCheckThread(NomadServer _server) {
	    server = _server;
	}
	public void run()    {			
	    NGlobals.lPrint("NomadsErrCheckThread -> run()");
	    while (true)  {
		server.errCheck();
	    }
	}
    }

    public synchronized long getHandleStart() {
	return handleStart;
    }
    
    public synchronized long getHandleEnd() {
	return handleEnd;
    }
    
    public synchronized void setHandleStart(long hs) {
	handleStart = hs;
    }
    
    public synchronized void setHandleEnd(long he) {
	handleEnd = he;
    }


    public synchronized Boolean getHandleActive() {
	return handleActive;
    }

    public synchronized void setHandleActive(Boolean ha) {
	handleActive = ha;
    }

    public synchronized void setRunState(Boolean state) {
	runState = state;
    }
    
    public synchronized Boolean getRunState() {
	return runState;
    }

    // private NomadsErrCheckThread nECThread;

    int errChecks = 0;

    public void errCheck() {
	Calendar now;
	long mSecN=0;
	long mSecH=0;
	long mSecE=0;
	long mSecDiff=0;

	String eString = new String("SERVER ERROR CHECK THREAD " + errChecks);
	errChecks++;

	NGlobals.dtPrint(eString);
	 try {

	     if (getHandleActive() == true) {


		 now = Calendar.getInstance();
		 //		 mSecN = now.getTimeInMillis();
		 mSecN = System.nanoTime()/1000;

		 mSecH = getHandleStart();
		 mSecE = getHandleEnd();
		 
	 	mSecDiff = mSecN-mSecH;

	 	System.out.println(">>> handleErrCheck time diff: " + mSecDiff);

	 	if ((mSecDiff > 1000) && (mSecE > mSecH)) {
	 	    errFlag += 1;
	 	    if (errFlag > 0) {
	 		System.out.println(">>> INCR ERROR COUNT: " + errFlag);
	 	    }
	 	    if (errFlag > 3)  {
			resetCtr++;
			setRunState(false);
			NGlobals.dtPrint(">>>>>>>>>>>>> ERRFLAG triggered for client");
			//NGlobals.dtPrint("    IP" + currentClientHolder.getIP());
			NGlobals.dtPrint("    THREAD" + currentClientHolder.getThreadID());
			
			currentClientHolder.threadSand.disconnect();
			NomadsErrCheckThread.sleep(2000);
			currentClientHolder.threadSand.connect();
			errFlag = 0;
		    }
		    else if (errFlag > 0) {
			errFlag--;
			System.out.println(">>> DECR ERROR COUNT: " + errFlag);
		    }
		}
	     }
	     
	     if (resetCtr > maxResets) {
		 System.out.println("######### CRITICAL ERROR");
		 System.out.println(">>> #### MAX RESETS");
		 System.out.println(">>> sleeping 10 sec");
		 // Get clent number of inc client
		 int tNum = currentClientHolder.getThreadID();
		 int tCNum = clientNumFromThreadID[tNum];
		 remove(tCNum);
		 NomadsErrCheckThread.sleep(1000);
		 resetCtr=0;

	     }
	     // THIS SETS THE ERROR CHECKING RATE
	     NomadsErrCheckThread.sleep(500);

	 }
	 catch (InterruptedException ie) {}
    }





    private String printID (byte id) {
	String[] idList = new String[255];
	int i;
	for(i=0;i<255;i++) {
	    idList[i] = null;
	}

	// Populate the list

	idList[NAppID.SERVER] = new String("SERVER");
	idList[NAppID.CONDUCTOR_PANEL] = new String("CONDUCTOR_PANEL");
	idList[NAppID.OPERA_MAIN] = new String("OPERA_MAIN");
	idList[NAppID.OPERA_CLIENT] = new String("OPERA_CLIENT");
	idList[NAppID.OC_DISCUSS] = new String("OC_DISCUSS");
	idList[NAppID.OC_CLOUD] = new String("OC_CLOUD");
	idList[NAppID.OC_LOGIN] = new String("OC_LOGIN");
	idList[NAppID.OC_POINTER] = new String("OC_POINTER");
	idList[NAppID.JOC_POINTER] = new String("JOC_POINTER");

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


    public NomadServer(int port) {  	    
	for (int i=0;i<MAX_THREAD_IDS;i++) {
	    clientNumFromThreadID[i] = -1;
	    clientDisplayNumFromThreadID[i] = -1;
	}
	for (int i=0;i<MAX_DISP_THR;i++) {
	    mainDisplayClientList[i] = null;
	}
	for (int i=0;i<MAX_THREADS;i++) {
	    clientList[i] = null;
	}

	for (int i=0;i<MAX_IPS;i++) {
	    IPsLoggedIn[i] = null;
	    users[i] = null;
	}
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
	int tMDNum;

	byte incAppCmd, incAppDataType;
	int incAppDataLen;
	byte incAppID;

	int[] x1 = new int[1];
	int[] x2 = new int[2];
	int[] x3 = new int[3];

	NomadServerThread currentClient;
	NGrain inGrain;
	int mySpriteSkipper=0;

	now = Calendar.getInstance();
	// setHandleStart(now.getTimeInMillis());
	setHandleStart(System.nanoTime()/1000);
	setHandleActive(true);

	// Do the following for EACH client

	// 1 ---- READ ============================================================================================

	// Read in relevant SAND header info
	incAppID = myGrain.appID;
	incAppCmd = myGrain.command;
	incAppDataType = myGrain.dataType;
	incAppDataLen = myGrain.dataLen;

	// DT Printout

	pToggle++;
	if (pToggle%2000 == 0) {
	    NGlobals.dtPrint("==>  max (" + getMaxClients() + ") count (" + getClientCount() + ") hDiff (" + hDiff + ")" + " hAvg (" + hAvg + ")  <==");
	}
	if (pToggle > 9999) {
	    pToggle = 0;
	}		

	// Skip data because we're taking too long (crash prevention) based on an individual hDiff
	//   specifically, we're skipping OC_DISCUSS data

	if ((incAppID == NAppID.OC_DISCUSS) && 
	    (incAppCmd != NCommand.REGISTER) && 
	    (skipper > 0)) {

	    // NGlobals.dtPrint("HANDLE():  OC_DISCUSS skipping");
	    skipper--;

	    if (skipper == 0) {
		NGlobals.dtPrint(" ");
		NGlobals.dtPrint("    HANDLE():  done skipping (OC_DISCUSS)");
		NGlobals.dtPrint(" ");
		setRunState(true);
	    }
	    else {
		// send to the incoming client (so people can see their text)
		tCNum = clientNumFromThreadID[THREAD_ID];
		currentClient = clientList[tCNum];
		currentClient.threadSand.sendGrain(myGrain);

		for (int c = 0; c < mainDisplayClientCount; c++) {
		    // Get the client off the master list
		    NGlobals.sPrint("   sending to ===> display client[" + c + "]");
		    currentClient = mainDisplayClientList[c];
		    currentClientHolder = currentClient;
		    NGlobals.sPrint("   w/ appID = " + printID((byte)currentClient.getAppID()));
		    currentClient.threadSand.sendGrain(myGrain);
		}
		return;
	    }
	}
	    
	// Skip data because we're taking too long (crash prevention) based on average hDiff
	//   skipping everything except the following

	if ((incAppCmd != NCommand.REGISTER) && 
	    (incAppID != NAppID.SERVER) && 
	    (incAppID != NAppID.CONDUCTOR_PANEL) &&
	    (incAppID != NAppID.OC_CLOUD) && 
	    (aSkipper > 0)) {
	    // NGlobals.dtPrint("HANDLE():  AVG skipping");
	    aSkipper--;
	    if (aSkipper == 0) {
		NGlobals.dtPrint(" ");
		NGlobals.dtPrint("    HANDLE():  done skipping ALL (except SERVER, CPANEL and CLOUD)\n");
		setRunState(true);
	    }
	    else {
		return;
	    }
	}
	
	if (getClientCount() < 10) {
	    setMaxClients(getClientCount());
	}

	// TROTTLE UP based on hAvg

	if ((hAvg < 700) && 
	    (hDiff < 700) &&
	    (getMaxClients() < getClientCount())) {
	    NGlobals.dtPrint("   THROTTLE UP:  hAvg/hDiff < 700 msec (" + hAvg + ")(" + hDiff + ") maxClients (" + getMaxClients() + ")  clientCount (" + getClientCount() +")");
	    if (getMaxClients() < getClientCount()) { 
		setMaxClients(getMaxClients()+1);
	    }
	    else if (getMaxClients() > getClientCount()) {
		setMaxClients(getClientCount());
	    }
	}


	if (incAppCmd != NCommand.SEND_SPRITE_XY) {
	    NGlobals.sPrint("===== READING =====");
	    NGlobals.sPrint("-----------------------------------------------------[" + debugLine++ + "]");
	    NGlobals.sPrint("appID: " + incAppID);
	    NGlobals.sPrint("mainDisplayClientCount = " + mainDisplayClientCount);
	}




	// Print out at the SERVER level
	if (false) {
	    NGlobals.sPrint("appID: " + incAppID);
	    NGlobals.sPrint("command: " + incAppCmd);
	    NGlobals.sPrint("dataType: " + incAppDataType);
	    NGlobals.sPrint("dataLen: " + incAppDataLen);
	}
	//myGrain.print();

	// Thread admin stuff ---------------------------------------------------------------------

	// Get client number of inc client
	tCNum = clientNumFromThreadID[THREAD_ID];

	if (tCNum < 0) {
	    NGlobals.sPrint("   ERROR:  client thread not found.");
	    // TODO:  send the bye command!!!
	    remove(THREAD_ID);
	    return;
	}

	currentClient = clientList[tCNum];
	currentClientHolder = currentClient;

	// Login and THREAD registration ----------------------------------------------------------

	// 1: check if client thread is registered
	//    if not reg, REGISTER the client's appID with the SERVER client thread
	//
	//    otherwise KICK if not registering as the very first thing
	//

	if (currentClient.getAppID() == -1) {
	    if (incAppCmd != NCommand.REGISTER) {
		NGlobals.sPrint("ERROR:  you must REGISTER your app first before sending data\n");
		remove(THREAD_ID);
		return;
	    }
	    else {
		NGlobals.sPrint("===== REGISTERING (ONE TIME) =====");
		NGlobals.sPrint("  Setting client[" + tCNum + "] incAppID to: " + incAppID);
		currentClient.setAppID(incAppID);
		if (incAppID == NAppID.OPERA_MAIN) {
		    NGlobals.sPrint("appID: " + printID(incAppID));
		    NGlobals.sPrint("appID: " + incAppID);
		    if (mainDisplayClientCount < mainDisplayClientList.length) {  
			NGlobals.sPrint("  ################ Adding client to OPERA_MAIN cache @ " + mainDisplayClientCount);
			mainDisplayClientList[mainDisplayClientCount] = currentClient;
			clientDisplayNumFromThreadID[THREAD_ID] = (short)mainDisplayClientCount;
			mainDisplayClientCount++; 
			NGlobals.sPrint("mainDisplayClientCount = " + mainDisplayClientCount);
		    }
		    else {
			NGlobals.sPrint("  ERROR:  current client > MAX_DISPLAY_THREADS, client NOT added to DISPLAY cache");
		    }
		}
	    }
	}
	
	NGlobals.sPrint("----------- PRE-INIT ---------------");
	NGlobals.sPrint("appID: " + printID(incAppID));
	NGlobals.sPrint("appID: " + incAppID);

	// 2: INIT =================================================================================================================
	
	// TODO:  what other apps need init?  EG., OperaMain display too? - to set last state in case of a crash
	//IP = currentClient.getIP();   	// Grab IP (more for data logging)


	// INIT for CONDUCTOR_PANEL - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - WI

	currentClient = clientList[tCNum];
	currentClientHolder = currentClient;

	if ((currentClient.getAppID() == NAppID.CONDUCTOR_PANEL) && (currentClient.getButtonInitStatus() == 0)) {

	    NGlobals.lPrint("  Sending button states to CONDUCTOR PANEL from SERVER.");
	    byte d[] = new byte[1];

	    d[0] = _DISCUSS_STATUS;
	    currentClient.threadSand.sendGrain(NAppID.SERVER, NCommand.SET_DISCUSS_STATUS, NDataType.UINT8, 1, d);
	    NGlobals.lPrint("_DISCUSS_STATUS: " + d[0]);

	    d[0] = _CLOUD_STATUS;
	    currentClient.threadSand.sendGrain(NAppID.SERVER, NCommand.SET_CLOUD_STATUS, NDataType.UINT8, 1, d);
	    NGlobals.lPrint("_CLOUD_STATUS:  " + d[0]);

	    d[0] = _POINTER_STATUS;
	    currentClient.threadSand.sendGrain(NAppID.SERVER, NCommand.SET_POINTER_STATUS, NDataType.UINT8, 1, d);
	    NGlobals.lPrint("_POINTER_STATUS:  " + d[0]);

	    int ix[] = new int[1];
	    ix[0] = _DROPLET_VOLUME;
	    currentClient.threadSand.sendGrain(NAppID.SERVER, NCommand.SET_DROPLET_VOLUME, NDataType.INT32, 1, ix);
	    NGlobals.lPrint("_DROPLET_VOLUME:  " + ix[0]);

	    d[0] = _DROPLET_STATUS;
	    currentClient.threadSand.sendGrain(NAppID.SERVER, NCommand.SET_DROPLET_STATUS, NDataType.UINT8, 1, d);
	    NGlobals.lPrint("_DROPLET_STATUS:  " + d[0]);

	    d[0] = _CLOUD_SOUND_STATUS;
	    currentClient.threadSand.sendGrain(NAppID.SERVER, NCommand.SET_CLOUD_SOUND_STATUS, NDataType.UINT8, 1, d);
	    NGlobals.lPrint("_CLOUD_SOUND_STATUS:  " + d[0]);

	    ix[0] = _CLOUD_SOUND_VOLUME;
	    currentClient.threadSand.sendGrain(NAppID.SERVER, NCommand.SET_CLOUD_SOUND_VOLUME, NDataType.INT32, 1, ix);
	    NGlobals.lPrint("_CLOUD_SOUND_VOLUME:  " + ix[0]);
			
	    d[0] = _POINTER_TONE_STATUS;
	    currentClient.threadSand.sendGrain(NAppID.SERVER, NCommand.SET_POINTER_TONE_STATUS, NDataType.UINT8, 1, d);
	    NGlobals.lPrint("_POINTER_TONE_STATUS:  " + d[0]);

	    ix[0] = _POINTER_TONE_VOLUME;
	    currentClient.threadSand.sendGrain(NAppID.SERVER, NCommand.SET_POINTER_TONE_VOLUME, NDataType.INT32, 1, ix);
	    NGlobals.lPrint("_POINTER_TONE_VOLUME:  " + ix[0]);

	    ix[0] = _SYNTH_VOLUME;
	    currentClient.threadSand.sendGrain(NAppID.SERVER, NCommand.SET_SYNTH_VOLUME, NDataType.INT32, 1, ix);
	    NGlobals.lPrint("_SYNTH_VOLUME:  " + ix[0]);
			
	    String tString = _SEND_PROMPT_ON;
	    byte[] tStringAsBytes = tString.getBytes();
	    int tLen = _SEND_PROMPT_ON.length();
	    currentClient.threadSand.sendGrain(NAppID.SERVER, (byte)NCommand.SEND_PROMPT_ON, (byte)NDataType.CHAR, tLen, tStringAsBytes);
	    NGlobals.lPrint("ACP: Prompt " + tString + " sent"); 

			
	    currentClient.setButtonInitStatus((byte)1);
	    // tempString = new String("CNT:" + maxClients);
	    // clientList[cNum].send((byte)NAppID.MONITOR, tempString);
	    // NGlobals.lPrint("  Sending " + tempString + " to MONITOR client [" + cNum + "] from SERVER");

			
			
	}

	// INIT for OPERA_CLIENT - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - WI
	currentClient = clientList[tCNum];
	currentClientHolder = currentClient;

	NGlobals.sPrint("----------- OC-INIT ---------------");
	NGlobals.sPrint("appID: " + printID(incAppID));
	NGlobals.sPrint("appID: " + incAppID);

	if ((incAppID == NAppID.OPERA_CLIENT) && (currentClient.getButtonInitStatus() == 0)) {
	    

	    NGlobals.lPrint("  Sending button states to OPERA CLIENT from SERVER / CONDUCTOR_PANEL.");
	    byte d[] = new byte[1];

	    d[0] = _DISCUSS_STATUS;
	    currentClient.threadSand.sendGrain(NAppID.CONDUCTOR_PANEL, NCommand.SET_DISCUSS_STATUS, NDataType.UINT8, 1, d);
	    NGlobals.lPrint("_DISCUSS_STATUS: " + d[0]);

	    d[0] = _CLOUD_STATUS;
	    currentClient.threadSand.sendGrain(NAppID.CONDUCTOR_PANEL, NCommand.SET_CLOUD_STATUS, NDataType.UINT8, 1, d);
	    NGlobals.lPrint("_CLOUD_STATUS:  " + d[0]);

	    d[0] = _POINTER_STATUS;
	    currentClient.threadSand.sendGrain(NAppID.CONDUCTOR_PANEL, NCommand.SET_POINTER_STATUS, NDataType.UINT8, 1, d);
	    NGlobals.lPrint("_POINTER_STATUS:  " + d[0]);

	    int ix[] = new int[1];
	    ix[0] = _DROPLET_VOLUME;
	    currentClient.threadSand.sendGrain(NAppID.CONDUCTOR_PANEL, NCommand.SET_DROPLET_VOLUME, NDataType.INT32, 1, ix);
	    NGlobals.lPrint("_DROPLET_VOLUME:  " + ix[0]);

	    d[0] = _DROPLET_STATUS;
	    currentClient.threadSand.sendGrain(NAppID.CONDUCTOR_PANEL, NCommand.SET_DROPLET_STATUS, NDataType.UINT8, 1, d);
	    NGlobals.lPrint("_DROPLET_STATUS:  " + d[0]);

	    d[0] = _CLOUD_SOUND_STATUS;
	    currentClient.threadSand.sendGrain(NAppID.CONDUCTOR_PANEL, NCommand.SET_CLOUD_SOUND_STATUS, NDataType.UINT8, 1, d);
	    NGlobals.lPrint("_CLOUD_SOUND_STATUS:  " + d[0]);

	    ix[0] = _CLOUD_SOUND_VOLUME;
	    currentClient.threadSand.sendGrain(NAppID.CONDUCTOR_PANEL, NCommand.SET_CLOUD_SOUND_VOLUME, NDataType.INT32, 1, ix);
	    NGlobals.lPrint("_CLOUD_SOUND_VOLUME:  " + ix[0]);
			
	    d[0] = _POINTER_TONE_STATUS;
	    currentClient.threadSand.sendGrain(NAppID.CONDUCTOR_PANEL, NCommand.SET_POINTER_TONE_STATUS, NDataType.UINT8, 1, d);
	    NGlobals.lPrint("_POINTER_TONE_STATUS:  " + d[0]);

	    ix[0] = _POINTER_TONE_VOLUME;
	    currentClient.threadSand.sendGrain(NAppID.CONDUCTOR_PANEL, NCommand.SET_POINTER_TONE_VOLUME, NDataType.INT32, 1, ix);
	    NGlobals.lPrint("_POINTER_TONE_VOLUME:  " + ix[0]);
			
	    String tString = _SEND_PROMPT_ON;
	    byte[] tStringAsBytes = tString.getBytes();
	    int tLen = _SEND_PROMPT_ON.length();
	    currentClient.threadSand.sendGrain(NAppID.SERVER, (byte)NCommand.SEND_PROMPT_ON, (byte)NDataType.CHAR, tLen, tStringAsBytes);
	    NGlobals.lPrint("ACP: Prompt " + tString + " sent"); 

	    //Cached Discuss String
	    for (int i=0;i<discussStringCached.size();i++) {
		tString = discussStringCached.get(i);
		tStringAsBytes = tString.getBytes();
		tLen = tString.length();
		currentClient.threadSand.sendGrain(NAppID.SERVER, (byte)NCommand.SEND_CACHED_DISCUSS_STRING, (byte)NDataType.CHAR, tLen, tStringAsBytes);
		NGlobals.lPrint("ACP: Cached Discuss String " + tString + " arrayElt# " + i + " sent");
	    }
			
	    currentClient.setButtonInitStatus((byte)1);
	}

	// INIT for OPERA_MAIN - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - WI

	// HACKISH - sends message back to server, mimicking a send from the CONDUCTOR_PANEL
	// effectively getting the message back from the server as if the CP had sent it

	// TOP

	// FIX:  just have the cached synth volume sent when an OM connects (along with the other attributes like alpha, etc.. perhaps even cached text and cloud)

	currentClient = clientList[tCNum];
	currentClientHolder = currentClient;

	NGlobals.sPrint("----------- PRE-HACKISH OPERA_MAIN VOLUME SET ---------------");
	NGlobals.sPrint("appID: " + printID(incAppID));
	NGlobals.sPrint("appID: " + incAppID);
	
	if (incAppID == NAppID.OPERA_MAIN) {
	    NGlobals.lPrint("  Sending button states to OPERA MAIN from SERVER / CONDUCTOR_PANEL.");
	    int ix[] = new int[1];
	    ix[0] = _SYNTH_VOLUME;
	    currentClient.threadSand.sendGrain(NAppID.CONDUCTOR_PANEL, NCommand.SET_SYNTH_VOLUME, NDataType.INT32, 1, ix);
	    NGlobals.lPrint("_SYNTH_VOLUME:  " + ix[0]);
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

	    // incoming appID = CONDUCTOR_PANEL = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 

	    NGlobals.sPrint("----------- APPCMD != REGISTER (top level of WRITE section) ---------------");
	    NGlobals.sPrint("appID: " + printID(incAppID));
	    NGlobals.sPrint("appID: " + incAppID);

	    if (incAppID == NAppID.CONDUCTOR_PANEL) {
		// scroll through all clientList // TODO: FIX: SPEEDUP: change to separate APPID[client] arrays

		NGlobals.sPrint("----------- appID = CONDUCTOR PANEL ---------------");
		NGlobals.sPrint("appID: " + printID(incAppID));
		NGlobals.sPrint("appID: " + incAppID);

		// Store various FEATURE STATES - - - - - - - - - - - - - - - - -

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

		for (int c = 0; c < getMaxClients(); c++) {
		    // Get the client off the master list
		    currentClient = clientList[c];
		    currentClientHolder = currentClient;

		    if (currentClient.getAppID() == NAppID.CONDUCTOR_PANEL) {
			if (incAppCmd == NCommand.SEND_PROMPT_ON) {
			    NGlobals.sPrint("   sending to ===> client[" + c + "] w/ appID = " + printID((byte)currentClient.getAppID()));
			    //myGrain.print();
			    // Write the data out
			    currentClient.threadSand.sendGrain(myGrain);
			}
			else if (incAppCmd == NCommand.SEND_PROMPT_OFF) {
			    NGlobals.sPrint("   sending to ===> client[" + c + "] w/ appID = " + printID((byte)currentClient.getAppID()));
			    //myGrain.print();
			    // Write the data out
			    currentClient.threadSand.sendGrain(myGrain);
			}
		    }


		    // send data to ===> OPERA CLIENT - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		    if (currentClient.getAppID() == NAppID.OPERA_CLIENT) {
			if ((incAppCmd != NCommand.SET_CLOUD_ALPHA) && 
			    (incAppCmd != NCommand.SET_DISCUSS_ALPHA) &&
			    (incAppCmd != NCommand.SET_POINTER_ALPHA)) {

			    NGlobals.sPrint("   sending to ===> client[" + c + "] w/ appID = " + printID((byte)currentClient.getAppID()));
			    //myGrain.print();
			    // Write the data out
			    currentClient.threadSand.sendGrain(myGrain);
			}
		    }

		    // send data to ===> OPERA CLIENT - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		    if (currentClient.getAppID() == NAppID.OPERA_MAIN) {
			if ((incAppCmd != NCommand.SET_DROPLET_VOLUME) && 
			    (incAppCmd != NCommand.SET_DROPLET_STATUS)) {
			    NGlobals.sPrint("   sending to ===> client[" + c + "] w/ appID = " + printID((byte)currentClient.getAppID()));
			    //myGrain.print();
			    // Write the data out
			    currentClient.threadSand.sendGrain(myGrain);
			}
		    }

		}
	    }   

	    // incoming appID = OC_DISCUSS = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 

	    else if (incAppID == NAppID.OC_DISCUSS) {

		NGlobals.sPrint("----------- appID = OC_DISCUSS ---------------");
		NGlobals.sPrint("appID: " + printID(incAppID));
		NGlobals.sPrint("appID: " + incAppID);


		if (incAppCmd == NCommand.SEND_MESSAGE && (_DISCUSS_STATUS == 1)) {
			    	
		    //Sets cached discuss strings from input
		    discussStringCached.add(new String(myGrain.bArray));
		    if (discussStringCached.size() > MAX_CACHED_DISCUSS_STRINGS) {
			discussStringCached.remove(0);
		    }
			    	
		    // scroll through all clientList // TODO: FIX: SPEEDUP: change to separate APPID[client] arrays
		    for (int c = 0; c < getMaxClients(); c++) {
			// Get the client off the master list
			currentClient = clientList[c];
			currentClientHolder = currentClient;

			// send data to ===> OPERA CLIENT - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
			if (currentClient.getAppID() == NAppID.OPERA_CLIENT) {
			    NGlobals.sPrint("   sending to ===> client[" + c + "] w/ appID = " + printID((byte)currentClient.getAppID()));
			    //myGrain.print();
			    // Write the data out
			    currentClient.threadSand.sendGrain(myGrain);
			}

			// send data to ===> OPERA MAIN - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
			else if (currentClient.getAppID() == NAppID.OPERA_MAIN) {
			    NGlobals.sPrint("   sending to ===> client[" + c + "] w/ appID = " + printID((byte)currentClient.getAppID()));
			    //myGrain.print();
			    // Write the data out
			    currentClient.threadSand.sendGrain(myGrain);
			}

		    }
		}   
	    }

	    // incoming appID = OC_CLOUD = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 

	    else if (incAppID == NAppID.OC_CLOUD && (_CLOUD_STATUS == 1)) {

		NGlobals.sPrint(" -------------------- appID == OC_CLOUD ---------------");
		NGlobals.sPrint("appID: " + printID(incAppID));
		NGlobals.sPrint("appID: " + incAppID);

		if (incAppCmd == NCommand.SEND_MESSAGE) {

		    // (see below)
		    // TODO: FIX: SPEEDUP: change to separate APPID[client] arrays
		    // scroll through all clientList 
		    // for (int c = 0; c < getMaxClients; c++) {
		    // 	// Get the client off the master list
		    // 	currentClient = clientList[c];

		    // 	// send data to ===> OPERA MAIN - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		    // 	if (currentClient.getAppID() == NAppID.OPERA_MAIN) {
		    // 	    NGlobals.sPrint("   sending to ===> client[" + c + "] w/ appID = " + printID((byte)currentClient.getAppID()));
		    // 	    //myGrain.print();
		    // 	    // Write the data out
		    // 	    currentClient.threadSand.sendGrain(myGrain);
		    // 	}

		    // }

		    // FIXED: SPEDUP: changed to separate APPID[client] arrays
		    NGlobals.sPrint("mainDisplayClientCount = " + mainDisplayClientCount);
		    for (int c = 0; c < mainDisplayClientCount; c++) {
			// Get the client off the master list
			NGlobals.sPrint("   sending to ===> display client[" + c + "]");
			currentClient = mainDisplayClientList[c];
			currentClientHolder = currentClient;
			NGlobals.sPrint("   w/ appID = " + printID((byte)currentClient.getAppID()));
			currentClient.threadSand.sendGrain(myGrain);
		    }

		}

	    }   
	    
	    // incoming appID = OC_POINTER = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
	    
	    else if ((incAppID == NAppID.OC_POINTER || incAppID == NAppID.JOC_POINTER) && (_POINTER_STATUS == 1)) {
		if (incAppCmd == NCommand.SEND_SPRITE_XY) {

		    if (getSpriteTicker() < getSpriteSkipper()) {
			setSpriteTicker(getSpriteTicker()+1);
		    }
		    else {
			setSpriteTicker(0);
			NGlobals.sPrint(" >------------------> APPID == OC_POINTER !!! <-- the mini holy grail --------------------<");
			NGlobals.sPrint("appID: " + printID(incAppID));
			NGlobals.sPrint("appID: " + incAppID);
			NGlobals.sPrint("mainDisplayClientCount = " + mainDisplayClientCount);
			
			// FIXED: SPEDUP: changed to separate APPID[client] arrays
			for (int c = 0; c < mainDisplayClientCount; c++) {
			    // Get the client off the master list
			    currentClient = mainDisplayClientList[c];
			    currentClientHolder = currentClient;
			    // NGlobals.sPrint("   sending to ===> display client[" + c + "] w/ appID = " + printID((byte)currentClient.getAppID()));
			    // CUSTOM DATA PACKING into 3 ints: THREAD_ID, x, y
			    x3[0] = THREAD_ID;
			    x3[1] = myGrain.iArray[0];
			    x3[2] = myGrain.iArray[1];
			    currentClient.threadSand.sendGrain(incAppID, NCommand.SEND_SPRITE_THREAD_XY, NDataType.INT32, 3, x3);
			}
		    }
		    // OLD METHOD 

		    // for (int c = 0; c < maxClients; c++) {
		    // 	currentClient = clientList[c];
		    // 	// NGlobals.sPrint("===> client[" + c + "] w/ id = " + currentClient.getAppID());
		    // 	// send out to SOUND_SWARM_DISPLAY - - - - - - - - - - - - - - - - - - - - - - - - -
		    // 	if (currentClient.getAppID() == NAppID.OPERA_MAIN) {
		    // 	    NGlobals.sPrint("Sending SOUND_SWARM:THREAD_ID to ---> OPERA_MAIN: " + THREAD_ID);
		    // 	    // CUSTOM DATA PACKING into 3 ints: THREAD_ID, x, y
		    // 	    int[] x = new int[3];
		    // 	    x[0] = THREAD_ID;
		    // 	    x[1] = myGrain.iArray[0];
		    // 	    x[2] = myGrain.iArray[1];
		    // 	    currentClient.threadSand.sendGrain(incAppID, NCommand.SEND_SPRITE_THREAD_XY, NDataType.INT32, 3, x);
		    // 	}
		    // }

		}
	    }

	    else if (false) {
		NGlobals.sPrint("===> sending PASSTHROUGH network data");
		for (int c = 0; c < getMaxClients(); c++) {

		    // Get the client off the master list
		    currentClient = clientList[c];
		    currentClientHolder = currentClient;
		    NGlobals.sPrint("===> client[" + c + "] w/ appID = " + currentClient.getAppID());

		    // Extra step for SOUND_SWARM_DISPLAY: need to send THREAD_ID


		    //myGrain.print();
		    // Write the data out
		    currentClient.threadSand.sendGrain(myGrain);

		}   
		// END --------------------------------------------------------------------
		if (incAppCmd != NCommand.SEND_SPRITE_XY) {
		    NGlobals.sPrint("handle(DONE) " + THREAD_ID + ":" + myGrain.appID);
		}
		// Free up memory
		if (myGrain != null) {
		    myGrain = null;
		}
	    }
	}

	now = Calendar.getInstance();
	// setHandleEnd(now.getTimeInMillis());
	setHandleEnd(System.nanoTime()/1000);
	setHandleActive(false);

	hDiff = getHandleEnd()-getHandleStart();
	hAvg = ((hAvg*99)+hDiff)/100;

	if ((hAvg > 500) && (hAvg <= 1000)) {
	    if (pToggle%100 == 0) {
		NGlobals.dtPrint("      1000 > hAvg > 500 msec (" + hAvg + ")");
	    }
	}

	// THROTTLE DOWN:  based on hAvg, reduce maxClients by 1
	
	else if ((hAvg > 700) && 
		 (debugLine > 100) ){
	    if (getMaxClients() > 10) {
		setMaxClients(getMaxClients()-10);
		NGlobals.dtPrint(" ");
		NGlobals.dtPrint("   THROTTLE DOWN:  hAvg > 700 msec (" + hAvg + ")");
		NGlobals.dtPrint("    incAppID = " + printID(incAppID) + " threadID " + THREAD_ID + " | setting grain (avg) skip: " + aSkipper + "\n");
	    }
	}

	// THROTTLE DOWN:  based on hAvg, skip ALL data (temporarily)

	if ((hAvg > 1200) && (debugLine > 100)){
	    if ((getRunState() == true) && (aSkipper == 0)) {
		aSkipper = (int)(hAvg/100);
		NGlobals.dtPrint(" ");
		NGlobals.dtPrint("   THROTTLE DOWN: hAvg > 1200 msec (" + hAvg + ")");
		NGlobals.dtPrint("    incAppID = " + printID(incAppID) + " threadID " + THREAD_ID + " | setting grain (avg) skip: " + aSkipper + "\n");
		setRunState(false);
	    }
	}
	else {
	    setRunState(true);
	}

	if ((hDiff > 500) && (hDiff <= 1000)) {
	    if (pToggle%100 == 0) {
		NGlobals.dtPrint("      1000 > hDiff > 500 msec (" + hDiff + ")");
	    }
	}

	// THROTTLE DOWN:  based on hDiff, reduce maxClients by 1

	else if ((hDiff > 1200) && (debugLine > 100)){
	    NGlobals.dtPrint(" ");
	    NGlobals.dtPrint("   THROTTLE DOWN:  hDiff > 1200 msec (" + hDiff + ")");
	    if (getMaxClients() > 10) {
		setMaxClients(getMaxClients()-2);

		NGlobals.dtPrint("    incAppID = " + printID(incAppID) + " threadID " + THREAD_ID +  " | setting maxClients to (" + getMaxClients() + ") | total (" + getClientCount() + ")");
	    }

	    // THROTTLE DOWN:  based on hDiff, skip OC_DISCUSS data (temporarily)
	    
	    if ((skipper == 0) && (getRunState() == true)) {
		skipper = (int)(hDiff/100);
		NGlobals.dtPrint("    incAppID = " + printID(incAppID) + " threadID " + THREAD_ID +  " | setting grain skip: " + skipper);
		setRunState(false);
	    }
	    NGlobals.dtPrint(" ");
	}
	else {
	    setRunState(true);
	}
	
	
    }    

    // =====================================================================================================
    // END main data routing code --- handle() fn
    // =====================================================================================================

    public synchronized void remove(int THREAD_ID) {  
	int pos = clientNumFromThreadID[THREAD_ID];
	int dPos = clientDisplayNumFromThreadID[THREAD_ID];
	int tID;
	int ix[] = new int[1];
	NomadServerThread currentClient;
	NomadServerThread inClient;

	inClient = clientList[pos];

	// SEND REMOVE/DELETE TO SWARM DISPLAY CLIENTS ---------------------------------------------------

	for (int c = 0; c < getClientCount(); c++) {
	    
	    currentClient = clientList[c];
	    NGlobals.sPrint("===> client[" + c + "] w/ id = " + currentClient.getAppID());
	    
	    
	    // send out to SOUND_SWARM_DISPLAY - - - - - - - - - - - - - - - - - - - - - - - - -
	    if ((currentClient.getAppID() == NAppID.OPERA_MAIN) && (currentClient.getThreadID() != THREAD_ID)) {
		NGlobals.sPrint("Sending DELETE_SPRITE ---> OPERA_MAIN: " + THREAD_ID);
		
		// CUSTOM DATA PACKING into 3 ints: THREAD_ID, x, y
		ix[0] = THREAD_ID;
		
		currentClient.threadSand.sendGrain(NAppID.SERVER, NCommand.DELETE_SPRITE, NDataType.INT32, 1, ix);
		
	    }
	}

	// remove from lists ---------------------------------------------------


	if (pos >= 0) {  
	    clientNumFromThreadID[THREAD_ID] = -1;
	    NomadServerThread toTerminate = clientList[pos];

	    // rem from main display list
	    if (inClient.getAppID() == NAppID.OPERA_MAIN) {
		if (dPos >=0) {
		    
		    if (dPos < mainDisplayClientCount-1) {
			NGlobals.sPrint("Removing thread from MAIN_DISPLAY cache" + THREAD_ID + " at " + pos);
			for (int i = dPos+1; i < mainDisplayClientCount; i++) {
			    mainDisplayClientList[i-1] = mainDisplayClientList[i];
			    tID = mainDisplayClientList[i-1].getThreadID();
			    clientDisplayNumFromThreadID[tID] = (short)(i-1);
			}
		    }
		}
		mainDisplayClientCount--;
	    }


	    
	    // rem from client list
	    if (pos < getClientCount()-1) {
		NGlobals.sPrint("Removing client thread " + THREAD_ID + " at " + pos);
		for (int i = pos+1; i < getClientCount(); i++) {
		    clientList[i-1] = clientList[i];
		    tID = clientList[i-1].getThreadID();
		    clientNumFromThreadID[tID] = (short)(i-1);
		}
	    }
	    setClientCount(getClientCount()-1);
	    if (getMaxClients() > getClientCount()) {
		setMaxClients(getClientCount());
	    }

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
	NomadServerThread tHolder;
	NGlobals.sPrint("addThread(" + socket + ")");

	String IP = new String((socket.getInetAddress()).getHostAddress());

	NGlobals.sPrint("     clientCount = " + getClientCount());
	NGlobals.sPrint("     clientList.length = " + clientList.length);

	if (getClientCount() < clientList.length) {  
	    NGlobals.sPrint("  Client accepted, adding to Global Client List: " + socket + " w/IP " + IP);
	    tHolder = new NomadServerThread(this, socket);
	    clientList[getClientCount()] = tHolder;
	    try {  
		clientList[getClientCount()].open(); 
		clientList[getClientCount()].setIP(IP);
		tID = clientList[getClientCount()].getThreadID();
		clientNumFromThreadID[tID] = (short)getClientCount();
		clientList[getClientCount()].start();  
		NGlobals.sPrint("  Client added to lookup array at slot # " + getClientCount());
		setClientCount(getClientCount()+1);
		setSpriteSkipper((int)(getMaxClients()/50));
	    }
	    catch(IOException ioe) {  
		NGlobals.sPrint("    Error opening thread: " + ioe); 
	    } 


	}
	else
	    NGlobals.sPrint("  Client refused: maximum " + clientList.length + " reached.");
    }

    public static void main(String args[]) {  
	NomadServer server = null;
	if (args.length != 1)
	    NGlobals.sPrint("Usage: java NomadServer port");
	else
	    server = new NomadServer(Integer.parseInt(args[0]));
    }

}

