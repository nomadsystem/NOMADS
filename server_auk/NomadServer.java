import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.Object.*;
import nomads.v210_auk.*;

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
	private Calendar cal;
	long nowT,appT,diffT,lagT;

	private static byte _DISCUSS_STATUS = 0;
	private static byte _CLOUD_STATUS = 0;
	private static byte _POINTER_STATUS = 0;
	private static byte _DROPLET_STATUS = 0;
	private static int _DROPLET_VOLUME = 0;
	private static byte _CLOUD_SOUND_STATUS = 0;
	private static int _CLOUD_SOUND_VOLUME = 0;
	private static byte _POINTER_TONE_STATUS = 0;
	private static int _POINTER_TONE_VOLUME = 0;
	private static int _SYNTH_VOLUME = 0;



	int iDay;
	FileOutputStream out; // declare a file output object
	PrintStream p; // declare a print stream object

	NGrain myGrain;

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
		idList[NAppID.OC_CLOUD] = new String("OC_LOGIN");
		idList[NAppID.OC_CLOUD] = new String("OC_POINTER");
		idList[NAppID.OC_CLOUD] = new String("DISCUSS_TOPIC");
		idList[NAppID.OC_CLOUD] = new String("CLOUD_TOPIC");
		idList[NAppID.OC_CLOUD] = new String("MONITOR");
		idList[NAppID.OC_CLOUD] = new String("CENSOR");
		idList[NAppID.OC_CLOUD] = new String("DEBUG");

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

		NGrain inGrain;

		NGlobals.sPrint("-----------------------------------------------------[" + debugLine++ + "]");

		// Do the following for EACH client

		// 1 ---- READ ============================================================================================

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
		myGrain.print();

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
			}
		}

		// 2: INIT =================================================================================================================

		// TODO:  what other apps need init?  EG., OperaMain display too? - to set last state in case of a crash
		IP = currentClient.getIP();   	// Grab IP (more for data logging)

		// INIT for CONDUCTOR_PANEL - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - WI

		currentClient = clients[tCNum];
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

			d[0] = _DROPLET_STATUS;
			currentClient.threadSand.sendGrain(NAppID.SERVER, NCommand.SET_DROPLET_STATUS, NDataType.UINT8, 1, d);
			NGlobals.lPrint("_DROPLET_STATUS:  " + d[0]);

			int ix[] = new int[1];
			ix[0] = _DROPLET_VOLUME;
			currentClient.threadSand.sendGrain(NAppID.SERVER, NCommand.SET_DROPLET_VOLUME, NDataType.INT32, 1, ix);
			NGlobals.lPrint("_DROPLET_VOLUME:  " + ix[0]);

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

			currentClient.setButtonInitStatus((byte)1);
			// tempString = new String("CNT:" + clientCount);
			// clients[cNum].send((byte)NAppID.MONITOR, tempString);
			// NGlobals.lPrint("  Sending " + tempString + " to MONITOR client [" + cNum + "] from SERVER");

		}

		// INIT for OPERA_CLIENT - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - WI
		currentClient = clients[tCNum];
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

			d[0] = _DROPLET_STATUS;
			currentClient.threadSand.sendGrain(NAppID.CONDUCTOR_PANEL, NCommand.SET_DROPLET_STATUS, NDataType.UINT8, 1, d);
			NGlobals.lPrint("_DROPLET_STATUS:  " + d[0]);

			int ix[] = new int[1];
			ix[0] = _DROPLET_VOLUME;
			currentClient.threadSand.sendGrain(NAppID.CONDUCTOR_PANEL, NCommand.SET_DROPLET_VOLUME, NDataType.INT32, 1, ix);
			NGlobals.lPrint("_DROPLET_VOLUME:  " + ix[0]);

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

			currentClient.setButtonInitStatus((byte)1);
		}

		// INIT for OPERA_MAIN - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - WI
		currentClient = clients[tCNum];
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
		//    we cycle through all (or a subset of) clients and send data out
		//
		// ====================================================================================================W

		// 3 ---- WRITE ===========================================================================================================

		NGlobals.sPrint("===== WRITING =====");

		// FILTER out some commands that don't need to go past this point

		if (incAppCmd != NCommand.REGISTER) {

			// TODO:  add code to cache the button statuses
			//        IDEA:  use button_status[array]

			// incoming appID = CONDUCTOR_PANEL = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 

			if (incAppID == NAppID.CONDUCTOR_PANEL) {
				// scroll through all clients // TODO: FIX: SPEEDUP: change to separate APPID[client] arrays

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


				for (int c = 0; c < clientCount; c++) {
					// Get the client off the master list
					currentClient = clients[c];


					if (currentClient.getAppID() == NAppID.CONDUCTOR_PANEL) {
						if (incAppCmd == NCommand.SEND_PROMPT_ON) {
							NGlobals.sPrint("   sending to ===> client[" + c + "] w/ appID = " + printID((byte)currentClient.getAppID()));
							myGrain.print();
							// Write the data out
							currentClient.threadSand.sendGrain(myGrain);
						}
						else if (incAppCmd == NCommand.SEND_PROMPT_OFF) {
							NGlobals.sPrint("   sending to ===> client[" + c + "] w/ appID = " + printID((byte)currentClient.getAppID()));
							myGrain.print();
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
							myGrain.print();
							// Write the data out
							currentClient.threadSand.sendGrain(myGrain);
						}
					}

					// send data to ===> OPERA CLIENT - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
					if (currentClient.getAppID() == NAppID.OPERA_MAIN) {
						if ((incAppCmd != NCommand.SET_DROPLET_VOLUME) && 
								(incAppCmd != NCommand.SET_DROPLET_STATUS)) {
							NGlobals.sPrint("   sending to ===> client[" + c + "] w/ appID = " + printID((byte)currentClient.getAppID()));
							myGrain.print();
							// Write the data out
							currentClient.threadSand.sendGrain(myGrain);
						}
					}

				}
			}   

			// incoming appID = OC_DISCUSS = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 

			if (incAppID == NAppID.OC_DISCUSS) {

			    if (incAppCmd == NCommand.SEND_MESSAGE && (_DISCUSS_STATUS == 1)) {
					// scroll through all clients // TODO: FIX: SPEEDUP: change to separate APPID[client] arrays
					for (int c = 0; c < clientCount; c++) {
						// Get the client off the master list
						currentClient = clients[c];

						// send data to ===> OPERA CLIENT - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
						if (currentClient.getAppID() == NAppID.OPERA_CLIENT) {
							NGlobals.sPrint("   sending to ===> client[" + c + "] w/ appID = " + printID((byte)currentClient.getAppID()));
							myGrain.print();
							// Write the data out
							currentClient.threadSand.sendGrain(myGrain);
						}

						// send data to ===> OPERA MAIN - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
						else if (currentClient.getAppID() == NAppID.OPERA_MAIN) {
							NGlobals.sPrint("   sending to ===> client[" + c + "] w/ appID = " + printID((byte)currentClient.getAppID()));
							myGrain.print();
							// Write the data out
							currentClient.threadSand.sendGrain(myGrain);
						}

					}
				}   
			}

			// incoming appID = OC_CLOUD = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 

			if (incAppID == NAppID.OC_CLOUD && (_CLOUD_STATUS == 1)) {

				if (incAppCmd == NCommand.SEND_MESSAGE) {
					// scroll through all clients // TODO: FIX: SPEEDUP: change to separate APPID[client] arrays
					for (int c = 0; c < clientCount; c++) {
						// Get the client off the master list
						currentClient = clients[c];

						// send data to ===> OPERA MAIN - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
						if (currentClient.getAppID() == NAppID.OPERA_MAIN) {
							NGlobals.sPrint("   sending to ===> client[" + c + "] w/ appID = " + printID((byte)currentClient.getAppID()));
							myGrain.print();
							// Write the data out
							currentClient.threadSand.sendGrain(myGrain);
						}

					}
				}   
			}


			// incoming appID = OC_POINTER = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 

			if (incAppID == NAppID.OC_POINTER && (_POINTER_STATUS == 1)) {
				if (incAppCmd == NCommand.SEND_SPRITE_XY) {

					for (int c = 0; c < clientCount; c++) {

						currentClient = clients[c];
						NGlobals.sPrint("===> client[" + c + "] w/ id = " + currentClient.getAppID());


						// send out to SOUND_SWARM_DISPLAY - - - - - - - - - - - - - - - - - - - - - - - - -
						if (currentClient.getAppID() == NAppID.OPERA_MAIN) {
							NGlobals.sPrint("Sending SOUND_SWARM:THREAD_ID to ---> SOUND_SWARM_DISPLAY: " + THREAD_ID);

							// CUSTOM DATA PACKING into 3 ints: THREAD_ID, x, y
							int[] x = new int[3];
							x[0] = THREAD_ID;
							x[1] = myGrain.iArray[0];
							x[2] = myGrain.iArray[1];

							currentClient.threadSand.sendGrain(incAppID, NCommand.SEND_SPRITE_THREAD_XY, NDataType.INT32, 3, x);

						}
					}
				}
			}

			else if (false) {
				NGlobals.sPrint("===> sending PASSTHROUGH network data");
				for (int c = 0; c < clientCount; c++) {

					// Get the client off the master list
					currentClient = clients[c];
					NGlobals.sPrint("===> client[" + c + "] w/ appID = " + currentClient.getAppID());

					// Extra step for SOUND_SWARM_DISPLAY: need to send THREAD_ID


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

