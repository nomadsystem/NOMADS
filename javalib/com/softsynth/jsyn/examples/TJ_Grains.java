/**
 * Granular Synthesis using Java Audio Synthesiser
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Panel;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.circuits.MultiTapDelay;
import com.softsynth.jsyn.view11x.ExponentialPortFader;
import com.softsynth.jsyn.view11x.PortFader;
import com.softsynth.jsyn.view11x.SynthScope;
import com.softsynth.jsyn.view11x.UsageDisplay;

public class TJ_Grains extends Applet
{
	final static int NUM_GRAINS = 4;
	GrainFarm    rain;
	MultiTapDelay myReverb;
	LineOut      myOut;
	SynthScope   scope;

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_Grains applet = new TJ_Grains();
		AppletFrame frame = new AppletFrame("Test Grains", applet);
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
		setLayout( new BorderLayout() );
		try
		{
/* Start synthesis engine. */
		Synth.startEngine( 0 );
/* Track objects to prevent garbage collection and to delete at stopEngine(). */
		SynthObject.enableTracking( true );

/* Set level of tracing. */
		Synth.verbosity = Synth.SILENT;

/* Define delay times and gains for multi-tap delay. */
		double[] delays = { 0.457, 0.719, 0.901, 1.0, 1.17 };
		double[] gains = {0.1, -0.3, -0.2, 0.1, 0.3};
		myReverb = new MultiTapDelay( delays, gains );

		rain = new GrainFarm( NUM_GRAINS );
		myOut = new LineOut();

/* Connect rain to output. */
		rain.output.connect( myReverb.input );
		rain.output.connect( 0, myOut.input, 0 );
		myReverb.output.connect( 0, myOut.input, 1 );

/* Show faders so we can manipulate sound parameters. */
		Panel faderPanel = new Panel();
		faderPanel.setLayout( new GridLayout(0,1) );
		faderPanel.add( new PortFader( rain.probability,   0.001,   0.0,  0.01   ) );
		faderPanel.add( new ExponentialPortFader( rain.frequency,    280.0,   50.0, 2000.0   ) );
		faderPanel.add( new ExponentialPortFader( rain.spread,   120.0, 50.0, 2000.0   ) );
		faderPanel.add( new PortFader( rain.grainSpeed,   120.0, 0.0,  500.0   ) );
		faderPanel.add( new PortFader( rain.amplitude,   0.9, 0.0,  1.0   ) );
		faderPanel.add( new PortFader( myReverb.feedback,  0.4, 0.0,  1.0   ) );

		faderPanel.add( new UsageDisplay() );
		add( "North", faderPanel );
		
/* Create an oscilloscope to show envelope and final output. */
		add( "Center", scope = new SynthScope( 512 ) );
		scope.createProbe( rain.grains[0].parabola.output, "Env[0]", Color.yellow );
		scope.createProbe( rain.grains[1].parabola.output, "Env[1]", Color.red );
		scope.createProbe( rain.grains[2].parabola.output, "Env[2]", Color.cyan );
		scope.createProbe( rain.grains[3].parabola.output, "Env[3]", Color.green );
		scope.createProbe( rain.output, "Output", Color.white );
		scope.finish();
		scope.hideControls();
		

/* Start execution of units. */
		myOut.start();
		myReverb.start();
		rain.start();

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
/* Stop execution of units. */
			myOut.stop();
			rain.stop();

/* Delete units. */
			rain.delete();
			rain = null;
			myOut.delete();
			myOut = null;
			scope = null;
			removeAll(); // remove portFaders
/* Turn off tracing. */
			Synth.verbosity = Synth.SILENT;
/* Stop synthesis engine. */
			Synth.stopEngine();

		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	}
}
