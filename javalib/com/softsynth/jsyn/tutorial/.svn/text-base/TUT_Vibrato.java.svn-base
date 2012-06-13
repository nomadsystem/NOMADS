
/**
 * Modulate the frequency of one oscillator with another using JSyn.
 *
 * @author (C) 1998 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.tutorial;  /* Put resulting class into tutorial package. */

import java.applet.Applet;
import java.awt.GridLayout;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view102.PortFader;


public class TUT_Vibrato extends Applet
{
/* Declare Synthesis Objects here */
	SineOscillator     modOsc;
	TriangleOscillator triOsc;
	AddUnit            freqAdder;
	MultiplyUnit       modScaler; // just so we can see it in the scope
	LineOut            lineOut;
	
	PortFader          centerFader;
	PortFader          modFreqFader;
	PortFader          modRangeFader;
	
	static final double MAX_MOD_DEPTH = 100.0;
	
/* Can be run as either an application or as an applet. */
	public static void main(String args[])
	{
/* DO: Change TUT_Vibrato to match the name of your class. Must match file name! */
	   TUT_Vibrato  applet = new TUT_Vibrato();
	   AppletFrame frame = new AppletFrame("Test JSyn", applet);
	   frame.resize(500,140);
	   frame.show();
 /* Begin test after frame opened so that DirectSound will use Java window. */
	   frame.test();
	}

 /*
  * Setup synthesis by overriding start() method.
  */
	public void start()  
	{
		setLayout( new GridLayout( 0, 1 ));

		try
		{
			Synth.startEngine(0);
 /* DO: Your setup code goes here. ******************/
 /* Create unit generators. */
			modOsc = new SineOscillator();
			triOsc = new TriangleOscillator();
			freqAdder    = new AddUnit();
			lineOut  = new LineOut();

/* Feed first oscillators through adder to offset center frequency. */
			modOsc.output.connect( freqAdder.inputA );
			freqAdder.output.connect( triOsc.frequency );
			
/* Connect oscillator to LineOut so we can hear it. */
			triOsc.output.connect( 0, lineOut.input, 0 );
			triOsc.output.connect( 0, lineOut.input, 1 );

/* Create a fader to control Frequency. */
			add( centerFader = new PortFader( freqAdder.inputB,
					"Center Frequency", 330.0, 0.0, 500.0) );
		
			add( modFreqFader = new PortFader( modOsc.frequency,
					"Modulation Frequency", 2.0, 0.0, 50.0) );
			
			add( modRangeFader = new PortFader( modOsc.amplitude,
					"Modulation Depth", 0.0, 0.0, MAX_MOD_DEPTH) );

/* *****************************************/
/* Create an oscilloscope to show sine waveforms. */
			modScaler = new MultiplyUnit();
			modOsc.output.connect( modScaler.inputA );
			modScaler.inputB.set( 1.0/MAX_MOD_DEPTH );

/* Synchronize Java display to make buttons appear. */
			getParent().validate();
			getToolkit().sync();
			
/* Start units. */
			modOsc.start();
			freqAdder.start();
			triOsc.start();
			lineOut.start();
			modScaler.start();
			
	   } catch(SynthException e) {
		  SynthAlert.showError(this,e);
	   }
	}

/*
 * Clean up synthesis by overriding stop() method.
 */
	public void stop()  
	{
	   try
	   {
 /* Your cleanup code goes here. */
		  removeAll(); // remove components from Applet panel.
		  Synth.stopEngine();
	   } catch(SynthException e) {
		  SynthAlert.showError(this,e);
	   }
	}
   
}