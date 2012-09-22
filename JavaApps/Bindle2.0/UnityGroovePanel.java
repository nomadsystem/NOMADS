/*
  NOMADS Unity Groove Student v.210
  Revised/cleaned, 6/20/2012, Steven Kemper
  Integrating NSAND class
*/

import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import netscape.javascript.*;
import nomads.v210.*;

import com.softsynth.jsyn.*;

public class UnityGroovePanel extends JPanel implements ActionListener, Runnable
{

    /* DO: Declare Synthesis Objects here */
    SineOscillator    	sineOsc;
    SquareOscillator	squareOsc;
    AddUnit				mixer;
    SynthEnvelope      	envData;
    EnvelopePlayer     	envPlayer;
    LineOut            	lineOut;

    Random 		randNum;
    colorBox	colorBox;

    JPanel		butPanelA, butPanelB, labelPanelC, p;

    int red = (int)255/2;
    int green = (int)0;
    int blue = (int)255/2;

    JButton     startButton;
    JButton		plusButton;
    JButton		minusButton;
    JButton		stopButton;
    JLabel		frequencyText;
    JLabel		counterDisplay;
    Panel		graphicsBox;

    Thread		runner;

    float		counter;
    int		 	loopOn, tRand;
    double[]	data;
    float		sineFreq, startFreq;
    float 		envRate;
    float		envPauseTime;
    boolean alphaBlink = false;
    NSand mySand;

    private Boolean runSynth = false;

    public void setRun (boolean r) {
	runSynth = r;
    }

    public boolean getRun () {
	return runSynth;
    }


    public void setupSynth() {
	try {
	    Synth.startEngine(0);
	    // Create unit generators.
	    sineOsc = new SineOscillator();
	    sineOsc.frequency.set( sineFreq );
	    squareOsc = new SquareOscillator();
	    squareOsc.frequency.set( sineFreq );
	    mixer    = new AddUnit();
	    envPlayer = new EnvelopePlayer();  // create an envelope player
	    lineOut = new LineOut();

	    /* Feed both oscillators to the mixer to be added together. */
	    sineOsc.output.connect( mixer.inputA );
	    squareOsc.output.connect( mixer.inputB );

	    /* Connect oscillator to LineOut so we can hear it. */
	    mixer.output.connect( 0, lineOut.input, 0 );
	    mixer.output.connect( 0, lineOut.input, 1 );


	    /* Start amplitudes at zero. */
	    sineOsc.amplitude.set( 0.0 );
	    squareOsc.amplitude.set( 0.0 );


	    // control sine wave amplitude with envelope output
	    envPlayer.output.connect( sineOsc.amplitude );
	    envPlayer.output.connect( squareOsc.amplitude );

	    // define shape of envelope as an array of doubles
	    double[] data =
		{
		    0.1, 0.4,  // Take 0.1 seconds to go to value 1.0. "Attack"
		    0.10, 0.25,  // #1, Take 0.10 seconds to drop to value 0.5. Part of "Sustain Loop"
		    0.1, 0.3,  // #2, Take 0.05 seconds to rise to value 0.8. Part of "Sustain Loop"
		    0.1, 0.0,   // Take 0.3 seconds to drop to 0.0.  "Release"
		    envPauseTime, 0.0	//#4
		};
	    envData = new SynthEnvelope( data );

	    lineOut.start();
	    mixer.start();
	    sineOsc.start();
	    squareOsc.start();
	    envPlayer.start();

	    // Synchronize Java display to make buttons appear.
	    getToolkit().sync();

	} catch(SynthException e) {
	    SynthAlert.showError(this,e);
	}
    } 
    public void init(NSand inSand)
    {
	mySand = inSand;
	startFreq = 1000.0F;
	envRate = 1.0F;
	counter = 0.0F;
	loopOn = 0;
	envPauseTime = 1.0F;
	randNum = new Random();
	tRand = randNum.nextInt(255);
	sineFreq = startFreq + tRand;
	System.out.println("Starting Frequency = "+ sineFreq);

	//		Container content = getContentPane();
	//    	content.setBackground(Color.black);

	createButtons();

	setupSynth();
	start();
    }

    public void createButtons() 
    {

	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));  // sets horizontal layout
	setBackground(Color.black);

	JPanel buttonWrapper = new JPanel();
	buttonWrapper.setLayout(new FlowLayout(FlowLayout.CENTER));
	buttonWrapper.setBackground(Color.black);

	startButton = new JButton("Start");

	startButton.addActionListener(this);

	stopButton = new JButton("Stop");
	stopButton.addActionListener(this);

	buttonWrapper.add( startButton );
	buttonWrapper.add( stopButton );

	JPanel controls = new JPanel();
	controls.setLayout(new FlowLayout(FlowLayout.CENTER));
	controls.setBackground(Color.black);


	plusButton = new JButton("+");
	plusButton.addActionListener(this);

	minusButton = new JButton("-");
	minusButton.addActionListener(this);

	controls.add( plusButton );
	controls.add( minusButton );

	JPanel freqDisplay = new JPanel();
	freqDisplay.setLayout(new FlowLayout(FlowLayout.CENTER));
	freqDisplay.setBackground(Color.black);

	counterDisplay = new JLabel("");
	counterDisplay.setForeground( Color.WHITE );
	counterDisplay.setBackground( Color.BLACK );
	counterDisplay.setOpaque(true);
	String counterText = new Float((counter + sineFreq)).toString();
	counterDisplay.setText(counterText);
	counterDisplay.setFont(new Font("Serif", Font.BOLD, 36));

	frequencyText = new JLabel("Frequency:");
	frequencyText.setForeground( Color.WHITE );
	frequencyText.setBackground( Color.BLACK );
	frequencyText.setOpaque(true);
	frequencyText.setFont(new Font("Serif", Font.PLAIN, 36));

	freqDisplay.add( frequencyText );
	freqDisplay.add( counterDisplay );

	colorBox = new colorBox( );
	colorBox.setMyColor(red,green,blue);
	colorBox.setBackground(Color.black);
	colorBox.setPreferredSize( new Dimension( 300,300 ) );
	colorBox.setLayout(new FlowLayout(FlowLayout.CENTER));

	this.add(buttonWrapper);
	this.add(colorBox);
	this.add(controls);
	// 	pane.add(freqDisplay);
	//pane.add(colorBox);

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
	try
	    {
		/* Your cleanup code goes here. */
		removeAll(); // remove components from Applet panel.
		Synth.stopEngine();
	    } catch(SynthException e) {
	    SynthAlert.showError(this,e);
	}
    }


    //	public void handle() //byte b, string s
    //	{
    //
    //		int incCmd, incAppID;
    //
    //		NGrain grain;
    //
    //		NGlobals.cPrint("UnityGrooveStudent -> handle()");
    //
    //		grain = uGrooveSand.getGrain();
    //		grain.print(); //prints grain data to console
    //
    //		incAppID = grain.appID;
    //		incCmd = grain.command;
    //		
    //		if (incAppID == NAppID.INSTRUCT_EMRG_SYNTH_PROMPT) {
    //			NGlobals.cPrint("UGStudent: Got message from UGPrompt");
    //
    //			if (incCmd == NCommand.SYNTH_ENABLE_STATUS) {
    //				if (grain.bArray[0] == 1) {
    //					startButton.setEnabled(true);
    //					stopButton.setEnabled(true);
    //				}
    //				else if (grain.bArray[0] == 0) {
    //					startButton.setEnabled(false);
    //					stopButton.setEnabled(false);
    //				}
    //			}
    //			
    //			else if (incCmd == NCommand.SYNTH_START_STOP) {
    //				if (grain.bArray[0] == 1) {
    //					//run();
    //					setRun(true);
    //				}
    //				else if (grain.bArray[0] == 0) {
    //					envPlayer.envelopePort.clear();
    //					envPlayer.envelopePort.queue( envData, 3, 1 );  // queue release
    //					setRun(false);
    //				}
    //			}
    //		}
    //
    //
    //		// If we need a message from the instructor panel
    //		else if (incAppID == NAppID.INSTRUCTOR_PANEL) {
    //		}
    //
    //	}

    public void run()    // real-time task for thread
    {
	//	int nextTime = 1000;
	//		int duration = 1000;
	System.out.println("RUN()");

	int time = Synth.getTickCount();
	while(true) 
	    {
		if (getRun()) {
		    //    System.out.println("running a loop");
		    time += (1);
		    //Synth.sleepForTicks( 100 );
		    envPlayer.envelopePort.clear(); // clear the queue 
		    //	envPlayer.envelopePort.queueLoop( envData );  // queue loop
		    envPlayer.envelopePort.queue( envData);  // queue attack
		    //		nextTime += duration;    // Advance nextTime by fixed amount!!!!!!
		    //Synth.sleepForTicks( 1000 ); 	
		    Synth.sleepUntilTick( time );
		}
		else {
		    //    System.out.println("not running");
		}
		try {
		    if (getRun()) {
			for (int i=1;i<10;i++) {
			    //	    System.out.println("changing alpha");
			    colorBox.setMyColor(red,green,blue);
			    colorBox.setAlpha((int)((20*i)+55));
			    runner.sleep((int)((350/10) * (1/envRate)));
			}
			for (int i=10;i>0;i--) {
			    //	    System.out.println("changing alpha");
			    colorBox.setMyColor(red,green,blue);
			    colorBox.setAlpha((int)((20*i)+55));
			    runner.sleep((int)((350/10) * (1/envRate)));
			}

		    }
		    runner.sleep((int)(1050 * (1/envRate)));
		}
		catch (InterruptedException ie) {}
	    }
    }

    /* Process button hits. */

    public void actionPerformed(ActionEvent ae)
    {
	Object obj = ae.getSource();

	if( obj == startButton )
	    {
		// Start units.
		NGlobals.cPrint("uGROOVE start");
		setRun(true);
	    }

	else if( obj == plusButton )
	    {

		red += 3;
		if (red >= 255)
		    red = 255;

		blue -= 3;
		if (blue < 0)
		    blue = 0;

		green += 3;
		if (green > 255/2)
		    green = 255/2;

		colorBox.setMyColor(red,green,blue);

		counter += 20.0;

		sineOsc.frequency.set( sineFreq + counter);
		squareOsc.frequency.set( sineFreq + counter);
		System.out.println("Frequency = " + counter);
		envRate = (sineFreq + counter)/sineFreq;
		System.out.println(envRate);
		envPlayer.rate.set( envRate ); // play at half speed
		String counterText = new Float((sineFreq + counter)).toString();
		counterDisplay.setText(counterText);
	    }
	else if( obj == minusButton )
	    {

		red -= 3;
		if (red < 255/2)
		    red = 255/2;

		blue += 3;
		if (blue > 255)
		    blue = 255;

		green -= 3;
		if (green < 0)
		    green = 0;

		colorBox.setMyColor(red,green,blue);

		counter -= 20.0;
		if ((sineFreq + counter) >  200.0) {
		    sineOsc.frequency.set( sineFreq + counter);
		    squareOsc.frequency.set( sineFreq + counter);
		    System.out.println("Frequency = " + counter);
		    String counterText = new Float((sineFreq + counter)).toString();
		    counterDisplay.setText(counterText);
		    envRate = (sineFreq + counter)/sineFreq;
		    System.out.println("envRate = " + envRate);
		    envPlayer.rate.set( envRate ); // play at half speed
		}
		else {
		    sineOsc.frequency.set( 200.0 );
		    squareOsc.frequency.set( 200.0 );
		    String counterText = new Float(200.0).toString();
		    counterDisplay.setText(counterText);
		    envRate = (sineFreq + counter)/sineFreq;
		    System.out.println("envRate = " + envRate);
		    envPlayer.rate.set( envRate ); // play at half speed
		    counter = (float)(780.0 * -1.0);
		}
		System.out.println("counter = " + counter);

	    }
	else if( obj == stopButton )
	    {
		envPlayer.envelopePort.clear();
		envPlayer.envelopePort.queue( envData, 3, 1 );  // queue release	
		setRun(false);
		NGlobals.cPrint("uGROOVE stop");

		//Start units.
		//lineOut.stop();
		//mixer.stop();
		//sineOsc.stop();
		//squareOsc.stop();
		//envPlayer.stop();
	    }
    }
}