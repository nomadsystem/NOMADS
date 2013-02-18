//
//  TEST CLIENT - CLOUD
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

public class CloudTest implements Runnable {   

    private class NomadsAppThread extends Thread {
	CloudTest client; //Replace with current class name

	public NomadsAppThread(CloudTest _client) {
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

    NSand cloudTestSand;
    private NomadsAppThread nThread;

    Random randNum;

    URL imgWebBase;

    Thread runner;

    String cloudString[] = new String[31];

    int tNum, tNumL;

    public static void main(String args[])
    {

	CloudTest  applet = null;
	applet = new CloudTest();

    }

    public void init()
    {  	

	int i;

	NGlobals.cPrint("CloudTest -> init()");


	i=0;
	cloudString[i++] = new String("loud");
	cloudString[i++] = new String("quiet");
	cloudString[i++] = new String("wierd");
	cloudString[i++] = new String("buzzy");
	cloudString[i++] = new String("harmonic");
	cloudString[i++] = new String("melody");
	cloudString[i++] = new String("Radiohead");
	cloudString[i++] = new String("RJD2");
	cloudString[i++] = new String("sine wave");
	cloudString[i++] = new String("square wave");
	cloudString[i++] = new String("FM synthesis");
	cloudString[i++] = new String("Max Matthews");
	cloudString[i++] = new String("Additive Synthesis");
	cloudString[i++] = new String("rave");
	cloudString[i++] = new String("techno");
	cloudString[i++] = new String("Moby");
	cloudString[i++] = new String("funky");
	cloudString[i++] = new String("groovy");
	cloudString[i++] = new String("wow");
	cloudString[i++] = new String("cool");
	cloudString[i++] = new String("dance");
	cloudString[i++] = new String("trippy");
	cloudString[i++] = new String("DEADMAU5");
	cloudString[i++] = new String("metal");
	cloudString[i++] = new String("new age");
	cloudString[i++] = new String("science");
	cloudString[i++] = new String("computers");
	cloudString[i++] = new String("NOMADS");
	cloudString[i++] = new String("crank");
	cloudString[i++] = new String("rap");
	cloudString[i++] = new String("reggae");


	cloudTestSand = new NSand(); 
	cloudTestSand.connect();

	int d[] = new int[1];
	d[0] = 0;

	nThread = new NomadsAppThread(this);
	nThread.start();

	cloudTestSand.sendGrain((byte)NAppID.BINDLE, (byte)NCommand.REGISTER, (byte)NDataType.UINT8, 1, d );
	
	String tString = new String("testCloud");
	int tLen = tString.length();

	byte[] tStringAsBytes = tString.getBytes();

	cloudTestSand.sendGrain((byte)NAppID.BINDLE, (byte)NCommand.LOGIN, (byte)NDataType.CHAR, tLen, tStringAsBytes );


    }	


    public CloudTest() {  	    
	randNum = new Random();
	init();
	start();
    }




    public void start()  {  
	NGlobals.cPrint("CloudTest -> start()");

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
	NGlobals.cPrint("CloudTest -> run()");
	int i = 0;

	while (true) {
	    String tString;
	    // if (i>500) {
	    // 	tString = new String("RESETTING CLOUD TEST STRING");
	    // 	i=0;
	    // }
	    // else {
	    // 	tString = new String("CLOUD TEST " + i++);
	    // }

	    tNumL = tNum;
	    System.out.println("tNumL = " + tNumL);
	    tNum = randNum.nextInt(15)+randNum.nextInt(16);
	    while (tNum == tNumL) {
		tNum = randNum.nextInt(15)+randNum.nextInt(16);
		System.out.println("tNum = " + tNum);
	    }
	    System.out.println("#tNum = " + tNum);
	    tString = new String(cloudString[tNum]);

	    int tLen = tString.length();
	    byte[] tBytes = tString.getBytes();

	    try {
		NGlobals.cPrint("CloudTest -> NSand.send()");
		cloudTestSand.sendGrain((byte)NAppID.CLOUD_CHAT, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.CHAR, tLen, tBytes );
		runner.sleep(500);
	    }
	    catch (InterruptedException ie) {}

	}

    }

    public void handle() {

	NGrain grain;

	NGlobals.cPrint("CloudTest -> handle()");

	grain = cloudTestSand.getGrain();
	grain.print();
	System.out.println("CloudTest handle()");

	    
    }



}
