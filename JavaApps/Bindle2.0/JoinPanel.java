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

	int i;
	
	boolean c = false; //flag to see if it is connected to server
	int wait;
	
	public void init( ) {
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

// handle method from GroupDiscuss
 /*
  public void handle(byte bite, String text) {
	  int incCmd, incNBlocks, incDType, incDLen;
		int i,j;
		int incIntData[] = new int[1000];
		byte incByteData[] = new byte[1000];  // Cast as chars here because we're using chars -> strings
		NGrain grain;

		NGlobals.cPrint("DiscussClient -> handle()");

		grain = joinSand.getGrain();
		grain.print(); //prints grain data to console
		String msg = new String(grain.bArray);

		if (grain.appID == NAppID.DISCUSS_PROMPT) {
			topic.setText(msg);
			tempString = new String(msg);
			topic.setForeground(Color.BLACK);
			topicFont = new Font("TimesRoman", Font.PLAIN, 20);
		}
		// Disable discuss when the student panel button is off
		else if (grain.appID == NAppID.INSTRUCTOR_PANEL) {
			if (msg.equals("DISABLE_DISCUSS_BUTTON")) {
				speak.setEnabled(false);
				topic.setText("Discuss Disabled");
				chatWindow.setText("");
			}
			else if (msg.equals("ENABLE_DISCUSS_BUTTON")) {
				speak.setEnabled(true);
				topic.setText(msg);
			}			
		}
		else if (grain.appID == NAppID.WEB_CHAT || grain.appID == NAppID.SERVER){
			chatWindow.append(msg + "\n");
			login.requestFocus();
		}
		else {
			grain = null;
		}
		if (grain != null)
			grain = null;
		
	}
*/


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

			// joinSand.sendGrain((byte)NAppID.LOGIN, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.BYTE, tLen, tStringAsBytes );


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

			// joinSand.sendGrain((byte)NAppID.LOGIN, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.BYTE, tLen, tStringAsBytes );


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
