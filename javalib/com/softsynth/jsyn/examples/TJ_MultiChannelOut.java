package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.GridLayout;
import java.awt.Label;

import com.softsynth.jsyn.*;

/**
 * Output a sine wave on as many channels as are available.
 *
 * @author (C) 2001  Phil Burk, SoftSynth.com, All Rights Reserved
 */

public class TJ_MultiChannelOut extends Applet
{
	SynthContext       synthContext;
// Arrays to hold units for multiple channels.
	ChannelOut         outputs[];
	SineOscillator     sines[];
	int                numOutputChannels;

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_MultiChannelOut applet = new TJ_MultiChannelOut();
		AppletFrame frame = new AppletFrame("Test JSyn Multi-Channel Devices", applet);
		frame.resize(600,300);
		frame.show();
/* Begin test after frame opened so that DirectSound will use Java window. */
		frame.test();
	}

	public void start()
	{

		setLayout( new GridLayout(0,1) );

		try
		{
			startAudio();

		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}

/* Synchronize Java display. */
		getParent().validate();
		getToolkit().sync();
	}


/*
 * Setup synthesis.
 */
	void startAudio()
	{
	// Make sure we are using the necessary version of JSyn
		Synth.requestVersion( 142 );

	// Create a unique SynthContext so that this Applet will not interfere with other Applets. */
		synthContext = new SynthContext();
		synthContext.initialize();

	// Figure out how many channels to use.
		int outDevID = AudioDevice.getDefaultOutputDeviceID();
		numOutputChannels = AudioDevice.getMaxOutputChannels( outDevID );
		add( new Label("Open " + numOutputChannels + " audio channels.", Label.CENTER ) );

		synthContext.start( 0,
						   Synth.DEFAULT_FRAME_RATE,
						   Synth.NO_DEVICE, 0,
						   outDevID, numOutputChannels
						   );

	// Play a sine wave on each channel.
		if( numOutputChannels > 0 )
		{
			outputs = new ChannelOut[numOutputChannels];
			sines = new SineOscillator[numOutputChannels];

			for( int i=0; i<numOutputChannels; i++ )
			{
			// open a mono output to the audio card
				outputs[i] = new ChannelOut(synthContext, i);
				outputs[i].start();

		    // connect sine waves playing a harmonic series.
				sines[i] = new SineOscillator(synthContext);
				sines[i].frequency.set(200.0 * (i+1));
				sines[i].amplitude.set(1.0);
				sines[i].start();
				sines[i].output.connect( outputs[i].input );
			}
		}
	}

	public void stop()
	{
		try
		{
			stopAudio();
			synthContext.delete();
		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	}

/** Delete an array of units.
 */
	void killUnits( SynthUnit units[] )
	{
		if( units != null )
		{
			for( int i=0; i<units.length; i++ )
			{
				units[i].stop();
				units[i].delete();
			}
		}
	}

	void stopAudio()
	{
		killUnits( outputs );
		outputs = null;
		killUnits( sines );
		sines = null;

		synthContext.stop();
	}
}
