/*
 * Fanfare
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */
package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.Button;
import java.awt.Event;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.circuits.FilteredSawtoothBL;
import com.softsynth.jsyn.view102.SoundTester;

public class Fanfare extends Applet

{
	FilteredSawtoothBL[]    fsbl;
	final static int  NUM_VOICES=3;
	LineOut           myOut;
	SynthMixer        mixer;
	Button            bang;

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		Fanfare applet = new Fanfare();
		AppletFrame frame = new AppletFrame("Simple Fanfare", applet);
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
		try
		{
/* Start synthesis engine. */
		Synth.startEngine( 0 );

/* Set level of tracing. */
		Synth.verbosity = Synth.SILENT;

/* Create circuits and unit generators. */
		mixer = new SynthMixer( NUM_VOICES, 2 );
		fsbl = new FilteredSawtoothBL[NUM_VOICES];
		for( int i=0; i<NUM_VOICES; i++ )
		{
			fsbl[i] = new FilteredSawtoothBL();
			mixer.connectInput( i, fsbl[i].output, 0 );
			mixer.setGain( i, 0, i*0.2 );
			mixer.setGain( i, 1, (NUM_VOICES-i-1)*0.2 );
		}
		myOut = new LineOut();
	
/* Connect voice1 to output. */
		mixer.connectOutput( 0, myOut.input, 0 );
		mixer.connectOutput( 1, myOut.input, 1 );

/* Start execution of units. */
		myOut.start();
		mixer.start();

		play();

/* Show faders so we can manipulate sound parameters. */
		add( new SoundTester( fsbl[0] ) );
		add( bang = new Button("Bang"));

/* Synchronize Java display. */
		getParent().validate();
		getToolkit().sync();


		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	}

	public void play()
	throws SynthException
	{
		
		int dur = (int) (Synth.getTickRate()*0.5);
		int now = Synth.getTickCount() + dur ;
/* Use event buffer to play notes at various times in the immediate future. */
		double freq = 150.0;  /* Set initial pitch. */
		double ampl = 0.3;
		fsbl[0].note( now, dur, freq, ampl );

		now+=dur/2;  /* Advance time. */
 /* Play a note 5/4 above previous note.  Set new "fundamental". */
		freq = freq*(5.0/4.0); 
		fsbl[1].note( now, dur, freq, ampl );

		now+=dur/2;
		freq = freq*(3.0/4.0);
		fsbl[0].note( now, dur, freq, ampl );
		fsbl[2].note( now, dur, freq*(4.0/5.0), ampl );

		now+=dur/2;
		freq = freq*(4.0/3.0);
		fsbl[1].note( now, dur, freq, ampl );

		now+=dur/4;
		freq = freq*(4.0/3.0);
		fsbl[0].note( now, dur*2, freq, ampl );

		now+=dur/4;
		fsbl[2].note( now, dur*2, freq*(6.0/4.0), ampl );
		fsbl[1].note( now, dur*2, freq*(5.0/4.0), ampl );
	}

	public void stop()
	{
		try
		{
			removeAll(); // remove portFaders
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
		if(bang == evt.target)
		{
			try
			{
				play();
			} catch (SynthException e) {
				SynthAlert.showError(this,e);
			}
			return true;
		}
		return false;
    }
}
