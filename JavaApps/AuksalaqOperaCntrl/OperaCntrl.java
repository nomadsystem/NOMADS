/*
  NOMADS Opera Control v.210
  Revised/cleaned, 6/20/2012, Steven Kemper
  Integrating NSand class
*/
import java.applet.*;

import java.awt.*;
import java.lang.Math;
import java.applet.*;
import java.awt.event.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;
import javax.swing.JToggleButton;
import javax.swing.DefaultButtonModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import java.util.*;
import java.util.Calendar;
import java.util.Random;

import java.net.*;
import java.io.*;
import netscape.javascript.*;
import nomads.v210_auk.*;

public class OperaCntrl extends JApplet implements ActionListener, KeyListener, Runnable {   

    int errFlag = 0;
    int lastThread = 0;
    Boolean handleActive = false;
    Boolean sandRead = false;
    Boolean connected = false;

    long mSecR=0;
    int resetCtr=0;
    int maxResets=1000;

    float mSecAvg=10;
    float mSecAvgL=10;
    int pToggle=0;

    int mSecLimit=3000;
    int errTrip=20;

    NSand operaSand;
    private NomadsAppThread nThread;
    private NomadsErrCheckThread nECThread;


    Random randNum;

    int     MAX_THREADS = 100000;


    int i,j;
    int width,height,fontSize;
    Font textFont;
    JButton aButton;

    int x,y,w,h;

    JSlider discussAlpha;
    JSlider cloudAlpha;
    JSlider pointerAlpha;
    JSlider dropletLevel;
    JSlider cloudSoundLevel;
    JSlider pointerToneLevel;
    JSlider mainVolLevel;

    JToggleButton discussCntrl;
    JToggleButton cloudCntrl;
    JToggleButton pointerCntrl;
    JToggleButton dropletCntrl;
    JToggleButton cloudSoundCntrl;
    JToggleButton pointerToneCntrl;

    JLabel promptLabel;
    JToggleButton promptButton;
    JTextField promptTextField;

    int clientCount = 0;
    JLabel clientCountLabel;

    JButton discussClear, cloudClear;  //, pointerClear;

    // private class NomadsAppThread extends Thread {
    // 	OperaCntrl client; //Replace with current class name

    // 	public NomadsAppThread(OperaCntrl _client) {
    // 	    client = _client;
    // 	}
    // 	public void run()    {			
    // 	    NGlobals.lPrint("ACP: NomadsAppThread -> run()");
    // 	    while (true)  {
    // 		client.handle();
    // 	    }
    // 	}
    // }

    // START errCheck() !!===================================!!

    private int maxSkip;

    private Object sandReadLock = new Object();

    public Boolean getSandRead() {
	synchronized (sandReadLock) {
	    return sandRead;
	}
    }

    public void setSandRead(Boolean sr) {
	synchronized (sandReadLock) {
	    sandRead = sr;
	}
    }

    private Object handleActiveLock = new Object();

    public Boolean getHandleActive() {
	synchronized(handleActiveLock) {
	    return handleActive;
	}
    }

    public void setHandleActive(Boolean ha) {
	synchronized(handleActiveLock) {
	    handleActive = ha;
	}
    }

    Thread runner;

    // DT 6/30/10:  not sure we need these anymore

    public void start() {
	runner = new Thread(this);
	runner.start();
    }

    public synchronized void run () {
	while (true) {
	    try {
		runner.sleep(1000);
	    }
	    catch (InterruptedException ie) {}
	}
    }

    private class NomadsErrCheckThread extends Thread {
	OperaCntrl client; //Replace with current class name

	public NomadsErrCheckThread(OperaCntrl _client) {
	    client = _client;
	}
	public synchronized void run()    {			
	    NGlobals.dtPrint("OperaControl ERRCHECKTHREAD -> run");
	    while (true)  {
		client.errCheck();
	    }
	}
    }



    private class NomadsAppThread extends Thread {
	OperaCntrl client; //Replace with current class name
	Calendar now;

	long handleStart=0;
	long handleEnd=1;
	long millis=0;
	Boolean runState=false;

	long mSecR=0;
	int resetCtr=0;
	int maxResets=10;
	
	float mSecAvg=10;
	float mSecAvgL=10;

	private Object handleStartLock = new Object();

	public long getHandleStart() {
	    synchronized(handleStartLock) {
		return handleStart;
	    }
	}

	public void setHandleStart(long hs) {
	    synchronized(handleStartLock) {
		handleStart = hs;
	    }
	}

	private Object handleEndLock = new Object();

	public long getHandleEnd() {
	    synchronized(handleEndLock) {
		return handleEnd;
	    }
	}

	public void setHandleEnd(long he) {
	    synchronized(handleEndLock) {
		handleEnd = he;
	    }
	}

	private Object runStateLock = new Object();

	public void setRunState(Boolean state) {
	    synchronized(runStateLock) {
		runState = state;
	    }
	}

	public Boolean getRunState() {
	    synchronized(runStateLock) {
		return runState;
	    }
	}


	public NomadsAppThread(OperaCntrl _client) {
	    client = _client;
	    // Connect
	}

	public synchronized void run()    {			
	    //NGlobals.dtPrint("NomadsAppThread -> run()");
	    while (getRunState() == true)  {
		now = Calendar.getInstance();
		setHandleStart(now.getTimeInMillis());
		client.handle();
		client.setHandleActive(false);
		handleEnd = now.getTimeInMillis();
		millis = getHandleEnd()-getHandleStart();
		NGlobals.dtPrint("handle() proc time:" + millis);
	    }
	}
    }



    public void errCheck() {
	Calendar now;
	long mSecN=0;
	long mSecH=0;
	long mSecDiff=0;

	// NGlobals.csvPrint(" . ");

	try {

	    if ((getHandleActive() == true) && (getSandRead() == true)) {

		now = Calendar.getInstance();
		mSecN = now.getTimeInMillis();
		mSecH = nThread.getHandleStart();

		mSecDiff = mSecN-mSecH;
		mSecAvg = ((mSecAvg*4)+mSecDiff)/5;
		mSecAvgL = ((mSecAvgL*19)+mSecDiff)/20;

		NGlobals.dtPrint("errCheck --> mSecDiff: " + mSecDiff + " avg: " + mSecAvg + " avgL: " + mSecAvgL);

		pToggle++;
		if (pToggle > 10) {
		    pToggle=0;
		}
		if (pToggle%2 == 0) {
		    NGlobals.dtPrint(">>> maxSkip:" + maxSkip);
		}

		if (mSecDiff > mSecLimit) {
		    errFlag += 1;
		    if (errFlag > 0) {
			System.out.println("   INCR ERROR COUNT: " + errFlag);
		    }
		    if ((errFlag > errTrip) && (connected == true)) {
			now = Calendar.getInstance();
			mSecR = now.getTimeInMillis(); // time of this reset
			System.out.println("-----> EREC #" + resetCtr);
			resetCtr++;
			if (resetCtr > maxResets) {
			    System.out.println("######### CRITICAL ERROR");
			    System.out.println(">>> #### MAX RESETS");
			    System.out.println(">>> sleeping 10 sec");
			    NomadsErrCheckThread.sleep(12000);
			    resetCtr=0;
			}
			nThread.setHandleStart(mSecN);
			System.out.println("######### NETWORK ERROR");
			// System.out.println(">>> handleErrCheck time diff: " + mSecDiff);
			// System.out.println(">>> halting thread.");
			nThread.setRunState(false);
			NomadsErrCheckThread.sleep(800);
			// deleteSynth(lastThread);
			nThread = null;
			System.out.println("   disconnecting.");
			operaSand.disconnect();
			NomadsErrCheckThread.sleep(800);
			operaSand = null;
			connected = false;
			System.out.println("   disconneced.");
			// System.out.println(">>> deleting sprites/synths.");
			// deleteAllSynths();
			// System.out.println(">>> sprites/synths deleted.");
			System.out.println("   Attempting reconnect.");
			NomadsErrCheckThread.sleep(800);
			operaSand = new NSand(); 
			operaSand.connect();

			int d[] = new int[1];
			d[0] = 0;
			operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.REGISTER, (byte)NDataType.UINT8, 1, d );

			connected = true;
			System.out.println("   reconnected!");			
			System.out.println("   attempting to restart thread.");			
			NomadsErrCheckThread.sleep(800);
			nThread = new NomadsAppThread(this);
			nThread.setRunState(true);
			nThread.start();


			System.out.println("Thread restarted.");			
			errFlag = 0;

			now = Calendar.getInstance();
			mSecN = now.getTimeInMillis();
			nThread.setHandleStart(mSecN);

		    }
		}
		else if ((errFlag > 0) && (mSecDiff < mSecLimit)) {
		    errFlag--;
		    System.out.println(">>> DECR ERROR COUNT: " + errFlag);
		}
	    }
	    NomadsErrCheckThread.sleep(100);
	}
	catch (InterruptedException ie) {}

    }

    // END errcheck -------------------------------------------




    public void createGUI(Container pane) {

	Color BG = new Color(0,0,100);      
	pane.setBackground(BG);
	pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
	//pane.setLayout(new FlowLayout(FlowLayout.CENTER));

	JPanel tPane = new JPanel();
	tPane.setLayout(new BoxLayout(tPane, BoxLayout.X_AXIS));
	//tPane.setLayout(new FlowLayout(FlowLayout.CENTER));

	JPanel discussWrapper = new JPanel();
	discussWrapper.setBorder(BorderFactory.createLineBorder (Color.black, 1));  
	JLabel discussLabel = new JLabel("Discuss");
	discussLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	discussWrapper.add(discussLabel);

	JPanel cloudWrapper = new JPanel();
	cloudWrapper.setBorder(BorderFactory.createLineBorder (Color.black, 1));
	JLabel cloudLabel = new JLabel("Cloud");
	cloudLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	cloudWrapper.add(cloudLabel);

	JPanel pointerWrapper = new JPanel();
	pointerWrapper.setBorder(BorderFactory.createLineBorder (Color.black, 1));
	JLabel pointerLabel = new JLabel("Pointer");
	pointerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	pointerWrapper.add(pointerLabel);

	JPanel dropletWrapper = new JPanel();
	dropletWrapper.setBorder(BorderFactory.createLineBorder (Color.black, 1));
	JLabel dropletLabel = new JLabel("Droplet");
	dropletLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	dropletWrapper.add(dropletLabel);

	JPanel cloudSoundWrapper = new JPanel();
	cloudSoundWrapper.setBorder(BorderFactory.createLineBorder (Color.black, 1));
	JLabel cloudSoundLabel = new JLabel("Cloud Sound");
	cloudSoundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	cloudSoundWrapper.add(cloudSoundLabel);

	JPanel pointerToneWrapper = new JPanel();
	pointerToneWrapper.setBorder(BorderFactory.createLineBorder (Color.black, 1));
	JLabel pointerToneLabel = new JLabel("Pointer Tone");
	pointerToneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	pointerToneWrapper.add(pointerToneLabel);

	JPanel mainVolWrapper = new JPanel();
	mainVolWrapper.setBorder(BorderFactory.createLineBorder (Color.black, 1));
	JLabel mainVolLabel = new JLabel("MainVol");
	mainVolLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	mainVolWrapper.add(mainVolLabel);

	discussCntrl = new JToggleButton("Discuss");
	//	discussCntrl.addChangeListener(buttonListener);
	discussCntrl.addItemListener(buttonListener);
	discussCntrl.setAlignmentX(Component.CENTER_ALIGNMENT);

	discussClear = new JButton("Clear");
	discussClear.addActionListener(checkListener); //To be renamed!
	discussClear.setAlignmentX(Component.CENTER_ALIGNMENT);


	cloudCntrl = new JToggleButton("Cloud");
	//		cloudCntrl.addChangeListener(buttonListener);
	cloudCntrl.addItemListener(buttonListener);
	cloudCntrl.setAlignmentX(Component.CENTER_ALIGNMENT);

	cloudClear = new JButton("Clear");
	cloudClear.addActionListener(checkListener);
	cloudClear.setAlignmentX(Component.CENTER_ALIGNMENT);


	pointerCntrl = new JToggleButton("Pointer");
	//		pointerCntrl.addChangeListener(buttonListener);
	pointerCntrl.addItemListener(buttonListener);
	pointerCntrl.setAlignmentX(Component.CENTER_ALIGNMENT);

	// pointerClear = new JButton("Clear");
	// pointerClear.addActionListener(checkListener);
	// pointerClear.setAlignmentX(Component.CENTER_ALIGNMENT);

	dropletCntrl = new JToggleButton("Droplets");
	//		dropletCntrl.addChangeListener(buttonListener);
	dropletCntrl.addItemListener(buttonListener);
	dropletCntrl.setAlignmentX(Component.CENTER_ALIGNMENT);

	cloudSoundCntrl = new JToggleButton("Cloud Sound");
	//		dropletCntrl.addChangeListener(buttonListener);
	cloudSoundCntrl.addItemListener(buttonListener);
	cloudSoundCntrl.setAlignmentX(Component.CENTER_ALIGNMENT);

	pointerToneCntrl = new JToggleButton("Pointer Tone");
	//		dropletCntrl.addChangeListener(buttonListener);
	pointerToneCntrl.addItemListener(buttonListener);
	pointerToneCntrl.setAlignmentX(Component.CENTER_ALIGNMENT);

	discussWrapper.setLayout(new BoxLayout(discussWrapper, BoxLayout.Y_AXIS));
	cloudWrapper.setLayout(new BoxLayout(cloudWrapper, BoxLayout.Y_AXIS));
	pointerWrapper.setLayout(new BoxLayout(pointerWrapper,BoxLayout.Y_AXIS));
	dropletWrapper.setLayout(new BoxLayout(dropletWrapper,BoxLayout.Y_AXIS));
	cloudSoundWrapper.setLayout(new BoxLayout(cloudSoundWrapper,BoxLayout.Y_AXIS));
	pointerToneWrapper.setLayout(new BoxLayout(pointerToneWrapper,BoxLayout.Y_AXIS));
	mainVolWrapper.setLayout(new BoxLayout(mainVolWrapper,BoxLayout.Y_AXIS));

	discussAlpha = new JSlider(JSlider.VERTICAL,0,255, 180);
	discussAlpha.addChangeListener(sliderListener);
	discussAlpha.addKeyListener(this); 

	discussAlpha.setMajorTickSpacing(10);
	discussAlpha.setPaintTicks(true);
	discussAlpha.setPaintLabels(true);

	discussWrapper.add(discussAlpha);
	discussWrapper.add(discussCntrl);
	discussWrapper.add(discussClear);

	cloudAlpha = new JSlider(JSlider.VERTICAL,0,255, 180);
	cloudAlpha.addChangeListener(sliderListener);

	cloudAlpha.setMajorTickSpacing(10);
	cloudAlpha.setPaintTicks(true);
	cloudAlpha.setPaintLabels(true);

	cloudWrapper.add(cloudAlpha);
	cloudWrapper.add(cloudCntrl);
	cloudWrapper.add(cloudClear);

	pointerAlpha = new JSlider(JSlider.VERTICAL,0, 255, 180);
	pointerAlpha.addChangeListener(sliderListener);

	pointerAlpha.setMajorTickSpacing(10);
	pointerAlpha.setPaintTicks(true);
	pointerAlpha.setPaintLabels(true);

	pointerWrapper.add(pointerAlpha);
	pointerWrapper.add(pointerCntrl);
	//	pointerWrapper.add(pointerClear);

	dropletLevel = new JSlider(JSlider.VERTICAL,0, 100, 100);
	dropletLevel.addChangeListener(sliderListener);

	dropletLevel.setMajorTickSpacing(10);
	dropletLevel.setPaintTicks(true);
	dropletLevel.setPaintLabels(true);

	dropletWrapper.add(dropletLevel);
	dropletWrapper.add(dropletCntrl);

	cloudSoundLevel = new JSlider(JSlider.VERTICAL,0, 100, 100);
	cloudSoundLevel.addChangeListener(sliderListener);

	cloudSoundLevel.setMajorTickSpacing(10);
	cloudSoundLevel.setPaintTicks(true);
	cloudSoundLevel.setPaintLabels(true);

	cloudSoundWrapper.add(cloudSoundLevel);
	cloudSoundWrapper.add(cloudSoundCntrl);

	pointerToneLevel = new JSlider(JSlider.VERTICAL,0, 100, 100);
	pointerToneLevel.addChangeListener(sliderListener);

	pointerToneLevel.setMajorTickSpacing(10);
	pointerToneLevel.setPaintTicks(true);
	pointerToneLevel.setPaintLabels(true);

	pointerToneWrapper.add(pointerToneLevel);
	pointerToneWrapper.add(pointerToneCntrl);

	mainVolLevel = new JSlider(JSlider.VERTICAL,0, 100, 100);
	mainVolLevel.addChangeListener(sliderListener);

	mainVolLevel.setMajorTickSpacing(10);
	mainVolLevel.setPaintTicks(true);
	mainVolLevel.setPaintLabels(true);
	mainVolWrapper.add(mainVolLevel);

	//Prompt init
	JPanel promptLabelPanel = new JPanel();
	promptLabel = new JLabel("NOMADS Prompt", JLabel.CENTER);
	promptLabelPanel.add(promptLabel);
	promptLabelPanel.setMaximumSize(new Dimension((int)(width * 0.1), height));

	JPanel promptTextAndButton = new JPanel();
	promptTextAndButton.setLayout(new GridLayout(1,2));
	promptTextField = new JTextField("",40);
	promptTextField.addKeyListener(this);
	promptButton = new JToggleButton("Prompt");
	promptButton.setMaximumSize(new Dimension(20,10));
	promptButton.setSelected(true);
	promptButton.addItemListener(buttonListener);
	promptTextAndButton.add(promptTextField);
	promptTextAndButton.add(promptButton);
	//	promptTextAndButton.setMaximumSize(new Dimension(width, height));

	//Prompt init
	JPanel clientCountLabelPanel = new JPanel();

	clientCountLabel = new JLabel("Client Count: " + clientCount, JLabel.CENTER);
	// clientCountLabelPanel.add(clientCountLabel);
	clientCountLabelPanel.setMaximumSize(new Dimension((int)(width * 0.1), height));


	JPanel promptWrapper = new JPanel(new GridLayout(3,1));
	promptWrapper.setBorder(BorderFactory.createLineBorder (Color.black, 1));
	promptWrapper.setMaximumSize(new Dimension((int)(width * 0.5), height));
	// promptWrapper.add(clientCountLabelPanel);
	promptWrapper.add(promptLabelPanel);
	promptWrapper.add(promptTextAndButton);


	tPane.add(discussWrapper);
	tPane.add(cloudWrapper);
	tPane.add(pointerWrapper);
	tPane.add(dropletWrapper);
	tPane.add(cloudSoundWrapper);
	tPane.add(pointerToneWrapper);
	tPane.add(mainVolWrapper);

	tPane.setOpaque(false);
	pane.add(promptWrapper);
	pane.add(tPane);


    }

    public static void main(String args[])
    {
	/* DO: Change TUT_SineFreq to match the name of your class. Must match file name! */
	OperaCntrl  applet = new OperaCntrl();

	Frame appletFrame = new Frame("OperaCntrl");

	appletFrame.add(applet);
	appletFrame.resize(600,600);
	appletFrame.show();
    }

    public void init()
    {  	

	int i;

	randNum = new Random();

	width = getSize().width;
	height = getSize().height;

	i = 0;
	j = 0;
	createGUI(getContentPane());

	operaSand = new NSand();
	operaSand.connect();
	connected = true;

	byte d[] = new byte[1];
	d[0] = 0;

	nThread = new NomadsAppThread(this);
	nThread.setRunState(true);
	nThread.start();

	nECThread = new NomadsErrCheckThread(this);
	nECThread.start();

	operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.REGISTER, (byte)NDataType.UINT8, 1, d );

    }	

    // ------------------------------------------------------------------------------------------------
    // BEGIN handle()
    // ------------------------------------------------------------------------------------------------

    public void handle() { ///bite, text
	int i,j,fc,sc,x,y,cnt,cln,chk;
	float freq,amp;
	String temp,thread,input,tCnt,tCln,tChk,tempString;
	int THREAD_ID;
	float xput,yput;

	byte incCmd, incAppID, incDType, incDLen;
	int incIntData[] = new int[1000];
	byte incByteData[] = new byte[1000];  // Cast as chars here because we're using chars -> strings
	NGrain grain;

	NGlobals.cPrint("ACP: OperaCntrl -> handle()");

	setSandRead(false);
	grain = operaSand.getGrain();
	if (grain == null) {
	    setSandRead(true);
	    setHandleActive(true);
	    while(true) {
		try {
		    runner.sleep(1000);
		}
		catch (InterruptedException ie) {}
	    }
	}

	if (grain == null) 
	    return;
	else 
	    grain.print(); //prints grain data to console

	incAppID = grain.appID;
	incCmd = grain.command;


	byte incAppCmd = grain.command;
	byte incAppDataType = grain.dataType;
	int incAppDataLen = grain.dataLen;

	NGlobals.cPrint("OCTL ===== READING ===== ");
	NGlobals.cPrint("OCTL appID: " + NAppID.printID(incAppID));
	NGlobals.cPrint("OCTL command: " + NCommand.printCmd(incAppCmd));
	NGlobals.cPrint("OCTL dataType: " + NDataType.printDT(incAppDataType));
	NGlobals.cPrint("OCTL dataLen: " + incAppDataLen);


	//		if (incAppID == NAppID.MONITOR) {
	//			if (text.equals("CHECK")) {
	//				try {
	//					streamOut.writeByte((byte)app_id.CONDUCTOR_PANEL);
	//					streamOut.writeUTF("PING");
	//				}
	//				catch(IOException ioe) {
	//					System.out.println("Error writing to output stream: ");
	//				}
	//			}	 
	//		}   

	if (incAppID == NAppID.SERVER) {

	    // checked = new JLabel("CHECKED: 0");
	    // cleaned = new JLabel("CLEANED: 0");
	    // counted = new JLabel("CLIENTS: 0");


	    // if (incCmd == NCommand.SET_CLIENT_COUNT) {
	    // 	clientCount = grain.iArray[0];
	    // 	String tString = new String("Client Count:  " + clientCount);
	    // 	clientCountLabel.setText(tString);
	    // }

	    if (incCmd == NCommand.SET_DISCUSS_STATUS) {
		if (grain.bArray[0] == 0) {
		    discussCntrl.getModel().setSelected(false);
		    NGlobals.cPrint("ACP: Discuss disable");
		}
		else if (grain.bArray[0] == 1) {
		    discussCntrl.getModel().setSelected(true);
		    NGlobals.cPrint("ACP: Discuss enable");
		}
	    }

	    if (incCmd == NCommand.SET_CLOUD_STATUS) {
		if (grain.bArray[0] == 0) {
		    cloudCntrl.getModel().setSelected(false);
		    NGlobals.cPrint("ACP: Cloud Disable");
		}
		else if (grain.bArray[0] == 1) {
		    cloudCntrl.getModel().setSelected(true);
		    NGlobals.cPrint("ACP: Cloud Enable");
		}
	    }

	    if (incCmd == NCommand.SET_POINTER_STATUS) {
		if (grain.bArray[0] == 0) {
		    pointerCntrl.getModel().setSelected(false);
		    NGlobals.cPrint("ACP: Pointer Disable");
		}
		else if (grain.bArray[0] == 1) {
		    pointerCntrl.getModel().setSelected(true);
		    NGlobals.cPrint("ACP: Pointer Enable");
		}
	    }


	    if (incCmd == NCommand.SET_DROPLET_STATUS) {
		if (grain.bArray[0] == 0) {
		    dropletCntrl.getModel().setSelected(false);
		    NGlobals.cPrint("ACP: Droplet disable");
		}
		else if (grain.bArray[0] == 1) {
		    dropletCntrl.getModel().setSelected(true);
		    NGlobals.cPrint("ACP: Droplet enable");
		}
	    }
	    if (incCmd == NCommand.SET_CLOUD_SOUND_STATUS) {
		if (grain.bArray[0] == 0) {
		    cloudSoundCntrl.getModel().setSelected(false);
		    NGlobals.cPrint("ACP: cloudSound disable");
		}
		else if (grain.bArray[0] == 1) {
		    cloudSoundCntrl.getModel().setSelected(true);
		    NGlobals.cPrint("ACP: cloudSound enable");
		}
	    }
	    if (incCmd == NCommand.SET_POINTER_TONE_STATUS) {
		if (grain.bArray[0] == 0) {
		    pointerToneCntrl.getModel().setSelected(false);
		    NGlobals.cPrint("ACP: pointerTone disable");
		}
		else if (grain.bArray[0] == 1) {
		    pointerToneCntrl.getModel().setSelected(true);
		    NGlobals.cPrint("ACP: pointerTone enable");
		}
	    }
	    if (incCmd == NCommand.SEND_PROMPT_ON) {
		String prompt = new String(grain.bArray);
		NGlobals.cPrint("ACP: SEND_PROMPT_ON Prompt: "+ prompt);
		promptLabel.setText(prompt);
		promptTextField.setText(prompt);
		promptTextField.setEnabled(false);
	    }
	}

	if (incAppID == NAppID.CONDUCTOR_PANEL) {
	    if (incCmd == NCommand.SEND_PROMPT_ON) {
		NGlobals.cPrint("ACP: SEND_PROMPT_ON");
		String prompt = new String(grain.bArray);
		promptLabel.setText(prompt);
	    }
	    if (incCmd == NCommand.SEND_PROMPT_OFF) {
		promptLabel.setText("");
		NGlobals.cPrint("ACP: SEND_PROMPT_OFF");
	    }
	}

	NGlobals.cPrint("-------------------------------------------------[OC]\n");

    }

    // ------------------------------------------------------------------------------------------------
    // END handle()
    // ------------------------------------------------------------------------------------------------

    //OC_Discuss code===============================================
    ActionListener checkListener = new ActionListener() {	
	    public void actionPerformed(java.awt.event.ActionEvent ae) //for button press
	    {
		//	String tInput;
		Object source = ae.getSource();
		//	NGlobals.cPrint("entering speakListener");

		byte d[] = new byte[1]; //to stash settings data

		if (source == discussClear) {
		    NGlobals.cPrint("ACP: Action:  discussClear");
		    d[0] = 0; //0 clears display
		    operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_DISCUSS_DISPLAY_STATUS, (byte)NDataType.UINT8, 1, d);
		}
		else if (source == cloudClear) {
		    NGlobals.cPrint("ACP: Action:  cloudClear");
		    d[0] = 0; //0 clears display
		    operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_CLOUD_DISPLAY_STATUS, (byte)NDataType.UINT8, 1, d);
		}

		//			if (source == pointerClear) {
		//				NGlobals.cPrint("Action:  pointerClear");
		//				String tString = " "; 
		//				int tLen = tString.length();
		//				byte[] tStringAsBytes = tString.getBytes();
		//				operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.CLEAR_POINTER, (byte)NDataType.BYTE, tLen, tStringAsBytes );
		//			}
	    }		
	};

    //****STK 7/24/12 Changed from changelistener to itemlistener, prevents multiple outputs from single button click
    ItemListener buttonListener = new ItemListener() {
	    public void itemStateChanged(ItemEvent e) {
		AbstractButton abstractButton = (AbstractButton) e.getSource();
		ButtonModel buttonModel = abstractButton.getModel();

		byte d[] = new byte[1]; //to stash settings data

		if (abstractButton == discussCntrl) {
		    if (e.getStateChange() == ItemEvent.SELECTED) {
			d[0] = 1;
			operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_DISCUSS_STATUS, (byte)NDataType.UINT8, 1, d );
			NGlobals.cPrint("ACP: discussCntrl:  ON");
		    }
		    else {
			d[0] = 0;
			operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_DISCUSS_STATUS, (byte)NDataType.UINT8, 1, d );
			NGlobals.cPrint("ACP: discussCntrl:  OFF");
		    }
		}

		else if (abstractButton == cloudCntrl) {
		    if (e.getStateChange() == ItemEvent.SELECTED) {
			d[0] = 1;
			operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_CLOUD_STATUS, (byte)NDataType.UINT8, 1, d );
			NGlobals.cPrint("ACP: cloudCntrl:  ON");

		    } 
		    else {
			d[0] = 0;
			operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_CLOUD_STATUS, (byte)NDataType.UINT8, 1, d );
			NGlobals.cPrint("ACP: cloudCntrl:  OFF");

		    }
		}

		else if (abstractButton == pointerCntrl) {
		    if (e.getStateChange() == ItemEvent.SELECTED) {
			d[0] = 1;
			operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_POINTER_STATUS, (byte)NDataType.UINT8, 1, d );
			NGlobals.cPrint("ACP: pointerCntrl:  ON");

		    } 
		    else {
			d[0] = 0;
			operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_POINTER_STATUS, (byte)NDataType.UINT8, 1, d );
			NGlobals.cPrint("ACP: pointerCntrl:  OFF"); 
		    }
		}
		else if (abstractButton == dropletCntrl) {
		    if (e.getStateChange() == ItemEvent.SELECTED) {
			d[0] = 1;
			operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_DROPLET_STATUS, (byte)NDataType.UINT8, 1, d );
			NGlobals.cPrint("ACP: dropletCntrl:  ON"); 

		    } 
		    else {
			d[0] = 0;
			operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_DROPLET_STATUS, (byte)NDataType.UINT8, 1, d );
			NGlobals.cPrint("ACP: dropletCntrl:  OFF"); 
		    }
		}
		else if (abstractButton == cloudSoundCntrl) {
		    if (e.getStateChange() == ItemEvent.SELECTED) {
			d[0] = 1;
			operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_CLOUD_SOUND_STATUS, (byte)NDataType.UINT8, 1, d );
			NGlobals.cPrint("ACP: cloudSoundCntrl:  ON"); 
		    } 
		    else {
			d[0] = 0;
			operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_CLOUD_SOUND_STATUS, (byte)NDataType.UINT8, 1, d );
			NGlobals.cPrint("ACP: cloudSoundCntrl:  OFF"); 
		    }
		}
		else if (abstractButton == pointerToneCntrl) {
		    if (e.getStateChange() == ItemEvent.SELECTED) {
			d[0] = 1;
			operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_POINTER_TONE_STATUS, (byte)NDataType.UINT8, 1, d );
			NGlobals.cPrint("ACP: pointerToneCntrl:  ON"); 
		    } 
		    else {
			d[0] = 0;
			operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_POINTER_TONE_STATUS, (byte)NDataType.UINT8, 1, d );
			NGlobals.cPrint("ACP: pointerToneCntrl:  OFF"); 
		    }
		}
		else if (abstractButton == promptButton) {
		    if (e.getStateChange() == ItemEvent.SELECTED) {
			String tString = promptTextField.getText();
			byte[] tStringAsBytes = tString.getBytes();
			int tLen = tString.length();
			if (tLen < 1) {
			    NGlobals.cPrint("ACP: Prompt trying to send Null string (BAD!)");
			    promptButton.setSelected(false);
			}
			else {
			    operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SEND_PROMPT_ON, (byte)NDataType.CHAR, tLen, tStringAsBytes);
			    NGlobals.cPrint("ACP: Prompt " + tString + " sent"); 
			    promptTextField.setEnabled(false);
			}
		    }
		    else {
			d[0] = 0;
			promptTextField.setText("");
			promptTextField.setEnabled(true);
			operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SEND_PROMPT_OFF, (byte)NDataType.UINT8, 1, d);
			NGlobals.cPrint("ACP: Prompt OFF");
		    }
		}
	    }
	};

    //****STK 7/24/12 Moved buttons to itemlistener, code below to be deleted once everything is tested...

    //ChangeListener buttonListener = new ChangeListener() {
    //	public void stateChanged(ChangeEvent changeEvent) {
    //		AbstractButton abstractButton = (AbstractButton) changeEvent.getSource();
    //		ButtonModel buttonModel = abstractButton.getModel();
    //		boolean armed = buttonModel.isArmed();
    //		boolean pressed = buttonModel.isPressed();
    //		boolean selected = buttonModel.isSelected();
    //
    //		byte d[] = new byte[1]; //to stash settings data
    //
    //		if (abstractButton == discussCntrl) {
    //			if (selected) {
    //				d[0] = 1;
    //				operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_DISCUSS_STATUS, (byte)NDataType.UINT8, 1, d );
    //				NGlobals.cPrint("ACP: discussCntrl:  ON");
    //			} 
    //			else {
    //				d[0] = 0;
    //				operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_DISCUSS_STATUS, (byte)NDataType.UINT8, 1, d );
    //				NGlobals.cPrint("discussCntrl:  OFF");
    //
    //			}
    //		}
    //		else if (abstractButton == cloudCntrl) {
    //			if (selected) {
    //				d[0] = 1;
    //				operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_CLOUD_STATUS, (byte)NDataType.UINT8, 1, d );
    //				NGlobals.cPrint("cloudCntrl:  ON");
    //
    //			} 
    //			else {
    //				d[0] = 0;
    //				operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_CLOUD_STATUS, (byte)NDataType.UINT8, 1, d );
    //				NGlobals.cPrint("cloudCntrl:  OFF");
    //
    //			}
    //		}
    //		if (abstractButton == pointerCntrl) {
    //			if (selected) {
    //				d[0] = 1;
    //				operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_POINTER_STATUS, (byte)NDataType.UINT8, 1, d );
    //				NGlobals.cPrint("pointerCntrl:  ON");
    //
    //			} 
    //			else {
    //				String tString = " "; 
    //				d[0] = 0;
    //				operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_POINTER_STATUS, (byte)NDataType.UINT8, 1, d );
    //				NGlobals.cPrint("pointerCntrl:  OFF"); 
    //			}
    //		}
    //		if (abstractButton == dropletCntrl) {
    //			if (selected) {
    //				d[0] = 1;
    //				operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_DROPLET_STATUS, (byte)NDataType.UINT8, 1, d );
    //				NGlobals.cPrint("dropletCntrl:  ON"); 
    //
    //			} 
    //			else {
    //				d[0] = 0;
    //				operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_DROPLET_STATUS, (byte)NDataType.UINT8, 1, d );
    //				NGlobals.cPrint("dropletCntrl:  OFF"); 
    //			}
    //		}
    //	}
    //};

    ChangeListener sliderListener = new ChangeListener() {
	    public void stateChanged(ChangeEvent e) {
		int d[] = new int[1]; //to stash settings data

		String n;
		String tempString;
		JSlider source = (JSlider)e.getSource();
		if (source == discussAlpha) {
		    d[0] = (int)source.getValue();
		    operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_DISCUSS_ALPHA, (byte)NDataType.INT32, 1, d );
		    NGlobals.cPrint("ACP: discussAlpha:" + d[0]);  
		}
		if (source == cloudAlpha) {
		    d[0] = (int)source.getValue();
		    operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_CLOUD_ALPHA, (byte)NDataType.INT32, 1, d );
		    NGlobals.cPrint("ACP: cloudAlpha:" + d[0]); 

		}
		if (source == pointerAlpha) {
		    d[0] = (int)source.getValue();
		    operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_POINTER_ALPHA, (byte)NDataType.INT32, 1, d );
		    NGlobals.cPrint("ACP: pointerAlpha:" + d[0]); 

		}
		if (source == dropletLevel) {
		    d[0] = (int)source.getValue();
		    operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_DROPLET_VOLUME, (byte)NDataType.INT32, 1, d );
		    NGlobals.cPrint("ACP: dropletLevel:" + d[0]); 
		}
		if (source == cloudSoundLevel) {
		    d[0] = (int)source.getValue();
		    operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_CLOUD_SOUND_VOLUME, (byte)NDataType.INT32, 1, d );
		    NGlobals.cPrint("ACP: cloudSoundLevel:" + d[0]); 
		}
		if (source == pointerToneLevel) {
		    d[0] = (int)source.getValue();
		    operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_POINTER_TONE_VOLUME, (byte)NDataType.INT32, 1, d );
		    NGlobals.cPrint("ACP: pointerToneLevel:" + d[0]); 
		}
		if (source == mainVolLevel) {
		    d[0] = (int)source.getValue();
		    operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_SYNTH_VOLUME, (byte)NDataType.INT32, 1, d );
		    NGlobals.cPrint("ACP: mainVolLevel:" + d[0]); 
		}

	    }
	};

    public void keyPressed (KeyEvent e)
    {

	//boolean armed = buttonModel.isArmed();
	//boolean pressed = buttonModel.isPressed();
	//boolean selected = buttonModel.isSelected();

	int v,t,k;
	k = e.getKeyCode();
	//	NGlobals.cPrint("ACP: key: " + k);

	//****STK 7/31/12 Disabled other key commands, as they could be triggered from text entered in text field
	//		if (k == 103) { 
	//			v = discussAlpha.getValue();
	//			discussAlpha.setValue(v+3);
	//		}
	//		else if (k == 100) { 
	//			v = discussAlpha.getValue();
	//			discussAlpha.setValue(v-3);
	//		}
	//		else if (k == 97) { 				
	//			discussCntrl.getModel().setArmed(true);
	//			discussCntrl.getModel().setSelected(true);
	//			discussCntrl.getModel().setPressed(true);
	//		}
	//
	//		else if (k == 104) { 
	//			v = cloudAlpha.getValue();
	//			cloudAlpha.setValue(v+3);
	//		}
	//		else if (k == 101) { 
	//			v = cloudAlpha.getValue();
	//			cloudAlpha.setValue(v-3);
	//		}
	//		else if (k == 98) { 
	//			cloudCntrl.getModel().setPressed(true);
	//			cloudCntrl.getModel().setSelected(true);
	//			cloudCntrl.getModel().setPressed(true);
	//		}
	//
	//		else if (k == 105) { 
	//			v = pointerAlpha.getValue();
	//			pointerAlpha.setValue(v+3);
	//		}
	//		else if (k == 102) { 
	//			v = pointerAlpha.getValue();
	//			pointerAlpha.setValue(v-3);
	//		}
	//		else if (k == 99) { 
	//			pointerCntrl.getModel().setPressed(true);
	//			pointerCntrl.getModel().setSelected(true);
	//			pointerCntrl.getModel().setPressed(true);
	//		}
    }

    public void keyReleased(KeyEvent e){
    }

    public void keyTyped(KeyEvent e){
    }


    public void actionPerformed( ActionEvent ae )
    {
	Object obj = ae.getSource();
	if( obj == aButton ) {
	    NGlobals.cPrint("ACP: You pressed a button");
	}
    }


}

