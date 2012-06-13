/**
 * Test any SynthCicruit
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.examples;
import com.softsynth.jsyn.AppletFrame;
import com.softsynth.jsyn.SynthCircuit;
import com.softsynth.jsyn.SynthException;
import com.softsynth.jsyn.circuits.PluckedString;

public class TJ_Sound extends SoundTestApplet
{
	String       className = null;

/* Can be run as either an application or as an applet. */
	public static void main(String args[])
	{
		TJ_Sound applet = new TJ_Sound();
		AppletFrame frame = new AppletFrame("Test Sound", applet);
		frame.resize(600,400);
		frame.show();
		if (args.length > 0)
		{
			System.out.println("arg0 = " + args[0]);
			applet.className = args[0];
		}
		frame.test();
	}
	
/** Override this method to make an Applet that tests a different circuit. */
	public SynthCircuit makeCircuit() throws SynthException
	{
		SynthCircuit mySound = null;
		
/* If a name was not passed on the command line, look for an Applet parameter. */
		try
		{
			if( className == null )
			{
				className = getParameter("Circuit");
			}
		} catch( NullPointerException e ) {
			System.out.println("Applet is really an Application!");
			className = null;
		}
		
		
/* Create circuits and unit generators. */
		if( className == null ) mySound = new PluckedString(); /* MODIFY !!!!!!*/
		else
		{
			System.out.println("Attempt to load " + className );
			mySound = SynthCircuit.loadByName( className );
		}
		
		return mySound;
	}
}
