/** 
 * Test execution in threads.
 *
 * @author Phil Burk
 */
/*
 * (C) 1997 Phil Burk
 * All Rights Reserved
 */

package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.GridLayout;

import com.softsynth.jsyn.*;

class NoodlerThread extends Thread
{
	boolean    go = true;
	int        tickNumber;
	int        duration;
	int        startTime;
	static int nextID = 0;
	int        id;
	public SineOscillator  myOsc;
	public LineOut  myOut;


	public NoodlerThread( int startTime, int duration )
	throws SynthException
	{
		id = nextID++;
		this.duration = duration;
		this.startTime = startTime;

		myOsc = new SineOscillator();
		myOut = new LineOut();

		myOsc.amplitude.set( 0.2 );
		myOsc.output.connect( 0, myOut.input, 0 );
		myOsc.output.connect( 0, myOut.input, 1 );
	}
	

	void stopSound()
	{
		try
		{
			myOut.stop();
			myOsc.stop();
		} catch (SynthException e) {
			System.err.println(e);
		}
	}

	public void halt()
	{
		go = false;
		interrupt();
	}

	public void run()
	{
		double frequency;

		try
		{
			
			Synth.sleepUntilTick(startTime - Synth.timeAdvance);
			tickNumber = startTime;

			myOsc.frequency.set( 0.0 );
			myOut.start(tickNumber);
			myOsc.start(tickNumber);

			for(int i=0; ((i<10) && go); i++)
			{
				frequency = Math.random()*200.0 + 400.0;
				myOsc.frequency.set( tickNumber, frequency );

				tickNumber += duration;
//				System.out.println(id + " goes to sleep.");
				Synth.sleepUntilTick(tickNumber - Synth.timeAdvance);
//				System.out.println(id + " wakes up.");
			}

		} catch (SynthException e) {
			System.err.println(e);
		}
		stopSound();
	}

}

public class TJ_Threads extends Applet
{
	NoodlerThread noodler1;
	NoodlerThread noodler2;
	NoodlerThread noodler3;

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_Threads applet = new TJ_Threads();
		AppletFrame frame = new AppletFrame("Test Threads", applet);
		frame.resize(600,400);
		frame.show();
/* Begin test after frame opened so that DirectSound will use Java window. */
		frame.test();
	}


/*
 * Setup synthesis.
 */
	public void start()
	{
		setLayout( new GridLayout(0,1) );
		
		try
		{
/* Start synthesis engine. */
		Synth.startEngine( 0 );

		System.out.println("Main thread = " + Synth.debug(Synth.DEBUG_THREAD,0));
/* Create several noodlers that run at different rates. */

		int startTime = Synth.getTickCount();
		noodler1 = new NoodlerThread( startTime+100, 3000 );
		noodler2 = new NoodlerThread( startTime+600, 1500 );
		noodler3 = new NoodlerThread( startTime+1100, 2000 );

/* Start execution of units staggered in time. */
		startTime = Synth.getTickCount();
		noodler1.start();
		noodler2.start();
		noodler3.start();
		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	}
	
	public void stop()
	{
		try
		{
			noodler1.halt();
			noodler2.halt();
			noodler3.halt();
/* Turn off tracing. */
			Synth.verbosity = Synth.SILENT;
/* Stop synthesis engine. */
			Synth.stopEngine();

		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	}
}
