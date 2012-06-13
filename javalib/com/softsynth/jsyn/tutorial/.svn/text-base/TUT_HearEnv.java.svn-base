/**
 * Use an envelope to control the amplitude of a sine wave.
 * This version uses the obsolete but universal 1.0.2 AWT API for user interfaces.
 *
 * @author (C) 2000 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.tutorial;  /* Put resulting class into tutorial package. */

import java.applet.Applet;
import java.awt.Button;
import java.awt.Event;

import com.softsynth.jsyn.*;

public class TUT_HearEnv extends Applet
{
/* DO: Declare Synthesis Objects here */
	SineOscillator     sineOsc;
	SynthEnvelope      envData;
	EnvelopePlayer     envPlayer;
	LineOut            lineOut;
	
	Button             queueButton;
	Button             clearButton;
	
/* Can be run as either an application or as an applet. */
	public static void main(String args[])
	{
	   TUT_HearEnv  applet = new TUT_HearEnv();
	   AppletFrame frame = new AppletFrame("Hear Envelope", applet);
	   frame.resize(300,100);
	   frame.show();
 /* Begin test after frame opened so that DirectSound will use Java window. */
	   frame.test();
	}

 /*
  * Setup synthesis by overriding start() method.
  */
	public void start()  
	{
	   try
	   {
		  Synth.startEngine(0);
		// Create unit generators.
			sineOsc = new SineOscillator()   ;
			envPlayer = new EnvelopePlayer();  // create an envelope player
			lineOut = new LineOut();

		// Connect oscillator to LineOut so we can hear it.
			sineOsc.output.connect( 0, lineOut.input, 0 );
			sineOsc.output.connect( 0, lineOut.input, 1 );
			
		// control sine wave amplitude with envelope output
			envPlayer.output.connect( sineOsc.amplitude );
		
		// define shape of envelope as an array of doubles
			double[] data =
			{
				0.02, 1.0,  // The envelope moves quickly to 1.0 in 0.02 seconds.
				0.30, 0.5,  // Then it takes 0.3 seconds to drop to 0.5.
				1.20, 0.0   // Then it takes 1.2 seconds to drop to 0.0.
			};
			envData = new SynthEnvelope( data );
			
		// Create buttons.
			add( queueButton = new Button("Queue Envelope") );
			add( clearButton = new Button("Clear Queue") );
			
		// Start units.
			lineOut.start();
			sineOsc.start();
			envPlayer.start();

		// Synchronize Java display to make buttons appear.
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
		  removeAll(); // remove components from Applet panel.
		  Synth.stopEngine();
	   } catch(SynthException e) {
		  SynthAlert.showError(this,e);
	   }
	}
 /* Process button hits. */
   	public boolean action(Event evt, Object what)
   	{
		if( evt.target == queueButton )
   		{
			envPlayer.envelopePort.queue( envData );  // queue an envelope
			return true;
   		}
		if( evt.target == clearButton )
   		{
			envPlayer.envelopePort.clear(); // clear the queue
			return true;
   		}
   		return false;
	}
}