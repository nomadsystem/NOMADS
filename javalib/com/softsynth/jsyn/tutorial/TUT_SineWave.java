
package com.softsynth.jsyn.tutorial;  /* Put resulting class into tutorial package. */

import java.applet.Applet;
import java.awt.Checkbox;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import com.softsynth.jsyn.*;

/**
 * Start or stop an oscillator using JSyn.
 * AWT 1.1 version
 * @author (C) 1998 Phil Burk, All Rights Reserved
 * <BR>
 * Modifications for AWT 1.1 by Nick Didkovsky
 */

/* DO: Change TUT_SineWave to match the name of your class. Must match file name! 
<br>
	NOTE: this class implements ItemListener, so it can receive ItemEvents from its Checkbox.  
	Buttons and TextFields, for example, send ActionEvents, so if you want to use those, implement ActionListener.
*/
public class TUT_SineWave extends Applet implements ItemListener
{
/* DO: Declare Synthesis Objects here */
	SineOscillator     sineOsc;
	LineOut            lineOut;
	
	Checkbox           checker;
	
/* Can be run as either an application or as an applet. */
	public static void main(String args[])
	{
/* DO: Change TUT_SineWave to match the name of your class. Must match file name! */
	   TUT_SineWave  applet = new TUT_SineWave();
	   AppletFrame frame = new AppletFrame("Test JSyn", applet);
	   frame.setSize(300,100);		// resize() is deprecated, use setSize()
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
 /* DO: Your setup code goes here. ******************/
 /* Create unit generators. */
			sineOsc = new SineOscillator()   ;
			lineOut = new LineOut();

/* Connect oscillator to LineOut so we can hear it. */
			sineOsc.output.connect( 0, lineOut.input, 0 );
			sineOsc.output.connect( 0, lineOut.input, 1 );
			
/* Create a button that causes a beep. */
			add( checker = new Checkbox("Play Sine Wave Oscillator") );
			
/* Call us if the user clicks on the checkbox. */
			checker.addItemListener( this );
			
/* Start unit. */
			lineOut.start();
/* *****************************************/

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
		  removeAll(); // remove components from Applet panel.
		  Synth.stopEngine();
	   } catch(SynthException e) {
		  SynthAlert.showError(this,e);
	   }
	}
   
/** Start or stop depending on state of checkbox. */
	void handleChecker()
	{
  		try {
  			if( checker.getState() )
			{
				sineOsc.start();
			}
			else
			{
				sineOsc.stop();
			}
   		} catch (SynthException e) {
   			SynthAlert.showError(this,e);
   		}
	}
  
/* Respond to event using AWT 1.1 Event model. */
	public void itemStateChanged(ItemEvent e)
	{
		Object source = e.getSource();

		if( source == checker ) handleChecker();
 		// if you have more than one checkbox, add tests and responses here
	}
}