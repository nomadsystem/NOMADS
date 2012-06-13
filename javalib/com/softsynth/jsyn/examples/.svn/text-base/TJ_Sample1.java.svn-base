/** 
 * Test Sample playback using Java Audio Synthesiser
 * Checkbox controls length of sample loop.
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.examples;

import java.applet.Applet;
import java.awt.Checkbox;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view102.PortFader;

public class TJ_Sample1 extends Applet
{
	SynthSample mySamp;
	SampleReader_16V1 mySampler;
	LineOut myOut;
	final int NUM_FRAMES = 64;
	short[] data = new short[NUM_FRAMES];
	Checkbox toggle;
	SynthContext synthContext;

	/* Can be run as either an application or as an applet. */
	public static void main( String args[] )
	{
		TJ_Sample1 applet = new TJ_Sample1();
		AppletFrame frame = new AppletFrame( "Test SynthSample", applet );
		frame.resize( 600, 400 );
		frame.show();
		/*
		 * Begin test after frame opened so that DirectSound will use Java
		 * window.
		 */
		frame.test();
	}

	/*
	 * Setup synthesis.
	 */
	public void start()
	{
		setLayout( new GridLayout( 0, 1 ) );

		try
		{
			Synth.requestVersion( 142 ); // for SynthContext

			// Create a unique SynthContext so that this Applet cannot interfere
			// with other Applets. */
			synthContext = new SynthContext();

			// Start synthesis engine.
			synthContext.startEngine( 0 );

			// Create a sample and fill it with recognizable data.
			mySamp = new SynthSample( synthContext, NUM_FRAMES );
			for( int i = 0; i < NUM_FRAMES / 2; i++ )
			{
				data[i] = (short) (i * 0x100); // Ascending data.
			}
			for( int i = NUM_FRAMES / 2; i < NUM_FRAMES; i++ )
			{
				data[i] = (short) ((NUM_FRAMES - i) * 0x100); // Descending
																// data.
			}
			mySamp.write( data );

			// Create SynthUnits whose peers are unit generators.
			mySampler = new SampleReader_16V1( synthContext );
			myOut = new LineOut( synthContext );

			mySampler.samplePort.queueLoop( mySamp, 0, NUM_FRAMES );

			// Connect SynthUnits to output.
			mySampler.output.connect( 0, myOut.input, 0 );
			mySampler.output.connect( 0, myOut.input, 1 );

			add( toggle = new Checkbox( "Toggle" ) );
			toggle.addItemListener( new ItemListener()
			{
				public void itemStateChanged( ItemEvent e )
				{
					if( toggle.getState() )
					{
						// Queue half of sample.
						mySampler.samplePort.queueLoop( mySamp, 0,
								NUM_FRAMES / 2 );
					}
					else
					{
						// Queue entire sample.
						mySampler.samplePort.queueLoop( mySamp, 0, NUM_FRAMES );
					}
				}
			} );

			// Show faders so we can manipulate sound parameters.
			add( new PortFader( mySampler.amplitude, 0.7, 0.0, 1.0 ) );
			add( new PortFader( mySampler.rate, 44100.0, 0.0, 88200.0 ) );

			// Start execution of units.
			myOut.start();
			mySampler.start();

		} catch( SynthException e )
		{
			SynthAlert.showError( this, e );
		}

		// Synchronize Java display.
		getParent().validate();
		getToolkit().sync();
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
			synthContext.stopEngine();
			synthContext.delete();

		} catch( SynthException e )
		{
			SynthAlert.showError( this, e );
		}
	}

}
