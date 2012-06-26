/*
  NOMADS Poll Student v.210
  Revised/cleaned, 6/15/2012, Steven Kemper
  Integrating NOMADSApp class
 */
import java.net.*;
import java.io.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.lang.*;
import nomads.v210.*;

// Writes bytes and Messages seperately

public class PollStudent extends JApplet implements ItemListener, ActionListener
{

	private class NomadsAppThread extends Thread {
		PollStudent client; //Replace with current class name

		public NomadsAppThread(PollStudent _client) {
			client = _client;
		}
		public void run()    {			
			NGlobals.lPrint("NomadsAppThread -> run()");
			while (true)  {
				client.handle();
			}
		}
	}

	NSand pollSand;
	private NomadsAppThread nThread;

	JCheckBox one, two, three, four, five, six, seven, eight, nine, ten;
	JCheckBox yes, no;
	JLabel question, title;
	JPanel oneToTen, wholeThing;

	// background color for applet
	Color BG = new Color(191, 140, 44);
	Color BG2 = new Color(145, 106, 65);

	int currentQuestionType = 0;

	//just for gui making purposes, delete later
	String q = "";

	// A through E also new method that should have been used initially
	JCheckBox[] ateBox;
	String [] numBoxLetter;

	public void init()
	{
		int i;
		// topmost container. It will hold wholeThing, which is the applet
		setLayout(new BorderLayout());		

		//the whole applet
		wholeThing = new JPanel (new GridLayout(4,1));
		wholeThing.setBackground(BG2);

		// holds the selection options for one to ten questions, and yes no options
		oneToTen = new JPanel(new FlowLayout());

		question = new JLabel("", JLabel.CENTER);
		// question.setBackground(BG2);

		//placeholder for app title
		title = new JLabel("<html><h1 style='color:black;font-size:125%'>NOMADS Poll</h1></html>", JLabel.CENTER);

		numBoxLetter = new String[5];
		ateBox = new JCheckBox[5];

		numBoxLetter[0] = "a";
		numBoxLetter[1] = "b";
		numBoxLetter[2] = "c";
		numBoxLetter[3] = "d";
		numBoxLetter[4] = "e";

		for (i=0;i<5;i++) {
			ateBox[i] = new JCheckBox(numBoxLetter[i]);
			ateBox[i].addActionListener(this);

		}

		//placeholder for bottom of grid layout

		//one to ten options 
		one = new JCheckBox("1");
		two = new JCheckBox("2");
		three = new JCheckBox("3");
		four = new JCheckBox("4");
		five = new JCheckBox("5");
		six = new JCheckBox("6");
		seven = new JCheckBox("7");
		eight = new JCheckBox("8");
		nine = new JCheckBox("9");
		ten = new JCheckBox("10");

		//yes no options
		yes = new JCheckBox("Yes");
		no = new JCheckBox("No");

		// listeners
		one.addActionListener(this);
		two.addActionListener(this);
		three.addActionListener(this);
		four.addActionListener(this);
		five.addActionListener(this);
		six.addActionListener(this);
		seven.addActionListener(this);
		eight.addActionListener(this);
		nine.addActionListener(this);
		ten.addActionListener(this);		

		yes.addActionListener(this);
		no.addActionListener(this);

		//oneToTen holds the options

		oneToTen.setBackground(BG2);

		wholeThing.add(title);
		wholeThing.add(question);
		wholeThing.add(oneToTen);

		add(wholeThing, BorderLayout.CENTER);

		pollSand = new NSand(); 
		pollSand.connect();

		nThread = new NomadsAppThread(this);
		nThread.start();

	}





	//read poll information from teacherpoll app
	public void handle()    {
		int i,j;
		int appID, incCmd, incDType, incDLen;
		int incIntData[] = new int[1000];
		byte incByteData[] = new byte[1000];  
		NGrain grain;

		grain = pollSand.getGrain();
		grain.print(); //prints grain data to console

		appID = grain.appID;
		incCmd = grain.command;
		String toBePosed = new String(grain.bArray); //the incoming question ****STK 6/18/12

		NGlobals.cPrint("****************");
		NGlobals.cPrint("Student Poll handle method");
		NGlobals.cPrint("Student Poll Command = " + incCmd);
		NGlobals.cPrint("Student Poll toBePosed = " + toBePosed);

		if (appID == NAppID.INSTRUCTOR_PANEL) { //Instructor Panel not Yet Implemented ****STK 6/18/12

			if (incCmd == NCommand.VOTE) {
				if (currentQuestionType == NCommand.QUESTION_TYPE_YES_NO) {
					yes.setEnabled(true);
					no.setEnabled(true);

					yes.setSelected(false);
					no.setSelected(false);

					oneToTen.revalidate();
					oneToTen.repaint();
				}
				if (currentQuestionType == NCommand.QUESTION_TYPE_ONE_TO_TEN) {
					one.setEnabled(true);
					two.setEnabled(true);
					three.setEnabled(true);
					four.setEnabled(true);
					five.setEnabled(true);
					six.setEnabled(true);
					seven.setEnabled(true);
					eight.setEnabled(true);
					nine.setEnabled(true);
					ten.setEnabled(true);

					one.setSelected(false);
					two.setSelected(false);
					three.setSelected(false);
					four.setSelected(false);
					five.setSelected(false);
					six.setSelected(false);
					seven.setSelected(false);
					eight.setSelected(false);
					nine.setSelected(false);
					ten.setSelected(false);

					oneToTen.revalidate();
					oneToTen.repaint();
				}

				//refresh the gui

			}
		}


		//if this string was sent from the teacher poll
		if (appID == NAppID.TEACHER_POLL)
		{	 


			//set the question type ===========================================--------------

			if (incCmd == NCommand.QUESTION_TYPE_A_TO_E)  // +++++++++++++++++++++++++++	
			{
				NGlobals.cPrint("Student Poll: Got A-E Question");
				if (currentQuestionType == 0)
				{
					// gui is blank
					// add the options
					for(i=0;i<5;i++) {
						oneToTen.add(ateBox[i]);
						ateBox[i].setEnabled(true);
						ateBox[i].setSelected(false);
					}
					NGlobals.cPrint("Student Poll AEQ: Adding A-E Buttons");


					//refresh the gui
					oneToTen.revalidate();
					oneToTen.repaint();

					currentQuestionType = NCommand.QUESTION_TYPE_A_TO_E;
				}

				else if (currentQuestionType == incCmd) //Question type is already set...
				{
					// gui is already set to display yes no question type

					for(i=0;i<5;i++) {
						//	oneToTen.add(ateBox[i]);
						ateBox[i].setEnabled(true);
						ateBox[i].setSelected(false);
					}
					NGlobals.cPrint("Student Poll AEQ: Already has question Adding A-E Buttons");
					currentQuestionType = NCommand.QUESTION_TYPE_A_TO_E;
				}

				else if (currentQuestionType == NCommand.QUESTION_TYPE_ONE_TO_TEN)
				{
					// out with the old
					oneToTen.remove(one);
					oneToTen.remove(two);
					oneToTen.remove(three);
					oneToTen.remove(four);
					oneToTen.remove(five);
					oneToTen.remove(six);
					oneToTen.remove(seven);
					oneToTen.remove(eight);
					oneToTen.remove(nine);
					oneToTen.remove(ten);

					// in with the new

					for(i=0;i<5;i++) {
						oneToTen.add(ateBox[i]);
						ateBox[i].setEnabled(true);
						ateBox[i].setSelected(false);
					}
					NGlobals.cPrint("Student Poll AEQ: Removing 1-10, Adding A-E Buttons");

					//refresh the gui
					oneToTen.revalidate();
					oneToTen.repaint();

					currentQuestionType = NCommand.QUESTION_TYPE_A_TO_E;
				}

				else if (currentQuestionType == NCommand.QUESTION_TYPE_YES_NO)
				{
					// out with the old
					oneToTen.remove(yes);
					oneToTen.remove(no);

					// in with the new      	 	 		
					for(i=0;i<5;i++) {
						oneToTen.add(ateBox[i]);
						ateBox[i].setEnabled(true);
						ateBox[i].setSelected(false);
					}
					NGlobals.cPrint("Student Poll AEQ: Removing Yes/No, Adding A-E Buttons");

					//refresh the gui
					oneToTen.revalidate();
					oneToTen.repaint();

					currentQuestionType =  NCommand.QUESTION_TYPE_A_TO_E;
				}

			}


			if (incCmd == NCommand.QUESTION_TYPE_YES_NO)  {// +++++++++++++++++++++++++++
				NGlobals.cPrint("Student Poll: Got Yes/No question");
				NGlobals.cPrint("Student Poll: Current Question Type: " + currentQuestionType);

				if (currentQuestionType == 0)
				{
					// gui is blank
					// add the options
					oneToTen.add(yes);
					oneToTen.add(no);

					NGlobals.cPrint("Student Poll YNQ: Adding Yes/No Buttons");

					yes.setEnabled(true);
					no.setEnabled(true);

					// refresh the options
					yes.setSelected(false);
					no.setSelected(false);

					//refresh the gui
					oneToTen.revalidate();
					oneToTen.repaint();

					currentQuestionType = NCommand.QUESTION_TYPE_YES_NO;
				}

				else if (currentQuestionType == incCmd)
				{
					// gui is already set to display yes no question type

					// refresh the options
					yes.setEnabled(true);
					no.setEnabled(true);
					yes.setSelected(false);
					no.setSelected(false);
					NGlobals.cPrint("Student Poll YNQ: keeping buttons that are already there");
					currentQuestionType = NCommand.QUESTION_TYPE_YES_NO;

				}

				else if (currentQuestionType == NCommand.QUESTION_TYPE_ONE_TO_TEN)
				{
					// out with the old
					oneToTen.remove(one);
					oneToTen.remove(two);
					oneToTen.remove(three);
					oneToTen.remove(four);
					oneToTen.remove(five);
					oneToTen.remove(six);
					oneToTen.remove(seven);
					oneToTen.remove(eight);
					oneToTen.remove(nine);
					oneToTen.remove(ten);
					NGlobals.cPrint("Student Poll YNQ: Removing 1-10 Buttons");

					// in with the new
					oneToTen.add(yes);
					oneToTen.add(no);
					NGlobals.cPrint("Student Poll YNQ: Adding Yes/No Buttons");

					// refresh the options
					yes.setEnabled(true);
					no.setEnabled(true);
					yes.setSelected(false);
					no.setSelected(false);

					//refresh the gui
					oneToTen.revalidate();
					oneToTen.repaint();

					currentQuestionType = NCommand.QUESTION_TYPE_YES_NO;
				}

				else if (currentQuestionType == NCommand.QUESTION_TYPE_A_TO_E) {

					// out with the old
					for(i=0;i<5;i++) {
						oneToTen.remove(ateBox[i]);
					}
					NGlobals.cPrint("Student Poll YNQ: Removing A-E Buttons");

					// in with the new
					oneToTen.add(yes);
					oneToTen.add(no);
					NGlobals.cPrint("Student Poll YNQ: Adding Yes/No Buttons");

					// refresh the options
					yes.setEnabled(true);
					no.setEnabled(true);
					yes.setSelected(false);
					no.setSelected(false);

					//refresh the gui
					oneToTen.revalidate();
					oneToTen.repaint();

					currentQuestionType = NCommand.QUESTION_TYPE_YES_NO;
				}

			}

			if (incCmd == NCommand.QUESTION_TYPE_ONE_TO_TEN)  {// +++++++++++++++++++++++++++
				NGlobals.cPrint("Student Poll: Got 1-10 question");
				NGlobals.cPrint("Student Poll: Current Question Type: " + currentQuestionType);

				if (currentQuestionType == 0)
				{
					// gui is blank
					// add the options
					oneToTen.add(one);
					oneToTen.add(two);
					oneToTen.add(three);
					oneToTen.add(four);
					oneToTen.add(five);
					oneToTen.add(six);
					oneToTen.add(seven);
					oneToTen.add(eight);
					oneToTen.add(nine);
					oneToTen.add(ten);
					NGlobals.cPrint("Student Poll OTQ: Adding 1-10 Buttons");

					// refresh the options
					one.setEnabled(true);
					two.setEnabled(true);
					three.setEnabled(true);
					four.setEnabled(true);
					five.setEnabled(true);
					six.setEnabled(true);
					seven.setEnabled(true);
					eight.setEnabled(true);
					nine.setEnabled(true);
					ten.setEnabled(true);

					one.setSelected(false);
					two.setSelected(false);
					three.setSelected(false);
					four.setSelected(false);
					five.setSelected(false);
					six.setSelected(false);
					seven.setSelected(false);
					eight.setSelected(false);
					nine.setSelected(false);
					ten.setSelected(false);	

					//refresh the gui
					oneToTen.revalidate();
					oneToTen.repaint();

					currentQuestionType = NCommand.QUESTION_TYPE_ONE_TO_TEN;
				}

				else if (currentQuestionType == incCmd)
				{
					// gui is already set to display scale of one to ten question

					// refresh the options
					one.setEnabled(true);
					two.setEnabled(true);
					three.setEnabled(true);
					four.setEnabled(true);
					five.setEnabled(true);
					six.setEnabled(true);
					seven.setEnabled(true);
					eight.setEnabled(true);
					nine.setEnabled(true);
					ten.setEnabled(true);

					one.setSelected(false);
					two.setSelected(false);
					three.setSelected(false);
					four.setSelected(false);
					five.setSelected(false);
					six.setSelected(false);
					seven.setSelected(false);
					eight.setSelected(false);
					nine.setSelected(false);
					ten.setSelected(false);	
					NGlobals.cPrint("Student Poll OTQ: 1-10 Buttons already there");
					currentQuestionType = NCommand.QUESTION_TYPE_ONE_TO_TEN;
				}

				else if (currentQuestionType == NCommand.QUESTION_TYPE_YES_NO)
				{
					// out with the old
					oneToTen.remove(yes);
					oneToTen.remove(no);
					NGlobals.cPrint("Student Poll OTQ: Removing Y/N Buttons");
					// in with the new
					oneToTen.add(one);
					oneToTen.add(two);
					oneToTen.add(three);
					oneToTen.add(four);
					oneToTen.add(five);
					oneToTen.add(six);
					oneToTen.add(seven);
					oneToTen.add(eight);
					oneToTen.add(nine);
					oneToTen.add(ten);

					// refresh the options
					one.setEnabled(true);
					two.setEnabled(true);
					three.setEnabled(true);
					four.setEnabled(true);
					five.setEnabled(true);
					six.setEnabled(true);
					seven.setEnabled(true);
					eight.setEnabled(true);
					nine.setEnabled(true);
					ten.setEnabled(true); 

					one.setSelected(false);
					two.setSelected(false);
					three.setSelected(false);
					four.setSelected(false);
					five.setSelected(false);
					six.setSelected(false);
					seven.setSelected(false);
					eight.setSelected(false);
					nine.setSelected(false);
					ten.setSelected(false);	

					//refresh the gui
					oneToTen.revalidate();
					oneToTen.repaint();

					currentQuestionType = NCommand.QUESTION_TYPE_ONE_TO_TEN;
				}
				else if (currentQuestionType == NCommand.QUESTION_TYPE_A_TO_E) {

					// out with the old
					for(i=0;i<5;i++) {
						oneToTen.remove(ateBox[i]);
					}
					NGlobals.cPrint("Student Poll OTQ: Removing A-E Buttons");

					// in with the new
					oneToTen.add(one);
					oneToTen.add(two);
					oneToTen.add(three);
					oneToTen.add(four);
					oneToTen.add(five);
					oneToTen.add(six);
					oneToTen.add(seven);
					oneToTen.add(eight);
					oneToTen.add(nine);
					oneToTen.add(ten);
					NGlobals.cPrint("Student Poll OTQ: Adding 1-10 Buttons");

					// refresh the options
					one.setEnabled(true);
					two.setEnabled(true);
					three.setEnabled(true);
					four.setEnabled(true);
					five.setEnabled(true);
					six.setEnabled(true);
					seven.setEnabled(true);
					eight.setEnabled(true);
					nine.setEnabled(true);
					ten.setEnabled(true); 

					one.setSelected(false);
					two.setSelected(false);
					three.setSelected(false);
					four.setSelected(false);
					five.setSelected(false);
					six.setSelected(false);
					seven.setSelected(false);
					eight.setSelected(false);
					nine.setSelected(false);
					ten.setSelected(false);	

					//refresh the gui
					oneToTen.revalidate();
					oneToTen.repaint();

					currentQuestionType = NCommand.QUESTION_TYPE_ONE_TO_TEN;
				}
			}	 
			//set the question to be posed
			question.setText("<html><h2 style='color:black;font-size:125%'>" + toBePosed + "</h2></html>");
			NGlobals.cPrint("Setting toBePosed to: " + toBePosed);
		}
		else
		{
			NGlobals.cPrint("PollStudent says extraneous information");
		}
		NGlobals.cPrint("Leaving PollStudent handle method");      	 
	}


	// to make the compiler happy
	public void itemStateChanged(java.awt.event.ItemEvent ie){
	}

	public void actionPerformed(java.awt.event.ActionEvent ae)
	{
		int i;
		// changed to byte, removed subsequent casting **PT 6/26/12
		byte tCommand = 0;
		Object source = ae.getSource();

		int turnOff = 0; //Allows re-voting (turnOff = 0) 

		for (i=0;i<5;i++) {
			if (source == ateBox[i]) {
				ateBox[i].setSelected(true);
			}
			else {
				ateBox[i].setSelected(false);
			}
			ateBox[i].setEnabled(false);
		}

		if (source == yes)
		{
			yes.setSelected(true);
			no.setSelected(false);
			//Disable after you choose
			yes.setEnabled(false);
			no.setEnabled(false);

			//show results with color

			String tString = "yes";
			tCommand = NCommand.QUESTION_TYPE_YES_NO;
			int tLen = tString.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = tString.getBytes();
			pollSand.sendGrain(NAppID.STUDENT_POLL, tCommand, NDataType.BYTE, tLen, tStringAsBytes );
			NGlobals.cPrint("Sending Yes");
		}

		if (source == no)
		{
			yes.setSelected(false);
			no.setSelected(true);
			//Disable after you choose
			yes.setEnabled(false);
			no.setEnabled(false);
			//show results with color

			String tString = "no";
			tCommand = NCommand.QUESTION_TYPE_YES_NO;
			int tLen = tString.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = tString.getBytes();
			pollSand.sendGrain(NAppID.STUDENT_POLL, tCommand, NDataType.BYTE, tLen, tStringAsBytes );
			NGlobals.cPrint("Sending No");
		}

		if (source == one)
		{
			one.setSelected(true);
			two.setSelected(false);
			three.setSelected(false);
			four.setSelected(false);
			five.setSelected(false);
			six.setSelected(false);
			seven.setSelected(false);
			eight.setSelected(false);
			nine.setSelected(false);
			ten.setSelected(false);



			//show understanding with color

			if (turnOff == 1) {
				one.setEnabled(false);
				two.setEnabled(false);
				three.setEnabled(false);
				four.setEnabled(false);
				five.setEnabled(false);
				six.setEnabled(false);
				seven.setEnabled(false);
				eight.setEnabled(false);
				nine.setEnabled(false);
				ten.setEnabled(false);
			}

			//Eventually change to ints instead of string ***STK 6/18/12
			String tString = "1";
			tCommand = NCommand.QUESTION_TYPE_ONE_TO_TEN;
			int tLen = tString.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = tString.getBytes();
			pollSand.sendGrain(NAppID.STUDENT_POLL, tCommand, NDataType.BYTE, tLen, tStringAsBytes );
			NGlobals.cPrint("Sending 1");
		}

		if (source == two)
		{
			one.setSelected(false);
			two.setSelected(true);
			three.setSelected(false);
			four.setSelected(false);
			five.setSelected(false);
			six.setSelected(false);
			seven.setSelected(false);
			eight.setSelected(false);
			nine.setSelected(false);
			ten.setSelected(false);

			//show understanding with color

			if (turnOff == 1) {

				one.setEnabled(false);
				two.setEnabled(false);
				three.setEnabled(false);
				four.setEnabled(false);
				five.setEnabled(false);
				six.setEnabled(false);
				seven.setEnabled(false);
				eight.setEnabled(false);
				nine.setEnabled(false);
				ten.setEnabled(false);
			}

			String tString = "2";
			tCommand = NCommand.QUESTION_TYPE_ONE_TO_TEN;
			int tLen = tString.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = tString.getBytes();
			pollSand.sendGrain(NAppID.STUDENT_POLL, tCommand, NDataType.BYTE, tLen, tStringAsBytes );
			NGlobals.cPrint("Sending 2");

		}

		if (source == three)
		{
			one.setSelected(false);
			two.setSelected(false);
			three.setSelected(true);
			four.setSelected(false);
			five.setSelected(false);
			six.setSelected(false);
			seven.setSelected(false);
			eight.setSelected(false);
			nine.setSelected(false);
			ten.setSelected(false);

			//show understanding with color

			if (turnOff == 1) {
				one.setEnabled(false);
				two.setEnabled(false);
				three.setEnabled(false);
				four.setEnabled(false);
				five.setEnabled(false);
				six.setEnabled(false);
				seven.setEnabled(false);
				eight.setEnabled(false);
				nine.setEnabled(false);
				ten.setEnabled(false);
			}

			String tString = "3";
			tCommand = NCommand.QUESTION_TYPE_ONE_TO_TEN;
			int tLen = tString.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = tString.getBytes();
			pollSand.sendGrain(NAppID.STUDENT_POLL, tCommand, NDataType.BYTE, tLen, tStringAsBytes );
			NGlobals.cPrint("Sending 3");


		}

		if (source == four)
		{
			one.setSelected(false);
			two.setSelected(false);
			three.setSelected(false);
			four.setSelected(true);
			five.setSelected(false);
			six.setSelected(false);
			seven.setSelected(false);
			eight.setSelected(false);
			nine.setSelected(false);
			ten.setSelected(false);

			//show understanding with color

			if (turnOff == 1) {
				one.setEnabled(false);
				two.setEnabled(false);
				three.setEnabled(false);
				four.setEnabled(false);
				five.setEnabled(false);
				six.setEnabled(false);
				seven.setEnabled(false);
				eight.setEnabled(false);
				nine.setEnabled(false);
				ten.setEnabled(false);
			}
			String tString = "4";
			tCommand = NCommand.QUESTION_TYPE_ONE_TO_TEN;
			int tLen = tString.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = tString.getBytes();
			pollSand.sendGrain(NAppID.STUDENT_POLL, tCommand, NDataType.BYTE, tLen, tStringAsBytes );
			NGlobals.cPrint("Sending 4");


		}

		if (source == five)
		{
			one.setSelected(false);
			two.setSelected(false);
			three.setSelected(false);
			four.setSelected(false);
			five.setSelected(true);
			six.setSelected(false);
			seven.setSelected(false);
			eight.setSelected(false);
			nine.setSelected(false);
			ten.setSelected(false);

			//show understanding with color

			if (turnOff == 1) {
				one.setEnabled(false);
				two.setEnabled(false);
				three.setEnabled(false);
				four.setEnabled(false);
				five.setEnabled(false);
				six.setEnabled(false);
				seven.setEnabled(false);
				eight.setEnabled(false);
				nine.setEnabled(false);
				ten.setEnabled(false);
			}
			String tString = "5";
			tCommand = NCommand.QUESTION_TYPE_ONE_TO_TEN;
			int tLen = tString.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = tString.getBytes();
			pollSand.sendGrain(NAppID.STUDENT_POLL, tCommand, NDataType.BYTE, tLen, tStringAsBytes );
			NGlobals.cPrint("Sending 5");


		}

		if (source == six)
		{
			one.setSelected(false);
			two.setSelected(false);
			three.setSelected(false);
			four.setSelected(false);
			five.setSelected(false);
			six.setSelected(true);
			seven.setSelected(false);
			eight.setSelected(false);
			nine.setSelected(false);
			ten.setSelected(false);

			//show understanding with color

			if (turnOff == 1) {
				one.setEnabled(false);
				two.setEnabled(false);
				three.setEnabled(false);
				four.setEnabled(false);
				five.setEnabled(false);
				six.setEnabled(false);
				seven.setEnabled(false);
				eight.setEnabled(false);
				nine.setEnabled(false);
				ten.setEnabled(false);
			}
			String tString = "6";
			tCommand = NCommand.QUESTION_TYPE_ONE_TO_TEN;
			int tLen = tString.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = tString.getBytes();
			pollSand.sendGrain(NAppID.STUDENT_POLL, tCommand, NDataType.BYTE, tLen, tStringAsBytes );
			NGlobals.cPrint("Sending 6");

		}

		if (source == seven)
		{
			one.setSelected(false);
			two.setSelected(false);
			three.setSelected(false);
			four.setSelected(false);
			five.setSelected(false);
			six.setSelected(false);
			seven.setSelected(true);
			eight.setSelected(false);
			nine.setSelected(false);
			ten.setSelected(false);

			//show understanding with color

			if (turnOff == 1) {
				one.setEnabled(false);
				two.setEnabled(false);
				three.setEnabled(false);
				four.setEnabled(false);
				five.setEnabled(false);
				six.setEnabled(false);
				seven.setEnabled(false);
				eight.setEnabled(false);
				nine.setEnabled(false);
				ten.setEnabled(false);
			}
			String tString = "7";
			tCommand = NCommand.QUESTION_TYPE_ONE_TO_TEN;
			int tLen = tString.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = tString.getBytes();
			pollSand.sendGrain(NAppID.STUDENT_POLL, tCommand, NDataType.BYTE, tLen, tStringAsBytes );
			NGlobals.cPrint("Sending 7");

		}

		if (source == eight)
		{
			one.setSelected(false);
			two.setSelected(false);
			three.setSelected(false);
			four.setSelected(false);
			five.setSelected(false);
			six.setSelected(false);
			seven.setSelected(false);
			eight.setSelected(true);
			nine.setSelected(false);
			ten.setSelected(false);

			//show understanding with color

			if (turnOff == 1) {
				one.setEnabled(false);
				two.setEnabled(false);
				three.setEnabled(false);
				four.setEnabled(false);
				five.setEnabled(false);
				six.setEnabled(false);
				seven.setEnabled(false);
				eight.setEnabled(false);
				nine.setEnabled(false);
				ten.setEnabled(false);
			}
			String tString = "8";
			tCommand = NCommand.QUESTION_TYPE_ONE_TO_TEN;
			int tLen = tString.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = tString.getBytes();
			pollSand.sendGrain(NAppID.STUDENT_POLL, tCommand, NDataType.BYTE, tLen, tStringAsBytes );
			NGlobals.cPrint("Sending 8");

		}

		if (source == nine)
		{
			one.setSelected(false);
			two.setSelected(false);
			three.setSelected(false);
			four.setSelected(false);
			five.setSelected(false);
			six.setSelected(false);
			seven.setSelected(false);
			eight.setSelected(false);
			nine.setSelected(true);
			ten.setSelected(false);


			if (turnOff == 1) {
				one.setEnabled(false);
				two.setEnabled(false);
				three.setEnabled(false);
				four.setEnabled(false);
				five.setEnabled(false);
				six.setEnabled(false);
				seven.setEnabled(false);
				eight.setEnabled(false);
				nine.setEnabled(false);
				ten.setEnabled(false);
			}

			//show understanding with color

			String tString = "9";
			tCommand = NCommand.QUESTION_TYPE_ONE_TO_TEN;
			int tLen = tString.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = tString.getBytes();
			pollSand.sendGrain(NAppID.STUDENT_POLL, tCommand, NDataType.BYTE, tLen, tStringAsBytes );
			NGlobals.cPrint("Sending 9");

		}

		if (source == ten)
		{
			one.setSelected(false);
			two.setSelected(false);
			three.setSelected(false);
			four.setSelected(false);
			five.setSelected(false);
			six.setSelected(false);
			seven.setSelected(false);
			eight.setSelected(false);
			nine.setSelected(false);
			ten.setSelected(true);

			//show understanding with color

			if (turnOff == 1) {
				one.setEnabled(false);
				two.setEnabled(false);
				three.setEnabled(false);
				four.setEnabled(false);
				five.setEnabled(false);
				six.setEnabled(false);
				seven.setEnabled(false);
				eight.setEnabled(false);
				nine.setEnabled(false);
				ten.setEnabled(false);
			}
			String tString = "10";
			tCommand = NCommand.QUESTION_TYPE_ONE_TO_TEN;
			int tLen = tString.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = tString.getBytes();
			pollSand.sendGrain(NAppID.STUDENT_POLL, tCommand, NDataType.BYTE, tLen, tStringAsBytes );
			NGlobals.cPrint("Sending 10");

		}		  		
	}	
}
