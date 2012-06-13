/**
 * Generate lots of grains.
 * Daisy chain ParabolicGrains so that grains are dynamically assigned
 * when a trigger pulse occurs.
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.examples;
import com.softsynth.jsyn.*;
import com.softsynth.jsyn.circuits.ParabolicGrain;
import com.softsynth.jsyn.circuits.PoissonTrigger;

public class GrainFarm extends SynthNote
{
	public PoissonTrigger  triggerMaker;
	public ParabolicGrain  grains[];
	public BusWriter       mixers[];
	
	public WhiteNoise      freqNoise;
	public AddUnit         freqAdder;
	public AddUnit         tieSpeed;  /* Just to fan-out grainSpeed to all the grains. */

/** Probability of triggering in a sample frame. */
	public SynthInput  probability;
/** Range of offsets for frequency. */
	public SynthInput  spread;
	public SynthInput  grainSpeed;
	
	public BusReader busMix;

	int numGrains;
/*
 * Setup synthesis.
 */
	public GrainFarm( int numGrains ) throws SynthException
	{
		super();

		this.numGrains = numGrains;

/* Create various unit generators and add them to circuit. */
		add( busMix = new BusReader() );
		add( triggerMaker = new PoissonTrigger() );
		add( freqNoise = new WhiteNoise() );
		add( freqAdder = new AddUnit() );
		add( tieSpeed = new AddUnit() );

/* Create arrays to hold individual grain generators. */
		grains = new ParabolicGrain[numGrains];
		mixers = new BusWriter[numGrains];

		for( int i=0; i<numGrains; i++ )
		{
			add( grains[i] = new ParabolicGrain() );
			add( mixers[i] = new BusWriter() );

			freqAdder.output.connect( grains[i].frequency );
			tieSpeed.output.connect( grains[i].rate );

			grains[i].amplitude.set( 1.0/numGrains ); /* Guarantee no clipping. */

/* Mix output of grains via SynthBus */
			grains[i].output.connect( mixers[i].input );
			mixers[i].busOutput.connect( busMix.busInput );

/* Daisy chain triggers. */
			if( i == 0 )
			{
				triggerMaker.output.connect( grains[i].triggerInput );
			}
			else
			{
				grains[i-1].triggerPass.connect( grains[i].triggerInput );
			}
		}

/* Connect SynthUnits to make signal path. */
		freqNoise.output.connect( freqAdder.inputA );

/* Make ports on internal units appear as ports on circuit. */ 
		addPort( probability = triggerMaker.probability );
		addPort( frequency = freqAdder.inputB, "Frequency" );
		addPort( spread = freqNoise.amplitude, "Spread" );
		addPort( grainSpeed = tieSpeed.inputA, "GrainSpeed" );
		addPort( amplitude = busMix.amplitude );
		addPort( output = busMix.output );

/* Set signal type for rate control so that we can operate in hertz. */
		frequency.setSignalType( grains[0].frequency );
		spread.setSignalType( grains[0].frequency );
		grainSpeed.setSignalType( grains[0].rate );
		
		spread.setup(   0.0, 120.0, 1000.0 );
		grainSpeed.setup(   0.0, 60.0, 1000.0 );
		probability.setup(   0.0, 0.001, 0.03 );
		amplitude.setup(   0.0, 0.7, 0.999 );
		frequency.setup(   0.0, 800.0, 3000.0 );
	}
}

