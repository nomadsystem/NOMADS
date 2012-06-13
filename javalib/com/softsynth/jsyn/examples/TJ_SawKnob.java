package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.*;

import com.softsynth.jsyn.*;
import com.softsynth.tools.jsyn.PortKnob;
import com.softsynth.tools.view.RotaryKnob;

/**
 * Play with an oscillator being modulated by an LFO.
 * Use Band Limited version of sawtooth for better sound quality. 
 * Same as TJ_SawFader except this uses its own SynthContext
 * and it uses EXPONENTIAL taper on the fader.
 * <br>
 * This is similar to TJ_SawFader2 except is uses RotaryKnobs instead of Faders.
 *
 * @author Phil Burk
 * (C) 1997 Phil Burk
 */

public class TJ_SawKnob extends Applet
{
	SynthOscillator    osc;
	LineOut            lineOut;
	TriangleOscillator lfo;
	AddUnit            adder;
	ExponentialLag     myLag;
	PanUnit            myPanner;
	Button             ping;
	SynthContext       synthContext;

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_SawKnob applet = new TJ_SawKnob();
		AppletFrame frame = new AppletFrame("Test Rotary Knobs", applet);
		frame.resize(600,150);
		frame.show();
/* Begin test after frame opened so that DirectSound will use Java window. */
		frame.test();
	}

/*
 * Setup synthesis.
 */
	public void start()
	{
		PortKnob pKnob;
		setLayout( new BorderLayout() );
		
		try
		{
	/* Create a unique SynthContext so that this Applet cannot interfere with other Applets. */
		synthContext = new SynthContext();
			
		synthContext.setTrace( Synth.SILENT );
/* Start synthesis engine. */
		synthContext.startEngine( 0 );
		
/* Make unit generators specific to this SynthContext. */
		osc = new SawtoothOscillatorBL(synthContext);
		lfo = new TriangleOscillator(synthContext);
		adder = new AddUnit(synthContext);
		myLag = new ExponentialLag(synthContext);
		myPanner = new PanUnit(synthContext);
		lineOut = new LineOut(synthContext);

/* LFO and Lag are added together to calculate new frequency. */
		myLag.output.connect( adder.inputB );
		lfo.output.connect( adder.inputA );
		adder.output.connect( osc.frequency );
/* Connect oscillator to both channels of stereo player. */
		osc.output.connect( myPanner.input );
		myPanner.output.connect( 0, lineOut.input, 0 );
		myPanner.output.connect( 1, lineOut.input, 1 );

		
		Panel knobPanel = new Panel();
		knobPanel.setLayout( new GridLayout(1,0) );
		add( "Center", knobPanel );

	// Here is a compact way of creating a PortFader
		knobPanel.add( new PortKnob( lfo.frequency, "ModRate (Hz)", 0.2, 0.1, 20.0, 8 ) );
		
	// Here is an alternative way of creating the PortKnob
		lfo.amplitude.setAlias( "ModDepth (Hz)" );
		lfo.amplitude.setup( 0.1, 20.0, 1000.0 );
		knobPanel.add( new PortKnob( lfo.amplitude ) );
		
		myLag.input.setAlias( "Frequency (Hz)" );
		myLag.input.setup( 0.1, 100.0, 1000.0 );
		knobPanel.add( pKnob = new PortKnob( myLag.input ) );
		pKnob.getKnob().setTaper( RotaryKnob.EXPONENTIAL );
		
		knobPanel.add( new PortKnob( osc.amplitude, "Amplitude", 0.5, 0.0, 1.0, 8 ) );
		knobPanel.add( new PortKnob( myPanner.pan, "Pan", 0.0, -1.0, +1.0, 8 ) );
		
		myLag.halfLife.setup( 0.02, 0.1, 1.0 );
		knobPanel.add( new PortKnob( myLag.halfLife ) );
		
		add( "South", ping = new Button("Ping") );

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
/* Stop synthesis engine. */
			synthContext.stopEngine();
			synthContext.delete();
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
