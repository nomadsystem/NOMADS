//
// NOMADS Instructor Control Panel
//

import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import nomads.v210.*;

public class InstructorControlPanel extends JApplet  implements  ActionListener {

    // Change appropriate directory 
    // TODO:  use -D_$USER directive in makefile

    // String user = new String("http://nomads.music.virginia.edu/classroom/JavaApps/");
    // String user = new String("http://nomads.music.virginia.edu/kemper/NOMADS/JavaApps/");
    String user = new String("http://nomads.music.virginia.edu/topper/NOMADS/JavaApps/");


    NSand instructorControlPanelSand;
    private NomadsAppThread nThread;

    JMenuItem pollMenu_VoteAgain ;
    JMenuItem pollMenu_ResetScreen;
    JMenuItem pollMenu_PollMode;
    JCheckBoxMenuItem  pollMenu_ShowAnswer;
    JCheckBoxMenuItem  pollMenu_ShowStats;
    JCheckBoxMenuItem  pollMenu_ShowQuestion;

    JButton joinButton, discussButton, cloudButton,  pollButton, uGrooveButton;
    JButton discussDisplayButton, cloudDisplayButton, pollDisplayButton, uGrooveDisplayButton;
    JButton discussPromptButton, cloudPromptButton, pollPromptButton, uGroovePromptButton;
    JButton mosaicButton, mosaicDisplayButton;
    JButton pointerButton, pointerDisplayButton, pointerPropmtButton;
    JLabel blankButton, blankButton2;

    Image discussPromptImg, discussDisplayImg, discussImgOn, discussImgOff;
    Image cloudPromptImg, cloudDisplayImg, cloudImgOn, cloudImgOff;
    Image pollPromptImg, pollDisplayImg, pollImgOn, pollImgOff;
    Image mosaicImgOff, mosaicImgOn, mosaicPromptImg, mosaicDisplayImg;
    Image pointerDisplayImg, pointerPromptImg, pointerImgOn, pointerImgOff;
    Image uGroovePromptImg, uGrooveDisplayImg, uGrooveImgOff, uGrooveImgOn;

    ImageIcon discussIcon, discussPromptIcon, discussDisplayIcon;
    ImageIcon cloudIcon, cloudPromptIcon, cloudDisplayIcon;
    ImageIcon pollIcon, pollPromptIcon, pollDisplayIcon;
    ImageIcon uGrooveIcon, uGroovePromptIcon, uGrooveDisplayIcon;
    ImageIcon mosaicIcon, mosaicerDisplayIcon;
    ImageIcon pointerIcon, pointerDisplayIcon;
    ImageIcon icon;

    ImageIcon discussIconOn, pollIconOn, cloudIconOn, mosaicIconOn, pointerIconOn, uGrooveIconOn;
    ImageIcon discussIconOff, pollIconOff, cloudIconOff, mosaicIconOff, pointerIconOff, uGrooveIconOff;

    GridLayout buttonGridLayout = new GridLayout(6,3,0,0); //3rd value was set to 5

    int discussOnOff, cloudOnOff, pollOnOff, mosaicOnOff, pointerOnOff, uGrooveOnOff; //*****STK variables store current state of button	

    JPanel butPanel = new JPanel();
    JPanel logoPanel;
    JLabel imageLabel;

    URL discussPromptURL;
    URL discussDisplayURL;
    URL cloudPromptURL;
    URL cloudDisplayURL;
    URL pollPromptURL;
    URL pollDisplayURL;
    URL mosaicDisplayURL;
    URL pointerDisplayURL;
    URL uGroovePromptURL;
    URL uGrooveDisplayURL;

    // begin v2.0 panels and frames --------------

    InstructorGroupDiscuss myInstructorGroupDiscussPanel;
    JFrame instructorGroupDiscussFrame;

    GroupDiscussPrompt myGroupDiscussPromptPanel;
    JFrame groupDiscussPromptFrame;

    PollPrompt myPollPromptPanel;
    JFrame pollPromptFrame;

    // CloudPrompt myCloudPromptPanel;
    // JFrame cloudPromptFrame;

    // CloudDisplay myCloudDisplayPanel;
    // JFrame cloudDisplayFrame;



    // PollDisplay myPollDisplayPanel;
    // JFrame pollDisplayFrame;

    // MosaicDisplay myMosaicDisplayPanel;
    // JFrame mosaciDisplayFrame;

    // PointerDisplay myPointerDisplayPanel;
    // JFrame pointerDisplayFrame;

    // UnityGrooveDisplay myUnityGrooveDisplayPanel;
    // JFrame unityGrooveDisplayFrame;

    // UnityGroovePrompt myUnityGroovePromptPanel;
    // JFrame unityGroovePromptFrame;

    // end v2.0 panels and frames ----------------
    

    private class NomadsAppThread extends Thread {
	InstructorControlPanel client; //Replace with current class name

	public NomadsAppThread(InstructorControlPanel _client) {
	    client = _client;
	    // Connect
	    instructorControlPanelSand = new NSand();
	    instructorControlPanelSand.connect();
	}

	public void run() {			
	    NGlobals.lPrint("NomadsAppThread -> run()");
	    while (true)  {
		client.handle();
	    }
	}
    }


    public void init( ) {
	int discussOnOff = 0;
	int cloudOnOff = 0;
	int pollOnOff = 0;
	int mosaicOnOff = 0;
	int pointerOnOff = 0;
	int uGrooveOnOff = 0;

	//============================= MENU BAR BEGIN ==============================
	JMenuBar menuBar = new JMenuBar ();
	JMenu pollMenu = new JMenu ("poll");

	pollMenu_VoteAgain = new JMenuItem ("Vote Again");
	pollMenu.add (pollMenu_VoteAgain);
	pollMenu_VoteAgain.addActionListener (this);

	menuBar.add (pollMenu);
	setJMenuBar (menuBar);

	pollMenu_VoteAgain.setEnabled (true);
	//============================= MENU BAR END ==============================

	butPanel.setLayout(buttonGridLayout);
	Container content = getContentPane();
	content.setBackground(Color.black);
	setJMenuBar(menuBar);

	try {
	    // discussPromptURL = new URL(user +  "GroupDiscussPrompt");                  	
	    // discussDisplayURL = new URL(user + "GroupDiscussInstructor");
	    cloudPromptURL = new URL(user + "CloudPrompt");                  	
	    cloudDisplayURL = new URL(user + "CloudDisplay");   
	    pollPromptURL = new URL(user + "PollPrompt");       
	    pollDisplayURL = new URL(user + "PollDisplay");                  	
	    mosaicDisplayURL = new URL(user + "MosaicInstructor");
	    pointerDisplayURL = new URL(user + "SandPointerDisplay");
	    uGroovePromptURL = new URL(user + "UnityGroovePrompt");       
	    uGrooveDisplayURL = new URL(user + "UnityGrooveStudent");
	}
	catch (MalformedURLException e) {
	}
		
	//Code below starts thread (connects), sends register byte
	byte d[] = new byte[1];
	d[0] = 0;
	nThread = new NomadsAppThread(this);
	nThread.start();

	setupButtons( );

	instructorControlPanelSand.sendGrain((byte)NAppID.INSTRUCTOR_PANEL, (byte)NCommand.REGISTER, (byte)NDataType.UINT8, 1, d );
    }

    // button setup function -----------------------------------------------------------------------

    public void setupButtons() {
	int w,h;
	w = (int)(240*0.7);
	h = (int)(57*0.9);

	// Images --------------------------------
	
	discussImgOff=getImage(getCodeBase( ),"buttons/InstructDiscussOff.png");
	discussImgOn=getImage(getCodeBase( ),"buttons/InstructDiscussOn.png");
	discussPromptImg=getImage(getCodeBase(), "buttons/InstructDiscussPrompt.png");
	discussDisplayImg=getImage(getCodeBase(), "buttons/InstructDiscussDisplay.png");

	cloudImgOff=getImage(getCodeBase( ),"buttons/InstructCloudOff.png");
	cloudImgOn=getImage(getCodeBase( ),"buttons/InstructCloudOn.png");
	cloudPromptImg=getImage(getCodeBase(), "buttons/InstructCloudPrompt.png");
	cloudDisplayImg=getImage(getCodeBase(), "buttons/InstructCloudDisplay.png ");	

	pollImgOff=getImage(getCodeBase( ),"buttons/InstructPollOff.png");
	pollImgOn=getImage(getCodeBase( ),"buttons/InstructPollOn.png");
	pollPromptImg=getImage(getCodeBase(), "buttons/InstructPollPrompt.png");
	pollDisplayImg=getImage(getCodeBase(), "buttons/InstructPollDisplay.png ");

	mosaicImgOff=getImage(getCodeBase( ), "buttons/InstructSoundOff.png"); //InstructSoundOff.png
	mosaicImgOn=getImage(getCodeBase( ),"buttons/InstructSoundOn.png");
	mosaicPromptImg=getImage(getCodeBase( ),"buttons/InstructSoundPrompt.png");
	mosaicDisplayImg=getImage(getCodeBase( ),"buttons/InstructSoundDisplay.png"); 

	pointerImgOff=getImage(getCodeBase( ), "buttons/InstructPointerOff.png"); //
	pointerImgOn=getImage(getCodeBase( ),"buttons/InstructPointerOn.png");
	pointerPromptImg=getImage(getCodeBase( ),"buttons/InstructPointerPrompt.png");
	pointerDisplayImg=getImage(getCodeBase( ),"buttons/InstructPointerDisplay.png");

	uGrooveImgOff=getImage(getCodeBase( ), "buttons/InstructUnityOff.png"); // FIX THESE NAMES 
	uGrooveImgOn=getImage(getCodeBase( ),"buttons/InstructUnityOn.png");
	uGroovePromptImg=getImage(getCodeBase( ),"buttons/InstructUnityPrompt.png");
	uGrooveDisplayImg=getImage(getCodeBase( ),"buttons/InstructUnityDisplay.png");

	// Icons ---------------------------------

	discussIconOn = new ImageIcon(discussImgOn);
	cloudIconOn = new ImageIcon(cloudImgOn);
	pollIconOn = new ImageIcon(pollImgOn);
	mosaicIconOn = new ImageIcon(mosaicImgOn);
	pointerIconOn = new ImageIcon(pointerImgOn);
	uGrooveIconOn = new ImageIcon(uGrooveImgOn);

	discussIconOff = new ImageIcon(discussImgOff);
	cloudIconOff = new ImageIcon(cloudImgOff);
	pollIconOff = new ImageIcon(pollImgOff);
	mosaicIconOff = new ImageIcon(mosaicImgOff);
	pointerIconOff = new ImageIcon(pointerImgOff);
	uGrooveIconOff = new ImageIcon(uGrooveImgOff);

	discussPromptIcon = new ImageIcon(discussPromptImg); //STK 1_29_10
	discussDisplayIcon = new ImageIcon(discussDisplayImg);
	cloudPromptIcon = new ImageIcon(cloudPromptImg); //STK 1_29_10
	cloudDisplayIcon = new ImageIcon(cloudDisplayImg);
	pollPromptIcon = new ImageIcon(pollPromptImg); //STK 1_29_10
	pollDisplayIcon = new ImageIcon(pollDisplayImg);
	uGroovePromptIcon = new ImageIcon(uGroovePromptImg); 

	pointerDisplayIcon = new ImageIcon(pointerDisplayImg);
	pointerDisplayIcon = new ImageIcon(pointerDisplayImg);

	uGrooveDisplayIcon = new ImageIcon(uGrooveDisplayImg);

	// on/off button basics ------------------

	discussButton = new JButton( discussIcon );
	discussButton.setMargin(new Insets(0, 0, 0, 0));

	cloudButton = new JButton( cloudIcon );
	cloudButton.setMargin(new Insets(0,0,0,0));

	pollButton = new JButton( pollIcon );
	pollButton.setMargin(new Insets(0,0,0,0));

	mosaicButton = new JButton( mosaicIcon);
	mosaicButton.setMargin(new Insets(0,0,0,0));

	pointerButton = new JButton( pointerIcon );
	pointerButton.setMargin(new Insets(0,0,0,0));

	uGrooveButton = new JButton( uGrooveIcon);
	uGrooveButton.setMargin(new Insets(0,0,0,0));

	// Set up button specifics + actions + set class pointers ----------------------------------
	//
	// v2.0 frame/panel instantiation code below

	// Discuss Prompt ------------------------

	myGroupDiscussPromptPanel = new GroupDiscussPrompt();
	myGroupDiscussPromptPanel.init(instructorControlPanelSand);
	groupDiscussPromptFrame = new JFrame("Discuss Prompt");
	groupDiscussPromptFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	groupDiscussPromptFrame.setLocationRelativeTo(null);
	groupDiscussPromptFrame.setPreferredSize(new Dimension(750,200));
	groupDiscussPromptFrame.getContentPane().add(myGroupDiscussPromptPanel);
	groupDiscussPromptFrame.pack();

	discussPromptButton = new JButton ( discussPromptIcon ); //STK 1_29_10
	discussPromptButton.setMargin(new Insets(0, 0, 0, 0));
	discussPromptButton.setBorderPainted(false);
	discussPromptButton.addActionListener( this );

	// Instructor Discuss (display) ----------

	myInstructorGroupDiscussPanel = new InstructorGroupDiscuss();
	myInstructorGroupDiscussPanel.init(instructorControlPanelSand);
	instructorGroupDiscussFrame = new JFrame("Discussion");
	instructorGroupDiscussFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	instructorGroupDiscussFrame.setLocationRelativeTo(null);
	instructorGroupDiscussFrame.setPreferredSize(new Dimension(800,600));
	instructorGroupDiscussFrame.getContentPane().add(myInstructorGroupDiscussPanel);
	instructorGroupDiscussFrame.pack();

	discussDisplayButton = new JButton ( discussDisplayIcon );
	discussDisplayButton.setMargin(new Insets(0,0,0,0));
	discussDisplayButton.setBorderPainted(false);
	discussDisplayButton.addActionListener( this );

	// Poll Prompt ---------------------------

	myPollPromptPanel = new PollPrompt();
	myPollPromptPanel.init(instructorControlPanelSand);
	pollPromptFrame = new JFrame("Discuss Prompt");
	pollPromptFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	pollPromptFrame.setLocationRelativeTo(null);
	pollPromptFrame.setPreferredSize(new Dimension(750,200));
	pollPromptFrame.getContentPane().add(myPollPromptPanel);
	pollPromptFrame.pack();

	pollPromptButton = new JButton ( pollPromptIcon );
	pollPromptButton.setMargin(new Insets(0,0,0,0));
	pollPromptButton.setBorderPainted(false);
	pollPromptButton.addActionListener( this );

	// these still need to be 2.0 ified ------

	cloudPromptButton = new JButton ( cloudPromptIcon );
	cloudPromptButton.setMargin(new Insets(0,0,0,0));
	cloudPromptButton.setBorderPainted(false);
	cloudPromptButton.addActionListener( this );

	cloudDisplayButton = new JButton ( cloudDisplayIcon );
	cloudDisplayButton.setMargin(new Insets(0,0,0,0));
	cloudDisplayButton.setBorderPainted(false);
	cloudDisplayButton.addActionListener( this );

	pollDisplayButton = new JButton ( pollDisplayIcon );
	pollDisplayButton.setMargin(new Insets(0,0,0,0));
	pollDisplayButton.setBorderPainted(false);
	pollDisplayButton.addActionListener( this );

	pointerDisplayButton = new JButton ( pointerDisplayIcon );
	pointerDisplayButton.setMargin(new Insets(0,0,0,0));
	pointerDisplayButton.setBorderPainted(false);
	pointerDisplayButton.addActionListener( this );

	uGroovePromptButton = new JButton ( uGroovePromptIcon ); 
	uGroovePromptButton.setMargin(new Insets(0,0,0,0));
	uGroovePromptButton.setBorderPainted(false);
	uGroovePromptButton.addActionListener( this );

	uGrooveDisplayButton = new JButton ( uGrooveDisplayIcon ); 
	uGroovePromptButton.setMargin(new Insets(0,0,0,0));
	uGrooveDisplayButton.setBorderPainted(false);
	uGrooveDisplayButton.addActionListener( this );


	//Need to make a "blank" button for the grid layout to be happy
	blankButton = new JLabel( "" );
	blankButton2 = new JLabel( "" );

	// on/off buttons

	discussButton.setBorderPainted(false);
	discussButton.addActionListener( this );
	discussButton.setPressedIcon(new ImageIcon(discussImgOn));

	cloudButton.setBorderPainted(false);
	cloudButton.addActionListener( this );
	cloudButton.setPressedIcon(new ImageIcon(cloudImgOn));

	pollButton.setBorderPainted(false);
	pollButton.addActionListener( this );
	pollButton.setPressedIcon(new ImageIcon(pollImgOn));

	mosaicButton.setBorderPainted(false);
	mosaicButton.addActionListener( this );
	mosaicButton.setPressedIcon(new ImageIcon(mosaicImgOn));

	pointerButton.setBorderPainted(false);
	pointerButton.addActionListener( this );
	pointerButton.setPressedIcon(new ImageIcon(pointerImgOn));

	uGrooveButton.setBorderPainted(false);
	uGrooveButton.addActionListener( this );
	uGrooveButton.setPressedIcon(new ImageIcon(uGrooveImgOn)); //UN COMMENT ME WHEN WE GET THE uGROOVE FILES

	// add the buttons to "button panel" screen --------

	butPanel.setBackground(Color.black);
	butPanel.add( discussButton, buttonGridLayout );
	butPanel.add( discussPromptButton, buttonGridLayout );
	butPanel.add( discussDisplayButton, buttonGridLayout );
	butPanel.add( cloudButton, buttonGridLayout );
	butPanel.add( cloudPromptButton, buttonGridLayout );
	butPanel.add( cloudDisplayButton, buttonGridLayout );
	butPanel.add( pollButton, buttonGridLayout );
	butPanel.add( pollPromptButton, buttonGridLayout );
	butPanel.add( pollDisplayButton, buttonGridLayout );
	butPanel.add( mosaicButton, buttonGridLayout );
	butPanel.add( blankButton, buttonGridLayout );
	butPanel.add( pointerDisplayButton, buttonGridLayout);
	butPanel.add( pointerButton, buttonGridLayout );
	butPanel.add( blankButton2, buttonGridLayout );
	butPanel.add( pointerDisplayButton, buttonGridLayout );
	butPanel.add( uGrooveButton, buttonGridLayout );
	butPanel.add( uGroovePromptButton, buttonGridLayout );
	butPanel.add( uGrooveDisplayButton, buttonGridLayout );

	// add the "button panel" to the window ------------

	add (butPanel);
    }

    public void setupImage( Image img ) {
	icon.setImage( img );
	imageLabel.setIcon( icon );
	repaint( );
    }    


    //============================= HANDLE ==============================
    public void handle() {
	NGlobals.cPrint("InstructorControlPanel -> handle()");

	NGrain grain;
	byte tByte;
	grain = instructorControlPanelSand.getGrain();
	grain.print(); //prints grain data to console

	byte incAppID = grain.appID;
	byte tCmd = grain.command;
	String input = new String(grain.bArray);

	if (grain.dataType == NDataType.CHAR || grain.dataType == NDataType.UINT8) {
	    tByte = grain.bArray[0];
	    NGlobals.cPrint("INSTRUCTOR PANEL RCVD CMD: " + tCmd + " w/ BYTE: " + tByte);
	}
	else {
	    tByte = 0;
	}
	    

	// Buttons --------------------------------------------------------------
	
	if (incAppID == NAppID.SERVER) {   // TODO:  may need to change this to NAppID.INSTRUCTOR_PANEL
	    
	    if (tCmd == NCommand.SET_DISCUSS_STATUS) {
		discussOnOff = (int)tByte;
		if (tByte == 0) {
		    discussButton.setIcon(discussIconOff);
		}
		else if (tByte == 1) {
		    discussButton.setIcon(discussIconOn);
		}
	    }	    
	    else if (tCmd == NCommand.SET_CLOUD_STATUS) {
		cloudOnOff = (int)tByte;
		if (tByte == 0) {
		    cloudButton.setIcon(cloudIcon);
		}
		else if (tByte == 1) {
		    cloudButton.setIcon(cloudIconOn);
		}
	    }
	    else if (tCmd == NCommand.SET_POLL_STATUS) {
		pollOnOff = (int)tByte;
		if (tByte == 0) {
		    pollButton.setIcon(pollIcon);
		}
		else if (tByte == 1) {
		    pollButton.setIcon(pollIconOn);
		}
	    }
	    else if (tCmd == NCommand.SET_MOSAIC_STATUS) {
		mosaicOnOff = (int)tByte;
		if (tByte == 0) {
		    mosaicButton.setIcon(mosaicIcon);
		}
		else if (tByte == 1) {
		    mosaicButton.setIcon(mosaicIconOn);
		}
	    }
	    
	    else if (tCmd == NCommand.SET_SWARM_STATUS) {
		pointerOnOff = (int)tByte;
		if (tByte == 0) {
		    pointerButton.setIcon(pointerIcon);
		}
		else if (tByte == 1) {
		    pointerButton.setIcon(pointerIconOn);
		}
	    }
	    else if (tCmd == NCommand.SET_UGROOVE_STATUS) {
		uGrooveOnOff = (int)tByte;
		if (tByte == 0) {
		    uGrooveButton.setIcon(uGrooveIcon);
		}
		else if (tByte == 1) {
		    uGrooveButton.setIcon(uGrooveIconOn);
		}
	    }
	}
	
	// end buttons ----------------------------

	// Send to DISCUSS ------------------------
	if (incAppID == NAppID.INSTRUCTOR_PANEL || incAppID == NAppID.DISCUSS || incAppID == NAppID.INSTRUCTOR_DISCUSS || incAppID == NAppID.DISCUSS_PROMPT) {
	    myInstructorGroupDiscussPanel.handle(grain);
	}
	

    }

    public void open() {
	sendMessage("KHAN");
    }

    //-------------------------------------------------------------------------------
    public void actionPerformed(ActionEvent ae) {
	byte tByte[] = new byte[1];

	Object source = ae.getSource( );

	// ON/OF buttons ------------------------------------

	if( source == discussButton) {
	    if ( discussOnOff == 0) {
		discussButton.setIcon(discussIconOn);
		discussOnOff = 1;
	    }
	    else if ( discussOnOff == 1) {
		discussButton.setIcon(discussIcon);
		discussOnOff = 0;
	    }
	    tByte[0] = (byte)discussOnOff;
	    NGlobals.cPrint(NAppID.INSTRUCTOR_PANEL + "SET_DISCUSS_STATUS: " + discussOnOff);
	    instructorControlPanelSand.sendGrain(NAppID.INSTRUCTOR_PANEL,
						 NCommand.SET_DISCUSS_STATUS,
						 NDataType.UINT8,
						 1,
						 tByte);
	}

	else if( source == cloudButton ) {		
	    if ( cloudOnOff == 0) {
		cloudButton.setIcon(cloudIconOn);
		cloudOnOff = 1;
	    }
	    else if ( cloudOnOff == 1) {
		cloudButton.setIcon(cloudIcon);
		cloudOnOff = 0;
	    }
	    tByte[0] = (byte)cloudOnOff;
	    NGlobals.cPrint(NAppID.INSTRUCTOR_PANEL + "SET_CLOUD_STATUS: " + cloudOnOff);
	    instructorControlPanelSand.sendGrain(NAppID.INSTRUCTOR_PANEL,
						 NCommand.SET_CLOUD_STATUS,
						 NDataType.UINT8,
						 (byte)1,
						 tByte);
	}

	else if( source == mosaicButton ) {
	    if ( mosaicOnOff == 0) {
		mosaicButton.setIcon(mosaicIconOn);
		mosaicOnOff = 1;
	    }
	    else if ( mosaicOnOff == 1) {
		mosaicButton.setIcon(mosaicIcon);
		mosaicOnOff = 0;
	    }
	    tByte[0] = (byte)mosaicOnOff;
	    NGlobals.cPrint(NAppID.INSTRUCTOR_PANEL + "SET_MOSAIC_STATUS: " + cloudOnOff);
	    instructorControlPanelSand.sendGrain(NAppID.INSTRUCTOR_PANEL,
						 NCommand.SET_MOSAIC_STATUS,
						 NDataType.UINT8,
						 1,
						 tByte);
	}

	else if( source == pollButton ) {
	    if ( pollOnOff == 0) {
		pollButton.setIcon(pollIconOn);
		pollOnOff = 1;
	    }      
	    else if ( pollOnOff == 1) {
		pollButton.setIcon(pollIcon);
		pollOnOff = 0;
	    }
	    tByte[0] = (byte)pollOnOff;
	    NGlobals.cPrint(NAppID.INSTRUCTOR_PANEL + "SET_POLL_STATUS: " + pollOnOff);
	    instructorControlPanelSand.sendGrain(NAppID.INSTRUCTOR_PANEL,
						 NCommand.SET_POLL_STATUS,
						 NDataType.UINT8,
						 1,
						 tByte);
	}

	else if( source == pointerButton ) {	
	    if ( pointerOnOff == 0) {
		pointerButton.setIcon(pointerIconOn);
		pointerOnOff = 1;
	    }      
	    else if ( pointerOnOff == 1) {
		pointerButton.setIcon(pointerIcon);
		pointerOnOff = 0;
	    }
	    tByte[0] = (byte)pointerOnOff;
	    NGlobals.cPrint(NAppID.INSTRUCTOR_PANEL + "SET_POINTER_STATUS: " + pointerOnOff);
	    instructorControlPanelSand.sendGrain(NAppID.INSTRUCTOR_PANEL,
						 NCommand.SET_POINTER_STATUS,
						 NDataType.UINT8,
						 1,
						 tByte);
	}

	else if( source == uGrooveButton ) {	
	    if ( uGrooveOnOff == 0) {
		uGrooveButton.setIcon(uGrooveIconOn);
		uGrooveOnOff = 1;
	    }      
	    else if ( uGrooveOnOff == 1) {
		uGrooveButton.setIcon(uGrooveIcon);
		uGrooveOnOff = 0;
	    }
	    tByte[0] = (byte)uGrooveOnOff;
	    NGlobals.cPrint(NAppID.INSTRUCTOR_PANEL + "SET_UGROOVE_STATUS: " + uGrooveOnOff);
	    instructorControlPanelSand.sendGrain(NAppID.INSTRUCTOR_PANEL,
						 NCommand.SET_UGROOVE_STATUS,
						 NDataType.UINT8,
						 1,
						 tByte);

	}

	// END ------ ON/OFF buttons ------------------------------------

	// Launch sub apps v2.0 ---------------------------------------

	else if( source == discussPromptButton ) {
	    groupDiscussPromptFrame.setVisible(true);
	    // getAppletContext().showDocument(discussPromptURL,"DiscussPrompt"); 	
	}

	else if( source == discussDisplayButton ) {
	    instructorGroupDiscussFrame.setVisible(true);
	    // xxx
	    // getAppletContext().showDocument(discussURL,"CloudPrompt"); 	
	    

	}

	else if( source == cloudDisplayButton ) {
	    getAppletContext().showDocument(cloudDisplayURL,"CloudDisplay");
	}	

	else if( source == pollPromptButton ) {
	    getAppletContext().showDocument(pollPromptURL,"PollPrompt"); 	
	}	

	else if( source == pollDisplayButton ) {
	    getAppletContext().showDocument(pollDisplayURL,"PollDisplay"); 	
	}


	else if( source == cloudPromptButton ) {
	    getAppletContext().showDocument(cloudPromptURL,"CloudPrompt"); 	
	}

	else if( source == mosaicDisplayButton ) {
	    getAppletContext().showDocument(mosaicDisplayURL,"Sound Mosaic"); 	
	}

	else if( source == pointerDisplayButton ) {
	    getAppletContext().showDocument(pointerDisplayURL,"Sand Pointer"); 	
	}

	else if( source == uGroovePromptButton ) {
	    getAppletContext().showDocument(uGroovePromptURL,"Unity Groove Prompt"); 	
	}

	else if( source == uGrooveDisplayButton ) {
	    getAppletContext().showDocument(uGrooveDisplayURL,"Unity Groove Display"); 	
	}

	else if ( source == pollMenu_VoteAgain ) {
	    sendMessage("VOTE");

	    NGlobals.cPrint("Vote Again selected");
	}
    }

    void sendMessage (String _output){
	String output = _output;
	int outputLen = output.length();
	byte[] outputAsBytes = output.getBytes();
	instructorControlPanelSand.sendGrain(
					     NAppID.INSTRUCTOR_PANEL,
					     NCommand.SEND_MESSAGE,
					     NDataType.BYTE,
					     outputLen,
					     outputAsBytes);
    }
}
