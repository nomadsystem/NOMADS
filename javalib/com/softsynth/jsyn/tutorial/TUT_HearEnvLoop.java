/**
 * Use an envelope to control the amplitude of a sine wave.
 * Play attack-sustain-release parts of envelope.
 * This version uses the obsolete but universal 1.0.2 AWT API for user interfaces.
 *
 * @author (C) 2000 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.tutorial;  /* Put resulting class into tutorial package. */

import java.applet.Applet;
import java.awt.Button;
import java.awt.Event;

import com.softsynth.jsyn.*;

public class TUT_HearEnvLoop extends Applet
{
/* DO: Declare Synthesis Objects here */
	SineOscillator     sineOsc;
	SynthEnvelope      envData;
	EnvelopePlayer     envPlayer;
	LineOut            lineOut;
	
	Button             attackButton;
	Button             loopButton;
	Button             releaseButton;
	
/* Can be run as either an application or as an applet. */
	public static void main(String args[])
	{
	   TUT_HearEnvLoop  applet = new TUT_HearEnvLoop();
	   AppletFrame frame = new AppletFrame("Hear Envelope Sustain Loop", applet);
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
			    0.1, 1.0,  // Take 0.1 seconds to go to value 1.0. "Attack"
				0.10, 0.5,  // #1, Take 0.10 seconds to drop to value 0.5. Part of "Sustain Loop"
				0.05, 0.8,  // #2, Take 0.05 seconds to rise to value 0.8. Part of "Sustain Loop"
			    0.3, 0.0   // Take 0.3 seconds to drop to 0.0.  "Release"
			};
			envData = new SynthEnvelope( data );
			
		// Create buttons.
			add( attackButton = new Button("Attack+Steady") );
			add( loopButton = new Button("Attack+Wiggle") );
			add( releaseButton = new Button("Release") );
			
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
		if( evt.target == attackButton )
   		{
			envPlayer.envelopePort.clear(); // clear the queue
			envPlayer.envelopePort.queue( envData, 0, 1 );  // queue attack
			return true;
   		}
		else if( evt.target == loopButton )
   		{
			envPlayer.envelopePort.clear(); // clear the queue
			envPlayer.envelopePort.queue( envData, 0, 1 );  // queue attack
			envPlayer.envelopePort.queueLoop( envData, 1, 2 );  // queue loop
			return true;
   		}
		else if( evt.target == releaseButton )
   		{
			envPlayer.envelopePort.queue( envData, 3, 1 );  // queue release
			return true;
   		}
   		return false;
	}
}