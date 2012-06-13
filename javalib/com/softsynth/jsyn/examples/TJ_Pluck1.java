
package com.softsynth.jsyn.examples;
import com.softsynth.jsyn.AppletFrame;
import com.softsynth.jsyn.SynthCircuit;
import com.softsynth.jsyn.SynthException;
import com.softsynth.jsyn.circuits.TunedPluckedString;
/** 
 * Test plucked string algorithm using Java Audio Synthesiser.
 *
 * @author (C) 1997 Phil Burk All Rights Reserved
 */

public class TJ_Pluck1 extends SoundTestApplet
{

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_Pluck1 applet = new TJ_Pluck1();
		AppletFrame frame = new AppletFrame("Test Plucked String", applet);
		frame.resize(600,500);
		frame.show();
/* Begin test after frame opened so that DirectSound will use Java window. */
		frame.test();
	}
	
/** Override this method to make an Applet that tests a different circuit. */
	public SynthCircuit makeCircuit() throws SynthException
	{
		return new TunedPluckedString();
	}
}
