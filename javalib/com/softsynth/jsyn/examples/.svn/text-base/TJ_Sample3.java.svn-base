
package com.softsynth.jsyn.examples;
import java.awt.Label;
import java.io.IOException;
import java.io.InputStream;

import com.softsynth.jsyn.AppletFrame;
import com.softsynth.jsyn.SynthSample;

/** 
 * Just like TJ_Sample3 but adds code to monitor the loading of a sample
 * and report its progress.
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

class SampleProgressLabel extends Label implements Runnable
{
	boolean      go;
	SynthSample  sample;
	
	public SampleProgressLabel( SynthSample sample )
	{
		this.sample = sample;
	}
	
/** Launch thread to update progress display periodically. */
	public void start()
	{
		Thread thread = new Thread( this );
		go = true;
		thread.start();
	}
	
/** Stop updating progress display. */
	public void stop()
	{
		go = false;
	}
	
	public void run()
	{
		while( go )
		{
	// Get total file size if ready.
			long total = sample.getFileSize();
			if( total >= 0 )
			{
	// Query how much we have read so far.
				long offset = sample.getNumBytesRead();
	
	// Display progress as a text message.
				setText(
				    "Loaded " + offset + " bytes out of " + total );
			}
	// Sleep a while so we have time to read the display.
			try
			{
				Thread.sleep( 200 );
			} catch( InterruptedException e ) {
			}
		}
	}
}

public class TJ_Sample3 extends TJ_Sample2
{
	SampleProgressLabel  progressLabel;
	
/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_Sample3 applet = new TJ_Sample3();
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
	
/** Display progress while loading sample.
 */
	public void loadSample( SynthSample sample, InputStream stream )  throws IOException
	{	
	// create a label to show progress
		add( progressLabel = new SampleProgressLabel( sample ) );
		
	// synchronize Java display
		getParent().validate();
		getToolkit().sync();
		
	// start showing progress
		progressLabel.start();
		
	// load the sample which could take awhile
		super.loadSample( sample, stream );
		
	// sample done loading so shut down progress display
		progressLabel.stop();
		remove( progressLabel );
	}
		
}
