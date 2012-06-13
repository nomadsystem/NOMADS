/**
 * Test Various Circuits using a generic SoundTester interface.
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.Color;
import java.awt.GridLayout;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.circuits.PluckedString;
import com.softsynth.jsyn.view102.SoundTester;
import com.softsynth.jsyn.view102.SynthScope;

public class SoundTestApplet extends Applet
{
	SynthCircuit    circ;
	LineOut         myOut;
	SynthScope      scope;
	public boolean  ifScope = true;

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		SoundTestApplet applet = new SoundTestApplet();
		AppletFrame frame = new AppletFrame("Test Circuit", applet);
		frame.resize(600,500);
		frame.show();
		frame.test();
	}
	
/** Override this method to make an Applet that tests a different circuit. */
	public SynthCircuit makeCircuit() throws SynthException
	{
//		return new LagAmpNoise();
		return new PluckedString();
//		return new AnalogSnare();
//		return new FilteredSawtoothBL();
//		return new patches.Nested1();
//		return new WindSound();
//		return new FMPairEnv();
	}
	
/*
 * Setup synthesis.
 */
	public void start()
	{
		setLayout( new GridLayout(0,1) );
//		System.out.println("SoundTestApplet: enter start()");
		try
		{
/* Set level of tracing. */
		Synth.verbosity = Synth.SILENT;
/* Start synthesis engine. */
		Synth.startEngine( 0 );

/* Create circuits and unit generators. */
		circ = makeCircuit();
		myOut = new LineOut();
	
/* Use generic tester that let's us manipulate sound parameters. */
		add( new SoundTester( circ ) );

/* Connect circ to output so we can hear it. */
		if( circ.output != null )
		{
			circ.output.connect( 0, myOut.input, 0 );
			if( circ.output.getNumParts() > 1 )
			{
				circ.output.connect( 1, myOut.input, 1 );
			}
			else
			{
				circ.output.connect( 0, myOut.input, 1 );
			}
				
			
/* Create an oscilloscope to show circ output. */
			if( ifScope )
			{
				add( scope = new SynthScope() );
				scope.createProbe( circ.output, "Circuit Output 0", Color.yellow );
				if( circ.output.getNumParts() > 1 )
				{
					scope.createProbe( circ.output, 1, "Circuit Output 1", Color.green );
				}
				scope.finish();
			}
		}
		
/* Start execution of units. */
		myOut.start();
		circ.start();

		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}

/* Synchronize Java display. */
		getParent().validate();
		getToolkit().sync();

	}

	public void stop()
	{
//		System.out.println("SoundTestApplet: enter stop()");
		try
		{
			myOut.delete();
			circ.delete();
			removeAll(); // remove portFaders and scope
/* Turn off tracing. */
			Synth.verbosity = Synth.SILENT;
/* Stop synthesis engine. */
			Synth.stopEngine();

		} catch (SynthException e) {
			System.err.println("Caught " + e);
			e.printStackTrace();
		}
//		System.out.println("SoundTestApplet: exit stop()");
	}
}
