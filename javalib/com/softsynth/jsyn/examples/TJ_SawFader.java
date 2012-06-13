package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.Button;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.Label;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view11x.PortFader;

/**
 * Play with an oscillator being modulated by an LFO.
 * Use Band Limited version of sawtooth for better sound quality. 
 *
 * @author Phil Burk
 * (C) 1997 Phil Burk
 */

public class TJ_SawFader extends Applet
{
	SawtoothOscillatorDPW osc;  // BL for Band Limited
	LineOut            lineOut;
	TriangleOscillator lfo;
	AddUnit            adder;
	ExponentialLag     myLag;
	PanUnit            myPanner;
	Button ping;

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_SawFader applet = new TJ_SawFader();
		AppletFrame frame = new AppletFrame("Test Java Synthesis", applet);
		frame.resize(600,300);
		frame.show();
/* Begin test after frame opened so that DirectSound will use Java window. */
		frame.test();
	}

/*
 * Setup synthesis.
 */
	public void start()
	{		
	// check for presence of JSyn plugin
		try
		{
			Synth.getVersion();
		} catch( Throwable thr ) {
			add( new Label("To run this Applet, you need the JSyn Plugin from: ") );
			add( new Label("http://www.softsynth.com/jsyn/plugins/") );
			return;
		}
		
		setLayout( new GridLayout(0,1) );
		try
		{
			
		Synth.setTrace( Synth.SILENT );
/* Start synthesis engine. */
		Synth.startEngine( 0 );
		
/* Make waveform unit generators. */
		osc = new SawtoothOscillatorDPW();
		lfo = new TriangleOscillator();
		adder = new AddUnit();
		myLag = new ExponentialLag();
		myPanner = new PanUnit();
		lineOut = new LineOut();

/* LFO and Lag are added together to calculate new frequency. */
		myLag.output.connect( adder.inputB );
		lfo.output.connect( adder.inputA );
		adder.output.connect( osc.frequency );
/* Connect oscillator to both channels of stereo player. */
		osc.output.connect( myPanner.input );
		myPanner.output.connect( 0, lineOut.input, 0 );
		myPanner.output.connect( 1, lineOut.input, 1 );

		add( new PortFader( osc.amplitude, 0.5, 0.0, 0.999 ) );
		add( new PortFader( lfo.frequency, "ModRate", 0.2, 0.0, 20.0 ) );
		add( new PortFader( lfo.amplitude, "ModDepth", 400.0, 0.0, 2000.0 ) );
		add( new PortFader( myLag.input, "CenterFreq", 200.0, 0.0, 2000.0 ) );
		add( new PortFader( myPanner.pan,  0.0, -1.0, 1.0 ) );
		add( new PortFader( myLag.halfLife,  0.1, 0.0, 1.0 ) );
		add( ping = new Button("Ping") );

/* Start execution of units. */
		lineOut.start();
		osc.start();
		adder.start();
		myLag.start();
		myPanner.start();
		lfo.start();

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
			osc.delete();
			lineOut.delete();
			lfo.delete();
			adder.delete();
			myLag.delete();
			osc = null;
			lineOut = null;
			lfo = null;
			adder = null;
			myLag = null;
			removeAll(); // remove portFaders
/* Turn off tracing. */
			Synth.verbosity = Synth.SILENT;
/* Stop synthesis engine. */
			Synth.stopEngine();

		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	}

/* If the ping button is hit, set current value of Lag_Exponential high. */
	public boolean action(Event evt, Object what)
	{
		try
		{
			if( evt.target == ping )
			{
					myLag.current.set( 2000.0);
					return true;
			}
		} catch (SynthException e) {
			SynthAlert.showError(this,e);
			return true;
		}
		return false;
    }

}
