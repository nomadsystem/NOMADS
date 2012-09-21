import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JToggleButton;
import javax.swing.DefaultButtonModel;
import java.util.*;
import java.net.*;
import java.io.*;
import java.applet.*;
import java.util.Calendar;
import java.util.Date.*;
import java.util.Random;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import netscape.javascript.*; 

import nomads.v210.*;

// STUDENT SEQUENCER

public class SoundMosaicPanel extends JApplet implements ActionListener, Runnable
{


	private static int maxAudioClips = 1000;
	private static int cols = 24;
	private static int fileOffset = 7;
	private static int pulseCols = cols + fileOffset;

	//    private static int rows = maxAudioClips;
	private static int rows = 25;
	private static String sFiles[];
	ArrayList<String> fileLister;
	private int numFiles = 0;
	private static int maxFiles = 1000;

	// specific to my app
	JFrame frame;
	Thread runner;
	private AudioClip sounds[];
	private String sfName[];
	private JToggleButton buttons[][];
	private    JComboBox fileButtons[];
	private    JComboBox rhythmButtons[];
	private JButton phaseButtons[];

	JPanel filePanel, rhythmPanel, phasePanel;

	private JLabel pulses[];

	private boolean on[][];
	private JButton start, getFiles,allOff,randAll,randAud,syncTime;
	private JComboBox autoRhythm;

	private URL webBase;
	int tBpm, beatSync, tempoSync,newBeat,timeCtr;
	private int wait;
	long startT,nowT,diffT, syncT, offT,serverT,avgDiffT,sumT,calcT,instructorT,lagT;
	Calendar cal;
	JTextField tempoText;
	JSlider tempoSlide;
	static int run;
	Random randNum;
	int startTempo = 180;
	Boolean dBug = false;
	Boolean hBug = true;
	Boolean sortFiles = true;
	Boolean syncIt = false;

	private Boolean SOOC = true;
	private Boolean STC = true;
	private Boolean SPC = true;
	private Boolean SFS = true;
	private Boolean SBC = false;
	private Boolean IBC = false;

	public void setWait(int w) {
		wait = w;
	}

	public int getWait() {
		return wait;
	}

	public void print(String str) {
		if (dBug) {
			System.out.println(str);
		}
	}

	public void hPrint(String str) {
		if (hBug) {
			System.out.println(">" + str);
		}
	}


	public void createButtons(Container pane) {
		int r,m,i,j;
		r=m=i=j=0;
		buttons = new JToggleButton[rows][cols];
		pulses = new JLabel[pulseCols];
		on = new boolean[rows][cols];
		fileButtons = new JComboBox[rows];
		rhythmButtons = new JComboBox[rows];
		phaseButtons = new JButton[rows];

		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

		JPanel buttonWrapper = new JPanel();
		buttonWrapper.setLayout(new FlowLayout(FlowLayout.CENTER));

		filePanel = new JPanel();
		filePanel.setLayout(new GridLayout(rows,1));
		filePanel.setPreferredSize(new Dimension((int)110,(int)500));

		if (dBug)
			print("create_buttons():  numFiles = " + numFiles);
		j=0;
		for (i=0;i<rows;i++) {
			JComboBox fileList = new JComboBox();
			fileList.addActionListener(fileSelectListener);
			filePanel.add(fileList);
			//	    fileList.setSelectedIndex(0);
			fileButtons[i] = fileList;
		}
		buttonWrapper.add(filePanel);

		rhythmPanel = new JPanel();
		rhythmPanel.setLayout(new GridLayout(rows,1));
		rhythmPanel.setPreferredSize(new Dimension((int)70,(int)500));

		String rhythms[] = new String[cols];
		for (i=0;i<cols;i++) {
			rhythms[i] = Integer.toString(i);
		}

		for (i=0;i<rows;i++) {
			JComboBox autoRhythm = new JComboBox(rhythms);
			autoRhythm.setSelectedIndex(0);
			autoRhythm.addActionListener(autoRhythmListener);
			rhythmPanel.add(autoRhythm);
			rhythmButtons[i] = autoRhythm;
		}

		phasePanel = new JPanel();
		phasePanel.setLayout(new GridLayout(rows,1));
		phasePanel.setPreferredSize(new Dimension((int)30,(int)500));

		j=0;
		for (i=0;i<rows;i++) {
			JButton phaser = new JButton("+");
			phasePanel.add(phaser);
			phaser.addActionListener(phaseListener);
			//	    fileList.setSelectedIndex(0);
			phaseButtons[i] = phaser;
		}

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(rows,cols));
		//buttonPanel.setBackground(Color.black);
		buttonPanel.setPreferredSize(new Dimension((int)720,(int)500));

		JPanel pulseWrapper = new JPanel();
		pulseWrapper.setLayout(new FlowLayout(FlowLayout.CENTER));

		JPanel pulsePanel = new JPanel();
		pulsePanel.setLayout(new GridLayout(1,pulseCols));
		pulsePanel.setPreferredSize(new Dimension(930,10));
		for(i=0;i<pulseCols; i++) {
			pulses[i] = new JLabel(" ");
			pulses[i].setPreferredSize(new Dimension(1,1));
			pulses[i].setOpaque(true);
			if (i>=fileOffset)
				pulses[i].setBackground(Color.gray);
			pulsePanel.add(pulses[i], "align center, height 1");
		}
		pulsePanel.setBackground(Color.black);

		Font buttonFont = new Font("Courier", Font.PLAIN,8);

		for (i=0;i<rows;i++) {
			r = 1+randNum.nextInt(12);
			m = 1+randNum.nextInt(12);

			for(j=0;j<cols; j++) {
				buttons[i][j] = new JToggleButton(" ");
				buttons[i][j].setContentAreaFilled(false);
				buttons[i][j].setOpaque(true);
				buttons[i][j].setFont(buttonFont);
				//	    pulses[i].setPreferredSize(new Dimension(1,1));
				buttons[i][j].setContentAreaFilled(false);
				buttons[i][j].addActionListener(this);
				buttons[i][j].addChangeListener(changeListener);
				buttonPanel.add(buttons[i][j]);
			}
		}

		JPanel  controls = new JPanel();
		controls.setLayout(new FlowLayout(FlowLayout.CENTER));
		JLabel title = new JLabel("Tempo (bpm)");
		controls.add(title);
		startTempo += (randNum.nextInt(60));

		tempoText = new JTextField(Integer.toString(startTempo),3);

		tempoText.addActionListener(this);
		controls.add(tempoText);
		start = new JButton("Start/Stop");
		start.addActionListener(this);
		start.setEnabled(true);
		//	getFiles = new JButton("Get Audiofiles");
		//	getFiles.addActionListener(getFilesListener);
		randAll = new JButton("Random Pattern");
		randAll.addActionListener(randButListener);

		randAud = new JButton("Random Audio");
		randAud.addActionListener(randAudListener);

		allOff = new JButton("All Off");
		allOff.addActionListener(allOffListener);
		allOff.setEnabled(false);
		// syncTime = new JButton("Sync Time");
		// syncTime.addActionListener(syncTimeListener);

		controls.add(start);
		//	controls.add(getFiles);
		controls.add(randAll);
		controls.add(randAud);
		controls.add(allOff);
		//	controls.add(syncTime);

		tempoSlide = new JSlider(JSlider.HORIZONTAL,
				1, 600, startTempo);
		tempoSlide.setMajorTickSpacing(10);
		tempoSlide.setPaintTicks(true);

		Hashtable labelTable = new Hashtable();
		labelTable.put( new Integer( 1 ), new JLabel("1") );
		labelTable.put( new Integer( 600/2 ), new JLabel("300") );
		labelTable.put( new Integer( 600 ), new JLabel("600") );
		tempoSlide.setLabelTable( labelTable );

		tempoSlide.setPaintLabels(true);
		tempoSlide.addChangeListener(sliderListener);

		buttonWrapper.add(phasePanel);
		buttonWrapper.add(rhythmPanel);
		buttonWrapper.add(buttonPanel);


		pane.add(buttonWrapper);
		pulseWrapper.add(pulsePanel);
		pane.add(pulseWrapper);
		//	pane.add(tempoSlide);
		pane.add(controls);
	}

	// xxx

	private static void addAButton(String text, Container container) {
		JButton button = new JButton(text);
		//        button.setAlignmentX(Component.CENTER_ALIGNMENT);
		// button.setAlignmentY(Component.CENTER_ALIGNMENT);
		container.add(button);
	}

	public void initSounds() {
		for (int i=0;i<rows;i++) {
			for(int j=0;j<cols; j++) {
				on[i][j] = false;
			}
		}
		int i=0;
	}


	public void run() {
		int i,j,t;
		i=j=0;
		print("I'm running!");
		while(true) {
			try {
				if (run == 1) {
					//			print(".");
					for (j=0;j<cols;j++) {
						for (i=0;i<rows;i++) {
							if (on[i][j]) {
								sounds[i].play();
							}
							else {
							}
							if (run == 0) {
								i = rows-1;
								j = cols-1;
							}
						}
						if (beatSync > 0) {
							j = newBeat;
							cal = Calendar.getInstance();
							nowT = cal.getTimeInMillis();
							//diffT = nowT-startT;
							//offT = diffT-syncT;
							//offT /= 10;
							beatSync = -1;
						}
						pulses[j+fileOffset].setBackground(Color.RED);
						if (tempoSync > 0) {
							setWait(60000/tBpm);
							tempoSync = -1;
						}
						if (syncIt) {
							runner.sleep(getWait() + lagT);
							syncIt = false;
						}
						else {
							runner.sleep(getWait());
						}
						//offT = 0;
						pulses[j+fileOffset].setBackground(Color.gray);
					}
				}
			}
			catch (InterruptedException ie) {}
		}
	}

	public void start() {
		runner = new Thread(this);
		runner.start();
	}

	public void init()
	{
		int i;
		setWait(180);
		run = 0;
		randNum = new Random();
		sounds = new AudioClip[maxAudioClips];
		sfName = new String[maxAudioClips];
		sFiles = new String[maxFiles];
		fileLister = new ArrayList<String>();
		on = new boolean[rows][cols];
		cal = Calendar.getInstance();
		startT = cal.getTimeInMillis();
		beatSync = -1;
		tempoSync = -1;
		newBeat = 0;
		
		//Display the window.
		//        frame.pack();
		//        frame.setVisible(true);
		initSounds();
		createButtons(getContentPane());

		cal = Calendar.getInstance();
		nowT = cal.getTimeInMillis();
		avgDiffT = 0;
		sumT = 0;
		timeCtr = 0;
//		for (i=0;i<3;i++) {
//			try {
//				streamOut.writeByte(app_id.STUDENT_SEQUENCER);
//				String tempString = new String("T");
//				streamOut.writeUTF(tempString);
//				hPrint("tempString:" + tempString);
//				hPrint("Sending sync");
//			}
//			catch(IOException ioe) {
//				hPrint("Error writing...");
//			}
//		}
//		try {
//			streamOut.writeByte(app_id.STUDENT_SEQUENCER);
//			streamOut.writeUTF("F");
//			start.setEnabled(true);
//			allOff.setEnabled(true);
//		}
//		catch(IOException ioe) {
//			hPrint("Error writing...");
//		}
	}

	// BEGIN can cut and paste from here ....


	public boolean isNumber(String in) {
		try {
			Integer.parseInt(in);

		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}


	// END cut and paste
	// Takes information from the server
	public void handle()
	{
		int incCmd, incNBlocks, incDType, incDLen;
		int i,j;
		int incIntData[] = new int[1000];
		byte incByteData[] = new byte[1000];  // Cast as chars here because we're using chars -> strings
		NGrain grain;

		NGlobals.cPrint("DiscussClient -> handle()");

		// grain = mosaicSand.getGrain();
		// grain.print(); //prints grain data to console
		// String msg = new String(grain.bArray);
		String msg = new String("foo");

		int k,t, fc, sc,tRow,tCol;
		String temp,fileName,stringTime;
		fc=0;
		sc=0;
		// NUMBER = tempo
		// DONE reading files ... load them into the program
		
		
		if (msg.equals("D")) {
			hPrint("D = done getting file list, load 'em up");
			if (sortFiles)
				Collections.sort(fileLister,String.CASE_INSENSITIVE_ORDER);
			for (i=0;i<numFiles;i++) {
				sFiles[i] = fileLister.get(i);
			}
			k=0;
			for (i=0;i<rows;i++) {
				fileButtons[i].removeAllItems();
				for (j=0;j<numFiles;j++) {
					fileButtons[i].addItem(sFiles[j]);
				}
				fileButtons[i].setSelectedIndex(k++);
				if (k >= numFiles) {
					k = 0;
				}
			}
			fileLister.clear();
		}
		else if (msg.equals("N")) {
			numFiles = 0;
			fileLister.clear();
			sortFiles = false;
		}
		else if (isNumber(msg)) {
			hPrint("# = tempo");
			t = (int)Integer.parseInt(msg);
			// if (t >= 1000) {
			// 	beatSync = 1;
			// 	syncT = t;
			// }
			if (t < 0) {
				beatSync = 1;
				newBeat = -1*t;
				hPrint("newBeat = " + newBeat);
			}
			else if ((t < 600) && (t > 0)) {
				tempoSync = 1;
				tBpm = t;
				hPrint("bpm = " + msg);
				setWait(60000/t);
				tempoText.setText(msg);
				tempoSlide.setValue(t);
			}
		}
		// STOP
		else if (msg.equals("msg")) {
			run = 0;
			hPrint("msg = stop");
		}
		// RUN
		else if (msg.equals("R")) {
			run = 1;
			cal = Calendar.getInstance();
			startT = cal.getTimeInMillis();
			hPrint("R = run");
		}
		// START/STOP CONTROL
		else if (msg.equals("SOOCON")) {
			SOOC = true;
			start.setEnabled(false);
		}
		else if (msg.equals("SOOCOFF")) {
			SOOC = false;
			start.setEnabled(false);
		}
		// TEMPO CONTROL
		else if (msg.equals("STCON")) {
			STC = true;
			tempoSlide.setEnabled(true);
		}
		else if (msg.equals("STCOFF")) {
			STC = false;
			tempoSlide.setEnabled(false);
		}
		// LOCAL PATTERN CONTROL
		else if (msg.equals("SPCON")) {
			SPC = true;
			for (i=0;i<rows;i++) {			
				phaseButtons[i].setEnabled(true);
				rhythmButtons[i].setEnabled(true);
				for(j=0;j<cols; j++) {
					buttons[i][j].getModel().setArmed(true);
				}
			}
		}
		else if (msg.equals("SPCOFF")) {
			SPC = false;
			for (i=0;i<rows;i++) {			
				phaseButtons[i].setEnabled(false);
				rhythmButtons[i].setEnabled(false);
				for(j=0;j<cols; j++) {
					buttons[i][j].getModel().setArmed(false);
				}
			}
		}
		// FILE SELECT
		else if (msg.equals("SFSON")) {
			SFS = true;
			for (i=0;i<rows;i++) {
				fileButtons[i].setEnabled(true);
			}
		}
		else if (msg.equals("SFSOFF")) {
			SFS = false;
			for (i=0;i<rows;i++) {
				fileButtons[i].setEnabled(false);
			}
		}
		// STUDENT BUTTON CONTROL
		else if (msg.equals("SBCON")) {
			SBC = true;
		}
		else if (msg.equals("SBCOFF")) {
			SBC = false;
		}
		// INSTRUCTOR BUTTON CONTROL
		else if (msg.equals("IBCON")) {
			IBC = true;
		}
		else if (msg.equals("IBCOFF")) {
			IBC = false;
		}
		// MESSAGE PASSING STRINGS ++++++++++++++++++++++
		else if (msg.length() > 1) {
			temp = msg.substring(0,2);
			// FILENAME
			if (temp.equals("T:")) {
				stringTime = msg.substring(2,msg.length());
				serverT = Long.parseLong(stringTime);
				hPrint("server time (serverT)" + serverT);
				diffT = serverT-nowT;
				hPrint("difference between server and nowT (SSeq):" + diffT);
				sumT += diffT;
				timeCtr++;
				avgDiffT = sumT/timeCtr;
				hPrint("average time difference (serverT-nowT)" + avgDiffT);
				calcT = nowT+avgDiffT;
				hPrint("what time I (SSeq) think the server time is (calcT):" + calcT);
			}
			if (temp.equals("X:")) {
				cal = Calendar.getInstance();
				nowT = cal.getTimeInMillis();
				hPrint("SSeq app time (nowT): " + nowT);
				stringTime = msg.substring(2,msg.length());
				instructorT = Long.parseLong(stringTime);
				hPrint("ISeq time (recvd from server) (instructorT): " + instructorT);
				calcT = nowT+avgDiffT;
				hPrint("what time I (SSeq) think the server time is (calcT):" + calcT);
				lagT = instructorT - calcT;
				hPrint("difference between ISeq and SSeq version of server time (lagT):" + lagT);
			}
			else if (temp.equals("F:")) {
				hPrint("F: = filename");
				fileName = msg.substring(2,msg.length());
				fileLister.add(fileName);
				numFiles++;
				hPrint("File: " + msg);
			}
			// SYNC:  first set all off
			else if (temp.equals("msg:")) {
				hPrint("msg: = set all cells off");
				for (i=0;i<rows;i++) {
					for (j=0;j<cols;j++) {
						buttons[i][j].getModel().setArmed(false);
						buttons[i][j].getModel().setSelected(false);
						buttons[i][j].getModel().setPressed(false);
						//		buttons[i].getModel().setArmed(false);
						on[i][j] = false;
					}
				}
			}
			// SYNC:  specific buttons, one P: at a time
			else if (temp.equals("P:")) {
				hPrint("P: = patter(x,y) on");
				fc = msg.indexOf(":");
				sc = msg.indexOf(":", fc+1);
				print("fc = " + fc);
				print("sc = " + sc);
				temp = msg.substring(2,sc);
				tRow = (int)Integer.parseInt(temp);
				print("temp " + temp);
				temp = msg.substring(sc+1,msg.length());
				tCol = (int)Integer.parseInt(temp);
				print("temp " + temp);

				buttons[tRow][tCol].getModel().setArmed(true);
				buttons[tRow][tCol].getModel().setSelected(true);
				buttons[tRow][tCol].getModel().setPressed(true);
				//		buttons[i].getModel().setArmed(false);
				on[tRow][tCol] = true;
			}
			else if (temp.equals("O:")) {
				hPrint("O: = patter(x,y) off");
				fc = msg.indexOf(":");
				sc = msg.indexOf(":", fc+1);
				print("fc = " + fc);
				print("sc = " + sc);
				temp = msg.substring(2,sc);
				tRow = (int)Integer.parseInt(temp);
				print("temp " + temp);
				temp = msg.substring(sc+1,msg.length());
				tCol = (int)Integer.parseInt(temp);
				print("temp " + temp);

				buttons[tRow][tCol].getModel().setArmed(false);
				buttons[tRow][tCol].getModel().setSelected(false);
				buttons[tRow][tCol].getModel().setPressed(false);
				//		buttons[i].getModel().setArmed(false);
				on[tRow][tCol] = false;
			}
		}
		// END MESSAGE PASSING STRINGS ------------------
		else {
			hPrint("Unknown input from server: " + msg);
		}
	}

	// Action listeners **************************************************************************************

	ActionListener allOffListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			int i,j;
			for (i=0;i<rows;i++) {
				for (j=0;j<cols;j++) {
					buttons[i][j].getModel().setArmed(false);
					buttons[i][j].getModel().setSelected(false);
					buttons[i][j].getModel().setPressed(false);
					//		buttons[i].getModel().setArmed(false);
					on[i][j] = false;
					rhythmButtons[i].setSelectedIndex(0);
				}
			}
		}
	};

	ActionListener syncTimeListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			cal = Calendar.getInstance();
			nowT = cal.getTimeInMillis();
			String tempString = new String("T");
			
			int tLen = tempString.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = tempString.getBytes();
			
			// mosaicSand.sendGrain((byte)NAppID.STUDENT_SEQUENCER, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.BYTE, tLen, tStringAsBytes );
		}
	};

	ActionListener phaseListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			int i,j,c=0;
			JButton b = (JButton)e.getSource();
			print("phase");
			Boolean tBut[];

			tBut = new Boolean[cols];

			for (i=0;i<rows;i++) {
				if (b == phaseButtons[i]) {
					print("row = " + i);
					for (j=0;j<cols;j++) {
						if ((j+1) == cols) {
							c = 0;
						}
						else
							c = j+1;
						if (on[i][j])
							tBut[c] = true;
						else
							tBut[c] = false;
					}
					for (j=0;j<cols;j++) {
						if (tBut[j]) {
							on[i][j] = true;
							buttons[i][j].getModel().setArmed(true);
							buttons[i][j].getModel().setSelected(true);
							buttons[i][j].getModel().setPressed(true);
						}				
						else  {
							on[i][j] = false;
							buttons[i][j].getModel().setArmed(false);
							buttons[i][j].getModel().setSelected(false);
							buttons[i][j].getModel().setPressed(false);
						}				
					}
				}
			}
		}
	};


	ActionListener autoRhythmListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			int m,r,i,j;
			JComboBox cb = (JComboBox)e.getSource();

			for (i=0;i<rows;i++) {
				if (cb == rhythmButtons[i]) {
					print("Row: " + i);
					String sInt = (String)cb.getSelectedItem();
					m = Integer.parseInt(sInt); 
					print("m = " + m);
					for (j=0;j<cols;j++) {
						if (m == 0) {
							buttons[i][j].getModel().setArmed(false);
							buttons[i][j].getModel().setSelected(false);
							buttons[i][j].getModel().setPressed(false);
							on[i][j] = false;
						}
						else if (m == 1) {
							buttons[i][j].getModel().setArmed(true);
							buttons[i][j].getModel().setSelected(true);
							buttons[i][j].getModel().setPressed(true);
							on[i][j] = true;
						}
						else if (m > 1) {
							if ((j+1)%m == 1)  {
								buttons[i][j].getModel().setArmed(true);
								buttons[i][j].getModel().setSelected(true);
								buttons[i][j].getModel().setPressed(true);
								on[i][j] = true;
							}
							else {
								buttons[i][j].getModel().setArmed(false);
								buttons[i][j].getModel().setSelected(false);
								buttons[i][j].getModel().setPressed(false);
								on[i][j] = false;
							}
						}
					}
				}
			}
		}
	};

	ActionListener randButListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			int r,m,t;

			// t = 200 + randNum.nextInt(300);
			// tempoText.setText(Integer.toString(t));
			// tempoSlide.setValue(t);

			if (SPC) {
				for (int i=0;i<rows;i++) {
					r = 1+randNum.nextInt(10);
					m = 1+randNum.nextInt(10);

					for(int j=0;j<cols; j++) {
						if (j%m == r)  {
							buttons[i][j].getModel().setArmed(true);
							buttons[i][j].getModel().setSelected(true);
							buttons[i][j].getModel().setPressed(true);
							//		buttons[i].getModel().setArmed(false);
							on[i][j] = true;
							//m.setPressed(true);
						}
						else {
							buttons[i][j].getModel().setArmed(false);
							buttons[i][j].getModel().setSelected(false);
							buttons[i][j].getModel().setPressed(false);
							//		buttons[i].getModel().setArmed(false);
							on[i][j] = false;
							//m.setPressed(true);
						}
					}
				}
			}
		}
	};

	ActionListener getFilesListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			numFiles = 0;
			
			String tempString = "F";
			int tLen = tempString.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = tempString.getBytes();
			
			// mosaicSand.sendGrain((byte)NAppID.STUDENT_SEQUENCER, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.BYTE, tLen, tStringAsBytes );
			start.setEnabled(true);
			allOff.setEnabled(true);
			
		}
	};

	ActionListener randAudListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			int k=0;
			for (int i=0;i<rows;i++) {
				int tf = randNum.nextInt(numFiles);
				fileButtons[i].setSelectedIndex(tf);
			}
		}
	};

	ActionListener fileSelectListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JComboBox cb = (JComboBox)e.getSource();
			String prefix = "http://nomads.music.virginia.edu/sounds/";
			try {
				webBase = new URL(prefix);
			} catch (MalformedURLException mue) {
				hPrint("Ouch - a MalformedURLException happened.");
				mue.printStackTrace();
			} catch (IOException ioe) {
				hPrint("Oops- an IOException happened.");
				ioe.printStackTrace();
			}
			if (SFS) {
				for (int i=0;i<rows;i++) {
					if (cb == fileButtons[i]) {
						print("Row: " + i);
						String fileName = (String)cb.getSelectedItem();
						print("File:  " + fileName);
						for (int j=0;j<cols;j++) {
							buttons[i][j].setText(fileName);
						}
						String soundFile = new String(prefix + fileName);
						print("loading sound: " + soundFile);
						sounds[i] = getAudioClip( webBase, fileName);
					}
				}
			}
		}
	};

	ChangeListener sliderListener = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			if (STC) {
				JSlider source = (JSlider)e.getSource();
				int val = (int)source.getValue();
				//	    	if (!source.getValueIsAdjusting()) {
				setWait(60000/(int)val);
				if (getWait() <= 0) {
					setWait(1);
				}
				//	    	}
				tempoText.setText(Integer.toString(val));
			}
		}
	};

	ChangeListener changeListener = new ChangeListener() {
		public void stateChanged(ChangeEvent changeEvent) {
			AbstractButton abstractButton = (AbstractButton) changeEvent.getSource();
			ButtonModel buttonModel = abstractButton.getModel();
			boolean armed = buttonModel.isArmed();
			boolean pressed = buttonModel.isPressed();
			boolean selected = buttonModel.isSelected();
			print("Changed: " + armed + "/" + pressed + "/" + selected);
			for (int i=0;i<rows;i++) {
				for(int j=0;j<cols; j++) {
					if (abstractButton == buttons[i][j]) {
						if (selected) {
							if (SPC) {
								on[i][j] = true;
								if (SBC) {
									String tempString = new String("P:" + i + ":" + j);
									int tLen = tempString.length();
									//    char[] tStringAsChars = tString.toCharArray();
									byte[] tStringAsBytes = tempString.getBytes();
									
									// mosaicSand.sendGrain((byte)NAppID.STUDENT_SEQUENCER, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.BYTE, tLen, tStringAsBytes );
									
								}
							}
							else {
								buttons[i][j].getModel().setPressed(false);
								buttons[i][j].getModel().setSelected(false);
							}
						}
						else {
							if (SPC) {
								on[i][j] = false;
								if (SBC) {
									String tempString = new String("O:" + i + ":" + j);
									int tLen = tempString.length();
									//    char[] tStringAsChars = tString.toCharArray();
									byte[] tStringAsBytes = tempString.getBytes();
									
									// mosaicSand.sendGrain((byte)NAppID.STUDENT_SEQUENCER, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.BYTE, tLen, tStringAsBytes );
									
								}
							}
						}
					}
				}
			}
		}
	};

	public void actionPerformed(ActionEvent ae)
	{
		int bpm;
		Object obj = ae.getSource();
		// if (obj == tempoText) {
		//     if (STC) {
		// 	bpm = Integer.parseInt(tempoText.getText());
		// 	setWait(60000/bpm);
		//     }
		// }
		if (obj == start) {
			bpm = Integer.parseInt(tempoText.getText());
			setWait(60000/bpm);
			if (run == 0) {
				run = 1;
				print("run = 1");
			}
			else {
				run = 0;
				print("run = 0");
			}
		}
		else {
			for (int i=0;i<rows;i++) {
				for(int j=0;j<cols; j++) {
					if (obj == buttons[i][j]) {
						// sounds[i].play();
						// print("play:" + i);
						// try {
						//     streamOut.writeByte(app_id.STUDENT_SEQUENCER);
						//     streamOut.writeUTF("click");
						// }
						// catch(IOException ioe) {
						//     print("Error writing...");
						// }
					}
				}
			}
		}
	}
}