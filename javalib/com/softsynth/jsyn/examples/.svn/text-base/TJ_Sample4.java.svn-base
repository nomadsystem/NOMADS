
package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view102.PortFader;

/** 
 * Start and stop a sample in a way that won't cause a click.
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

public class TJ_Sample4 extends Applet
{
	SynthSample        sample;
	SynthSample        sampleZero;
	SampleReader_16V1  samplePlayer;
	SynthEnvelope      envelope;
	EnvelopePlayer     envPlayer;
	
	LineOut            myOut;
	Button             startButton;
	Button             stopButton;
	Button             stopSmoothButton;
	Button             testButton;
	Label              messageLabel;
	boolean            ifApplication = false;
	InputStream        stream;
	final int          NUM_FRAMES = 64;
	public String      fileName = "samples/NotHereNow.wav";
	double             rampUpTime = 0.0; // so we can hear pops
	double             rampDownTime = 0.2;

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_Sample4 applet = new TJ_Sample4();
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
				stream = (InputStream) (new FileInputStream(fileName));
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
				sample = new SynthSampleAIFF();
				break;
			case SynthSample.WAV:
				sample = new SynthSampleWAV();
				break;
			default:
				SynthAlert.showError(this, "Unrecognized sample file suffix.");
				break;
			}
				
			if( sample != null ) loadSample( sample, stream );
			stream.close();
		} catch( IOException e ) {
			SynthAlert.showError(this,e);
		} catch( SecurityException e ) {
			SynthAlert.showError(this,e);
		}

/* Create a unit generator to play the sample. */
		samplePlayer = new SampleReader_16V1();
		short zeros[] = { 0, 0, 0, 0 };
		sampleZero = new SynthSample( zeros.length, 1 );
		sampleZero.write( zeros );
		
		envPlayer = new EnvelopePlayer();
		double data[] =
		{
			rampUpTime, 1.0,
			rampDownTime, 0.0
		};
		envelope = new SynthEnvelope( data );
		
/* Create a unit generator to output the sound from the player. */
		myOut = new LineOut();

/* Connect sample player to output. */
		samplePlayer.output.connect( envPlayer.amplitude );
		envPlayer.output.connect( 0, myOut.input, 0 );
		envPlayer.output.connect( 0, myOut.input, 1 );

/* Build GUI */
		Panel panel = new Panel();
		panel.setLayout( new GridLayout( 1, 0 ) );
		add( panel );
		panel.add( startButton = new Button("Start") );
		panel.add( stopButton = new Button("Stop") );
		panel.add( stopSmoothButton = new Button("StopSmooth") );
		panel.add( testButton = new Button("TestOutput") );

/* Show faders so we can manipulate sound parameters. */
		add( new PortFader( samplePlayer.amplitude, 0.7, 0.0, 1.0 ) );
		add( messageLabel = new Label("  ") );

/* Start execution of units. */
		myOut.start();
		samplePlayer.start();
		envPlayer.start();

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
			envPlayer.delete();
			envPlayer = null;
			samplePlayer.delete();
			samplePlayer = null;
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
	
	void showOutput()
	{
		double outVal = samplePlayer.output.get();
		messageLabel.setText("Output = " + outVal);
		repaint();
	}
	
	void startSample()
	{
		showOutput();
	/* Start sample and ramp envelope up. */
		samplePlayer.start();
		samplePlayer.samplePort.queue( sample );
		envPlayer.envelopePort.queue( envelope, 0, 1 );
	}
	void stopSampleAbruptly()
	{
		showOutput();
		samplePlayer.samplePort.clear();
		envPlayer.envelopePort.clear();
	}
	
	void stopSampleSmoothly()
	{
		showOutput();
	// Get current time.
		int beginTime = Synth.getTickCount();
	// Advance slightly ahead of clock so everything is in sync.
		beginTime += 2;
	// Calculate time when ramp will finish.
		int endTime = beginTime + (int)(rampDownTime * Synth.getTickRate());
	// Queue amplitude envelope rampDown.
		envPlayer.envelopePort.queue( beginTime, envelope, 1, 1 );
	// After envelope finishes, clear sample queue then
	// play a short silence to zero sampler output.
		samplePlayer.samplePort.clear( endTime );
		samplePlayer.samplePort.queue( endTime, sampleZero,
							0, sampleZero.getNumFrames(), Synth.FLAG_AUTO_STOP );
	}
	
/* Process mouse events. */
	public boolean action(Event evt, Object what)
	{
		try
		{
			if( evt.target == startButton )
			{
				startSample();
				return true;
			}
			else if( evt.target == stopButton )
			{
				stopSampleAbruptly();
				return true;
			}
			else if( evt.target == stopSmoothButton )
			{
				stopSampleSmoothly();
				return true;
			}
			else if( evt.target == testButton )
			{
				showOutput();
				return true;
			}
		} catch (SynthException e) {
			SynthAlert.showError(this,e);
			return true;
		}
		return false;
    }
}
