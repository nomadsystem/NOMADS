import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.*;
import java.io.*;
import nomads.v210.*;
import java.awt.image.BufferedImage;
import java.awt.image.*;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view11x.ExponentialPortFader;
import com.softsynth.jsyn.view11x.LabelledFader;
import com.softsynth.jsyn.view11x.PortFader;
import com.softsynth.jsyn.view11x.SynthScope;
 
public class SandPointerDisplay extends JPanel implements MouseListener, MouseMotionListener
{

    int THREAD_ID;

    // will always need this
    int numOscs = 0;
    private int BackgroundNum = 0;

    String imgPrefix;
    Image backgroundImg;
    URL imgWebBase;
    public Boolean soundStatus = false;

    Random randNum;

    // specific to my app
    int counter;
    JLabel adda;
    JButton button;

    int     MAX_THREADS = 100000;
    SineOscillator     modOsc[];
    int oscNum[];
    SineOscillator carOsc[];
    AddUnit            freqAdder[];
    Boolean            isOsc[];
    Boolean            oscCheck[];
    int tOscNum[];
    
    LineOut            lineOut;
    int                lineOutType;
    BusReader          myBusReader;
    BusWriter          myBusWriter[];

    PortFader          centerFader;
    PortFader          modFreqFader;
    PortFader          modRangeFader;

    // For swarm video display

    int width, height;
    int x, y; // coords of the box
    int mx, my; // recent mouse coords
    boolean isMouseDraggingBox = false;

    NSand mySand;
    
    public class Sprite {
	int x, y;
	Color color;
    }

    Sprite sprites[];

    static final double MAX_MOD_DEPTH = 500.0;


    public void print(String str) {
	if (1 < 0) {
	    System.out.println(str);
	}
    }

    public void hPrint(String str) {
	if (1 < 0) {
	    System.out.println(">" + str);
	}
    }


    public synchronized void deleteOsc(int i) {
	isOsc[i] = false;
    }

    public synchronized void deleteSprite(int i) {
	sprites[i] = null;
    }

    public synchronized Sprite getSprite(int i) {
	return sprites[i];
    }


    // public static void main(String args[])
    // {
    // 	DO: Change TUT_SineFreq to match the name of your class. Must match file name!
    // 	AppletFrame frame = new AppletFrame("SandPointerDisplay", applet);
    // 	frame.resize(500,400);
    // 	frame.show();
    // 	Begin test after frame opened so that DirectSound will use Java window.
    // 	frame.test();
    // }
    

    public void setAllSynthVol(int vol) {
	int threadNum, tNum,i;
	float tAmp;
	if (vol > 0) {
	    for (i=0;i<numOscs;i++) {
		threadNum = i;
		tNum = oscNum[i];
		tAmp = (float)(1.0/(numOscs+1));
		//hPrint(i + ":resetting amp for osc " + tNum + " to " + tAmp);

		// modOsc[tNum].frequency.set(200.0);  // MF
		// carOsc[tNum].frequency.set(400.0);  // CF
		
		modOsc[tNum].amplitude.set(100.0);
		freqAdder[tNum].inputB.set(200.0);  // CF

		// modOsc[tNum].amplitude.set(0.5);
		carOsc[tNum].amplitude.set(tAmp);
	    }
	    // freqAdder[threadNum].amplitude.set(0.5);
	}
	else {
	    for (i=0; i<numOscs; i++) {
		tNum = oscNum[i];
		tAmp = (float)(1.0/(numOscs+1));
		//hPrint(i + ":resetting amp for osc " + tNum + " to " + tAmp);
		modOsc[tNum].amplitude.set(0);
		carOsc[tNum].amplitude.set(0);
	    }
	}
    }
 
    public void setBackground(int i) {
	NGlobals.dtPrint("  ---> setBackground(" + i + ")");
	BackgroundNum = i;
	repaint();
    }

    public void init(NSand inSand)
    {

	mySand = inSand;

	int i;

	THREAD_ID=0;

	String tString = new String("REGISTER");
	int tLen = tString.length();
	byte[] tStringAsBytes = tString.getBytes();

	// mySand.sendGrain((byte)NAppID.SOUND_SWARM_DISPLAY, (byte)NCommand.REGISTER, (byte)NDataType.BYTE, tLen, tStringAsBytes);

	randNum = new Random();

	width = 800;
	height = 800;
	// setBackground(Color.black);
	
	// imgPrefix = "http://nomads.music.virginia.edu/images/";
	// try { 
	//     imgWebBase = new URL(imgPrefix); 
	// } 
	// catch (Exception e) {}

	// backgroundImg = getImage(imgWebBase,"NOMADS_world_map.jpg");

	x = width / 2 - 20;
	y = height / 2 - 20;
	
	addMouseListener(this);
	addMouseMotionListener(this);

	sprites = new Sprite[MAX_THREADS];
	oscNum = new int[MAX_THREADS];

	isOsc = new Boolean[MAX_THREADS];
	oscCheck = new Boolean[MAX_THREADS];
	tOscNum = new int[MAX_THREADS];


	for (i=0;i<MAX_THREADS;i++) {
	    isOsc[i] = false;
	}
	
	try
	    {
		Synth.startEngine(0);
		/* DO: Your setup code goes here. ******************/
		/* Create unit generators. */
		modOsc = new SineOscillator[MAX_THREADS];
		carOsc = new SineOscillator[MAX_THREADS];
		freqAdder = new AddUnit[MAX_THREADS];
		lineOut  = new LineOut();
		    
		myBusReader = new BusReader();
		
		myBusWriter = new BusWriter[MAX_THREADS];
		    
		/* Synchronize Java display to make buttons appear. */
		// getParent().validate();
		// getToolkit().sync();
		    
		myBusReader.output.connect(0, lineOut.input, 0 );
		myBusReader.output.connect(0, lineOut.input, 1 );

		myBusReader.start();
		lineOut.start();

		//	myBusReader.output.connect(lineOut.input, 0 );

		
	    } catch(SynthException e) {
	    SynthAlert.showError(this,e);
	}
	//	connect(serverName, 52807);
    }
    
    // BEGIN can cut and paste from here ....
    
    public void close()
    {  
	try
	    {
		/* Your cleanup code goes here. */
		removeAll(); // remove components from Applet panel.
		Synth.stopEngine();
	    } 
	catch(SynthException e) {
	    SynthAlert.showError(this,e);
	}
    }
 
    public void makeSynth(int threadNum) {
	int tNum;
	float tAmp;
	if (!isOsc[threadNum]) {
	    NGlobals.dtPrint("SOUND_SWARM_DISPLAY::  creating oscillator for thread: " + threadNum);
	    sprites[threadNum] = new Sprite();
	    sprites[threadNum].x = width / 2 - 20;
	    sprites[threadNum].y = height / 2 - 20;
	    sprites[threadNum].color = new Color(randNum.nextInt(255),randNum.nextInt(255),randNum.nextInt(255));

	    oscNum[numOscs++] = threadNum;
	    modOsc[threadNum] = new SineOscillator();
	    carOsc [threadNum]= new SineOscillator();
	    freqAdder[threadNum]    = new AddUnit();

	    modOsc[threadNum].amplitude.set(0.5);
	    for (int i=0;i<numOscs;i++) {
		tNum = oscNum[i];
		tAmp = (float)1/(numOscs+1);
		//hPrint(i + ":resetting amp for osc " + tNum + " to " + tAmp);
		carOsc[tNum].amplitude.set(tAmp);
	    }
	    // freqAdder[threadNum].amplitude.set(0.5);

	    /* Feed first oscillators through adder to offset center frequency. */
	    modOsc[threadNum].output.connect( freqAdder[threadNum].inputA );
	    freqAdder[threadNum].output.connect( carOsc[threadNum].frequency );
			
	    /* Connect oscillator to LineOut so we can hear it. */

	    myBusWriter[threadNum]   = new BusWriter(); /* Create bus writers. */

	    carOsc[threadNum].output.connect(myBusWriter[threadNum].input);

	    myBusWriter[threadNum].busOutput.connect( myBusReader.busInput );	    

	    modOsc[threadNum].frequency.set(200.0);  // MF
	    carOsc[threadNum].frequency.set(400.0);  // CF

	    modOsc[threadNum].amplitude.set(100.0);
	    freqAdder[threadNum].inputB.set(200.0);  // CF
	    
	    // // /* Create a fader to control Frequency. */
	    // add( centerFader = new PortFader( freqAdder[threadNum].inputB,
	    //  				      "Center Frequency", 200.0, 0.0, 500.0) );
		
	    //  add( modFreqFader = new PortFader( modOsc[threadNum].frequency,
	    //  				       "Modulation Frequency", 100.0, 0.0, 500.0) );
			
	    //  add( modRangeFader = new PortFader( modOsc[threadNum].amplitude,
	    //  					"Modulation Depth", 1.0, 0.0, MAX_MOD_DEPTH) );

	    NGlobals.dtPrint("SOUND_SWARM_DISPLAY: setting isOsc[" + threadNum + "] to true");
	    isOsc[threadNum] = true;
	    if (true) {
		/* Start units. */
		modOsc[threadNum].start();
		freqAdder[threadNum].start();
		carOsc[threadNum].start();
		myBusWriter[threadNum].start();
	    }
	}
	else {
	    NGlobals.dtPrint("SOUND_SWARM_DISPLAY: synth already created for thread: " + threadNum);

	}
	if (soundStatus == false) {
	    setAllSynthVol(0);
	}
    }

    public void deleteAllSynths() {
	NGlobals.dtPrint("deleteAllSynths()");
	for (int i=0;i<MAX_THREADS;i++) {
	    //	    int tNum = 	oscNum[i];
	    deleteSynth(i);
	}
	numOscs = 0;
	repaint();
    }
 

    public synchronized void deleteSynth(int threadNum) {
	int tNum = threadNum;
	if (isOsc[threadNum]) {

	    freqAdder[tNum].inputA.set(0.0);  // CF
	    freqAdder[tNum].inputB.set(0.0);  // CF
	    modOsc[tNum].amplitude.set(0.0);
	    carOsc[tNum].amplitude.set(0.0);

	    deleteSprite(threadNum);
	    deleteOsc(threadNum);

	    freqAdder[tNum].stop();
	    modOsc[tNum].stop();
	    carOsc[tNum].stop();
	    freqAdder[tNum].delete();
	    modOsc[tNum].delete();
	    

	    carOsc[tNum].delete();

	    int j=0;

	    for (int i=0;i<numOscs;i++) {
		tNum = 	oscNum[i];
		if (sprites[tNum] != null) {
		    tOscNum[j] = oscNum[i];
		    j++;
		}
	    }
	    numOscs--;
	    for (int i=0;i<numOscs;i++) {
		oscNum[i] = tOscNum[i];
	    }
	    
	}
    }


    // ================================ HANDLE ==================================

    public void handle(NGrain inGrain)
    {
	int i,j,fc,sc,x,y;
	float freq,amp;
	String temp,thread,input;

	float xput,yput;
	//System.out.println("input = " + s);

	NGrain grain;
	x=y=0;

	grain = inGrain;
	NGlobals.dtPrint("SOUND_SWARM_DISPLAY::  got GRAIN");

	NGlobals.dtPrint("appID = " + grain.appID);
	NGlobals.dtPrint("command = " + grain.command);

	if (grain.appID == NAppID.SOUND_SWARM) {

	    if (grain.command == NCommand.SEND_SPRITE_XY) {
		THREAD_ID = grain.iArray[0];
		x = grain.iArray[1];
		y = grain.iArray[2];

		NGlobals.dtPrint("SOUND_SWARM_DISPLAY::  got SEND_SPRITE_XY from SOUND_SWARM: " + x + "," + y);
		// x *= (1.3);
		// y *= (1.3);
    
	    
		if (x > 999)
		    x = 1999;
		xput = (float)(x/3);

		if (xput < 50)
		    xput = 50;
		if (xput > 2000)
		    xput = 2000;
		
		if (y > 999)
		    y = 999;
		
		yput = (float)((y/3));
		if (yput < 50)
		    yput = 50;
		if (yput > 2000)
		    yput = 2000;
		
		freq = (float)x;
		amp = (float)(y/1000);


		if (isOsc[THREAD_ID]) {
		    NGlobals.dtPrint("SOUND_SWARM_DISPLAY: setting osc values for thread: " + THREAD_ID);
		    
		    // carOsc[THREAD_ID].frequency.set((float)xput);
		    // carOsc[THREAD_ID].amplitude.set((float)yput);
		    
		    sprites[THREAD_ID].x = (int)((x/1000.0)*width);
		    sprites[THREAD_ID].y = (int)((y/1000.0)*height);
		    
		    if (soundStatus == true) {
			carOsc[THREAD_ID].frequency.set((float)xput);
			freqAdder[THREAD_ID].inputB.set((float)xput);
			modOsc[THREAD_ID].frequency.set((float)yput);
		    }
		    repaint();
		}
		else {
		    makeSynth(THREAD_ID);
		    NGlobals.dtPrint("SOUND_SWARM_DISPLAY: isOsc[" + THREAD_ID + "] = false");
		}
	    }
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
	    if (new_mx < 5)
		new_mx = 5;
	    if (new_mx > 490)
		new_mx = 490;
	    if (new_my < 5)
		new_my = 5;
	    if (new_my > 490)
		new_my = 490;

	    x += new_mx - mx;
	    y += new_my - my;

	    // update our data
	    mx = new_mx;
	    my = new_my;
	    
	    repaint();
	    e.consume();
	}
    }

    public void paint(Graphics g) {
	int tx, ty;
	Color tc;
	int ksize = 7;
	int ssize = 5;

	if (BackgroundNum == 0) {
	    g.setColor(Color.BLACK);
	    g.fillRect(0,0,width,height);
	    ssize = 5;
	}
	else if (BackgroundNum == 1) {
	    g.drawImage(backgroundImg, 0, 0, width, height, this);
	    ssize = 7;
	}

	//	int xpoints[] = {x-(int)(ksize/2), x+(int)(ksize/2), x+(int)(ksize*1.5), x+(int)(ksize/2)};
	//	int ypoints[] = {y+(int)(ksize/1), y-(int)(ksize/1), y+(int)(ksize/1), y+(int)(ksize*3)};


	int xpoints[] = {x-(int)(ksize/2), x+(int)(ksize/2), x+(int)(ksize*1.5), x+(int)(ksize/2)};
	int ypoints[] = {y+(int)(ksize/2), y-(int)(ksize/2), y+(int)(ksize/2), y+(int)(ksize*1.5)};
    
	//	g.setColor(Color.YELLOW);
	//g.fillRect(x, y, 10, 10);
	// g.setColor(Color.RED);
	// g.fillPolygon(xpoints, ypoints, xpoints.length);

	if (BackgroundNum == 0) {
	    g.setColor(Color.BLACK);
	    g.fillRect(0,0,width,height);
	}
	else if (BackgroundNum == 1) {
	    g.drawImage(backgroundImg, 0, 0, width, height, this);
	}

	//	setBackground(Color.black);

	for (int i=0;i<numOscs;i++) {
	    int threadNum = oscNum[i];
	    tx = sprites[threadNum].x;
	    ty = sprites[threadNum].y;
	    tc = sprites[threadNum].color;

	    g.setColor(tc);

	    int txpoints[] = {tx-(int)(ssize/2), tx+(int)(ssize/2), tx+(int)(ssize*1.5), tx+(int)(ssize/2)};
	    int typoints[] = {ty+(int)(ssize/2), ty-(int)(ssize/2), ty+(int)(ssize/2), ty+(int)(ssize*1.5)};

	    g.fillPolygon(txpoints, typoints, txpoints.length);
	    //	    g.fillRect(tx, ty, 10, 10);
	}

    }
 
}
