/*
 * Tuna
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */
package com.softsynth.jsyn.examples.tuna;
import java.util.*;
import java.awt.*;
import java.applet.Applet;
import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view102.*;
import com.softsynth.jsyn.circuits.*;
import com.softsynth.jsyn.examples.GrainFarm;

/*
 * Generate pitches using dynamic tuning.
 */
class DynamicPitchGenerator
{
	double previousPitch;
	double minPitch;
	double maxPitch;
	Random rand;
	static int[] numers = {  1, 4, 3, 5, 9, 5, 6, 2 };
	static int[] denoms = {  1, 3, 2, 4, 8, 3, 5, 1 };

	public DynamicPitchGenerator( double pitch, double minPitch, double maxPitch, Random rand )
	{
		previousPitch = pitch;
		this.rand = rand;
		this.minPitch = minPitch;
		this.maxPitch = maxPitch;
	}

	public double getPitch()
	{
		return previousPitch;
	}

	public double nextPitch( double complexity )
	{
		int rndx = (int) (rand.nextFloat() * rand.nextFloat() * numers.length);
		double f1 = (double) numers[rndx];
		double f2 = (double) denoms[rndx];
		double freq;
		if( (rand.nextInt() & 1) == 0)
		{
			freq = previousPitch * f1 / f2;
		}
		else
		{
			freq = previousPitch * f2 / f1;
		}
		while( freq < minPitch ) freq *= 2.0;
		while( freq > maxPitch ) freq *= 0.5;
//		System.out.println("nextPitch: " + rndx + ", " + freq );
		previousPitch = freq;
		return previousPitch;
	}

}

/* ****************************************************************** */
class MelodicSequence
{
	int[]    durations;
	double[] pitches;
	double[] amplitudes;
	int      ticksPerBeat;
	int      numNotes;
	int      totalDuration;

	public MelodicSequence( int maxNotes )
	{
		pitches = new double[ maxNotes ];
		amplitudes = new double[ maxNotes ];
		durations = new int[ maxNotes ];
		ticksPerBeat = (int) (Synth.getTickRate() * 0.3);
		clear();
	}

	public void add( int duration, double pitch, double amplitude )
	{
		durations[ numNotes ] = duration;
		totalDuration += duration;
		pitches[ numNotes ] = pitch;
		amplitudes[ numNotes ] = amplitude;
		numNotes += 1;
	}

	public void clear()
	{
		numNotes = 0;
		totalDuration = 0;
	}

	public int getTotalDuration()
	{
		return totalDuration;
	}

	public void setTicksPerBeat( int ticks )
	{
		ticksPerBeat = ticks;
	}
	public int getTicksPerBeat( )
	{
		return ticksPerBeat;
	}

	public int size()
	{
		return numNotes;
	}
	public double pitchAt( int n )
	{
		return pitches[n];
	}
	public int durationAt( int n )
	{
		return durations[n];
	}
	public double amplitudeAt( int n )
	{
		return amplitudes[n];
	}
}

/* ******************************************************************
** Interactive sound generator with GUI
*/
class TunaRiff
{
	BusWriter          unitBusWriter;
	SynthNote          unitNote;
	Random             rand;
	long               seedA;
	long               seedB;
	Checkbox           playBox;
	Checkbox           changeBoxA;
	Checkbox           changeBoxB;
	Checkbox           useBoxA;
	Checkbox           useBoxB;
	CheckboxGroup      useGroup;
	boolean            usingA = true;
	Panel              riffPanel;
	double             ampl;
	boolean            started = false;

	public TunaRiff( Panel panel, String name, SynthBusInput bus, SynthNote unitNote, double ampl )
	throws SynthException
	{
/* Mix output using global bus. */
		unitBusWriter   = new BusWriter();
		this.unitNote = unitNote;
		this.ampl     = ampl;
		unitNote.output.connect(unitBusWriter.input);
		unitBusWriter.busOutput.connect( bus );

/* Create a pseudo-random generator. */
		rand = new Random();  /* gets random seed based on time */
		seedA = (long) (Math.random() * Long.MAX_VALUE);
		seedB = (long) (Math.random() * Long.MAX_VALUE);

/* Add some buttons to outside panel. */
		panel.add( riffPanel = new Panel() );
		riffPanel.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
		riffPanel.add( new Label( name ) );
		riffPanel.add( playBox = new Checkbox("Play") );
		riffPanel.add( changeBoxA = new Checkbox("ChangeA", null, true) );
		riffPanel.add( changeBoxB = new Checkbox("ChangeB") );
		useGroup = new CheckboxGroup();
		riffPanel.add( useBoxA = new Checkbox("UseA", useGroup, true ));
		riffPanel.add( useBoxB = new Checkbox("UseB", useGroup, false ));
	}

	public void start( int time )
	throws SynthException
	{
		unitNote.start( time );
		unitBusWriter.start( time );
		started = true;
	}

	public void stop( int time )
	throws SynthException
	{
		unitNote.stop( time );
		unitBusWriter.stop( time );
		started = false;
	}

	public boolean play( int time, MelodicSequence melody )
	throws SynthException
	{
		boolean result = false;

/* Start or stop if needed. */
		if( playBox.getState() )
		{
			if( !started ) start( time );
		}
		else
		{
			if( started ) stop( time );
		}

		if( changeBoxA.getState() )
		{
			seedA = (long) (Math.random() * Long.MAX_VALUE);
			changeBoxA.setState( false );
			if( usingA ) result = true;
		}
		if( changeBoxB.getState() )
		{
			seedB = (long) (Math.random() * Long.MAX_VALUE);
			changeBoxB.setState( false );
			if( !usingA ) result = true;
		}

		if( useBoxA.getState() )
		{
			rand.setSeed( seedA );
			if( !usingA ) result = true;
			usingA = true;
		}
		else
		{
			rand.setSeed( seedB );
			if( usingA ) result = true;
			usingA = false;
		}
		return result;
	}
}

/* ****************************************************************** */
class TunaLead extends TunaRiff
{
	DynamicPitchGenerator pitchGen;
	Label                 label;

	public TunaLead( Panel panel, SynthBusInput bus, double ampl )
	throws SynthException
	{
		super( panel, "Lead", bus, new FilteredSawtoothBL(), ampl );
		pitchGen = new DynamicPitchGenerator( 440.0, 100.0, 1000.0, rand );
		riffPanel.add( label = new Label("0") );
		
	}

	void change( MelodicSequence melody )
	{
/* Make sure melody is not being used while we are changing it. */
		synchronized(melody)
		{
			melody.clear();
			int beat = melody.getTicksPerBeat();
			for( int i=0; i<8; i++ )
			{
				double amp = ampl * ( 1.0 - (i/16.0) );
				int dur = ((rand.nextInt() & 1) == 0) ? beat : beat*2;
				melody.add( dur, pitchGen.nextPitch( 1.0 ), amp );
			}
		}
	}

/* Play melody as given. */
	public boolean play( int time, MelodicSequence melody )
	throws SynthException
	{
		boolean ifChange = super.play( time, melody );
		change(melody);
		label.setText( "T=" + Synth.getTickCount() + ", freq = " + melody.pitchAt(0) );
/* Synchronize Java display. */
		label.getParent().validate();
		
		if( playBox.getState() )
		{
			for( int i=0; i<melody.size(); i++ )
			{
				unitNote.note( time, melody.durationAt(i)/2, melody.pitchAt(i), melody.amplitudeAt(i) );
				time += melody.durationAt(i);
			}
		}
		return ifChange;
	}
}

/* ****************************************************************** */
class TunaBell extends TunaRiff
{
	RingModBell    unitBell;

	public TunaBell( Panel panel, SynthBusInput bus, double ampl )
	throws SynthException
	{
		super( panel, "Fast Bell", bus, new RingModBell(), ampl );
		unitBell = (RingModBell) unitNote;
		unitBell.halfLife.set( 0.1 );
	}

/* Play fast bell pattern using first and middle notes of melody. */
	public boolean play( int time, MelodicSequence melody )
	throws SynthException
	{
		boolean ifChange = super.play( time, melody );
		if( playBox.getState() )
		{
			double pitch = melody.pitchAt(0);
			for( int i=0; i<melody.size(); i++ )
			{
				int dur = melody.getTicksPerBeat() / 2;
				int numNotes = melody.durationAt(i) / dur;
				if ( (i & 3) == 0 )
				{
					pitch = melody.pitchAt(i) * 1.5;
					while( pitch < 1200.0 ) pitch *= 2.0;
				}
				for( int j=0; j<numNotes; j++ )
				{
					int now = time + (dur * j);
		/* Randomly skip notes. */
					if( (j==0) || ( (rand.nextInt() & 3) != 0 ) )
					{
						unitNote.note( now, dur/2, pitch, ampl );
					}
				}
				time += melody.durationAt(i);
			}
		}
		return ifChange;
	}
}

/* ****************************************************************** */
class TunaSwoop extends TunaRiff
{
	NoiseModSwoop    unitSwoop;

	public TunaSwoop( Panel panel, SynthBusInput bus, double ampl )
	throws SynthException
	{
		super( panel, "NoiseModSwoop", bus, new NoiseModSwoop(), ampl );
		unitSwoop = (NoiseModSwoop) unitNote;
	}

/* Swoop noise at rate of beats. */
	public boolean play( int time, MelodicSequence melody )
	throws SynthException
	{
		boolean ifChange = super.play( time, melody );
		if( playBox.getState() )
		{
			double rate = 4.0 * Synth.getTickRate() / melody.getTicksPerBeat();
			unitNote.note( time, 2, rate, ampl );
		}
		return ifChange;
	}
}

/* ****************************************************************** */
class TunaGrains extends TunaRiff
{
	GrainFarm  grains;
	
	public TunaGrains( Panel panel, SynthBusInput bus, double ampl )
	throws SynthException
	{
		super( panel, "Grains", bus, new GrainFarm(8), ampl );
		grains = (GrainFarm) unitNote;
		panel.add( new PortFader( grains.probability,   0.001,   0.0,  0.01   ) );

	}

/* Play melody as given. */
	public boolean play( int time, MelodicSequence melody )
	throws SynthException
	{
		boolean ifChange = super.play( time, melody );
		
		if( playBox.getState() )
		{
			for( int i=0; i<melody.size(); i++ )
			{
				grains.frequency.set( time, melody.pitchAt(i) );
				time += melody.durationAt(i);
			}
		}
		return ifChange;
	}
}

/* ****************************************************************** */
class TunaSwarm extends TunaRiff
{
	Swarm  swarm;
	Checkbox timesBox2, timesBox4, timesBox8;
	
	public TunaSwarm( Panel panel, SynthBusInput bus, double ampl )
	throws SynthException
	{
		super( panel, "Swarm", bus, new Swarm(), ampl );
		swarm = (Swarm) unitNote;
		Panel timesPanel = new Panel();
		CheckboxGroup useGroup = new CheckboxGroup();
		timesPanel.add( timesBox2 = new Checkbox("*2", useGroup, false ));
		timesPanel.add( timesBox4 = new Checkbox("*4", useGroup, true ));
		timesPanel.add( timesBox8 = new Checkbox("*8", useGroup, false ));
		panel.add( timesPanel );
	}

/* Play melody as given. */
	public boolean play( int time, MelodicSequence melody )
	throws SynthException
	{
		boolean ifChange = super.play( time, melody );
		
		if( playBox.getState() )
		{
/* Get frequency scalar from radio buttons. */
			double interval = 1.0;
			if( timesBox2.getState() ) interval = 2.0;
			else if( timesBox4.getState() ) interval = 4.0;
			else if( timesBox8.getState() ) interval = 8.0;
/* Play swarm as a continuous sweep that tracks melody times N. */
			for( int i=0; i<melody.size(); i++ )
			{
				swarm.frequency.set( time, interval * melody.pitchAt(i) );
				time += melody.durationAt(i);
			}
		}
		return ifChange;
	}
}

/* ****************************************************************** */
public class Tuna extends Applet implements Runnable
{
	Vector            riffs;
	MelodicSequence   melody;
	LineOut           unitOut;
//	BusReader         unitReverb;
	Reverb1           unitReverb;
	boolean           keepPlaying;
	Thread            mainThread = null;

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		Tuna applet = new Tuna();
		AppletFrame frame = new AppletFrame("Tuna", applet);
		frame.resize(550,400);
		frame.show();
/* Begin test after frame opened so that DirectSound will use Java window. */
		frame.test();
	}
	
/*
 * Start applet.
 */
	public void start()
	{	
/* Arrange panels for each riff in a column. */
		setLayout( new GridLayout(0,1) );
		
		startSynth();

/* Start thread that plays "music". */
		keepPlaying = true;
		mainThread = new Thread( this );
		mainThread.start();
	}
	
/* ------------------------------- */
	public void stop()
	{
		keepPlaying = false;
		stopSynth();
	}
/*
 * Setup synthesis.
 */
	public void startSynth() throws SynthException
	{	


/* Start synthesis engine. */
		Synth.startEngine( 0 );

		melody = new MelodicSequence( 64 );

/* Set level of tracing. */
		Synth.verbosity = Synth.SILENT;

/* Make a bus for mixing outputs together. */
//		unitReverb = new BusReader( );		
		unitReverb = new Reverb1();
		unitOut = new LineOut( );

/* Set priority low so that Bus_Read can get current data from bus.
** The Bus_Writes will run at a higher default priority.
*/
		unitReverb.setPriority(Synth.PRIORITY_LOW);
		unitReverb.output.connect( 0, unitOut.input, 0);
		unitReverb.output.connect( 0, unitOut.input, 1 );
	
		add( new UsageDisplay() );

		riffs = new Vector();
		riffs.addElement( new TunaLead( this, unitReverb.busInput, 0.2 ) );
		riffs.addElement( new TunaBell( this, unitReverb.busInput, 0.2 ) );
		riffs.addElement( new TunaSwoop( this, unitReverb.busInput, 0.3 ) );
		riffs.addElement( new TunaGrains( this, unitReverb.busInput, 0.5 ) );
		riffs.addElement( new TunaSwarm( this, unitReverb.busInput, 0.4 ) );

/* Start execution of output units. */
		unitReverb.start();
		unitOut.start();

/* Synchronize Java display. */
		getParent().validate();
		getToolkit().sync();

	}
/* ------------------------------- */
	public void stopSynth()  throws SynthException
	{
/* Stop execution of units. */
		unitOut.stop();

/* Delete unit peers. */
		unitOut.delete();
		unitOut = null;
		removeAll(); // remove portFaders
/* Turn off tracing. */
		Synth.verbosity = Synth.SILENT;
/* Stop synthesis engine. */
		Synth.stopEngine();
	}

/* ------------------------------- */
	public void run()
	{
		int beginTick, endTick, elapsed, maxElapsed=-1; /* Used for measuring Java performance. */
		try
		{
/* Calculate number of ticks needed to play some number of seconds ahead. */
			int advance = (int) (Synth.getTickRate() * 0.2);
			int now = Synth.getTickCount();
			while( keepPlaying )
			{
/* Play in the future for more stable timing. */
				beginTick = Synth.getTickCount();
				play( now  + advance );
				endTick = Synth.getTickCount();
				elapsed = endTick - beginTick;
				if( elapsed > maxElapsed )
				{
					maxElapsed = elapsed;
					System.out.println("elapsed = " + elapsed );
				}
				now += melody.getTotalDuration();
				Synth.sleepUntilTick( now );
			}
		} catch (SynthException e) {
			System.out.println("Caught " + e);
		}
	}

/* ------------------------------- */
	public void play( int now )
	throws SynthException
	{
/* Make sure melody does not get changed while we are reading it. */
		synchronized(melody)
		{
			for( int i=0; i<riffs.size(); i++ )
			{
				TunaRiff riff = (TunaRiff) riffs.elementAt( i );
				riff.play( now, melody );
			}
		}
	}

}
