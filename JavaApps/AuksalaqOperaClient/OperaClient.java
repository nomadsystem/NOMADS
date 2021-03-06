//
//  NOMADS Opera Client v.210
//  Integrating NSand class
//

import java.util.Random;
import java.awt.*;
import java.lang.Math;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.*;
import java.io.*;
import netscape.javascript.*;
import java.text.*;
import java.applet.*;
import java.awt.image.BufferedImage;

import nomads.v210_auk.*;

public class OperaClient extends JApplet implements Runnable
{

    private int msgCntr;
    NSand operaSand;
    private NomadsAppThread nThread;

    Thread runner;    

    JLabel label1, label2, label3, label4;
    JPanel panel1, panel2, panel3, panel4;
    //	CloudDiscuss CloudDiscuss; // so this is now like a JPanel. . .
    OC_Cloud myOC_Cloud;
    OC_Discuss myOC_Discuss;
    OC_Pointer myOC_Pointer;
    DiscussDisplayOnly myDiscussDisplayOnly;

    URL imgWebBase, webBase;

    int ocpMaxX, ocpMaxY, ocpCentX, ocpCentY;


    private int maxSkip;

    int pToggle=0;
    private NomadsErrCheckThread nECThread;
    int skipper = 0;
    Boolean handleActive = false;
    Boolean sandRead = false;
    Boolean connected = true;
    int errFlag = 0;
    int errTrip = 10;

    int lastThread = 0;
    float mSecAvg=10;
    float mSecAvgL=10;

    long mSecR=0;
    int resetCtr=0;
    int maxResets=10;


    private class NomadsAppThread extends Thread {
	OperaClient client; //Replace with current class name
	Calendar now;
	long handleStart=0;
	long handleEnd=1;
	long millis=0;
	Boolean runState=false;

	int numSynths=0;
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

	public synchronized void setRunState(Boolean state) {
	    runState = state;
	}

	public synchronized Boolean getRunState() {
	    return runState;
	}


	public NomadsAppThread(OperaClient _client) {
	    client = _client;
	}

	public void run()    {			
	    NGlobals.lPrint("NomadsAppThread -> run()");
	    while (getRunState() == true)  {
		now = Calendar.getInstance();
		setHandleStart(now.getTimeInMillis());
		client.handle();
		client.setHandleActive(false);
		handleEnd = now.getTimeInMillis();
		millis = getHandleEnd()-getHandleStart();
		// NGlobals.dtPrint("handle() proc time:" + millis);
	    }
	}
    }

    private class NomadsErrCheckThread extends Thread {
	OperaClient client; //Replace with current class name

	public NomadsErrCheckThread(OperaClient _client) {
	    client = _client;
	}
	public void run()    {			
	    NGlobals.dtPrint("NomadsErrCheckThread -> run()");
	    while (true)  {
		client.errCheck();
	    }
	}
    }


    public synchronized int getMaxSkip() {
	return maxSkip;
    }

    public synchronized void setMaxSkip(int m) {
	maxSkip = m;
    }

    public synchronized Boolean getSandRead() {
	return sandRead;
    }

    public synchronized void setSandRead(Boolean sr) {
	sandRead = sr;
    }

    public synchronized Boolean getHandleActive() {
	return handleActive;
    }

    public synchronized void setHandleActive(Boolean ha) {
	handleActive = ha;
    }

    public void errCheck() {
	Calendar now;
	long mSecN=0;
	long mSecH=0;
	long mSecDiff=0;

	// NGlobals.dtPrint("errCheck ...");

	try {

	    if ((getHandleActive() == true) && (getSandRead() == true)) {

		now = Calendar.getInstance();
		mSecN = now.getTimeInMillis();
		mSecH = nThread.getHandleStart();

		mSecDiff = mSecN-mSecH;
		mSecAvg = ((mSecAvg*4)+mSecDiff)/5;
		mSecAvgL = ((mSecAvgL*19)+mSecDiff)/20;

		NGlobals.dtPrint("errCheck --> mSecDiff: " + mSecDiff + " avg: " + mSecAvg + " avgL: " + mSecAvgL);

		if (mSecDiff > 2000) {
		    errFlag += 1;
		    if (errFlag > 0) {
			NGlobals.dtPrint(">>> INCR ERROR COUNT: " + errFlag);
		    }
		    if ((errFlag > errTrip) && (connected == true)) {
			now = Calendar.getInstance();
			mSecR = now.getTimeInMillis(); // time of this reset
			NGlobals.dtPrint("-----> EREC #" + resetCtr);
			resetCtr++;
			if (resetCtr > maxResets) {
			    NGlobals.dtPrint("######### CRITICAL ERROR");
			    NGlobals.dtPrint(">>> #### MAX RESETS");
			    NGlobals.dtPrint(">>> sleeping 10 sec");
			    NomadsErrCheckThread.sleep(12000);
			    resetCtr=0;
			}
			nThread.setHandleStart(mSecN);
			NGlobals.dtPrint("######### CRITICAL ERROR");
			NGlobals.dtPrint(">>> handleErrCheck time diff: " + mSecDiff);
			NGlobals.dtPrint(">>> halting thread ...");
			nThread.setRunState(false);
			NomadsErrCheckThread.sleep(2000);
			// deleteSynth(lastThread);
			nThread = null;
			NGlobals.dtPrint(">>> disconnecting ...");
			operaSand.disconnect();
			NomadsErrCheckThread.sleep(1000);
			operaSand = null;
			connected = false;
			NGlobals.dtPrint(">>> disconneced ...");
			// NGlobals.dtPrint(">>> deleting sprites/synths ...");
			// deleteAllSynths();
			// NGlobals.dtPrint(">>> sprites/synths deleted ...");
			NGlobals.dtPrint("+++++ Attempting reconnect ...");
			NomadsErrCheckThread.sleep(1000);
			operaSand = new NSand(); 
			operaSand.connect();
			int d[] = new int[1];
			d[0] = 0;
			operaSand.sendGrain((byte)NAppID.OPERA_MAIN, (byte)NCommand.REGISTER, (byte)NDataType.UINT8, 1, d );
			connected = true;
			NomadsErrCheckThread.sleep(1000);
			NGlobals.dtPrint("+++ reconnected!");			
			NGlobals.dtPrint("+++ attempting to restart thread ...");			
			NomadsErrCheckThread.sleep(1000);
			nThread = new NomadsAppThread(this);
			nThread.setRunState(true);
			nThread.start();
			NGlobals.dtPrint("+++ thread restarted!");			
			errFlag = 0;

			now = Calendar.getInstance();
			mSecN = now.getTimeInMillis();
			nThread.setHandleStart(mSecN);

		    }
		}
		else if (errFlag > 0) {
		    errFlag--;
		    NGlobals.dtPrint(">>> DECR ERROR COUNT: " + errFlag);
		}
	    }
	    NomadsErrCheckThread.sleep(10);
	}
	catch (InterruptedException ie) {}

    }




    public void init( )
    {

	msgCntr = 0;
	String imgPrefix = "http://nomads.music.virginia.edu/images/";

	try { 
	    imgWebBase = new URL(imgPrefix); 
	} 
	catch (Exception e) {}

	Container content = getContentPane();
	content.setBackground(Color.BLACK);
	//	setLayout(new GridLayout( 2, 2, 10, 10 ) );
	setLayout(new BorderLayout( 10, 30) );

	myOC_Discuss = new OC_Discuss();
	//Set up background image for Discuss 
	Image discussImg=getImage(imgWebBase, "IceImageDiscussInputBackground_sm.jpg");
	myOC_Discuss.setImage(discussImg);
	myOC_Discuss.init();
	myOC_Discuss.speak.addActionListener(discussSpeakListener);
	myOC_Discuss.input.addKeyListener(discussReturnListener);
	// myOC_Discuss.setBorder(BorderFactory.createCompoundBorder(
	//                    BorderFactory.createLineBorder(Color.red),
	//                    myOC_Discuss.getBorder()));
	myOC_Discuss.setPreferredSize(new Dimension(450, 75));

	myOC_Cloud = new OC_Cloud();
	Image cloudImg=getImage(imgWebBase, "IceImageCloudInputBackground_sm.jpg");
	myOC_Cloud.setImage(cloudImg);
	myOC_Cloud.init();
	myOC_Cloud.speak.addActionListener(cloudSpeakListener);
	myOC_Cloud.input.addKeyListener(cloudReturnListener);
	// myOC_Cloud.setBorder(BorderFactory.createCompoundBorder(
	//                    BorderFactory.createLineBorder(Color.red),
	//                    myOC_Cloud.getBorder()));
	myOC_Cloud.setPreferredSize(new Dimension(400, 75));

	myDiscussDisplayOnly= new DiscussDisplayOnly();
	Image discussDisplayImg=getImage(imgWebBase, "IceImageDiscussBackground_sm.jpg");
	myDiscussDisplayOnly.setImage(discussDisplayImg);
	myDiscussDisplayOnly.init();
	// myDiscussDisplayOnly.setBorder(BorderFactory.createCompoundBorder(
	//                    BorderFactory.createLineBorder(Color.red),
	//                    myDiscussDisplayOnly.getBorder()));
	myDiscussDisplayOnly.setPreferredSize(new Dimension(250, 500));

	myOC_Pointer = new OC_Pointer();

	ocpMaxX = 675;
	ocpMaxY = 539;
	ocpCentX = ocpMaxX/2;
	ocpCentY = ocpMaxY/2;

	myOC_Pointer.addMouseListener(pointerMouseListener);
	myOC_Pointer.addMouseMotionListener(pointerMotionListener);

	myOC_Pointer.setPreferredSize(new Dimension(ocpMaxX, ocpMaxY));

	int twidth = 645;
	int theight = 539;

	myOC_Pointer.offScreen = this.createImage(twidth, theight);
	Image backgroundIce = getImage(imgWebBase,"Ice.1_blue.jpg");
	myOC_Pointer.setImage(backgroundIce);

	myOC_Pointer.init();

	// myOC_Pointer.setBorder(BorderFactory.createCompoundBorder(
	//                    BorderFactory.createLineBorder(Color.red),
	//                    myOC_Pointer.getBorder()));
	//  There are issues here.  setPrefSize does not pass info along to OCP
	//  so we hard code it


	//myOC_Pointer.setPreferredSize(new Dimension(800, 539));

	//779x539

	//Set up the background image for Pointer


	//Set up background image for Discuss Display
	//		Image discussDisplayImg=getImage(imgWebBase, "IceImageDiscussBackground_sm.jpg");
	//		myDiscussDisplayOnly.setImage(discussDisplayImg);


	label1 = new JLabel("A", JLabel.CENTER);
	label2 = new JLabel("LABEL2", JLabel.CENTER);
	label3 = new JLabel("LABEL3", JLabel.CENTER);
	label4 = new JLabel("LABEL4", JLabel.CENTER);

	panel1 = new JPanel( );
	//	panel1.setLayout(new GridLayout( 1, 2 ));
	panel1.setLayout(new BorderLayout( 10,10));
	panel1.add(myOC_Discuss, BorderLayout.WEST);
	panel1.add(label1, BorderLayout.CENTER);
	panel1.add(myOC_Cloud, BorderLayout.EAST);
	panel1.setBackground(Color.black);

	// panel2 = new JPanel( );
	// 		panel2.setLayout(new BorderLayout( 10,10));
	// 		panel2.add(myDiscussDisplayOnly, BorderLayout.WEST);
	// 		panel2.add(label1, BorderLayout.CENTER);
	// 		panel2.add(myOC_Pointer, BorderLayout.EAST);
	// 		panel2.setBackground(Color.black);




	panel2 = new JPanel( );
	panel2.setLayout(new BoxLayout( panel2, BoxLayout.X_AXIS));
	panel2.add(myDiscussDisplayOnly);
	panel2.add(label1);
	panel2.add(myOC_Pointer);
	panel2.setBackground(Color.black);
	panel2.setPreferredSize(new Dimension(900, 500));


	// panel2 = new JPanel( );
	// 		panel2.setLayout(new FlowLayout());
	// 		panel2.add(myDiscussDisplayOnly);
	// 		panel2.add(myOC_Pointer);

	//	add(myDiscussClient);
	//	add(myCloudClient);
	//	add(myCloudDiscuss);

	add(panel1, BorderLayout.NORTH);
	add(panel2, BorderLayout.CENTER);
	//		add(myOC_Pointer, BorderLayout.EAST);
	//		add(myDiscussDisplayOnly, BorderLayout.WEST);


	operaSand = new NSand(); 
	operaSand.connect();
	connected = true;
	int d[] = new int[1];
	d[0] = 0;
	nThread = new NomadsAppThread(this);
	nThread.setRunState(true);
	nThread.start();

	nECThread = new NomadsErrCheckThread(this);
	nECThread.start();


	operaSand.sendGrain((byte)NAppID.OPERA_CLIENT, (byte)NCommand.REGISTER, (byte)NDataType.UINT8, 1, d );
	// init volume to 0
	myOC_Pointer.myBusReader.amplitude.set(0.0);


    }


    public void handle() { //byte bite String text
	byte incCmd, incAppID, incDType, incDLen;
	int i,j;
	int incIntData[] = new int[1000];
	byte incByteData[] = new byte[1000];  // Cast as chars here because we're using chars -> strings
	NGrain grain;

	NGlobals.cPrint("OperaClient -> handle() ... waiting for data ...");

	setSandRead(false);
	grain = operaSand.getGrain();
	setSandRead(true);
	setHandleActive(true);

	NGlobals.cPrint("OperaClient -> got data === message num: " + msgCntr);

	// grain.print(); //prints grain data to console

	incAppID = grain.appID;
	incCmd = grain.command;

	byte incAppCmd = grain.command;
	byte incAppDataType = grain.dataType;
	int incAppDataLen = grain.dataLen;

	NGlobals.cPrint("OCli ===== READING ===== ");
	NGlobals.cPrint("OCli appID: " + NAppID.printID(incAppID));
	NGlobals.cPrint("OCli command: " + NCommand.printCmd(incAppCmd));
	NGlobals.cPrint("OCli dataType: " + NDataType.printDT(incAppDataType));
	NGlobals.cPrint("OCli dataLen: " + incAppDataLen);


	if (incAppID == NAppID.CONDUCTOR_PANEL) {
	    NGlobals.cPrint("OC: from Conductor Panel: ");

	    //if Appid= conductor panel, command = droplet status, get value, if zero diable, if 1 enable

	    if (incCmd == NCommand.SET_DROPLET_STATUS) {
		if (grain.bArray[0] == 0) {
		    myOC_Pointer.myBusReader.amplitude.set(0.0);
		    NGlobals.cPrint("OC: Setting Droplets to OFF");
		}
		else if (grain.bArray[0] == 1) {
		    myOC_Pointer.myBusReader.amplitude.set(1.0);
		    NGlobals.cPrint("OC: Setting Droplets to ON");
		}
	    }

	    else if (incCmd == NCommand.SET_DISCUSS_STATUS) {
		if (grain.bArray[0] == 0) {
		    myOC_Discuss.speak.setEnabled(false);
		    NGlobals.cPrint("OC: DISCUSS_STATUS false");
		}
		else if (grain.bArray[0] == 1) {
		    myOC_Discuss.speak.setEnabled(true);
		    NGlobals.cPrint("OC: DISCUSS_STATUS true");
		}
	    }

	    else if (incCmd == NCommand.SET_CLOUD_STATUS) {
		if (grain.bArray[0] == 0) {
		    myOC_Cloud.speak.setEnabled(false);
		    NGlobals.cPrint("OC: CLOUD_STATUS false");
		}
		else if (grain.bArray[0] == 1) {
		    myOC_Cloud.speak.setEnabled(true);
		    NGlobals.cPrint("OC: CLOUD_STATUS true");
		}
	    }
			
	    else if (incCmd == NCommand.SET_POINTER_STATUS) {
		if (grain.bArray[0] == 0) {
		    myOC_Pointer.ksize = 0;
		    myOC_Pointer.removeMouseListener(pointerMouseListener);
		    myOC_Pointer.removeMouseMotionListener(pointerMotionListener);
		    myOC_Pointer.repaint();
		    NGlobals.cPrint("OC: POINTER_STATUS false");
		}
		else if (grain.bArray[0] == 1) {
		    myOC_Pointer.ksize = 7;
		    myOC_Pointer.addMouseListener(pointerMouseListener);
		    myOC_Pointer.addMouseMotionListener(pointerMotionListener);
		    myOC_Pointer.repaint();
		    NGlobals.cPrint("OC: POINTER_STATUS true");
		}
	    }

	    else if (incCmd == NCommand.SET_DROPLET_VOLUME) {	
		double tDropVal = (double)grain.iArray[0]; //Using text from NGrain byte array--Should change to int array ***STK 6/20/12
		float tDropVolume = (float)(Math.pow(tDropVal, 2)/10000.0);

		NGlobals.cPrint("OC: tDropVolume = " + tDropVolume);
		//TO DO: Make this a log function. . .
		myOC_Pointer.myBusReader.amplitude.set(tDropVolume);

	    }
	}
	else if (incAppID == NAppID.OC_DISCUSS) {
	    if (incCmd == NCommand.SEND_MESSAGE) {
		String text = new String(grain.bArray);
		NGlobals.cPrint("OC: incoming discuss text: " + text);
		myDiscussDisplayOnly.handle(text);
		NGlobals.cPrint("OC: Setting Discuss Display");
	    }
	}

	// else if (incAppID == NAppID.OC_CLOUD) {
	//     if (incCmd == NCommand.SEND_MESSAGE) {
	// 	String text = new String(grain.bArray);
	// 	myOC_Cloud.handle(text);
	// 	NGlobals.cPrint("OC: Entering Cloud Discuss");
	//     }
	// }

	else if (incAppID == NAppID.DISCUSS_TOPIC)
	    if (incCmd == NCommand.SEND_MESSAGE) {
		String text = new String(grain.bArray);
		myOC_Discuss.handle(text); //Don't need to send AppID again, we've already filtered it out! ***STK 6/20/12
		NGlobals.cPrint("OC: Setting Discuss Topic");
	    }

	setHandleActive(false);

    }

    // ======================= end handle() ===================================================


    //	public void open()
    //	{  
    //		try {
    //			streamOut = new DataOutputStream(socket.getOutputStream());
    //			client = new Opera_ClientThread(this, socket);
    //			streamOut.writeByte((byte)app_id.OPERA_CLIENT);
    //			streamOut.writeUTF("PING");
    //
    //			int offsetNew_mx = (int)(myOC_Pointer.origX * 1.4728); // STK scales dot movement to size of Opera Main
    //			int offsetNew_my = (int)(myOC_Pointer.origY * 1.20593);
    //			String towrite = new String("INIT: C:" + offsetNew_mx + ":" + offsetNew_my);
    //			//	NGlobals.dtPrint("Byte is " + app_id.OC_POINTER + " and write is "
    //			//			+ towrite);
    //			//	NGlobals.dtPrint("towrite = " + towrite);
    //			streamOut.writeByte(app_id.OC_POINTER); 
    //			streamOut.writeUTF(towrite);
    //		} 
    //		catch(IOException ioe) { 
    //			NGlobals.dtPrint("Error opening output stream: ");
    //		} 
    //	}




    public void start() {
	runner = new Thread(this);
	runner.start();
    }

    public void run() {
	myOC_Pointer.run();
    }

    //These two methods check to make sure we only get ASCII values between 32-127
    public static boolean isAsciiPrintable(String str) {
	if (str == null) {
	    return false;
	}
	int sz = str.length();
	for (int i = 0; i < sz; i++) {
	    if (isAsciiPrintable(str.charAt(i)) == false) {
		return false;
	    }
	}
	return true;
    }
    public static boolean isAsciiPrintable(char ch) {
	//	NGlobals.dtPrint("incoming Ascii char= " + ch);
	return ch >= 32 && ch < 127;
    }
	
    //Action Listeners==================================================

    //OC_Discuss code===============================================
    ActionListener discussSpeakListener = new ActionListener() {	
	    public void actionPerformed(java.awt.event.ActionEvent ae) //for button press
	    {
		//	String tInput;
		Object source = ae.getSource();
		//	NGlobals.cPrint("entering speakListener");



		//listener code for speak button
		if (source == myOC_Discuss.speak)   {
		    myOC_Discuss.tInput = myOC_Discuss.input.getText();
		    if (myOC_Discuss.lastInput.equals(myOC_Discuss.tInput)) {
			myOC_Discuss.input.setText("");
			return;
		    }
		    else {
			myOC_Discuss.lastInput = myOC_Discuss.tInput;
			myOC_Discuss.input.setText("");
			//		NGlobals.cPrint("words = " + myOC_Discuss.tInput);
			int tLen = myOC_Discuss.tInput.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = myOC_Discuss.tInput.getBytes();
			if (isAsciiPrintable(myOC_Discuss.tInput) && (tLen > 0)) {
			    operaSand.sendGrain((byte)NAppID.OC_DISCUSS, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.BYTE, tLen, tStringAsBytes );
			}
			else {
			    NGlobals.cPrint("OC_Discuss: tInput:BAD ASCII CHAR: " + myOC_Discuss.tInput);
			}
						
			myOC_Discuss.input.setText("");
			NGlobals.cPrint("OC_Discuss: tInput: " + myOC_Discuss.tInput);
		    }

		} 
	    }
	};

    KeyListener discussReturnListener = new KeyListener() {	
	    public void keyPressed(java.awt.event.KeyEvent de) //for pressing return
	    {
		//	String tInput;
		Object source = de.getSource();
		//	NGlobals.cPrint("entering returnListener");

		if (de.getKeyCode() == 10) {
		    //listener code for pressing return key
		    if (source == myOC_Discuss.input)   {
			myOC_Discuss.tInput = myOC_Discuss.input.getText();
			if (myOC_Discuss.lastInput.equals(myOC_Discuss.tInput)) {
			    myOC_Discuss.input.setText("");
			    return;
			}
			else {
			    myOC_Discuss.lastInput = myOC_Discuss.tInput;
			    myOC_Discuss.input.setText("");
			    //		NGlobals.cPrint("words = " + myOC_Discuss.tInput);
			    int tLen = myOC_Discuss.tInput.length();
			    //    char[] tStringAsChars = tString.toCharArray();
			    byte[] tStringAsBytes = myOC_Discuss.tInput.getBytes();
			    //xxa
			    // byte d[] = new byte[1];
			    // d[0] = 1;
			    // NGlobals.cPrint("OC_Discuss: SENDING FAKE DATA");

			    // operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_DISCUSS_DISPLAY_STATUS, (byte)NDataType.UINT8, 1, d);
			    if (isAsciiPrintable(myOC_Discuss.tInput) && (tLen > 0)) {
				operaSand.sendGrain((byte)NAppID.OC_DISCUSS, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.BYTE, tLen, tStringAsBytes );
			    }
			    else {
				NGlobals.cPrint("OC_Discuss: tInput:BAD ASCII CHAR: " + myOC_Discuss.tInput);
			    }
			    myOC_Discuss.input.setText("");
			    NGlobals.cPrint("OC_Discuss: tInput: " + myOC_Discuss.tInput);
			}

		    }
		}
	    }
	    // end key pressed

	    public void keyReleased(KeyEvent de){
	    }

	    public void keyTyped(KeyEvent de){
	    }

	};


    //END OC_Discuss code===============================================

    //OC_Cloud code===============================================
    ActionListener cloudSpeakListener = new ActionListener() {	
	    public void actionPerformed(java.awt.event.ActionEvent ae) //for button press
	    {
		//	String tInput;
		Object source = ae.getSource();
		//		NGlobals.cPrint("entering speakListener");


		//listener code for speak button
		if (source == myOC_Cloud.speak)   {
		    myOC_Cloud.tInput = myOC_Cloud.input.getText();
		    if (myOC_Cloud.lastInput.equals(myOC_Cloud.tInput)) {
			myOC_Cloud.input.setText("");
			return;
		    }
		    else {
			myOC_Cloud.lastInput = myOC_Cloud.tInput;
			myOC_Cloud.input.setText("");
			//		NGlobals.cPrint("cloud words = " + myOC_Cloud.tInput);
			int tLen = myOC_Cloud.tInput.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = myOC_Cloud.tInput.getBytes();
			if (isAsciiPrintable(myOC_Cloud.tInput) && (tLen > 0)) {
			    operaSand.sendGrain((byte)NAppID.OC_CLOUD, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.BYTE, tLen, tStringAsBytes );
			}
			else {
			    NGlobals.cPrint("OC_Cloud: tInput:BAD ASCII CHAR: " + myOC_Cloud.tInput);
			}
			myOC_Cloud.input.setText("");
			NGlobals.cPrint("OC_Cloud: tInput: " + myOC_Cloud.tInput);
		    }

		} 
	    }
	};

    KeyListener cloudReturnListener = new KeyListener() {	
	    public void keyPressed(java.awt.event.KeyEvent ce) //for pressing return
	    {
		//	String tInput;
		Object source = ce.getSource();
		//		NGlobals.cPrint("entering returnListener");

		if (ce.getKeyCode() == 10) {
		    //listener code for pressing return key
		    if (source == myOC_Cloud.input)   {
			myOC_Cloud.tInput = myOC_Cloud.input.getText();
			if (myOC_Cloud.lastInput.equals(myOC_Cloud.tInput)) {
			    myOC_Cloud.input.setText("");
			    return;
			}
			else {
			    myOC_Cloud.lastInput = myOC_Cloud.tInput;
			    myOC_Cloud.input.setText("");
			    //		NGlobals.cPrint("words = " + myOC_Cloud.tInput);
			    int tLen = myOC_Cloud.tInput.length();
			    //    char[] tStringAsChars = tString.toCharArray();
			    byte[] tStringAsBytes = myOC_Cloud.tInput.getBytes();
			    if (isAsciiPrintable(myOC_Cloud.tInput) && tLen > 0) {
				operaSand.sendGrain((byte)NAppID.OC_CLOUD, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.BYTE, tLen, tStringAsBytes );
			    }
			    else {
				NGlobals.cPrint("OC_Cloud: tInput:BAD ASCII CHAR: " + myOC_Cloud.tInput);
			    }						myOC_Cloud.input.setText("");
			    NGlobals.cPrint("OC_Cloud: tInput: " + myOC_Cloud.tInput);
			}

		    }
		}
	    }
	    // end key pressed

	    public void keyReleased(KeyEvent de){
	    }

	    public void keyTyped(KeyEvent de){
	    }

	};
    //END OC_Cloud code===============================================

    // OC_Pointer code===============================================

    MouseListener pointerMouseListener = new MouseListener() {	
	    public void mouseEntered(java.awt.event.MouseEvent e){
	    }
	    public void mouseExited(java.awt.event.MouseEvent e){
	    }
	    public void mouseClicked(java.awt.event.MouseEvent e){
	    }
	    public void mousePressed(MouseEvent e) {
		myOC_Pointer.mx = e.getX();
		myOC_Pointer.my = e.getY();
		if (myOC_Pointer.x < myOC_Pointer.mx && myOC_Pointer.mx < myOC_Pointer.x + 40 
		    && myOC_Pointer.y < myOC_Pointer.my && myOC_Pointer.my < myOC_Pointer.y + 40) {
		    myOC_Pointer.isMouseDraggingBox = true;
		}
		e.consume();
	    }
	    public void mouseReleased(java.awt.event.MouseEvent e) {
		/*if (isMouseDraggingBox) {
		  try {
		  double myx = (mx - (width / 2)) / ((double) width * 3);
		  double myy = (my - (height / 2)) / ((double) height * 3);
		  String towrite = "move" + " " + personnum + " " + myx + " "
		  + myy;
		  NGlobals.cPrint("Byte is " + app_id.STUDENT_SAND_POINTER + " and write is "
		  + towrite);
		  streamOut.writeByte(app_id.STUDENT_SAND_POINTER);
		  streamOut.writeUTF(towrite);
		  } catch (IOException ioe) {
		  NGlobals.cPrint("Error writing...");
		  }
		  }*/
		myOC_Pointer.isMouseDraggingBox = false;
		e.consume();
	    }

	};

    MouseMotionListener pointerMotionListener = new MouseMotionListener() {	
	    public void mouseMoved(java.awt.event.MouseEvent e) {
	    }
	    public void mouseDragged(java.awt.event.MouseEvent e) {
		if (myOC_Pointer.isMouseDraggingBox) {
		    // get the latest mouse position
		    int new_mx = e.getX();
		    int new_my = e.getY();

		    //		NGlobals.cPrint("OCP: new_mx" + new_mx + " new_my" + new_my);

		    // displace the box by the distance the mouse moved since the last
		    // event
		    // Note that "x += ...;" is just shorthand for "x = x + ...;"

		    if (new_mx < 5)
			new_mx = 5;
		    if (new_mx > myOC_Pointer.width - 5)
			new_mx = myOC_Pointer.width - 5;
		    if (new_my < 5)
			new_my = 5;
		    if (new_my > myOC_Pointer.height-5)
			new_my = myOC_Pointer.height-5;

		    myOC_Pointer.x += new_mx - myOC_Pointer.mx;
		    myOC_Pointer.y += new_my - myOC_Pointer.my;

		    // update our data
		    myOC_Pointer.mx = new_mx;
		    myOC_Pointer.my = new_my;

		    myOC_Pointer.posX = myOC_Pointer.mx;
		    myOC_Pointer.posY = (myOC_Pointer.height - myOC_Pointer.my);
		    // NGlobals.cPrint( "posX " + myOC_Pointer.posX + "posY  " + myOC_Pointer.posY );
		    if (myOC_Pointer.isMouseDraggingBox) {
			double myx = (myOC_Pointer.mx - (myOC_Pointer.width / 2)) / ((double) myOC_Pointer.width * 3); 
			double myy = (myOC_Pointer.my - (myOC_Pointer.height / 2)) / ((double) myOC_Pointer.height * 3);
			NGlobals.cPrint("OCP: myx =" + myx + " myy" + myy);

			NumberFormat formatter = new DecimalFormat("#0.0000");
			float fx = (float)(new_mx-ocpCentX)/(float)ocpCentX;
			int offsetNew_mx = (int)(((fx + 1)/2) * 1000); // STK scales dot movement from 0-1000--TO CHANGE to floats...
			float fy = (float)(new_my-ocpCentY)/(float)ocpCentX;
			int offsetNew_my = (int)(((fy + 1)/2) * 1000);

			NGlobals.cPrint("OCP: width =" + offsetNew_mx + " height" + offsetNew_my + " fx:" + fx);

			int[] xy = new int[2];
			xy[0] = offsetNew_mx;
			xy[1] = offsetNew_my;

			operaSand.sendGrain((byte)NAppID.JOC_POINTER, (byte)NCommand.SEND_SPRITE_XY, (byte)NDataType.INT32, 2, xy );

		    }

		    myOC_Pointer.repaint();
		    e.consume();
		}
	    }

	};
    // END OC_Pointer code===============================================
}

