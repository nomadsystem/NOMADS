/*
 * Interactive synthesis piece that incorporates multiple performable
 * objects.
 * The RackablePerformance objects will all appear in a Panel where thay can be manipulated.
 * Each object will receive a periodic "beat" that will allow the object to stay synchronized with
 * other objects. The beats will be indexed with increasing integers.
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */
package com.softsynth.jsyn.examples.perfrack;
import java.util.*;
import java.awt.*;
import java.applet.Applet;
import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view102.*;
import com.softsynth.jsyn.circuits.*;
/** Interface for a monophonic object that has a GUI and a sound generator.
 * These objects can be placed in a "Rack" and played along with other similar objects.
*/
interface RackablePerformance
{
	public SynthOutput getOutput();
	
/** @return GUI component that can be incorporated into big performance display. */
	Component getUserInterface();
	
/** @return Number of "rack units" required for GUI. */
	int getNumRackUnits();
	
	String getName();
	void setName( String name );
	
/** Start synthesis. */
	void start( int time ) throws SynthException;
/** Stop synthesis. */
	void stop( int time ) throws SynthException;
	
/** trigger at a specific time for a specific duration. May be ignored but must be implemented. */
	void beat( int time, int duration, int beatIndex ) throws SynthException;
}
/* ========================================================================== */
/**
 * Simple implementation of RackablePerformance. Interactive sound generator with GUI
 */
class SimpleRackUnit extends Panel implements RackablePerformance
{
	SynthNote          unitNote;
	boolean            started = false;
	int                numRackUnits = 1;
	String             name;
	public SimpleRackUnit()
	{
	}
	
	public String getName() { return name; }
	public void setName( String name ){ this.name = name; }
	
	public SynthOutput getOutput() { return unitNote.output; };
	
	public Component getUserInterface()
	{
		return this;
	}
	
/** @return Number of "rack units" required for GUI. */
	public void setNumRackUnits( int numRackUnits ) { this.numRackUnits = numRackUnits; }
	public int getNumRackUnits() { return numRackUnits; }
	
/** Start playing at the given time. */
	public void start( int time )
	throws SynthException
	{
		unitNote.start( time );
		started = true;
	}
	public void stop( int time )
	throws SynthException
	{
		unitNote.stop( time );
		started = false;
	}
/** Trigger at a specific time for a specific duration. May be ignored. */
	public void beat( int time, int duration, int beatIndex )
	throws SynthException
	{
	}
}
/* ========================================================================== */
class BellPatternRackUnit extends SimpleRackUnit
{
	static final int NUM_BOXES = 16;
	static final int BOXES_PER_BEAT = 2;
	Panel            panel;
	CheckboxPanel    cbPanel;
	RingModBell      bell;
	BarGraphEditor    freqEditor;
	double           freqData[];
	
	public BellPatternRackUnit()
	{
		InsetPanel   insetPanel;
		setName("Ring Mod Bell");
		try
		{
			unitNote = bell = new RingModBell();
			unitNote.amplitude.set( 0.5 );
			bell.halfLife.set( 0.02 );
		} catch( SynthException e) {
			System.err.println("Error in BellPatternRackUnit" + e);
			return;
		}
		numRackUnits = 3;
		setLayout( new BorderLayout() );
/* Make a panel with an array of checkboxes for setting rhythm. */
		cbPanel = new CheckboxPanel( NUM_BOXES );
		add( "North", insetPanel = new InsetPanel( cbPanel, Color.green ) );
		cbPanel.getNthBox(0).setState( true );  // turn on first box
		cbPanel.getNthBox(NUM_BOXES/2).setState( true );  // turn on middle box
		
/* Setup Frequency BarGraphEditor */
		freqData = new double[ NUM_BOXES ];
		for( int i=0; i<freqData.length; i++ ) freqData[i] = 1120.0;
		freqEditor = new BarGraphEditor( freqData );
		add( "Center", insetPanel = new InsetPanel( freqEditor, Color.yellow ) );
		freqEditor.setBackground( Color.white );
		freqEditor.setMinWorldY( 100.0 );
		freqEditor.setMaxWorldY( 4000.0 );
		
		add( "South", new PortFader( bell.modIndex, 0.26, 0.0, 1.0 ) );
	}
	
/** Trigger if the corresponding checkbox is turned on.
 *  Trigger two notes for double tempo.
 */
	public void beat( int time, int duration, int beatIndex )
	throws SynthException
	{
		int boxIndex = (beatIndex*BOXES_PER_BEAT) % NUM_BOXES;
		
		freqEditor.highlight( boxIndex );
		cbPanel.highlight( boxIndex );
		for( int i=0; i < BOXES_PER_BEAT; i++ )
		{
			if( cbPanel.getNthBox( boxIndex ).getState() )
			{
				bell.setStage( time, 0 );
				bell.frequency.set( time, freqData[boxIndex] );
			}
			time += duration / BOXES_PER_BEAT;
			boxIndex++;
		}
	}
}
/* ========================================================================== */
class NoiseContourRackUnit extends SimpleRackUnit
{
	static final int NUM_BOXES = 32;
	static final int BOXES_PER_BEAT = 4;
	Panel            panel;
	LagFilteredNoise lagNoise;
	BarGraphEditor    ampEditor;
	double           ampData[];
	BarGraphEditor    freqEditor;
	double           freqData[];
	
	public NoiseContourRackUnit()
	{
		setName("Filtered Noise");
		InsetPanel insetPanel;
		try
		{
			unitNote = lagNoise = new LagFilteredNoise();
			unitNote.amplitude.set( 0.3 );
			lagNoise.resonance.set( 0.02 );
		} catch( SynthException e) {
			System.err.println("Error in NoiseContourRackUnit" + e);
			return;
		}
		numRackUnits = 3;
		setLayout( new GridLayout(0,1) );
		
/* Setup Amplitude BarGraphEditor */
		ampData = new double[ NUM_BOXES ];
		ampEditor = new BarGraphEditor( ampData );
		ampEditor.setBackground( Color.white );
		add( insetPanel = new InsetPanel( ampEditor, Color.green ) );
		
/* Setup Frequency BarGraphEditor */
		freqData = new double[ NUM_BOXES ];
		for( int i=0; i<freqData.length; i++ ) freqData[i] = 400.0;
		freqEditor = new BarGraphEditor( freqData );
		freqEditor.setBackground( Color.white );
		add( insetPanel = new InsetPanel( freqEditor, Color.yellow ) );
		freqEditor.setMinWorldY( 60.0 );
		freqEditor.setMaxWorldY( 1400.0 );
/* Initial sound. */
		ampData[NUM_BOXES/4] = 0.8;
		freqData[NUM_BOXES/4] = 1000.0;
		
	}
	
/** Set noise target to contour data value.
 */
	public void beat( int time, int duration, int beatIndex )
	throws SynthException
	{
		double lagTime = duration / (BOXES_PER_BEAT * Synth.getTickRate()); // calc seconds
		lagNoise.amplitudeTime.set( lagTime );
		lagNoise.frequencyTime.set( lagTime );
		
		int boxIndex = (beatIndex * BOXES_PER_BEAT) % NUM_BOXES;
		ampEditor.highlight( boxIndex );
		freqEditor.highlight( boxIndex );
		
		for( int i=0; i < BOXES_PER_BEAT; i++ )
		{
			lagNoise.amplitudeTarget.set( time, ampData[i + boxIndex] );
			lagNoise.frequency.set( time, freqData[i + boxIndex] );
			time += duration / BOXES_PER_BEAT;
		}
	}
}
/* ========================================================================== */
/**
 * Generate a sequence of integers based on a recursive mining of
 * previous material.
 * Notes are generated by one of the following formula:
 *
 *   value[n] = value[n-delay] + offset;
 *
 * The parameters length and transpose are randomly generated.
*/
class RecursiveSequence
{
	int    maxDelay;
	int    delay = 1;
	int    maxValue;
	int    maxInterval;
	int    offset = 0;
	int    values[];
	boolean enables[];
	int    cursor = 0;
	int    countdown = -1;
	double actualDensity = 0.0;
	double desiredDensity = 0.5;
	int    beatsPerMeasure = 8;
	
	public RecursiveSequence()
	{
		this( 25, 7, 64 );
	}
	public RecursiveSequence( int maxValue, int maxInterval, int arraySize )
	{
		maxDelay = arraySize/2;
		values = new int[arraySize];
		enables = new boolean[arraySize];
		this.maxValue = maxValue;
		this.maxInterval = maxInterval;
		for( int i=0; i<values.length; i++ )
		{
			values[i] = maxValue/2;
			enables[i] = isNextEnabled( false );
		}
	}
	
/** Set density of notes. 0.0 to 1.0 */
	void setDensity( double density )
	{
		desiredDensity = density;
	}
	double getDensity()
	{
		return desiredDensity;
	}
	
/** Set maximum for generated value. */
	void setMaxValue( int maxValue )
	{
		this.maxValue = maxValue;
	}
	int getMaxValue()
	{
		return maxValue;
	}
/** Set maximum for generated value. */
	void setMaxInterval( int maxInterval )
	{
		this.maxInterval = maxInterval;
	}
	int getMaxInterval()
	{
		return maxInterval;
	}
	
/* Determine whether next in sequence should occur. */
	boolean isNextEnabled( boolean preferance )
	{
/* Calculate note density using low pass IIR filter. */
		double newDensity = (actualDensity * 0.9) + (preferance ? 0.1 : 0.0);
/* Invert enable to push density towards desired level, with hysteresis. */
		if( preferance && (newDensity > ((desiredDensity*0.7) + 0.3)) ) preferance = false;
		else if( !preferance && (newDensity < (desiredDensity*0.7)) ) preferance = true;
		actualDensity = (actualDensity * 0.9) + (preferance ? 0.1 : 0.0);
		return preferance;
	}
	
	int randomPowerOf2( int maxExp )
	{
/* Generate values that are powers of 2. */
		return (1 << (int)(Math.random() * (maxExp+1)));
	}
	
	int randomEvenInterval()
	{
/* Random number evenly distributed from -maxInterval to +maxInterval */
		 return (int)(Math.random() * ((maxInterval * 2) + 1)) - maxInterval;
	}
	
	void calcNewOffset()
	{
		offset = randomEvenInterval();
	}
	void randomize()
	{
		
		delay = randomPowerOf2(4);
		calcNewOffset();
//		System.out.println("NewSeq: delay = " + delay + ", offset = " + offset );
	}
	
/* Change parameters based on randomcountdown. */
	int next()
	{
// If this sequence is finished, start a new one.
		if( countdown-- < 0 )
		{
			randomize();
			countdown = randomPowerOf2(3);
		}
		return nextValue();
	}
	
/* Change parameters using a probability based on beatIndex. */
	int next( int beatIndex )
	{
		int beatMod = beatIndex % beatsPerMeasure;
		switch( beatMod )
		{
		case 0:
			if( Math.random() < 0.90) randomize();
			break;
		case 2:
		case 6:
			if( Math.random() < 0.15) randomize();
			break;
		case 4:
			if( Math.random() < 0.30) randomize();
			break;
		default:
			if( Math.random() < 0.07) randomize();
			break;
		}
		return nextValue();
	}
	
/* Generate nextValue based on current delay and offset */
	int nextValue()
	{
// Generate index into circular value buffer.
		int idx = (cursor - delay);
		if( idx < 0 ) idx += values.length;
		
// Generate new value. Calc new offset if too high or low.
		int nextVal = 0;
		int timeout = 100;
		while( timeout > 0 )
		{
			nextVal = values[idx] + offset;
			if( (nextVal >= 0) && (nextVal < maxValue ) ) break;
// Prevent endless loops when maxValue changes.
			if( nextVal > (maxValue + maxInterval - 1) )
			{
				nextVal = maxValue;
				break;
			}
			calcNewOffset();
			timeout--;
//			System.out.println("NextVal = " + nextVal + ", offset = " + offset );
		}
		if( timeout <= 0 )
		{
			System.err.println("RecursiveSequence: nextValue timed out. offset = " + offset );
			nextVal = maxValue/2;
			offset = 0;
		}
		
// Save new value in circular buffer.
		values[cursor] = nextVal;
		
		boolean playIt = enables[cursor] = isNextEnabled( enables[idx] );
		cursor++;
		if( cursor >= values.length ) cursor = 0;
		
//		System.out.println("nextVal = " + nextVal );
		
		return playIt ? nextVal : -1 ;
	}
}
/* ========================================================================== */
class RecursiveMelodyRackUnit extends SimpleRackUnit
{
	Panel            panel;
	FilteredSawtoothBL      sawIns;
	BarGraphEditor    freqEditor;
	double           freqData[];
	CustomFader        densityBar;
	CustomFader        stretchBar;
	RecursiveSequence rSeq;
	EqualTemperedTuning  NTET;
	Choice           ntetChoice;
	Label            ntetLabel;
	Button           ntetButton;
	static double ampls[] = { 0.4, 0.2, 0.3, 0.2 };
	
	public RecursiveMelodyRackUnit()
	{
		setName("Recursive Melody");
		InsetPanel   insetPanel;
		
		try
		{
			unitNote = sawIns = new FilteredSawtoothBL();
			unitNote.amplitude.set( 0.5 );
		} catch( SynthException e) {
			System.err.println("Error in RecursiveMelodyRackUnit" + e);
			return;
		}
		
		rSeq = new RecursiveSequence( 7*3, 5, 20 );
		NTET = new EqualTemperedTuning( 200.0 );
		numRackUnits = 1;
		setLayout( new GridLayout(0,2) );
		
		Panel densityPanel = new Panel();
		add( densityPanel );
		densityPanel.setLayout( new GridLayout(0,2) );
		densityPanel.add( new Label("Note Density ", Label.RIGHT) );
		densityPanel.add( densityBar = new CustomFader(CustomFader.HORIZONTAL, 75, 1, 0, 100) );
		
// Add GUI to select N tone equal temperament.
		Panel ntetPanel = new Panel();
		add( ntetPanel );
		ntetPanel.add( ntetChoice = new Choice() );
		ntetChoice.addItem( Integer.toString(5) );
		ntetChoice.addItem( Integer.toString(7) );
		ntetChoice.addItem( Integer.toString(9) );
		ntetChoice.addItem( Integer.toString(12) );
		ntetChoice.addItem( Integer.toString(17) );
		ntetChoice.addItem( Integer.toString(19) );
		ntetChoice.addItem( Integer.toString(24) );
		ntetChoice.addItem( Integer.toString(31) );
		ntetPanel.add( ntetButton = new Button("Set Notes/Octave") );
		ntetPanel.add( ntetLabel = new Label() );
		setEqualTemper( 7 );
		
		Panel stretchPanel = new Panel();
		add( stretchPanel );
		stretchPanel.setLayout( new GridLayout(0,2) );
		stretchPanel.add( new Label("Max Interval ", Label.RIGHT) );
		stretchPanel.add( stretchBar = new CustomFader(CustomFader.HORIZONTAL, 7, 1, 1, 20) );
	}
	
	void setEqualTemper( int N )
	{
		NTET.setNotesPerOctave( N );
		rSeq.setMaxValue( N*3 ); // allow 3 octave range
		ntetLabel.setText( Integer.toString( N ) + " Notes/Octave" );
	}
	
	public void beat( int time, int duration, int beatIndex ) throws SynthException
	{
		rSeq.setDensity( densityBar.getValue() / 100.0 );
		rSeq.setMaxInterval( stretchBar.getValue() );
		int pitch = rSeq.next( beatIndex );
		if( pitch >= 0 )
		{
			double amplitude = ampls[ (beatIndex % ampls.length) ];
			sawIns.note( time, duration, NTET.getFrequency( pitch ), amplitude );
		}
	}
	
	public boolean action(Event evt, Object what)
	{
		if( evt.target == ntetButton )
		{
			int n = Integer.parseInt( ntetChoice.getSelectedItem() );
			setEqualTemper( n );
			return true;
		}
		return false;
    }
}
/* ========================================================================== */
class RackUnitMixControl extends Panel implements CustomFaderListener
{
	SynthMixer       mixer;
	int              track;
	CustomFader        gainFader;
	CustomFader        panFader;
	CustomFader        sendFader;
	double           gain, pan, send;
	public RackUnitMixControl( String title, SynthMixer mixer, int track )
	{
		this.mixer = mixer;
		this.track = track;
		setLayout( new GridLayout( 1, 0 ) );
		add( new Label( title, Label.RIGHT ) );
		add( gainFader = new CustomFader( CustomFader.HORIZONTAL, 100, 1, 0, 100) );
		gainFader.addCustomFaderListener( this );
		add( panFader = new CustomFader( CustomFader.HORIZONTAL, 50, 1, 0, 100) );
		panFader.addCustomFaderListener( this );
		add( sendFader = new CustomFader( CustomFader.HORIZONTAL, 0, 1, 0, 100) );
		sendFader.addCustomFaderListener( this );
	}
	
	void updateGains()
	{
		try
		{
			mixer.setGain( track, 0, gain*(1.0 - pan) );
			mixer.setGain( track, 1, gain*pan );
			mixer.setGain( track, 2, gain*send );
		} catch( SynthException e )
		{
			SynthAlert.showError(this,e);
			return;
		}
			
	}
/** Set volume level 0.0 to 1.0 */
	public void setGain( double gain )
	{
		this.gain = gain;
		gainFader.setValue( (int)(gain*100.0));
		updateGains();
	}
	public double getGain() { return gain; }
	
/** Set left/right pan, L = 0.0, R = 1.0 */
	public void setPan( double pan )
	{
		this.pan = pan;
		panFader.setValue( (int)(pan*100.0));
		updateGains();
	}
	public double getPan() { return pan; }
/** Set effects send. 0.0 to 1.0 */
	public void setAuxSend( double send )
	{
		this.send = send;
		sendFader.setValue( (int)(send*100.0));
		updateGains();
	}
	public double getAuxSend() { return pan; }

/** Any class listening to a CustomFader must implement CustomFaderListener by defining this method */
    public void customFaderValueChanged(Object fdr, int value)
	{
		CustomFader fader = (CustomFader) fdr;
		if( fader == gainFader )
		{
			gain = gainFader.getValue() / 100.0;
			updateGains();
		}
		else if( fader == panFader )
		{
			pan = panFader.getValue() / 100.0;
			updateGains();
		}
		else if( fader == sendFader )
		{
			send = sendFader.getValue() / 100.0;
			updateGains();
		}
    }
}
/* ========================================================================== */
public class PerformanceRack extends Applet implements Runnable, Tweakable
{
	Vector            bandMembers;
	LineOut           unitOut;
	MultiTapDelay     myReverb;
	SynthMixer        mixer;
	int               duration;
	SynthScope        scope;
	Dialog            scopeDialog;
	static final int  TWEAK_INDEX_TEMPO = 0;
	GridBagConstraints  constraint;
	GridBagLayout     gridbag;
	Panel             mixPanel;
	static int        numStarts = 0;
	Object            semaphore = new Object();
	boolean           keepRunning = false;
	Thread            mainThread = null;
	
/* Can be run as either an application or as an applet. */
	public static void main(String args[])
	{
		PerformanceRack applet = new PerformanceRack();
		AppletFrame frame = new AppletFrame("PerformanceRack", applet);
		frame.resize(600,550);
		frame.show();
/* Begin test after frame opened so that DirectSound will use Java window. */
		frame.test();
	}
	void makeTopControlPanel()
	{
		Panel topPanel = new Panel();
		constraint.weighty  =  0;  
		gridbag.setConstraints(topPanel,  constraint);
		add( topPanel );
//		topPanel.setLayout( new BorderLayout() );
/* make big JSyn label */
		Label aLabel;
		topPanel.add( aLabel = new Label("JSyn Rack #1"));
		aLabel.setForeground( Color.red );
/* Make whatever font we have bigger. */
		Font smallFont = aLabel.getFont();
		Font bigFont = new Font( smallFont.getName(), Font.BOLD, smallFont.getSize()*2 );
		if( bigFont != null ) aLabel.setFont( bigFont );
		
		LabelledFader tempoFader;
		topPanel.add( tempoFader = new LabelledFader( this, TWEAK_INDEX_TEMPO, "Tempo",
			120.0, 60.0, 300.0 ));
		tempoFader.getFader().resize(100,30);
		topPanel.add(  "East" , new UsageDisplay() );
	}
	
/* Set up multi-track mixer control panel. */
	void makeMixControlPanel()
	{
		mixPanel = new Panel();
		mixPanel.setLayout( new GridLayout( 0, 1 ) );
		Panel mixLabelPanel = new Panel();
		mixPanel.add( mixLabelPanel );
		mixLabelPanel.setLayout( new GridLayout( 1, 0 ) );
		mixLabelPanel.add( new Label("RackUnit", Label.CENTER) );
		mixLabelPanel.add( new Label("Volume", Label.CENTER) );
		mixLabelPanel.add( new Label("Left/Right Pan", Label.CENTER) );
		mixLabelPanel.add( new Label("Echo Send", Label.CENTER) );
	}
	
/*
 * Start applet.
 */
	public void start()
	{	
		startSynth();
		mainThread = new Thread( this );
		keepRunning = true;
		mainThread.start();
	}
	
/* ------------------------------- */
	public void stop()
	{
		keepRunning = false;
		if( mainThread != null )
		{
			mainThread.interrupt();
			try
			{
				mainThread.join( 1000 );
			} catch( InterruptedException e ) { }
		}
		stopSynth();
	}
/*
 * Setup synthesis.
 */
	public void startSynth() throws SynthException
	{	
/* Start synthesis engine. */
		Synth.startEngine( 0 );
		
		duration = (int) (Synth.getTickRate() * (60.0/120.0)); /* 120 beats per minute */
/* Set level of tracing. */
		Synth.verbosity = Synth.SILENT;
/* Use GridBagLayout to get reasonable sized components. */
        gridbag  =  new  GridBagLayout();
        constraint  =  new  GridBagConstraints();
        setLayout(gridbag);
        constraint.gridwidth  =  GridBagConstraints.REMAINDER; 
        constraint.fill  =  GridBagConstraints.BOTH;
        constraint.weightx  =  1.0;
		
/* Add main rack control panel. */
		makeTopControlPanel();
		makeMixControlPanel();
		
		bandMembers = new Vector();     /* Add more SimpleRackUnits HERE!!! */
		bandMembers.addElement( new BellPatternRackUnit() );
		bandMembers.addElement( new NoiseContourRackUnit() );
		bandMembers.addElement( new RecursiveMelodyRackUnit() );
		initBand();
		
/* Put mixer at bottom of Applet with border. */
		InsetPanel insetPanel = new InsetPanel( mixPanel, 10 );
		add( insetPanel );
		constraint.weighty  =  0.0;  
		constraint.gridheight  =  GridBagConstraints.REMAINDER;
		gridbag.setConstraints(insetPanel,  constraint);
		
/* Create an oscilloscope to show wind output. */
		if( false )
		{
			scope = new SynthScope();
			scope.createProbe( myReverb.output, "Effect", Color.yellow );
			scope.finish();

			scopeDialog = new Dialog( (Frame) getParent(), "JSyn Scope", false );
			scopeDialog.add( "Center", scope );
			scopeDialog.reshape( 200,100, 500,400 );
			scopeDialog.show();
		}
/* Synchronize Java display. */
		getParent().validate();
		getToolkit().sync();
	}
/* ------------------------------- */
	void initBand()	throws SynthException
	{
		RackUnitMixControl mixTrack;
		InsetPanel insetPanel;
		int numBandMembers = bandMembers.size();
		mixer = new SynthMixer( numBandMembers+1, 3 );
		mixer.start();
		for( int i=0; i<numBandMembers; i++ )
		{
			RackablePerformance riff = (RackablePerformance) bandMembers.elementAt( i );
/* Connect riff to mixer. */
			mixer.connectInput( i, riff.getOutput(), 0 );
			mixPanel.add( mixTrack = new RackUnitMixControl( riff.getName(), mixer, i ) );
			
/* Set left and right gain so as to avoid clipping. */
			mixTrack.setGain( 1.0/bandMembers.size() );
			mixTrack.setPan( 0.5 );
			mixTrack.setAuxSend( 0.3 );
			
/* Start riff playing. */
			riff.start( Synth.getTickCount() );
/* add user interface to Applet inside InsetPanel */
			insetPanel = new InsetPanel( riff.getUserInterface(), 10 );
/* Alternate border colors. */
			insetPanel.setBackground( (((i&1)==0) ? Color.lightGray : Color.gray) );
			add( insetPanel );
			constraint.weighty  =  (double) riff.getNumRackUnits();
System.out.println("units = " + riff.getNumRackUnits() );
			gridbag.setConstraints( insetPanel,  constraint );
		}
/* Connect mixer to output. */
		unitOut = new LineOut( );
		mixer.connectOutput( 0, unitOut.input, 0 );
		mixer.connectOutput( 1, unitOut.input, 1 );
		
/* Connect "reverb" */
		double[] delays = { 0.457, 0.719, 0.901, 1.0, 1.17 };
		double[] gains = {0.1, -0.3, -0.2, 0.1, 0.3};
		myReverb = new MultiTapDelay( delays, gains );
		mixer.connectOutput( 2, myReverb.input, 0 );
		mixer.connectInput( numBandMembers, myReverb.output, 0 );
		mixPanel.add( mixTrack = new RackUnitMixControl( "Echo Return", mixer, numBandMembers ) );
			
/* Set reverb level so as to avoid clipping. */
		mixTrack.setGain( 1.0/numBandMembers );
		mixTrack.setPan( 0.5 );
		mixTrack.setAuxSend( 0.0 );
		myReverb.feedback.set( 0.0 ); // control feedback with mixer
		
		myReverb.start();
		unitOut.start();
	}
/* ------------------------------- */
	public void stopSynth()  throws SynthException
	{
		System.out.println("stopSynth() cleaning up.");
		stopBand();
		if( scopeDialog != null )
		{
			scopeDialog.hide();
		}
		removeAll(); // remove portFaders
/* Turn off tracing. */
		Synth.verbosity = Synth.SILENT;
/* Stop synthesis engine. */
		Synth.stopEngine();
	}
/* ------------------------------- */
	void stopBand()	throws SynthException
	{
		if( mixer != null ) mixer.stop();
		if( bandMembers != null )
		{
			for( int i=0; i<bandMembers.size(); i++ )
			{
				RackablePerformance riff = (RackablePerformance) bandMembers.elementAt( i );
				riff.stop( Synth.getTickCount() );
			}
		}
		if( unitOut != null ) unitOut.stop();
		if( myReverb != null ) myReverb.stop();
	}
/* ------------------------------- */
	public void run()
	{
		System.out.println("run() - begin currentThread = " + Thread.currentThread() );
		int beatIndex = 0;
		try
		{
/* Calculate number of ticks needed to play some number of seconds ahead. */
			int advance = (int) (Synth.getTickRate() * 0.2);
			int now = Synth.getTickCount();
			while( keepRunning )
			{
				beat( now  + advance, duration, beatIndex );
				now += duration;
				beatIndex++;
				Synth.sleepUntilTick( now );
			}
			
		} catch (SynthException e) {
			System.out.println("run caught " + e );
		}
		System.out.println("run() - end currentThread = " + Thread.currentThread() );
	}
	
/* ------------------------------- */
	public void beat( int now, int duration, int beatIndex )
	throws SynthException
	{
		for( int i=0; i<bandMembers.size(); i++ )
		{
			RackablePerformance riff = (RackablePerformance) bandMembers.elementAt( i );
			riff.beat( now, duration, beatIndex );
		}
	}
	
/* Called by LabelledFader. */
	public void tweak( int index, double val )
	{
		switch( index )
		{
		case TWEAK_INDEX_TEMPO:
			duration = (int) (Synth.getTickRate() * (60.0/val)); /* val is beats per minute */
			break;
		}
	}
}
	