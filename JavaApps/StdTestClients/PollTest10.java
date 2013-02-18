//
//  TEST CLIENT - SWARM
//

import java.awt.*;
import java.lang.Math;
import java.applet.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.*;
import java.io.*;
import nomads.v210.*;

public class PollTest10 implements Runnable {   

    private class NomadsAppThread extends Thread {
	PollTest10 client; //Replace with current class name

	public NomadsAppThread(PollTest10 _client) {
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

    NSand pollTestSand;
    private NomadsAppThread nThread;

    Random randNum;

    URL imgWebBase;

    Thread runner;

    public static void main(String args[])
    {

	PollTest10  applet = null;
	applet = new PollTest10();

    }

    public void init()
    {  	

	int i;

	NGlobals.cPrint("PollTest10 -> init()");

	pollTestSand = new NSand(); 
	pollTestSand.connect();

	int d[] = new int[1];
	d[0] = 0;

	nThread = new NomadsAppThread(this);
	nThread.start();

	pollTestSand.sendGrain((byte)NAppID.BINDLE, (byte)NCommand.REGISTER, (byte)NDataType.UINT8, 1, d );

	String tString = new String("testPoll10");
	int tLen = tString.length();

	byte[] tStringAsBytes = tString.getBytes();

	pollTestSand.sendGrain((byte)NAppID.BINDLE, (byte)NCommand.LOGIN, (byte)NDataType.UINT8, tLen, tStringAsBytes );
	



    }	


    public PollTest10() {  	    
	randNum = new Random();
	init();
	start();
    }




    public void start()  {  
	NGlobals.cPrint("PollTest10 -> start()");

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
	int i = 0;
	int pollRes;

	while (true) {

	    int[] pollD = new int[1];

	    pollRes = randNum.nextInt(10)+1;

	    pollD[0] = pollRes;

	    try {
		pollTestSand.sendGrain((byte)NAppID.STUDENT_POLL, (byte)NCommand.QUESTION_TYPE_ONE_TO_TEN, (byte)NDataType.INT32, 1, pollD );
		runner.sleep(1000);
	    }
	    catch (InterruptedException ie) {}

	}

    }

    public void handle() {

	NGrain grain;

	NGlobals.cPrint("PollTest10 -> handle()");

	grain = pollTestSand.getGrain();
	grain.print();
	System.out.println("PollTest10 handle()");

	    
    }



}
