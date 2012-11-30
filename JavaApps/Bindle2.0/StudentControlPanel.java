import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import nomads.v210.*;
import com.softsynth.jsyn.*;


public class StudentControlPanel extends JApplet  implements  ActionListener {


    NSand studentControlPanelSand;
    private NomadsAppThread nThread;
    public String userName;

    JButton joinButton, discussButton, cloudButton, soundButton, pollButton, pointButton, uGrooveButton;

    Image joinImg, discussImg, cloudImg, soundImg, pollImg, pointImg, logoImg, uGrooveImg;
    Image joinImgOn, discussImgOn, cloudImgOn, soundImgOn, pollImgOn, pointImgOn, uGrooveImgOn; 
    Image joinImgFocus, discussImgFocus, cloudImgFocus, soundImgFocus, pollImgFocus, pointImgFocus, uGrooveImgFocus;
    Image discussImgOff, cloudImgOff, soundImgOff, pollImgOff, pointImgOff, uGrooveImgOff;

    ImageIcon joinIcon, discussIcon, cloudIcon, soundIcon, pollIcon, pointIcon, icon, uGrooveIcon;

    JPanel butPanel, logoPanel;
    JLabel imageLabel;

    JoinPanel myJoinPanel;
    DiscussClientPanel myDiscussClientPanel;
    CloudDiscussPanel myCloudDiscussPanel;
    PollStudentPanel myPollStudentPanel;
    SoundMosaicPanel mySoundMosaicPanel;
    SandPointerPanel mySandPointerPanel;
    UnityGroovePanel myUnityGroovePanel;
    public Boolean joined = false;

    JFrame joinFrame = null;
    JFrame discussFrame = null;
    JFrame cloudFrame = null;
    JFrame pollFrame = null;
    JFrame soundMosaicFrame = null;
    JFrame sandPointerFrame = null;
    JFrame uGrooveFrame = null;

    public Graphics g; 
    // URL joinURL;
    // URL discussURL;
    // URL cloudURL;
    // URL soundURL;
    // URL pollURL;
    // URL pointURL;
    // URL uGrooveURL;

    int soundOn = 0;

    private class NomadsAppThread extends Thread {
	StudentControlPanel client; //Replace with current class name

	public NomadsAppThread(StudentControlPanel _client) {
	    client = _client;
	}

	public void run() {			
	    NGlobals.lPrint("Student Control Panel Thread-> run()");
	    while (true)  {
		client.handle();
	    }
	}
    }

    public void init( ) {
	setLayout(new BorderLayout( ) );
	setupButtons( );      
	icon = new ImageIcon( );
	imageLabel=new JLabel( icon, JLabel.CENTER);
	add( imageLabel, BorderLayout.CENTER ); 
	logoImg=getImage(getCodeBase( ),"NOMADLogo_roundcorners.png");
	setupImage( logoImg );
	Container content = getContentPane();
	content.setBackground(Color.black);

	// Connect
	studentControlPanelSand = new NSand();
	studentControlPanelSand.connect();
	byte d[] = new byte[1];
	d[0] = 1;
	studentControlPanelSand.sendGrain((byte)NAppID.BINDLE, (byte)NCommand.REGISTER, (byte)NDataType.UINT8, 1, d );

	NGlobals.lPrint("Student Control Panel Thread -> connected");

	//FRAME STUFF for pop up windows===========================================================
	//Group Discuss Frame

	myJoinPanel = new JoinPanel();
	myJoinPanel.parent = this;
	myJoinPanel.init(studentControlPanelSand);
	joinFrame = new JFrame("Join");
	//Could add window listener here if we wanted to
	joinFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	joinFrame.setLocationRelativeTo(null);
	joinFrame.getContentPane().add(myJoinPanel);
	joinFrame.pack();
	//    joinFrame.setVisible(true);

	myDiscussClientPanel = new DiscussClientPanel();
	myDiscussClientPanel.parent = this;
	myDiscussClientPanel.init(studentControlPanelSand);
	discussFrame = new JFrame("Group Discuss");
	//Could add window listener here if we wanted to
	discussFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	discussFrame.setLocationRelativeTo(null);
	discussFrame.setPreferredSize(new Dimension(800,600));
	discussFrame.getContentPane().add(myDiscussClientPanel);
	discussFrame.pack();
	//    discussFrame.setVisible(true);

	//Thought Cloud Frame
	myCloudDiscussPanel = new CloudDiscussPanel();
	myCloudDiscussPanel.init(studentControlPanelSand);
	cloudFrame = new JFrame("Thought Cloud");
	//Could add window listener here if we wanted to
	cloudFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	cloudFrame.setLocationRelativeTo(null);
	cloudFrame.setPreferredSize(new Dimension(600,200));
	cloudFrame.getContentPane().add(myCloudDiscussPanel);
	cloudFrame.pack();

	//Poll Frame
	myPollStudentPanel = new PollStudentPanel();
	myPollStudentPanel.init(studentControlPanelSand);
	pollFrame = new JFrame("Poll Aura");
	//Could add window listener here if we wanted to
	pollFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	pollFrame.setLocationRelativeTo(null);
	pollFrame.setPreferredSize(new Dimension(500, 250));
	pollFrame.getContentPane().add(myPollStudentPanel);
	pollFrame.pack();

	//SoundMosaic Frame
	mySoundMosaicPanel = new SoundMosaicPanel();
	mySoundMosaicPanel.init(studentControlPanelSand);
	soundMosaicFrame = new JFrame("Sound Mosaic");
	//Could add window listener here if we wanted to
	soundMosaicFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	soundMosaicFrame.setLocationRelativeTo(null);
	soundMosaicFrame.setPreferredSize(new Dimension(1050, 700));
	soundMosaicFrame.getContentPane().add(mySoundMosaicPanel);
	soundMosaicFrame.pack();

	//SandPointer Frame
	mySandPointerPanel = new SandPointerPanel();
	mySandPointerPanel.init(studentControlPanelSand);
	sandPointerFrame = new JFrame("Sand Pointer");
	//Could add window listener here if we wanted to
	sandPointerFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	sandPointerFrame.setLocationRelativeTo(null);
	sandPointerFrame.setPreferredSize(new Dimension(600, 600));
	sandPointerFrame.getContentPane().add(mySandPointerPanel);
	sandPointerFrame.pack();

	//UGroove Frame
	myUnityGroovePanel = new UnityGroovePanel();
	myUnityGroovePanel.init(studentControlPanelSand);
	uGrooveFrame = new JFrame("Unity Groove");
	//Could add window listener here if we wanted to
	uGrooveFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	uGrooveFrame.setLocationRelativeTo(null);
	uGrooveFrame.setPreferredSize(new Dimension(500, 500));
	//uGrooveFrame.getContentPane().add(myUnityGroovePanel);
	uGrooveFrame.getContentPane().add(myUnityGroovePanel);
	uGrooveFrame.pack();

	// try {
	// 	joinURL = new URL(user + "Join");
	// 	discussURL = new URL(user + "GroupDiscuss");
	// 	cloudURL = new URL(user +  "CloudDiscuss");   
	// 	pollURL = new URL(user + "PollStudent");       
	// 	soundURL = new URL(user + "SoundMosaic");
	// 	pointURL = new URL(user +  "SandPointer");
	// 	uGrooveURL = new URL(user + "UnityGrooveStudent");
	// }

	// catch (MalformedURLException e) {
	// }

	//Code below starts thread (connects), sends register byte
	nThread = new NomadsAppThread(this);
	nThread.start();
    }


    public void setupButtons() {
	//***** STK Need to update the rest of these buttons 1/18/10
	joinImg=getImage(getCodeBase( ),"joinButton.png");
	joinImgOn=getImage(getCodeBase( ),"joinButtonOn.png");
	joinImgFocus=getImage(getCodeBase( ),"joinButtonMouse.png");

	discussImg=getImage(getCodeBase( ),"groupDiscussButton.png");
	discussImgOn=getImage(getCodeBase( ),"groupDiscussButtonOn.png");
	discussImgFocus=getImage(getCodeBase( ),"groupDiscussButtonMouse.png");
	discussImgOff=getImage(getCodeBase( ),"groupDiscussButtonBlurOff.png");


	cloudImg=getImage(getCodeBase( ),"thoughtCloudButton.png");
	cloudImgOn=getImage(getCodeBase( ),"thoughtCloudButtonOn.png");
	cloudImgFocus=getImage(getCodeBase( ),"thoughtCloudButtonMouse.png");
	cloudImgOff=getImage(getCodeBase( ),"thoughtCloudButtonBlurOff.png");

	pollImg=getImage(getCodeBase( ),"pollAuraButton.png");
	pollImgOn=getImage(getCodeBase( ),"pollAuraButtonOn.png");
	pollImgFocus=getImage(getCodeBase( ),"pollAuraButtonMouse.png");
	pollImgOff=getImage(getCodeBase( ),"pollAuraButtonBlurOff.png");



	soundImg=getImage(getCodeBase( ),"soundMosaicButton.png");
	soundImgOn=getImage(getCodeBase( ),"soundMosaicButtonOn.png");
	soundImgFocus=getImage(getCodeBase( ),"soundMosaicButtonMouse.png");
	soundImgOff=getImage(getCodeBase( ),"soundMosaicButtonBlurOff.png");


	pointImg=getImage(getCodeBase( ),"sandPointerButton.png");
	pointImgOn=getImage(getCodeBase( ),"sandPointerButtonOn.png");
	pointImgFocus=getImage(getCodeBase( ),"sandPointerButtonMouse.png");
	pointImgOff=getImage(getCodeBase( ),"sandPointerButtonBlurOff.png");

	uGrooveImg=getImage(getCodeBase( ),"unityGrooveButton.png");
	uGrooveImgOn=getImage(getCodeBase( ),"unityGrooveButtonOn.png");
	uGrooveImgFocus=getImage(getCodeBase( )," unityGrooveButtonMouse.png");
	uGrooveImgOff=getImage(getCodeBase( ),"unityGrooveButtonBlur.png");


	joinIcon = new ImageIcon(joinImg);
	discussIcon = new ImageIcon(discussImgOff);
	cloudIcon = new ImageIcon(cloudImgOff);
	pollIcon = new ImageIcon(pollImgOff);
	soundIcon = new ImageIcon(soundImgOff); 
	pointIcon = new ImageIcon(pointImgOff); 
	uGrooveIcon = new ImageIcon(uGrooveImgOff);

	joinButton = new JButton( joinIcon );
	discussButton = new JButton( discussIcon );
	cloudButton = new JButton( cloudIcon );
	pollButton = new JButton( pollIcon );
	soundButton = new JButton( soundIcon );
	pointButton = new JButton( pointIcon );
	uGrooveButton = new JButton( uGrooveIcon );

	joinButton.setBorderPainted(false);
	discussButton.setBorderPainted(false);
	cloudButton.setBorderPainted(false);
	pollButton.setBorderPainted(false);
	soundButton.setBorderPainted(false);
	pointButton.setBorderPainted(false);
	uGrooveButton.setBorderPainted(false);

	joinButton.addActionListener( this );
	joinButton.setRolloverIcon(new ImageIcon(joinImgFocus));
	joinButton.setPressedIcon(new ImageIcon(joinImgOn));

	// discussButton.addActionListener( this );
	discussButton.setRolloverIcon(new ImageIcon(discussImgFocus));
	discussButton.setPressedIcon(new ImageIcon(discussImgOn));

	// cloudButton.addActionListener( this );
	cloudButton.setRolloverIcon(new ImageIcon(cloudImgFocus));
	cloudButton.setPressedIcon(new ImageIcon(cloudImgOn));

	// pollButton.addActionListener( this );
	pollButton.setRolloverIcon(new ImageIcon(pollImgFocus));
	pollButton.setPressedIcon(new ImageIcon(pollImgOn));

	// soundButton.addActionListener( this );
	soundButton.setRolloverIcon(new ImageIcon(soundImgFocus));
	soundButton.setPressedIcon(new ImageIcon(soundImgOn));

	// pointButton.addActionListener( this );
	pointButton.setRolloverIcon(new ImageIcon(pointImgFocus));
	pointButton.setPressedIcon(new ImageIcon(pointImgOn));

	// uGrooveButton.addActionListener( this );
	uGrooveButton.setRolloverIcon(new ImageIcon(uGrooveImgFocus));
	uGrooveButton.setPressedIcon(new ImageIcon(uGrooveImgOn));

	butPanel=new JPanel(new GridLayout(7,1));
	butPanel.setBackground(Color.black);
	butPanel.add( joinButton );
	butPanel.add( discussButton );
	butPanel.add( cloudButton );
	butPanel.add( pollButton );
	butPanel.add( soundButton );
	butPanel.add( pointButton );
	butPanel.add( uGrooveButton );
	add( butPanel, BorderLayout.WEST );
    }

    public void setupImage( Image img )
    {
	icon.setImage( img );
	imageLabel.setIcon( icon );
	repaint( );
    }    

    // HANDLE---------------------------------------------------------------    
    public void handle() {
	NGlobals.cPrint("StudentControlPanel -> handle()");

	NGrain grain;

	grain = studentControlPanelSand.getGrain();

	if (joined == false) {
	    return;
	}

	// grain.print(); //prints grain data to console

	byte incAppID = grain.appID;
	byte tByte = 0; // = new byte[grain.dataLen]; //****STK DT, why are you taking the data length?
	byte tCmd = grain.command;
	byte tAppDataType = grain.dataType;
	int tDataLen = grain.dataLen;

	if ((grain.dataType == NDataType.CHAR || grain.dataType == NDataType.UINT8) && (grain.dataLen > 0)){
	    tByte = grain.bArray[0];
	    String input = new String(grain.bArray);
	}

	// Set various BUTTON states as sent by the SERVER

	if (incAppID == NAppID.SERVER) {

	    if (tCmd == NCommand.SET_DISCUSS_STATUS) {
		if (tByte == 0) {
		    discussButton.removeActionListener (this);
		    discussButton.setIcon(new ImageIcon(discussImgOff));
		    NGlobals.cPrint("discuss DISABLED");
		}
		else if (tByte == 1) {
		    discussButton.addActionListener (this);
		    discussButton.setIcon(new ImageIcon(discussImg));
		    NGlobals.cPrint("discuss ENABLED");
		}
	    }

	    else if (tCmd == NCommand.SET_CLOUD_STATUS) {
		if (tByte == 0) {
		    cloudButton.removeActionListener (this);
		    cloudButton.setIcon(new ImageIcon(cloudImgOff));
		    NGlobals.cPrint("cloud DISABLED");
		}
		else if (tByte == 1) {
		    cloudButton.addActionListener (this);
		    cloudButton.setIcon(new ImageIcon(cloudImg));
		    NGlobals.cPrint("cloud ENABLED");
		}
	    }

	    else if (tCmd == NCommand.SET_POLL_STATUS) {
		if (tByte == 0) {
		    pollButton.removeActionListener (this);
		    pollButton.setIcon(new ImageIcon(pollImgOff));
		    NGlobals.cPrint("poll DISABLED");
		}
		else if (tByte == 1) {
		    pollButton.addActionListener (this);
		    pollButton.setIcon(new ImageIcon(pollImg));
		    NGlobals.cPrint("poll ENABLED");
		}
	    }
	    else if (tCmd == NCommand.SET_MOSAIC_STATUS) {
		if (tByte == 0) {
		    soundButton.removeActionListener (this);
		    soundButton.setIcon(new ImageIcon(soundImgOff));
		    NGlobals.cPrint("sound mosaic DISABLED");
		}
		else if (tByte == 1) {
		    soundButton.addActionListener (this);
		    soundButton.setIcon(new ImageIcon(soundImg));
		    NGlobals.cPrint("sound mosaic ENABLED");
		}
	    }

	    else if (tCmd == NCommand.SET_SWARM_STATUS) {
		if (tByte == 0) {
		    pointButton.removeActionListener (this);
		    pointButton.setIcon(new ImageIcon(pointImgOff));
		    NGlobals.cPrint("sand pointer DISABLED");
		}
		else if (tByte == 1) {
		    pointButton.addActionListener (this);
		    pointButton.setIcon(new ImageIcon(pointImg));
		    NGlobals.cPrint("sand pointer ENABLED");
		}
	    }

	    else if (tCmd == NCommand.SET_UGROOVE_STATUS) {
		if (tByte == 0) {
		    uGrooveButton.removeActionListener (this);
		    uGrooveButton.setIcon(new ImageIcon(uGrooveImgOff));
		    NGlobals.cPrint("UGroove DISABLED");
		}
		else if (tByte == 1) {
		    uGrooveButton.addActionListener (this);
		    uGrooveButton.setIcon(new ImageIcon(uGrooveImg));
		    NGlobals.cPrint("UGroove ENABLED");
		}
	    }
	}  // end BUTTON states from SERVER

	// Set various BUTTON states as sent by the INSTRUCTOR PANEL

	if (incAppID == NAppID.INSTRUCTOR_PANEL) { 
	    if (tCmd == NCommand.SET_DISCUSS_STATUS) {
		if (tByte == 0) {
		    discussButton.removeActionListener (this);
		    discussButton.setIcon(new ImageIcon(discussImgOff));
		    NGlobals.cPrint("discuss DISABLED");
		}
		else if (tByte == 1) {
		    discussButton.addActionListener (this);
		    discussButton.setIcon(new ImageIcon(discussImg));
		    NGlobals.cPrint("discuss ENABLED");
		}
	    }

	    else if (tCmd == NCommand.SET_CLOUD_STATUS) {
		if (tByte == 0) {
		    cloudButton.removeActionListener (this);
		    cloudButton.setIcon(new ImageIcon(cloudImgOff));
		    NGlobals.cPrint("cloud DISABLED");
		}
		else if (tByte == 1) {
		    cloudButton.addActionListener (this);
		    cloudButton.setIcon(new ImageIcon(cloudImg));
		    NGlobals.cPrint("cloud ENABLED");
		}
	    }

	    else if (tCmd == NCommand.SET_POLL_STATUS) {
		if (tByte == 0) {
		    pollButton.removeActionListener (this);
		    pollButton.setIcon(new ImageIcon(pollImgOff));
		    NGlobals.cPrint("poll DISABLED");
		}
		else if (tByte == 1) {
		    pollButton.addActionListener (this);
		    pollButton.setIcon(new ImageIcon(pollImg));
		    NGlobals.cPrint("poll ENABLED");
		}
	    }
	    else if (tCmd == NCommand.SET_MOSAIC_STATUS) {
		if (tByte == 0) {
		    soundButton.removeActionListener (this);
		    soundButton.setIcon(new ImageIcon(soundImgOff));
		    NGlobals.cPrint("sound mosaic DISABLED");
		}
		else if (tByte == 1) {
		    soundButton.addActionListener (this);
		    soundButton.setIcon(new ImageIcon(soundImg));
		    NGlobals.cPrint("sound mosaic ENABLED");
		}
	    }

	    else if (tCmd == NCommand.SET_SWARM_STATUS) {
		if (tByte == 0) {
		    pointButton.removeActionListener (this);
		    pointButton.setIcon(new ImageIcon(pointImgOff));
		    NGlobals.cPrint("sand pointer DISABLED");
		}
		else if (tByte == 1) {
		    pointButton.addActionListener (this);
		    pointButton.setIcon(new ImageIcon(pointImg));
		    NGlobals.cPrint("sand pointer ENABLED");
		}
	    }

	    else if (tCmd == NCommand.SET_UGROOVE_STATUS) {
		if (tByte == 0) {
		    uGrooveButton.removeActionListener (this);
		    uGrooveButton.setIcon(new ImageIcon(uGrooveImgOff));
		    NGlobals.cPrint("UGroove DISABLED");
		}
		else if (tByte == 1) {
		    uGrooveButton.addActionListener (this);
		    uGrooveButton.setIcon(new ImageIcon(uGrooveImg));
		    NGlobals.cPrint("UGroove ENABLED");
		}
	    }
	}  // end SET BUTTON states from INSTRUCTOR PANEL

	// Send to DISCUSS ----------------------
	if (incAppID == NAppID.INSTRUCTOR_PANEL || incAppID == NAppID.DISCUSS || incAppID == NAppID.INSTRUCTOR_DISCUSS || incAppID == NAppID.DISCUSS_PROMPT) {
	    myDiscussClientPanel.handle(grain);
	}

	// Send to CLOUD DISCUSS ----------------------
	if (incAppID == NAppID.INSTRUCTOR_PANEL || incAppID == NAppID.CLOUD_PROMPT) {
	    myCloudDiscussPanel.handle(grain);
	}

	// Send to POLL ----------------------
	if (incAppID == NAppID.INSTRUCTOR_PANEL || incAppID == NAppID.TEACHER_POLL) {
	    myPollStudentPanel.handle(grain);
	}

	// Send to SEQUENCER/MOSAIC ----------------------
	if (incAppID == NAppID.INSTRUCTOR_PANEL || incAppID == NAppID.INSTRUCTOR_SEQUENCER) {
	    //	mySoundMosaicPanel.handle(grain);
	}

	// Send to SWARM/POINTER ---------------------
	if (incAppID == NAppID.INSTRUCTOR_PANEL) {
	    //	mySandPointerPanel.handle(grain);
	}

	// Send to UNITY GROOVE ----------------------
	if (incAppID == NAppID.INSTRUCTOR_PANEL || incAppID == NAppID.INSTRUCT_EMRG_SYNTH_PROMPT) {
	    myUnityGroovePanel.handle(grain);
	}
    }

    public void actionPerformed(ActionEvent ae)
    {
	Object source = ae.getSource( );

	if( source == joinButton )
	    {
		joinFrame.setVisible(true);
		//			getAppletContext().showDocument(discussURL,"Discuss"); 
		setupImage( joinImg );

		//	discussFrame.setVisible(true);
		//			CustomDialog myDialog = new CustomDialog(discussFrame, true, "Do you like Java?");
		//            System.err.println("After opening dialog.");
		//            if(myDialog.getAnswer()) {
		//                System.err.println("The answer stored in CustomDialog is 'true' (i.e. user clicked yes button.)");
		//            }
		//            else {
		//                System.err.println("The answer stored in CustomDialog is 'false' (i.e. user clicked no button.)");
		//            }
		//			getAppletContext().showDocument(joinURL,"Join"); 
		//			setupImage( joinImg );
	    }	
	else if( source == discussButton )
	    {
		myDiscussClientPanel.myUserName = userName;
		discussFrame.setVisible(true);
		//			getAppletContext().showDocument(discussURL,"Discuss"); 
		setupImage( discussImg );
	    }
	else if( source == cloudButton ) 
	    {
		cloudFrame.setVisible(true);
		//			getAppletContext().showDocument(cloudURL,"Cloud"); 
		setupImage( cloudImg );
	    }

	else if( source == pollButton )
	    {
		pollFrame.setVisible(true);
		//			getAppletContext().showDocument(pollURL,"Poll"); 
		setupImage( pollImg );
	    }
	else if( (source == soundButton ) && (soundOn == 0))
	    {
		soundOn = 1;
		soundMosaicFrame.setVisible(true);
		setupImage( soundImg );
	    }
	else if( source == pointButton )
	    {
		sandPointerFrame.setVisible(true);
		//			getAppletContext().showDocument(pointURL,"Point"); 
		setupImage( pointImg );
	    }
	else if( source == uGrooveButton )
	    {
		uGrooveFrame.setVisible(true);
		//			getAppletContext().showDocument( uGrooveURL,"Unity Groove"); 
		setupImage( uGrooveImg );
	    }
    }    
}  
