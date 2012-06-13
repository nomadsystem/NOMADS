/** 
 * Record and Playback sound using LineIn.
 * Uses AWT 1.1 for the GUI.
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.softsynth.jsyn.*;

public class TJ_Recorder extends Applet
{
	LineIn                 lineIn;
	LineOut                lineOut;
	final static int       NUM_CHANNELS = 1; // 1 for mono, 2 for stereo
	final static double    RECORD_TIME = 5.0; // seconds
	final static double    FRAME_RATE = Synth.DEFAULT_FRAME_RATE/2;
	MyRecorder             recorder;
	
/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_Recorder applet = new TJ_Recorder();
		AppletFrame frame = new AppletFrame("Record and Playback Sound.", applet);
		frame.resize(500,200);
		frame.show();
		frame.test();
	}
				
/**
 * Setup synthesis.
 */
	public void start()
	{
		try
		{
/* Make sure we are using the necessary version of JSyn */
		Synth.requestVersion( 142 );
		
// Use initialize() instead of startEngine() so that we can start() and stop() the engine
// without losing our allocated JSyn objects.
// The reason we need to start() and stop() is because many PC sound cards cannot
// do BOTH input and output at the same time. They are not "full duplex".
// So we have to start() for recording, then stop(). Then we start() again for playback.
// Look for FLAG_ENABLE_INPUT below.
		Synth.initialize();
		
// Make I/O unit generators. We can replace these with other sources of audio, or
// audio processors.
		lineIn = new LineIn();
		lineOut = new LineOut();

// Use a recorder to record and playback captured audio.
		recorder = new MyRecorder( RECORD_TIME, FRAME_RATE, NUM_CHANNELS );

		lineIn.output.connect( 0, recorder.input, 0 );
		recorder.output.connect( 0, lineOut.input, 0 );
		if( NUM_CHANNELS == 1 )
		{
			recorder.output.connect( 0, lineOut.input, 1 );
		}
		else if ( NUM_CHANNELS == 2 )
		{
			lineIn.output.connect( 1, recorder.input, 1 );
			recorder.output.connect( 1, lineOut.input, 1 );
		}

	// add prebuilt recorder GUI to our Applet
		add( recorder.buildGUI() );
		
		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	
// Synchronize Java display so buttons show up.
		getParent().validate();
		getToolkit().sync();
	}


	public void stop()
	{
		lineOut.delete();
		lineOut = null;
		removeAll(); // remove portFaders
/* Turn off tracing. */
		Synth.setTrace(Synth.SILENT);
/* Stop synthesis engine. */
		Synth.terminate();
	}
	
/** Version of recorder that also starts and stops the JSyn engine, and our I/O units.
 */
	class MyRecorder extends Recorder
	{
		public MyRecorder( double seconds, double frameRate, int numChannels )
		{
			super( seconds, frameRate, numChannels );
		}
		
	/** Record a block of sound.
	*/
	   	public void record( boolean ifLoop )
	   	{
			try
			{
			// start engine in half duplex audio input mode
	   			Synth.start( Synth.FLAG_ENABLE_INPUT | Synth.FLAG_DISABLE_OUTPUT, frameRate );
	   			lineIn.start();
	   			super.record( ifLoop );
			} catch (SynthException e) {
				SynthAlert.showError(e);
			}
	   	}		

	/** Play back the recorded sound.
	*/
	   	public void play(  boolean ifLoop )
	   	{
			try
			{
			// start engine in typical half duplex audio output mode
	   			Synth.start( 0, frameRate );
	   			lineOut.start();
	   			super.play( ifLoop );
			} catch (SynthException e) {
				SynthAlert.showError(e);
			}
	   	}
			
	/** Stop playing or recording.
	*/
	   	public void stop()
	   	{
	   		super.stop();
	   		lineIn.stop();
	   		lineOut.stop();
	   		Synth.stop();
	   	}
	}
}

/** Define a class that records sound into a SynthSample and plays it back.
*/
class Recorder
{
   	public SynthInput  input;
   	public SynthOutput output;
/* Modes of operation. */
   	public final static int STOPPED = 0;
   	public final static int PLAYING = 1;
   	public final static int RECORDING = 2;
	
   	SynthSample        sample;
   	SampleWriter       sampleWriter;
   	SampleReader       sampleReader;
   	Button             recordButton, playButton, stopButton;
   	Checkbox           loopBox;
   	double             frameRate;
   	int                mode;

   	public Recorder( double seconds, double frameRate, int numChannels )
   	{
		this.frameRate = frameRate;
	// create a sample big enough to hold the recorded data
   		sample = new SynthSample( (int)(frameRate * seconds), numChannels );
			
	// create the appropriate type of sample units, mono or stereo
   		switch( numChannels )
		{
		case 1:
			sampleWriter = new SampleWriter_16F1();
   			sampleReader = new SampleReader_16F1();
			break;
		case 2:
			sampleWriter = new SampleWriter_16F2();
   			sampleReader = new SampleReader_16F2();
			break;
		default:
			throw new SynthException("Only support 1 or 2 channel recording.");
		}
	// export sampler units ports
   		input = sampleWriter.input;
   		output = sampleReader.output;
   	}

/** Construct a GUI for recording and playing back audio with standard tape transport controls.
 */
   	public Panel buildGUI()
   	{
   		Panel panel = new Panel();
	// Add a record button.
   		panel.add( recordButton = new Button("Record") );
	// Define an actionListener class that will record() when the button is pressed.
   		recordButton.addActionListener(
   			new ActionListener()
   			{
   				public void actionPerformed(ActionEvent e)
   				{
   					record( loopBox.getState() );
   				}
   			} );
			
			
   		panel.add( stopButton = new Button("Stop") );
   		stopButton.addActionListener(	new ActionListener() {
   				public void actionPerformed(ActionEvent e)
   				{ stop();	} } );
			
   		panel.add( playButton = new Button("Play") );
   		playButton.addActionListener(	new ActionListener() {
   				public void actionPerformed(ActionEvent e)
   				{ play( loopBox.getState() );	} } );
			
   		panel.add( loopBox = new Checkbox("Loop") );
		
	// return panel so caller can add this GUI to its display
   		return panel;
   	}
		
/** Enable or disable GUI buttons based on current mode of operation.
 */
   	void setMode( int mode )
   	{
   		this.mode = mode;
   		switch( mode )
   		{
   		case STOPPED:
   			recordButton.setEnabled( true );
   			playButton.setEnabled( true );
   			stopButton.setEnabled( false );
   			break;
   		case PLAYING:
   		case RECORDING:
   			recordButton.setEnabled( false );
   			playButton.setEnabled( false );
   			stopButton.setEnabled( true );
   			break;
   		}	
   	}
		
/** Record sound into the sample.
*/
   	public void record( boolean ifLoop )
   	{
   		sampleWriter.start();
   		if( ifLoop )
   		{
   			sampleWriter.samplePort.queueLoop( sample );
   		}
   		else
   		{
   			sampleWriter.samplePort.queue( sample );
   		}
   		setMode( RECORDING );
   	}		

/** Play back the recorded sound from the sample.
*/
   	public void play(  boolean ifLoop )
   	{
   		sampleReader.start();
   		if( ifLoop )
   		{
   			sampleReader.samplePort.queueLoop( sample );
   		}
   		else
   		{
   			sampleReader.samplePort.queue( sample );
   		}
    	setMode( PLAYING );
  	}
		
/** Stop playing or recording.
*/
   	public void stop()
   	{
   		sampleWriter.samplePort.clear();
   		sampleWriter.stop();
   		sampleReader.samplePort.clear();
   		sampleReader.stop();
    	setMode( STOPPED );
   	}
}

