package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Event;
import java.awt.GridLayout;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view11x.DecibelPortFader;
import com.softsynth.jsyn.view11x.ExponentialPortFader;
import com.softsynth.jsyn.view11x.PortFader;
import com.softsynth.tools.jsyn.CheckForJSyn;

/**
 * Play with an oscillator being modulated by an LFO.
 * Use Band Limited version of sawtooth for better sound quality.
 * Same as TJ_SawFader except this uses its own SynthContext
 * and it uses EXPONENTIAL taper on the fader.
 *
 * @author Phil Burk
 * (C) 1997 Phil Burk
 */

public class TJ_SawFader2 extends Applet
{
	SawtoothOscillatorBL osc;  // BL for Band Limited
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
		TJ_SawFader2 applet = new TJ_SawFader2();
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
    // Make sure JSyn is available.
        int jsynStatus = CheckForJSyn.getStatus();
        if( jsynStatus != CheckForJSyn.AVAILABLE )
        {
            setLayout( new BorderLayout() );
            add( "Center", new CheckForJSyn( this ).setupGUI( jsynStatus ) );
		    getParent().validate();
		    getToolkit().sync();
            return;
        }

		setLayout( new GridLayout(0,1) );
		try
		{
/* Create a unique SynthContext so that this Applet cannot interfere with other Applets. */
		synthContext = new SynthContext();

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

		PortFader freqFader;
		add( new DecibelPortFader( osc.amplitude, "Volume (dB)", -3.0, -60.0, 0.0 ) );
		add( new ExponentialPortFader( lfo.frequency, "ModRate (Hz)", 0.2, 0.1, 20.0 ) );
		add( new ExponentialPortFader( lfo.amplitude, "ModDepth (Hz)", 100.0, 0.01, 1000.0 ) );
		PortFader pitchFader = new PortFader( myLag.input, "CenterFreq (Semitones)", 60.0, 21.0, 108.0 ) {
				public void tweak( int idx, double fval )
				{
					double freq = EqualTemperedTuning.getMIDIFrequency( fval );
					if( getPort() != null ) getPort().set( freq );
			} };
		add( pitchFader );
		add( new PortFader( myPanner.pan,  0.0, -1.0, 1.0 ) );
		add( new ExponentialPortFader( myLag.halfLife,  0.1, 0.002, 1.0 ) );
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
/* Stop synthesis engine. */
			synthContext.stopEngine();
			synthContext.delete();
		} catch (NullPointerException e) {
			System.err.println(e);
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
