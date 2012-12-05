/*
  Revised/cleaned, 6/12/2012, Steven Kemper
  Integrating NOMADSApp class
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.text.DefaultCaret;
import javax.swing.*;
import java.util.*;
import java.lang.*;
import nomads.v210.*;

public class DiscussClientPanel extends JPanel implements ActionListener, KeyListener {   

    JTextArea chatWindow;
    JScrollPane spane;
    JButton speak;
    JTextField input;
    JPanel panel, panPanel, wholeThing, lTab, rTab, titleTopicPanel;
    JLabel title, topic, spa1, spa2, spa3, spa5;
    public String myUserName;
    Font titleFont, topicFont;
    String tempString = "";   

    Boolean DiscussOn=true;


    int tAlpha = 255;
    //    nomadsColors[i++] = new Color(191,140,44,tAlpha);

    Color BG = new Color(145, 106, 65);
    Color BG2 = new Color(191, 140, 44);

    // Color BG =   new Color(235,220,160,tAlpha);
    //background color for chatwindow
    Color cWindy = new Color(185, 146, 105);

    // Color BG2 = new Color(249,245,220,tAlpha); //light yellow = 10

    //background color for input text field
    Color inputColor = new Color(249,241,131);

    //color for chat window
    Color chatColor = new Color(0,0,0);

    Font chatFont = new Font("sansserif", Font.PLAIN, 14);

    boolean c = false; //flag to see if it is connected to server
    int wait;

    NSand mySand;

    StudentControlPanel parent;

    public void chatBottom() {
	chatWindow.setCaretPosition(chatWindow.getDocument().getLength());
    }

    public void resetSand(NSand inSand)
    { 
	mySand = inSand;
    }

    public void init(NSand inSand)
    { 
	mySand = inSand;
	//topmost container. It will hold wholeThing, which is the applet
	setLayout( new BorderLayout() );

	//applet it will be added to the topmost container  
	//this is done for the purposes of color
	wholeThing = new JPanel( new BorderLayout() );
	wholeThing.setBackground(BG);

	//initialize components
	speak = new JButton("Speak");
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
	panPanel = new JPanel( new GridLayout(1,1,0,5)); //holds panel for spacing and color purposes
	panPanel.setBackground(BG);
	panel.setBackground(BG);		  

	titleTopicPanel = new JPanel( new GridLayout(2,1));
	titleTopicPanel.setBackground(BG);

	titleFont = new Font("TimesRoman", Font.BOLD, 28);
	title = new JLabel("Discussion", JLabel.CENTER);
	title.setFont(titleFont);
	topicFont = new Font("TimesRoman", Font.PLAIN, 20);
	topic = new JLabel("", JLabel.CENTER);
	topic.setFont(topicFont);
	titleTopicPanel.add(title);
	titleTopicPanel.add(topic);


	//key listener stuff
	input.addKeyListener(this); 

	//add action listeners to the buttons
	speak.addActionListener(this);

	//buffer the sides of the applet  
	lTab = new JPanel( new FlowLayout() );
	rTab = new JPanel( new FlowLayout() );
	lTab.setBackground(BG);
	rTab.setBackground(BG);

	spa1 = new JLabel("            ");
	spa2 = new JLabel("            ");
	spa5 = new JLabel("                 ");
	lTab.add(spa1);
	rTab.add(spa2);

	//add components to the applet
	panel.add(spa5);
	panel.add(input);
	panel.add(speak);   
	panPanel.add(panel);


	wholeThing.add(titleTopicPanel, BorderLayout.NORTH);
	wholeThing.add(panPanel, BorderLayout.SOUTH);
	wholeThing.add(spane, BorderLayout.CENTER);
	wholeThing.add(lTab, BorderLayout.WEST);
	wholeThing.add(rTab, BorderLayout.EAST);	   

	add(wholeThing, BorderLayout.CENTER);

    }

    public synchronized void handle(NGrain inGrain)
    {	
	int incNBlocks, incDType, incDLen;
	int i,j;
	int incIntData[] = new int[1000];
	byte incByteData[] = new byte[1000];  // Cast as chars here because we're using chars -> strings
	NGrain grain;
    
	NGlobals.cPrint("DiscussClient -> handle()");
    
	grain = inGrain;

	byte incAppID = grain.appID;
	byte incCmd = grain.command;
	String msg = new String(grain.bArray);
    
	if (incAppID == NAppID.DISCUSS_PROMPT && incCmd == NCommand.SEND_DISCUSS_PROMPT) {
	    topic.setText(msg);
	    tempString = new String(msg);
	    topic.setForeground(Color.BLACK);
	    topicFont = new Font("TimesRoman", Font.PLAIN, 20);
	}
	// Disable discuss when the student panel button is off
	else if (incAppID == NAppID.INSTRUCTOR_PANEL && incCmd == NCommand.SET_DISCUSS_STATUS) {
	    if (grain.bArray[0] == 0) {
		speak.setEnabled(false);
		topic.setText("Discuss Disabled");
		DiscussOn = false;
	    }
	    else if (grain.bArray[0] == 1) {
		chatWindow.setText("");
		speak.setEnabled(true);
		topic.setText(tempString);
		DiscussOn = true;
	    }		
	}
    		

	else if ((incAppID == NAppID.DISCUSS || 
		 incAppID == NAppID.INSTRUCTOR_DISCUSS ||
		 grain.appID == NAppID.SERVER) &&
		 DiscussOn == true) {

	    if (incCmd == NCommand.SEND_MESSAGE) {
		chatWindow.append(msg + "\n");
		// input.requestFocus();
	    }
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
	if (e.getKeyCode() == 10) {// enter key

	    NGlobals.cPrint("ENTER pressed");
	    String t1String = input.getText();
	    int t1Len = t1String.length();

	    String tString = new String(myUserName + ": " + t1String);
	    int tLen = tString.length();
	    //    char[] tStringAsChars = tString.toCharArray();
	    byte[] tStringAsBytes = tString.getBytes();

	    if (t1Len > 0)
		mySand.sendGrain((byte)NAppID.DISCUSS, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.CHAR, tLen, tStringAsBytes );

	    // The data 
	    NGlobals.cPrint("sending:  (" + tLen + ") of this data type");

	    //                for (int i=0; i<tLen; i++) {
	    //                NGlobals.cPrint("sending:  " + tString.charAt(i));
	    //                streamOut.writeByte(tString.charAt(i));
	    //                }

	    NGlobals.cPrint("sending: (" + tString + ")");
	    input.setText("");
	    chatBottom();

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
		NGlobals.cPrint("pressed speak button");
		String t1String = input.getText();
		int t1Len = t1String.length();

		String tString = new String(myUserName + ": " + t1String);
		int tLen = tString.length();
		//			//    char[] tStringAsChars = tString.toCharArray();
		byte[] tStringAsBytes = tString.getBytes();
		//
		if (t1Len > 0)
		    mySand.sendGrain((byte)NAppID.DISCUSS, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.CHAR, tLen, tStringAsBytes );


		// The data 
		NGlobals.cPrint("sending:  (" + tLen + ") of this data type");

		//                for (int i=0; i<tLen; i++) {
		//                NGlobals.cPrint("sending:  " + tString.charAt(i));
		//                streamOut.writeByte(tString.charAt(i));
		//                }

		NGlobals.cPrint("sending: (" + tString + ")");
		input.setText("");
		chatBottom();
	    } 
    }

}
