package com.softsynth.jsyn.examples;
import com.softsynth.jsyn.*;

public class Noodler extends Thread
{
/* DO: Declare Synthesis Objects here */
	SineOscillator   myBeep;
	EnvelopePlayer   myEnvPlayer;
	SynthEnvelope    myEnv;
	LineOut          myOut;
	
	static double 	maxAmp;
	
	int duration, lastTime, frequency;	
	boolean       keepPlaying;

 /*
  * Setup synthesis by overriding start() method.
  */
	public void setupNoodler()  
	{
	   try
	   {
		  
 /* Create unit generators. */
			myBeep = new SineOscillator()   ;
			myEnvPlayer = new EnvelopePlayer();
			myOut = new LineOut();

/* Connect units together. */
			myBeep.output.connect( myEnvPlayer.amplitude );
			myEnvPlayer.output.connect( myOut.input );
			
/* Create Envelope to be played. */
			double[] data =
			{
				0.10, 1.0,  /* duration,value pair for frame[0] */
				0.30, 0.5,
				0.80, 0.0  
			};
			myEnv = new SynthEnvelope( data );
			
/* Start units. */
			myEnvPlayer.start();
			myBeep.start();
			myOut.start();
/* *****************************************/

	   } catch(SynthException e) {
   			System.err.println("Error:" + e);
	   }
	}

/** Set max amp of noodlers, which is computed from elsewhere */
	public static void setMaxAmp( double maxAmp )
	{
		Noodler.maxAmp = maxAmp;
	}
	
/** Request that noodler stop its while() loop. */
	public void die()
	{
		keepPlaying = false;
	}
	
   	public void run()
   	{
   		try
   		{
/* Setup unit generators. */
			setupNoodler();
			keepPlaying = true;
			
/* Loop doing random beeps. */
			while( keepPlaying )
			{
				int now = Synth.getTickCount() + 20;
				
				myBeep.amplitude.set( now, maxAmp );
				myBeep.frequency.set( now, (Math.random() * 1040.0) + 60.0 );
   				myEnvPlayer.envelopePort.queue( now, myEnv );
   				Synth.sleepUntilTick((int) (now + (Math.random() * 1000)) );
			}
			myEnvPlayer.stop();
			myBeep.stop();
			myOut.stop();
			
   		} catch (SynthException e) {
   			System.err.println("Error:" + e);
   		}
    }
}