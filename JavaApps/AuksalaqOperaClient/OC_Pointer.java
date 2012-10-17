import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.applet.*;
import netscape.javascript.*;
import nomads.v210_auk.*;

import com.softsynth.jsyn.*;


public class OC_Pointer extends JPanel implements Runnable
{
	
    private static final long serialVersionUID = 1L;
    int width, height, diagonal;
    double diagonalSQ;
    int x, y; // coords of the box
    int mx, my; // recent mouse coords
    int personnum;
    double posXTimeScaler;
    private boolean assigned = false;
    boolean isMouseDraggingBox = false;	
    public String towrite;
    public String sendCoords;

    SynthSampleAIFF fooSample = null;
    SampleReader_16V1  gSampler;
    BusReader          myBusReader;
    BusWriter          myBusWriter;
    LineOut            	lineOut;
    InputStream inputStream = null;
    URL sampleURL;
    int printIt = 0;

    Image backgroundIce; 


    String prefix, imgPrefix, fileString;
    URL webBase, imgWebBase;

    Random 		randFileNum, randThreadWait;
    Thread		runner;

    float		counter;
    int		 	fileNum, threadWait, runnerSleepTime, startFileNum, fileOffset, fileBankNumber;

    int 		posX, posY, origX, origY;
    static int fileBankMultiplier = 29;

    public Image offScreen;
    Graphics2D offScreenGrp;

    public void setupSynth() {
	try {
	    Synth.startEngine(0);
	    // Create unit generators.
	    lineOut = new LineOut();

	    myBusReader = new BusReader();

	    myBusWriter = new BusWriter();



	    myBusReader.output.connect(0, lineOut.input, 0 );
	    myBusReader.output.connect(0, lineOut.input, 1 );

	    myBusReader.start();
	    lineOut.start();



	} catch(SynthException e) {
	    SynthAlert.showError(this,e);
	}
    }

    public void init()
    {






	randFileNum = new Random();
	fileOffset = (randFileNum.nextInt(27) + 1); //choose a random file to play from each bank

	randThreadWait = new Random();
	threadWait = randThreadWait.nextInt(10); //determines how long the thread pauses between samples

	posXTimeScaler = 5.43; //3500/width gives value to subtract from  
	runnerSleepTime = ((4000 + (threadWait * 50)) - (int)(posXTimeScaler * 322.5)); //init sleep times between 1000-1500ms
	NGlobals.cPrint("runner SleepTime = " + runnerSleepTime);

	width = this.getSize().width;
	height = this.getSize().height;
	//		width = 779;
	width = 645;
	height = 539;

	System.out.println("createImage(" + width + "," + height + ")");
	// offScreen = (BufferedImage)this.createImage(width,height);
	offScreenGrp = (Graphics2D) offScreen.getGraphics();
	// offScreenGrp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	diagonalSQ = Math.pow(width, 2) + Math.pow(height, 2);
	diagonal = (int)(Math.sqrt(diagonalSQ)/2); //figure out 1/2 of diagonal
	//setBackground(Color.black);

	x = width / 2 - 20;
	y = height / 2 - 20;

	NGlobals.cPrint( "OCP: width =" + width + " height" + height);
	NGlobals.cPrint( "OCP: x =" + x + " y =" + y);

	NGlobals.cPrint( "OCP: width =" + width + " height" + height);
	NGlobals.cPrint( "OCP: x =" + x + " y =" + y);

	origX = x;
	origY = y;
	posX = origX;
	posY = origY; //need to reverse this (make low value 5, high value 490)

	//	addMouseListener(this);
	//	addMouseMotionListener(this);

	fileBankNumber = 2; //(starting with bank 2--high sounds)

	startFileNum = fileBankNumber * fileBankMultiplier + fileOffset;
	NGlobals.cPrint( "startFileNum = " + startFileNum );
	fileString = Integer.toString(startFileNum);


	prefix = "http://nomads.music.virginia.edu/sounds/glacierSoundsSK/";


	try {
	    webBase = new URL(prefix); //will be prefix + sfName
	} catch (MalformedURLException mue) {
	    mue.printStackTrace();
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	}

	setupSynth();



	// Create InputStream to download WAV data.
	try {
	    sampleURL = new URL(prefix + fileString + ".aif"); 	
	    //	NGlobals.cPrint("URL = " + sampleURL);
	    //	NGlobals.cPrint(prefix + fileString + ".aif");
	    //	NGlobals.cPrint(prefix + "1.aiff");
	    inputStream = sampleURL.openConnection().getInputStream();
	    fooSample = new SynthSampleAIFF(inputStream);
	    //	NGlobals.cPrint("Sample Frames = " + fooSample.getNumFrames());
	}	catch (MalformedURLException mue) {
	    mue.printStackTrace();
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	}


	gSampler = new SampleReader_16V1();
	gSampler.rate.set( 44100.0 );
	//	gSampler.output.connect( 0, lineOut.input, 0 );
	//	gSampler.output.connect( 0, lineOut.input, 1 );


	myBusWriter   = new BusWriter();

	gSampler.output.connect(myBusWriter.input);

	myBusWriter.busOutput.connect( myBusReader.busInput );
	myBusWriter.start();
	gSampler.start();

	myBusReader.amplitude.set(0.0);

	gSampler.samplePort.queue( fooSample, 0, fooSample.getNumFrames() );

	//	inputStream.close();

	//		mySample = getAudioClip(webBase, fileString + ".aif");

	try { 
	    imgWebBase = new URL(imgPrefix); 
	} 
	catch (Exception e) {}
	// 		backgroundIce = getImage(imgWebBase,"Ice.1_blue.jpg");


    }
    // filename conversions
    // 1-29=high1 bank 0
    // 30-58=high2 bank 1
    // 59-87=high3 bank 2
    // 88-116=low1 bank 3
    // 117-145=low2 bank 4
    // 146-174=low3 bank 5
    // 175-203=mid bank 6



    public void setImage(Image backgroundImage) {
	backgroundIce = backgroundImage;
    }


	
    //TODO: Implement double buffering ****STK 6/20/12
    public void paint(Graphics g) {

	    super.paint(g);
	    setBackground(Color.black);
	    offScreenGrp.setColor(Color.black);
	    offScreenGrp.drawImage(backgroundIce, 0, 0, width, height, this);
	    //    g.fillRect(0,0,width,height);
	    int ksize = 7;
	    int xpoints[] = {x-(int)(ksize/2), x+(int)(ksize/2), x+(int)(ksize*1.5), x+(int)(ksize/2)};
	    int ypoints[] = {y+(int)(ksize/2), y-(int)(ksize/2), y+(int)(ksize/2), y+(int)(ksize*1.5)};
	    
	    
	    offScreenGrp.setColor(Color.ORANGE);
	    offScreenGrp.fillPolygon(xpoints, ypoints, xpoints.length);
	    
	    g.drawImage(offScreen, 0, 0, width, height, this);
	    
	    
	    //		g.fillRect(x, y, 10, 10);
    }


    public void start()  
    {
	runner = new Thread(this);
	runner.start();
    }

    /*
     * Clean up synthesis by overriding stop() method.
     */
    public void stop()  
    {

    }

    public void close()
    {  
	try {
	    Synth.stopEngine();
	}
	catch (SynthException e) {
	}   
    }

    public void handle(byte b, String s) {
	if (b == NAppID.OC_POINTER) {
	    if (s.contains("assigned") && assigned == false) {
		String delims = "[ ]+";
		String[] tokens = s.split(delims);
		personnum = Integer.parseInt(tokens[1]);
		NGlobals.cPrint("This new sphere is assigned value "
				+ tokens[1]);
		assigned = true;
	    }
	    else if (s.equals("OC_DROPLET_ENABLE")) {
		gSampler.amplitude.set(1.0);
		System.out.println("Droplets enabled");
	    }
	    else if (s.equals("OC_DROPLET_DISABLE")) {
		gSampler.amplitude.set(0.0);
		System.out.println("Droplets disabled");
	    }


	}
    }


    public void run()    // real-time task for thread
    {

	//		NGlobals.cPrint("RUN()");

	while(true) 
	    {
		//Here's where I'm figuring out new values based on x/y coordinates
		//let's try making it higher when you get closer to the center. . .
		//	NGlobals.cPrint( "posX " + posX + "posY  " + posY );

		//	System.out.println("sendCoords = " + sendCoords);

		int time = Synth.getTickCount();
		double myX, myY, myH_Sqr;
		int myH;
		int tFileNum;
		int threadWaitSubtractor = 0;

		// if (posX >= origX)
		// 			myX = (double)(posX - origX); //if X value is bigger than origin value, distance = X-origin (x - 230)
		// 		else 
		// 			myX = (double)(origX - posX);
		// 			
		if (posY >= origY)
		    myY = (double)(posY - origY);
		else
		    myY = (double)(origY - posY);
		//	NGlobals.cPrint( "myY =" + myY);
		// 			
		// 		myH_Sqr = Math.pow(myX, 2) + Math.pow(myY, 2); //Pythagoras' Theorem 
		// 		
		// 		myH = (int)Math.sqrt(myH_Sqr); //distance from center
		// 		NGlobals.cPrint( "H = " + myH + "Diagonal = " + diagonal);
		// 		
		int ySection = (int)((height/2)/7);

		if (myY < ySection) {
		    fileBankNumber = 2; //(starting with bank 7--medium sounds)
		    //		NGlobals.cPrint( "zone 1");
		}
		else if ((myY >= ySection*1) && myY < (ySection*2)) {
		    fileBankNumber = 1;
		    //	NGlobals.cPrint( "zone 2");
		}
		else if (myY >= (ySection*2) && myY < (ySection*3)) {
		    fileBankNumber = 0;
		    //	NGlobals.cPrint( "zone 3");
		}
		else if (myY >= (ySection*3) && myY < (ySection*4)) {
		    fileBankNumber = 6;
		    //	NGlobals.cPrint( "zone 4");
		}
		else if (myY >= (ySection*4) && myY < (ySection*5)) {
		    fileBankNumber = 3;
		    //	NGlobals.cPrint( "zone 5");
		}
		else if (myY >= (ySection*5) && myY < (ySection*6)) {
		    fileBankNumber = 4;
		    //		NGlobals.cPrint( "zone 6");
		}
		else if (myY >= (ySection*6) ) {
		    fileBankNumber = 5;	
		    //	NGlobals.cPrint( "zone 7");
		}

		threadWaitSubtractor = (int)(posXTimeScaler * posX);

		fileOffset = (randFileNum.nextInt(27) + 1);
		tFileNum = fileBankNumber * fileBankMultiplier + fileOffset;
		fileString = Integer.toString(tFileNum);

		try {
		    sampleURL = new URL(prefix + fileString + ".aif"); 	
		    //	NGlobals.cPrint("URL = " + sampleURL);
		    //	NGlobals.cPrint(prefix + fileString + ".aif");
		    //	NGlobals.cPrint(prefix + "1.aiff");
		    inputStream = sampleURL.openConnection().getInputStream();
		    fooSample = new SynthSampleAIFF(inputStream);
		    //	NGlobals.cPrint("Sample Frames = " + fooSample.getNumFrames());
		}	catch (MalformedURLException mue) {
		    mue.printStackTrace();
		} catch (IOException ioe) {
		    ioe.printStackTrace();
		}
		//NGlobals.cPrint( "fileString = " + fileString + "fileBankNumber = " + fileBankNumber + "FileOffset =" + fileOffset);
		//		mySample.play();
		//		Synth.sleepForTicks( 1000 );

		gSampler.samplePort.queue( fooSample, 0, fooSample.getNumFrames() );

		threadWait = randThreadWait.nextInt(10); //determines how long the thread pauses between samples
		runnerSleepTime = ((4000 + (threadWait * 50)) - threadWaitSubtractor);
		//	NGlobals.cPrint("runner SleepTime = " + runnerSleepTime);
		Synth.sleepForTicks( runnerSleepTime );

		try {
		    runner.sleep(runnerSleepTime);
		}
		catch (InterruptedException ie) {}
	    }
    }


}