/**
 * Start or stop an oscillator using JSyn.
 *
 * @author (C) 1998 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.tutorial;  /* Put resulting class into tutorial package. */

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Panel;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view11x.ExponentialPortFader;
import com.softsynth.jsyn.view11x.LabelledFader;
import com.softsynth.jsyn.view11x.PortFader;
import com.softsynth.jsyn.view11x.SynthScope;

/* DO: Change TUT_SineFreq to match the name of your class. Must match file name! */
public class TUT_SineFreq extends Applet
{
/* DO: Declare Synthesis Objects here */
	SineOscillator     sineOsc;
	LineOut            lineOut;
	
	LabelledFader      freqFader;
	LabelledFader      ampFader;
	SynthScope         scope;
	
/* Can be run as either an application or as an applet. */
	public static void main(String args[])
	{
/* DO: Change TUT_SineFreq to match the name of your class. Must match file name! */
	   TUT_SineFreq  applet = new TUT_SineFreq();
	   AppletFrame frame = new AppletFrame("Test JSyn", applet);
	   frame.resize(500,400);
	   frame.show();
 /* Begin test after frame opened so that DirectSound will use Java window. */
	   frame.test();
	}

 /*
  * Setup synthesis by overriding start() method.
  */
	public void start()  
	{
		Panel faderPanel;
		setLayout( new BorderLayout() );

		try
		{
			Synth.startEngine(0);
 /* DO: Your setup code goes here. ******************/
 /* Create unit generators. */
			sineOsc = new SineOscillator()   ;
			lineOut = new LineOut();

/* Connect oscillator to LineOut so we can hear it. */
			sineOsc.output.connect( 0, lineOut.input, 0 );
			sineOsc.output.connect( 0, lineOut.input, 1 );
			
/* Start amplitude at zero. */
			sineOsc.amplitude.set( 0.0 );
						
/* Start units. */
			lineOut.start();
			sineOsc.start();
			
/* *****************************************/
/* Create an oscilloscope to show sine waveform. */
			add( "Center", scope = new SynthScope() );
			scope.createProbe( sineOsc.output, "Output", Color.yellow );
			scope.finish();
			scope.hideControls();

			faderPanel = new Panel();
			faderPanel.setLayout( new GridLayout( 0, 1 ) );
			add( "North", faderPanel );
			
/* Create an exponential fader to control Frequency. */
			faderPanel.add( freqFader = new ExponentialPortFader( sineOsc.frequency,
					                         "Frequency", 440.0, 50.0, 2000.0) );
			
/* Create an exponential fader to control Amplitude. */
			faderPanel.add( ampFader = new PortFader( sineOsc.amplitude,
											"Amplitude", 0.0, 0.0, 1.0) );

/* Synchronize Java display to make buttons appear. */
			getParent().validate();
			getToolkit().sync();
			
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
			sineOsc.delete();
			removeAll(); // remove components from Applet panel.
			Synth.stopEngine();
		} catch(SynthException e) {
			System.out.println("Caught " + e);
		}
	}
}