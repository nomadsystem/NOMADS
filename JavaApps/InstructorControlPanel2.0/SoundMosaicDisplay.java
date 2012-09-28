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
import java.util.Random;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import netscape.javascript.*; 
import nomads.v210.*;

// INSTRUCTOR SEQUENCER

public class SoundMosaicDisplay extends JApplet implements ActionListener, Runnable
{

    NSand mySand;

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
    private JButton start, getFiles, sendBank, syncPattern,allOff,syncTime;
    private JButton randAll,randAud;
    private JToggleButton studTempoCtl, studPatCtl, studFileSel,studDispCtl,instButCtl,studButCtl,studOnOffCtl;

    private Boolean SOOC = true;
    private Boolean STC = true;
    private Boolean SPC = true;
    private Boolean SFS = true;
    private Boolean SBC = false;
    private Boolean IBC = false;  

    private URL webBase;
    int tWait, newBeat, timeCtr;
    private int wait;
    long startT,nowT,diffT, syncT, offT,serverT,avgDiffT,sumT,calcT;
    Calendar cal;
    JTextField tempoText;
    JSlider tempoSlide;
    static int run;
    Random randNum;
    int startTempo = 180;

    Boolean dBug = false;
    Boolean hBug = true;

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
	filePanel.setPreferredSize(new Dimension((int)110,(int)450));

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
	rhythmPanel.setPreferredSize(new Dimension((int)60,(int)450));

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
	phasePanel.setPreferredSize(new Dimension((int)30,(int)450));

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
	//	buttonPanel.setBackground(Color.black);
	buttonPanel.setPreferredSize(new Dimension((int)720,(int)450));

	JPanel pulseWrapper = new JPanel();
	pulseWrapper.setLayout(new FlowLayout(FlowLayout.CENTER));

        JPanel pulsePanel = new JPanel();
        pulsePanel.setLayout(new GridLayout(1,pulseCols));
	pulsePanel.setPreferredSize(new Dimension(930,10));
	for(i=0;i<pulseCols; i++) {
	    pulses[i] = new JLabel(" ");
	    pulses[i].setPreferredSize(new Dimension(5,5));
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
	JPanel  controls2 = new JPanel();
	controls2.setLayout(new FlowLayout(FlowLayout.CENTER));
	//	controls2.setPreferredSize(new Dimension((int)400,(int)1));

	JLabel title = new JLabel("Tempo (bpm)");
	controls.add(title);
	startTempo += (randNum.nextInt(60));

	// try {
	//     // streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
	//     // streamOut.writeUTF(Integer.toString(startTempo));
	//     hPrint("startTempo: " + startTempo);
	// }
	// catch(IOException ioe) {
	//     hPrint("Error writing to output stream: ");
	// } 

	tempoText = new JTextField(Integer.toString(startTempo),3);

	tempoText.addActionListener(this);
	controls.add(tempoText);
	start = new JButton("Start/Stop");
	start.setEnabled(false);
	start.addActionListener(this);
	//	getFiles = new JButton("Get Audiofiles");
	//	getFiles.addActionListener(getFilesListener);
	sendBank = new JButton("Send Bank");
	sendBank.setEnabled(true);
	sendBank.addActionListener(sendBankListener);
	syncPattern = new JButton("Sync Pattern");
	syncPattern.setEnabled(true);
	syncPattern.addActionListener(syncPatternListener);
	allOff = new JButton("All Off");
	allOff.addActionListener(allOffListener);
	// syncTime = new JButton("Sync Time");
	// syncTime.addActionListener(syncTimeListener);

	//allOff.setEnabled(false);

	studOnOffCtl = new JToggleButton("*On/off*");
	studOnOffCtl.getModel().setArmed(true);
	studOnOffCtl.getModel().setSelected(true);
	studOnOffCtl.getModel().setPressed(true);
	studOnOffCtl.addActionListener(onOffCtlListener);

	// studTempoCtl = new JToggleButton("*Tempo*");
	// studTempoCtl.getModel().setArmed(true);
	// studTempoCtl.getModel().setSelected(true);
	// studTempoCtl.getModel().setPressed(true);
	// studTempoCtl.addActionListener(tempoCtlListener);

	studPatCtl = new JToggleButton("*Pattern*");
	studPatCtl.getModel().setArmed(true);
	studPatCtl.getModel().setSelected(true);
	studPatCtl.getModel().setPressed(true);
	studPatCtl.addActionListener(patCtlListener);
	
	studFileSel = new JToggleButton("*File Sel*");
	studFileSel.getModel().setArmed(true);
	studFileSel.getModel().setSelected(true);
	studFileSel.getModel().setPressed(true);

	studFileSel.addActionListener(fileSelCtlListener);
	
	studButCtl = new JToggleButton("*Tribe Ctl*");
	studButCtl.addActionListener(studButCtlListener);

	instButCtl = new JToggleButton("*Khan Ctl*");
	instButCtl.addActionListener(instButCtlListener);

	randAll = new JButton("Random Rhythm");
	randAll.addActionListener(randButListener);

	randAud = new JButton("Random Audio File");
	randAud.addActionListener(randAudListener);

	controls.add(start);
	//	controls.add(getFiles);
	controls.add(randAll);
	controls.add(randAud);
	controls.add(allOff);
	//	controls.add(syncTime);

	controls2.add(studOnOffCtl);
	//	controls2.add(studTempoCtl);
	controls2.add(studPatCtl);
	controls2.add(studFileSel);
	controls2.add(studButCtl);
	controls2.add(instButCtl);

	controls2.add(sendBank);
	controls2.add(syncPattern);

	tempoSlide = new JSlider(JSlider.HORIZONTAL,
                                      1, 600, startTempo);
	tempoSlide.setMajorTickSpacing(10);
	tempoSlide.setPaintTicks(true);
    //	tempoSlide.setPreferredSize(new Dimension((int)400,(int)1));

	Hashtable labelTable = new Hashtable();
	labelTable.put( new Integer( 1 ), new JLabel("1") );
	labelTable.put( new Integer( 600/2 ), new JLabel("300") );
	labelTable.put( new Integer( 600 ), new JLabel("600") );
	tempoSlide.setLabelTable( labelTable );
	
	tempoSlide.setPaintLabels(true);
	tempoSlide.addChangeListener(sliderListener);

	buttonWrapper.add(rhythmPanel);
	buttonWrapper.add(phasePanel);
	buttonWrapper.add(buttonPanel);

	pane.add(buttonWrapper);
	pulseWrapper.add(pulsePanel);
	pane.add(pulseWrapper);
	pane.add(tempoSlide);
	pane.add(controls2);
	pane.add(controls);
    }

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
	int i,j;
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
			    // newBeat = -1*j;
			    // try {
			    // 	streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
			    // 	streamOut.writeUTF(Integer.toString(newBeat));
				
			    // 	//cal = Calendar.getInstance();
			    // 	//nowT = cal.getTimeInMillis();
			    // 	// diffT = nowT-startT;
			    // 	//				    streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
			    // 	//				    streamOut.writeUTF(Long.toString(diffT));
			    // }
			    // catch(IOException ioe) {
			    // 	hPrint("Error writing to output stream: ");
			    // } 

			    pulses[j+fileOffset].setBackground(Color.RED);
			    runner.sleep(getWait());
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

    public void init(NSand inSand)
    {
	mySand = inSand;
	int i;
	setWait(60);
	run = 0;
	randNum = new Random();
	sounds = new AudioClip[maxAudioClips];
	sfName = new String[maxAudioClips];
	fileLister = new ArrayList<String>();
	on = new boolean[rows][cols];
	cal = Calendar.getInstance();
	startT = cal.getTimeInMillis();
	newBeat = 0;
	// connect(serverName, serverPort);
	initSounds();
        createButtons(getContentPane());
	
	cal = Calendar.getInstance();
	nowT = cal.getTimeInMillis();
	avgDiffT = 0;
	sumT = 0;
	timeCtr = 0;
	// Ask the server "what time is it?"
	// calculate an average

	// Register
	
	byte d[] = new byte[1];
	d[0] = 0;
	mySand.sendGrain((byte)NAppID.INSTRUCTOR_SEQUENCER, (byte)NCommand.REGISTER, (byte)NDataType.UINT8, 1, d );

	// Request list of sound files

	mySand.sendGrain((byte)NAppID.INSTRUCTOR_SEQUENCER, (byte)NCommand.GET_SOUNDFILE_LIST, (byte)NDataType.UINT8, 1, d );

	start.setEnabled(true);
	allOff.setEnabled(true);
	
    }
 
    // BEGIN can cut and paste from here ....
  
    public boolean isNumber(String in) {
        // try {
        //     Integer.parseInt(in);
	    
        // } catch (NumberFormatException ex) {
        //     return false;
        // }
        return true;
    }

    // END cut and paste
    // Takes information from the server
    public void handle(NGrain inGrain)
    {
	int i,j,k,t,tRow,tCol,fc,sc;
	fc = sc = 0;
	String temp,fileName,stringTime;
	NGrain grain;

	grain = inGrain;

	Byte b = grain.appID;
	String s = new String(grain.bArray);

	hPrint("handle(" + b + "," + s +")");

	if (s.equals("D")) {
	    Collections.sort(fileLister,String.CASE_INSENSITIVE_ORDER);
	    //	    System.out.println(fileLister);
	    sFiles = new String[numFiles];
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
	else if (s.length() > 1) {
	    temp = s.substring(0,2);
	    // Get server reply to "what time do you have?"
	    if (temp.equals("T:")) {
		stringTime = s.substring(2,s.length());
		serverT = Long.parseLong(stringTime);
		hPrint("server time (serverT)" + serverT);
		diffT = serverT-nowT;
		hPrint("difference between server and nowT (ISeq):" + diffT);
		sumT += diffT;
		timeCtr++;
		avgDiffT = sumT/timeCtr;
		hPrint("average time difference (serverT-nowT)" + avgDiffT);
		calcT = nowT+avgDiffT;
		hPrint("what time I (ISeq) think the server time is (calcT):" + calcT);
	    }
	    if (temp.equals("F:")) {
		fileName = s.substring(2,s.length());
		fileLister.add(fileName);
		numFiles++;
		hPrint("File: " + s);
	    }
	    else if (temp.equals("P:")) {
		hPrint("P: = patter(x,y) on");
		fc = s.indexOf(":");
		sc = s.indexOf(":", fc+1);
		print("fc = " + fc);
		print("sc = " + sc);
		temp = s.substring(2,sc);
		tRow = (int)Integer.parseInt(temp);
		print("temp " + temp);
		temp = s.substring(sc+1,s.length());
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
		fc = s.indexOf(":");
		sc = s.indexOf(":", fc+1);
		print("fc = " + fc);
		print("sc = " + sc);
		temp = s.substring(2,sc);
		tRow = (int)Integer.parseInt(temp);
		print("temp " + temp);
		temp = s.substring(sc+1,s.length());
		tCol = (int)Integer.parseInt(temp);
		print("temp " + temp);
		
		buttons[tRow][tCol].getModel().setArmed(false);
		buttons[tRow][tCol].getModel().setSelected(false);
		buttons[tRow][tCol].getModel().setPressed(false);
		//		buttons[i].getModel().setArmed(false);
		on[tRow][tCol] = false;
	    }
	}
	else {
	    hPrint("Unknown input from server: " + s);
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
			rhythmButtons[i].setSelectedIndex(0);
			on[i][j] = false;
		    }
		}
	    }
	};

    ActionListener syncTimeListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		
		cal = Calendar.getInstance();
		nowT = cal.getTimeInMillis();
		// calcT = "what time I think it is in "server time"
		calcT = nowT + avgDiffT;
		// try {
		//     // Send "T: to server to initiate sync time
		//     // streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);   
		//     String tempString = new String("T:" + calcT);
		//     // streamOut.writeUTF(tempString);
		//     hPrint("tempString:" + tempString);
		//     hPrint("Sending sync");
		// }
		// catch(IOException ioe) {
		//     hPrint("Error writing...");
		// }
	    }
	};


    ActionListener syncPatternListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		int i,j;
		// try {
		//     // streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
		//     String tempString = new String("S:");
		//     // streamOut.writeUTF(tempString);
		//     hPrint("tempString:" + tempString);

		//     print("Sending sync");
		// }
		// catch(IOException ioe) {
		//     hPrint("Error writing...");
		// }
		for (i=0;i<rows;i++) {
		    for (j=0;j<cols;j++) {
			if (on[i][j]) {
			    // try {
			    // 	// streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
			    // 	String tempString = new String("P:" + i + ":" + j);
			    // 	// streamOut.writeUTF(tempString);
			    // 	hPrint("tempString:" + tempString);
			    // }
			    // catch(IOException ioe) {
			    // 	hPrint("Error writing...");
			    // }
			}
		    }
		}
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

    ActionListener sendBankListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		// try {
		//     // streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
		//     // streamOut.writeUTF("N");
		// }
		// catch(IOException ioe) {
		//     hPrint("Error writing...");
		// }
		for (int i=0;i<rows;i++) {
		    String fileName = (String)fileButtons[i].getSelectedItem();
		    print("File:  " + fileName);
		    // try {
		    // 	// streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
		    // 	String tempString = new String("F:" + fileName);
		    // 	// streamOut.writeUTF(tempString);
		    // }
		    // catch(IOException ioe) {
		    // 	hPrint("Error writing...");
		    // }
		}
		// try {
		//     // streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
		//     // streamOut.writeUTF("D");
		// }
		// catch(IOException ioe) {
		//     hPrint("Error writing...");
		// }
	    }
	};

    ActionListener onOffCtlListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (SOOC) {
		    SOOC = false;
		    // try {
		    // 	// streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
		    // 	// streamOut.writeUTF("SOOCOFF");
		    // 	hPrint("SOOCOFF");
		    // 	studOnOffCtl.getModel().setArmed(false);
		    // 	studOnOffCtl.getModel().setSelected(false);
		    // 	studOnOffCtl.getModel().setPressed(false);
		    // }
		    // catch(IOException ioe) {
		    // 	hPrint("Error writing...");
		    // }
		}
		else {			hPrint("SOOCOFF:");			hPrint("SOOCOFF:");


		    SOOC = true;
		    // try {
		    // 	// streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
		    // 	// streamOut.writeUTF("SOOCON");
		    // 	hPrint("SOOCON");
		    // 	studOnOffCtl.getModel().setArmed(true);
		    // 	studOnOffCtl.getModel().setSelected(true);
		    // 	studOnOffCtl.getModel().setPressed(true);
		    // }
		    // catch(IOException ioe) {
		    // 	hPrint("Error writing...");
		    // }
		}
	    }
	};


    ActionListener tempoCtlListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (STC) {
		    STC = false;
		    // try {
		    // 	// streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
		    // 	// streamOut.writeUTF("STCOFF");
		    // 	studTempoCtl.getModel().setArmed(false);
		    // 	studTempoCtl.getModel().setSelected(false);
		    // 	studTempoCtl.getModel().setPressed(false);
		    // }
		    // catch(IOException ioe) {
		    // 	hPrint("Error writing...");
		    // }
		}
		else {
		    STC = true;
		    // try {
		    // 	// streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
		    // 	// streamOut.writeUTF("STCON");
		    // 	hPrint("STCON:");

		    // 	studTempoCtl.getModel().setArmed(true);
		    // 	studTempoCtl.getModel().setSelected(true);
		    // 	studTempoCtl.getModel().setPressed(true);
		    // }
		    // catch(IOException ioe) {
		    // 	hPrint("Error writing...");
		    // }
		}
	    }
	};
	
    ActionListener patCtlListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (SPC) {
		    SPC = false;
		    // try {
		    // 	// streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
		    // 	// streamOut.writeUTF("SPCOFF");
		    // 	hPrint("SPCOFF:");

		    // 	studPatCtl.getModel().setArmed(false);
		    // 	studPatCtl.getModel().setSelected(false);
		    // 	studPatCtl.getModel().setPressed(false);
		    // }
		    // catch(IOException ioe) {
		    // 	hPrint("Error writing...");
		    // }
		}
		else {
		    SPC = true;
		    // try {
		    // 	// streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
		    // 	// streamOut.writeUTF("SPCON");
		    // 	studPatCtl.getModel().setArmed(true);
		    // 	studPatCtl.getModel().setSelected(true);
		    // 	studPatCtl.getModel().setPressed(true);
		    // }
		    // catch(IOException ioe) {
		    // 	hPrint("Error writing...");
		    // }
		}
	    }
	};

    ActionListener fileSelCtlListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (SFS) {
		    SFS = false;
		    // try {
		    // 	// streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
		    // 	// streamOut.writeUTF("SFSOFF");
		    // 	hPrint("SFSOFF");

		    // 	studFileSel.getModel().setArmed(false);
		    // 	studFileSel.getModel().setSelected(false);
		    // 	studFileSel.getModel().setPressed(false);
		    // }
		    // catch(IOException ioe) {
		    // 	hPrint("Error writing...");
		    // }
		}
		else {
		    SFS = true;
		    // try {
		    // 	// streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
		    // 	// streamOut.writeUTF("SFSON");
		    // 	hPrint("SFSON");

		    // 	studFileSel.getModel().setArmed(true);
		    // 	studFileSel.getModel().setSelected(true);
		    // 	studFileSel.getModel().setPressed(true);
		    // }
		    // catch(IOException ioe) {
		    // 	hPrint("Error writing...");
		    // }
		}
	    }
	};


    ActionListener studButCtlListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (SBC) {
		    SBC = false;
		    // try {
		    // 	// streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
		    // 	// streamOut.writeUTF("SBCOFF");
		    // 	hPrint("SBCOFF");

		    // 	studButCtl.getModel().setArmed(false);
		    // 	studButCtl.getModel().setSelected(false);
		    // 	studButCtl.getModel().setPressed(false);
		    // 	instButCtl.setEnabled(true);
		    // }
		    // catch(IOException ioe) {
		    // 	hPrint("Error writing...");
		    // }
		}
		else {
		    SBC = true;
		    // try {
		    // 	// streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
		    // 	// streamOut.writeUTF("SBCON");
		    // 	hPrint("SBCON");

		    // 	studButCtl.getModel().setArmed(true);
		    // 	studButCtl.getModel().setSelected(true);
		    // 	studButCtl.getModel().setPressed(true);
		    // 	instButCtl.setEnabled(false);
		    // }
		    // catch(IOException ioe) {
		    // 	hPrint("Error writing...");
		    // }
		}
	    }
	};

    ActionListener instButCtlListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (IBC) {
		    IBC = false;
		    // try {
		    // 	// streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
		    // 	// streamOut.writeUTF("IBCOFF");
		    // 	hPrint("IBCOFF");

		    // 	instButCtl.getModel().setArmed(false);
		    // 	instButCtl.getModel().setSelected(false);
		    // 	instButCtl.getModel().setPressed(false);
		    // 	studButCtl.setEnabled(true);
		    // }
		    // catch(IOException ioe) {
		    // 	hPrint("Error writing...");
		    // }
		}
		else {
		    IBC = true;
		    // try {
		    // 	// streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
		    // 	// streamOut.writeUTF("IBCON");
		    // 	hPrint("IBCOFF");

		    // 	instButCtl.getModel().setArmed(true);
		    // 	instButCtl.getModel().setSelected(true);
		    // 	instButCtl.getModel().setPressed(true);
		    // 	studButCtl.setEnabled(false);
		    // }
		    // catch(IOException ioe) {
		    // 	hPrint("Error writing...");
		    // }
		}
	    }
	};

    ActionListener randButListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		int r,m,t;
		
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
		// t = 200 + randNum.nextInt(300);
		// tempoText.setText(Integer.toString(t));
		// tempoSlide.setValue(t);
		
	    }
	};

    ActionListener getFilesListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		numFiles = 0;
		fileLister.clear();
		// try {
		//     // streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
		//     // streamOut.writeUTF("F");
		//     hPrint("F");

		//     start.setEnabled(true);
		//     sendBank.setEnabled(true);
		//     syncPattern.setEnabled(true);
		//     //allOff.setEnabled(true);
		// }
		// catch(IOException ioe) {
		//     hPrint("Error writing...");
		// }
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

    ActionListener fileSelectListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		JComboBox cb = (JComboBox)e.getSource();
		String prefix = "http://nomads.music.virginia.edu/sounds/";
		// try {
		//     webBase = new URL(prefix);
		// } catch (MalformedURLException mue) {
		//     hPrint("Ouch - a MalformedURLException happened.");
		//     mue.printStackTrace();
		// } catch (IOException ioe) {
		//     hPrint("Oops- an IOException happened.");
		//     ioe.printStackTrace();
		// }
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
	};

    ChangeListener sliderListener = new ChangeListener() {
	    public void stateChanged(ChangeEvent e) {
	    	JSlider source = (JSlider)e.getSource();
		int val = (int)source.getValue();
		if (!source.getValueIsAdjusting()) {
		    setWait(60000/(int)val);
		    if (getWait() <= 0) {
			setWait(1);
		    }
		    //	    	}
		    tempoText.setText(Integer.toString(val));
		    // try {
		    // 	// streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
		    // 	// streamOut.writeUTF(Integer.toString(val));
		    // 	hPrint("Slider (tempo):" + val);
		    // }
		    // catch(IOException ioe) {
		    // 	hPrint("Error writing to output stream: ");
		    // } 
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
				on[i][j] = true;
				if (IBC) {
				    // try {
				    // 	// streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
				    // 	String tempString = new String("P:" + i + ":" + j);
				    // 	// streamOut.writeUTF(tempString);
				    // 	hPrint("tempString: " + tempString);
				    // }
				    // catch(IOException ioe) {
				    // 	hPrint("Error writing...");
				    // }
				}
			    }
			    else {
				on[i][j] = false;
				if (IBC) {
				    // try {
				    // 	// streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
				    // 	String tempString = new String("O:" + i + ":" + j);
				    // 	// streamOut.writeUTF(tempString);
				    // 	hPrint("tempString: " + tempString);
				    // }
				    // catch(IOException ioe) {
				    // 	hPrint("Error writing...");
				    // }
				}
			    }
			}
		    }
		}
	    }
    	};
    
    public void actionPerformed(ActionEvent ae)
    {
	int bpm,i,j;
     	Object obj = ae.getSource();
	
	if (obj == tempoText) {
	    bpm = Integer.parseInt(tempoText.getText());
	    setWait(60000/bpm);

	}
	else if (obj == start) {
	    bpm = Integer.parseInt(tempoText.getText());
	    setWait(60000/bpm);
	    if (run == 0) {
		run = 1;
		print("run = 1");
		cal = Calendar.getInstance();
		startT = cal.getTimeInMillis();

		for (i=0;i<10;i++) {
		    cal = Calendar.getInstance();
		    nowT = cal.getTimeInMillis();
		    calcT = nowT + avgDiffT;
		    // try {
		    // 	// streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
		    // 	String tempString = new String("T:" + calcT);
		    // 	hPrint("sending server sync message (T:) to server->clients with calculate server time (calcT): " + calcT);
		    // 	// streamOut.writeUTF(tempString);
		    // }
		    // catch(IOException ioe) {
		    // 	hPrint("Error writing...");
		    // }
		}
		// try {
		//     // streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
		//     // streamOut.writeUTF("R");
		//     hPrint("R");

		//     // streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
		//     // streamOut.writeUTF(Integer.toString(bpm));
		//     hPrint("bpm:" + bpm );

		// }
		// catch(IOException ioe) {
		//     hPrint("Error writing to output stream: ");
		// } 
	    }
	    else {
		run = 0;
		print("run = 0");
		// try {
		//     // streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
		//     // streamOut.writeUTF("S");
		//     hPrint("S");

		// }
		// catch(IOException ioe) {
		//     hPrint("Error writing to output stream: ");
		// } 
	    }
	}
	else {
	    for (i=0;i<rows;i++) {
		for(j=0;j<cols; j++) {
		    if (obj == buttons[i][j]) {
			//sounds[i].play();
			// try {
			//     streamOut.writeByte(app_id.INSTRUCTOR_SEQUENCER);
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