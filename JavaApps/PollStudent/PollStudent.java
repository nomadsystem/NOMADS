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
import nomads.technosonics.*;
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

	//the question to pose. taken from teacher's app.
	String toBePosed = "yy";

	String temp = "";

	String currentQuestionType = "";

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
		int i;
		NGlobals.cPrint("****************");
		NGlobals.cPrint("socketpoll handle method");
		SANDnumber = sandByte;
		if (globals.clientDebugLevel > 0) 
		{NGlobals.cPrint("...");} 
		NGlobals.cPrint("sandByte: " + sandByte);

		temp = "";
		toBePosed = "xx";
		String questionType = "";
		//String qTypeANDtoBePosed = "";
		if (globals.clientDebugLevel > 0) 
		{NGlobals.cPrint("...");} 
		NGlobals.cPrint("s " + s);

		if (previousQuestion.equals(s)) {
			return;
		}

		previousQuestion = new String(s);

		//parse the SANDnumber and qTypeANDtoBePosed
		/*for (int i = 0; i < s.length(); i++)
	  {
	  temp = s.substring(i,i+1);
	  if (temp.equalsIgnoreCase(":"))
	  {
	  //SANDnumber = s.substring(i+2, i+4);
	  qTypeANDtoBePosed = s.substring(i+4);
	  break;
	  }
	  }

	  NGlobals.cPrint("SANDnumber " + SANDnumber);
	  NGlobals.cPrint("qTypeANDtoBePosed " + qTypeANDtoBePosed);
		 */
		//temp = "";

		if (SANDnumber == app_id.INSTRUCTOR_PANEL) {

			if (s.equalsIgnoreCase("VOTE")) {
				if (currentQuestionType.equalsIgnoreCase("Yes-No")) {
					yes.setEnabled(true);
					no.setEnabled(true);

					yes.setSelected(false);
					no.setSelected(false);

					oneToTen.revalidate();
					oneToTen.repaint();
				}
				if (currentQuestionType.equalsIgnoreCase("Scale of 1 to 10")) {
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
		if (SANDnumber == app_id.TEACHER_POLL)
		{	 
			// parse the questionType and the question toBePosed from String s
			for (int j = 0; j < s.length(); j++)
			{
				temp = s.substring(j,j+1);
				if (temp.equalsIgnoreCase(";"))
				{
					questionType = s.substring(0,j);
					toBePosed = s.substring(j+1);
					break;
				}
			}
			if (globals.clientDebugLevel > 0) 
			{NGlobals.cPrint("...");} 
			NGlobals.cPrint("questionType " + questionType);
			if (globals.clientDebugLevel > 0) 
			{NGlobals.cPrint("...");} 
			NGlobals.cPrint("toBePosed " + toBePosed);


			//set the question type ===========================================--------------

			if (questionType.equalsIgnoreCase("A through E"))  // +++++++++++++++++++++++++++
			{
				if (currentQuestionType.equalsIgnoreCase(""))
				{
					// gui is blank
					// add the options
					for(i=0;i<5;i++) {
						oneToTen.add(ateBox[i]);
						ateBox[i].setEnabled(true);
						ateBox[i].setSelected(false);
					}


					//refresh the gui
					oneToTen.revalidate();
					oneToTen.repaint();

					currentQuestionType = "A through E";
				}

				if (currentQuestionType.equalsIgnoreCase(questionType))
				{
					// gui is already set to display yes no question type

					for(i=0;i<5;i++) {
						oneToTen.add(ateBox[i]);
						ateBox[i].setEnabled(true);
						ateBox[i].setSelected(false);
					}

				}

				if (currentQuestionType.equalsIgnoreCase("Scale of 1 to 10"))
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

					//refresh the gui
					oneToTen.revalidate();
					oneToTen.repaint();

					currentQuestionType = "A through E";
				}
				if (currentQuestionType.equalsIgnoreCase("Yes-No"))
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

					//refresh the gui
					oneToTen.revalidate();
					oneToTen.repaint();

					currentQuestionType = "A through E";
				}

			}


			if (questionType.equalsIgnoreCase("Yes-No"))  // +++++++++++++++++++++++++++
			{
				if (currentQuestionType.equalsIgnoreCase(""))
				{
					// gui is blank
					// add the options
					oneToTen.add(yes);
					oneToTen.add(no);

					yes.setEnabled(true);
					no.setEnabled(true);

					// refresh the options
					yes.setSelected(false);
					no.setSelected(false);

					//refresh the gui
					oneToTen.revalidate();
					oneToTen.repaint();

					currentQuestionType = "Yes-No";
				}

				if (currentQuestionType.equalsIgnoreCase(questionType))
				{
					// gui is already set to display yes no question type

					// refresh the options
					yes.setEnabled(true);
					no.setEnabled(true);
					yes.setSelected(false);
					no.setSelected(false);

				}

				if (currentQuestionType.equalsIgnoreCase("Scale of 1 to 10"))
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
					oneToTen.add(yes);
					oneToTen.add(no);

					// refresh the options
					yes.setEnabled(true);
					no.setEnabled(true);
					yes.setSelected(false);
					no.setSelected(false);

					//refresh the gui
					oneToTen.revalidate();
					oneToTen.repaint();

					currentQuestionType = "Yes-No";
				}

				if (currentQuestionType.equalsIgnoreCase("A through E")) {

					// out with the old
					for(i=0;i<5;i++) {
						oneToTen.remove(ateBox[i]);
					}

					// in with the new
					oneToTen.add(yes);
					oneToTen.add(no);

					// refresh the options
					yes.setEnabled(true);
					no.setEnabled(true);
					yes.setSelected(false);
					no.setSelected(false);

					//refresh the gui
					oneToTen.revalidate();
					oneToTen.repaint();

					currentQuestionType = "Yes-No";
				}

			}

			if (questionType.equalsIgnoreCase("Scale of 1 to 10"))  // +++++++++++++++++++++++++++
			{
				if (currentQuestionType.equalsIgnoreCase(""))
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

					currentQuestionType = "Scale of 1 to 10";
				}

				if (currentQuestionType.equalsIgnoreCase(questionType))
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
				}

				if (currentQuestionType.equalsIgnoreCase("Yes-No"))
				{
					// out with the old
					oneToTen.remove(yes);
					oneToTen.remove(no);

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

					currentQuestionType = "Scale of 1 to 10";
				}
				if (currentQuestionType.equalsIgnoreCase("A through E")) {

					// out with the old
					for(i=0;i<5;i++) {
						oneToTen.remove(ateBox[i]);
					}

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

					currentQuestionType = "Scale of 1 to 10";
				}
			}	 
			//set the question to be posed
			question.setText("<html><h2 style='color:black;font-size:125%'>" + toBePosed + "</h2></html>");
			globals.cPrint("Setting toBePosed to: " + toBePosed);
		}
		else
		{
			NGlobals.cPrint("Socketpoll says extraneous information");
		}
		NGlobals.cPrint("Leaving socketpoll handle method");      	 
	}


	// to make the compiler happy
	public void itemStateChanged(java.awt.event.ItemEvent ie){
	}

	public void actionPerformed(java.awt.event.ActionEvent ae)
	{
		int i;
		Object source = ae.getSource();

		int turnOff = 1;

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

			int tCommand = NCommand.SEND_MESSAGE; //CHANGE ME

			int tQuestionType = qType.getSelectedIndex();
			
			if (tQuestionType == 0) //refers to JComboBox index number
				tCommand = NCommand.SEND_MESSAGE;	// Replace with QUESTION_TYPE_YES_NO;
			else if (tQuestionType == 1)
				tCommand = NCommand.SEND_MESSAGE;	// Replace with QUESTION_TYPE_ONE_TO_TEN;
			else 
				NGlobals.cPrint("Invalid question type specified");

			String tString = question.getText();
			int tLen = tString.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = tString.getBytes();
			pollSand.sendGrain((byte)NAppID.TEACHER_POLL, (byte)tCommand, (byte)NDataType.BYTE, tLen, tStringAsBytes );
			
			try
			{	
				NGlobals.cPrint("WRITING YES");
				streamOut.writeByte(app_id.STUDENT_POLL);
				streamOut.writeUTF("yes");
				streamOut.flush();
				NGlobals.cPrint("IT WAS WRITTEN");
			}
			catch (IOException ioe)
			{
			}
		}

		if (source == no)
		{
			yes.setSelected(false);
			no.setSelected(true);
			//Disable after you choose
			yes.setEnabled(false);
			no.setEnabled(false);
			//show results with color

			try
			{	
				NGlobals.cPrint("WRITING NO");
				streamOut.writeByte(app_id.STUDENT_POLL);
				streamOut.writeUTF("no");
				streamOut.flush();
				NGlobals.cPrint("IT WAS WRITTEN");
			}
			catch (IOException ioe)
			{
			}
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

			try
			{
				streamOut.writeByte(app_id.STUDENT_POLL);
				streamOut.writeUTF("1");
				streamOut.flush();
			}
			catch (IOException ioe)
			{   
			}
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
			try
			{
				streamOut.writeByte(app_id.STUDENT_POLL);
				streamOut.writeUTF("2");
				streamOut.flush();
			}
			catch (IOException ioe)
			{   
			}

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
			try
			{
				streamOut.writeByte(app_id.STUDENT_POLL);
				streamOut.writeUTF("3");
				streamOut.flush();
			}
			catch (IOException ioe)
			{   
			}

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
			try
			{
				streamOut.writeByte(app_id.STUDENT_POLL);
				streamOut.writeUTF("4");
				streamOut.flush();
			}
			catch (IOException ioe)
			{   
			}

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
			try
			{
				streamOut.writeByte(app_id.STUDENT_POLL);
				streamOut.writeUTF("5");
				streamOut.flush();
			}
			catch (IOException ioe)
			{   
			}

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
			try
			{
				streamOut.writeByte(app_id.STUDENT_POLL);
				streamOut.writeUTF("6");
				streamOut.flush();
			}
			catch (IOException ioe)
			{   
			}

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
			try
			{
				streamOut.writeByte(app_id.STUDENT_POLL);
				streamOut.writeUTF("7");
				streamOut.flush();
			}
			catch (IOException ioe)
			{   
			}

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
			try
			{
				streamOut.writeByte(app_id.STUDENT_POLL);
				streamOut.writeUTF("8");
				streamOut.flush();
			}
			catch (IOException ioe)
			{   
			}

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

			try
			{
				streamOut.writeByte(app_id.STUDENT_POLL);
				streamOut.writeUTF("9");
				streamOut.flush();
			}
			catch (IOException ioe)
			{   
			}

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
			try
			{
				streamOut.writeByte(app_id.STUDENT_POLL);
				streamOut.writeUTF("10");
				streamOut.flush();
			}
			catch (IOException ioe)
			{   
			}

		}		  		
	}	
}
