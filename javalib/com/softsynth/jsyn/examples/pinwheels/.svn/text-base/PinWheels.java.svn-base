/**
 * Rotational Transformation of Musical Information
 *
 * Based on an HMSL piece written for the Amiga.
 * @author (C) 1999 Phil Burk, All Rights Reserved
 */

package com.softsynth.jsyn.examples.pinwheels;
import java.util.*;
import java.awt.*;
import java.applet.Applet;
import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view102.*;
import com.softsynth.jsyn.circuits.*;
import com.softsynth.jsyn.util.*;

/* ============================================================ */
class XYPointDisplay extends XYController
{
	double       xData[];
	double       yData[];
	int          numValues;
	int          radius = 8;
		
	public XYPointDisplay( double xData[], double yData[] )
	{
		setArrays( xData, yData );
	}
/** Specify array to be edited by this controller. */
	public void setArrays( double xData[], double yData[] )
	{
		this.xData = xData;
		this.yData = yData;
		numValues = xData.length;
	}

/* Override default paint action. */
	public void paint( Graphics g )
	{
		int width = bounds().width;
		int height = bounds().height;

// draw background and erase all values
		g.setColor( getBackground() );
		g.fillRect(0, 0, width, height );
		
// lines between points
		g.setColor( getForeground() );

			int x1,y1, x2,y2;
		x1 = convertWXtoGX( xData[0] );
		y1 = convertWYtoGY( yData[0] );
		for( int i=1; i<numValues; i++ )
		{
			g.fillOval( x1 - radius, y1 - radius, radius*2, radius*2 );
			x2 = convertWXtoGX( xData[i] );
			y2 = convertWYtoGY( yData[i] );
			g.drawLine( x1, y1, x2, y2 );
			x1 = x2;
			y1 = y2;
		}
		g.fillOval( x1 - radius, y1 - radius, radius*2, radius*2 );
	}
}
/* ============================================================ */
/** Spin a shape.
 */
class ContourSpinner extends Panel implements Runnable, CustomFaderListener
{
	int              numPoints;
	double           xData[];
	double           yData[];
	double           xSortedData[];
	double           ySortedData[];
	XYPointDisplay    xyDisplay;
	Thread           spinThread;
	boolean          go = false;
	CustomFader      speedBar;
	Button           randomButton;
	double           rotationalIncrement = 0.0;
	static final int SPEED_LEVELS = 20;
	
	public ContourSpinner( int numPoints )
	{
		this.numPoints = numPoints;
		xData = new double[numPoints];
		yData = new double[numPoints];
		xSortedData = new double[numPoints];
		ySortedData = new double[numPoints];
		
		makeRandomPoints( numPoints );
		setLayout( new BorderLayout() );
		add( xyDisplay = new XYPointDisplay( xSortedData, ySortedData ), "Center" );
		Panel panel = new Panel();
		add( panel, "South" );
		panel.setLayout( new GridLayout( 0,1 ) );
		panel.add( speedBar = new CustomFader( CustomFader.HORIZONTAL, SPEED_LEVELS, 1, 0, SPEED_LEVELS*2  ) );
		speedBar.addCustomFaderListener( this );
		panel.add( randomButton = new Button("Random") );
		xyDisplay.setMinWorldX(-1.0 );
		xyDisplay.setMaxWorldX( 1.0 );
		xyDisplay.setMinWorldY(-1.0 );
		xyDisplay.setMaxWorldY( 1.0 );
	}
	
	void setPoint( int index, double x, double y )
	{
		synchronized( xData )
		{
			xData[index] = x;
			yData[index] = y;
		}
	}
	
	void makeRandomPoints( int numPoints )
	{
		double range = 1.0 / Math.sqrt(2.0);
		for( int i=0; i<numPoints; i++ )
		{
			xData[i] = (Math.random() * 2.0 * range) - range;
			yData[i] = (Math.random() * 2.0 * range) - range;
		}
		sortDataByX();
		if( xyDisplay != null) xyDisplay.repaint();
	}
	
	void sortDataByX()
	{
		synchronized( xData )
		{
/* Use insertion sort because I'm lazy. */
			for( int j=0; j<xData.length; j++ )
			{
				int place;
				double xnew = xData[j];
/* Look for place to put new data. */
				for( place=0; place<j; place++ )
				{
					if( xnew < xSortedData[place] )
					{
						break;
					}
				}
/* Move existing data up. */
				for( int i=j; i>place; i-- )
				{
					xSortedData[i] = xSortedData[i-1];
					ySortedData[i] = ySortedData[i-1];
				}
				xSortedData[place] = xnew;
				ySortedData[place] = yData[j];
			}
		}
	}
	
	public double[] getSortedXY()
	{
		double[] dar;
		synchronized( xData )
		{
			dar = new double[2*xData.length];
			for( int i=0; i<xData.length; i++ )
			{
				dar[i*2] = xSortedData[i];
				dar[(i*2)+1] = ySortedData[i];
			}
		}
		return dar;
	}
	
/* Should the thread keep running? */
	boolean keepRunning()
	{
		return( Thread.currentThread() == spinThread );
	}
	
	public synchronized void start()
	{	
		System.out.println("start: got semaphore ");
		
/* Start thread that plays "music". */
		if( spinThread == null )
		{
			spinThread = new Thread( this );
			go = true;
			spinThread.start();
		}
		else
		{
			System.err.println("start() - thread already started!");
		}
	}
	
/* ------------------------------- */
	public synchronized void stop()
	{
		go = false;
		spinThread = null;
	}
	
	public void rotatePoints( double angle )
	{
		double x, y;
		if( angle == 0.0 ) return;
		double sin = Math.sin( angle );
		double cos = Math.cos( angle );
		for( int i=0; i<xData.length; i++ )
		{
			x = xData[i];
			y = yData[i];
			xData[i] = (x * cos) - (y * sin);
			yData[i] = (x * sin) + (y * cos);
		}
		sortDataByX();
		xyDisplay.repaint();
	}
	
	public void run()
	{
		while( go )
		{
			rotatePoints( rotationalIncrement );
			try
			{
				Thread.sleep( 100 );
			} catch( InterruptedException e ) {
				System.err.println( e );
			}
		}
	}
	

	public void customFaderValueChanged(Object jsb, int value )
	{
		if (jsb == speedBar)
		{
			int val = value - SPEED_LEVELS;
			rotationalIncrement = (val*val) * (-0.02/SPEED_LEVELS);
			if( val < 0 ) rotationalIncrement = -rotationalIncrement;
		}
	}
		
	public boolean action(Event evt, Object what)
	{
		if( evt.target == randomButton )
		{
			makeRandomPoints( numPoints );
			repaint();

			Runtime.getRuntime().gc();  // make sure we are referencing all objects

			return true;
		}
		return false;
    }
}

/* ============================================================ */
class PitchSpinner extends ContourSpinner
{
	static final double PITCH_SCALAR = 12.0 * Math.sqrt(2.0);
	static int[] pitches = { 0, 2, 4, 7, 9, 12, 14, 16, 19, 21, 24 };	// two octave pentatonic scale
	static final EqualTemperedTuning tuning = new EqualTemperedTuning( 293.3 );

	public PitchSpinner( int numPoints )
	{
		super( numPoints );
	}
	
	void makeRandomPoints( int numPoints )
	{
		double range = 1.0 / Math.sqrt(2.0);
		for( int i=0; i<numPoints; i++ )
		{
	// quantize time
			xData[i] = (( ((int) (16.0 * Math.random())/16.0) * 2.0 * range) - range);
	// select pitches from a two octave set
			yData[i] = (pitches[(int) (Math.random() * pitches.length)] - 12) / PITCH_SCALAR;
		}
		sortDataByX();
		if( xyDisplay != null) xyDisplay.repaint();
	}
	
	double yToFrequency( double yval )
	{
		return tuning.getFrequency( (yval * PITCH_SCALAR) + 12 );
	}
}

/* ============================================================ */
class SpinnerThread extends Thread 
{
	static final double     MAX_DELAY = 0.100;
	static final int        MAX_NUM_VOICES = 6;
	PitchSpinner            spinner;
	public BussedVoiceAllocator    allocator;
	boolean                 go = true;

/*
 * Setup synthesis.
 */
	public SpinnerThread( 	PitchSpinner   spinner )  throws SynthException
	{
		this.spinner = spinner;
		
/* Create an allocator for several voices.
 * Define abstract makeVoice() methog so class can be instantiated.
 */
		allocator = new BussedVoiceAllocator( MAX_NUM_VOICES )
			{
				public SynthCircuit makeVoice() throws SynthException
				{
					SynthNote circ = new com.softsynth.jsyn.circuits.FilteredSawtoothBL();
					return addVoiceToMix( circ ); // mix through bus writer
				}
			};
	}
		
	public void run()
	{
		int msec;
		while( go )
		{
			try
			{
				msec = playPitches( );
			} catch( SynthException e )
			{
				System.err.println( e );
				return;
			}
			try
			{
				Thread.sleep( msec );
			} catch( InterruptedException e )
			{
				System.err.println( e );
				return;
			}
		}
	}
	
	public void kill()
	{
		go = false;
	}
					
	int playPitches( ) throws SynthException
	{
		double x1, x2;
		int delay1 = 0;
		int delay2 = 0;
		int duration = 100;
		double dar[] = spinner.getSortedXY();
		double timeScalar = Synth.getTickRate()*1.0;
		double firstX = x2 = dar[0];
		int now = Synth.getTickCount();
		for( int i=0; i<dar.length/2; i++ )
		{
			x1 = x2;
			delay1 = delay2;
			if( i < (dar.length/2)-1 )
			{
				x2 = dar[(i+1)*2];
				delay2 = (int) ((x2-firstX)*timeScalar);
				duration = (delay2 - delay1) / 2;
			}
			
			double y = dar[i*2+1];
			double freq = spinner.yToFrequency(y);
			
			int nextTime = now + delay1;
			
//			System.out.println("y = " + y + ", freq = " + freq + ", delay1 = " + delay1 );
			SynthNote voice = (SynthNote) allocator.steal( nextTime );
			if( voice == null )
			{
				System.err.println("Couldn't allocate voice.");
			}
			else
			{
				voice.note( nextTime, duration, freq, 1.0 / MAX_NUM_VOICES );
			}
		}
		return (int)(2.0 * (1000.0*delay2/Synth.getTickRate()));
	}
	
}


/* ============================================================ */
class RadioButtonPanel extends Panel
{
	Checkbox[] boxes;
	
	public RadioButtonPanel( int numBoxes )
	{
		setLayout( new GridLayout( 1,0 ) );
		boxes = new Checkbox[ numBoxes ];
		CheckboxGroup cbg = new CheckboxGroup();
		for( int i=0; i<numBoxes; i++ )
		{
			add( boxes[i] = new Checkbox( Integer.toString(i), cbg, (i==0) ) );
		}
	}
	
/** @return the Nth checkbox in the panel */
	public Checkbox getNthBox( int N )
	{
		return boxes[N];
	}
	
	public int getSelection()
	{
		for( int i=0; i<boxes.length; i++ )
		{
			if( boxes[i].getState() )
			{
				return i;
			}
		}
		return 0;
	}
}

/* ============================================================ */
public class PinWheels extends Applet
{
	PitchSpinner   spinner;
	SpinnerThread  spun;
	LineOut        unitOut;
	
/* Can be run as either an application or as an applet. */
    public static void main(String args[])
	{
		PinWheels applet = new PinWheels();
		AppletFrame frame = new AppletFrame("PinWheel", applet);
		frame.resize(550,500);
		frame.show();
		frame.test();
	}
	
	public void start()
	{
		setLayout( new BorderLayout() );
		spinner = new PitchSpinner( 8 );	
		add( new InsetPanel( spinner, Color.yellow ), "Center"  );
		spinner.start();
		try
		{
/* Start synthesis engine. */
			Synth.startEngine( 0, Synth.DEFAULT_FRAME_RATE );
			unitOut = new LineOut( );
			spun = new SpinnerThread( spinner );
			spun.allocator.output.connect( 0, unitOut.input, 0 );
			spun.allocator.output.connect( 0, unitOut.input, 1 );
			spun.start();
			unitOut.start();

			Synth.setTrace( Synth.SILENT );
/*			
			Panel panel = new Panel();
			panel.setLayout( new GridLayout( 2, 2 ) );
			panel.add( new PortFader( spun.amplitude, 0.0, 0.0, 1.0 ) );
			add( panel, "North" );
*/
		} catch (SynthException e) {
			SynthAlert.showError(this,e);
		}
/* Synchronize Java display. */
		getParent().validate();
		getToolkit().sync();
		
	}
	
	public void stop()
	{
		try
		{
			spun.kill();
			spinner.stop();
			Synth.stopEngine();
		} catch (SynthException e) {
			System.err.println("Caught " + e);
		}
	}
}
