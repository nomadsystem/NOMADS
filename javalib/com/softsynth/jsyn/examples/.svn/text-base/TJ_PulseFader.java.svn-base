
package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view11x.ExponentialPortFader;
import com.softsynth.jsyn.view11x.PortFader;
import com.softsynth.jsyn.view11x.SynthScope;

/**
 * Play with a Pulse oscillator being modulated by an LFO.
 * Change PulseWidth to change timbre.
 *
 * @author Phil Burk
 * (C) 1997 Phil Burk
 */

public class TJ_PulseFader extends Applet
{
	PulseOscillatorBL   myOscBL;
	PulseOscillator     myOsc;
	SynthDistributor    amplitude;
	SynthDistributor    width;
	LineOut             myOut;
	TriangleOscillator  myLFO;
	AddUnit             mySum;
	SynthScope          scope;
	Dialog              dialog;

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_PulseFader applet = new TJ_PulseFader();
		AppletFrame frame = new AppletFrame("Test Java Synthesis", applet);
		frame.resize(400,300);
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

/* Make a waveform unit generators. */
		myOscBL = new PulseOscillatorBL();
		myOsc = new PulseOscillator();
		myLFO = new TriangleOscillator();
		mySum = new AddUnit();
		myOut = new LineOut();
		amplitude = new SynthDistributor("Amplitude");
		width = new SynthDistributor("PulseWidth");

		amplitude.connect( myOsc.amplitude );
		amplitude.connect( myOscBL.amplitude );
		width.connect( myOsc.width );
		width.connect( myOscBL.width );
		myLFO.output.connect( mySum.inputA );
		mySum.output.connect( myOsc.frequency );
		mySum.output.connect( myOscBL.frequency );
		myOsc.output.connect( 0, myOut.input, 0 );
		myOscBL.output.connect( 0, myOut.input, 1 );

		add( new PortFader( amplitude, 0.5, 0.0, 0.999 ) );
		add( new PortFader( width, 0.0, -1.0, 1.0 ) );
		add( new PortFader( myLFO.frequency, "ModRate", 3.0, 0.0, 20.0 ) );
		add( new PortFader( myLFO.amplitude, "ModDepth", 300.0, 0.0, 1000.0 ) );
		add( new ExponentialPortFader( mySum.inputB, "CenterFreq", 400.0, 50.0, 20000.0 ) );

/* Create an oscilloscope to show oscillator and LFO output. */
		scope = new SynthScope();
		scope.createProbe( myOsc.output, "Osc", Color.yellow );
		scope.createProbe( myOscBL.output, "OscBL", Color.cyan );
		scope.createProbe( myLFO.output, "LFO", Color.red );
		scope.createProbe( mySum.output, "Freq", Color.green );
        scope.finish();
		scope.show();
		dialog = new Dialog( (Frame) getParent(), "JSyn Scope", false );
		dialog.resize(600, 600);
		dialog.add( scope );
		dialog.show();
		
/* Start execution of units in the future so everything is synced. */
		int time = Synth.getTickCount() + (int)(Synth.getTickRate() * 0.5);
		myOut.start(time);
		myOsc.start(time);
		myOscBL.start(time);
		mySum.start(time);
		myLFO.start(time);

		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	
		getParent().validate();
		getToolkit().sync();
	}

	public void stop()
	{
		try
		{
/* Stop execution of units. */
			myOsc.delete();
			myOsc = null;
			myLFO.delete();
			myLFO = null;
			mySum.delete();
			mySum = null;
			myOut.delete();
			myOut = null;
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
