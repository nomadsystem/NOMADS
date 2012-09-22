/*
  NOMADS JoinPanel v.210
  Revised, 6/19/2012, Paul Turowski
  - Integrate NOMADSApp class
*/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.*;
import java.io.*;
import netscape.javascript.*;
import nomads.v210.*;

public class JoinPanel extends JApplet implements ActionListener {

 	
    String userID, tUserID, tLogin;
    JButton submit;
    JLabel usernameLabel, empty;
    JTextField login;
    Color bgColor;
    NSand mySand;

    int i;
	
    boolean c = false; //flag to see if it is connected to server
    int wait;
	
    public void init(NSand inSand) {
	mySand = inSand;
	Color bgColor = new Color(233, 158, 37);
	Container content = getContentPane();
	content.setBackground(bgColor);
	i = 0;
	setLayout( new GridLayout( 2, 2) );
	usernameLabel = new JLabel( "        Username:");
	empty = new JLabel();
    	submit = new JButton( "Submit" );
    	submit.addActionListener( this );
	login = new JTextField(15);
	login.addActionListener( this );
	add( usernameLabel);
	add( login);
	add(empty);
	add( submit);

    }

    // handle does nothing right now
    // TODO change Login GUI to have a message display showing connection status and login retry

    public void handle(NSand inSand) {

      NGlobals.cPrint("Login -> handle()");

      NGrain grain = inSand.getGrain();
      grain.print(); //prints grain data to console
      if (grain != null)
	  grain = null;
		
    }

    ////////////////////////////////////////////////////////////////////////////
    //key listener code
    ///////////////////////////////////////////////////////////////////////////


    public void keyPressed (KeyEvent e) {
	if (e.getKeyCode() == 10) {// enter key

	    NGlobals.cPrint("ENTER");

	    String tString = login.getText();
	    int tLen = tString.length();
	    //    char[] tStringAsChars = tString.toCharArray();
	    byte[] tStringAsBytes = tString.getBytes();

	    mySand.sendGrain((byte)NAppID.BINDLE, (byte)NCommand.LOGIN, (byte)NDataType.CHAR, tLen, tStringAsBytes );

	    // The data 
	    NGlobals.cPrint("sending:  (" + tLen + ") of this data type");

	    //                for (int i=0; i<tLen; i++) {
	    //                NGlobals.cPrint("sending:  " + tString.charAt(i));
	    //                streamOut.writeByte(tString.charAt(i));
	    //                }

	    NGlobals.cPrint("sending: (" + tString + ")");
	    login.setText("");

	}
    }

    //makes compiler happy
    public void keyReleased(KeyEvent e){
    }

    public void keyTyped(KeyEvent e){
    }

    ////////////////////////////////////////////////////////////////////////////
    //action listener code
    ///////////////////////////////////////////////////////////////////////////  
    public void actionPerformed(java.awt.event.ActionEvent ae) {
	Object source = ae.getSource();

	//listener code for speak button
	if (source == submit) {
	    NGlobals.cPrint("pressed speak button");
	    String tString = login.getText();
	    int tLen = tString.length();
	    //    char[] tStringAsChars = tString.toCharArray();
	    byte[] tStringAsBytes = tString.getBytes();

	    mySand.sendGrain((byte)NAppID.BINDLE, (byte)NCommand.LOGIN, (byte)NDataType.CHAR, tLen, tStringAsBytes );

	    // The data 
	    NGlobals.cPrint("sending:  (" + tLen + ") of this data type");

	    //                for (int i=0; i<tLen; i++) {
	    //                NGlobals.cPrint("sending:  " + tString.charAt(i));
	    //                streamOut.writeByte(tString.charAt(i));
	    //                }

	    NGlobals.cPrint("sending: (" + tString + ")");
	    login.setText("");

	} 
    }

}
