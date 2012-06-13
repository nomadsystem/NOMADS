/**
 * Test FM Feedback using Java Audio Synthesiser
 * Fader "feedbackDepth" controls amount of carrier that feeds
 * back to the frequency of the modulator.
 *
 * Feedback in FM can result in rich, chaotic or noisy sounds.
 *
 * @author (C) 1997 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.examples;
import com.softsynth.jsyn.*;


/* FM pair with feedback from carrier to modulator frequency. */
class FeedbackFM extends SynthNote
{
	SineOscillator modOsc;
	SineOscillator carOsc;
	AddUnit adder;
	MultiplyAddUnit feedbackScaler;
	public SynthInput modFrequency;
	public SynthInput modAmplitude;
	public SynthInput feedbackDepth;
        
	public FeedbackFM() throws SynthException
	{
	// FIXME - we break up the creation into two stages so we can over ride port assignments
	// before calling addPort().
		makeCircuit();
		addAllPorts();
	}
		
	void makeCircuit()
	{
/* Add SynthUnits to circuit */
		add(modOsc = new SineOscillator());
		add(carOsc = new SineOscillator());
		add(adder = new AddUnit());
		
/* The feedbackScaler unit mixes some of the carrier output with the desired modFrequency. */
		add(feedbackScaler = new MultiplyAddUnit() );

/* Connect units together. */
		modOsc.output.connect(adder.inputB);
		adder.output.connect(carOsc.frequency);
		carOsc.output.connect(feedbackScaler.inputB);
		feedbackScaler.output.connect(modOsc.frequency);
                
/* Make ports visible. */
		frequency = adder.inputA;
		amplitude = carOsc.amplitude;
		feedbackDepth = feedbackScaler.inputA;
		modFrequency = feedbackScaler.inputC;
		modAmplitude = modOsc.amplitude;
		output = carOsc.output;
                
/* Set signal types for more conveniant control. */
		frequency.setSignalType(Synth.SIGNAL_TYPE_OSC_FREQ);	
		feedbackDepth.setSignalType(Synth.SIGNAL_TYPE_OSC_FREQ);	
		modFrequency.setSignalType(Synth.SIGNAL_TYPE_OSC_FREQ);	
		modAmplitude.setSignalType(Synth.SIGNAL_TYPE_OSC_FREQ); // so we can set modulation range in Hz

/* Set ports to useful values and ranges. */
		modFrequency.setup( 0.0, 600.0, 6000.0 );
		modAmplitude.setup(   0.0, 200.0, 5000.0 );
		frequency.setup(  0.0, 300.0, 2000.0 );
		feedbackDepth.setup(  0.0, 000.0, 6000.0 );
		amplitude.setup(   0.0, 0.5, 1.0 );
    }

/* Do addPorts in separate method so we can override port assignments. */
	void addAllPorts()
	{
		addPort(modFrequency, "modFrequency");
		addPort(modAmplitude, "modAmplitude");
		addPort(frequency, "frequency");
		addPort(feedbackDepth, "feedbackDepth");
		addPort(amplitude);
		addPort(output);
	}

}

public class TJ_FeedbackFM extends SoundTestApplet
{
/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		TJ_FeedbackFM applet = new TJ_FeedbackFM();
		AppletFrame frame = new AppletFrame("Test FeedbackFM", applet);
		frame.resize(600,500);
		frame.show();
/* Begin test after frame opened so that DirectSound will use Java window. */
		frame.test();
	}
	
/** Override this method to make an Applet that tests a different circuit. */
	public SynthCircuit makeCircuit() throws SynthException
	{
		return new FeedbackFM();
	}
}
