package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.circuits.WaveShapingOscillator;
import com.softsynth.jsyn.util.BussedVoiceAllocator;
import com.softsynth.jsyn.view102.SynthScope;
import com.softsynth.math.ChebyshevPolynomial;
import com.softsynth.math.PolynomialTableData;

/************************************************************/
/* Calculate pseudo-random 32 bit number based on linear congruential method. */
class LinearCongruentialRandom
{
	long randSeed = 22222;
	
	public void setSeed( int seed )
	{
		randSeed = ((long)seed) & 0xFFFFFFFF;
	}
	
	public int getSeed()
	{
		return (int) randSeed;
	}
	
	int randomInteger()
	{
		randSeed = (randSeed * 196314165) + 907633515;
		randSeed = randSeed & 0xFFFFFFFF;
		return (int) randSeed;
	}
	
	double random()
	{
		return  (randomInteger() & 0x7FFFFFFF) / ((double)0x7FFFFFFF);
	}
	
	int choose( int range )
	{
		long bigRandom = (long) (randomInteger() & 0x7FFFFFFF);
		long temp = bigRandom * range;
		return (int) (temp >> 31);
	}
	
    public static void main(String args[])
	{
		LinearCongruentialRandom lc = new LinearCongruentialRandom();
		for( int i=0; i<200; i++ )
		{
			System.out.print(", " + lc.choose(10) );
			if( (i & 7) == 0 ) System.out.println();
		}
	}
}

/* Allocate Chebyshev based WaveShapingOscillators */
class ChebyshevOscAllocator extends BussedVoiceAllocator
{
	PolynomialTableData chebData;
	SynthTable         table;

	public ChebyshevOscAllocator( int maxVoices,
				int order, int numFrames ) throws SynthException
	{
		super( maxVoices );
	// make table with Chebyshev polynomial to share among voices
		PolynomialTableData chebData =
		          new PolynomialTableData(ChebyshevPolynomial.T(order), numFrames);
		table = new SynthTable(  chebData.getData() );
	}
	
	public SynthCircuit makeVoice() throws SynthException
	{
		SynthNote circ = new WaveShapingOscillator( table );
		circ.amplitude.set( 1.0 / getMaxVoices() );
		return addVoiceToMix( circ );
	}
}

/***************************************************************
 * Play notes using a WaveShapingOscillator.
 * Allocate the notes using a BussedVoiceAllocator. 
 */
public class TJ_ChebyshevSong extends Applet implements Runnable
{
	ChebyshevOscAllocator allocator;
	LineOut                      unitOut;
	SynthScope                   scope;
	boolean                      go = false;
	LinearCongruentialRandom           linCon;
	final static int             scale[] = { 0, 2, 4, 7, 9 }; // pentatonic scale
	final static int             MAX_NOTES = 8;
	final static int             CHEBYSHEV_ORDER = 11;
	
/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_ChebyshevSong applet = new TJ_ChebyshevSong();
		AppletFrame frame = new AppletFrame("TJ_ChebyshevSong", applet);
		frame.setSize(600,500);
		frame.show();
		frame.test();
	}
	
/*
 * Setup synthesis.
 */
	public void start()
	{
		setLayout( new BorderLayout() );
		
		linCon = new LinearCongruentialRandom();
		
		try
		{
/* Start synthesis engine. */
		Synth.startEngine( 0 );

// Create a voice allocator and connect it to a LineOut.
		allocator = new ChebyshevOscAllocator( MAX_NOTES, CHEBYSHEV_ORDER, 1024 );
		unitOut = new LineOut( );
		allocator.getOutput().connect( 0, unitOut.input, 0 );
		allocator.getOutput().connect( 0, unitOut.input, 1 );
		
		unitOut.start();
		
// Show signal on a scope.
		scope = new SynthScope();
		scope.createProbe( allocator.getOutput(), "Bus out", Color.yellow );
		scope.finish();
		scope.hideControls();
		add( "Center", scope );
		
	/* Synchronize Java display. */
		getParent().validate();
		getToolkit().sync();
		
	// start thread that plays notes
		Thread thread = new Thread( this );
		go = true;
		thread.start();
		
		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
		
	}
	
	public void stop()
	{
	// tell song thread to finish
		go = false;
		removeAll();
		try
		{
			System.out.println("call stopEngine");
			Synth.stopEngine();
			System.out.println("returned from stopEngine");
		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	}
	
	double indexToFrequency( int index )
	{
		int octave = index / scale.length;
		int temp = index % scale.length;
		int pitch = scale[temp] + (12 * octave);
		return EqualTemperedTuning.getMIDIFrequency( (int) (pitch + 16) );
	}
		
	public void run()
	{
	// always choose a new song based on time&date
		int savedSeed = (int)System.currentTimeMillis();
	// calculate tempo
		int ticksPerBeat = (int) (Synth.getTickRate() * 0.2);
	// calculate time ahead of any system latency
		int advanceTime = (int) (Synth.getTickRate() * 0.5);
	// time for next note to start
		int nextTime = Synth.getTickCount() + advanceTime;
	// note is ON for half the duration
		int onTime = ticksPerBeat / 2;
		int beatIndex = 0;
		try
		{
			do
			{
		// on every measure, maybe repeat previous pattern
				if( (beatIndex & 7) == 0 )
				{
					if( (Math.random() < (1.0/2.0)) ) linCon.setSeed( savedSeed );
					else if( (Math.random() < (1.0/2.0)) ) savedSeed = linCon.getSeed();
				}
				int numNotes = linCon.choose( 5 );
				for( int i=0; i<numNotes; i++ )
				{
		// allocate a new note, stealing one if necessary
					SynthNote note = (SynthNote) allocator.steal( nextTime,
									nextTime + (2*ticksPerBeat) );
		// calculate random pitch
					double frequency = indexToFrequency( linCon.choose( 30 ) );
		// play note using event buffer for accurate timing
					note.noteOnFor( nextTime, onTime,
									frequency, 0.15 );
				}
				nextTime += ticksPerBeat;
				beatIndex += 1;
				
		// wake up before we need to play note to cover system latency
				Synth.sleepUntilTick( nextTime  - advanceTime );
			} while( go );
		} catch( SynthException e ) {
			System.err.println("Song exiting. " + e);
		}
	}
}
	