package com.softsynth.jsyn.tutorial;
import com.softsynth.jsyn.AppletFrame;
import com.softsynth.jsyn.Synth;

/** Demonstrate why absolute sleep is better for scheduling notes,
 * but not the final solution.
 */
public class TUT_SeqAbsolute extends TUT_SeqBuffered
{
	/* Can be run as either an application or as an applet. */
	public static void main(String args[])
	{
/* DO: Change MyJSynProgram to match the name of your class. Must match file name! */
	   TUT_SeqAbsolute  applet = new TUT_SeqAbsolute();
	   AppletFrame frame = new AppletFrame("Play Sequence using Absolute Sleep", applet);
	   frame.resize(600,120);
	   frame.show();
	   frame.test();
	}
	
	TickerThread makeThread()
	{
		return new TickerThreadAbsolute();
	}

class TickerThreadAbsolute extends TickerThread
{
/** This is called by run() method of thread.
 */
	public void playNotes()
	{
	// try to start in sync
		Synth.sleepUntilTick( startTime );
		int nextTime = startTime;
		while( go )
		{
	// Play a note right now.
			bang();
	// Advance nextTime by fixed amount.
			nextTime += duration;
	// Wake up at nextTime (or slightly later).
			Synth.sleepUntilTick( nextTime );
		}
	}
}

}