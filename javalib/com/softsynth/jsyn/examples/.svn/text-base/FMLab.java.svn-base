/*
 * FM Laboratory
 *
 * @author (C) 2000 Phil Burk, All Rights Reserved
 */
package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.circuits.FMOperator;
import com.softsynth.jsyn.view11x.EditListener;
import com.softsynth.jsyn.view11x.EnvelopeEditor;
import com.softsynth.jsyn.view11x.UsageDisplay;
import com.softsynth.tools.jsyn.DecibelPortKnob;
import com.softsynth.tools.jsyn.PortKnob;
import com.softsynth.tools.view.RotaryKnob;
import com.softsynth.util.NumericOutput;

/** Editor for an FM Operator
 */
class OperatorEditor extends Panel implements EditListener
{
	Checkbox                 hearBox;
	Checkbox                 sustainBox;
	Checkbox                 sourceBoxes[];
	Checkbox                 fixedBox;
// knobs used to control main parameters
	public PortKnob          scaleKnob;
	public PortKnob          depthKnob;
	public PortKnob          indexKnob;
	public DecibelPortKnob   amplitudeKnob;
	Panel                    knobPanel;
	FMOperator               operator;
	AddUnit                  adders[]; // used to mix modulation sources
	SynthOutput              output;
	SynthEnvelope            envelope;
	SynthEnvelope            resetEnvelope; // used to restore default levels
	public static final int  SUSTAIN_END = 2;

	EnvelopeEditor           envEditor;
	EnvelopePoints           points;
	final int                MAX_FRAMES = 16;
	
	FMLab                    myLab;
	int                      index;
	int                      sourceIndex = -1;
	boolean                  connected[];
	
	public OperatorEditor( FMLab lab, int index )
	{
		myLab = lab;
		this.index = index;
		
	
		operator = new FMOperator();
	// make envelope used to reset default values
		envelope = new SynthEnvelope( MAX_FRAMES );
		double data[] = { 0.010, 0.0 };
		resetEnvelope = new SynthEnvelope( data );
		operator.setEnvelope( envelope );
		operator.start();
		output = operator.output;
		
// create adders for mixing modulation sources
		connected = new boolean[lab.NUM_OPERATORS];
		adders = new AddUnit[lab.NUM_OPERATORS - 1];
		for( int i=0; i<adders.length; i++ )
		{
			adders[i] = new AddUnit();
			adders[i].start();
			if( i > 0 ) adders[i-1].output.connect( adders[i].inputA );
		}
		adders[adders.length - 1].output.connect( operator.input );
		
// create GUI
		setLayout( new BorderLayout() );
		knobPanel = new Panel();
		add( knobPanel );
		
		knobPanel.setLayout( new GridLayout( 1, 0 ) );
		knobPanel.add( setupChecks( index ) );
		knobPanel.add( setupEnvEditor() );
		
		knobPanel.add( scaleKnob = new FMPortKnob( operator.scale ) );
		scaleKnob.getKnob().setTaper( RotaryKnob.EXPONENTIAL );
		
		knobPanel.add( depthKnob = new FMPortKnob( operator.depth ) );
		depthKnob.getKnob().setTaper( RotaryKnob.EXPONENTIAL );
		
		indexKnob = new FMPortKnob( operator.index );
		
		amplitudeKnob = new DecibelPortKnob( operator.amplitude )
			{
				public void actionPerformed( ActionEvent e)
				{
					super.actionPerformed( e );
					getKnob().requestFocus();
				}
			};
		amplitudeKnob.getKnob().addKeyListener( myLab );
		
		reset();
	}
	
/** Extend knob so that when the USER hits <ENTER> in the TextField, the focus
 * will shift to the rotary knob so the user can play the keyboard.
 */
	class FMPortKnob extends PortKnob
	{
		public FMPortKnob( SynthVariable port )
		{
			super( port );
			getKnob().addKeyListener( myLab );
		}
		
		public void actionPerformed( ActionEvent e)
		{
			super.actionPerformed( e );
			getKnob().requestFocus();
		}
	}
	
/** Connect or disconnect a source of modulation.
 */
	public void setSource( int idx, boolean used )
	{
		SynthInput port;
		if( idx == 0 )
		{
			port = adders[0].inputA;
		}
		else
		{
			port = adders[idx-1].inputB;
		}
		if( used )
		{
			myLab.getNthEditor( idx ).output.connect( port );
		}
		else
		{
			if( connected[idx] ) port.disconnect();
		}
		sourceBoxes[idx].setState( used );
		connected[idx] = used;
	}
	
/** Set whether this operator is used as a Carrier.
 */
	public void setHeard( boolean heard )
	{
		if( heard )
		{
			myLab.setOperatorMix( index, 0.2 );
			indexKnob.getKnob().setValue( 0 );
// replace index knob with amplitude knob
			knobPanel.remove( indexKnob );
			knobPanel.add( amplitudeKnob );
		}
		else
		{
			myLab.setOperatorMix( index, 0.0 );
			amplitudeKnob.getKnob().setValue( 0 );
			knobPanel.remove( amplitudeKnob );
			knobPanel.add( indexKnob );
		}
		knobPanel.validate();
		hearBox.setState( heard );
	}
	
	public void setSustainIndex( int index )
	{
		envelope.setSustainLoop( index, index );
		sustainBox.setState( index >= 0 );
	}
	
/** Set frequency control to be tied to keyboard input, or a fixed frequency.
 * Use different label, range and taper for each.
 */
	public void setFixed( boolean fixed )
	{
		RotaryKnob knob = scaleKnob.getKnob();
		if( fixed )
		{
			scaleKnob.setText( "frequency" );
			knob.setMaximum( 4000.0 );
			knob.setMinimum( 0.01 );
			knob.setTaper( knob.EXPONENTIAL );
		}
		else
		{
			scaleKnob.setText( "freqMult" );
			knob.setMaximum( 30.0 );
			knob.setMinimum( 0.00 );
			knob.setTaper( knob.LINEAR );
		}
		fixedBox.setState( fixed );
	}
	
	Panel setupChecks( int index )
	{
		Panel checkPanel = new Panel();
		
		Label aLabel;
		checkPanel.add( aLabel = new Label("Op#" + index) );
		aLabel.setForeground( Color.red );
/* Make whatever font we have bigger. */
		Font smallFont = aLabel.getFont();
		if( smallFont != null )
		{
			Font bigFont = new Font( smallFont.getName(),
						Font.BOLD, smallFont.getSize()*2 );
			if( bigFont != null ) aLabel.setFont( bigFont );
		}

		sourceBoxes = new Checkbox[myLab.NUM_OPERATORS];
		for( int i=0; i<myLab.NUM_OPERATORS; i++ )
		{
			sourceBoxes[i] = new Checkbox( Integer.toString( i ), false );
			sourceBoxes[i].addKeyListener( myLab );
			sourceBoxes[i].addItemListener(	new ItemListener() {
				public void itemStateChanged(ItemEvent e)
				{
					String s = (String) e.getItem();
					int idx = Integer.parseInt( s );
					setSource( idx, sourceBoxes[idx].getState() );
				} } );
			checkPanel.add( sourceBoxes[i] );
		}
		
		hearBox = new Checkbox( "Hear", false );
		hearBox.addKeyListener( myLab );
		hearBox.addItemListener(	new ItemListener() {
			public void itemStateChanged(ItemEvent e)
			{
				setHeard( hearBox.getState() );
			} } );
		checkPanel.add( hearBox );
		
		sustainBox = new Checkbox( "Sust", false );
		sustainBox.addKeyListener( myLab );
		sustainBox.addItemListener(	new ItemListener() {
			public void itemStateChanged(ItemEvent e)
			{
				setSustainIndex( sustainBox.getState() ? SUSTAIN_END : -1 );
			} } );
		checkPanel.add( sustainBox );

		fixedBox = new Checkbox( "Fixed", false );
		fixedBox.addKeyListener( myLab );
		fixedBox.addItemListener(	new ItemListener() {
			public void itemStateChanged(ItemEvent e)
			{
				setFixed( fixedBox.getState() );
			} } );
		checkPanel.add( fixedBox );

		return checkPanel;
	}
	
	EnvelopeEditor setupEnvEditor()
	{
	// Add an envelope editor to the center of the panel.
		envEditor = new EnvelopeEditor();
		envEditor.addKeyListener( myLab );
		envEditor.setBackground( Color.cyan.brighter() );
		envEditor.setVerticalBarsEnabled( true );
		envEditor.setVerticalBarSpacing( 0.2 );
		envEditor.setMaximumXRange( 20.0 );
		
	// Ask editor to inform us when envelope modified.
		envEditor.addEditListener( this );
		
	// Create vector of points for editor.
		points = new EnvelopePoints();
		points.setName( "amplitude" );
		
	// Tell editor to use these points.
		envEditor.setPoints( points ); 
		envEditor.setMaxPoints( MAX_FRAMES );
		
	// Setup initial envelope shape.
		double dar[] = {
			0.1, 1.0,
			0.2, 0.5,
			0.5, 0.5,
			0.5, 0.0
		};
		setContour( dar );
		
		return envEditor;
	}
	
/** Write this double array to the SynthEnvelope and the EnvelopeEditor
 */
	public void setContour( double dar[] )
	{
		points.removeAllElements();
		for( int i=0; i<(dar.length/2); i++ )
		{
			points.add( dar[i*2], dar[(i*2)+1] );
		}
		updateEnvelope();
		envEditor.repaint();
	}
	
/** This is called by the EnvelopeEditor when the envelope is modified.
 * If we are playing a loop, update envelope and requeue in case length changed. */
	public void objectEdited( Object editor, Object objPoints )
	{
		updateEnvelope();
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
	// FIXME - just setNumFrames() in V14.2
		double lastVal = 0.0;
		if( numFrames > 0 )
		{
			lastVal =  points.getPoint( numFrames-1 )[1];
		}
			
		double filler[] = { 0.0, lastVal };
		for( int i=numFrames; i<envelope.getNumFrames(); i++ )
		{
			envelope.write( i, filler, 0, 1 );
		}
	}

	public void noteOn( double frequency )
	{
		// System.out.println("frequency = " + frequency);
		operator.frequency.set( fixedBox.getState() ? 1.0 : frequency );
		operator.setStage( Synth.getTickCount(), 0 );
	}
	public void noteOff()
	{
		operator.setStage( Synth.getTickCount(), 1 );
	}
	
	public void reset()
	{
		
		if( resetEnvelope != null )
		{
			operator.envelopePort.queue( resetEnvelope );
		}
		setSustainIndex(-1);
		setFixed( false );
		for( int i=0; i<connected.length; i++ )
		{
			setSource( i, false );
		}
	}

	public void dumpSource( PrintStream ps, int idx )
	{
		for( int i=0; i<myLab.NUM_OPERATORS; i++ )
		{
			if( sourceBoxes[i].getState() )
			{
				ps.println("	editor.setSource( " + i + ", true );" );
			}
		}
		ps.println("	editor.setHeard( " + hearBox.getState() + " );" );
		ps.println("	editor.setFixed( " + fixedBox.getState() + " );" );
		ps.println("	editor.setSustainIndex( " +
				   envelope.getSustainBegin() + " );" );
		ps.println("	editor.scaleKnob.setValue( " + scaleKnob.getKnob().getValue() + " );" );
		ps.println("	editor.depthKnob.setValue( " + depthKnob.getKnob().getValue() + " );" );
		ps.println("	editor.indexKnob.setValue( " + indexKnob.getKnob().getValue() + " );" );
		ps.println("	editor.amplitudeKnob.setValue( " + amplitudeKnob.getKnob().getValue() + " );" );
	
		ps.println("	double dar" + idx + "[] = {" );
		int numFrames = points.size();
		for( int i=0; i<numFrames; i++ )
		{
			double dar[] = points.getPoint( i );
			ps.print( "		" + NumericOutput.doubleToString( dar[0], 1, 4 ) +
					  ", " + NumericOutput.doubleToString( dar[1], 1, 3 ) );
			if( i<(numFrames-1) ) ps.println( "," );
		}
		ps.println(" };" );
		ps.println("	editor.setContour( dar" + idx + ");" );
	}
}

/******************************************************************
 * Interactive Applet for editing FM sounds.
 */
public class FMLab extends Applet implements KeyListener
{
	public static final int  NUM_OPERATORS = 4;
	OperatorEditor    editors[];
	OperatorEditor    currentEditor;
	LineOut           lineOut;
	SynthMixer        mixer;
	Button            keyButton;
	Button            dumpButton;
	Button            showButton;
	int               lastNoteKey = (int)'a';
	Checkbox          onBox;
	static String keyboard = "zxcvbnmasdfghjqwertyu1234567";  /* define music keyboard layout */
	Dialog         dialog;

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		FMLab applet = new FMLab();
		AppletFrame frame = new AppletFrame("Simple FMLab", applet);
		frame.resize(800,600);
		frame.show();
/* Begin test after frame opened so that DirectSound will use Java window. */
		frame.test();
	}
/*
 * Setup synthesis.
 */
	public void start()
	{	
		
		dialog = new Dialog( SynthAlert.getFrame( this ), "FM Lab" );
		dialog.reshape( 200,100, 800,600 );
		// dialog.setSize( 800,600 );
		dialog.setLayout( new BorderLayout() );
		
		try
		{
/* Start synthesis engine. */
		Synth.startEngine( 0 );

/* Set level of tracing. */
		Synth.setTrace( Synth.SILENT );

/* Create circuits and unit generators. */
		mixer = new SynthMixer( NUM_OPERATORS, 1 );
		editors = new OperatorEditor[NUM_OPERATORS];
		Panel opPanel = new Panel();
		opPanel.setLayout( new GridLayout( 0, 1 ) );
		dialog.add( "Center", opPanel );
		for( int i=0; i<NUM_OPERATORS; i++ )
		{
			com.softsynth.util.InsetPanel iPanel =
				new com.softsynth.util.InsetPanel( editors[i] =
					new OperatorEditor( this, i ), Color.blue, 2 );
			mixer.connectInput( i, editors[i].output, 0 );
			mixer.setGain( i, 0, 0.0 );
			opPanel.add( iPanel );
		}
		lineOut = new LineOut();
	
/* Connect voice1 to output. */
		mixer.connectOutput( 0, lineOut.input, 0 );
		mixer.connectOutput( 0, lineOut.input, 1 );

/* Start execution of units. */
		lineOut.start();
		mixer.start();


/* Setup GUI */
		Panel controlPanel = new Panel();
		
		dialog.add( "South", controlPanel );
		controlPanel.add( keyButton = new Button("Play Keys") );
		keyButton.addKeyListener( this );
   		keyButton.addActionListener(	new ActionListener() {
   				public void actionPerformed(ActionEvent e)
   				{ keyButton.requestFocus();	} } );
		
		controlPanel.add( onBox = new Checkbox("NoteOn") );
		onBox.addKeyListener( this );
		onBox.addItemListener(	new ItemListener() {
			public void itemStateChanged(ItemEvent e)
			{
				if( onBox.getState() ) startNoteByKey( lastNoteKey );
				else noteOff();
			} } );
		
/*		controlPanel.add( dumpButton = new Button("Dump Source") );
   		dumpButton.addActionListener(	new ActionListener() {
   				public void actionPerformed(ActionEvent e)
   				{ dumpSource( System.out );	} } );
*/		
		Choice presets;
		controlPanel.add( presets = new Choice() );
		presets.addKeyListener( this );
		presets.add( "Simple" );
		presets.add( "Bell" );
		presets.add( "WoodDrum" );
		presets.add( "Brass" );
		presets.add( "Clarinet" );
		presets.add( "Trumpet" );
		presets.add( "Gong" );
		presets.add( "Woozy" );
   		presets.addItemListener(	new ItemListener() {
   			public void itemStateChanged(ItemEvent e)
   			{
				keyButton.requestFocus();
				reset();
				if( ((String)e.getItem()).equals( "Simple" ) )
				{
					SimplePreset();
				}
				else if( ((String)e.getItem()).equals( "Bell" ) )
				{
					BellPreset();
				}
				else if( ((String)e.getItem()).equals( "WoodDrum" ) )
				{
					WoodDrumPreset();
				}
				else if( ((String)e.getItem()).equals( "Brass" ) )
				{
					BrassPreset();
				}
				else if( ((String)e.getItem()).equals( "Clarinet" ) )
				{
					ClarinetPreset();
				}
				else if( ((String)e.getItem()).equals( "Trumpet" ) )
				{
					TrumpetPreset();
				}
				else if( ((String)e.getItem()).equals( "Gong" ) )
				{
					GongPreset();
				}
				else if( ((String)e.getItem()).equals( "Woozy" ) )
				{
					WoozyPreset();
				}
				repaint();
			} } );
		

		add( new UsageDisplay() );
		
		add( showButton = new Button("Show Editor") );
		showButton.addKeyListener( this );
   		showButton.addActionListener(	new ActionListener() {
   				public void actionPerformed(ActionEvent e)
   				{ if( dialog != null ) dialog.show();	} } );

		updateDisplay();

		SimplePreset();
		dialog.show();
		
		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	}

	
	public void stop()
	{
		try
		{
			removeAll(); // remove portFaders
			dialog.removeAll();
			dialog.hide();
/* Turn off tracing. */
			Synth.setTrace( Synth.SILENT );
/* Stop synthesis engine. */
			Synth.stopEngine();
		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
	}
	OperatorEditor getNthEditor( int index )
	{
		return editors[index];
	}
	
	void setOperatorMix( int index, double level )
	{
		mixer.setGain( index, 0, level );
	}
	
	void updateDisplay()
	{
		validate();
		dialog.validate();
		getToolkit().sync();
	}
	
	
	void reset()
	{
		for( int i=0; i<NUM_OPERATORS; i++ )
		{
			editors[i].reset();
		}
	}

	void noteOn( double frequency )
	{
		for( int i=0; i<NUM_OPERATORS; i++ )
		{
			editors[i].noteOn( frequency );
		}
	}
	void noteOff()
	{
		for( int i=0; i<NUM_OPERATORS; i++ )
		{
			editors[i].noteOff();
		}
	}
	
/** Convert index in major scale to Frequency */
	double convertIndexToFreq( int index )
	{
		int scale[] = { 0, 2, 4, 5, 7, 9, 11 };
		int octave = index / scale.length;
		int idx = index % scale.length;
		int pitch = (octave * 12) + scale[idx];
		
		return EqualTemperedTuning.getMIDIFrequency( pitch + 48 );
	}
	
	boolean startNoteByKey( int key ) throws SynthException 
	{
/* Lookup position of key in keyboard string. */
		int index = keyboard.indexOf( key );
		if( index < 0 ) return false;
		else
		{
			double freq = convertIndexToFreq( index );
			// System.out.println( "freq = " + freq );
			noteOn( freq );
			return true;
		}
	}
	
	int lastKeyDown = -1;

/* Start note like a key press on a MIDI keyboard. */
	public void keyPressed( KeyEvent e )
	{
		char key = e.getKeyChar();
		if( key != lastKeyDown )
		{
			lastKeyDown = key;
			if( startNoteByKey( key ) ) lastNoteKey = key;
		}
	}
	
/* Typed note like a key release on a MIDI keyboard. */
	public void keyTyped( KeyEvent e )
	{
	}
	
/* Release note like a key release on a MIDI keyboard. */
	public void keyReleased( KeyEvent e )
	{
		char key = e.getKeyChar();
		// System.out.println("key up = " + key  + ", modifiers = " + e.getModifiers());
		if( key == lastKeyDown)
		{
			lastKeyDown = -1;
			noteOff();
		}
	}
		
	void dumpSource( PrintStream ps )
	{
		ps.println();
		ps.println("/* Java Source for an FM Preset.  Automatically generated. Do NOT edit by hand! */" );
		for( int i=0; i<NUM_OPERATORS; i++ )
		{
			ps.println("	editor =  editors[" + i + "];" );
			editors[i].dumpSource( ps, i );
		}	
	}

	void SimplePreset()
	{
	OperatorEditor editor;
/* Java Source for an FM Preset. Automatically generated. Do NOT edit by hand! */
	editor =  editors[0];
	editor.setSource( 1, true );
	editor.setHeard( true );
	editor.scaleKnob.setValue( 1.0 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 0.0 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar0[] = {
		0.100, 1.000,
		0.962, 0.455,
		1.232, 0.436,
		0.500, 0.000 };
	editor.setContour( dar0);
	editor =  editors[1];
	editor.setHeard( false );
	editor.scaleKnob.setValue( 3.0 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 6.449713125272715 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar1[] = {
		0.100, 1.000,
		0.064, 0.491,
		0.127, 0.209,
		0.500, 0.000 };
	editor.setContour( dar1);
	editor =  editors[2];
	editor.setHeard( false );
	editor.scaleKnob.setValue( 1.0 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 0.0 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar2[] = {
		0.100, 1.000,
		0.200, 0.500,
		0.500, 0.500,
		0.500, 0.000 };
	editor.setContour( dar2);
	editor =  editors[3];
	editor.setHeard( false );
	editor.scaleKnob.setValue( 1.0 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 0.0 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar3[] = {
		0.100, 1.000,
		0.200, 0.500,
		0.500, 0.500,
		0.500, 0.000 };
	editor.setContour( dar3);
	}
	
	
	void BellPreset()
	{
	OperatorEditor editor;
/* Java Source for an FM Preset.  Automatically generated. Do NOT edit by hand! */
	editor =  editors[0];
	editor.setSource( 1, true );
	editor.setHeard( true );
	editor.scaleKnob.setValue( 1.0 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 0.0 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar0[] = {
		0.045, 0.909,
		0.322, 0.545,
		0.497, 0.245,
		2.282, 0.055,
		4.492, 0.000 };
	editor.setContour( dar0);
	editor =  editors[1];
	editor.setHeard( false );
	editor.scaleKnob.setValue( 1.4 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 8.0 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar1[] = {
		0.000, 0.973,
		0.526, 0.345,
		1.188, 0.100,
		2.069, 0.000 };
	editor.setContour( dar1);
	editor =  editors[2];
	editor.setHeard( false );
	editor.scaleKnob.setValue( 1.0 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 0.0 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar2[] = {
		0.100, 1.000,
		0.200, 0.500,
		0.500, 0.500,
		0.500, 0.000 };
	editor.setContour( dar2);
	editor =  editors[3];
	editor.setHeard( false );
	editor.scaleKnob.setValue( 1.0 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 0.0 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar3[] = {
		0.100, 1.000,
		0.200, 0.500,
		0.500, 0.500,
		0.500, 0.000 };
	editor.setContour( dar3);
}
	
	void WoodDrumPreset()
	{
	OperatorEditor editor;
	/* Java Source for an FM Preset.  Automatically generated. Do NOT edit by hand! */
	editor =  editors[0];
	editor.setSource( 1, true );
	editor.setHeard( true );
	editor.scaleKnob.setValue( 1.0 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 0.0 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar0[] = {
		0.000, 0.445,
		0.004, 0.736,
		0.014, 0.973,
		0.018, 0.736,
		0.031, 0.345,
		0.109, 0.127,
		0.058, 0.000 };
	editor.setContour( dar0);
	editor =  editors[1];
	editor.setHeard( false );
	editor.scaleKnob.setValue( 0.6875 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 25.0 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar1[] = {
		0.000, 0.973,
		0.005, 0.245,
		0.012, 0.073,
		0.043, 0.000 };
	editor.setContour( dar1);
	editor =  editors[2];

	editor.setHeard( false );
	editor.scaleKnob.setValue( 1.0 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 0.0 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar2[] = {
		0.100, 1.000,
		0.200, 0.500,
		0.500, 0.500,
		0.500, 0.000 };
	editor.setContour( dar2);
	editor =  editors[3];
	editor.setHeard( false );
	editor.scaleKnob.setValue( 1.0 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 0.0 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar3[] = {
		0.100, 1.000,
		0.200, 0.500,
		0.500, 0.500,
		0.500, 0.000 };
	editor.setContour( dar3);
	}

	void BrassPreset()
	{
	OperatorEditor editor;
/* Java Source for an FM Preset.  Automatically generated. Do NOT edit by hand! */
	editor =  editors[0];
	editor.setSource( 1, true );
	editor.setSource( 2, true );
	editor.setHeard( true );
	editor.setFixed( false );
	editor.setSustainIndex( 2 );
	editor.scaleKnob.setValue( 1.0 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 0.0 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar0[] = {
		0.014, 0.927,
		0.077, 0.627,
		0.210, 0.000 };
	editor.setContour( dar0);
	editor =  editors[1];
	editor.setSource( 2, true );
	editor.setHeard( false );
	editor.setFixed( false );
	editor.setSustainIndex( 2 );
	editor.scaleKnob.setValue( 1.0 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 6.449713125272715 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar1[] = {
		0.029, 0.955,
		0.088, 0.582,
		0.158, 0.000 };
	editor.setContour( dar1);
	editor =  editors[2];
	editor.setHeard( false );
	editor.setFixed( true );
	editor.setSustainIndex( -1 );
	editor.scaleKnob.setValue( 3.4375415772291475 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 0.5643362207853508 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar2[] = {
		0.000, 0.000,
		0.159, 0.000,
		0.500, 0.500 };
	editor.setContour( dar2);
	editor =  editors[3];
	editor.setHeard( false );
	editor.setFixed( false );
	editor.setSustainIndex( -1 );
	editor.scaleKnob.setValue( 1.0 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 0.0 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar3[] = {
		0.100, 1.000,
		0.200, 0.500,
		0.500, 0.500,
		0.500, 0.000 };
	editor.setContour( dar3);
	}

	void ClarinetPreset()
	{
	OperatorEditor editor;
/* Java Source for an FM Preset.  Automatically generated. Do NOT edit by hand! */
	editor =  editors[0];
	editor.setSource( 1, true );
	editor.setSource( 2, true );
	editor.setHeard( true );
	editor.setFixed( false );
	editor.setSustainIndex( 2 );
	editor.scaleKnob.setValue( 3.0 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 0.0 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar0[] = {
		0.022, 0.700,
		0.049, 0.912,
		0.232, 0.876,
		0.210, 0.000 };
	editor.setContour( dar0);
	editor =  editors[1];
	editor.setSource( 2, true );
	editor.setHeard( false );
	editor.setFixed( false );
	editor.setSustainIndex( -1 );
	editor.scaleKnob.setValue( 2.0 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 4.0 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar1[] = {
		0.018, 0.918,
		0.021, 0.752,
		0.027, 0.593,
		0.100, 0.531 };
	editor.setContour( dar1);
	editor =  editors[2];
	editor.setHeard( false );
	editor.setFixed( true );
	editor.setSustainIndex( -1 );
	editor.scaleKnob.setValue( 1.717964842236856 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 2.3219476777485197 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar2[] = {
		0.000, 0.000,
		0.140, 0.064,
		0.267, 0.273,
		0.467, 0.118 };
	editor.setContour( dar2);
	editor =  editors[3];
	editor.setHeard( false );
	editor.setFixed( false );
	editor.setSustainIndex( -1 );
	editor.scaleKnob.setValue( 1.0 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 0.0 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar3[] = {
		0.100, 1.000,
		0.200, 0.500,
		0.500, 0.500,
		0.500, 0.000 };
	editor.setContour( dar3);
	}

	
	void TrumpetPreset()
	{
	OperatorEditor editor;
/* Java Source for an FM Preset.  Automatically generated. Do NOT edit by hand! */
	editor =  editors[0];
	editor.setSource( 3, true );
	editor.setHeard( true );
	editor.setFixed( false );
	editor.setSustainIndex( 2 );
	editor.scaleKnob.setValue( 1.0 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 0.0 );
	editor.amplitudeKnob.setValue( -0.1 );
	double dar0[] = {
		0.014, 0.927,
		0.094, 0.809,
		0.136, 0.318,
		0.335, 0.000 };
	editor.setContour( dar0);
	editor =  editors[1];
	editor.setSource( 3, true );
	editor.setHeard( true );
	editor.setFixed( false );
	editor.setSustainIndex( 2 );
	editor.scaleKnob.setValue( 6.006 );
	editor.depthKnob.setValue( 0.0 );
	editor.indexKnob.setValue( 0.0 );
	editor.amplitudeKnob.setValue( -10.0 );
	double dar1[] = {
		0.029, 0.955,
		0.088, 0.791,
		0.085, 0.209,
		0.080, 0.000 };
	editor.setContour( dar1);
	editor =  editors[2];
	editor.setSource( 3, true );
	editor.setHeard( true );
	editor.setFixed( false );
	editor.setSustainIndex( 2 );
	editor.scaleKnob.setValue( 5.009 );
	editor.depthKnob.setValue( 0.5 );
	editor.indexKnob.setValue( 0.0 );
	editor.amplitudeKnob.setValue( -6.1 );
	double dar2[] = {
		0.031, 0.973,
		0.175, 0.858,
		0.098, 0.191,
		0.047, 0.000 };
	editor.setContour( dar2);
	editor =  editors[3];
	editor.setHeard( false );
	editor.setFixed( false );
	editor.setSustainIndex( 2 );
	editor.scaleKnob.setValue( 1.001 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 9.35406887647245 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar3[] = {
		0.014, 0.953,
		0.178, 0.827,
		0.075, 0.132 };
	editor.setContour( dar3);
	}

	void GongPreset()
	{
	OperatorEditor editor;
/* Java Source for an FM Preset.  Automatically generated. Do NOT edit by hand! */
	editor =  editors[0];
	editor.setSource( 1, true );
	editor.setSource( 2, true );
	editor.setSource( 3, true );
	editor.setHeard( true );
	editor.setFixed( false );
	editor.setSustainIndex( -1 );
	editor.scaleKnob.setValue( 1.0 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 0.0 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar0[] = {
		0.045, 0.909,
		0.922, 0.518,
		0.917, 0.218,
		2.272, 0.082,
		1.916, 0.000 };
	editor.setContour( dar0);
	editor =  editors[1];
	editor.setHeard( false );
	editor.setFixed( false );
	editor.setSustainIndex( -1 );
	editor.scaleKnob.setValue( 1.007 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 0.9587270464471019 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar1[] = {
		0.000, 0.973,
		0.526, 0.345,
		1.188, 0.100,
		2.069, 0.009 };
	editor.setContour( dar1);
	editor =  editors[2];
	editor.setHeard( false );
	editor.setFixed( false );
	editor.setSustainIndex( -1 );
	editor.scaleKnob.setValue( 3.141 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 1.2361358646926257 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar2[] = {
		0.000, 0.964,
		0.006, 0.082,
		0.040, 0.000 };
	editor.setContour( dar2);
	editor =  editors[3];
	editor.setHeard( false );
	editor.setFixed( false );
	editor.setSustainIndex( -1 );
	editor.scaleKnob.setValue( 1.1693410785057612 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 1.6917461386579746 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar3[] = {
		0.000, 0.982,
		0.345, 0.491,
		0.399, 0.209,
		1.503, 0.064,
		2.042, 0.000 };
	editor.setContour( dar3);
	}

	void WoozyPreset()
	{
	OperatorEditor editor;

/* Java Source for an FM Preset.  Automatically generated. Do NOT edit by hand! */
	editor =  editors[0];
	editor.setSource( 1, true );
	editor.setSource( 3, true );
	editor.setHeard( true );
	editor.setFixed( false );
	editor.setSustainIndex( 2 );
	editor.scaleKnob.setValue( 1.0 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 0.0 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar0[] = {
		0.071, 0.945,
		0.160, 0.555,
		0.623, 0.000 };
	editor.setContour( dar0);
	editor =  editors[1];
	editor.setSource( 2, true );
	editor.setSource( 3, true );
	editor.setHeard( false );
	editor.setFixed( false );
	editor.setSustainIndex( 2 );
	editor.scaleKnob.setValue( 0.5 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 9.821455999237836 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar1[] = {
		0.182, 0.200,
		1.139, 0.800,
		0.393, 0.264,
		0.730, 0.000 };
	editor.setContour( dar1);
	editor =  editors[2];
	editor.setSource( 3, true );
	editor.setHeard( false );
	editor.setFixed( false );
	editor.setSustainIndex( -1 );
	editor.scaleKnob.setValue( 3.0 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 1.5124727965756355 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar2[] = {
		0.000, 0.000,
		0.803, 0.118,
		1.256, 0.600,
		0.568, 0.500,
		0.536, 0.318 };
	editor.setContour( dar2);
	editor =  editors[3];
	editor.setHeard( false );
	editor.setFixed( true );
	editor.setSustainIndex( 2 );
	editor.scaleKnob.setValue( 6.50951821486465 );
	editor.depthKnob.setValue( 1.0 );
	editor.indexKnob.setValue( 0.5372214641762998 );
	editor.amplitudeKnob.setValue( 0.0 );
	double dar3[] = {
		0.000, 0.000,
		1.086, 0.927,
		0.941, 0.000 };
	editor.setContour( dar3);
	}
}
