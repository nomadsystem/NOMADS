/**
 * Test Wind Synthesis using Java Audio Synthesiser
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.examples;
import com.softsynth.jsyn.AppletFrame;
import com.softsynth.jsyn.SynthCircuit;
import com.softsynth.jsyn.SynthException;
import com.softsynth.jsyn.circuits.WindSound;

public class TJ_Wind extends SoundTestApplet
{
/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_Wind applet = new TJ_Wind();
		AppletFrame frame = new AppletFrame("Test Wind", applet);
		frame.resize(600,500);
		frame.show();
/* Begin test after frame opened so that DirectSound will use Java window. */
		frame.test();
	}
	
/** Override this method to make an Applet that tests a different circuit. */
	public SynthCircuit makeCircuit() throws SynthException
	{
		return new WindSound();
	}
}
