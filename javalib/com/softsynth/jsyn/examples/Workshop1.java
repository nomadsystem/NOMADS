package com.softsynth.jsyn.examples;
import com.softsynth.jsyn.*;

/**
 * Play with an oscillator being modulated by an LFO.
 * Use Band Limited version of sawtooth for better sound quality. 
 *
 * @author Phil Burk
 * (C) 1997 Phil Burk
 */

public class Workshop1
{
	SawtoothOscillatorBL osc;  // BL for Band Limited
	LineOut            lineOut;
	TriangleOscillator lfo;
	AddUnit            adder;
	SynthInput         frequency;

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		Workshop1 app = new Workshop1();
		app.test();
		System.exit( 0 );
	}

/*
 * Setup synthesis.
 */
	public void test()
	{
		System.out.println("Play SawtoothBL");
		try
		{
/* Start synthesis engine. */
		Synth.startEngine( 0 );
		
/* Make unit generators. */
		osc = new SawtoothOscillatorBL();
		lineOut = new LineOut();
		lfo = new TriangleOscillator();
		adder = new AddUnit();

/* LFO and center frequency are added together to calculate new frequency. */
		frequency = adder.inputB;
		lfo.output.connect( adder.inputA );
		adder.output.connect( osc.frequency );
		
/* Connect oscillator to both channels of stereo player. */
		osc.output.connect( 0, lineOut.input, 0 );
		osc.output.connect( 0, lineOut.input, 1 );

/* Start execution of units. */
		lineOut.start();
		osc.start();
		adder.start();
		lfo.start();

	// Calculate note duration in ticks.
		int duration = (int) (Synth.getTickRate() * 0.2);
	// schedule in advance for stable timing
		int advanceTime = (int) (Synth.getTickRate() * 0.5);
		int time = Synth.getTickCount() + advanceTime;
		
	// play notes in a loop
		double freq = 200.0;
		for( int i=0; i<10; i++ )
		{
			frequency.set( time, freq );
			lfo.frequency.set( time, Math.random() * 2.0 );
			Synth.sleepUntilTick( time );
			freq *= (4.0 / 3.0);
			time += duration;
		}
		
		osc.delete();
		lineOut.delete();
		lfo.delete();
		adder.delete();
/* Stop synthesis engine. */
		Synth.stopEngine();

		} catch (SynthException e) {
			SynthAlert.showError(e);
		}
		System.out.println("Finished.");
	}
}
