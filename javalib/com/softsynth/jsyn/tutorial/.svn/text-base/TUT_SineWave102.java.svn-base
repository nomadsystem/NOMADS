/**
 * Start or stop an oscillator using JSyn.
 * This version uses the obsolete but universal 1.0.2 AWT API for user interfaces.
 *
 * @author (C) 1998 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.tutorial;  /* Put resulting class into tutorial package. */

import java.applet.Applet;
import java.awt.Checkbox;
import java.awt.Event;

import com.softsynth.jsyn.*;

/* DO: Change TUT_SineWave102 to match the name of your class. Must match file name! */
public class TUT_SineWave102 extends Applet
{
/* DO: Declare Synthesis Objects here */
	SineOscillator     sineOsc;
	LineOut            lineOut;
	
	Checkbox           checker;
	
/* Can be run as either an application or as an applet. */
	public static void main(String args[])
	{
/* DO: Change TUT_SineWave102 to match the name of your class. Must match file name! */
	   TUT_SineWave102  applet = new TUT_SineWave102();
	   AppletFrame frame = new AppletFrame("Test JSyn", applet);
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
 /* DO: Your setup code goes here. ******************/
 /* Create unit generators. */
			sineOsc = new SineOscillator()   ;
			lineOut = new LineOut();

/* Connect oscillator to LineOut so we can hear it. */
			sineOsc.output.connect( 0, lineOut.input, 0 );
			sineOsc.output.connect( 0, lineOut.input, 1 );
			
/* Create a button that causes a beep. */
			add( checker = new Checkbox("Play Sine Wave Oscillator") );
			
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
   
   	public boolean action(Event evt, Object what)
   	{
   		try
   		{
			if( evt.target == checker )
   			{
/* Start or stop depending on state of checkbox. */
				if( checker.getState() )
				{
					sineOsc.start();
				}
				else
				{
					sineOsc.stop();
				}
   				return true;
   			}
   		} catch (SynthException e) {
   			SynthAlert.showError(this,e);
   			return true;
   		}
   		return false;
	}
}