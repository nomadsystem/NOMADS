/**
 * FilterFun - experiment with digital filters.
 * This is used to hear the effect of various filters on a variety of source material.
 * The filters have a graphical component that are subclasses of FunFilter.
 *
 * @author (C) 1998 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view11x.*;
import com.softsynth.view.CustomFaderDouble;

public class FilterFun extends Applet implements Tweakable
{
	final static Color filterColor = Color.cyan;
	WhiteNoise        whiteNoise;
	PinkNoise         pinkNoise;
	RedNoise          redNoise;
	ImpulseOscillator impulse;
	SawtoothOscillatorBL sawtooth;
	SineOscillator    sineosc;
	SynthUnit         currentSource = null;
	SynthOutput       currentSourceOutput = null;
	SynthInput        currentSourceAmplitude = null;
	SynthFilter       currentFilter = null;
	AddUnit           srcTap;
	AddUnit           filterTap;
	final static double MIN_CUTOFF = 10.0;
	final static double DEF_CUTOFF = 400.0;
	final static double MAX_CUTOFF = 10000.0;
	double            freqValue = 2000.0;
	double            ampValue = 0.5;
	double            cutoffValue = MAX_CUTOFF/2.0;
	double            bandwidthValue = 0.5;
	double            dBGainValue = 0.0;
	double            slopeValue = 1.0;
	
	FunFilter         funBiquad;
	FunFilter         funSVF;
	FunFilter         fun1Z;
	FunFilter         fun1P;
	FunFilter         fun1P1Z;
	FunFilter         fun2P;
	FunFilter         funLowPass;
	FunFilter         funHighPass;
	FunFilter         funBandPass;
	FunFilter         funBandStop;
	FunFilter         funPeakingEQ;
	FunFilter         funLowShelf;
	FunFilter         funHighShelf;
	
	LineOut           myOut;
	
	Choice            sourceChoice;
	Choice            filterChoice;
	LabelledFader     freqFader;
	LabelledFader     ampFader;
	Panel             filterGUI;
	SynthScope        scope;
	
/* The order of these must match the items in passChoice */
	final static int LOW_PASS  = 0;
	final static int BAND_PASS  = 1;
	final static int HIGH_PASS  = 2;
	final static int NOTCH      = 3;
	final static int PEAKING_EQ = 4;
	final static int LOW_SHELF  = 5;
	final static int HIGH_SHELF = 6;
	
/* Can be run as either an application or as an applet. */
	public static void main(String args[])
	{
/* DO: Change FilterLab to match the name of your class. Must match file name! */
	   FilterFun  applet = new FilterFun();
	   AppletFrame frame = new AppletFrame("Filter Fun", applet);
	   frame.resize(700,500);
	   frame.show();
 /* Begin test after frame opened so that DirectSound will use Java window. */
	   frame.test();
	}

/********************************************************************/
	void buildGUI()
	{
/* Use GridBagLayout to get reasonably sized components. */
		GridBagLayout  gridbag  =  new  GridBagLayout();
		setLayout(gridbag);
		GridBagConstraints  constraint  =  new  GridBagConstraints();
		constraint.fill  =  GridBagConstraints.BOTH;  
		constraint.gridwidth  = 1; 
		constraint.gridheight  = 1;
		constraint.gridx = 0;
		constraint.gridy = GridBagConstraints.RELATIVE;
		constraint.weightx  =  1.0;
		constraint.weighty  =  0.0;
		
		gridbag.setConstraints(makeSourcePanel(),  constraint);
		
	// Use LabelledFaders instead of PortFaders because we need
	// to control the ports of several different units.
		add( freqFader = new LabelledFader( this, 0, "Source Frequency",
											freqValue, MIN_CUTOFF, MAX_CUTOFF ) );
		freqFader.getFader().setTaper( CustomFaderDouble.EXPONENTIAL );
		gridbag.setConstraints(freqFader,  constraint);

		add( ampFader = new LabelledFader( this, 1, "Source Amplitude", ampValue, 0.001, 1.0) );
		ampFader.getFader().setTaper( CustomFaderDouble.EXPONENTIAL );
		gridbag.setConstraints(ampFader,  constraint);
		
		gridbag.setConstraints( makeFilterPanel(),  constraint);
		
		add( filterGUI = new Panel() );
		filterGUI.setLayout( new BorderLayout() );
		filterGUI.setBackground( filterColor );
		constraint.gridheight  = 4;
		constraint.weighty  =  1.0;
		gridbag.setConstraints(filterGUI,  constraint);
		
/* Create an oscilloscope to show each waveform. */
		add( scope = new SynthScope() );
		scope.createProbe( srcTap.output, "Source", Color.red );
		scope.createProbe( filterTap.output, "Filtered", Color.green );
		scope.finish();
		scope.hideControls();
			
		constraint.gridheight = GridBagConstraints.REMAINDER;  
		constraint.weighty  =  2.0;
		gridbag.setConstraints(scope,  constraint);
		
/* Synchronize Java display to make buttons appear. */
		getParent().validate();
		getToolkit().sync();
	}
	
/********************************************************************/		
	Panel makeSourcePanel( )
	{
		Panel panel = new Panel();
		add( panel );
		
		panel.add( new Label("Select Source:") );
		panel.add( sourceChoice = new Choice() );
			
		sourceChoice.addItem("WhiteNoise");
		sourceChoice.addItem("PinkNoise");
		sourceChoice.addItem("RedNoise");
		sourceChoice.addItem("Impulse");
		sourceChoice.addItem("Sawtooth");
		sourceChoice.addItem("SineWave");
		
   		sourceChoice.addItemListener(	new ItemListener() {
   				public void itemStateChanged(ItemEvent e)
   				{ selectSource( sourceChoice.getSelectedItem() );	} } );
		
		return panel;
	}
		
	void selectSource( String sourceName )
	{
		System.out.println("Source is " + sourceName );
		if( sourceName.equals( "WhiteNoise" ) ) useSource( whiteNoise, whiteNoise.amplitude, whiteNoise.output );
		else if ( sourceName.equals( "PinkNoise" ) ) useSource( pinkNoise, pinkNoise.amplitude, pinkNoise.output );
		else if ( sourceName.equals( "RedNoise" ) ) useSource( redNoise, redNoise.amplitude, redNoise.output );
		else if ( sourceName.equals( "Impulse" ) ) useSource( impulse, impulse.amplitude, impulse.output );
		else if ( sourceName.equals( "Sawtooth" ) ) useSource( sawtooth, sawtooth.amplitude, sawtooth.output );
		else if ( sourceName.equals( "SineWave" ) ) useSource( sineosc, sineosc.amplitude, sineosc.output );
		else System.out.println("Unrecognized choice!" + sourceName );
	}
	
/********************************************************************/		
	Panel makeFilterPanel( )
	{
		Panel panel = new Panel();
		add( panel );
		panel.setBackground( filterColor );
		panel.add( new Label("Select Filter:") );
		panel.add( filterChoice = new Choice() );
			
		filterChoice.addItem("LowPass");
		filterChoice.addItem("HighPass");
		filterChoice.addItem("BandPass");
		filterChoice.addItem("BandStop");
		filterChoice.addItem("PeakingEQ");
		filterChoice.addItem("LowShelf");
		filterChoice.addItem("HighShelf");
		filterChoice.addItem("StateVariable");
		filterChoice.addItem("BiQuad");
		filterChoice.addItem("OnePole");
		filterChoice.addItem("OneZero");
		filterChoice.addItem("OnePoleOneZero");
		filterChoice.addItem("TwoPole");
		
   		filterChoice.addItemListener(	new ItemListener() {
   				public void itemStateChanged(ItemEvent e)
   				{ selectFilter( filterChoice.getSelectedItem() );	} } );
				
		return panel;
	}
		
	void selectFilter( String sourceName )
	{
		if( sourceName.equals( "StateVariable" ) ) useFilter( funSVF );
		else if ( sourceName.equals( "BiQuad" ) ) useFilter( funBiquad );
		else if ( sourceName.equals( "OnePole" ) ) useFilter( fun1P );
		else if ( sourceName.equals( "OneZero" ) ) useFilter( fun1Z );
		else if ( sourceName.equals( "OnePoleOneZero" ) ) useFilter( fun1P1Z );
		else if ( sourceName.equals( "TwoPole" ) ) useFilter( fun2P );
		else if ( sourceName.equals( "LowPass" ) ) useFilter( funLowPass );
		else if ( sourceName.equals( "HighPass" ) ) useFilter( funHighPass );
		else if ( sourceName.equals( "BandPass" ) ) useFilter( funBandPass );
		else if ( sourceName.equals( "BandStop" ) ) useFilter( funBandStop );
		else if ( sourceName.equals( "PeakingEQ" ) ) useFilter( funPeakingEQ );
		else if ( sourceName.equals( "LowShelf" ) ) useFilter( funLowShelf );
		else if ( sourceName.equals( "HighShelf" ) ) useFilter( funHighShelf );
		else System.out.println("Unrecognized choice!" + sourceName );
	}
		
	void stopAll()
	{
		if( currentSource != null ) currentSource.stop();
		srcTap.stop();
		if( currentFilter != null ) currentFilter.stop();
		filterTap.stop();
	}
	
/** Start units in signal order so that scope does not show delays. */
	void startAll()
	{
		if( currentSource != null ) currentSource.start();
		srcTap.start();
		if( currentFilter != null ) currentFilter.start();
		filterTap.start();
	}
/********************************************************************/
	void useSource( SynthUnit unit, SynthInput amplitude, SynthOutput output )
	{
		stopAll();
		currentSource = unit;
		currentSourceAmplitude = amplitude;
		currentSourceOutput = output;
		if( currentFilter != null ) currentSourceOutput.connect( currentFilter.input );
		currentSourceOutput.connect( srcTap.inputA );
		freqFader.getFader().setEnabled( currentSource instanceof SynthOscillator);
		updateSource();
		startAll();
	}
	
/********************************************************************/
	void updateSource( )
	{
		if( currentSource == null ) return;
		if( currentSource instanceof SynthOscillator )
		{
			((SynthOscillator)currentSource).frequency.set( freqValue );
		}
		currentSourceAmplitude.set( ampValue );
	}
	
/********************************************************************/
	void useFilter( FunFilter funFilter )
	{
		stopAll();
		currentFilter = funFilter.filter;
		if( currentSourceOutput != null ) currentSourceOutput.connect( currentFilter.input );
		currentFilter.output.connect( filterTap.inputA );
		startAll();
		if( filterGUI != null )
		{
			filterGUI.removeAll();
			filterGUI.add( "Center", funFilter );
/* Synchronize Java display to make buttons appear. */
			filterGUI.validate();
			getToolkit().sync();
		}
	}

/********************************************************************/
	void buildSynth()
	{
		try
		{
			Synth.startEngine(0);

		/* Create unit generators. */
			whiteNoise = new WhiteNoise();
			pinkNoise = new PinkNoise();
			redNoise = new RedNoise();
			impulse = new ImpulseOscillator();
			sawtooth = new SawtoothOscillatorBL();
			sineosc = new SineOscillator();
			srcTap = new AddUnit();
			filterTap = new AddUnit();
			funBiquad = new FunBiquadFilter();
			funSVF = new FunSVFFilter();
			fun1Z = new Fun1ZFilter();
			fun1P = new Fun1PFilter();
			fun1P1Z = new Fun1P1ZFilter();
			fun2P = new Fun2PFilter();
			funLowPass = new FunLowPass();
			funHighPass = new FunHighPass();
			funBandPass = new FunBandPass();
			funBandStop = new FunBandStop();
			funPeakingEQ = new FunPeakingEQ();
			funLowShelf = new FunLowShelf();
			funHighShelf = new FunHighShelf();
			
			myOut = new LineOut();
			filterTap.output.connect( 0, myOut.input, 0 );
			filterTap.output.connect( 0, myOut.input, 1 );
			myOut.start();
			
		} catch(SynthException e) {
			SynthAlert.showError(this,e);
		}
	}

/********************************************************************/
	public void start()  
	{
		buildSynth();
		buildGUI();
		selectSource( sourceChoice.getSelectedItem() );
		selectFilter( filterChoice.getSelectedItem() );
	}

/********************************************************************/
	public void stop()  
	{
		try
		{
			whiteNoise.delete();
			pinkNoise.delete();
			redNoise.delete();
			impulse.delete();
			sawtooth.delete();
			sineosc.delete();
			srcTap.delete();
			filterTap.delete();
			funBiquad.delete();
			funSVF.delete();
			fun1Z.delete();
			fun1P.delete();
			fun1P1Z.delete();
			fun2P.delete();
			myOut.delete();
			removeAll();
			Synth.stopEngine();
		} catch(SynthException e) {
			SynthAlert.showError(this,e);
		}
	}

/********************************************************************
 ** Called by LabelledFader to apply new value. */
	public void tweak( int targetIndex, double val )
	{
		switch( targetIndex )
		{
		case 0:
			{
				freqValue = val;
				break;
			}
		case 1:
			{
				ampValue = val;
				break;
			}
		}
		updateSource();
	}
	
/*
 * Define classes of graphical components for each filter.
 */
/********************************************************************/
/******* FunFilter **************************************************/
/********************************************************************/
	abstract class FunFilter extends Panel
	{
		public SynthFilter filter;
		
		public FunFilter()
		{
			setLayout( new GridLayout( 0, 1 ) );
			add( new Label( getEquation(), Label.CENTER ) );
		}
	
		void delete()
		{
			filter.delete();
		}
		
		public abstract String getEquation();
		
	}
	
/********************************************************************/
/******* FunTunable *******************************************/
/********************************************************************/
	abstract class FunTunable extends FunFilter
	{
		PortFader        cutoffFader;
		PortFader        QFader;
				
		void setupFaders()
		{
			TunableFilter tf = (TunableFilter) filter;
			add( cutoffFader = new ExponentialPortFader( tf.frequency, DEF_CUTOFF, MIN_CUTOFF, MAX_CUTOFF ) );
			add( QFader = new PortFader( tf.Q, 1.0, 0.001, 10.0) );
		}
		public String getEquation()
		{
			return "y(n) = A0*x(n) + A1*x(n-1) + A2*x(n-2) - B1*y(n-1) - B2*y(n-2)";
		}
	}

	class FunLowPass extends FunTunable
	{
		public FunLowPass()
		{
			filter = new Filter_LowPass();
			setupFaders();
		}
	}
	class FunHighPass extends FunTunable
	{
		public FunHighPass()
		{
			filter = new Filter_HighPass();
			setupFaders();
		}
	}
	
	class FunBandPass extends FunTunable
	{
		public FunBandPass()
		{
			filter = new Filter_BandPass();
			setupFaders();
		}
	}
	
	class FunBandStop extends FunTunable
	{
		public FunBandStop()
		{
			filter = new Filter_BandStop();
			setupFaders();
		}
	}
	
	class FunPeakingEQ extends FunTunable
	{
		PortFader  gainFader;
		public FunPeakingEQ()
		{
			filter = new Filter_PeakingEQ();
			setupFaders();
			add( gainFader = new ExponentialPortFader( ((Filter_PeakingEQ)filter).gain, 1.0, 0.001, 10.0) );
		}
	}
	
	class FunLowShelf extends FunFilter
	{
		PortFader  cutoffFader;
		PortFader  gainFader;
		PortFader  slopeFader;
		
		public FunLowShelf()
		{
			filter = new Filter_LowShelf();
			add( cutoffFader = new ExponentialPortFader( ((Filter_LowShelf)filter).frequency, DEF_CUTOFF, MIN_CUTOFF, MAX_CUTOFF ) );
			add( gainFader = new ExponentialPortFader( ((Filter_LowShelf)filter).gain, 1.0, 0.001, 10.0) );
			add( slopeFader = new PortFader( ((Filter_LowShelf)filter).slope, 1.0, 0.001, 2.0) );
		}
		public String getEquation()
		{
			return "y(n) = A0*x(n) + A1*x(n-1) + A2*x(n-2) - B1*y(n-1) - B2*y(n-2)";
		}
	}
	class FunHighShelf extends FunFilter
	{
		PortFader  cutoffFader;
		PortFader  gainFader;
		PortFader  slopeFader;
		
		public FunHighShelf()
		{
			filter = new Filter_HighShelf();
			add( cutoffFader = new ExponentialPortFader( ((Filter_HighShelf)filter).frequency, DEF_CUTOFF, MIN_CUTOFF, MAX_CUTOFF ) );
			add( gainFader = new ExponentialPortFader( ((Filter_HighShelf)filter).gain, 1.0, 0.001, 10.0) );
			add( slopeFader = new PortFader( ((Filter_HighShelf)filter).slope, 1.0, 0.001, 2.0) );
		}
		public String getEquation()
		{
			return "y(n) = A0*x(n) + A1*x(n-1) + A2*x(n-2) - B1*y(n-1) - B2*y(n-2)";
		}
	}
/********************************************************************/
/******* FunTunable *******************************************/
/********************************************************************/
	abstract class FunParametric extends FunFilter  implements Tweakable
	{
		LabelledFader    cutoffFader;
		LabelledFader    widthFader;
		Choice           passChoice;
		
		public FunParametric()
		{
			add( cutoffFader = new LabelledFader( this, 0, "Cutoff Frequency", cutoffValue, MIN_CUTOFF, MAX_CUTOFF ) );
			add( widthFader = new LabelledFader( this, 1, "Bandwidth", bandwidthValue, 0.001, 0.5) );
			
/* The order of these must match the constants LOW_PASS, HIGH_PASS, BAND_PASS, PEAKING_EQ, etc. */
			add( passChoice = new Choice() );
			
			passChoice.addItem("Low Pass");
			passChoice.addItem("Band Pass");
			passChoice.addItem("High Pass");
   			passChoice.addItemListener(	new ItemListener() {
   				public void itemStateChanged(ItemEvent e)
   				{ setPass( passChoice.getSelectedIndex() );	} } );
		}
		

/********************************************************************
 ** Called by LabelledFader to apply new value. */
	public void tweak( int targetIndex, double val )
	{
		switch( targetIndex )
		{
		case 0:
			{
				cutoffValue = val;
				updateFilter();
				break;
			}
		case 1:
			{
				bandwidthValue = val;
				updateFilter();
				break;
			}
		case 2:
			{
				dBGainValue = val;
				updateFilter();
				break;
			}
		case 3:
			{
				slopeValue = val;
				updateFilter();
				break;
			}
		}
	}

	public abstract void setPass( int passIndex );
	public abstract void updateFilter();
	}

/*************************************************************************/
/*************************************************************************/
/*************************************************************************/
	class FunSVFFilter extends FunParametric  implements Tweakable
	{
		Filter_StateVariable svf;
		
		public FunSVFFilter()
		{
			this.filter = svf = new Filter_StateVariable();
			updateFilter();
		}
		public String getEquation()
		{
			return "State Variable Filter as described by Hal Chamberlain";
		}
		public void setPass( int passIndex )
		{
			switch( passIndex )
			{
				case LOW_PASS: svf.lowPass.connect( filterTap.inputA ); break;
				case BAND_PASS: svf.bandPass.connect( filterTap.inputA ); break;
				case HIGH_PASS: svf.highPass.connect( filterTap.inputA ); break;
			}
		}
/*
 * Update biquadFilter based on currently selected biquadFilter type (low,high,etc).
 * Either use methods in this lab class, or use methods in biquadFilter
 * for comparison.
 */
		public void updateFilter()
		{
			svf.frequency.set( cutoffValue );
			svf.resonance.set( bandwidthValue );
		}
	}
	
/*************************************************************************/
/*************************************************************************/
/*************************************************************************/
	class FunBiquadFilter extends FunParametric  implements Tweakable
	{
		LabelledFader    gainFader;
		LabelledFader    slopeFader;
		Filter_2o2p2z biquad;
		int passIndex = LOW_PASS;
		
		public FunBiquadFilter()
		{
			this.filter = biquad = new Filter_2o2p2z();
			add( gainFader = new LabelledFader( this, 2, "dB Gain", dBGainValue, -80.0, +80.0) );
			add( slopeFader = new LabelledFader( this, 3, "slope", slopeValue, 0.01, 2.0) );

			passChoice.addItem("Notch");
			passChoice.addItem("Peaking EQ");
			passChoice.addItem("Low Shelf");
			passChoice.addItem("High Shelf");
			updateFilter();
		}

		public String getEquation()
		{
			return "y(n) = 2.0 * (A0*x(n) + A1*x(n-1) + A2*x(n-2) - B1*y(n-1) - B2*y(n-2))";
		}
		
		public void setPass( int passIndex )
		{
			this.passIndex = passIndex;
			updateFilter();
		}

/*
 * Update biquadFilter based on currently selected biquadFilter type (low,high,etc).
 */
		public void updateFilter()
		{
			switch( passChoice.getSelectedIndex() )
			{
				case LOW_PASS:   biquad.lowPass( cutoffValue, bandwidthValue ); break;
				case BAND_PASS:  biquad.bandPass( cutoffValue, bandwidthValue ); break;
				case HIGH_PASS:  biquad.highPass( cutoffValue, bandwidthValue ); break;
				case NOTCH:      biquad.notch( cutoffValue, bandwidthValue ); break;
				case PEAKING_EQ: biquad.peakingEQ( cutoffValue, bandwidthValue, dBGainValue ); break;
				case LOW_SHELF:  biquad.lowShelf( cutoffValue, dBGainValue, slopeValue ); break;
				case HIGH_SHELF: biquad.highShelf( cutoffValue, dBGainValue, slopeValue ); break;
			}
		}
	}
	
/*************************************************************************/
/*************************************************************************/
/*************************************************************************/
	class Fun1ZFilter extends FunFilter
	{
		Filter_1o1z      f1o1z;
		
		public Fun1ZFilter()
		{
			this.filter = f1o1z = new Filter_1o1z();
			add( new PortFader( f1o1z.A0, 0.5, -1.0, 1.0 ) );
			add( new PortFader( f1o1z.A1, 0.5, -1.0, 1.0 ) );
		}
		public String getEquation()
		{
			return "y(n) = A0*x(n) + A1*x(n-1)";
		}
	}
	
/*************************************************************************/
/*************************************************************************/
/*************************************************************************/
	class Fun1PFilter extends FunFilter
	{
		Filter_1o1p      f1o1p;
		
		public Fun1PFilter()
		{
			this.filter = f1o1p = new Filter_1o1p();
			add( new PortFader( f1o1p.A0, 0.6, -1.0, 1.0 ) );
			add( new PortFader( f1o1p.B1, 0.3, -1.0, 1.0 ) );
		}
		public String getEquation()
		{
			return "y(n) = A0*x(n) - B1*y(n-1)";
		}
	}
	
/*************************************************************************/
/*************************************************************************/
/*************************************************************************/
	class Fun1P1ZFilter extends FunFilter
	{
		Filter_1o1p1z      fltr;
		
		public Fun1P1ZFilter()
		{
			this.filter = fltr = new Filter_1o1p1z();
			add( new PortFader( fltr.A0, 0.5, -1.0, 1.0 ) );
			add( new PortFader( fltr.A1, 0.2, -1.0, 1.0 ) );
			add( new PortFader( fltr.B1, 0.3, -1.0, 1.0 ) );
		}
		public String getEquation()
		{
			return "y(n) = A0*x(n) + A1*x(n-1) - B1*y(n-1)";
		}
	}
/*************************************************************************/
/*************************************************************************/
/*************************************************************************/
	class Fun2PFilter extends FunFilter
	{
		Filter_2o2p     fltr;
		
		public Fun2PFilter()
		{
			this.filter = fltr = new Filter_2o2p();
			add( new PortFader( fltr.A0, 0.5, -1.0, 1.0 ) );
			add( new PortFader( fltr.B1, 0.2, -1.0, 1.0 ) );
			add( new PortFader( fltr.B2, 0.3, -1.0, 1.0 ) );
		}
		public String getEquation()
		{
			return "y(n) = A0*x(n) - B1*y(n-1) - B2*y(n-2)";
		}
	}
}
