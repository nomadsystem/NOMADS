/** 
 * Test recording to disk in non-real-time.
 * Play several frequencies of a sine wave.
 * Save data in a WAV file format.
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.examples;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.softsynth.jsyn.SineOscillator;
import com.softsynth.jsyn.Synth;
import com.softsynth.jsyn.SynthException;
import com.softsynth.jsyn.util.StreamRecorder;
import com.softsynth.jsyn.util.WAVFileWriter;

public class TJ_NonRealTime
{
	SineOscillator        myOsc;
	final static double   FRAME_RATE = 44100.0;
	final static int      FRAMES_PER_BUFFER = 8*1024;
	final static double   TOTAL_TIME = 10.0;
	final static int      TOTAL_FRAMES = (int) (FRAME_RATE * TOTAL_TIME);
	final static int      NUM_REC_CHANNELS = 1;
	StreamRecorder        recorder;
	WAVFileWriter         wavWriter;
	BufferedOutputStream  outStream;
	RandomAccessFile      rfile;
	long                  benchStart, benchStop;
	
/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_NonRealTime app = new TJ_NonRealTime();
		app.test();
		System.exit(0);
	}

/*
 * Setup synthesis.
 */
	public void test()
	{
	// Make sure we are using the necessary version of JSyn
		Synth.requestVersion( 141 );
		
		String fileName = "recorded.wav";
	// Create an output file to write to.
		try
		{
			rfile = new RandomAccessFile( fileName, "rw" );
			wavWriter = new WAVFileWriter( rfile );
			outStream = new BufferedOutputStream( wavWriter );
			System.out.println("Recording to file " + fileName );
			
		} catch( IOException e ) {
			System.err.println(e);
		} catch( SecurityException e ) {
			System.err.println(e);
		}
		

		try
		{
	// write header for WAV file
		wavWriter.writeHeader( NUM_REC_CHANNELS, (int) FRAME_RATE );
		
	// Start synthesis engine in non-real-time mode for faster recording.
		Synth.startEngine( Synth.FLAG_NON_REAL_TIME, FRAME_RATE );
		// Synth.startEngine( FRAME_RATE );

	// Create SynthUnits to generate some sound and record it.
		myOsc = new SineOscillator();

	// Create a recorder that will continuously record its input.
		recorder = new StreamRecorder( outStream, FRAMES_PER_BUFFER, 4, NUM_REC_CHANNELS );
		myOsc.output.connect( recorder.input );
			
	// Start slightly in the future so everything is synced.
		int time = Synth.getTickCount() + 20;
		
	// benchmark for non-real-time.
		benchStart = System.currentTimeMillis();
		
	// start playing and recording
		myOsc.start( time );
		recorder.start( time );
		
	// change frequency several times so we can hear something happen
		int totalTicks = TOTAL_FRAMES / Synth.getFramesPerTick();
		int numNotes = 8;
		int dur = totalTicks/numNotes;
		double freq = 200.0;
		for( int i=0; i<numNotes; i++ )
		{
			myOsc.frequency.set( time, freq );
			time += dur;
			freq *= 4.0/3.0;
			Synth.sleepUntilTick( time );
		}
		
	// stop recorder and oscillator
		recorder.stop( time );
		myOsc.stop( time );
		
	// Go back to the beginning of the random access file and
	// update the WAV file chunk sizes based on final recorded size.
		outStream.flush();
		wavWriter.fixSizes();
		outStream.close();
		rfile.close();

	// print benchmark info
		benchStop = System.currentTimeMillis();
		double realSeconds = 0.001 * (benchStop - benchStart);
		System.out.print("Took " + realSeconds + " seconds");
		System.out.println(" to generate and write " + TOTAL_TIME + " seconds worth of sound.");
		
		Synth.stopEngine();

		} catch( IOException e ) {
			System.err.println(e);
		} catch (SynthException e) {
			System.err.println(e);
		}
	}
}

