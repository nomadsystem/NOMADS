/**
 * Test Audio Capture by simulating a wire with full duplex audio.
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.examples;
import java.awt.GridLayout;

import com.softsynth.jsyn.*;

public class TJ_FullDuplex extends SoundTestApplet
{
	LineIn             myIn;
	LineOut            myOut;

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_FullDuplex applet = new TJ_FullDuplex();
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
		setLayout( new GridLayout(0,1) );
		
/* Make sure we are using the necessary version of JSyn */
		Synth.requestVersion( 140 );
		
		Synth.setTrace( Synth.SILENT );
		try
		{
/* Start synthesis engine. Use half default frame rate because many PC cards do not support
 * FullDuplex operation at the full sample rate.
 */
		Synth.startEngine( Synth.FLAG_ENABLE_INPUT,
						   Synth.DEFAULT_FRAME_RATE / 2 );
		
/* Make waveform unit generators. */
		myIn = new LineIn();
		myOut = new LineOut();

		myIn.output.connect( 0, myOut.input, 0 );
		myIn.output.connect( 1, myOut.input, 1 );


/* Start execution of units. */
		myOut.start();
		myIn.start();

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
			Synth.stopEngine();
			Synth.setTrace( Synth.SILENT );
		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	}
}
