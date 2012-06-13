package com.softsynth.jsyn.examples;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JButton;

import com.softsynth.jsyn.*;
import com.softsynth.tools.view.JAppletFrame;

/**
 * Play various sample formats from a column of buttons.
 * 
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

public class TJ_SampleFormats extends JApplet
{
	SampleReader_16V1 monoSampler;
	SampleReader_16V2 stereoSampler;
	AddUnit mixer;
	LineOut myOut;
	boolean ifApplication = false;
	SynthSample[] samples;

	final String SAMPLE_DIR_NAME = "E:\\nomad\\javasonics\\website\\samples";
	// Unsupported
	// "mono_sr22050_ima.wav",
	// "mono_sr44100_b8.aiff",
	// "stereo_sr44100_b8.aiff",
	// "stereo_sr22050_ima.wav",

	final String[] sampleNames = { "mono_sr22050_b16.wav",
			"mono_sr44100_b8.wav", "mono_sr44100_b16.wav",
			"mono_sr44100_b16.aiff", "stereo_sr11025_b16.wav",
			"stereo_sr22050_b16.aiff", "stereo_sr22050_b16.wav",
			"stereo_sr44100_b8.wav", "stereo_sr44100_b16.wav",
			"stereo_sr44100_b16.aiff", "stereo_sr48000_b16.wav", };

	/* Can be run as either an application or as an applet. */
	public static void main( String args[] )
	{
		TJ_SampleFormats applet = new TJ_SampleFormats();
		applet.ifApplication = true;
		JAppletFrame frame = new JAppletFrame( "Test SynthSample", applet );
		frame.setSize( 600, 200 );
		frame.show();
		/*
		 * Begin test after frame opened so that DirectSound will use Java
		 * window.
		 */
		frame.test();
	}

	class SampleTracker implements ActionListener
	{
		SynthSample sample;
		public SampleTracker(SynthSample sample)
		{
				this.sample = sample;
		}

		public void actionPerformed( ActionEvent e )
		{
			playSample( sample );
		}
	}
	
	SynthSample loadSample( String fileName )
	{
		SynthSample mySamp = null;
		InputStream stream = null;
		System.out.println( "Loading: " + fileName );
		try
		{
			if( ifApplication )
			{
				/* Load sample from a file. */
				File siteDir = new File( SAMPLE_DIR_NAME );
				File file = new File( siteDir, fileName );
				stream = (InputStream) (new FileInputStream( file ));
			}
			else
			{
				System.out.println( "CodeBase = " + getCodeBase() );
				System.out.println( "Path = " + getCodeBase() );
				/*
				 * To load a file from a web page in a browser, you must read
				 * from a URL.
				 */
				URL sampleURL = new URL( getCodeBase(), fileName );
				stream = sampleURL.openConnection().getInputStream();
			}

			/*
			 * Use the correct sample class depending on the file type, "*.aiff"
			 * or "*.wav".
			 */
			switch( SynthSample.getFileType( fileName ) )
			{
			case SynthSample.AIFF:
				mySamp = new SynthSampleAIFF();
				break;
			case SynthSample.WAV:
				mySamp = new SynthSampleWAV();
				break;
			default:
				SynthAlert.showError( this, "Unrecognized sample file suffix." );
				break;
			}

			if( mySamp != null )
			{
				loadSample( mySamp, stream );
			}

			stream.close();

			System.out.println( mySamp.dump() );
		} catch( IOException e )
		{
			System.out.println( "Caught " + e );
		} catch( SecurityException e )
		{
			System.out.println( "Caught " + e );
		}
		return mySamp;
	}

	void loadSamples()
	{
		samples = new SynthSample[sampleNames.length];
		for( int i = 0; i < sampleNames.length; i++ )
		{
			samples[i] = loadSample( sampleNames[i] );

			JButton button = new JButton( sampleNames[i] );
			SampleTracker tracker = new SampleTracker( samples[i] );
			button.addActionListener( tracker );
			getContentPane().add( button );
		}
	}

	void playSample( SynthSample sample )
	{
		if( sample.getChannelsPerFrame() == 1 )
		{
			monoSampler.rate.set( sample.getSampleRate() );
			monoSampler.samplePort.queue( sample );
			stereoSampler.samplePort.clear();
		}
		else
		{
			stereoSampler.rate.set( sample.getSampleRate() );
			stereoSampler.samplePort.queue( sample );
			monoSampler.samplePort.clear();
		}
	}

	/*
	 * Setup synthesis.
	 */
	public void start()
	{
		getContentPane().setLayout( new GridLayout( 0, 1 ) );

		try
		{
			/* Start synthesis engine. */
			Synth.startEngine( 0 );

			/* Create a unit generator to play the sample. */
			monoSampler = new SampleReader_16V1();
			stereoSampler = new SampleReader_16V2();
			mixer = new AddUnit();
			/* Create a unit generator to output the sound from the player. */
			myOut = new LineOut();

			/* Connect sample player to output. */
			monoSampler.output.connect( 0, mixer.inputA, 0 );
			stereoSampler.output.connect( 0, mixer.inputB, 0 );
			mixer.output.connect( 0, myOut.input, 0 );
			stereoSampler.output.connect( 1, myOut.input, 1 );

			/* Start execution of units. */
			myOut.start();
			monoSampler.start();
			stereoSampler.start();
			mixer.start();

			loadSamples();
		} catch( SynthException e )
		{
			SynthAlert.showError( this, e );
		}

		/* Synchronize Java display. */
		getParent().validate();
		getToolkit().sync();
	}

	/* Use this method so we can override it in other classes. */
	public void loadSample( SynthSample sample, InputStream stream )
			throws IOException
	{
		sample.load( stream );
	}

	public void stop()
	{
		try
		{
			/* Delete unit peers. */
			monoSampler.delete();
			stereoSampler.delete();
			mixer.delete();
			myOut.delete();
			removeAll(); // remove portFaders
			/* Stop synthesis engine. */

			Synth.stopEngine();

		} catch( SynthException e )
		{
			SynthAlert.showError( this, e );
		}
	}

}
