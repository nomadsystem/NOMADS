package com.softsynth.jsyn.tutorial;
import com.softsynth.jsyn.*;

/**
 * Siren
 * Create a siren sound by modulating a square wave with a sine wave.
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

public class Siren extends SynthCircuit
{
/* Declare units that will be part of the circuit. */
	TriangleOscillator modOsc;
	SquareOscillatorBL squareOsc; // use band limited version
	AddUnit            freqAdder;
	
/* Declare ports. */
	public SynthInput frequency;
	public SynthInput modulationRate;
	public SynthInput modulationDepth;
	public SynthInput amplitude;
// public SynthOuput output; // is already declared as part of SynthCircuit
	
	public Siren()  throws SynthException
	{

/* Create various unit generators and add them to circuit.
 * Units that are added to the circuit will be compiled into the circuit
 * and started together when one starts the circuit.
 */
		add( modOsc     = new TriangleOscillator() );
		add( squareOsc  = new SquareOscillatorBL() );
		add( freqAdder  = new AddUnit() );


/* Make ports on internal units appear as ports on circuit. */ 
/* Optionally give some circuit ports more meaningful names. */
		addPort( frequency = freqAdder.inputB, "frequency" );
		addPort( modulationRate = modOsc.frequency, "modRate" );
		addPort( modulationDepth = modOsc.amplitude, "modDepth" );
		addPort( amplitude = squareOsc.amplitude );
		addPort( output = squareOsc.output );
		
/* Feed first oscillators through adder to offset center frequency. */
		modOsc.output.connect( freqAdder.inputA );
		freqAdder.output.connect( squareOsc.frequency );

/* Set ports to useful values and ranges. */
		frequency.setup( 0.0, 300.0, 1000.0 );
		modulationRate.setup(   0.0, 1.0, 10.0 );
		modulationDepth.setup(  0.0, 100.0, 500.0 );
		amplitude.setup(   0.0, 0.9, 0.999 );
	}
	
/** Define a behavior for setStage(). This is a flexible way to do something like ON/OFF
 * control of a circuit.
 */
	public void setStage( int time, int stage )
	throws SynthException
	{
		if( stage == 0 )
		{
			start( time );
		}
		else
		{
			stop( time );
		}
	}
}

