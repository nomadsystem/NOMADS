/*
  NOMADS Poll Prompt v.210
  Revised/cleaned, 6/15/2012, Steven Kemper
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
import java.text.DecimalFormat;
import nomads.v210.*;

// writes bytes and strings seperately

public class PollPrompt extends JApplet implements ActionListener
{

    String typeOfQuestionSubmitted = "";

    // used for totaling purposes for yes no format
    int yesTotal = 0;
    int noTotal = 0;
    int totalYesAndNo = 0;
    double yesPer = 0, noPer = 0;
    double difference = 0, finAns = 0;

    JLabel title, results;
    String qText;
    JButton ask;
    JButton clear;
    JTextField question;
    JComboBox qType;

    //used for response averaging purposes 1 to 10 question format
    double resp = 0;
    double runningTotal = 0;
    double count = 0;
    double average = 0;
    //	float roundAverage = 0;
    DecimalFormat roundAverage;

    Color[] theColors = new Color[11];

    Color titleColor = new Color(191,140,44);
    Color middleColor = new Color(191,140,44);


    JPanel bottom, middle, top; //bottom shows teacher the results of poll
    JPanel subMiddle, dispResults, wholeThing;

    NSand mySand;

    //middle is where teacher submits poll question


    public void resetSand(NSand inSand) {
	mySand = inSand;
    }

    public void init(NSand inSand)
    {

	mySand = inSand;

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

	//bottom of applet. for results color
	bottom = new JPanel(new FlowLayout());

	//middle of applet
	//split to allow results and question stuff to fit
	middle = new JPanel(new FlowLayout());

	//going in bottom of middle
	dispResults = new JPanel(new FlowLayout());
	results = new JLabel("<html><center><h3> (ask question)</h3><h2 style='color:black'>Awaiting results");
	dispResults.add(results);

	// setting background colors
	// the names of the colors are irrelevent at this point 1-7-10
	top.setBackground(middleColor);
	middle.setBackground(middleColor);
	bottom.setBackground(titleColor);
	wholeThing.setBackground(middleColor);
	dispResults.setBackground(titleColor);

	qType = new JComboBox();
	qType.addItem("Yes-No");
	qType.addItem("A through E");
	qType.addItem("Scale of 1 to 10");
	//qType.addItem("Infinity and beyond");

	// set topmost layout, which just holds whole thing
	setLayout(new BorderLayout());

	//set title
	title = new JLabel("<html><h1 style='color:black;'>NOMADS Teacher Poll: Pose a question</h1><h3 style='color:black'>Choose a question type, type in the question, and hit 'ask' to poll students.<br>Poll Results will appear in the bottom of the screen.&nbsp<p></h3></html>", JLabel.CENTER);
	top.add(title);

	//JTextField to recieve question entered by user
	question = new JTextField("",40);
	question.setFocusable(true);
	//button to send question
	ask = new JButton("Ask");
	clear = new JButton("Clear");

	ask.addActionListener(this);
	clear.addActionListener(this);
	question.addActionListener(this);

	//creates middle JPanel
	middle.add(qType);
	middle.add(question);
	middle.add(ask);
	middle.add(clear);

	// middle.add(subMiddle, BorderLayout.NORTH);
	// middle.add(dispResults, BorderLayout.SOUTH);	

	//add componens to top-most container
	wholeThing.add(top);
	wholeThing.add(middle, BorderLayout.CENTER);
	wholeThing.add(dispResults);
	// wholeThing.add(bottom);

	add(wholeThing, BorderLayout.CENTER);
	// bottom.setBackground(theColors[2]);

	byte d[] = new byte[1];
	d[0] = 0;
	// mySand.sendGrain((byte)NAppID.TEACHER_POLL, (byte)NCommand.REGISTER, (byte)NDataType.UINT8, 1, d );
    }

    public void handle(NGrain myGrain)
    {
		
	int i,j;

	NGrain grain;
	NGlobals.cPrint("PollPrompt -> handle()");

	grain = myGrain;

	byte incAppID = grain.appID;
	byte incCmd = grain.command;
	int response;
		
	if (incAppID == NAppID.STUDENT_POLL) {
	    if (incCmd == NCommand.QUESTION_TYPE_YES_NO)	// Replace with QUESTION_TYPE_YES_NO)
		{
		    response = grain.iArray[0];

		    if (response == 1) //eventually may want to convert these to commands
			{
			    NGlobals.cPrint("PP:yes came in");
			    yesTotal++;
			    NGlobals.cPrint("PP:yesTotal " + yesTotal);
			}
		    if (response  == 0)
			{
			    NGlobals.cPrint("PP:no came in");
			    noTotal++;
			    NGlobals.cPrint("PP:noTotal " + noTotal);
			}

		    //convert yes no results to an average to map to color
		    totalYesAndNo = yesTotal + noTotal;
		    yesPer = (double) yesTotal / totalYesAndNo;
		    noPer = (double) noTotal / totalYesAndNo;
			
		    NGlobals.cPrint("PP:totalYesAndNo " + totalYesAndNo);

		    difference = yesPer - noPer;
		    difference *= 10;
			
		    NGlobals.cPrint("PP:difference " + difference);

		    finAns = 5 + difference;

		    NGlobals.cPrint("PP:finAns " + finAns);

		    // if it exceeds a boundary, it is set to the boundary value
		    if (finAns < 1)
			finAns = 1;

		    if (finAns > 10)
			finAns = 10;

		    NGlobals.cPrint("PP:finAns " + finAns);
		    NGlobals.cPrint("PP:(int)Math.round(finAns) " + (int)Math.round(finAns));

		    //show results with color in bottom of applet
		    bottom.setBackground(theColors[(int)Math.round(finAns)]);
		    dispResults.setBackground(theColors[(int)Math.round(finAns)]);

		    //show yes no totals in applet too
		    results.setText("<html><center><h3>" + qText + "</h3><p>&nbsp<p><h2 style='color:black'>Yes: " + yesTotal + " No: " + noTotal + "</h2></center></html>");	
		}
			
	    //parse 1 to 10 results
	    else if (incCmd == NCommand.QUESTION_TYPE_ONE_TO_TEN)	// Replace with QUESTION_TYPE_ONE_TO_TEN)
		{
		    response = grain.iArray[0];
		    resp = response; //May need to deal with gettng results as an int or string ****STK 6/15/12
		    runningTotal += resp;
		    count++;
				
		    NGlobals.cPrint("PP:runningTotal / count " + (runningTotal / count));
		    average = runningTotal / count;

		    //show results with color in bottom of applet
		    bottom.setBackground(theColors[(int)Math.round(average)]);
		    dispResults.setBackground(theColors[(int)Math.round(average)]);
		    DecimalFormat roundAverage = new DecimalFormat("#.##");//use to round to 2 decimal places
		    //	roundAverage = (float)average;

		    //show results average in applet too

		    results.setText("<html><center><h3>" + qText + "</h3><h2 style='color:black'>Average: " + roundAverage.format(average) + "</h2></center></html>");	
		}
	    else if (incCmd == NCommand.QUESTION_TYPE_A_TO_E) {
		    response = grain.iArray[0];
		    resp = response; //May need to deal with gettng results as an int or string ****STK 6/15/12
		    runningTotal += resp;
		    count++;
				
		    NGlobals.cPrint("PP:runningTotal / count " + (runningTotal / count));
		    average = runningTotal / count;

		    //show results with color in bottom of applet
		    bottom.setBackground(theColors[(int)Math.round(average)]);
		    dispResults.setBackground(theColors[(int)Math.round(average)]);
		    DecimalFormat roundAverage = new DecimalFormat("#.##");//use to round to 2 decimal places
		    //	roundAverage = (float)average;

		    //show results average in applet too

		    results.setText("<html><center><h3>" + qText + "</h3><h2 style='color:black'>Average: " + roundAverage.format(average) + "</h2></center></html>");	
	    }
	    else	
		{
		    NGlobals.cPrint("PP:Teacher poll says Extraneous information");
		}
	}
		
    }

    public void actionPerformed(java.awt.event.ActionEvent ae)
    {
	Object source = ae.getSource();

	if (source == clear)
	    {
		int tCommand = 0;

		int tQuestionType = qType.getSelectedIndex();
			
		if (tQuestionType == 0) //refers to JComboBox index number
		    tCommand = NCommand.QUESTION_TYPE_YES_NO;	// Replace with QUESTION_TYPE_YES_NO;
		else if (tQuestionType == 1)
		    tCommand = NCommand.QUESTION_TYPE_A_TO_E;	// Replace with QUESTION_TYPE_ONE_TO_TEN;
		else if (tQuestionType == 2)
		    tCommand = NCommand.QUESTION_TYPE_ONE_TO_TEN;	// Replace with QUESTION_TYPE_ONE_TO_TEN;
		else 
		    NGlobals.cPrint("Invalid question type specified");

		String tString = new String("");
		qText = tString;
		int tLen = qText.length();

		//    char[] tStringAsChars = tString.toCharArray();
		byte[] tStringAsBytes = qText.getBytes();
		mySand.sendGrain((byte)NAppID.TEACHER_POLL, (byte)tCommand, (byte)NDataType.CHAR, tLen, tStringAsBytes );

		// The data 
		NGlobals.cPrint("sending:  (" + tLen + ") of this data type");

		//                for (int i=0; i<tLen; i++) {
		//                NGlobals.cPrint("sending:  " + tString.charAt(i));
		//                streamOut.writeByte(tString.charAt(i));
		//                }
		NGlobals.cPrint("sending: Command:(" + tCommand + ")");
		NGlobals.cPrint("sending: (" + qText + ")");
		question.setText("");

		runningTotal = 0;
		count = 0;
		average = 0;

		// yes no number data variables
		yesTotal = 0;
		noTotal = 0;
		totalYesAndNo = 0;
		yesPer = 0;
		noPer = 0;
		difference = 0;
		finAns = 0;

		results.setText("<html><center><h3>" + qText + "</h3><h2 style='color:black'>Awaiting results");
		bottom.setBackground(titleColor);
		dispResults.setBackground(titleColor);

		typeOfQuestionSubmitted = qType.getSelectedItem().toString();


	    }


	if (source == ask)
	    {
		int tCommand = 0;

		int tQuestionType = qType.getSelectedIndex();
			
		if (tQuestionType == 0) //refers to JComboBox index number
		    tCommand = NCommand.QUESTION_TYPE_YES_NO;	// Replace with QUESTION_TYPE_YES_NO;
		else if (tQuestionType == 1)
		    tCommand = NCommand.QUESTION_TYPE_A_TO_E;	// Replace with QUESTION_TYPE_ONE_TO_TEN;
		else if (tQuestionType == 2)
		    tCommand = NCommand.QUESTION_TYPE_ONE_TO_TEN;	// Replace with QUESTION_TYPE_ONE_TO_TEN;

		String tString = question.getText();
		qText = tString;
		int tLen = tString.length();
		//    char[] tStringAsChars = tString.toCharArray();
		byte[] tStringAsBytes = tString.getBytes();
		mySand.sendGrain((byte)NAppID.TEACHER_POLL, (byte)tCommand, (byte)NDataType.CHAR, tLen, tStringAsBytes );


		// The data 
		NGlobals.cPrint("sending:  (" + tLen + ") of this data type");

		//                for (int i=0; i<tLen; i++) {
		//                NGlobals.cPrint("sending:  " + tString.charAt(i));
		//                streamOut.writeByte(tString.charAt(i));
		//                }
		NGlobals.cPrint("sending: Command:(" + tCommand + ")");
		NGlobals.cPrint("sending: (" + tString + ")");
		question.setText("");

		runningTotal = 0;
		count = 0;
		average = 0;

		// yes no number data variables
		yesTotal = 0;
		noTotal = 0;
		totalYesAndNo = 0;
		yesPer = 0;
		noPer = 0;
		difference = 0;
		finAns = 0;

		results.setText("<html><center><h3>" + qText + "</h3><h2 style='color:black'>Awaiting results");
		bottom.setBackground(titleColor);
		dispResults.setBackground(titleColor);

		typeOfQuestionSubmitted = qType.getSelectedItem().toString();

		//			try
		//			{
		//				NGlobals.cPrint("PP:pollPrompt sending data to server");
		//
		//				streamOut.writeByte(app_id.TEACHER_POLL);
		//				streamOut.writeUTF(qType.getSelectedItem().toString() + ";" + question.getText());
		//
		//				streamOut.flush();
		//				// ask.setText("");
		//				//clear existing answer data to prepare for new question
		//				// 1 to 10 number data variables
		//				runningTotal = 0;
		//				count = 0;
		//				average = 0;
		//
		//				// yes no number data variables
		//				yesTotal = 0;
		//				noTotal = 0;
		//				totalYesAndNo = 0;
		//				yesPer = 0;
		//				noPer = 0;
		//				difference = 0;
		//				finAns = 0;
		//
		//				results.setText("");
		//				bottom.setBackground(titleColor);
		//				dispResults.setBackground(titleColor);
		//
		//				typeOfQuestionSubmitted = qType.getSelectedItem().toString();
		//
		//			}
		//			catch(IOException ioe)
		//			{
		//				NGlobals.cPrint("PP:IOException. Question not posed");
		//			}

	    }
	if (source == question)
	    {
		int tCommand = 0;

		int tQuestionType = qType.getSelectedIndex();
		if (tQuestionType == 0) //refers to JComboBox index number
		    tCommand = NCommand.QUESTION_TYPE_YES_NO;	// Replace with QUESTION_TYPE_YES_NO;
		else if (tQuestionType == 1)
		    tCommand = NCommand.QUESTION_TYPE_A_TO_E;	// Replace with QUESTION_TYPE_ONE_TO_TEN;
		else if (tQuestionType == 2)
		    tCommand = NCommand.QUESTION_TYPE_ONE_TO_TEN;	// Replace with QUESTION_TYPE_ONE_TO_TEN;

		String tString = question.getText();
		qText = tString;
		int tLen = tString.length();
		//    char[] tStringAsChars = tString.toCharArray();
		byte[] tStringAsBytes = tString.getBytes();
		mySand.sendGrain((byte)NAppID.TEACHER_POLL, (byte)tCommand, (byte)NDataType.BYTE, tLen, tStringAsBytes );


		// The data 
		NGlobals.cPrint("sending:  (" + tLen + ") of this data type");

		//                for (int i=0; i<tLen; i++) {
		//                NGlobals.cPrint("sending:  " + tString.charAt(i));
		//                streamOut.writeByte(tString.charAt(i));
		//                }
		NGlobals.cPrint("sending: Command:(" + tCommand + ")");
		NGlobals.cPrint("sending: (" + tString + ")");
		// xxx
		question.setText("");


		runningTotal = 0;
		count = 0;
		average = 0;

		// yes no number data variables
		yesTotal = 0;
		noTotal = 0;
		totalYesAndNo = 0;
		yesPer = 0;
		noPer = 0;
		difference = 0;
		finAns = 0;

		results.setText("<html><center><h3>" + qText + "</h3><h2 style='color:black'>Awaiting results");
		bottom.setBackground(titleColor);
		dispResults.setBackground(titleColor);

		typeOfQuestionSubmitted = qType.getSelectedItem().toString();

	    }
    }

}