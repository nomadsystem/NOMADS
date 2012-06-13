
package com.softsynth.jsyn.tutorial;
import com.softsynth.jsyn.AppletFrame;
import com.softsynth.jsyn.Synth;

/** Demonstrate why relative sleep is bad for scheduling notes.
 */
public class TUT_SeqRelative extends TUT_SeqBuffered
{

/* Can be run as either an application or as an applet. */
	public static void main(String args[])
	{
/* DO: Change MyJSynProgram to match the name of your class. Must match file name! */
	   TUT_SeqRelative  applet = new TUT_SeqRelative();
	   AppletFrame frame = new AppletFrame("Play Sequence using Relative Sleep", applet);
	   frame.resize(600,120);
	   frame.show();
 /* Begin test after frame opened so that DirectSound will use Java window. */
	   frame.test();
	}

	TickerThread makeThread()
	{
		return new TickerThreadRelative();
	}
	
	class TickerThreadRelative extends TickerThread
	{
	
		/** This is called by run() method of thread.
		 */
			public void playNotes()
			{
			// Wait until startTime so that both threads start in sync.
				Synth.sleepUntilTick( startTime );
				while( go )
				{
			// Play a note right now.
					bang();
			// Wake up 'duration' ticks from now.
					Synth.sleepForTicks( duration );
				}
			}

	}
}