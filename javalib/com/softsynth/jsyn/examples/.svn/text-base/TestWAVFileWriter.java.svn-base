package com.softsynth.jsyn.examples;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.softsynth.jsyn.util.WAVFileWriter;

/** Create an array of shorts and write them to a WAV file.
 * Demonstrates the use of WAVFileWriter.
 */
public class TestWAVFileWriter
{
    public static void main(String args[])
	{
		try
		{
			RandomAccessFile rfile = new RandomAccessFile( "testout.wav", "rw" );
			WAVFileWriter wavWriter = new WAVFileWriter( rfile );
			
			// wavWriter.setLength(0); // only supported in Java 1.2 !!!
			final int FRAME_RATE = 44100;
			final int NUM_SAMPLES = FRAME_RATE * 3;
			
		// create an array of shorts and fill it with a sawtooth wave
			short data[] = new short[NUM_SAMPLES];
			short phase = 0;
			for( int i=0; i<NUM_SAMPLES; i++ )
			{
				data[i] = phase;
				phase += 456;
			}
		// write the data to a file
			wavWriter.write( data, 1, FRAME_RATE );
			
			wavWriter.close();
			
			System.out.println("Wrote testout.wav");
		} catch( IOException e ) {
			System.err.println( e );
		}
		System.exit(0);
	}
}
