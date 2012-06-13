package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Label;

import com.softsynth.jsyn.AppletFrame;
import com.softsynth.tools.jsyn.CheckForJSyn;

/**
 * Play with an oscillator being modulated by an LFO.
 * Use Band Limited version of sawtooth for better sound quality.
 * Same as TJ_SawFader except this uses its own SynthContext
 * and it uses EXPONENTIAL taper on the fader.
 *
 * @author Phil Burk
 * (C) 1997 Phil Burk
 */

public class TJ_SafeApplet extends Applet
{
    Applet jsynApplet;
    String appletName = "JSynExamples.TJ_Wind";

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_SafeApplet applet = new TJ_SafeApplet();
		AppletFrame frame = new AppletFrame("Test JSyn safe loader", applet);
		frame.resize(600,300);
		frame.show();
/* Begin test after frame opened so that DirectSound will use Java window. */
		frame.test();
	}

/*
 * Check for JSyn and load Applet if present.
 */
	public void init()
	{
        if( CheckForJSyn.getStatus() != CheckForJSyn.AVAILABLE )
        {
            add( new Label( "JSyn plugin not installed." ) );
            add( new Label( "Please download it from http://www.softsynth.com/jsyn/" ) );
            return;
        }

        setLayout( new BorderLayout() );

    // load an Applet that uses JSyn
        try
        {
            Class cl = Class.forName("appletName");
            Object obj = cl.newInstance();
            jsynApplet = (Applet) obj;
            add( "Center", jsynApplet );
        } catch( Exception e )
        {
            System.err.println( e );
        }

        if( jsynApplet != null ) jsynApplet.init();

		getParent().validate();
		getToolkit().sync();
	}


	public void stop()
	{
        if( jsynApplet != null ) jsynApplet.stop();
	}

	public void start()
	{
        if( jsynApplet != null ) jsynApplet.start();
	}

	public void destroy()
	{
        if( jsynApplet != null ) jsynApplet.destroy();
	}

}
