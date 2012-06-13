
package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.GridLayout;
import java.awt.Label;

import com.softsynth.jsyn.AppletFrame;
import com.softsynth.view.CustomFader;
import com.softsynth.view.CustomFaderDouble;
import com.softsynth.view.ValueEvent;
import com.softsynth.view.ValueListener;

/**
 * Demonstrate the use of a CustomFaderDouble.
 *
 * @author Phil Burk
 * (C) 1997 Phil Burk
 */

public class TJ_CustomFader extends Applet
{
	private  CustomFaderDouble fader;
	private  Label valueLabel;

/*
 * Setup GUI.
 */
	public void start()
	{
		setLayout( new GridLayout(0,1) );
        // Create a fader that ranges from 0.0 to 100.0
		add( fader = new CustomFaderDouble( CustomFader.HORIZONTAL, 50.0, 0.0, 100.0 ) );

        // Make a Label that we can use to show the fader value.
		add( valueLabel = new Label( "v = 50.0", Label.CENTER ) );

        // Tell the fader to call this ValueListener when the value changes.
        fader.addValueListener( new ValueListener()
            {
                public void valueChanged( ValueEvent e )
                {
                    // display value using Label
                    valueLabel.setText( "v = " + e.getValue() );
                }
            } );

		getParent().validate();
		getToolkit().sync();
	}

	public void stop()
	{
        removeAll();
	}

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_CustomFader applet = new TJ_CustomFader();
		AppletFrame frame = new AppletFrame("Test Custom Fader", applet);
		frame.resize(400,300);
		frame.show();
/* Begin test after frame opened so that DirectSound will use Java window. */
		frame.test();
	}
}
