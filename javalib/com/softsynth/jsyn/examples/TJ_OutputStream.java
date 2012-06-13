
package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.Button;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.util.SampleQueueOutputStream;
import com.softsynth.jsyn.view11x.PortFader;

class SawtoothStreamer extends SynthCircuit  implements Runnable
{
	SampleReader       mySampler;
	final static int   FRAMES_PER_BLOCK = 400; // number of frames to synthesize at one time
	final static int   FRAMES_IN_BUFFER = 8*1024;
	int                numChannels = 1;
	int                samplesPerBlock;
	short[]            data;
	double             phaseIncr = 0.02;
	double             phase = 0.0;
	double             freqScalar = 1.01;
	SampleQueueOutputStream  outStream;
	Thread             thread;
	SynthInput         amplitude;

	public SawtoothStreamer()
	{
		//	Create SynthUnits to play sample data.
		if( numChannels == 1 )
		{
			mySampler = new SampleReader_16F1();
		}
		else if( numChannels == 2 )
		{
			mySampler = new SampleReader_16F2();
		}
		else
		{
			throw new RuntimeException("This example only support mono or stereo!");
		}
		add( mySampler );
		
		samplesPerBlock = FRAMES_PER_BLOCK * numChannels;
		data = new short[samplesPerBlock];
		addPort( amplitude = mySampler.amplitude );

		// Create a stream that we can write to.
		outStream = new SampleQueueOutputStream( mySampler.samplePort, FRAMES_IN_BUFFER, numChannels );
	}
	
	public SynthOutput getOutput()
	{
		return mySampler.output;
	}

	public void run()
	{
		try
		{
			while( thread != null )
			{
				sendBuffer();
			}
		} catch( SynthException e )
		{
			System.out.println("run() caught " + e );
		}
	}

/** Generate an audio signal and send it to the stream to be heard.
 */
	void sendBuffer()
	{
	// synthesize some audio
		int i=0;
		while( i<samplesPerBlock )
		{
		// calculate triangle wave from phase
			double tri;
			if( phase > 0.0 ) tri = 1.0 - (2.0 * phase);
			else tri = 1.0 + (2.0 * phase);
		// convert to full amplitude signed 16 bit
			data[i++] = (short) (tri * 32767.0);
		// write lower amplitude sawtooth to optional second channel
			if( numChannels > 1 ) data[i++] = (short) (phase * 8000.0); 
		// advance phaser and wrap between -1.0 and +1.0
			phase += phaseIncr;
			if( phase > 1.0 ) phase -= 2.0;
		}
	// make frequency slide up and down
		phaseIncr *= freqScalar;
		final double SCALAR = 0.99;
		if( phaseIncr > 0.02 ) freqScalar = SCALAR;
		else if( phaseIncr < 0.002 ) freqScalar = 1.0 / SCALAR;
	
	// Write data to the stream.
	// Will block if there is not enough room so run in a thread.
		outStream.write( data, 0, FRAMES_PER_BLOCK );
	}

	void startStream()
	{
	// Prefill output stream buffer so that it starts out full.
		while( outStream.available() > FRAMES_PER_BLOCK ) sendBuffer();
	// Start slightly in the future so everything is synced.
		int time = Synth.getTickCount() + 4;
		mySampler.start( time );
		outStream.start( time );
			
	// launch a thread to keep stream supplied with data
		thread = new Thread( this );
		thread.start();
	}
	
	void stopStream()
	{
		thread = null;
		System.out.println("Wait for flush().");
		outStream.flush(); // wait for all data already written to stream to be played
		System.out.println("flush() done.");
		int time = Synth.getTickCount();
		mySampler.stop( time );
		outStream.stop( time );
	}
}


/** 
 * Generate a stream of audio data by doing DSP calculations in Java.
 * Output the synthesized data using SampleQueueOutputStream
 *
 * @author (C) 2000 Phil Burk, All Rights Reserved
 */
public class TJ_OutputStream extends Applet
{
	Button             startButton, stopButton;
	SawtoothStreamer streamer;
	LineOut            myOut;

/* Can be run as either an application or as an applet. */
	public static void main(String args[])
	{
		TJ_OutputStream applet = new TJ_OutputStream();
		AppletFrame frame = new AppletFrame("Stream a Sawtooth Wave", applet);
		frame.resize(600,300);
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
// Make sure we are using the necessary version of JSyn
		Synth.requestVersion( 140 );
		
// Start synthesis engine.
		Synth.startEngine( 0 );

		streamer = new SawtoothStreamer();
		
		myOut = new LineOut();
		
// Connect streamer to output.
		streamer.getOutput().connect( 0, myOut.input, 0 );
		if( streamer.getOutput().getNumParts() > 1 )
		{
			streamer.getOutput().connect( 1, myOut.input, 1 );
		}
		else
		{
			streamer.getOutput().connect( 0, myOut.input, 1 );
		}

// Show faders so we can manipulate sound parameters.
		add( new PortFader( streamer.amplitude, 0.7, 0.0, 1.0 ) );

// Start execution of units.
		myOut.start();
		
		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	
		add( startButton = new Button("Start") );
		startButton.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					streamer.startStream();
				}
			} );

		add( stopButton = new Button("Stop") );
		stopButton.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					streamer.stopStream();
				}
			} );
		
// Synchronize Java display.
		getParent().validate();
		getToolkit().sync();
	}

	public void stop()
	{
		try
		{
			streamer.stopStream();
			
// Delete unit peers.
			streamer.delete();
			streamer = null;
			myOut.delete();
			myOut = null;
			removeAll(); // remove portFaders
// Turn off tracing.
			Synth.verbosity = Synth.SILENT;
// Stop synthesis engine.
			Synth.stopEngine();

		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	}

}
