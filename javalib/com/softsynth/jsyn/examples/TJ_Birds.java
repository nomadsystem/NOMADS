package com.softsynth.jsyn.examples;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JApplet;
import javax.swing.JButton;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view102.SynthScope;
import com.softsynth.jsyn.view102.UsageDisplay;
import com.softsynth.tools.view.JAppletFrame;

/**
 * Generate some fake bird sounds using Java Audio Synthesiser. Demonstrates use
 * of EventBuffer with set().
 * 
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

public class TJ_Birds extends JApplet
{
	int numBirds;
	CozumelBird[] birds;
	JButton[] buttons;
	BusReader unitBusReader;
	LineOut unitOut;
	SynthScope scope;
	Dialog scopeDialog;
	SynthContext synthContext;

	/* Can be run as either an application or as an applet. */
	public static void main( String args[] )
	{
		TJ_Birds applet = new TJ_Birds();
		JAppletFrame frame = new JAppletFrame( "Birds", applet );
		frame.setSize( 440, 200 );
		frame.show();
		/*
		 * Begin test after frame opened so that DirectSound will use Java
		 * window.
		 */
		frame.test();
	}

	public String getAppletInfo()
	{
		return "(C) 1997 Phil Burk, updated 11/14/07";
	}

	class ChirpListener implements ActionListener
	{
		CozumelBird bird;

		public ChirpListener(CozumelBird bird)
		{
			this.bird = bird;
		}

		public void actionPerformed( ActionEvent e )
		{
			try
			{
				bird.chirp();
			} catch( SynthException exc )
			{
				exc.printStackTrace();
			}
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
			Synth.requestVersion( 142 ); // for SynthContext

			// Create a unique SynthContext so that this Applet cannot interfere
			// with
			// other Applets. */
			synthContext = new SynthContext();

			// Start synthesis engine.
			synthContext.startEngine( 0 );

			unitBusReader = new BusReader( synthContext );
			unitOut = new LineOut( synthContext );

			/*
			 * Set priority low so that Bus_Read can get current data from bus. *
			 * The Bus_Writes will run at a higher default priority.
			 */
			unitBusReader.setPriority( Synth.PRIORITY_LOW );
			unitBusReader.output.connect( 0, unitOut.input, 0 );
			unitBusReader.output.connect( 0, unitOut.input, 1 );

			// Make several synthContext birds. */
			numBirds = 5;
			birds = new CozumelBird[numBirds];
			buttons = new JButton[numBirds];
			for( int i = 0; i < numBirds; i++ )
			{
				birds[i] = new CozumelBird( synthContext,
						unitBusReader.busInput,
						1000.0 + ((4000.0 * i) / (numBirds + 1)) );
				birds[i].start();
				/* Add some buttons to trigger chirps. */
				getContentPane().add( buttons[i] = new JButton( "Bird" + i ) );
				buttons[i].addActionListener( new ChirpListener( birds[i] ) );
			}

			/* Create an oscilloscope to show bus output. */
			if( true )
			{
				scope = new SynthScope( synthContext, 1024 );
				scope
						.createProbe( unitBusReader.output, "BusOut",
								Color.yellow );
				scope.finish();

				scopeDialog = new Dialog( SynthAlert.getFrame( this ),
						"JSyn Scope" );
				scopeDialog.add( "Center", scope );
				scopeDialog.setBounds( 200, 100, 500, 400 );
				scopeDialog.show();
			}
			else
			{
				add( new UsageDisplay() );
			}

			/* Synchronize Java display. */
			getParent().validate();
			getToolkit().sync();

			/* Start execution of units. */
			unitBusReader.start();
			unitOut.start();

		} catch( SynthException e )
		{
			SynthAlert.showError( this, e );
		}
	}

	public void stop()
	{
		scopeDialog.hide();
		removeAll();
		try
		{
			/* Delete units. */
			for( int i = 0; i < numBirds; i++ )
			{
				birds[i].delete();
			}
			unitBusReader.delete();
			unitOut.delete();
			/* Stop synthesis engine. */
			synthContext.stopEngine();
			synthContext.delete();
		} catch( SynthException e )
		{
			SynthAlert.showError( this, e );
		}
	}

	/**
	 * These are designed to sound somewhat like the Grackle birds in Cozumel,
	 * Mexico. This class is part of TJ_Birds.
	 */
	class CozumelBird extends SynthCircuit
	{
		TriangleOscillator unitOsc;
		SawtoothOscillatorBL unitLFO1;
		ExponentialLag unitLagAmp;
		MultiplyAddUnit unitFreqMix;
		WaveShaper unitWaveShaper;
		BusWriter unitBusWriter;

		SynthTable tableMod2Amp;
		SynthBusInput bus;
		double topFreq;

		public CozumelBird(SynthContext synthContext, SynthBusInput bus,
				double topFreq) throws SynthException
		{
			super( synthContext );
			this.topFreq = topFreq;
			this.bus = bus;

			add( unitOsc = new TriangleOscillator( synthContext ) );
			add( unitLFO1 = new SawtoothOscillatorBL( synthContext ) );
			add( unitLagAmp = new ExponentialLag( synthContext ) );
			add( unitFreqMix = new MultiplyAddUnit( synthContext ) );
			add( unitBusWriter = new BusWriter( synthContext ) );

			/*
			 * Create a waveshaper that converts the sawtooth input to an
			 * amplitude envelope. * When the sawtooth goes / the waveshaper
			 * will go /\. * Thus the amplitude will be 1.0 when the freq offset
			 * is zero, * and will be 0.0 when the freq offset is -1.0 and +1.0.
			 */
			add( unitWaveShaper = new WaveShaper( synthContext ) );
			double[] data = { 0, 1.0, 0 };
			tableMod2Amp = new SynthTable( synthContext, data );
			unitWaveShaper.tablePort.setTable( tableMod2Amp );

			/* Amplitude control. */
			unitLFO1.output.connect( unitWaveShaper.input );
			unitLagAmp.halfLife.set( 0.1 );
			unitLagAmp.output.connect( unitWaveShaper.amplitude );
			unitWaveShaper.output.connect( unitOsc.amplitude );

			/* Frequency control. */
			unitLFO1.output.connect( unitFreqMix.inputA );
			unitFreqMix.output.connect( unitOsc.frequency );

			unitBusWriter.busOutput.connect( bus );

			unitOsc.output.connect( unitBusWriter.input );
			compile();
		}

		/* Trigger wierd chirp from "bird". */
		public void chirp() throws SynthException
		{
			int dur = 100;
			int ticks = getSynthContext().getTickCount();
			/* Change modulation rate randomly. */
			for( int i = 0; i < 6; i++ )
			{
				// Use event buffer to schedule modulation over time.
				unitLagAmp.input.set( ticks, 0.5 ); /* Ramp up amplitude. */
				unitLFO1.frequency.set( ticks, 50.0 * (Math.random() * (Math
						.random() - 0.5)) );
				// Mod Depth
				unitFreqMix.inputB.set( ticks, 600.0 * Math.random() + 100.0 );
				// Center Freq
				unitFreqMix.inputC.set( ticks, 0.5 * topFreq
						* (Math.random() + 1.0) );
				ticks += dur * (Math.random() + 0.5);
				unitLagAmp.input.set( ticks, 0.0 ); // Ramp down amplitude.
				ticks += dur * (Math.random() + 0.5);
			}
		}
	}
}
