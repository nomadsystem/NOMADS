
package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view102.SynthScope;

/**
 * General Purpose Digital Oscilloscope
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

public class TJ_Oscilloscope extends Applet implements ItemListener
{
	LineIn         lineIn;
	LineOut        lineOut;
	SynthScope     scope;
	SynthContext   synthContext;
	Checkbox       echoBox;

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_Oscilloscope applet = new TJ_Oscilloscope();
		AppletFrame frame = new AppletFrame("Oscilloscope", applet);
		frame.resize(600,500);
		frame.show();
/* Begin test after frame opened so that DirectSound will use Java window. */
		frame.test();
	}

/*
 * Setup synthesis.
 */
	public void start()
	{
		setLayout( new BorderLayout() );
		
		try
		{
		Synth.requestVersion(142); // for SynthContext
		
	// Create a unique SynthContext so that this Applet cannot interfere with other Applets. */
		synthContext = new SynthContext();
		
	// Start synthesis engine.
		try
		{
	// try to open full duplex
			synthContext.startEngine( Synth.FLAG_ENABLE_INPUT );
			lineOut = new LineOut( synthContext );
	// create a checkbox for echoing input to output
			echoBox = new Checkbox("Echo Input to Output", false );
			add( "North", echoBox );
			echoBox.addItemListener( this );
		} catch (SynthException e) {
	// if that fails, then just open with half duplex
			synthContext.startEngine( Synth.FLAG_ENABLE_INPUT | Synth.FLAG_DISABLE_OUTPUT );
		}
		
		lineIn = new LineIn( synthContext );
		if( lineOut != null )
		{
			lineIn.output.connect( 0, lineOut.input, 0 );
			lineIn.output.connect( 1, lineOut.input, 1 );
		}
		
/* Create an oscilloscope to show Line Input. */
		scope = new SynthScope( synthContext, 2048 );
		scope.createProbe( lineIn.output, 0, "Left", Color.red );
		scope.createProbe( lineIn.output, 1, "Right", Color.blue );
		scope.finish();
		scope.getWaveDisplay().setBackground( Color.white );
		scope.getWaveDisplay().setForeground( Color.black );
		add( "Center", scope );

/* Synchronize Java display. */
		getParent().validate();
		getToolkit().sync();

		lineIn.start();

		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	}

	public void stop()
	{
		removeAll();
		try
		{
			lineIn.delete();
			if( lineOut != null ) lineOut.delete();
			lineOut = null;
/* Stop synthesis engine. */
			synthContext.stopEngine();
			synthContext.delete();
		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	}
	
/* Turn on or off lineOut as desired. */
	public void itemStateChanged(ItemEvent e)
	{
		if (e.getID() == ItemEvent.ITEM_STATE_CHANGED )
		{
			if( echoBox.getState() )
			{
				lineOut.start();
			}
			else
			{
				lineOut.stop();
			}
		}
	}
}

