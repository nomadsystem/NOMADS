package com.softsynth.jsyn.examples;
import java.applet.Applet;
import java.awt.Button;
import java.awt.Event;
import java.awt.Label;
import java.util.Vector;

import com.softsynth.jsyn.AppletFrame;
import com.softsynth.jsyn.Synth;
import com.softsynth.jsyn.SynthAlert;
import com.softsynth.jsyn.SynthException;
import com.softsynth.jsyn.view102.UsageDisplay;

public class TJ_Noodler extends Applet
{
/* DO: Declare Synthesis Objects here */
	Vector myNoodlers;
	
	Button startOne, stopOne;
	Label       showNumNoodlers;
	
/* Can be run as either an application or as an applet. */
	public static void main(String args[])
	{
/* DO: Change MyJSynProgram to match the name of your class. Must match file name! */
	   TJ_Noodler  applet = new TJ_Noodler();
	   AppletFrame frame = new AppletFrame("Test JSyn", applet);
	   frame.resize(600,400);
	   frame.show();
 /* Begin test after frame opened so that DirectSound will use Java window. */
	   frame.test();
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

			myNoodlers = new Vector();
			
/* Create a button that starts a noodler */
			add( startOne = new Button("StartAnother") );
			add( stopOne = new Button("StopOne") );
			add( new UsageDisplay() );
			add( showNumNoodlers = new Label("Num = 0") );
			

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
/* Tell them all to stop before we stopEngine which would pull the rug out from under them. */
		for( int i=0; i<myNoodlers.size(); i++ )
		{
			Noodler nood = (Noodler) myNoodlers.elementAt(i);
			nood.die();
		}
		System.out.println("Wait for noodlers to finish.");
		while( !myNoodlers.isEmpty() )
		{
			Noodler nood = (Noodler) myNoodlers.firstElement();
			try
			{
				nood.join();
			} catch( InterruptedException e ) {
				System.err.println( "Caught " + e );
			}
			myNoodlers.removeElement( nood );
		}
		
		Synth.stopEngine();
   }
   	public void calcNewAmp()
	{
		int numNoodlers = myNoodlers.size();
		Noodler.setMaxAmp(1.0 / numNoodlers );
		showNumNoodlers.setText("Num = " + myNoodlers.size() );
	}
	
   	public boolean action(Event evt, Object what)
   	{
		int numNoodlers;
 		if( evt.target == startOne )
   		{
			/* call the noodler here */
			Noodler nood = new Noodler();
			myNoodlers.addElement( nood );
			calcNewAmp();
			nood.start();
   			return true;
   		}
		else if ( evt.target == stopOne )
		{
			numNoodlers = myNoodlers.size();
			if ( numNoodlers == 0 )
				return( true );
			int noodlerVictim = (int) (Math.random() * numNoodlers);
			Noodler nood = (Noodler) myNoodlers.elementAt( noodlerVictim );
			nood.die();
			myNoodlers.removeElement( nood );
			calcNewAmp();
			return(true);
		}
   		return false;
    }
}