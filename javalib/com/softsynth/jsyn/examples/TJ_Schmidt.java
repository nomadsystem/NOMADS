/**
 * Test SchmidtTrigger and SelectUnit using Java Audio Synthesiser
 *
 * Move the input knob. You should hear the triangle come in when
 * input goes above setLevel, and the noise should start when input
 * goes below resetLevel.
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.examples;
import com.softsynth.jsyn.*;

class TestSchmidtSelect extends SynthCircuit
{
	WhiteNoise     myNoise;
	TriangleOscillator  myTri;
	SelectUnit     mySelect;
	SchmidtTrigger mySchmidt;
	
/* Declare port. */
	public SynthInput input;
	public SynthInput setLevel;
	public SynthInput resetLevel;

/*
 * Setup synthesis.
 */
	public TestSchmidtSelect()  throws SynthException
	{
/* Create various unit generators and add them to circuit. */
		add( myNoise = new WhiteNoise() );
		add( myTri = new TriangleOscillator() );
		add( mySelect = new SelectUnit() );
		add( mySchmidt = new SchmidtTrigger() );

/* Select between noise or triangle depending on Schmidt output. */
		myNoise.output.connect( mySelect.inputA );
		myTri.output.connect( mySelect.inputB );
		mySchmidt.output.connect( mySelect.select );

/* Make ports on internal units appear as ports on circuit. */ 
/* Give some circuit ports more meaningful names. */
		addPort( input = mySchmidt.input);
		addPort( setLevel = mySchmidt.setLevel);
		addPort( resetLevel = mySchmidt.resetLevel);
		addPort( output = mySelect.output );

		myNoise.amplitude.set( 0.4 );
		myTri.amplitude.set( 0.4 );

/* Set ports to useful values and ranges. */
		input.setup( -10.0, 0.0, 10.0 );
		setLevel.setup( -10.0, 5.0, 10.0 );
		resetLevel.setup(  -10.0, -5.0, 10.0 );
	}
	
/* Define a behavior for setStage() */
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


public class TJ_Schmidt extends SoundTestApplet
{

/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_Schmidt applet = new TJ_Schmidt();
		AppletFrame frame = new AppletFrame("Test Schmidt", applet);
		frame.resize(600,400);
		applet.ifScope = false;
		frame.show();
/* Begin test after frame opened so that DirectSound will use Java window. */
		frame.test();
	}
	
/** Override this method to make an Applet that tests a different circuit. */
	public SynthCircuit makeCircuit() throws SynthException
	{
		return new TestSchmidtSelect();
	}

}
