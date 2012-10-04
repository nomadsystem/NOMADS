//
//  NOMADS Opera Main v.210
//

import java.awt.*;
import java.lang.Math;
import java.applet.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.*;
import java.io.*;
import netscape.javascript.*;
import nomads.v210_auk.*;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view11x.ExponentialPortFader;
import com.softsynth.jsyn.view11x.LabelledFader;
import com.softsynth.jsyn.view11x.PortFader;
import com.softsynth.jsyn.view11x.SynthScope;

public class OperaMain extends Applet implements MouseListener, MouseMotionListener, ActionListener, Runnable {   

    private class NomadsAppThread extends Thread {
	OperaMain client; //Replace with current class name

	public NomadsAppThread(OperaMain _client) {
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

    int skipper = 0;
    int maxSkip = 5;
    Random randNum;
    int numOscs = 0;

    String imgPrefix;
    Image backgroundIce;
    URL imgWebBase;

    int     MAX_THREADS = 100000;
    int oscNum[];
    Boolean            isOsc[];
    LineOut            lineOut;
    int                lineOutType;
    BusReader          myBusReader;
    BusWriter          myBusWriter[];
    NoiseSwarm		myNoiseSwarm[];
    SynthEnvelope      	envData[];
    EnvelopePlayer     	envPlayer[];

    int t,i,j,clear, picker;
    int width,height,twidth,theight,fontSize, centerX, centerY;
    Font textFont;
    int startFontSize, minFontSize, maxFontSize;

    int tRow, tCol,rows,cols,tRows, tCols;
    int chatRows;

    int x,y,w,h,dx,dy,dw,dh;
    int wait;
    int pbi;

    int posX, posY, origX, origY; //STK used to get H value for frequency
    double origXScaler, origYScaler;
    double hMax, hMaxSqr;
    int diagonal;
    double diagonalSQ;
    float freqMultiply, mainVolumeFromSlider;
    double[] startFreq;
    double[][]	data;
    int[] startX;
    int[] startY;

    int padX, padY; 
    double weightX, weightY, weight;
    int stringLength;
    int tPass, tNewFontSize;
    int wordFound;

    Color textColor,backGroundColor;

    int chatSpace, chatXLoc, chatYLoc, tH;

    int chatA, cloudA, pointerA;
    int numChatColors,maxChatColors;
    int numCloudColors,maxCloudColors;
    int numPointerColors,maxPointerColors;
    int tChatColorNum, tCloudColorNum, tPointerColorNum;

    Color chatColors[] = new Color[8];

    Color cloudColors[];
    Color pointerColors[];

    JButton	clearButton;
    String chatLines[];
    int numChatLines = 30;
    int activeChatLines = 0;

    int mx, my; // recent mouse coords
    boolean isMouseDraggingBox = false;

    public class Sprite {
	int x, y;
	int r;
	int g;
	int b;
	int a;
	Color color;
    }

    Sprite sprites[];

    //  static final double MAX_MOD_DEPTH = 500.0;

    public class HistoElt {
	String text;  // The actual text we're printing
	int pass;  // What pass the word was entered (ie., time)
	int size, x, y, cols, quad;
	Font font;
	int r,g,b,a;
	Color color;
    }    

    // Number of times we've checked our global word list array
    int numPasses;

    // List of all our elements
    ArrayList<HistoElt> histoGram;
    //  Temporary placeholder
    HistoElt tHist;

    int guesser,rGuess,cGuess;

    Image offScreen;
    Graphics2D offScreenGrp;
    Image player;

    Font chatFont = new Font("TimesRoman", Font.PLAIN, 20);

    Color chatColor = chatColors[0]; 

    // mist vars made global to try and improve performance

    double tVolumeVal;
    int tNum;
    float tAmp;
    float tVolume;
    int scaledX;
    int scaledY;
    double myX, myY, myH_Sqr;
    double myH;
    double tFreq;
    String text;
    int xMin;
    int xVar;
    int yMin;
    int yVar;
    int xRand;
    int yRand;
    int xpoints[] = new int[4];
    int ypoints[] = new int[4];
    int txpoints[] = new int[4];
    int typoints[] = new int[4];
    int threadNum;
    int quad;
    int lastQuad;
    int numScreenCells;
    int curCell;
    int cellCtr;
    int cellSlots[];

    public static void main(String args[])
    {
	/* DO: Change TUT_SineFreq to match the name of your class. Must match file name! */


	OperaMain  applet = new OperaMain();

	int width = applet.getSize().width;
	int height = applet.getSize().height;

	AppletFrame frame = new AppletFrame("OperaMain", applet);
	frame.resize(width,height);

	//	frame.resize(700,400);
	frame.show();

	/* Begin test after frame opened so that DirectSound will use Java window. */
	frame.test();



    }

    // public void setChatColors(int alpha) {
    // 	chatColors[0] = new Color(158, 55, 33, alpha);
    //   	chatColors[1] = new Color(145, 86, 65, alpha);
    //   	chatColors[2] = new Color(187, 137, 44, alpha);
    //   	chatColors[3] = new Color(191, 140, 44, alpha);
    //   	chatColors[4] = new Color(233, 158, 37, alpha);
    //   	chatColors[5] = new Color(242, 197, 126, alpha);
    //   	chatColors[6] = new Color(254, 205, 129, alpha);
    //   	chatColors[7] = new Color(249, 241, 131, alpha);
    //     }
    // 
    //     public void setCloudColors(int alpha) {
    // 	cloudColors[0] = new Color(158, 55, 33, alpha);
    //   	cloudColors[1] = new Color(145, 86, 65, alpha);
    //   	cloudColors[2] = new Color(187, 137, 44, alpha);
    //   	cloudColors[3] = new Color(191, 140, 44, alpha);
    //   	cloudColors[4] = new Color(233, 158, 37, alpha);
    //   	cloudColors[5] = new Color(242, 197, 126, alpha);
    //   	cloudColors[6] = new Color(254, 205, 129, alpha);
    //   	cloudColors[7] = new Color(249, 241, 131, alpha);
    //     }
    // 
    //     public void setPointerColors(int alpha) {
    // 	pointerColors[0] = new Color(158, 55, 33, alpha);
    //   	pointerColors[1] = new Color(145, 86, 65, alpha);
    //   	pointerColors[2] = new Color(187, 137, 44, alpha);
    //   	pointerColors[3] = new Color(191, 140, 44, alpha);
    //   	pointerColors[4] = new Color(233, 158, 37, alpha);
    //   	pointerColors[5] = new Color(242, 197, 126, alpha);
    //   	pointerColors[6] = new Color(254, 205, 129, alpha);
    //   	pointerColors[7] = new Color(249, 241, 131, alpha);
    //     }

    public void setChatColors(int alpha) {
	chatColors[0] = new Color(145, 86, 65, alpha);
	chatColors[1] = new Color(145, 86, 65, alpha);
	chatColors[2] = new Color(145, 86, 65, alpha);
	chatColors[3] = new Color(145, 86, 65, alpha);
	chatColors[4] = new Color(145, 86, 65, alpha);
	chatColors[5] = new Color(145, 86, 65, alpha);
	chatColors[6] = new Color(145, 86, 65, alpha);
	chatColors[7] = new Color(145, 86, 65, alpha);
    }

    public void setCloudColors(int alpha) {
	cloudColors[0] = new Color(130, 240, 255, alpha);
	cloudColors[1] = new Color(130, 240, 255, alpha);
	cloudColors[2] = new Color(130, 240, 255, alpha);
	cloudColors[3] = new Color(130, 240, 255, alpha);
	cloudColors[4] = new Color(130, 240, 255, alpha);
	cloudColors[5] = new Color(130, 240, 255, alpha);
	cloudColors[6] = new Color(130, 240, 255, alpha);
	cloudColors[7] = new Color(130, 240, 255, alpha);
    }

    public void setPointerColors(int alpha) {
	pointerColors[0] = new Color(255, 255, 255, alpha);
	pointerColors[1] = new Color(255, 255, 255, alpha);
	pointerColors[2] = new Color(255, 255, 255, alpha);
	pointerColors[3] = new Color(255, 255, 255, alpha);
	pointerColors[4] = new Color(255, 255, 255, alpha);
	pointerColors[5] = new Color(255, 255, 255, alpha);
	pointerColors[6] = new Color(255, 255, 255, alpha);
	pointerColors[7] = new Color(255, 255, 255, alpha);
    }


    public void init()
    {  	

	int i;

	int screenCell = 4;
	numScreenCells = (int)(screenCell*screenCell);

	cellSlots = new int[numScreenCells];
	cellSlots[0] = 0;
	cellSlots[1] = 13;
	cellSlots[2] = 2;
	cellSlots[3] = 7;
	cellSlots[4] = 4;
	cellSlots[5] = 11;
	cellSlots[6] = 6;
	cellSlots[7] = 15;
	cellSlots[8] = 8;
	cellSlots[9] = 9;
	cellSlots[10] = 10;
	cellSlots[11] = 5;
	cellSlots[12] = 12;
	cellSlots[13] = 1;
	cellSlots[14] = 14;
	cellSlots[15] = 3;

	startX = new int[numScreenCells];
	startY = new int[numScreenCells];

	imgPrefix = "http://nomads.music.virginia.edu/images/";

	quad = 0;
	lastQuad = 0;

	try { 
	    imgWebBase = new URL(imgPrefix); 
	} 
	catch (Exception e) {}

	width = getSize().width;
	height = getSize().height;

	offScreen = createImage(width,height);
	offScreenGrp = (Graphics2D) offScreen.getGraphics();
	backgroundIce = getImage(imgWebBase,"BackgroundDisplay1.jpg");

	// backgroundIce = getImage(imgWebBase,"NOMADSMainDisplay_5760x1200Background.jpg");

	//	backgroundIce = getImage(imgWebBase,"NOMADSMainDisplay_11520x1200Background.jpg");

	offScreenGrp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	randNum = new Random();

	int wEnd = (int)(width * 0.8);
	int hEnd = (int)(height * 0.8);
	int wStart = (int)(width * 0.1);
	int hStart = (int)(height * 0.1);
	int wDiff = wEnd-wStart;
	int hDiff = hEnd-hStart;

	int wIncr = (int)(wDiff/screenCell);
	int hIncr = (int)(hDiff/screenCell);

	cellCtr = randNum.nextInt(screenCell);

	int tX;
	int tY;
	int cell = 0;

	for (tX = wStart; tX < wEnd; tX += wIncr) {
	    for (tY = hStart; tY < hEnd; tY += hIncr) {
		startX[cell] = tX;
		startY[cell] = tY;
		cell++;
	    }
	}

	centerX = (width/2);
	centerY = (height/2); 

	NGlobals.cPrint("width = " + width + "height = " + height);
	setBackground(Color.black);	

	chatLines = new String[numChatLines];

	chatA = 255;
	setChatColors(chatA);
	tChatColorNum = 0;
	maxChatColors = 7;

	cloudColors = new Color[8];
	cloudA = 180;
	setCloudColors(cloudA);
	tCloudColorNum = 0;
	maxCloudColors = 7;

	pointerColors = new Color[8];
	pointerA = 180;
	setPointerColors(cloudA);
	tPointerColorNum = 0;
	maxPointerColors = 7;

	i = 0;
	j = 0;
	clear = 0;

	histoGram = new ArrayList<HistoElt>();

	wordFound = 0;
	numPasses = 0;
	startFontSize = 30;
	minFontSize = 10;
	maxFontSize = 100;

	x = width / 2 - 20;
	y = height / 2 - 20;
	origX = x;
	origY = y;
	posX = origX;
	posY = origY;
		
	//****STK 8/6/12 TopLeftMin/Max values with origin in top left, from 0-1000 (eventually to be scaled to 0-1 floats)
	origXScaler = (double)(width/1000.0);
	origYScaler = (double)(height/1000.0);
	NGlobals.cPrint("OMP: origXScaler = " + origXScaler + " origYScaler = " + origYScaler);

	diagonalSQ = Math.pow(width, 2) + Math.pow(height, 2);
	diagonal = (int)(Math.sqrt(diagonalSQ)/2); //figure out 1/2 of diagonal
	startFreq = new double [9];
	// Frequencies: 883.61hz, 700.16hz, 624.90hz, 441.8hz, 343.56hz, 280.43hz, 214.29hz, 171.89hz, 129.17hz
	startFreq[0] = 883.61;
	startFreq[1] = 700.16;
	startFreq[2] = 624.9;
	startFreq[3] = 441.8;
	startFreq[4] = 343.56;
	startFreq[5] = 280.43;
	startFreq[6] = 214.29;
	startFreq[7] = 171.89;
	startFreq[8] = 129.17;

	freqMultiply = 2.0F;
	mainVolumeFromSlider = 1.0F;

		

	hMaxSqr = Math.pow(x, 2) + Math.pow(y, 2); //Pythagoras' Theorem 
	hMax = (float)Math.sqrt(hMaxSqr); //Maximum distance from center
	NGlobals.cPrint("Maximum H = " + hMax);

	addMouseListener(this);
	addMouseMotionListener(this);

	sprites = new Sprite[MAX_THREADS];
	isOsc = new Boolean[MAX_THREADS];
	oscNum = new int[MAX_THREADS];
	data  = new double[MAX_THREADS][];

	for (i=0;i<numChatLines;i++) {
	    chatLines[i] = "";
	}

	for (i=0;i<MAX_THREADS;i++) {
	    isOsc[i] = false;
	}

	try
	    {
		Synth.startEngine(0);

		myNoiseSwarm = new NoiseSwarm[MAX_THREADS];
		envPlayer = new EnvelopePlayer[MAX_THREADS];
		envData = new SynthEnvelope[MAX_THREADS];
		lineOut  = new LineOut();

		myBusReader = new BusReader();

		myBusWriter = new BusWriter[MAX_THREADS];

		/* Synchronize Java display to make buttons appear. */
		getParent().validate();
		getToolkit().sync();

		myBusReader.output.connect(0, lineOut.input, 0 );
		myBusReader.output.connect(0, lineOut.input, 1 );

		myBusReader.start();
		lineOut.start();

		//	myBusReader.output.connect(lineOut.input, 0 );


	    } catch(SynthException e) {
	    SynthAlert.showError(this,e);
	}
	operaSand = new NSand(); 
	operaSand.connect();

	nThread = new NomadsAppThread(this);
	nThread.start();
	int d[] = new int[1];
	d[0] = 0;
	operaSand.sendGrain((byte)NAppID.OPERA_MAIN, (byte)NCommand.REGISTER, (byte)NDataType.UINT8, 1, d );

	repaint();
    }	

    public void makeSynth(int threadNum) {

	int tNum;
	float tAmp;
	Random randStartFreq;
	randStartFreq = new Random();
	int tStartFreq = randStartFreq.nextInt(8);
	NGlobals.cPrint("tstartFreq = " + tStartFreq);
	if (!isOsc[threadNum]) {

	    sprites[threadNum] = new Sprite();
	    sprites[threadNum].x = width / 2 - 20;
	    sprites[threadNum].y = height / 2 - 20;
	    //	    sprites[threadNum].color = new Color(randNum.nextInt(255),randNum.nextInt(255),randNum.nextInt(255), pointerA);
	    // sprites[threadNum].r = randNum.nextInt(255);
	    // 	    sprites[threadNum].g = randNum.nextInt(255);
	    // 	    sprites[threadNum].b = randNum.nextInt(255);
	    sprites[threadNum].r = 255;
	    sprites[threadNum].g = 255;
	    sprites[threadNum].b = 255;
	    sprites[threadNum].a = pointerA;

	    oscNum[numOscs++] = threadNum;
	    myNoiseSwarm[threadNum] = new NoiseSwarm();
	    envPlayer[threadNum] = new EnvelopePlayer();

	    for (int i=0;i<numOscs;i++) {
		tNum = oscNum[i];
		tAmp = (float)2/numOscs; //default amp = 2.0
		NGlobals.cPrint(i + ":resetting amp for osc " + tNum + " to " + tAmp);
		myNoiseSwarm[tNum].amplitude.set(tAmp * mainVolumeFromSlider);
		float tVolume = tAmp * mainVolumeFromSlider;
		NGlobals.cPrint("Amplitude = " + tVolume);
	    }

	    envPlayer[threadNum].output.connect( myNoiseSwarm[threadNum].frequency );

	    // define shape of envelope as an array of doubles
	    data[threadNum] = new double[2];
	    data[threadNum][0] = 0.1; //time point value
	    data[threadNum][1] = (startFreq[tStartFreq]); //frequency
	    NGlobals.cPrint( "starting freq= " + data[threadNum][1]);

	    envData[threadNum] = new SynthEnvelope( 1 );
	    envData[threadNum].write(0, data[threadNum], 0, 1); // 1 = number of frames

	    myBusWriter[threadNum]   = new BusWriter(); /* Create bus writers. */

	    myNoiseSwarm[threadNum].output.connect(myBusWriter[threadNum].input);
	    myBusWriter[threadNum].busOutput.connect( myBusReader.busInput );	    
	    myNoiseSwarm[threadNum].frequency.set((float)(startFreq[tStartFreq]));

	    envPlayer[threadNum].start();
	    myNoiseSwarm[threadNum].start();
	    myBusWriter[threadNum].start();
	    isOsc[threadNum] = true;
	    envPlayer[threadNum].envelopePort.queue( envData[threadNum] );

	}
	else {
	    NGlobals.cPrint("Synth already created for thread: " + threadNum);

	}
    }


    // For swarm display

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
	mx = e.getX();
	my = e.getY();
	if (x < mx && mx < x + 40 && y < my && my < y + 40) {
	    isMouseDraggingBox = true;
	}
	e.consume();
    }

    public void mouseReleased(MouseEvent e) {
	/*if (isMouseDraggingBox) {
	  try {
	  double myx = (mx - (width / 2)) / ((double) width * 3);
	  double myy = (my - (height / 2)) / ((double) height * 3);
	  String towrite = "move" + " " + personnum + " " + myx + " "
	  + myy;
	  System.out.println("Byte is " + app_id.STUDENT_SAND_POINTER + " and write is "
	  + towrite);
	  streamOut.writeByte(app_id.STUDENT_SAND_POINTER);
	  streamOut.writeUTF(towrite);
	  } catch (IOException ioe) {
	  System.out.println("Error writing...");
	  }
	  }*/
	isMouseDraggingBox = false;
	e.consume();
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
	if (isMouseDraggingBox) {
	    // get the latest mouse position
	    int new_mx = e.getX();
	    int new_my = e.getY();

	    // displace the box by the distance the mouse moved since the last
	    // event
	    // Note that "x += ...;" is just shorthand for "x = x + ...;"
	    // if (new_mx < 5)
	    //	new_mx = 5;
	    // if (new_mx > 890)
	    //	new_mx = 890;
	    // if (new_my < 5)
	    //	new_my = 5;
	    // if (new_my > 590)
	    //	new_my = 590;

	    x += new_mx - mx;
	    y += new_my - my;

	    // update our data
	    mx = new_mx;
	    my = new_my;


	    repaint();
	    e.consume();
	}
    }

    public void update ( Graphics g ) {
	paint (g);
    }

    // ------------------------------------------------------------------------------------------------
    // BEGIN handle()
    // ------------------------------------------------------------------------------------------------

    public void handle() { //bite text
	int i,j,fc,sc,x,y;
	float freq,amp;
	String temp,tAlpha,input, tTest;
	int THREAD_ID;
	float xput,yput;

	int incCmd, incAppID, incDType, incDLen;
	int incIntData[] = new int[1000];
	byte incByteData[] = new byte[1000];  // Cast as chars here because we're using chars -> strings
	NGrain grain;

	NGlobals.cPrint("OperaMain -> handle()");

	grain = operaSand.getGrain();
	grain.print(); //prints grain data to console

	incAppID = grain.appID;
	incCmd = grain.command;

	NGlobals.cPrint("...");
	NGlobals.cPrint("OM: incAppID= " + incAppID + " incCmd= " + incCmd);
	NGlobals.cPrint("...");


	// CONDUCTOR PANEL ================================================================================

	if (incAppID == NAppID.CONDUCTOR_PANEL) {


	    if (incCmd == NCommand.SET_DISCUSS_ALPHA) {
		chatA = grain.iArray[0];
		setChatColors(chatA);
		NGlobals.cPrint("Setting ChA to " + chatA);
		repaint();
	    }

	    else if (incCmd == NCommand.SET_CLOUD_ALPHA) {
		cloudA = grain.iArray[0];;
		setCloudColors(cloudA);
		NGlobals.cPrint("Setting ClA to " + cloudA);
		repaint();
	    }
	    else if (incCmd == NCommand.SET_POINTER_ALPHA) {
		pointerA = grain.iArray[0];;
		setPointerColors(pointerA);
		NGlobals.cPrint("Setting PtA to " + pointerA);
		repaint();
	    }
	    else if (incCmd == NCommand.SET_CLOUD_DISPLAY_STATUS) {  // Cloud reset
		if(grain.bArray[0] == 0) {
		    histoGram.clear();

		    // int tSize = histoGram.size();
		    // for (i=0;i<tSize;i++) {
		    //     histoGram.remove(i);
		    // }
		    NGlobals.cPrint("Resetting cloud...\n");
		    repaint();
		}
	    }
	    else if (incCmd == NCommand.SET_DISCUSS_DISPLAY_STATUS) {  // Discuss reset
		if(grain.bArray[0] == 0) {
		    for (i=0;i<numChatLines;i++) {
			chatLines[i] = "";
		    }
		    tH = (int)(height*1.1);
		    chatSpace = tH/numChatLines;
		    chatYLoc = height-chatSpace;
		    chatXLoc = 20;
		    NGlobals.cPrint("Resetting discuss...\n");
		    repaint();
		}
	    }
	    else if (incCmd == NCommand.SET_SYNTH_VOLUME) {	
		tVolumeVal = (double)grain.iArray[0];
		mainVolumeFromSlider = (float)(Math.pow(tVolumeVal, 2)/10000.0);
		for (i=0;i<numOscs;i++) {
		    tNum = oscNum[i];
		    tAmp = (float)2/numOscs; //default amp = 2.0
		    NGlobals.cPrint(i + ":resetting amp for osc " + tNum + " to " + tAmp);
		    myNoiseSwarm[tNum].amplitude.set(tAmp * mainVolumeFromSlider);
		    tVolume = tAmp * mainVolumeFromSlider;
		    NGlobals.cPrint("Amplitude = " + tVolume);
		}
		//TO DO: Make this a log function. . .
	    }




	}

	// ========= Pointer (regular) ============================================

	else if (incAppID == NAppID.OC_POINTER) {
	    NGlobals.cPrint("OMP: OC_POINTER\n");
	    if (grain.command == NCommand.SEND_SPRITE_THREAD_XY) {
		THREAD_ID = grain.iArray[0];
		x = grain.iArray[1];
		y = grain.iArray[2];
		NGlobals.cPrint("SOUND_SWARM_DISPLAY::  got SEND_SPRITE_XY from SOUND_SWARM: " + x + "," + y);

		makeSynth(THREAD_ID);

		NGlobals.cPrint("OMP: THREAD_ID = " + THREAD_ID);

		freq = (float)x;
		amp = (float)(y/1000);

		//				float fx = (float)(x+1000)/(float)2000;
		//				float fy = (float)(y+1000)/(float)2000;
		//
		//				x = (int)(fx*width);
		//				y = (int)(fy*height);
				
		scaledX = (int)(x * origXScaler);
		scaledY = (int)(y * origYScaler);
		x = scaledX;
		y = scaledY;

		NGlobals.cPrint("OMP: x = " + x);
		//		amp = 1;
		NGlobals.cPrint("OMP: y = " + y);
		NGlobals.cPrint("OMP: scaledX = " + scaledX);
		//		amp = 1;
		NGlobals.cPrint("OMP: scaledY = " + scaledY);

		//if (x > 900)
		//	x = 900;
		//				xput = (float)(x/0.5);
		xput = x;
		if (xput < 50)
		    xput = 50;
		if (xput > 900)
		    xput = 900;

		//if (y > 900)
		//	y = 900;

		//			yput = (float)((y/0.5));
		yput = y;
		if (yput < 0)
		    yput = 0;
		if (yput > 900)
		    yput = 900;


		//=============== STK code to get H value for frequency =======================

		double myX, myY, myH_Sqr;
		double myH;		


		if (isOsc[THREAD_ID]) {
		    NGlobals.cPrint("setting osc values for thread: " + THREAD_ID);

		    sprites[THREAD_ID].x = x;
		    sprites[THREAD_ID].y = y;

		    if (x >= origX)
			myX = (double)(x - origX); //if X value is bigger than origin value, distance = X-origin (x - 230)
		    else 
			myX = (double)(origX - x);

		    if (posY >= y)
			myY = (double)(y - origY);
		    else
			myY = (double)(origY - y);

		    NGlobals.cPrint( "x = " + x + "y = " + y + "myX = " + myX + "myY" + myY);


		    myH_Sqr = Math.pow(myX, 2) + Math.pow(myY, 2); //Pythagoras' Theorem 

		    myH = Math.sqrt(myH_Sqr); //distance from center
		    NGlobals.cPrint( "H = " + myH + "Diagonal = " + diagonal);

		    // double tFreq = (float)( 10.00 * Math.pow(1.005, myH));
		    tFreq = (float)myH * 4.0;

		    if (tFreq > 22050.0)
			tFreq = 22050.0;

		    if (tFreq < 20.0)
			tFreq = 20.0;

		    NGlobals.cPrint("tFreq " + THREAD_ID + " set to " + tFreq);
		    data[THREAD_ID][1] = tFreq;
		    //	System.out.println("data[1] = " + data[THREAD_ID][1]);
		    envData[THREAD_ID].write(0, data[THREAD_ID], 0, 1); // 1 = number of frames
		    envPlayer[THREAD_ID].envelopePort.clear();
		    envPlayer[THREAD_ID].envelopePort.queue( envData[THREAD_ID] );

		    repaint();
		}
	    }
	}

	// ========= Pointer (Java based) ============================================

	else if (incAppID == NAppID.JOC_POINTER) {
	    NGlobals.cPrint("OMP: JOC_POINTER\n");
	    if (grain.command == NCommand.SEND_SPRITE_THREAD_XY) {
		THREAD_ID = grain.iArray[0];
		x = grain.iArray[1];
		y = grain.iArray[2];
		NGlobals.cPrint("SOUND_SWARM_DISPLAY::  got SEND_SPRITE_XY from SOUND_SWARM: " + x + "," + y);

		makeSynth(THREAD_ID);

		NGlobals.cPrint("OMP: THREAD_ID = " + THREAD_ID);

		freq = (float)x;
		amp = (float)(y/1000);

		//				float fx = (float)(x+1000)/(float)2000;
		//				float fy = (float)(y+1000)/(float)2000;
		//
		//				x = (int)(fx*width);
		//				y = (int)(fy*height);
				
		scaledX = (int)(x * origXScaler);
		scaledY = (int)(y * origYScaler);
		x = scaledX;
		y = scaledY;

		NGlobals.cPrint("OMP: x = " + x);
		//		amp = 1;
		NGlobals.cPrint("OMP: y = " + y);
		NGlobals.cPrint("OMP: scaledX = " + scaledX);
		//		amp = 1;
		NGlobals.cPrint("OMP: scaledY = " + scaledY);

		//if (x > 900)
		//	x = 900;
		//				xput = (float)(x/0.5);
		xput = x;
		if (xput < 50)
		    xput = 50;
		if (xput > 900)
		    xput = 900;

		//if (y > 900)
		//	y = 900;

		//			yput = (float)((y/0.5));
		yput = y;
		if (yput < 0)
		    yput = 0;
		if (yput > 900)
		    yput = 900;


		//=============== STK code to get H value for frequency =======================

		double myX, myY, myH_Sqr;
		double myH;		


		if (isOsc[THREAD_ID]) {
		    NGlobals.cPrint("setting osc values for thread: " + THREAD_ID);

		    sprites[THREAD_ID].x = x;
		    sprites[THREAD_ID].y = y;

		    if (x >= origX)
			myX = (double)(x - origX); //if X value is bigger than origin value, distance = X-origin (x - 230)
		    else 
			myX = (double)(origX - x);

		    if (posY >= y)
			myY = (double)(y - origY);
		    else
			myY = (double)(origY - y);

		    NGlobals.cPrint( "x = " + x + "y = " + y + "myX = " + myX + "myY" + myY);


		    myH_Sqr = Math.pow(myX, 2) + Math.pow(myY, 2); //Pythagoras' Theorem 

		    myH = Math.sqrt(myH_Sqr); //distance from center
		    NGlobals.cPrint( "H = " + myH + "Diagonal = " + diagonal);

		    // double tFreq = (float)( 10.00 * Math.pow(1.005, myH));
		    tFreq = (float)myH * 4.0;

		    if (tFreq > 22050.0)
			tFreq = 22050.0;

		    if (tFreq < 20.0)
			tFreq = 20.0;

		    NGlobals.cPrint("tFreq " + THREAD_ID + " set to " + tFreq);
		    data[THREAD_ID][1] = tFreq;
		    //	System.out.println("data[1] = " + data[THREAD_ID][1]);
		    envData[THREAD_ID].write(0, data[THREAD_ID], 0, 1); // 1 = number of frames
		    envPlayer[THREAD_ID].envelopePort.clear();
		    envPlayer[THREAD_ID].envelopePort.queue( envData[THREAD_ID] );

		    //	myNoiseSwarm[THREAD_ID].frequency.set(((startFreq + myH) * freqMultiply));
		    if (skipper == 0)
			repaint();
		    skipper++;
		    if (skipper > maxSkip)
			skipper = 0;
		}
	    }
	}

	// ========= CLOUD INPUT ============================================

	else if (incAppID == NAppID.OC_CLOUD) {
	    if (incCmd == NCommand.SEND_MESSAGE) {
		text = new String(grain.bArray);
		NGlobals.cPrint("OM: CloudText: " + text);
		NGlobals.cPrint("OC_CLOUD\n");

		stringLength = text.length(); 

		// Then check text locations to avoid collisions *************************

		wordFound = 0;

		// Check our histogram =============================================

		for (i=0;i<histoGram.size();i++) {
		    tHist = histoGram.get(i);
		    NGlobals.cPrint("...");
		    NGlobals.cPrint("checking histogram ----- tHist.text = ||>> " + tHist.text + " <<||");
		    NGlobals.cPrint("...");
		    NGlobals.cPrint("  tHist.size = " + tHist.size);
		    NGlobals.cPrint("...");
		    NGlobals.cPrint(" histoGram.size() = " + histoGram.size());

		    // 1.  Histogram element [i] matches incoming text -----

		    x=tHist.x;
		    y=tHist.y;	       	

		    if (tHist.text.compareToIgnoreCase(text) == 0) {
			wordFound = 1;
			NGlobals.cPrint("...");
			NGlobals.cPrint(">>>FOUND " + tHist.text + " at [" + x + "]" + "[" + y + "]");
			NGlobals.cPrint("  INCreasing text size");

			// This will change to be a combination of rank v time (ie., numPasses);
			tHist.size += 4;

			quad = tHist.quad;
			NGlobals.cPrint("  quad = " + quad);
			if (quad > 2) {
				tHist.x-=8;
				tHist.y-=5;
				if (tHist.x < centerX)
				    tHist.x = centerX;
				if (tHist.y < centerY)
				    tHist.y = centerY;
			}
			else if (quad > 1) {
				tHist.x-=8;
				tHist.y+=5;
				if (tHist.x < centerX)
				    tHist.x = centerX;
				if (tHist.y > centerY)
				    tHist.y = centerY;
			}
			else if (quad > 0) {
				tHist.x+=6;
				tHist.y+=5;
				if (tHist.x > centerX)
				    tHist.x = centerX;
				if (tHist.y > centerY)
				    tHist.y = centerY;
			}
			else {
				tHist.x+=6;
				tHist.y-=5;
				if (tHist.x > centerX)
				    tHist.x = centerX;
				if (tHist.y < centerY)
				    tHist.y = centerY;
			}

			if (tHist.size > maxFontSize) {
			    fontSize = maxFontSize;
			}
			else {
			    fontSize = tHist.size;
			}
			tHist.font = new Font("TimesRoman", Font.PLAIN, fontSize);

			//i = histoGram.size();  // exit the loop

		    }

		    // 2a.  Blank cell ... do nothing
		    else if (tHist.text.compareToIgnoreCase("") == 0) {
			NGlobals.cPrint("|_|");
		    }

		    // 2.  Histogram element [i] DOES NOT match incoming text and is > min size -----

		    else if (tHist.size > minFontSize) {  // Decrease size (if > min AND modulo 2)
			NGlobals.cPrint("...");
			NGlobals.cPrint("  DECreasing word size for " + tHist.text);
			NGlobals.cPrint("...");
			NGlobals.cPrint("  numPasses = " + numPasses);
			if (numPasses%2 == 0) {
			    tHist.size--;
			    quad = tHist.quad;
			    if (quad > 2) {
			        tHist.x+=2;
			        tHist.y+=2;
			    }
			    else if (quad > 1) {
			        tHist.x+=2;
			        tHist.y-=2;
			    }
			    else if (quad > 0) {
			        tHist.x-=2;
			        tHist.y-=2;
			    }
			    else {
			        tHist.x-=2;
			        tHist.y+=2;
			    }
			    if (tHist.x < 10) 
			        tHist.x = 10;
			    if (tHist.x > (width-10))
			        tHist.x = width-10;
			    if (tHist.y < 10) 
			        tHist.y = 10;
			    if (tHist.y > (height-10))
			        tHist.y = height-10;
			}
			if (tHist.size > maxFontSize)
			    tHist.size = maxFontSize;
			fontSize = tHist.size;
			tHist.font = new Font("TimesRoman", Font.PLAIN, fontSize);

			NGlobals.cPrint("...");

			NGlobals.cPrint("  tHist.x,tHist.y = " + tHist.x + "," + tHist.y);
			NGlobals.cPrint("...");
			NGlobals.cPrint("  tHist.size= " + tHist.size);
		    }

		    // 3.  Histogram element [i] DOES NOT match and is < min size ... delete it

		    else if (tHist.size <= minFontSize) {
			NGlobals.cPrint("...");
			NGlobals.cPrint("  tHist.x,tHist.y = " + tHist.x + "," + tHist.y);
			NGlobals.cPrint("...");
			NGlobals.cPrint("  tHist.size= " + tHist.size);
			NGlobals.cPrint("...");
			NGlobals.cPrint("  REMoving word: " + tHist.text + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

			fontSize = tHist.size;
			if (fontSize < minFontSize)
			    fontSize = minFontSize;

			histoGram.remove(i);
			i--;
		    }			  	
		}  // end for (i=0;i<histoGram.size();i++)


		// No words found, add new word and store relevant data
		if (wordFound == 0) {
		    // Figure out where to put the text =============================================

		    // Find a free cell
		    picker = 1;

		    NGlobals.cPrint("...");

		    // figure out center, then expand range over time 

		    x=y=0;

		    tHist = new HistoElt();
		    tHist.text = new String(text);

		    xMin = (int)(width * 0.0);
		    xVar = (int)(width * 0.4);

		    yMin = (int)(height * 0.0);
		    yVar = (int)(height * 0.4);

		    lastQuad = quad;
		    quad = randNum.nextInt(5);
		    while (quad == lastQuad) {
			quad = randNum.nextInt(5);
		    }
		    tHist.quad = quad;
		    NGlobals.cPrint(">>>NEW WORD " + tHist.text + " at [" + x + "]" + "[" + y + "]");
		    NGlobals.cPrint("setting quad = " + quad);

		    xRand = xMin + randNum.nextInt(xVar);
		    yRand = yMin + randNum.nextInt(yVar);
		    
		    curCell = cellSlots[cellCtr];

		    x = startX[curCell]+randNum.nextInt(100);
		    y = startY[curCell]+randNum.nextInt(100);

		    cellCtr++;
		    if (cellCtr >= numScreenCells)
			cellCtr = 0;

		    // if (quad > 2) {
		    // 	x = centerX + xRand;
		    // 	y = centerY + yRand;
		    // }
		    // else if (quad > 1) {
		    // 	x = centerX + xRand;
		    // 	y = centerY - yRand;
		    // }
		    // else if (quad > 0) {
		    // 	x = centerX - xRand;
		    // 	y = centerY - yRand;
		    // }
		    // else  {
		    // 	x = centerX - xRand;
		    // 	y = centerY + yRand;
		    // }

		    if ((x > centerX) && (y > centerY)) {
		        quad = 3;
		    }
		    if ((x > centerX) && (y < centerY)) {
		        quad = 2;
		    }
		    if ((x < centerX) && (y < centerY)) {
		        quad = 1;
		    }
		    if ((x < centerX) && (y > centerY)) {
		        quad = 0;
		    }
		    tHist.quad = quad;

		    NGlobals.cPrint("<<<< ADDING new word: " + text + " at " + "[" + x + "]" + "[" + y + "]");

		    tHist.color = cloudColors[tCloudColorNum]; 	
		    tCloudColorNum++;
		    if (tCloudColorNum > maxCloudColors)
			tCloudColorNum = 0;	

		    fontSize = tHist.size = startFontSize;
		    tHist.pass = numPasses;

		    tHist.x = x;
		    tHist.y = y;

		    tHist.font = new Font("TimesRoman", Font.PLAIN, tHist.size);
		    histoGram.add(tHist);

		    // This will change to be a combination of rank v time (ie., numPasses);

		    // DRAW THE TEXT ======================================

		    NGlobals.cPrint("...");
		    NGlobals.cPrint("Drawing word: " + text);

		}

		// CODE TO CLEAR THE SCREEN, NOT USED AS OF 2/15/2010 ============================
		//**** If we fill up the cells they clear SK 12/03/09
		//****Ultimately we should make a button that does this

		clear = 0;
		if (clear == 1) {
		    NGlobals.cPrint("CLEAR!");
		    histoGram.clear();
		    clear = 0;
		    i = 0;
		    j = 0;
		    clear = 0;
		    guesser = 0;		
		    NGlobals.cPrint("CLEAR:  clearing rows/cols");
		}

		// END CLEAR CODE ==================================================================

		numPasses++;
		NGlobals.cPrint("...");
		NGlobals.cPrint("END handle(" + text + ") numPasses = " + numPasses + " -----");
		repaint();
	    }
	}
	// END OC_CLOUD ------------------------------------------------------------------------------------

	// OC_DISCUSS ============================================================================================

	else if (incAppID == NAppID.OC_DISCUSS) {
	    if (incCmd == NCommand.SEND_MESSAGE) {
		text = new String(grain.bArray);
		NGlobals.cPrint("OM: DiscussText: " + text);
		for (i=(numChatLines-1);i>0;i--) {
		    chatLines[i] = chatLines[i-1];
		}
		chatLines[0] = text;
		repaint();
	    }
	}

	//		if (bite == app_id.MONITOR) {
	//			if (text.equals("CHECK")) {
	//				try {
	//					streamOut.writeByte((byte)app_id.MONITOR);
	//					streamOut.writeUTF("PING");
	//				}
	//				catch(IOException ioe) {
	//					System.out.println("Error writing to output stream: ");
	//				}
	//			}	 
	//		}   


	NGlobals.cPrint ("-------------------------------------------------[OM]\n");
    }

    // ------------------------------------------------------------------------------------------------
    // END handle()
    // ------------------------------------------------------------------------------------------------

    public void actionPerformed( ActionEvent ae )
    {
	Object obj = ae.getSource();
	if( obj == clearButton ) {
	    NGlobals.cPrint("You pressed clear");
	    NGlobals.cPrint("...");
	    NGlobals.cPrint("actionPerformed():  clearing histogram");
	    for (i=0;i<histoGram.size();i++) {

		NGlobals.cPrint("...");
		String tempString = Integer.toString(histoGram.size());

		NGlobals.cPrint(tempString);
		tHist = histoGram.get(i);
		tHist.pass = 0;
		x=tHist.x;
		y=tHist.y;
		tHist.font = new Font("TimesRoman", Font.PLAIN, startFontSize);
	    }
	    NGlobals.cPrint("CLEAR!");
	    histoGram.clear();
	    clear = 0;
	    i = 0;
	    j = 0;
	    clear = 0;
	    guesser = 0;		
	    NGlobals.cPrint("...");
	    NGlobals.cPrint("actionPerformed():  clearing rows/cols");
	}

    }
    // DT 6/30/10:  not sure we need these anymore

    public void start() {

    }

    public void run () {
	if (i == 1) {
	} 
    }

    public void paint(Graphics g) {
	int tx, ty, r,gr,b,a;
	Color tc;
	int ksize = 7;
	int ssize = 5;


	int len1,len2;
	//	g.dispose();
	//setBackground(Color.black);
	//g.setColor(Color.black);
	super.paint(g);
	//setBackground(Color.black);
	// g.setColor(Color.black); //STK here's where to change the background color

	// DT DB method
	offScreenGrp.drawImage(backgroundIce, 0, 0, width, height, this);

	// DT old method
	// g.drawImage(backgroundIce, 0, 0, width, height, this);
	//g.fillRect(0,0,width,height);

	//g.setPaintMode();
	//g.dispose();
	xpoints[0] = x-(int)(ksize/2);
	xpoints[1] = x+(int)(ksize/2);
	xpoints[2] = x+(int)(ksize*1.5);
	xpoints[3] = x+(int)(ksize/2);

	ypoints[0] = y+(int)(ksize/1);
	ypoints[1] = y-(int)(ksize/1);
	ypoints[2] = y+(int)(ksize/1);
	ypoints[3] = y+(int)(ksize*3);

	//g.setColor(Color.RED);
	//g.fillPolygon(xpoints, ypoints, xpoints.length);

	//	setBackground(Color.black);

	for (int i=0;i<numOscs;i++) {
	    threadNum = oscNum[i];
	    tx = sprites[threadNum].x;
	    ty = sprites[threadNum].y;

	    r = sprites[threadNum].r;
	    gr = sprites[threadNum].g;
	    b = sprites[threadNum].b;
	    a = pointerA;
	    tc = new Color(r,gr,b,a);

	    offScreenGrp.setColor(tc);

	    if (numOscs > 1) {
		len1 = 0;
		if (i > 0) {
		    int tt1 = oscNum[i-1];
		    int x2 = sprites[tt1].x;
		    int y2 = sprites[tt1].y;
		    // len1 = sqrt(abs(tx-x2)+abs(ty-y2));	
		    offScreenGrp.drawLine(tx,ty,x2,y2);
		    len2 = 0;
		    if ((numOscs > 2) && (i > 1)) {
			tt1 = oscNum[i-2];
			x2 = sprites[tt1].x;
			y2 = sprites[tt1].y;
			// len2 = sqrt(abs(tx-x2)+abs(ty-y2));
			offScreenGrp.drawLine(tx,ty,x2,y2);
		    }
		}
	    }
	    // if (len1 != 0) {
	    // 	ssize += 100/len1;
	    // 	if (len2 != 0) {
	    // 	    ssize += ((100/len2) + (100/len1));
	    // 	}
	    // }


	    txpoints[0] = tx-(int)(ssize/2);
	    txpoints[1] = tx+(int)(ssize/2);
	    txpoints[2] = tx+(int)(ssize*1.5);
	    txpoints[3] = tx+(int)(ssize/2);

	    typoints[0] = ty+(int)(ssize/2);
	    typoints[1] = ty-(int)(ssize/2);
	    typoints[2] = ty+(int)(ssize/2);
	    typoints[3] = ty+(int)(ssize*1.5);

	    offScreenGrp.fillPolygon(txpoints, typoints, txpoints.length);
	    //	    g.fillRect(tx, ty, 10, 10);
	}

	//	setBackground(Color.black);

	for (i=0;i<histoGram.size();i++) {
	    tHist = histoGram.get(i);

	    r = tHist.color.getRed();
	    gr = tHist.color.getGreen();
	    b = tHist.color.getBlue();
	    a = cloudA;

	    tc = new Color(r,gr,b,a);

	    offScreenGrp.setColor(tc);
	    offScreenGrp.setFont(tHist.font);
	    offScreenGrp.drawString(tHist.text, tHist.x, tHist.y);
	}

	tH = (int)(height*1.1);
	chatSpace = tH/numChatLines;
	chatYLoc = height-chatSpace;
	chatXLoc = 20;



	offScreenGrp.setFont(chatFont);
	offScreenGrp.setColor(chatColor);

	for (i=0;i<numChatLines;i++) {
	    offScreenGrp.drawString(chatLines[i], chatXLoc, chatYLoc);
	    chatYLoc -= chatSpace;
	}

	g.drawImage(offScreen, 0, 0, width, height, this);


    }

}
