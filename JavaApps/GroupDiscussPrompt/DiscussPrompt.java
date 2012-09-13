/*
  NOMADS Group Discuss Prompt v.210
  Revised/cleaned, 6/14/2012, Steven Kemper
  Integrating NOMADSApp class
 */

import java.net.*;
import java.io.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.*;
import java.util.*;
import nomads.v210.*;

// writes bytes and strings seperately

public class DiscussPrompt extends JApplet implements ActionListener
{

	private class NomadsAppThread extends Thread {
		DiscussPrompt client; //Replace with current class name

		public NomadsAppThread(DiscussPrompt _client) {
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

	String temp = "";

	JLabel title;
	JButton ask;
	JTextField question;

	Color[] theColors = new Color[11];

	Color titleColor = new Color(233, 158, 37);
	Color middleColor = new Color(254, 205, 129);

	JPanel middle, top; //bottom shows teacher the results of poll
	JPanel subMiddle, wholeThing;



	//middle is where teacher submits poll question

	public void init()
	{
		theColors[0] = new Color(0,0,0); //dummy so array indices line up
		theColors[1] = new Color(94,41,28); //muddy red = no understanding
		theColors[2] = new Color(158,55,33);
		theColors[3] = new Color(145,86,65);
		theColors[4] = new Color(187,137,44);
		theColors[5] = new Color(191,140,44);
		theColors[6] = new Color(233,158,37);
		theColors[7] = new Color(242,197,126);
		theColors[8] = new Color(254,205,129);
		theColors[9] = new Color(249,241,131);
		theColors[10] = new Color(249,245,220); //light yellow = full understanding

		// wholeThing is whole applet
		wholeThing = new JPanel(new GridLayout(3,1));

		//top of applet
		top = new JPanel(new FlowLayout());

		//middle of applet
		//split to allow results and question stuff to fit
		subMiddle = new JPanel(new FlowLayout());
		middle = new JPanel(new BorderLayout());

		// setting background colors
		// the names of the colors are irrelevent at this point 1-7-10
		top.setBackground(middleColor);
		subMiddle.setBackground(middleColor);
		middle.setBackground(middleColor);
		wholeThing.setBackground(middleColor);

		// set topmost layout, which just holds whole thing
		setLayout(new BorderLayout());

		//set title
		title = new JLabel("<html><h1 style='color:black;font-size:150%'>NOMADS discuss Prompt</h1></html>", JLabel.CENTER);
		top.add(title);

		//JTextField to recieve question entered by user
		question = new JTextField("",40);

		//button to send question
		ask = new JButton("Ask");

		ask.addActionListener(this);
		question.addActionListener(this);

		//creates middle JPanel
		subMiddle.add(question);
		subMiddle.add(ask);

		middle.add(subMiddle, BorderLayout.NORTH);

		//add componens to top-most container
		wholeThing.add(top);
		wholeThing.add(middle);

		add(wholeThing, BorderLayout.CENTER);
		//bottom.setBackground(theColors[2]);

		discussSand = new NSand(); //Connects on init
		discussSand.connect();

		nThread = new NomadsAppThread(this);
		nThread.start();
	}



	

	public void handle()
	{
		//Not implemented for DiscussPrompt ****STK 6/14/12
	}

	public void actionPerformed(java.awt.event.ActionEvent ae)
	{
		Object source = ae.getSource();

		if (source == ask)
		{
			String tString = question.getText();
			int tLen = tString.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = tString.getBytes();
			discussSand.sendGrain((byte)NAppID.DISCUSS_PROMPT, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.BYTE, tLen, tStringAsBytes );


			// The data 
			NGlobals.cPrint("sending:  (" + tLen + ") of this data type");

			//                for (int i=0; i<tLen; i++) {
			//                NGlobals.cPrint("sending:  " + tString.charAt(i));
			//                streamOut.writeByte(tString.charAt(i));
			//                }

			NGlobals.cPrint("sending: (" + tString + ")");
			question.setText("");
			
		}
		if (source == question)
		{
			String tString = question.getText();
			int tLen = tString.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = tString.getBytes();
			discussSand.sendGrain((byte)NAppID.DISCUSS_PROMPT, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.BYTE, tLen, tStringAsBytes );


			// The data 
			NGlobals.cPrint("sending:  (" + tLen + ") of this data type");

			//                for (int i=0; i<tLen; i++) {
			//                NGlobals.cPrint("sending:  " + tString.charAt(i));
			//                streamOut.writeByte(tString.charAt(i));
			//                }

			NGlobals.cPrint("sending: (" + tString + ")");
			question.setText("");
		}
	}  
}