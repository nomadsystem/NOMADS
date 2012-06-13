
package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view102.PortFader;

/**
 * Test Mono Sample playback using JSyn
 * Play sample using ASCII keyboard.
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

public class TJ_Sample2 extends Applet
{
	public SynthSample mySamp;
	SampleReader_16V1  mySampler;
	LineOut            myOut;
	Button             hitButton;
	Button             onButton;
	Button             offButton;
	int                lastKeyDown = -1;
	int                lastKeyPlayed = -1;
	double             basePitch;
	boolean            ifApplication = false;
	InputStream        stream;
	final int          NUM_FRAMES = 64;
//	public String      fileName = "samples/Trumpet.aiff";
	public String      fileName = "samples/Clarinet.wav";
	static String keyboard = "zxcvbnmasdfghqwerty123456";  /* define music keyboard layout */

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_Sample2 applet = new TJ_Sample2();
		if( args.length > 0 )
		{
			applet.fileName = args[0];
		}
		applet.ifApplication = true;
		AppletFrame frame = new AppletFrame("Test SynthSample", applet);
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
		Synth.verbosity = Synth.SILENT;

		try
		{
			if( ifApplication )
			{
/* Load sample from a file. */
				File siteDir = new File("E:\\nomad\\www\\softsynth");
				File file = new File( siteDir, fileName );
				stream = (InputStream) (new FileInputStream(file));
			}
			else
			{
				System.out.println( "CodeBase = " + getCodeBase());
				System.out.println( "Path = " + getCodeBase());
/* To load a file from a web page in a browser, you must read from a URL. */
				URL sampleURL = new URL( getCodeBase(), fileName);
				stream = sampleURL.openConnection().getInputStream();
			}

/* Use the correct sample class depending on the file type, "*.aiff" or "*.wav". */
			switch( SynthSample.getFileType( fileName ) )
			{
			case SynthSample.AIFF:
				mySamp = new SynthSampleAIFF();
				break;
			case SynthSample.WAV:
				mySamp = new SynthSampleWAV();
				break;
			default:
				SynthAlert.showError(this, "Unrecognized sample file suffix.");
				break;
			}

			if( mySamp != null ) loadSample( mySamp, stream );

			stream.close();

		} catch( IOException e ) {
			SynthAlert.showError(this,e);
		} catch( SecurityException e ) {
			SynthAlert.showError(this,e);
		}

                if( mySamp.getChannelsPerFrame() != 1 ) {
                  throw new SynthException("Loaded stereo sample but this example only handles mono samples.");
                }
/* Determine MIDI pitch associated with this sample. */
		basePitch = EqualTemperedTuning.getMIDIPitch( mySamp.getBaseFrequency() );

/* Create a unit generator to play the sample. */
		mySampler = new SampleReader_16V1();
/* Create a unit generator to output the sound from the player. */
		myOut = new LineOut();

/* Connect sample player to output. */
		mySampler.output.connect( 0, myOut.input, 0 );
		mySampler.output.connect( 0, myOut.input, 1 );

/* Build GUI */
		add( new Label("Play Keys: " + keyboard) ) ;
		Panel panel = new Panel();
		panel.setLayout( new GridLayout( 1, 0 ) );
		add( panel );
		panel.add( hitButton = new Button("Hit") );
		panel.add( onButton = new Button("On") );
		panel.add( offButton = new Button("Off") );

/* Show faders so we can manipulate sound parameters. */
		add( new PortFader( mySampler.amplitude, 0.7, 0.0, 1.0 ) );
		add( new PortFader( mySampler.rate,  44100.0, 0.0, 88200.0 ) );

/* Start execution of units. */
		myOut.start();
		mySampler.start();

		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}

/* Synchronize Java display. */
		getParent().validate();
		getToolkit().sync();
	}

/* Use this method so we can override it in other classes. */
	public void loadSample( SynthSample sample, InputStream stream ) throws IOException
	{
		sample.load( stream );
	}

	public void stop()
	{
		try
		{
/* Delete unit peers. */
			mySampler.delete();
			mySampler = null;
			myOut.delete();
			myOut = null;
			removeAll(); // remove portFaders
/* Turn off tracing. */
			Synth.verbosity = Synth.SILENT;
/* Stop synthesis engine. */
			Synth.stopEngine();

		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	}

/** Convert MIDI pitch from keyboard to a sample rate in Hz */
	double convertPitchToRate( double pitch )
	{
		double frequency = EqualTemperedTuning.getMIDIFrequency( pitch );
		return mySamp.getSampleRate() * ( frequency / mySamp.getBaseFrequency() );
	}

	boolean setRateByKey( int key ) throws SynthException
	{
/* Lookup position of key in keyboard string. */
		int idx = keyboard.indexOf( key );
		if( idx < 0 ) return false;
		else
		{
			double pitch = idx - 12 + basePitch;  /* Go octave above and below base pitch */
			double srate = convertPitchToRate( pitch );
			mySampler.rate.set( srate );
			return true;
		}
	}

/* Start note like a key press on a MIDI keyboard. */
	public boolean keyDown( Event evt, int key )
	{
		// System.out.println("keyDown = " + key );
		if( key != lastKeyDown )
		{
			lastKeyDown = key;
			try
			{
				if( setRateByKey( key ) )
				{
					mySampler.samplePort.queueOn( mySamp );
					lastKeyPlayed = key;
					return true;
				}
			} catch (SynthException e) {
				SynthAlert.showError(this,e);
				return true;
			}
		}
		return super.keyDown( evt, key );
	}

/* Release note like a key release on a MIDI keyboard. */
	public boolean keyUp( Event evt, int key )
	{
		// System.out.println("keyUp = " + key );
		if( lastKeyPlayed != -1)
		{
			lastKeyPlayed = -1;
			try
			{
				mySampler.samplePort.queueOff( mySamp );
				return true;
			} catch (SynthException e) {
				SynthAlert.showError(this,e);
				return true;
			}
		}
		return super.keyUp( evt, key );
	}

/* Process mouse events. */
	public boolean action(Event evt, Object what)
	{
		try
		{
			if( evt.target == hitButton )
			{
					mySampler.samplePort.queue( mySamp );
					return true;
			}
			else if( evt.target == onButton )
			{
					mySampler.samplePort.queueOn( mySamp );
					return true;
			}
			else if( evt.target == offButton )
			{
					mySampler.samplePort.queueOff( mySamp );
					return true;
			}
		} catch (SynthException e) {
			SynthAlert.showError(this,e);
			return true;
		}
		return false;
    }
}
