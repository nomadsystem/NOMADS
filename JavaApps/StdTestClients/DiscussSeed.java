//
//  TEST CLIENT - DISCUSS SEED TEXT
//

import java.awt.*;
import java.lang.Math;
import java.applet.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.*;
import java.io.*;
import nomads.v210_auk.*;

public class DiscussSeed implements Runnable {   

    private class NomadsAppThread extends Thread {
	DiscussSeed client; //Replace with current class name

	public NomadsAppThread(DiscussSeed _client) {
	    client = _client;
	}
	public void run()    {			
	    NGlobals.lPrint("NomadsAppThread -> run()");
	    while (true)  {
		client.handle();
	    }
	}
    }

    private Thread       thread = null;

    NSand discussTestSand;
    private NomadsAppThread nThread;

    Random randNum;

    URL imgWebBase;

    Thread runner;

    public static void main(String args[])
    {

	DiscussSeed  applet = null;
	applet = new DiscussSeed();

    }

    public void init()
    {  	

	int i;

	NGlobals.cPrint("DiscussSeed -> init()");

	discussTestSand = new NSand(); 
	discussTestSand.connect();

	int d[] = new int[1];
	d[0] = 0;

	nThread = new NomadsAppThread(this);
	nThread.start();

	discussTestSand.sendGrain((byte)NAppID.OPERA_CLIENT, (byte)NCommand.REGISTER, (byte)NDataType.UINT8, 1, d );
	



    }	


    public DiscussSeed() {  	    
	randNum = new Random();
	init();
	start();
    }




    public void start()  {  
	NGlobals.cPrint("DiscussSeed -> start()");

	runner = new Thread(this);
	runner.start();

    }

    public void stop() {  
	if (thread != null)  {  
	    thread.stop(); 
	    thread = null;
	}
    }

    public void run () {
	NGlobals.cPrint("DiscussSeed -> run()");
	int i = 0;
	
	String tString;
	int tLen;
	byte[] tBytes;

	while (true) {
	    try {
		tString = new String("Welcome to Auksalaq NOMADS.");
		 tLen = tString.length();
		 tBytes = tString.getBytes();
		discussTestSand.sendGrain((byte)NAppID.OC_DISCUSS, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.CHAR, tLen, tBytes );
		runner.sleep(1000);
		tString = new String("NOMADS is an interactive system");
		tLen = tString.length();
		 tBytes = tString.getBytes();
		discussTestSand.sendGrain((byte)NAppID.OC_DISCUSS, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.CHAR, tLen, tBytes );
		runner.sleep(1000);
		tString = new String("designed to facilitate and augment");
		tLen = tString.length();
		 tBytes = tString.getBytes();
		discussTestSand.sendGrain((byte)NAppID.OC_DISCUSS, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.CHAR, tLen, tBytes );
		runner.sleep(1000);
		tString = new String("group collaboration and participation");
		tLen = tString.length();
		 tBytes = tString.getBytes();
		discussTestSand.sendGrain((byte)NAppID.OC_DISCUSS, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.CHAR, tLen, tBytes );
		runner.sleep(1000);
		tString = new String("through web and mobile device based applications.");
		tLen = tString.length();
		 tBytes = tString.getBytes();
		discussTestSand.sendGrain((byte)NAppID.OC_DISCUSS, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.CHAR, tLen, tBytes );
		runner.sleep(1000);
		tString = new String("");
		tLen = tString.length();
		 tBytes = tString.getBytes();
		discussTestSand.sendGrain((byte)NAppID.OC_DISCUSS, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.CHAR, tLen, tBytes );
		runner.sleep(5000);
		tString = new String("This instance of NOMADS has been designed");
		tLen = tString.length();
		 tBytes = tString.getBytes();
		discussTestSand.sendGrain((byte)NAppID.OC_DISCUSS, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.CHAR, tLen, tBytes );
		runner.sleep(1000);
		tString = new String("specifically for the multimedia opera, Auksalaq.");
		tLen = tString.length();
		 tBytes = tString.getBytes();
		discussTestSand.sendGrain((byte)NAppID.OC_DISCUSS, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.CHAR, tLen, tBytes );
		runner.sleep(1000);
		tString = new String("");
		tLen = tString.length();
		 tBytes = tString.getBytes();
		discussTestSand.sendGrain((byte)NAppID.OC_DISCUSS, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.CHAR, tLen, tBytes );
		runner.sleep(5000);
		tString = new String("For more information, please visit:");
		tLen = tString.length();
		 tBytes = tString.getBytes();
		discussTestSand.sendGrain((byte)NAppID.OC_DISCUSS, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.CHAR, tLen, tBytes );
		runner.sleep(1000);
		tString = new String("http://nomads.music.virginia.edu");
		tLen = tString.length();
		 tBytes = tString.getBytes();
		discussTestSand.sendGrain((byte)NAppID.OC_DISCUSS, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.CHAR, tLen, tBytes );
		runner.sleep(1000);
		tString = new String("");
		tLen = tString.length();
		 tBytes = tString.getBytes();
		discussTestSand.sendGrain((byte)NAppID.OC_DISCUSS, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.CHAR, tLen, tBytes );
		runner.sleep(5000);
		
	    }
	    
	    
	    catch (InterruptedException ie) {}
	    
	}
	
    }

    public void handle() {

	NGrain grain;

	NGlobals.cPrint("DiscussSeed -> handle()");

	grain = discussTestSand.getGrain();
	grain.print();
	System.out.println("DiscussSeed handle()");

	    
    }



}
