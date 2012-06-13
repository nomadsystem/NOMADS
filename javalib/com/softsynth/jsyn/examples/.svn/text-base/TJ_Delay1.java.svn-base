/* 
 * Test simple delay using Java Audio Synthesiser
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.GridLayout;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view11x.DecibelPortFader;
import com.softsynth.jsyn.view11x.PortFader;

/*
 * Define a circuit that creates a long echo.
 * In this circuit, a signal is written to a sample,
 * and then read back at a later time.
 *
 * If you want a shorter echo, consider using the DelayUnit class
 * which takes more memory but uses higher precision internally.
 */
class LongEcho extends SynthCircuit
{
	SynthSample       delayLine;
	SampleReader_16F1 unitReader;
	SampleWriter_16F1 unitWriter;
	MultiplyAddUnit   feedbackMixer;

	int               numSamples;
	
	public SynthInput input;
	public SynthInput feedback;
	public SynthInput amplitude;
/*
 * Setup synthesis.
 */
	public LongEcho( double delayTime )  throws SynthException
	{
/* Call SynthSound constructor to make room for subUnits. */
		super();

/* Calculate how many sample correspond to a given delay time. */
		numSamples = (int) (delayTime * Synth.getFrameRate());

/* Create an empty sample. */
		delayLine = new SynthSample( numSamples );

/* Create synthesis units. */
		add( unitReader = new SampleReader_16F1() );
		add( unitWriter = new SampleWriter_16F1() );
		add( feedbackMixer = new MultiplyAddUnit() );

/* Start sampler just after delay for maximal delay time. */
		unitReader.samplePort.queue( delayLine, 30, numSamples-30 );
		unitReader.samplePort.queueLoop( delayLine, 0, numSamples );
		unitWriter.samplePort.queueLoop( delayLine, 0, numSamples );

		unitReader.output.connect( feedbackMixer.inputA );
		feedbackMixer.output.connect( unitWriter.input );

/* Make ports on internal units appear as ports on circuit. */ 
/* Give some circuit ports more meaningful names. */
		addPort( input = feedbackMixer.inputC, "input" );
		addPort( feedback = feedbackMixer.inputB, "feedback" );
		addPort( amplitude = unitReader.amplitude );
		addPort( output = unitReader.output );
	}
}


public class TJ_Delay1 extends Applet
{
	TriangleOscillator mySource;
	LineOut            myOut;
	LongEcho           myEcho;

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_Delay1 applet = new TJ_Delay1();
		AppletFrame frame = new AppletFrame("Test Sample", applet);
		frame.resize(600,400);
		frame.show();
/* Begin test after frame opened so that DirectSound will use Java window. */
		frame.test();
	}

/*
 * Setup synthesis.
 */
	public void start()
	{
		setLayout( new GridLayout(0,1) );
		
		try
		{
/* Start synthesis engine. */
		Synth.startEngine( 0 );

/* Create an long echo that lasts 5 seconds. */
		myEcho = new LongEcho( 5.0 );

/* Create synthesis units. */
		mySource = new TriangleOscillator();
		myOut = new LineOut();


		mySource.output.connect( myEcho.input );
		mySource.output.connect( 0, myOut.input, 0 );
		myEcho.output.connect( 0, myOut.input, 1 );

/* Show faders so we can manipulate sound parameters. */
		add( new DecibelPortFader( mySource.amplitude, "source (dB)", -3.0, -60.0, 0.0 ) );
		add( new PortFader( mySource.frequency,  0.0, 0.0, 3000.0 ) );
		add( new PortFader( myEcho.feedback, -0.9, -1.0, 0.0 ) );
		add( new DecibelPortFader( myEcho.amplitude, "echo (dB)", -3.0, -60.0, 0.0 ) );

		myOut.start();
		myEcho.start();
		mySource.start();

		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}

/* Synchronize Java display. */
		getParent().validate();
		getToolkit().sync();
	}
	
	public void stop()
	{
		try
		{
/* Delete unit peers. */
			mySource.stop();
			myEcho.stop();
			myOut.stop();
			removeAll();

/* Turn off tracing. */
			Synth.verbosity = Synth.SILENT;
/* Stop synthesis engine. */
			Synth.stopEngine();

		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	}
}
