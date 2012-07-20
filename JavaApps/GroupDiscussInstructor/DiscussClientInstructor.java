/*
  NOMADS Group Discuss (Instructor) v.210
  Revised/cleaned, 6/14/2012, Steven Kemper
  Integrating NOMADSApp class
 */

import java.net.*;
import java.io.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.DefaultCaret;
import javax.swing.*;
import java.util.*;
import java.lang.*;
import nomads.v210_auk.*;

public class DiscussClientInstructor extends JApplet implements ActionListener, KeyListener {   

	private class NomadsAppThread extends Thread {
		DiscussClientInstructor client; //Replace with current class name

		public NomadsAppThread(DiscussClientInstructor _client) {
			client = _client;
		}
		public void run()    {			
			NGlobals.lPrint("NomadsAppThread -> run()");
			while (true)  {
				client.handle();
			}
		}
	}

	NSand discussSand;
	private NomadsAppThread nThread;

	JTextArea chatWindow;
	JScrollPane spane;
	JButton speak, connect, disconnect;
	JTextField input;
	JPanel panel, panPanel, wholeThing, lTab, rTab, titleTopicPanel;
	JLabel title, topic, spa1, spa2, spa3, spa5;
	Font titleFont, topicFont;

	//background color for the whole applet
	Color BG = new Color(158,55,33);      

	//background color for chatwindow
	Color cWindy = new Color(242,197,126);

	//background color for input text field
	Color inputColor = new Color(249,241,131);

	//color for chat window
	Color chatColor = new Color(0,0,0);

	Font chatFont = new Font("sansserif", Font.PLAIN, 18);

	boolean c = false; //flag to see if it is connected to server
	int wait;

	public void init()
	{ //daniel's swingin' code
		//topmost container. It will hold wholeThing, which is the applet
		setLayout( new BorderLayout() );

		//applet it will be added to the topmost container  
		//this is done for the purposes of color
		wholeThing = new JPanel( new BorderLayout() );
		wholeThing.setBackground(BG);

		//initialize components
		speak = new JButton("Speak");
		//	  connect = new JButton("Connect");
		//	  disconnect = new JButton("Disconnect");
		chatWindow = new JTextArea(10,30);
		//makes chat window autoscroll
		DefaultCaret caret = (DefaultCaret)chatWindow.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		chatWindow.setEnabled(false);
		chatWindow.setBackground(cWindy);
		chatWindow.setFont(chatFont);
		chatWindow.setDisabledTextColor(chatColor);
		spane = new JScrollPane(chatWindow);
		input = new JTextField("", 30);
		input.setBackground(inputColor);
		panel = new JPanel( new FlowLayout() ); //holds buttons and textfield 
		panPanel = new JPanel( new GridLayout(1,1,0,5)); //holds panel for spacing and color purposes...may not be necessary
		panPanel.setBackground(BG);
		panel.setBackground(BG);		  

		titleTopicPanel = new JPanel( new GridLayout(2,1));
		titleTopicPanel.setBackground(BG);

		titleFont = new Font("TimesRoman", Font.BOLD, 28);
		title = new JLabel("Group Discuss", JLabel.CENTER);
		title.setFont(titleFont);
		topicFont = new Font("TimesRoman", Font.PLAIN, 20);
		topic = new JLabel("", JLabel.CENTER);
		topic.setFont(topicFont);
		titleTopicPanel.add(title);
		titleTopicPanel.add(topic);


		//key listener stuff
		//	  input.setFocusable(true);
		input.addKeyListener(this); 

		//add action listeners to the buttons
		speak.addActionListener(this);
		//	  connect.addActionListener(this);
		//	  disconnect.addActionListener(this);

		//buffer the sides of the applet  
		lTab = new JPanel( new FlowLayout() );
		rTab = new JPanel( new FlowLayout() );
		lTab.setBackground(BG);
		rTab.setBackground(BG);

		spa1 = new JLabel("            ");
		spa2 = new JLabel("            ");
		//spa3 = new JLabel("            ");
		spa5 = new JLabel("                 ");
		lTab.add(spa1);
		rTab.add(spa2);

		//add components to the applet
		//	   panel.add(connect);
		//	   panel.add(disconnect);
		panel.add(spa5);
		panel.add(input);
		panel.add(speak);   
		//panPanel.add(spa3);
		panPanel.add(panel);


		wholeThing.add(titleTopicPanel, BorderLayout.NORTH);
		wholeThing.add(panPanel, BorderLayout.SOUTH);
		wholeThing.add(spane, BorderLayout.CENTER);
		wholeThing.add(lTab, BorderLayout.WEST);
		wholeThing.add(rTab, BorderLayout.EAST);	   

		add(wholeThing, BorderLayout.CENTER);

		discussSand = new NSand(); //Connects on init
		discussSand.connect();

		nThread = new NomadsAppThread(this);
		nThread.start();

	}

	public void handle()
	{	
		int incCmd, incNBlocks, incDType, incDLen;
		int i,j;
		int incIntData[] = new int[1000];
		byte incByteData[] = new byte[1000];  // Cast as chars here because we're using chars -> strings
		NGrain grain;

		NGlobals.cPrint("DiscussClient -> handle()");

		grain = discussSand.getGrain();
		grain.print(); //prints grain data to console
		String msg = new String(grain.bArray);

		if (grain.appID == NAppID.DISCUSS_PROMPT) {
			topic.setText(msg);
			String tempString = new String(msg);
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
			input.requestFocus();
		}
		else {
			grain = null;
		}
		if (grain != null)
			grain = null;
		
	}



	////////////////////////////////////////////////////////////////////////////
	//key listener code
	///////////////////////////////////////////////////////////////////////////
	public void keyPressed (KeyEvent e)
	{
		if (e.getKeyCode() == 10) // enter key
		{
			NGlobals.cPrint("ENTER");

			String tString = input.getText();
			int tLen = tString.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = tString.getBytes();

			discussSand.sendGrain((byte)NAppID.INSTRUCTOR_DISCUSS, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.BYTE, tLen, tStringAsBytes );


			// The data 
			NGlobals.cPrint("sending:  (" + tLen + ") of this data type");

			//                for (int i=0; i<tLen; i++) {
			//                NGlobals.cPrint("sending:  " + tString.charAt(i));
			//                streamOut.writeByte(tString.charAt(i));
			//                }

			NGlobals.cPrint("sending: (" + tString + ")");
			input.setText("");
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
		Object source = ae.getSource();

		//listener code for speak button
		if (source == speak)
		{
			NGlobals.cPrint("ENTER");

			String tString = input.getText();
			int tLen = tString.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = tString.getBytes();

			discussSand.sendGrain((byte)NAppID.INSTRUCTOR_DISCUSS, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.BYTE, tLen, tStringAsBytes );


			// The data 
			NGlobals.cPrint("sending:  (" + tLen + ") of this data type");

			//                for (int i=0; i<tLen; i++) {
			//                NGlobals.cPrint("sending:  " + tString.charAt(i));
			//                streamOut.writeByte(tString.charAt(i));
			//                }

			NGlobals.cPrint("sending: (" + tString + ")");
			input.setText("");
			//       	 }
		} 
	}

}
