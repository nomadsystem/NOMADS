/**
 * FilterLab - experiment with digital filters.
 * This was used to experiment with various filter coefficient equations.
 *
 * <P>Special thanks to Robert Bristow-Johnson for contributing his filter equations to the music-dsp list.
 *
 * @author (C) 1998 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.*;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view102.LabelledFader;
import com.softsynth.jsyn.view102.Tweakable;

public class FilterLab extends Applet implements Tweakable
{
	WhiteNoise       noise;
	Filter_2o2p2z    filter;
	LineOut          myOut;
	
	TextField        A0_Field;
	TextField        A1_Field;
	TextField        A2_Field;
	TextField        B1_Field;
	TextField        B2_Field;
	LabelledFader    freqFader;
	LabelledFader    widthFader;
	LabelledFader    gainFader;
	Label            omegaLabel;

	double           omega = 0.0;
	double           cs = 0.0;
	double           sn = 0.0;
	double           Q = 0.0;
	double           alpha = 0.0;

	double           A0_Value;
	double           A1_Value;
	double           A2_Value;
	double           B1_Value;
	double           B2_Value; 
	double           Freq_Value = 400.0;
	double           Bandwidth_Value = 0.2; 
	double           Gain_Value = 0.0; 

	Button           useFieldsButton;
	Checkbox         localBox;
	Choice           filterChoice;
/* The order of these must match the items in filterChoice */
	final static int LOW_PASS = 0;
	final static int HIGH_PASS = 1;
	final static int BAND_PASS = 2;
	final static int PEAKING_EQ = 3;
	
/* Can be run as either an application or as an applet. */
	public static void main(String args[])
	{
/* DO: Change FilterLab to match the name of your class. Must match file name! */
	   FilterLab  applet = new FilterLab();
	   AppletFrame frame = new AppletFrame("Test JSyn Filter_2o2p2z", applet);
	   frame.resize(700,500);
	   frame.show();
 /* Begin test after frame opened so that DirectSound will use Java window. */
	   frame.test();
	}

	void buildGUI()
	{
		Panel subPanel;

/* Build GUI */
		setLayout( new GridLayout( 0, 1) );

		add( freqFader = new LabelledFader( this, 0, "Frequency", Freq_Value, 0.1, 10000.0 ) );
		add( widthFader = new LabelledFader( this, 1, "Bandwidth", Bandwidth_Value, 0.001, 0.5) );
		add( widthFader = new LabelledFader( this, 2, "dBGain", Gain_Value, -60.0, 30.0) );

		add( subPanel = new Panel() );
		subPanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
		subPanel.add( new Label("A0:") );
		subPanel.add( A0_Field = new TextField("0.0",30) );

		add( subPanel = new Panel() );
		subPanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
		subPanel.add( new Label("A1:") );
		subPanel.add( A1_Field = new TextField("0.0",30) );

		add( subPanel = new Panel() );
		subPanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
		subPanel.add( new Label("A2:") );
		subPanel.add( A2_Field = new TextField("0.0",30) );

		add( subPanel = new Panel() );
		subPanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
		subPanel.add( new Label("B1:") );
		subPanel.add( B1_Field = new TextField("0.0",30) );

		add( subPanel = new Panel() );
		subPanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
		subPanel.add( new Label("B2:") );
		subPanel.add( B2_Field = new TextField("0.0",30) );

		add( omegaLabel = new Label("Internal values.") );

		
/* Add Buttons. */
		add( subPanel = new Panel() );
		subPanel.add( useFieldsButton = new Button("Use A & B fields.") );
		subPanel.add( localBox = new Checkbox("Use local methods.") );

/* The order of these must match the constants LOW_PASS, HIGH_PASS, BAND_PASS. */
		subPanel.add( filterChoice = new Choice() );
		filterChoice.addItem("LowPass");
		filterChoice.addItem("HighPass");
		filterChoice.addItem("BandPass");
		filterChoice.addItem("PeakingEQ");
	}
 /*
  * Setup synthesis by overriding start() method.
  */
	public void start()  
	{
	   try
	   {
		  Synth.startEngine(0);
 /* DO: Your setup code goes here. ******************/
 /* Create unit generators. */
			noise = new WhiteNoise()   ;
			filter = new Filter_2o2p2z();
			myOut = new LineOut();

/* Connect units together. */
			noise.output.connect( filter.input );
			filter.output.connect( myOut.input );
			
			buildGUI();

			noise.amplitude.set(0.2);

			updateFilter();

/* Start units. */
			noise.start();
			filter.start();
			myOut.start();
/* *****************************************/

/* Synchronize Java display to make buttons appear. */
			getParent().validate();
			getToolkit().sync();
			
	   } catch(SynthException e) {
		  SynthAlert.showError(this,e);
	   }
	}

/*
 * Clean up synthesis by overriding stop() method.
 */
	public void stop()  
	{
	   try
	   {
 /* Your cleanup code goes here. */
		  Synth.stopEngine();
	   } catch(SynthException e) {
		  SynthAlert.showError(this,e);
	   }
	}

/* Called by LabelledFader to apply new value. */
	public void tweak( int targetIndex, double val )
	{
		switch( targetIndex )
		{
		case 0: Freq_Value = val; break;
		case 1: Bandwidth_Value = val; break;
		case 2: Gain_Value = val; break;
		}
		updateFilter();
	}

/* Read filter coefficients from TextField. */
   	void getNumbers() throws NumberFormatException
	{
		A0_Value = Double.valueOf( A0_Field.getText() ).doubleValue();
		A1_Value = Double.valueOf( A1_Field.getText() ).doubleValue();
		A2_Value = Double.valueOf( A2_Field.getText() ).doubleValue();
		B1_Value = Double.valueOf( B1_Field.getText() ).doubleValue();
		B2_Value = Double.valueOf( B2_Field.getText() ).doubleValue();
	}

/* Set text in coefficient fileds to match newly calculated values. */
	void updateCoefficientFields()
	{
		A0_Field.setText( Double.toString( A0_Value ) );
		A1_Field.setText( Double.toString( A1_Value ) );
		A2_Field.setText( Double.toString( A2_Value ) );
		B1_Field.setText( Double.toString( B1_Value ) );
		B2_Field.setText( Double.toString( B2_Value ) );
		omegaLabel.setText( "omega = " + omega +
			",  cos(omega) = " + cs +
			",  Q = " + Q +
			",  alpha = " + alpha
			);
	}

/* Set coefficients to current values. */
	void setFilterCoefficients() throws SynthException
	{
		filter.A0.set( A0_Value );
		filter.A1.set( A1_Value );
		filter.A2.set( A2_Value );
		filter.B1.set( B1_Value );
		filter.B2.set( B2_Value );
	}

/* This function wasn't in Math so do it here. */
	double sinh( double x )
	{
		return (0.5 * (Math.exp(x) - Math.exp(-x)));
	}

/* Calculate common variables used in every filter. */
	void calcCommon( double freq, double bandwidth )
	{
		double ratio = freq / Synth.getFrameRate();
		if( ratio >= 0.499 ) ratio = 0.499; /* Don't get close to Nyquist because it will blow up. */
		omega = 2.0 * Math.PI * ratio;
		cs = Math.cos( omega );
		sn = Math.sin( omega );
		Q = sn / (Math.log(2.0) * bandwidth * omega );
		alpha = sn * sinh( 0.5 / Q );
	}

/* The calculations are based on the following filter implementation. */
/*    y[n] = (c0/d0)*x[n] + (c1/d0)*x[n-1] + (c2/d0)*x[n-2] - (d1/d0)*y[n-1] - (d2/d0)*y[n-2] */

/* Calculate LowPass filter coefficients. */
	void lowPass( double freq, double bandwidth )
	{
		calcCommon( freq, bandwidth );

		double c0 = (1.0 - cs) * 0.5;
		double c1 = (1.0 - cs);
		double c2 = (1.0 - cs) * 0.5;
		double d0 =  1.0 + alpha;
		double d1 = -2.0 * cs;
		double d2 =  1.0 - alpha;

		double temp = 0.5 / d0;
		A0_Value = c0 * temp;
		A1_Value = c1 * temp;
		A2_Value = c2 * temp;
		B1_Value = d1 * temp;
		B2_Value = d2 * temp;
	}

/* Calculate highPass filter coefficients. */
	void highPass( double freq, double bandwidth )
	{
		calcCommon( freq, bandwidth );

		double c0 = (1.0 + cs) * 0.5;
		double c1 = (-1.0 - cs);
		double c2 = (1.0 + cs) * 0.5;
		double d0 =  1.0 + alpha;
		double d1 = -2.0 * cs;
		double d2 =  1.0 - alpha;

		double temp = 0.5 / d0;
		A0_Value = c0 * temp;
		A1_Value = c1 * temp;
		A2_Value = c2 * temp;
		B1_Value = d1 * temp;
		B2_Value = d2 * temp;
	}
				
/* Calculate bandPass filter coefficients. */
	void bandPass( double freq, double bandwidth )
	{
		calcCommon( freq, bandwidth );

		double c0 = alpha;
		double c1 = 0.0;
		double c2 = -alpha;
		double d0 =  1.0 + alpha;
		double d1 = -2.0 * cs;
		double d2 =  1.0 - alpha;

		double temp = 0.5 / d0;
		A0_Value = c0 * temp;
		A1_Value = c1 * temp;
		A2_Value = c2 * temp;
		B1_Value = d1 * temp;
		B2_Value = d2 * temp;
	}

/*
 * Update filter based on currently selected filter type (low,high,etc).
 * Either use methods in this lab class, or use methods in filter
 * for comparison.
 */
	void updateFilter()
	{
   		try
   		{
			if( localBox.getState() )
			{
				switch( filterChoice.getSelectedIndex() )
				{
				case LOW_PASS: lowPass( Freq_Value, Bandwidth_Value ); break;
				case HIGH_PASS: highPass( Freq_Value, Bandwidth_Value ); break;
				case BAND_PASS: bandPass( Freq_Value, Bandwidth_Value ); break;
				// FIXME
				}
				updateCoefficientFields();
				setFilterCoefficients();
			}
			else
			{
				switch( filterChoice.getSelectedIndex() )
				{
				case LOW_PASS: filter.lowPass( Freq_Value, Bandwidth_Value ); break;
				case HIGH_PASS: filter.highPass( Freq_Value, Bandwidth_Value ); break;
				case BAND_PASS: filter.bandPass( Freq_Value, Bandwidth_Value ); break;
				case PEAKING_EQ: filter.peakingEQ( Freq_Value, Bandwidth_Value, Gain_Value ); break;
				}
			}
   		} catch (SynthException e) {
   			SynthAlert.showError(this,e);
   		}
	}

/* Process used input. */
   	public boolean action(Event evt, Object what)
   	{
   		try
   		{
/* Apply the coefficients currently in the test fields to filter. */
			if( evt.target == useFieldsButton )
   			{
				getNumbers();
				setFilterCoefficients();
				return true;
   			}
/* Update filter based on chosen methods. */
			else if( (evt.target == localBox) || (evt.target == filterChoice) )
   			{
				updateFilter();
				return true;
   			}
   		} catch (SynthException e) {
   			SynthAlert.showError(this,e);
   			return true;
   		}
   		return false;
    }
}