package com.softsynth.jsyn.examples;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Vector;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view11x.DecibelPortFader;
import com.softsynth.jsyn.view11x.ExponentialPortFader;
import com.softsynth.jsyn.view11x.PortFader;
import com.softsynth.jsyn.view11x.SynthScope;

/**
 * See many oscillator's waveforms.
 * 
 * @author Phil Burk (C) 1997 Phil Burk
 */

public class TJ_SeeOsc extends Applet
{
	LineOut lineOut;
	MultiplyUnit ampUnit;
	PanUnit panUnit;
	LinearLag lagUnit; // use lag to smooth out frequency control
	SynthScope scope;
	PortFader freqFader;
	PortFader panFader;
	PortFader ampFader;
	Checkbox[] sourceBoxes;
	Vector sounds;
	Vector names;
	Panel cboxPanel;
	SynthOscillator previous = null;

	/* Can be run as either an application or as an applet. */
	public static void main( String args[] )
	{
		TJ_SeeOsc applet = new TJ_SeeOsc();
		AppletFrame frame = new AppletFrame( "See Osc Waveforms", applet );
		frame.setSize( 600, 500 );
		frame.show();
		/*
		 * Begin test after frame opened so that DirectSound will use Java
		 * window.
		 */
		frame.test();
	}

	/** Respond to selection of oscillator. */
	class OscItemListener implements ItemListener
	{
		int oscIndex;

		public OscItemListener(int oscIndex)
		{
			this.oscIndex = oscIndex;
		}

		public void itemStateChanged( ItemEvent e )
		{
			useNthSound( oscIndex );
		}
	}

	/** Make a panel with a radio buton for each oscillator. */
	void makeCBoxPanel()
	{
		cboxPanel = new Panel();
		cboxPanel.setLayout( new GridLayout( 0, 1 ) );

		Panel topRow = new Panel();
		topRow.setLayout( new GridLayout( 1, 0 ) );
		cboxPanel.add( topRow );

		Panel bottomRow = new Panel();
		bottomRow.setLayout( new GridLayout( 1, 0 ) );
		cboxPanel.add( bottomRow );

		CheckboxGroup cbg = new CheckboxGroup();
		sourceBoxes = new Checkbox[sounds.size()];
		for( int i = 0; i < sourceBoxes.length; i++ )
		{
			sourceBoxes[i] = new Checkbox( (String) names.elementAt( i ), cbg,
					(i == 0) );
			if( (i & 1) == 0 )
			{
				topRow.add( sourceBoxes[i] );
			}
			else
			{
				bottomRow.add( sourceBoxes[i] );
			}
			sourceBoxes[i].addItemListener( new OscItemListener( i ) );
		}
	}

	void useNthSound( int soundIndex ) throws SynthException
	{
		if( previous != null )
			previous.stop();
		SynthOscillator osc = (SynthOscillator) sounds.elementAt( soundIndex );
		osc.output.connect( ampUnit.inputA );
		osc.start();
		previous = osc;
	}

	/*
	 * Setup synthesis.
	 */
	public void start()
	{
		sounds = new Vector();
		names = new Vector();

		/* Use GridBagLayout to get reasonably sized components. */
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints constraint = new GridBagConstraints();
		setLayout( gridbag );
		constraint.fill = GridBagConstraints.BOTH;
		constraint.weightx = 1.0;
		constraint.weighty = 1.0;

		try
		{
			/* Make sure we are using the necessary version of JSyn */
			Synth.requestVersion( 141 );
			/* Start synthesis engine. */
			Synth.startEngine( 0 );

			/* Make waveform unit generators. */
			sounds.addElement( new SineOscillator() );
			names.addElement( "Sine" );
			sounds.addElement( new RedNoise() );
			names.addElement( "RedNoise" );

			sounds.addElement( new SawtoothOscillator() );
			names.addElement( "Saw" );
			sounds.addElement( new SawtoothOscillatorBL() );
			names.addElement( "SawBL" );
			sounds.addElement( new SawtoothOscillatorDPW() );
			names.addElement( "SawDPW" );

			sounds.addElement( new SquareOscillator() );
			names.addElement( "Square" );
			sounds.addElement( new SquareOscillatorBL() );
			names.addElement( "SquareBL" );

			sounds.addElement( new ImpulseOscillator() );
			names.addElement( "Impulse" );
			sounds.addElement( new ImpulseOscillatorBL() );
			names.addElement( "ImpBL" );

			sounds.addElement( new TriangleOscillator() );
			names.addElement( "Triangle" );
			// sounds.addElement( new TriangleOscillatorBL() );
			// names.addElement( "TriBL" );

			ampUnit = new MultiplyUnit();
			panUnit = new PanUnit();
			lineOut = new LineOut();
			lagUnit = new LinearLag();
			lagUnit.time.set( 0.05 );
			// take 50 msec to reach new frequency value

			/* Set default sound to zeroth sound. */
			useNthSound( 0 );

			/* Connect lag to all Frequency controls for synchronous control. */
			for( int i = 0; i < sounds.size(); i++ )
			{
				SynthOscillator osc = (SynthOscillator) sounds.elementAt( i );
				lagUnit.output.connect( osc.frequency );
			}

			ampUnit.output.connect( panUnit.input );
			panUnit.output.connect( 0, lineOut.input, 0 );
			panUnit.output.connect( 1, lineOut.input, 1 );

			/* Create an oscilloscope to show each waveform. */
			add( scope = new SynthScope() );
			scope.createProbe( ampUnit.output, "Output", Color.ORANGE );
			scope.finish();

			constraint.gridwidth = GridBagConstraints.REMAINDER;
			constraint.gridheight = 1;
			constraint.weighty = 1.0;
			gridbag.setConstraints( scope, constraint );

			/* Create a fader to control Frequency up to Nyquist rate. */
			add( freqFader = new ExponentialPortFader( lagUnit.input,
					"Frequency", 440.0, 50.0, Synth.getFrameRate() / 2.0 ) );
			constraint.gridheight = 1;
			constraint.weighty = 0.0;
			gridbag.setConstraints( freqFader, constraint );

			/* Create a fader to control Panning up to Nyquist rate. */
			add( panFader = new PortFader( panUnit.pan, "Pan", 0.0, -1.0, 1.0 ) );
			constraint.gridheight = 1;
			constraint.weighty = 0.0;
			gridbag.setConstraints( panFader, constraint );

			/* Create a fader to control Amplitude. */
			add( ampFader = new DecibelPortFader( ampUnit.inputB, "Level (dB)",
					-3.0, -60.0, 0.0 ) );
			constraint.gridheight = GridBagConstraints.RELATIVE;
			gridbag.setConstraints( ampFader, constraint );

			/* Make an array of checkboxes to select which oscillator to hear. */
			makeCBoxPanel();
			constraint.gridheight = GridBagConstraints.REMAINDER;
			gridbag.setConstraints( cboxPanel, constraint );
			add( cboxPanel );

			ampUnit.start();
			panUnit.start();
			lagUnit.start();
			lineOut.start();

		} catch( SynthException e )
		{
			SynthAlert.showError( this, e );
		}

		getParent().validate();
		getToolkit().sync();
	}

	public void stop()
	{
		try
		{
			/* Delete units. */
			ampUnit.delete();
			lagUnit.delete();
			lineOut.delete();
			Enumeration enum = sounds.elements();
			while( enum.hasMoreElements() )
			{
				SynthUnit unit = (SynthUnit) enum.nextElement();
				unit.delete();
			}
			sounds.removeAllElements();
			removeAll(); // remove portFaders
			/* Turn off tracing. */
			Synth.setTrace( Synth.SILENT );
			/* Stop synthesis engine. */
			Synth.stopEngine();

		} catch( SynthException e )
		{
			SynthAlert.showError( this, e );
		}
	}
}
