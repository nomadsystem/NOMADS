/*
  NOMADS Opera Control v.210
  Revised/cleaned, 6/20/2012, Steven Kemper
  Integrating NSand class
 */
import java.applet.*;

import java.awt.*;
import java.lang.Math;
import java.applet.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JToggleButton;
import javax.swing.DefaultButtonModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.util.*;
import java.util.Calendar;
import java.util.Random;

import java.net.*;
import java.io.*;
import netscape.javascript.*;
import nomads.v210_Auk.*;

public class OperaCntrl extends JApplet implements ActionListener, KeyListener, Runnable {   

	private class NomadsAppThread extends Thread {
		OperaCntrl client; //Replace with current class name

		public NomadsAppThread(OperaCntrl _client) {
			client = _client;
		}
		public void run()    {			
			NGlobals.lPrint("NomadsAppThread -> run()");
			while (true)  {
				client.handle();
			}
		}
	}

	NSand operaSand;
	private NomadsAppThread nThread;

	Random randNum;

	int     MAX_THREADS = 100000;


	int i,j;
	int width,height,fontSize;
	Font textFont;
	JButton aButton;

	int x,y,w,h;

	JSlider discussAlpha;
	JSlider cloudAlpha;
	JSlider pointerAlpha;
	JSlider dropletLevel;
	JSlider mainVolLevel;

	JToggleButton discussCntrl;
	JToggleButton cloudCntrl;
	JToggleButton pointerCntrl;
	JToggleButton dropletCntrl;


	JButton discussClear, cloudClear;  //, pointerClear;

	public void createGUI(Container pane) {



		Color BG = new Color(0,0,100);      
		pane.setBackground(BG);
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		//pane.setLayout(new FlowLayout(FlowLayout.CENTER));

		JPanel tPane = new JPanel();
		tPane.setLayout(new BoxLayout(tPane, BoxLayout.X_AXIS));
		//tPane.setLayout(new FlowLayout(FlowLayout.CENTER));

		JPanel discussWrapper = new JPanel();
		discussWrapper.setBorder(BorderFactory.createLineBorder (Color.black, 1));
		JLabel discussLabel = new JLabel("Discuss");
		discussLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		discussWrapper.add(discussLabel);

		JPanel cloudWrapper = new JPanel();
		cloudWrapper.setBorder(BorderFactory.createLineBorder (Color.black, 1));
		JLabel cloudLabel = new JLabel("Cloud");
		cloudLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		cloudWrapper.add(cloudLabel);

		JPanel pointerWrapper = new JPanel();
		pointerWrapper.setBorder(BorderFactory.createLineBorder (Color.black, 1));
		JLabel pointerLabel = new JLabel("Pointer");
		pointerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		pointerWrapper.add(pointerLabel);

		JPanel dropletWrapper = new JPanel();
		dropletWrapper.setBorder(BorderFactory.createLineBorder (Color.black, 1));
		JLabel dropletLabel = new JLabel("Droplet");
		dropletLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		dropletWrapper.add(dropletLabel);

		JPanel mainVolWrapper = new JPanel();
		mainVolWrapper.setBorder(BorderFactory.createLineBorder (Color.black, 1));
		JLabel mainVolLabel = new JLabel("MainVol");
		mainVolLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainVolWrapper.add(mainVolLabel);

		discussCntrl = new JToggleButton("Discuss");
		discussCntrl.addChangeListener(buttonListener);
		discussCntrl.setAlignmentX(Component.CENTER_ALIGNMENT);

		discussClear = new JButton("Clear");
		discussClear.addActionListener(checkListener); //To be renamed!
		discussClear.setAlignmentX(Component.CENTER_ALIGNMENT);


		cloudCntrl = new JToggleButton("Cloud");
		cloudCntrl.addChangeListener(buttonListener);
		cloudCntrl.setAlignmentX(Component.CENTER_ALIGNMENT);

		cloudClear = new JButton("Clear");
		cloudClear.addActionListener(checkListener);
		cloudClear.setAlignmentX(Component.CENTER_ALIGNMENT);


		pointerCntrl = new JToggleButton("Pointer");
		pointerCntrl.addChangeListener(buttonListener);
		pointerCntrl.setAlignmentX(Component.CENTER_ALIGNMENT);

		// pointerClear = new JButton("Clear");
		// pointerClear.addActionListener(checkListener);
		// pointerClear.setAlignmentX(Component.CENTER_ALIGNMENT);

		dropletCntrl = new JToggleButton("Droplets");
		dropletCntrl.addChangeListener(buttonListener);
		dropletCntrl.setAlignmentX(Component.CENTER_ALIGNMENT);

		discussWrapper.setLayout(new BoxLayout(discussWrapper, BoxLayout.Y_AXIS));
		cloudWrapper.setLayout(new BoxLayout(cloudWrapper, BoxLayout.Y_AXIS));
		pointerWrapper.setLayout(new BoxLayout(pointerWrapper,BoxLayout.Y_AXIS));
		dropletWrapper.setLayout(new BoxLayout(dropletWrapper,BoxLayout.Y_AXIS));
		mainVolWrapper.setLayout(new BoxLayout(mainVolWrapper,BoxLayout.Y_AXIS));

		discussAlpha = new JSlider(JSlider.VERTICAL,0,255, 180);
		discussAlpha.addChangeListener(sliderListener);
		discussAlpha.addKeyListener(this); 

		discussAlpha.setMajorTickSpacing(10);
		discussAlpha.setPaintTicks(true);
		discussAlpha.setPaintLabels(true);

		discussWrapper.add(discussAlpha);
		discussWrapper.add(discussCntrl);
		discussWrapper.add(discussClear);

		cloudAlpha = new JSlider(JSlider.VERTICAL,0,255, 180);
		cloudAlpha.addChangeListener(sliderListener);

		cloudAlpha.setMajorTickSpacing(10);
		cloudAlpha.setPaintTicks(true);
		cloudAlpha.setPaintLabels(true);

		cloudWrapper.add(cloudAlpha);
		cloudWrapper.add(cloudCntrl);
		cloudWrapper.add(cloudClear);

		pointerAlpha = new JSlider(JSlider.VERTICAL,0, 255, 180);
		pointerAlpha.addChangeListener(sliderListener);

		pointerAlpha.setMajorTickSpacing(10);
		pointerAlpha.setPaintTicks(true);
		pointerAlpha.setPaintLabels(true);

		pointerWrapper.add(pointerAlpha);
		pointerWrapper.add(pointerCntrl);
		//	pointerWrapper.add(pointerClear);

		dropletLevel = new JSlider(JSlider.VERTICAL,0, 100, 100);
		dropletLevel.addChangeListener(sliderListener);

		dropletLevel.setMajorTickSpacing(10);
		dropletLevel.setPaintTicks(true);
		dropletLevel.setPaintLabels(true);

		dropletWrapper.add(dropletLevel);
		dropletWrapper.add(dropletCntrl);

		mainVolLevel = new JSlider(JSlider.VERTICAL,0, 100, 100);
		mainVolLevel.addChangeListener(sliderListener);

		mainVolLevel.setMajorTickSpacing(10);
		mainVolLevel.setPaintTicks(true);
		mainVolLevel.setPaintLabels(true);
		mainVolWrapper.add(mainVolLevel);

		tPane.add(discussWrapper);
		tPane.add(cloudWrapper);
		tPane.add(pointerWrapper);
		tPane.add(dropletWrapper);
		tPane.add(mainVolWrapper);

		tPane.setOpaque(false);
		pane.add(tPane);

	}

	public static void main(String args[])
	{
		/* DO: Change TUT_SineFreq to match the name of your class. Must match file name! */
		OperaCntrl  applet = new OperaCntrl();

		Frame appletFrame = new Frame("OperaCntrl");

		appletFrame.add(applet);
		appletFrame.resize(600,600);
		appletFrame.show();
	}

	public void init()
	{  	

		int i;

		randNum = new Random();

		width = getSize().width;
		height = getSize().height;

		i = 0;
		j = 0;
		createGUI(getContentPane());
		
		operaSand = new NSand(); 
		operaSand.connect();

		nThread = new NomadsAppThread(this);
		nThread.start();
	}	

	// ------------------------------------------------------------------------------------------------
	// BEGIN handle()
	// ------------------------------------------------------------------------------------------------

	public void handle() { ///bite, text
		int i,j,fc,sc,x,y,cnt,cln,chk;
		float freq,amp;
		String temp,thread,input,tCnt,tCln,tChk,tempString;
		int THREAD_ID;
		float xput,yput;
		
		int incCmd, incAppID, incDType, incDLen;
		int incIntData[] = new int[1000];
		byte incByteData[] = new byte[1000];  // Cast as chars here because we're using chars -> strings
		NGrain grain;

		NGlobals.cPrint("OperaCntrl -> handle()");

		grain = operaSand.getGrain();
		grain.print(); //prints grain data to console
		
		incAppID = grain.appID;
		incCmd = grain.command;
		
		String text = new String(grain.bArray);


		NGlobals.cPrint("handle(" + text + "," + incAppID + ") [operaCntrl]\n");
		NGlobals.cPrint("...");

//		if (incAppID == NAppID.MONITOR) {
//			if (text.equals("CHECK")) {
//				try {
//					streamOut.writeByte((byte)app_id.CONDUCTOR_PANEL);
//					streamOut.writeUTF("PING");
//				}
//				catch(IOException ioe) {
//					System.out.println("Error writing to output stream: ");
//				}
//			}	 
//		}   

		// ========= Pointer ============================================

		if (incAppID == NAppID.SERVER) {
			NGlobals.cPrint("Got data from SERVER: " + text);
			if (text.length() > 1) {
				temp = text.substring(0,4);

				// checked = new JLabel("CHECKED: 0");
				// cleaned = new JLabel("CLEANED: 0");
				// counted = new JLabel("CLIENTS: 0");
				if (incCmd == NCommand.OC_DISCUSS_ENABLE) {
					discussCntrl.getModel().setSelected(true);
					NGlobals.cPrint("ControlPanel Discuss Enable");
				}
				if (incCmd == NCommand.OC_DISCUSS_DISABLE) {
					discussCntrl.getModel().setSelected(false);
					NGlobals.cPrint("ControlPanel Discuss Disable");
				}
				if (incCmd == NCommand.OC_CLOUD_ENABLE) {
					cloudCntrl.getModel().setSelected(true);
					NGlobals.cPrint("ControlPanel Cloud Enable");
				}
				if (incCmd == NCommand.OC_CLOUD_DISABLE) {
					cloudCntrl.getModel().setSelected(false);
					NGlobals.cPrint("ControlPanel Cloud Disable");
				}
				if (incCmd == NCommand.OC_POINTER_ENABLE) {
					pointerCntrl.getModel().setSelected(true);
					NGlobals.cPrint("ControlPanel Pointer Enable");
				}
				if (incCmd == NCommand.OC_POINTER_DISABLE) {
					pointerCntrl.getModel().setSelected(false);
					NGlobals.cPrint("ControlPanel Pointer Disable");
				}
				if (incCmd == NCommand.OC_DROPLET_ENABLE) {
					dropletCntrl.getModel().setSelected(true);
					NGlobals.cPrint("ControlPanel Pointer Enable");
				}
				if (incCmd == NCommand.OC_DROPLET_DISABLE) {
					dropletCntrl.getModel().setSelected(false);
					NGlobals.cPrint("ControlPanel Cloud Disable");
				}

			}

		}
		NGlobals.cPrint("-------------------------------------------------[OC]\n");

	}

	// ------------------------------------------------------------------------------------------------
	// END handle()
	// ------------------------------------------------------------------------------------------------

	//OC_Discuss code===============================================
	ActionListener checkListener = new ActionListener() {	
		public void actionPerformed(java.awt.event.ActionEvent ae) //for button press
		{
			//	String tInput;
			Object source = ae.getSource();
			//	NGlobals.cPrint("entering speakListener");


			if (source == discussClear) {
				NGlobals.cPrint("Action:  discussClear");
				
				String tString = " ";
				int tLen = tString.length();
				byte[] tStringAsBytes = tString.getBytes();
				operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.CLEAR_DISCUSS, (byte)NDataType.BYTE, tLen, tStringAsBytes );
			}
			if (source == cloudClear) {
				NGlobals.cPrint("Action:  cloudClear");
				String tString = " "; 
				int tLen = tString.length();
				byte[] tStringAsBytes = tString.getBytes();
				operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.CLEAR_CLOUD, (byte)NDataType.BYTE, tLen, tStringAsBytes );
			}
//			if (source == pointerClear) {
//				NGlobals.cPrint("Action:  pointerClear");
//				String tString = " "; 
//				int tLen = tString.length();
//				byte[] tStringAsBytes = tString.getBytes();
//				operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.CLEAR_POINTER, (byte)NDataType.BYTE, tLen, tStringAsBytes );
//			}
		}		
	};

	ChangeListener buttonListener = new ChangeListener() {
		public void stateChanged(ChangeEvent changeEvent) {
			AbstractButton abstractButton = (AbstractButton) changeEvent.getSource();
			ButtonModel buttonModel = abstractButton.getModel();
			boolean armed = buttonModel.isArmed();
			boolean pressed = buttonModel.isPressed();
			boolean selected = buttonModel.isSelected();
			if (abstractButton == discussCntrl) {
				if (selected) {
					String tString = " "; 
					int tLen = tString.length();
					byte[] tStringAsBytes = tString.getBytes();
					operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.OC_DISCUSS_ENABLE, (byte)NDataType.BYTE, tLen, tStringAsBytes );
					NGlobals.cPrint("discussCntrl:  ON");
				} 
				else {
					String tString = " "; 
					int tLen = tString.length();
					byte[] tStringAsBytes = tString.getBytes();
					operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.OC_DISCUSS_DISABLE, (byte)NDataType.BYTE, tLen, tStringAsBytes );
					NGlobals.cPrint("discussCntrl:  OFF");
					
				}
			}
			else if (abstractButton == cloudCntrl) {
				if (selected) {
					String tString = " "; 
					int tLen = tString.length();
					byte[] tStringAsBytes = tString.getBytes();
					operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.OC_CLOUD_ENABLE, (byte)NDataType.BYTE, tLen, tStringAsBytes );
					NGlobals.cPrint("cloudCntrl:  ON");
					
				} 
				else {
					String tString = " "; 
					int tLen = tString.length();
					byte[] tStringAsBytes = tString.getBytes();
					operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.OC_CLOUD_DISABLE, (byte)NDataType.BYTE, tLen, tStringAsBytes );
					NGlobals.cPrint("cloudCntrl:  OFF");
					
				}
			}
			if (abstractButton == pointerCntrl) {
				if (selected) {
					String tString = " "; 
					int tLen = tString.length();
					byte[] tStringAsBytes = tString.getBytes();
					operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.OC_POINTER_ENABLE, (byte)NDataType.BYTE, tLen, tStringAsBytes );
					NGlobals.cPrint("pointerCntrl:  ON");
					
				} 
				else {
					String tString = " "; 
					int tLen = tString.length();
					byte[] tStringAsBytes = tString.getBytes();
					operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.OC_POINTER_DISABLE, (byte)NDataType.BYTE, tLen, tStringAsBytes );
					NGlobals.cPrint("pointerCntrl:  OFF"); 
				}
			}
			if (abstractButton == dropletCntrl) {
				if (selected) {
					String tString = " "; 
					int tLen = tString.length();
					byte[] tStringAsBytes = tString.getBytes();
					operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.OC_DROPLET_ENABLE, (byte)NDataType.BYTE, tLen, tStringAsBytes );
					NGlobals.cPrint("dropletCntrl:  ON"); 
					
				} 
				else {
					String tString = " "; 
					int tLen = tString.length();
					byte[] tStringAsBytes = tString.getBytes();
					operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.OC_DROPLET_DISABLE, (byte)NDataType.BYTE, tLen, tStringAsBytes );
					NGlobals.cPrint("dropletCntrl:  OFF"); 
				}
			}
		}
	};

	ChangeListener sliderListener = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			String n;
			String tempString;
			JSlider source = (JSlider)e.getSource();
			if (source == discussAlpha) {
				int val = (int)source.getValue();
				
				String tString = Integer.toString(val); 
				int tLen = tString.length();
				byte[] tStringAsBytes = tString.getBytes();
				operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_DISCUSS_ALPHA, (byte)NDataType.BYTE, tLen, tStringAsBytes );
				NGlobals.cPrint("discussAlpha:" + val);  
			}
			if (source == cloudAlpha) {
				int val = (int)source.getValue();
				String tString = Integer.toString(val); 
				int tLen = tString.length();
				byte[] tStringAsBytes = tString.getBytes();
				operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_CLOUD_ALPHA, (byte)NDataType.BYTE, tLen, tStringAsBytes );
				NGlobals.cPrint("cloudAlpha:" + val); 
				
			}
			if (source == pointerAlpha) {
				int val = (int)source.getValue();
				String tString = Integer.toString(val); 
				int tLen = tString.length();
				byte[] tStringAsBytes = tString.getBytes();
				operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_POINTER_ALPHA, (byte)NDataType.BYTE, tLen, tStringAsBytes );
				NGlobals.cPrint("pointerAlpha:" + val); 
	
			}
			if (source == dropletLevel) {
				int val = (int)source.getValue();
				String tString = Integer.toString(val); 
				int tLen = tString.length();
				byte[] tStringAsBytes = tString.getBytes();
				operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_DROPLET_VOLUME, (byte)NDataType.BYTE, tLen, tStringAsBytes );
				NGlobals.cPrint("dropletLevel:" + val); 
				 
			}
			if (source == mainVolLevel) {
				int val = (int)source.getValue();
				String tString = Integer.toString(val); 
				int tLen = tString.length();
				byte[] tStringAsBytes = tString.getBytes();
				operaSand.sendGrain((byte)NAppID.CONDUCTOR_PANEL, (byte)NCommand.SET_MAIN_VOLUME, (byte)NDataType.BYTE, tLen, tStringAsBytes );
				NGlobals.cPrint("mainVolLevel:" + val); 
			}

		}
	};

	public void keyPressed (KeyEvent e)
	{

		//boolean armed = buttonModel.isArmed();
		//boolean pressed = buttonModel.isPressed();
		//boolean selected = buttonModel.isSelected();

		int v,t,k;
		k = e.getKeyCode();
		NGlobals.cPrint("key: " + k);
		if (k == 103) { 
			v = discussAlpha.getValue();
			discussAlpha.setValue(v+3);
		}
		else if (k == 100) { 
			v = discussAlpha.getValue();
			discussAlpha.setValue(v-3);
		}
		else if (k == 97) { 				
			discussCntrl.getModel().setArmed(true);
			discussCntrl.getModel().setSelected(true);
			discussCntrl.getModel().setPressed(true);
		}

		else if (k == 104) { 
			v = cloudAlpha.getValue();
			cloudAlpha.setValue(v+3);
		}
		else if (k == 101) { 
			v = cloudAlpha.getValue();
			cloudAlpha.setValue(v-3);
		}
		else if (k == 98) { 
			cloudCntrl.getModel().setPressed(true);
			cloudCntrl.getModel().setSelected(true);
			cloudCntrl.getModel().setPressed(true);
		}

		else if (k == 105) { 
			v = pointerAlpha.getValue();
			pointerAlpha.setValue(v+3);
		}
		else if (k == 102) { 
			v = pointerAlpha.getValue();
			pointerAlpha.setValue(v-3);
		}
		else if (k == 99) { 
			pointerCntrl.getModel().setPressed(true);
			pointerCntrl.getModel().setSelected(true);
			pointerCntrl.getModel().setPressed(true);
		}
	}

	public void keyReleased(KeyEvent e){
	}

	public void keyTyped(KeyEvent e){
	}


	public void actionPerformed( ActionEvent ae )
	{
		Object obj = ae.getSource();
		if( obj == aButton ) {
			NGlobals.cPrint("You pressed a button");
		}
	}


	// DT 6/30/10:  not sure we need these anymore

	public void start() {

	}

	public void run () {
		if (i == 1) {
		} 
	}

}
