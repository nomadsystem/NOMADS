/** 
 * Demonstrate JSyn's Envelope Editor
 * including selection range feature.
 *
 * @author Copyright (C) 2000 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view11x.EditListener;
import com.softsynth.jsyn.view11x.EnvelopeEditor;

public class TJ_EnvEdit2 extends Applet implements EditListener
{
	LineOut           lineOut;
	SineOscillator    osc;
	EnvelopeEditor    envEditor;
	SynthEnvelope     envelope;
	EnvelopePlayer    envPlayer;
	EnvelopePoints    points;
	final int         MAX_FRAMES = 16;
	Button            onButton;
	Button            offButton;
	Button            clearButton;
	boolean           looping = false;

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_EnvEdit2 applet = new TJ_EnvEdit2();
		AppletFrame frame = new AppletFrame("Test EnvelopeEditor", applet);
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
/* Make sure we are using the necessary version of JSyn */
		Synth.requestVersion( 141 );
		
		setLayout( new BorderLayout() );
		try
		{
/* Start synthesis engine. */
		Synth.startEngine( 0 );

		envelope = new SynthEnvelope( MAX_FRAMES );
		envPlayer = new EnvelopePlayer();
		osc = new SineOscillator();
		lineOut = new LineOut();

// Connect envelope to oscillator amplitude.
		envPlayer.output.connect( 0, osc.frequency, 0 );
// Connect oscillator to output.
		osc.output.connect( 0, lineOut.input, 0 );
		osc.output.connect( 0, lineOut.input, 1 );

		osc.amplitude.set(0.8);

// Add an envelope editor to the center of the panel.
		add( "Center", envEditor = new EnvelopeEditor() );
		envEditor.setBackground( Color.cyan.brighter() );
// Ask editor to inform us when envelope modified.
		envEditor.addEditListener( this );
		
// Create vector of points for editor.
		points = new EnvelopePoints();
		points.setName( osc.amplitude.getName() );
		
// Setup initial envelope shape.
		points.add( 0.5, 600.0 );
		points.add( 0.5, 200.0 );
		points.add( 0.5, 800.0 );
		points.add( 0.5, 0.0 );
		updateEnvelope();

// Tell editor to use these points.
		envEditor.setPoints( points ); 
		envEditor.setMaxPoints( MAX_FRAMES );
		envEditor.setMaxWorldY( 1000.0 );
				
// Setup Queue buttons for triggering envelope.
		Panel buttonPanel = new Panel();
		add( buttonPanel, "South" );
		
		Choice presets;
		buttonPanel.add( presets = new Choice() );
		// presets.addKeyListener( this );
		presets.add( "Points" );
		presets.add( "Sustain" );
		presets.add( "Release" );
   		presets.addItemListener(	new ItemListener() {
   			public void itemStateChanged(ItemEvent e)
   			{
				if( ((String)e.getItem()).equals( "Points" ) )
				{
					envEditor.setMode( EnvelopeEditor.EDIT_POINTS );
				}
				else if( ((String)e.getItem()).equals( "Sustain" ) )
				{
					envEditor.setMode( EnvelopeEditor.SELECT_SUSTAIN );
				}
				else if( ((String)e.getItem()).equals( "Release" ) )
				{
					envEditor.setMode( EnvelopeEditor.SELECT_RELEASE );
				}
			} } );
		
		buttonPanel.add( onButton = new Button("On") );
		onButton.addActionListener(	new ActionListener() {
   				public void actionPerformed(ActionEvent e)
   				{ noteOn();	} } );

		buttonPanel.add( offButton = new Button("Off") );
		offButton.addActionListener(	new ActionListener() {
   				public void actionPerformed(ActionEvent e)
   				{ noteOff();	} } );

/* Start execution of units. */
		envPlayer.start();
		lineOut.start();
		osc.start();
	
		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}

/* Synchronize Java display. */
		getParent().validate();
		getToolkit().sync();
	}
	
	public void stop()
	{
		try
		{
/* Delete unit peers. */
			lineOut.delete();
			lineOut = null;
			osc.delete();
			osc = null;
			envPlayer.delete();
			envPlayer = null;
			removeAll(); // remove portFaders
/* Turn off tracing. */
			Synth.setTrace( Synth.SILENT );
/* Stop synthesis engine. */
			Synth.stopEngine();

		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	}

/** This is called by the EnvelopeEditor when the envelope is modified.
 */	public void objectEdited( Object editor, Object objPoints )
	{
	}
	
/** The editor works on a vector of points, not a real envelope.
 * The data must be written to a real SynthEnvelope in order to use it.
 */
	public void updateEnvelope()
	{
		int numFrames = points.size();
		for( int i=0; i<numFrames; i++ )
		{
			envelope.write( i, points.getPoint( i ), 0, 1 );
		}
		envelope.setSustainLoop( points.getSustainBegin(), points.getSustainEnd() );
		envelope.setReleaseLoop( points.getReleaseBegin(), points.getReleaseEnd() );
	}
/** Play the edited envelope once.
 */
	public void noteOn()
	{
		updateEnvelope();
		envPlayer.envelopePort.queueOn( envelope );
	}
	
/** Play the edited envelope repeatedly.
 */
	public void noteOff()
	{
		updateEnvelope();
		envPlayer.envelopePort.queueOff( envelope );
	}

}
