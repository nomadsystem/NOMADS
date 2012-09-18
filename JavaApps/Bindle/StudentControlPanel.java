import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import nomads.v210.*;


public class StudentControlPanel extends JApplet  implements  ActionListener {
	// Change to your directory
	// for production version, just use "/"
	String user = new String("http://nomads.music.virginia.edu/kemper/NOMADS/JavaApps/");
	
	NSand studentControlPanelSand;
	private NomadsAppThread nThread;
	
    JButton joinButton, discussButton, cloudButton, soundButton, pollButton, pointButton, uGrooveButton;
    
    Image joinImg, discussImg, cloudImg, soundImg, pollImg, pointImg, logoImg, uGrooveImg;
    Image joinImgOn, discussImgOn, cloudImgOn, soundImgOn, pollImgOn, pointImgOn, uGrooveImgOn; 
    Image joinImgFocus, discussImgFocus, cloudImgFocus, soundImgFocus, pollImgFocus, pointImgFocus, uGrooveImgFocus;
    Image discussImgOff, cloudImgOff, soundImgOff, pollImgOff, pointImgOff, uGrooveImgOff;
    
    ImageIcon joinIcon, discussIcon, cloudIcon, soundIcon, pollIcon, pointIcon, icon, uGrooveIcon;

    JPanel butPanel, logoPanel;
    JLabel imageLabel;
    public Graphics g; 
    URL joinURL;
    URL discussURL;
    URL cloudURL;
    URL soundURL;
    URL pollURL;
    URL pointURL;
    URL uGrooveURL;

    int soundOn = 0;
    
    private class NomadsAppThread extends Thread {
    	StudentControlPanel client; //Replace with current class name

		public NomadsAppThread(StudentControlPanel _client) {
			client = _client;
			// Connect
			studentControlPanelSand = new NSand();
			studentControlPanelSand.connect();
			NGlobals.lPrint("Student Control Panel Thread -> connected");
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

        try {
    	    joinURL = new URL(user + "Join");
            discussURL = new URL(user + "GroupDiscuss");
            cloudURL = new URL(user +  "CloudDiscuss");   
			pollURL = new URL(user + "PollStudent");       
           	soundURL = new URL(user + "SoundSwarm");
            pointURL = new URL(user +  "Pointer");
			uGrooveURL = new URL(user + "UnityGrooveStudent");
        }
        
        catch (MalformedURLException e) {
        }
        
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
		discussIcon = new ImageIcon(discussImg);
		cloudIcon = new ImageIcon(cloudImg);
		pollIcon = new ImageIcon(pollImg);
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
		
		discussButton.addActionListener( this );
		discussButton.setRolloverIcon(new ImageIcon(discussImgFocus));
		discussButton.setPressedIcon(new ImageIcon(discussImgOn));

		cloudButton.addActionListener( this );
		cloudButton.setRolloverIcon(new ImageIcon(cloudImgFocus));
		cloudButton.setPressedIcon(new ImageIcon(cloudImgOn));

		pollButton.addActionListener( this );
		pollButton.setRolloverIcon(new ImageIcon(pollImgFocus));
		pollButton.setPressedIcon(new ImageIcon(pollImgOn));
		
		soundButton.addActionListener( this );
		soundButton.setRolloverIcon(new ImageIcon(soundImgFocus));
		soundButton.setPressedIcon(new ImageIcon(soundImgOn));

		pointButton.addActionListener( this );
		pointButton.setRolloverIcon(new ImageIcon(pointImgFocus));
		pointButton.setPressedIcon(new ImageIcon(pointImgOn));
		
		uGrooveButton.addActionListener( this );
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
	
	grain.print(); //prints grain data to console
	
	byte tByte = new byte[grain.dataLen];
	byte tCmd = grain.command;
	String input = new String(grain.bArray);
    	
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

	// DT:  stopped here

	else if (tCmd == NCommand.SET_CLOUD_STATUS) {
	    cloudButton.removeActionListener (this);
	    cloudButton.setIcon(new ImageIcon(cloudImgOff));
	}


	else if (input.equals("ENABLE_CLOUD_BUTTON")) {
	    cloudButton.addActionListener (this);
	    cloudButton.setIcon(new ImageIcon(cloudImg));
	}
	else if (input.equals("DISABLE_POLL_BUTTON")) {
	    pollButton.removeActionListener (this);
	    pollButton.setIcon(new ImageIcon(pollImgOff));
	}
	else if (input.equals("ENABLE_POLL_BUTTON")) {
	    pollButton.addActionListener (this);
	    pollButton.setIcon(new ImageIcon(pollImg));
	}
	else if (input.equals("DISABLE_SOUND_BUTTON")) {
	    soundButton.removeActionListener (this);
	    soundButton.setIcon(new ImageIcon(soundImgOff));
	}
	else if (input.equals("ENABLE_SOUND_BUTTON")) {
	    soundButton.addActionListener (this);
	    soundButton.setIcon(new ImageIcon(soundImg));
	}
	else if (input.equals("DISABLE_POINTER_BUTTON")) {
	    pointButton.removeActionListener (this);
	    pointButton.setIcon(new ImageIcon(pointImgOff));
	}
	else if (input.equals("ENABLE_POINTER_BUTTON")) {
	    pointButton.addActionListener (this);
	    pointButton.setIcon(new ImageIcon(pointImg));
	}
	else if (input.equals("DISABLE_UGROOVE_BUTTON")) {
	    uGrooveButton.removeActionListener (this);
	    uGrooveButton.setIcon(new ImageIcon(uGrooveImgOff));
	}
	else if (input.equals("ENABLE_UGROOVE_BUTTON")) {
	    uGrooveButton.addActionListener (this);
	    uGrooveButton.setIcon(new ImageIcon(uGrooveImg));
	}
    }
    
    public void actionPerformed(ActionEvent ae)
    {
		Object source = ae.getSource( );
		if( source == joinButton )
			{
			getAppletContext().showDocument(joinURL,"Join"); 
			setupImage( joinImg );
			}	
		else if( source == discussButton )
			{
			getAppletContext().showDocument(discussURL,"Discuss"); 
			setupImage( discussImg );
			}
		else if( source == cloudButton ) 
			{
			getAppletContext().showDocument(cloudURL,"Cloud"); 
			setupImage( cloudImg );
			}
		else if( (source == soundButton ) && (soundOn == 0))
			{
			soundOn = 1;
			getAppletContext().showDocument(soundURL,"Sound"); 
			setupImage( soundImg );
			}
		else if( source == pollButton )
			{
			getAppletContext().showDocument(pollURL,"Poll"); 
			setupImage( pollImg );
			}
		else if( source == pointButton )
			{
			getAppletContext().showDocument(pointURL,"Point"); 
			setupImage( pointImg );
			}
		else if( source == uGrooveButton )
			{
			getAppletContext().showDocument( uGrooveURL,"Unity Groove"); 
			setupImage( uGrooveImg );
			}
    }    
}  
