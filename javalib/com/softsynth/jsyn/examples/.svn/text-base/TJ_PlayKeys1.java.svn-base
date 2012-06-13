/** 
 * Play various JSyn sounds using ASCII keyboard.
 * Demonstrate BussedVoiceAllocator.
 * Also demonstrate recording and playing back from a stereo sample.
 * Uses AWT 1.1 for the GUI.
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.Button;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.circuits.RingModBell;
import com.softsynth.jsyn.util.BussedVoiceAllocator;
import com.softsynth.jsyn.view102.UsageDisplay;

public class TJ_PlayKeys1 extends Applet implements KeyListener
{
	PitchedVoices          ringVoices;
	String                 ringKeys = "qwertyuiop";
	LineOut                lineOut;
	LineOut                lineOutRec;
	double                 pentatonic[];
	StereoRecorder         recorder;
	Button                 keyButton;
	BussedVoiceAllocator   voiceAllocator;
	
/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_PlayKeys1 applet = new TJ_PlayKeys1();
		AppletFrame frame = new AppletFrame("Play ASCII keyboard.", applet);
		frame.resize(600,400);
		frame.show();
/* Begin test after frame opened so that DirectSound will use Java window. */
		frame.test();
	}

/* Define a just intoned pentatonic scale. */
	double[] buildPentatonic( int numNotes, double fundamental )
	{
// Define ratios making up tuning.
		double scale[] = { 1.0/1.0, 5.0/4.0, 4.0/3.0, 3.0/2.0, 5.0/3.0 };
		
		double[] freqs = new double[ numNotes ];
// Fill in tuning table based on octaves.
		for( int i=0; i<numNotes; i++ )
		{
			int octave = i / 5;
			int pitch = i % 5;
			freqs[i] = scale[pitch] * fundamental * (1<<octave);
		}
		return freqs;
	}
				
/*
 * Setup synthesis.
 */
	public void start()
	{
		setLayout( new GridLayout(0,1) );
		pentatonic = buildPentatonic( ringKeys.length(), 400.0 );
		try
		{
/* Start synthesis engine. */
		Synth.startEngine( 0 );
		Synth.verbosity = Synth.SILENT;
		
/* Create an allocator for up to 4 RingModBell voices.
 * Define abstract makeVoice() method so class can be instantiated.
 */
		voiceAllocator = new BussedVoiceAllocator( 4 )
			{
				public SynthCircuit makeVoice() throws SynthException
				{
					SynthNote circ = new RingModBell();
					return addVoiceToMix( circ ); // mix through bus writer
				}
			};
			
/* Create an object that associates pitches with allocated voices. */
		ringVoices = new PitchedVoices( voiceAllocator, pentatonic );
		
		lineOut = new LineOut( );
		voiceAllocator.output.connect( 0, lineOut.input, 0 );
		lineOut.start();
		
/* Use a recorder to record and playback captured audio. */
		recorder = new StereoRecorder( 5.0 );
		recorder.start();
		voiceAllocator.getOutput().connect( 0, recorder.input, 0 );
/* Record output a second time on right channel. */
		recorder.output.connect( 0, recorder.input, 1 );
					
		lineOutRec = new LineOut();
		lineOutRec.start();
		recorder.output.connect( 0, lineOutRec.input, 0 );
		recorder.output.connect( 1, lineOutRec.input, 1 );

/* Build GUI */
		
		add( keyButton = new Button("Activate Keyboard") ) ;
		keyButton.addKeyListener( this );
		
		add( new Label("RingMod: " + ringKeys) ) ;
		add( new UsageDisplay() ) ;
		add( recorder.buildGUI() );
		
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
		voiceAllocator.stop();
		voiceAllocator.delete();
		voiceAllocator = null;
		
		removeAll(); // remove portFaders
/* Turn off tracing. */
		Synth.verbosity = Synth.SILENT;
/* Stop synthesis engine. */
		Synth.stopEngine();
	}
		
	int lastKeyDown = -1;

	
/* Start note like a key press on a MIDI keyboard. */
	public void keyPressed( KeyEvent e )
	{
		int key = e.getKeyChar();
		System.out.println("key pressed = " + key);
		int idx;
		if( key != lastKeyDown )
		{
			lastKeyDown = key;
	// Is key pressed part of assigned keys for this instrument?
			idx = ringKeys.indexOf( key );
			if( idx >= 0 )
			{
				ringVoices.noteOn( idx, 0.2 );
			}
	// This code is only here to make sure I am referenceing all my objects correctly.
	// An unreferenced object can get garbage collected and will disappear.
			else if( key == 'g' )
			{
				System.out.println("Garbage collect!");
				Runtime.getRuntime().gc();
			}
		}
	}
	
	public void keyTyped( KeyEvent e ) {};
	
/* Release note like a key release on a MIDI keyboard. */
	public void keyReleased( KeyEvent e )
	{
		int key = e.getKeyChar();
		if( lastKeyDown != -1)
		{
			lastKeyDown = -1;
			int idx;
	// Is key released part of assigned keys for this instrument?
			idx = ringKeys.indexOf( key );
			if( idx >= 0 )
			{
				ringVoices.noteOff( idx );
			}	
		}
	}
	
/**************************************************
 * Inner class to handle triggering voices by indexed pitch.
 */
	class PitchedVoices
	{
		BussedVoiceAllocator allocator;
		SynthNote            voices[];
		SynthNote            voice;
		double               frequencies[];
		int                  numPitches;
		
		public PitchedVoices( BussedVoiceAllocator allocator, double frequencies[] )
		{
			this.allocator = allocator;
			this.frequencies = frequencies;
			numPitches = frequencies.length;
			voices = new SynthNote[numPitches];  // make array to index voice by pitch
		}
/** Allocate and turn on a voice. If a voice with the same pitch
 * index is already playing, turn it off.
 */
		public void noteOn( int pitchIndex, double amplitude )
		{
			if( voices[pitchIndex] != null ) noteOff( pitchIndex );
	// Steal voice from those already playing.
	// Keep track of notes associated with pitches so that we can turn it off by pitch.
			voices[pitchIndex] = voice = (SynthNote) allocator.steal();
			voice.noteOn( Synth.getTickCount(), frequencies[pitchIndex], amplitude );
		}
		
		public void noteOff( int pitchIndex )
		{
			if( (voice = voices[pitchIndex]) != null )
			{
				voice.noteOff( Synth.getTickCount() );
				voices[pitchIndex] = null;
	// Make voice available for other notes.
				allocator.free( voice );
			}
		}
	}

/** Define a class that records sound into an in memory sample and plays it back.
 */
	class StereoRecorder extends SynthCircuit
	{
		SynthSample  stereoSample;
		SampleWriter_16F2  sampleWriter;
		SampleReader_16F2  sampleReader;
		SynthInput         input;
		Button             recordButton, recordLoopButton, recordStopButton;
		Button             playButton, playLoopButton, playStopButton;

		public StereoRecorder( double seconds )
		{
	// Allocate a stereo sample.
			stereoSample = new SynthSample( (int)(Synth.getFrameRate() * seconds), 2 );
			add( sampleWriter = new SampleWriter_16F2() );
			add( sampleReader = new SampleReader_16F2() );
			
			addPort( input = sampleWriter.input );
			addPort( output = sampleReader.output );
		}
		
		public Panel buildGUI()
		{
			Panel panel = new Panel();
			panel.add( recordButton = new Button("Record") );
/* Define an actionListener class that will record() when the button is pressed. */
			recordButton.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						record();
					}
				} );
			
			panel.add( recordLoopButton = new Button("RecordLoop") );
			recordLoopButton.addActionListener(	new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{ recordLoop();	} } );
			
			panel.add( recordStopButton = new Button("RecordStop") );
			recordStopButton.addActionListener(	new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{ recordStop();	} } );
			
			panel.add( playButton = new Button("Play") );
			playButton.addActionListener(	new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{ play();	} } );
			
			panel.add( playLoopButton = new Button("PlayLoop") );
			playLoopButton.addActionListener(	new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{ playLoop();	} } );
			
			panel.add( playStopButton = new Button("PlayStop") );
			playStopButton.addActionListener(	new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{ playStop();	} } );
			
			return panel;
		}
		
/** Record a block of sound.
 */
		public void record()
		{
			sampleWriter.samplePort.queue( stereoSample );
		}
/** Record a block of sound in a loop.
 */
		public void recordLoop()
		{
			sampleWriter.samplePort.queueLoop( stereoSample );
		}
/** Stop recording.
 */
		public void recordStop()
		{
			sampleWriter.samplePort.clear();
		}
/** Play back the recorded sound.
 */
		public void play()
		{
			sampleReader.samplePort.queue( stereoSample );
		}
/** Play back the recorded sound in a loop.
 */
		public void playLoop()
		{
			sampleReader.samplePort.queueLoop( stereoSample );
		}
/** Stop playing.
 */
		public void playStop()
		{
			sampleReader.samplePort.clear();
		}
	}
}
