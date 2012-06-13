/**
 * Use the siren circuit just like we would a unit generator.
 *
 * @author (C) 1998 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.tutorial;  /* Put resulting class into tutorial package. */

import java.applet.Applet;
import java.awt.GridLayout;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view102.PortFader;

public class TUT_Siren extends Applet
{
/* Declare Synthesis Objects here */
	Siren              siren;
	LineOut            lineOut;
	
	PortFader          centerFader;
	PortFader          amplitudeFader;
	PortFader          modFreqFader;
	PortFader          modDepthFader;
	
/* Can be run as either an application or as an applet. */
	public static void main(String args[])
	{
/* DO: Change TUT_Siren to match the name of your class. Must match file name! */
	   TUT_Siren  applet = new TUT_Siren();
	   AppletFrame frame = new AppletFrame("Test Siren", applet);
	   frame.resize(500,200);
	   frame.show();
 /* Begin test after frame opened so that DirectSound will use Java window. */
	   frame.test();
	}

 /*
  * Setup synthesis by overriding start() method.
  */
	public void start()  
	{
		setLayout( new GridLayout(0,1) );

		try
		{
			Synth.startEngine(0);
 /* DO: Your setup code goes here. ******************/
 /* Create unit generators. */
			siren = new Siren();
			lineOut  = new LineOut();
			
/* Connect oscillator to LineOut so we can hear it. */
			siren.output.connect( 0, lineOut.input, 0 );
			siren.output.connect( 0, lineOut.input, 1 );
			
/* Create faders to control siren. */
			add( centerFader = new PortFader( siren.frequency, 600.0, 0.0, 1000.0) );
			add( amplitudeFader = new PortFader( siren.amplitude, 0.5, 0.0, 1.0) );
			add( modFreqFader = new PortFader( siren.modulationRate, 0.1, 0.0, 4.0) );
			add( modDepthFader = new PortFader( siren.modulationDepth, 300.0, 0.0, 400.0) );

/* Synchronize Java display to make buttons appear. */
			getParent().validate();
			getToolkit().sync();
			
/* Start units. */
			siren.start();
			lineOut.start();
			
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