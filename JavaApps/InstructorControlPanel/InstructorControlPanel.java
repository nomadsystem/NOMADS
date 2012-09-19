// NOMADS
// Instructor Control Panel
// Revised, 2012.06.21, Paul Turowski


import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import nomads.v210.*;

public class InstructorControlPanel extends JApplet  implements  ActionListener {

	// Change to your directory
	// for production version, just use "/"
	String user = new String("http://nomads.music.virginia.edu/kemper/NOMADS/JavaApps/");

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
	JButton mosaicButton, soundDisplayButton;
	JButton swarmButton, pointDisplayButton, pointPropmtButton;
	JLabel blankButton, blankButton2;
	Image discussPromptImg, discussDisplayImg, discussImgOn, discussImgOff;
	Image cloudPromptImg, cloudDisplayImg, cloudImgOn, cloudImgOff;
	Image pollPromptImg, pollDisplayImg, pollImgOn, pollImgOff;
	Image soundDisplayImg, soundPromptImg, soundImgOn, soundImgOff;
	Image uGroovePromptImg, uGrooveDisplayImg, uGrooveImgOff, uGrooveImgOn;
	Image pointImgOff, pointImgOn, pointPromptImg, pointDisplayImg;

	ImageIcon discussIcon, discussPromptIcon, discussDisplayIcon;
	ImageIcon cloudIcon, cloudPromptIcon, cloudDisplayIcon;
	ImageIcon pollIcon, pollPromptIcon, pollDisplayIcon;
	ImageIcon uGrooveIcon, uGroovePromptIcon, uGrooveDisplayIcon;
	ImageIcon mosaicIcon, soundDisplayIcon, swarmIcon, pointDisplayIcon, icon;
	ImageIcon discussIconOn, pollIconOn, cloudIconOn, mosaicIconOn, swarmIconOn, uGrooveIconOn;

	GridLayout buttonGridLayout = new GridLayout(6,3,0,0); //3rd value was set to 5

	int discussOnOff, cloudOnOff, pollOnOff, mosaicOnOff, swarmOnOff, uGrooveOnOff; //*****STK variables store current state of button	

	JPanel butPanel = new JPanel();
	JPanel logoPanel;
	JLabel imageLabel;
	URL discussURL;
	URL cloudURL;
	URL soundDisplayURL;
	URL pollURL;
	URL pollDisplayURL;
	URL cloudPromptURL;
	URL discussPromptURL;
	URL soundSwarmDisplayURL;
	URL uGroovePromptURL;
	URL uGrooveDisplayURL;

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
		int swarmOnOff = 0;
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
		setupButtons( );

		try {
			discussURL = new URL(user + "GroupDiscuss");
			cloudURL = new URL(user + "CloudDisplay");   
			pollURL = new URL(user + "PollPrompt");       
			pollDisplayURL = new URL(user + "PollDisplay");                  	
			discussPromptURL = new URL(user +  "GroupDiscussPrompt");                  	
			cloudPromptURL = new URL(user + "CloudPrompt");                  	
			soundDisplayURL = new URL(user + "SoundMosaicInstructor");
			soundSwarmDisplayURL = new URL(user + "SoundSwarmDisplay");
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
		instructorControlPanelSand.sendGrain((byte)NAppID.INSTRUCTOR_PANEL, (byte)NCommand.REGISTER, (byte)NDataType.UINT8, 1, d );
	}

	public void setupButtons() {
		int w,h;
		w = (int)(240*0.7);
		h = (int)(57*0.9);
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

		//****** Need to change these buttons to new images STK 3_23_10
		soundImgOff=getImage(getCodeBase( ), "buttons/InstructSoundOff.png"); //InstructSoundOff.png
		soundImgOn=getImage(getCodeBase( ),"buttons/InstructSoundOn.png");
		soundPromptImg=getImage(getCodeBase( ),"buttons/InstructSoundPrompt.png");
		soundDisplayImg=getImage(getCodeBase( ),"buttons/InstructSoundDisplay.png"); 

		pointImgOff=getImage(getCodeBase( ), "buttons/InstructPointerOff.png"); //
		pointImgOn=getImage(getCodeBase( ),"buttons/InstructPointerOn.png");
		pointPromptImg=getImage(getCodeBase( ),"buttons/InstructPointerPrompt.png");
		pointDisplayImg=getImage(getCodeBase( ),"buttons/InstructPointerDisplay.png");

		uGrooveImgOff=getImage(getCodeBase( ), "buttons/InstructUnityOff.png"); // FIX THESE NAMES 
		uGrooveImgOn=getImage(getCodeBase( ),"buttons/InstructUnityOn.png");
		uGroovePromptImg=getImage(getCodeBase( ),"buttons/InstructUnityPrompt.png");
		uGrooveDisplayImg=getImage(getCodeBase( ),"buttons/InstructUnityDisplay.png");

		discussIcon = new ImageIcon(discussImgOff);
		cloudIcon = new ImageIcon(cloudImgOff);
		pollIcon = new ImageIcon(pollImgOff);
		mosaicIcon = new ImageIcon(soundImgOff);
		swarmIcon = new ImageIcon(pointImgOff);
		uGrooveIcon = new ImageIcon(uGrooveImgOff);

		discussPromptIcon = new ImageIcon(discussPromptImg); //STK 1_29_10
		discussDisplayIcon = new ImageIcon(discussDisplayImg);
		cloudPromptIcon = new ImageIcon(cloudPromptImg); //STK 1_29_10
		cloudDisplayIcon = new ImageIcon(cloudDisplayImg);
		pollPromptIcon = new ImageIcon(pollPromptImg); //STK 1_29_10
		pollDisplayIcon = new ImageIcon(pollDisplayImg);
		uGroovePromptIcon = new ImageIcon(uGroovePromptImg); 

		soundDisplayIcon = new ImageIcon(soundDisplayImg);
		pointDisplayIcon = new ImageIcon(pointDisplayImg);
		uGrooveDisplayIcon = new ImageIcon(uGrooveDisplayImg);

		discussIconOn = new ImageIcon(discussImgOn);
		cloudIconOn = new ImageIcon(cloudImgOn);
		pollIconOn = new ImageIcon(pollImgOn);
		mosaicIconOn = new ImageIcon(soundImgOn);
		swarmIconOn = new ImageIcon(pointImgOn);
		uGrooveIconOn = new ImageIcon(uGrooveImgOn);


		discussButton = new JButton( discussIcon );
		discussButton.setMargin(new Insets(0, 0, 0, 0));

		discussPromptButton = new JButton ( discussPromptIcon ); //STK 1_29_10
		discussPromptButton.setMargin(new Insets(0, 0, 0, 0));
		discussDisplayButton = new JButton ( discussDisplayIcon );
		discussDisplayButton.setMargin(new Insets(0,0,0,0));

		cloudButton = new JButton( cloudIcon );
		cloudButton.setMargin(new Insets(0,0,0,0));
		cloudPromptButton = new JButton ( cloudPromptIcon );
		cloudPromptButton.setMargin(new Insets(0,0,0,0));
		cloudDisplayButton = new JButton ( cloudDisplayIcon );
		cloudDisplayButton.setMargin(new Insets(0,0,0,0));

		pollButton = new JButton( pollIcon );
		pollButton.setMargin(new Insets(0,0,0,0));
		pollPromptButton = new JButton ( pollPromptIcon );
		pollPromptButton.setMargin(new Insets(0,0,0,0));
		pollDisplayButton = new JButton ( pollDisplayIcon );
		pollDisplayButton.setMargin(new Insets(0,0,0,0));


		mosaicButton = new JButton( mosaicIcon);
		mosaicButton.setMargin(new Insets(0,0,0,0));

		soundDisplayButton = new JButton ( soundDisplayIcon );
		soundDisplayButton.setMargin(new Insets(0,0,0,0));

		swarmButton = new JButton( swarmIcon );
		swarmButton.setMargin(new Insets(0,0,0,0));
		//	pointPromptButton = new JButton ( pointPromptIcon );
		//	pointPromptButton.setMargin(new Insets(0,0,0,0));
		pointDisplayButton = new JButton ( pointDisplayIcon );
		pointDisplayButton.setMargin(new Insets(0,0,0,0));

		//	uGrooveButton = new JButton( "uGrooveOnOff" ); //replace with uGrooveIcon
		uGrooveButton = new JButton( uGrooveIcon);
		uGrooveButton.setMargin(new Insets(0,0,0,0));
		//	uGrooveButton.setForeground(Color.WHITE);
		//	uGroovePromptButton = new JButton ( "uGroove Prompt" ); //replace with uGroovePromptIcon
		uGroovePromptButton = new JButton ( uGroovePromptIcon ); 
		uGroovePromptButton.setMargin(new Insets(0,0,0,0));
		//	uGroovePromptButton.setForeground(Color.WHITE);
		//	uGrooveDisplayButton = new JButton ( "uGroove Display" ); //replace with uGrooveDisplayIcon
		uGrooveDisplayButton = new JButton ( uGrooveDisplayIcon);
		uGrooveDisplayButton.setMargin(new Insets(0,0,0,0));
		//	uGrooveDisplayButton.setForeground(Color.WHITE);

		//Need to make a "blank" button for the grid layout to be happy
		blankButton = new JLabel( "" );
		blankButton2 = new JLabel( "" );

		discussButton.setBorderPainted(false);
		discussPromptButton.setBorderPainted(false);
		discussDisplayButton.setBorderPainted(false);
		cloudButton.setBorderPainted(false);
		cloudPromptButton.setBorderPainted(false);
		cloudDisplayButton.setBorderPainted(false);
		pollButton.setBorderPainted(false);
		pollPromptButton.setBorderPainted(false);
		pollDisplayButton.setBorderPainted(false);
		mosaicButton.setBorderPainted(false);
		soundDisplayButton.setBorderPainted(false);
		swarmButton.setBorderPainted(false);
		pointDisplayButton.setBorderPainted(false);
		uGrooveButton.setBorderPainted(false);
		uGroovePromptButton.setBorderPainted(false);
		uGrooveDisplayButton.setBorderPainted(false);

		discussButton.addActionListener( this );
		discussButton.setPressedIcon(new ImageIcon(discussImgOn));
		discussPromptButton.addActionListener( this );
		discussDisplayButton.addActionListener( this );


		cloudButton.addActionListener( this );
		cloudButton.setPressedIcon(new ImageIcon(cloudImgOn));
		cloudPromptButton.addActionListener( this );
		cloudDisplayButton.addActionListener( this );

		pollButton.addActionListener( this );
		pollButton.setPressedIcon(new ImageIcon(pollImgOn));
		pollPromptButton.addActionListener( this );
		pollDisplayButton.addActionListener( this );

		mosaicButton.addActionListener( this );
		mosaicButton.setPressedIcon(new ImageIcon(soundImgOn));
		soundDisplayButton.addActionListener( this );

		swarmButton.addActionListener( this );
		swarmButton.setPressedIcon(new ImageIcon(pointImgOn));
		pointDisplayButton.addActionListener( this );

		uGrooveButton.addActionListener( this );
		uGrooveButton.setPressedIcon(new ImageIcon(uGrooveImgOn)); //UN COMMENT ME WHEN WE GET THE uGROOVE FILES
		uGroovePromptButton.addActionListener( this );
		uGrooveDisplayButton.addActionListener( this );

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
		butPanel.add( soundDisplayButton, buttonGridLayout);
		butPanel.add( swarmButton, buttonGridLayout );
		butPanel.add( blankButton2, buttonGridLayout );
		butPanel.add( pointDisplayButton, buttonGridLayout );
		butPanel.add( uGrooveButton, buttonGridLayout );
		butPanel.add( uGroovePromptButton, buttonGridLayout );
		butPanel.add( uGrooveDisplayButton, buttonGridLayout );


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
		grain = instructorControlPanelSand.getGrain();
		grain.print(); //prints grain data to console
		String input = new String(grain.bArray);

		if (input.equals("DISABLE_DISCUSS_BUTTON")) {
			NGlobals.cPrint(NAppID.INSTRUCTOR_PANEL + "DISABLE_DISCUSS_BUTTON");
			discussButton.setIcon(discussIcon);
			discussOnOff = 0;
		}
		else if( input.equals("ENABLE_DISCUSS_BUTTON")) {
			NGlobals.cPrint(NAppID.INSTRUCTOR_PANEL + "ENABLE_DISCUSS_BUTTON");
			discussButton.setIcon(discussIconOn);
			discussOnOff = 1;
		}	
		else if (input.equals("DISABLE_CLOUD_BUTTON")) {
			NGlobals.cPrint(NAppID.INSTRUCTOR_PANEL + "DISABLE_CLOUD_BUTTON");
			cloudButton.setIcon(cloudIcon);
			cloudOnOff = 0;
		}
		else if ( input.equals("ENABLE_CLOUD_BUTTON")) {
			NGlobals.cPrint(NAppID.INSTRUCTOR_PANEL + "ENABLE_CLOUD_BUTTON");
			cloudButton.setIcon(cloudIconOn);
			cloudOnOff = 1;
		}
		else if (input.equals("DISABLE_POLL_BUTTON")) {
			NGlobals.cPrint(NAppID.INSTRUCTOR_PANEL + "DISABLE_POLL_BUTTON");			
			pollButton.setIcon(pollIcon);
			pollOnOff = 0;
		}
		else if ( input.equals("ENABLE_POLL_BUTTON")) {
			NGlobals.cPrint(NAppID.INSTRUCTOR_PANEL + "ENABLE_POLL_BUTTON");			
			pollButton.setIcon(pollIconOn);
			pollOnOff = 1;
		}
		else if (input.equals("DISABLE_SOUND_BUTTON")) {
			NGlobals.cPrint(NAppID.INSTRUCTOR_PANEL + "DISABLE_SOUND_BUTTON");			
			mosaicButton.setIcon(mosaicIcon);
			mosaicOnOff = 0;
		}
		else if ( input.equals("ENABLE_SOUND_BUTTON")) {
			NGlobals.cPrint(NAppID.INSTRUCTOR_PANEL + "ENABLE_SOUND_BUTTON");			
			mosaicButton.setIcon(mosaicIconOn);
			mosaicOnOff = 1;
		}
		else if (input.equals("DISABLE_POINTER_BUTTON")) {
			NGlobals.cPrint(NAppID.INSTRUCTOR_PANEL + "DISABLE_POINTER_BUTTON");	
			swarmButton.setIcon(swarmIcon);
			swarmOnOff = 0;
		}
		else if ( input.equals("ENABLE_POINTER_BUTTON")) {
			NGlobals.cPrint(NAppID.INSTRUCTOR_PANEL + "ENABLE_POINTER_BUTTON");
			swarmButton.setIcon(swarmIconOn);
			swarmOnOff = 1;
		}
		else if (input.equals("DISABLE_UGROOVE_BUTTON")) {
			NGlobals.cPrint(NAppID.INSTRUCTOR_PANEL + "DISABLE_UGROOVE_BUTTON");
			uGrooveButton.setIcon(uGrooveIcon);
			uGrooveOnOff = 0;
		}
		else if ( input.equals("ENABLE_UGROOVE_BUTTON")) {
			NGlobals.cPrint(NAppID.INSTRUCTOR_PANEL + "ENABLE_UGROOVE_BUTTON");
			uGrooveButton.setIcon(uGrooveIconOn);
			uGrooveOnOff = 1;
		}
	}

	public void open() {
		sendMessage("KHAN");
	}

	//-------------------------------------------------------------------------------
	public void actionPerformed(ActionEvent ae) {
		byte tByte[] = new byte[1];

		Object source = ae.getSource( );

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

		else if( source == swarmButton ) {	
			if ( swarmOnOff == 0) {
				swarmButton.setIcon(swarmIconOn);
				swarmOnOff = 1;
			}      
			else if ( swarmOnOff == 1) {
				swarmButton.setIcon(swarmIcon);
				swarmOnOff = 0;
			}
			tByte[0] = (byte)swarmOnOff;
			NGlobals.cPrint(NAppID.INSTRUCTOR_PANEL + "SET_SWARM_STATUS: " + swarmOnOff);
			instructorControlPanelSand.sendGrain(NAppID.INSTRUCTOR_PANEL,
					NCommand.SET_SWARM_STATUS,
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

		else if( source == discussDisplayButton ) {
			getAppletContext().showDocument(discussURL,"CloudPrompt"); 	
		}

		else if( source == cloudDisplayButton ) {
			getAppletContext().showDocument(cloudURL,"CloudDisplay");
		}	

		else if( source == pollPromptButton ) {
			getAppletContext().showDocument(pollURL,"PollPrompt"); 	
		}	

		else if( source == pollDisplayButton ) {
			getAppletContext().showDocument(pollDisplayURL,"PollDisplay"); 	
		}

		else if( source == discussPromptButton ) {
			getAppletContext().showDocument(discussPromptURL,"DiscussPrompt"); 	
		}

		else if( source == cloudPromptButton ) {
			getAppletContext().showDocument(cloudPromptURL,"CloudPrompt"); 	
		}

		else if( source == soundDisplayButton ) {
			getAppletContext().showDocument(soundDisplayURL,"Sound Mosaic"); 	
		}

		else if( source == pointDisplayButton ) {
			getAppletContext().showDocument(soundSwarmDisplayURL,"Sound Swarm"); 	
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
