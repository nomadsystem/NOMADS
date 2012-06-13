/** 
 * Test Envelope using Java Audio Synthesiser
 * Trigger different envelopes using buttons.
 * More complicated envelope with sustain loop.
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.Button;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.Label;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view102.PortFader;
import com.softsynth.jsyn.view102.UsageDisplay;

public class TJ_Env2 extends Applet
{
	SynthEnvelope     myEnvData;
	EnvelopePlayer  myEnv;
	int        numFrames;
	final int  MAX_FRAMES = 16;
	SineOscillator  myOsc;
	LineOut    myOut;
	Button     hitme;
	Button     attackButton;
	Button     releaseButton;
	Button     framesButton;
	Label      framesLabel;

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_Env2 applet = new TJ_Env2();
		AppletFrame frame = new AppletFrame("Test SynthEnvelope", applet);
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
		//Synth.setTrace( Synth.VERBOSE );
		try
		{
/* Start synthesis engine. */
		Synth.startEngine( 0 );

/* Create an envelope and fill it with recognizable data. */
		double[] data =
		{
			0.05, 1.0,  /* duration,value pair for frame[0] */
			0.30, 0.1,  /* duration,value pair for frame[1] */
			0.50, 0.7,  /* duration,value pair for frame[2] */
			0.50, 0.9,  /* duration,value pair for frame[3] */
			0.80, 0.0   /* duration,value pair for frame[4] */
		};
		numFrames = data.length/2;
		myEnvData = new SynthEnvelope( numFrames );
		myEnvData.write( 0, data, 0, numFrames );

/* Create SynthUnits whose peers are unit generators. */
		myEnv = new EnvelopePlayer();
		myOsc = new SineOscillator();
		myOut = new LineOut();

/* Connect envelope to oscillator amplitude. */
		myEnv.output.connect( 0, myOsc.amplitude, 0 );
/* Connect oscillator to output. */
		myOsc.output.connect( 0, myOut.input, 0 );
		myOsc.output.connect( 0, myOut.input, 1 );

		add( hitme = new Button("Hit") );
		add( attackButton = new Button("Attack") );
		add( releaseButton = new Button("Release") );
		add( framesButton = new Button("Query Frames") );
		add( framesLabel = new Label("Frames Played = ") );

/* Show faders so we can manipulate sound parameters. */
		add( new PortFader( myEnv.rate, 1.0, 0.0, 2.0 ) );
		add( new PortFader( myOsc.frequency, 400.0, 50.0, 2000.0 ) );
		add( new UsageDisplay() );

/* Start execution of units. */
		myOut.start();
		myOsc.start();
	
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
			myOut.delete();
			myOut = null;
			myOsc.delete();
			myOsc = null;
			myEnv.delete();
			myEnv = null;
			removeAll(); // remove portFaders
/* Turn off tracing. */
			Synth.setTrace( Synth.SILENT );
/* Stop synthesis engine. */
			Synth.stopEngine();

		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	}

	public boolean action(Event evt, Object what)
	{
		try
		{
			if( evt.target == hitme )
			{
				myEnv.envelopePort.clear( );
/* Use AutoStop feature of envelope so that unit stops when data gone. */
				myEnv.envelopePort.queue( myEnvData, 0, numFrames, Synth.FLAG_AUTO_STOP );
				myEnv.start();
				return true;
			}
/* Queue up all segments except last segment. */
			else if( evt.target == attackButton )
			{
				myEnv.envelopePort.clear( );
				myEnv.envelopePort.queue( myEnvData, 0, 3 );
				myEnv.envelopePort.queueLoop( myEnvData, 1, 2 );
				myEnv.start();
				return true;
			}
/* Queue final segment. */
			else if( evt.target == releaseButton )
			{
/* Use AutoStop feature of envelope so that unit stops when data gone. */
				myEnv.envelopePort.queue( myEnvData, 3, 2, Synth.FLAG_AUTO_STOP );
				return true;
			}
			else if( evt.target == framesButton )
			{
				int numFrames = myEnv.envelopePort.getNumFramesMoved();
				framesLabel.setText("Frames played = " + numFrames );
			}
		} catch (SynthException e) {
			SynthAlert.showError(this,e);
			return true;
		}
		return false;
    }

}
