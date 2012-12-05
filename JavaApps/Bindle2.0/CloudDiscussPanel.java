/*
  NOMADS Cloud Discuss2 v.210
  Revised/cleaned, 6/14/2012, Steven Kemper
  Integrating NOMADSApp class
 */


import java.awt.*;
import java.awt.event.*;
import javax.swing.text.DefaultCaret;
import javax.swing.*;
import java.util.*;
import java.lang.*;
import nomads.v210.*;

public class CloudDiscussPanel extends JPanel implements ActionListener, KeyListener {   

	private String lastInput = "";
	//    JTextArea chatWindow;
	JScrollPane spane;
	JButton speak, connect, disconnect;
	JTextField input;
	JPanel panel, panPanel, wholeThing, lTab, rTab;
	JLabel title, topic, spa1, spa2, spa3, spa5;
	String topicString = "";
	Font titleFont, topicFont;
	String tempString = "";

	//background color for the whole applet
	Color BG = new Color(145, 106, 65);

	Color BG2 = new Color(145, 106, 65);

	//background color for chatwindow
	//    Color cWindy = new Color(242,197,126);

	//background color for input text field
	Color inputColor = new Color(249,241,131);

	//color for chat window
	Color chatColor = new Color(0,0,0);

	Font chatFont = new Font("sansserif", Font.BOLD, 18);

	boolean c = false; //flag to see if it is connected to server
	int wait;

	NSand mySand;

	public void resetSand(NSand inSand)
	{ 
		mySand = inSand;
	}

	public void init(NSand inSand)
	{ 
		mySand = inSand;

		//topmost container. It will hold wholeThing, which is the applet
		//		Container content = getContentPane();
		//		content.setBackground(BG);
		setLayout( new BorderLayout() );

		//applet it will be added to the topmost container  
		//this is done for the purposes of color
		wholeThing = new JPanel( new BorderLayout() );
		wholeThing.setBackground(BG);

		//initialize components
		speak = new JButton("Add to the Cloud");
		//	  connect = new JButton("Connect");
		//	  disconnect = new JButton("Disconnect");
		//	  chatWindow = new JTextArea(10,30);
		//makes chat window autoscroll
		//	  DefaultCaret caret = (DefaultCaret)chatWindow.getCaret();
		//	  caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		//	  chatWindow.setEnabled(false);
		//	  chatWindow.setBackground(cWindy);
		//	  chatWindow.setFont(chatFont);
		//	  chatWindow.setDisabledTextColor(chatColor);
		//	  spane = new JScrollPane(chatWindow);
		input = new JTextField("", 30);
		input.setBackground(inputColor);
		panel = new JPanel( new FlowLayout() ); //holds buttons and textfield 
		//      panPanel = new JPanel( new GridLayout(1,1,0,1)); //holds panel for spacing and color purposes...may not be necessary
		//	  panPanel.setBackground(BG);
		panel.setBackground(BG);		  

		titleFont = new Font("TimesRoman", Font.BOLD, 20);
		title = new JLabel("Send your text to the NOMADS cloud", JLabel.CENTER);
		title.setFont(titleFont);
		topicFont = new Font("TimesRoman", Font.BOLD, 20);
		topic = new JLabel("", JLabel.CENTER);
		topic.setFont(topicFont);


		//key listener stuff
		//	  input.setFocusable(true);
		input.addKeyListener(this); 

		//add action listeners to the buttons
		speak.addActionListener(this);


		panel.add(input);
		panel.add(speak);   
		//panPanel.add(spa3);
		//	   panPanel.add(panel);

		wholeThing.add(panel, BorderLayout.SOUTH);
		wholeThing.add(title, BorderLayout.NORTH);
		wholeThing.add(topic, FlowLayout.CENTER);


		add(wholeThing);

	}


	public synchronized void  handle(NGrain inGrain)
	{	
		int i,j;
		NGrain grain;

		NGlobals.cPrint("CloudDiscuss -> handle()");

		grain = inGrain;
		grain.print(); //prints grain data to console
		byte incAppID = grain.appID;
		byte incCmd = grain.command;
		String msg = new String(grain.bArray);

		if (incAppID == NAppID.CLOUD_PROMPT && incCmd == NCommand.SEND_CLOUD_PROMPT) {
			topic.setText(msg);
			tempString = new String(msg);
			topic.setForeground(Color.BLACK);
			topicFont = new Font("TimesRoman", Font.BOLD, 20);
		}
		else if (incAppID == NAppID.INSTRUCTOR_PANEL && incCmd == NCommand.SET_CLOUD_STATUS) {
			if (grain.bArray[0] == 0) {
				speak.setEnabled(false);
				topic.setText("Cloud Disabled");
			}
			else if (grain.bArray[0] == 1) {
				speak.setEnabled(true);
				topic.setText(tempString);
			}
		} 
		else {
			grain = null;
		}
		if (grain != null)
			grain = null;
		input.requestFocus();
	}


	////////////////////////////////////////////////////////////////////////////
	//key listener code
	///////////////////////////////////////////////////////////////////////////
	public void keyPressed (KeyEvent e)
	{
		String tInput;
		if (e.getKeyCode() == 10) // enter key
		{
			NGlobals.cPrint("ENTER");

			tInput = input.getText().trim();

			if (lastInput.equals(tInput)) {
				input.setText("");
				return;
			}
			else {
				lastInput = tInput;
				int tLen = tInput.length();
				if (tLen < 2) 
				    return;
				byte[] tStringAsBytes = tInput.getBytes();

				mySand.sendGrain((byte)NAppID.CLOUD_CHAT, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.BYTE, tLen, tStringAsBytes );


				// The data 
				NGlobals.cPrint("sending:  (" + tLen + ") of this data type");

				//                for (int i=0; i<tLen; i++) {
				//                NGlobals.cPrint("sending:  " + tString.charAt(i));
				//                streamOut.writeByte(tString.charAt(i));
				//                }

				NGlobals.cPrint("sending: (" + tInput + ")");
				input.setText("");
			}
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
	public void actionPerformed(java.awt.event.ActionEvent ae)
	{
		String tInput;
		Object source = ae.getSource();

		//listener code for speak button
		if (source == speak)   {
			NGlobals.cPrint("Speak Button Pressed");

			tInput = input.getText();
			if (lastInput.equals(tInput)) {
				input.setText("");
				return;
			}
			else {
				lastInput = tInput;
				int tLen = tInput.length();
				if (tLen < 2) 
				    return;
				//    char[] tStringAsChars = tString.toCharArray();
				byte[] tStringAsBytes = tInput.getBytes();

				mySand.sendGrain((byte)NAppID.CLOUD_CHAT, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.BYTE, tLen, tStringAsBytes );


				// The data 
				NGlobals.cPrint("sending:  (" + tLen + ") of this data type");

				//                for (int i=0; i<tLen; i++) {
				//                NGlobals.cPrint("sending:  " + tString.charAt(i));
				//                streamOut.writeByte(tString.charAt(i));
				//                }

				NGlobals.cPrint("sending: (" + tInput + ")");
				input.setText("");
			}

		} 
	}
}
