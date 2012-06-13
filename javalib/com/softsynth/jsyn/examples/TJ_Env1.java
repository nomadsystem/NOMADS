/** 
 * Test Envelope using Java Audio Synthesiser
 * Trigger attack or release portion.
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.Button;
import java.awt.Event;
import java.awt.GridLayout;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view11x.ExponentialPortFader;

public class TJ_Env1 extends Applet
{
	SynthEnvelope     sgEnvData;
	EnvelopePlayer    sgEnv;
	SineOscillator    sgOsc;
	LineOut           sgOut;
	int        numFrames;
	final int  MAX_FRAMES = 16;
	double[]   data = new double[MAX_FRAMES*2];
	Button     hitme;
	Button     attackButton;
	Button     releaseButton;

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_Env1 applet = new TJ_Env1();
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

		try
		{
/* Start synthesis engine. */
		Synth.startEngine( 0 );

/* Create an envelope and fill it with recognizable data. */
		int i=0;
		data[i++] = 0.02; /* Duration of first segment. */
		data[i++] = 1.0; /* value */
		data[i++] = 0.4; /* duration */
		data[i++] = 0.4; /* value */
		data[i++] = 0.9; /* duration */
		data[i++] = 0.0; /* value */
		numFrames = i/2;
		sgEnvData = new SynthEnvelope( numFrames );
		sgEnvData.write( 0, data, 0, numFrames );

/* Create SynthUnits whose peers are unit generators. */
		sgEnv = new EnvelopePlayer();
		sgOsc = new SineOscillator();
		sgOut = new LineOut();

/* Connect envelope to oscillator amplitude. */
		sgEnv.output.connect( 0, sgOsc.amplitude, 0 );
/* Connect oscillator to output. */
		sgOsc.output.connect( 0, sgOut.input, 0 );
		sgOsc.output.connect( 0, sgOut.input, 1 );

		add( hitme = new Button("Hit") );
		add( attackButton = new Button("Attack") );
		add( releaseButton = new Button("Release") );

/* Show faders so we can manipulate sound parameters. */
		add( new ExponentialPortFader( sgOsc.frequency, 400.0, 50.0, 2000.0 ) );

/* Start execution of units. */
		sgOut.start();
		sgOsc.start();
		sgEnv.start();
	
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
			sgOut.delete();
			sgOut = null;
			sgOsc.delete();
			sgOsc = null;
			sgEnv.delete();
			sgEnv = null;

			removeAll();
/* Turn off tracing. */
			Synth.verbosity = Synth.SILENT;
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
				sgEnv.envelopePort.queue( sgEnvData, 0, numFrames );
				return true;
			}
/* Queue up all segments except last segment. */
			else if( evt.target == attackButton )
			{
				sgEnv.envelopePort.queue( sgEnvData, 0, numFrames-1 );
				return true;
			}
/* Queue final segment. */
			else if( evt.target == releaseButton )
			{
				sgEnv.envelopePort.queue( sgEnvData, numFrames-1, 1 );
				return true;
			}
		} catch (SynthException e) {
			SynthAlert.showError(this,e);
			return true;
		}
		return false;
    }

}
