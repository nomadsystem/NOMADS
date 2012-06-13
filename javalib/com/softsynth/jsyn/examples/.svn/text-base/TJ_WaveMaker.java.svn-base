/**
 * Test Osc_Table with harmonic wave designer
 * using Java Audio Synthesiser
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */
package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Panel;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.util.HarmonicTable;
import com.softsynth.jsyn.view11x.ExponentialPortFader;
import com.softsynth.jsyn.view11x.WaveDisplay;
import com.softsynth.jsyn.view11x.WaveMaker;
import com.softsynth.jsyn.view11x.WaveTrace;

public class TJ_WaveMaker extends Applet
{
	HarmonicTable    myTable;
	TableOscillator  myWaveOsc;
	LineOut          myOut;
	final static int WAVE_LENGTH = 512; /* Not including guard point. */
	final static int NUM_HARMONICS = 12;
	WaveMaker        maker;
	WaveDisplay      display;

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_WaveMaker applet = new TJ_WaveMaker();
		AppletFrame frame = new AppletFrame("Test WaveMaker", applet);
		frame.resize(600,700);
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

		Synth.verbosity = Synth.SILENT;

/* Create SynthUnits whose peers are unit generators. */
		myWaveOsc = new TableOscillator();
		myOut = new LineOut();
		myTable = new HarmonicTable( WAVE_LENGTH + 1, NUM_HARMONICS );  /* Include guard point. */

/* Create WaveMaker object to build waveforms. */
		display = new WaveDisplay( );
		display.setBackground( Color.blue );
		display.resize( 600, 400 );
		add( display );
		maker = new WaveMaker( myTable, display );
		display.addTrace( new WaveTrace( myTable.data, Color.white, 1.0 ));
		add( maker );
		myWaveOsc.tablePort.setTable( myTable );

/* Connect SynthUnits to output. */
		myWaveOsc.output.connect( 0, myOut.input, 0 );
		myWaveOsc.output.connect( 0, myOut.input, 1 );

/* Show faders so we can manipulate sound parameters. */
		Panel faderPanel = new Panel();
		faderPanel.setLayout( new GridLayout(0,1) );
		faderPanel.add( new ExponentialPortFader( myWaveOsc.amplitude, 0.8, 0.001, 1.0 ) );
		faderPanel.add( new ExponentialPortFader( myWaveOsc.frequency,  200.0, 40.0, 2000.0 ) );
		add( faderPanel );
		
/* Synchronize Java display. */
		getParent().validate();
		getToolkit().sync();

/* Start execution of units. */
			myOut.start();
			myWaveOsc.start();
		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	}

	public void stop()
	{
		try
		{
/* Stop execution of units. */
			myOut.stop();
			myWaveOsc.stop();

			removeAll(); // remove portFaders
/* Delete units and stop engine. */
			myOut.delete();
			myOut = null;
			myWaveOsc.delete();
			myWaveOsc = null;
			myTable.delete();
			myTable = null;
			maker = null;
			display = null;
/* Stop synthesis engine. */
			Synth.stopEngine();
		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	}
}
