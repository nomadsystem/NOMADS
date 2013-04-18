//package com.softsynth.jsyn.circuits;
import java.util.*;
import java.awt.*;
import com.softsynth.jsyn.*;


public class NoiseSwarm extends SynthCircuit
{
/* Declare units that will be part of the circuit. */
	WhiteNoise   myNoise;
	Filter_BandPass myBandPassFilter;
//	StateVariableFilter myFilter;
//	RedNoise     myLFO;
	MultiplyAddUnit  myScalar;
	
/* Declare ports. */
	public SynthInput noiseAmp;
//	public SynthInput modRate;
//	public SynthInput modDepth;
//	public SynthInput cutoff;
//	public SynthInput resonance;
	public SynthInput frequency;
	public SynthInput amplitude;
	public SynthInput myQ;
	
	public NoiseSwarm()  throws SynthException
	{
		super();

/* Create various unit generators and add them to circuit.
 * Units that are added to the circuit will be compiled into the circuit
 * and started together when one starts the circuit.
 */
		add( myNoise = new WhiteNoise() );
		add( myBandPassFilter = new Filter_BandPass() );
	//	add( myFilter = new StateVariableFilter() );
	//	add( myLFO = new RedNoise() );
	//	add( myScalar = new MultiplyAddUnit() );

/* Make ports on internal units appear as ports on circuit. */ 
/* Optionally give some circuit ports more meaningful names. */
		addPort( noiseAmp = myNoise.amplitude, "NoiseAmp" );
		addPort( amplitude = myBandPassFilter.amplitude, "FilterAmplitude" );
		addPort( frequency = myBandPassFilter.frequency, "FilterFreqency" );
		addPort( myQ = myBandPassFilter.Q, "FilterQ" );
	//	addPort( modRate = myLFO.frequency, "ModRate" );
	//	addPort( modDepth = myScalar.inputB, "ModDepth" );
	//	addPort( cutoff = myScalar.inputC, "Cutoff" );
	//	addPort( resonance = myFilter.resonance );
	//	addPort( amplitude = myFilter.amplitude );
		addPort( output = myBandPassFilter.output );
		
/* Connect SynthUnits to make control signal path. */
	//	myLFO.output.connect( myScalar.inputA );
	//	myScalar.output.connect( myFilter.frequency );
/* Connect SynthUnits to make audio signal path. */
		myNoise.output.connect( myBandPassFilter.input );

/* Set signal type for filter control so that we can operate in hertz. */
	//	modDepth.setSignalType( Synth.SIGNAL_TYPE_SVF_FREQ );
	//	cutoff.setSignalType( Synth.SIGNAL_TYPE_SVF_FREQ );
	

/* Set ports to useful values and ranges. */
		noiseAmp.setup( 0.0, 0.3, 0.4 );
	//	modRate.setup(   0.0, 1.0, 10.0 );
	//	modDepth.setup(  0.0, 300.0, 1000.0 );
	//	cutoff.setup(   0.0, 600.0, 1000.0 );
	//	resonance.setup(  0.0, 0.066, 0.2 );
		frequency.setup( 20.0, 440.0, 22050.0);
		amplitude.setup(   0.0, 0.9, 0.999 );
		myQ.setup( 0, 99, 100);
	}

    public void delete() {
	myNoise.deleteAll();
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

