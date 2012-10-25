//
//  TEST CLIENT - DISCUSS
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

public class DiscussTest implements Runnable {   

    private class NomadsAppThread extends Thread {
	DiscussTest client; //Replace with current class name

	public NomadsAppThread(DiscussTest _client) {
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

	DiscussTest  applet = null;
	applet = new DiscussTest();

    }

    public void init()
    {  	

	int i;

	NGlobals.cPrint("DiscussTest -> init()");

	discussTestSand = new NSand(); 
	discussTestSand.connect();

	int d[] = new int[1];
	d[0] = 0;

	nThread = new NomadsAppThread(this);
	nThread.start();

	discussTestSand.sendGrain((byte)NAppID.OPERA_CLIENT, (byte)NCommand.REGISTER, (byte)NDataType.UINT8, 1, d );
	



    }	


    public DiscussTest() {  	    
	randNum = new Random();
	init();
	start();
    }




    public void start()  {  
	NGlobals.cPrint("DiscussTest -> start()");

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
	NGlobals.cPrint("DiscussTest -> run()");
	int i = 0;

	while (true) {
	    String tString;
	    if (i>500) {
		tString = new String("RESETTING DISCUSS TEXT TAG TO 0");
		i=0;
	    }
	    else {
		tString = new String("DISCUSSION TEST " + i++);
	    }
	    int tLen = tString.length();
	    byte[] tBytes = tString.getBytes();

	    try {
		NGlobals.cPrint("DiscussTest -> NSand.send()");
		discussTestSand.sendGrain((byte)NAppID.OC_DISCUSS, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.CHAR, tLen, tBytes );
		runner.sleep(5000);
	    }
	    catch (InterruptedException ie) {}

	}

    }

    public void handle() {

	NGrain grain;

	NGlobals.cPrint("DiscussTest -> handle()");

	grain = discussTestSand.getGrain();
	grain.print();
	System.out.println("DiscussTest handle()");

	    
    }



}
