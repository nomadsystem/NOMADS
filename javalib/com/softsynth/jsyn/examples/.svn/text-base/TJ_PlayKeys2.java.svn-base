/** 
 * Play various JSyn sounds using ASCII keyboard.
 * Demonstrate using an envelope as an LFO.
 * Uses AWT 1.1 for the GUI.
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.util.HarmonicTable;

public class TJ_PlayKeys2 extends Applet implements KeyListener
{
	RedNoise               redNoise;
	FilteredSawEnv         fse;
	LineOut                lineOut;
	static double freq = 440.0;

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_PlayKeys2 applet = new TJ_PlayKeys2();
		AppletFrame frame = new AppletFrame("Play ASCII keyboard.", applet);
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
		
/* Create a simple circuit. */
		fse = new FilteredSawEnv();
		redNoise = new RedNoise();
		lineOut = new LineOut( );

		fse.output.connect( 0, lineOut.input, 0 );
		redNoise.output.connect( 0, lineOut.input, 1 );
		redNoise.amplitude.set(0.0);
		
		fse.start();
		redNoise.start();
		lineOut.start();
		fse.attack();
		fse.setStage( Synth.getTickCount(), 0 );
/* Request keyboard events. */
		addKeyListener( this );
		
/* Put up labels to help performer. */
		add( new Label("Play the ASCII keyboard. Hit 'A' to start sound.") ) ;
		add( new Label("Q,W,E = random freq patterns") ) ;
		add( new Label("R,T,Y = on,off,loop") ) ;
		add( new Label("U,I,O = play noise") ) ;
		add( new Label("A,S,D = queue cutoff envelope loops") ) ;
		add( new Label("Z,X,C = change envelope rate") ) ;
		
		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	
/* Synchronize Java display. */
		getParent().validate();
		getToolkit().sync();
	}


	public void stop()
	{
		lineOut.delete();
		lineOut = null;
		fse.delete();
		fse = null;
/* Turn off tracing. */
		Synth.verbosity = Synth.SILENT;
/* Stop synthesis engine. */
		Synth.stopEngine();
	}
		
	void randomFreqPattern( int numNotes, double dur, double span )
	{
// get current time
		int time = Synth.getTickCount();
// calulate duration in ticks
		int durTicks = (int) (dur * Synth.getTickRate());
// set frequency based on random walk
		for( int i=0; i<numNotes; i++ )
		{
			freq = freq + ((Math.random() * 2 * span) - span);
			if( freq < 80.0 ) freq = 100.0;
			else if ( freq > 1000.0 ) freq = 800.0;
			fse.frequency.set( time, freq );
			time += durTicks;
		}
	}
			
/** Turn on red nosie at the given freq. */
	void startNoise( double freq )
	{
		redNoise.frequency.set( freq );
		redNoise.amplitude.set( 0.4 );
	}
	void stopNoise()
	{
		redNoise.amplitude.set( 0.0 );
	}
	
	public void handleKeyDown( int key )
	{
		int time = Synth.getTickCount();
		switch(key)
		{
// Select different frequency controlling loops.
			case (int)'a': fse.setStage( time, 0 ); break;
			case (int)'s': fse.setStage( time, 1 ); break;
			case (int)'d': fse.setStage( time, 2  ); break;
// Select increasing envelope rates.
			case (int)'z': fse.rate.set( 0.5 ); break;
			case (int)'x': fse.rate.set( 1.0 ); break;
			case (int)'c': fse.rate.set( 2.0 ); break;
// Select random frequency patterns.
			case (int)'q': randomFreqPattern( 8, 0.1, 100.0 ); break;
			case (int)'w': randomFreqPattern( 8, 0.2, 200.0 ); break;
			case (int)'e': randomFreqPattern( 32, 0.02, 10.0 ); break;
// Control amplitude envelope.
			case (int)'r': fse.attack(); break;
			case (int)'t': fse.release(); break;
			case (int)'y': fse.loop(); break;
// Noise source.
			case (int)'u': startNoise( 200.0 ); break;
			case (int)'i': startNoise( 600.0 ); break;
			case (int)'o': startNoise( 2000.0 ); break;
		}
	}
	
	public void handleKeyUp( int key )
	{
		switch(key)
		{
// Turn off noise.
			case (int)'u': stopNoise(); break;
			case (int)'i': stopNoise(); break;
			case (int)'o': stopNoise(); break;
		}	
	}
	
/* Filter auto repeats and call key handler. */
	int lastKeyDown = -1;
	public void keyPressed( KeyEvent e )
	{
		int key = e.getKeyChar();
//		System.out.println("key pressed = " + key);
		if( key != lastKeyDown ) // filter out auto repeat
		{
			lastKeyDown = key;
			handleKeyDown( key );	
		}
	}
	
	public void keyTyped( KeyEvent e ) {};
	
	
	public void keyReleased( KeyEvent e )
	{
		int key = e.getKeyCode(); // FIXME - getKeyChar()?
//		System.out.println("key released = " + key);
// For some bizarre reason, key up events are upper case!
// Convert to lower case.
		if( (key >= 'A') && (key <= 'Z')) key += 'a' - 'A';
		lastKeyDown = -1;
		handleKeyUp( key );	
	}
}

/**
 * Band limited sawtooth wave table played through a
 * state variable resonant filter.
 *
 * @author (C) 1997 Phil Burk, SoftSynth.com, All Rights Reserved
 */

class FilteredSawEnv extends SynthNote
{
	HarmonicTable       myTable;
	TableOscillator     myOsc;
	Filter_StateVariable myFilter;
	SynthEnvelope       ampEnv; 
	EnvelopePlayer      ampEnvPlayer;
	EnvelopePlayer      freqEnvPlayer;
	SynthEnvelope[]     envelopes; 
	MultiplyUnit        freqScalar;
	final static int    NUM_HARMONICS = 12;
	final static int    WAVE_LENGTH = 512;
	final static int    NUM_ENVELOPES = 3;
/* Declare ports. */
	public SynthInput   cutoff;
	public SynthInput   resonance;
	public SynthInput   rate;

/*
 * Setup synthesis.
 */
	public FilteredSawEnv()  throws SynthException
	{
		super();

/* Create various unit generators and add them to circuit. */
		add( myOsc = new TableOscillator() );
		add( myFilter = new Filter_StateVariable() );
		add( ampEnvPlayer = new EnvelopePlayer() );
		add( freqEnvPlayer = new EnvelopePlayer() );
		add( freqScalar = new MultiplyUnit() );

		myTable = new HarmonicTable( WAVE_LENGTH + 1, NUM_HARMONICS );  /* Include guard point. */
		myOsc.tablePort.setTable( myTable );
		myTable.sawtooth();
		
		envelopes = new SynthEnvelope[NUM_ENVELOPES];

// Define cutoff frequency envelopes.
		double[] data0 =
		{
			0.1, 0.9,   /* duration,value pair for frame[0] */
			0.1, 0.5,   /* duration,value pair for frame[1] */
			0.1, 0.7,   /* duration,value pair for frame[2] */
			0.1, 0.2,   /* duration,value pair for frame[2] */
		};
		envelopes[0] = new SynthEnvelope( data0 );
		double[] data1 =
		{
			0.1, 0.1,   /* duration,value pair for frame[0] */
			0.4, 0.5,   /* duration,value pair for frame[1] */
			0.1, 0.0,   /* duration,value pair for frame[2] */
			0.1, 0.9,   /* duration,value pair for frame[2] */
		};
		envelopes[1] = new SynthEnvelope( data1 );
		
		double[] data2 =
		{
			0.1, 0.8,
			0.4, 0.5,
			0.02, 0.0,
			0.02, 0.8,
			0.02, 0.0,
			0.1, 0.4,
		};
		envelopes[2] = new SynthEnvelope( data2 );

// make amplitude envelope.		
		double[] ampData =
		{
			0.02, 0.2,
			0.05, 0.8,
			0.05, 1.0,
			0.3, 0.8, // flat loud segment
			0.1, 0.7,
			0.1, 0.2,
			0.1, 0.0,
			0.2, 0.0, // flat silent segment
		};
		ampEnv = new SynthEnvelope( ampData );

		freqEnvPlayer.output.connect( freqScalar.inputA );
		freqScalar.output.connect( myFilter.frequency );

// Patch oscillator through amplitude envelope multiplier.
		myOsc.output.connect( ampEnvPlayer.amplitude );
		ampEnvPlayer.output.connect( myFilter.input );

/* Make ports on internal units appear as ports on circuit. */ 
		addPort( frequency = myOsc.frequency );
		addPort( amplitude = myOsc.amplitude );
		addPort( cutoff = freqScalar.inputB, "cutoff" );
		addPort( resonance = myFilter.resonance );
		addPort( rate = freqEnvPlayer.rate );
		addPort( output = myFilter.output );

		amplitude.setup(0.0, 0.3, 1.0 );
		frequency.setup(0.0, 160.0, 2000.0);
		cutoff.setup(0.0, 2000.0, 8000.0);
		resonance.setup(0.0, 0.2, 0.9 );
		rate.set(1.0);
	}
/** Queue initial attack portion of amplitude envelope. */
	public void attack()
	{
		ampEnvPlayer.envelopePort.queue( ampEnv, 0, 3 );
	}
	
/** Queue final release portion of amplitude envelope. */
	public void release()
	{
		ampEnvPlayer.envelopePort.queue( ampEnv, 4, 3 );
	}

/** Loop entire amplitude envelope. */
	public void loop()
	{
		ampEnvPlayer.envelopePort.queueLoop( ampEnv );
	}

/* Queue different cutoff frequency envelopes. */
	public void setStage( int time, int stage )  throws SynthException
	{
		freqEnvPlayer.envelopePort.queueLoop( time, envelopes[stage] );
	}
}

